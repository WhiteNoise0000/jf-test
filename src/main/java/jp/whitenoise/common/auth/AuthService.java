package jp.whitenoise.common.auth;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.data.provider.AbstractBackEndDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.server.VaadinServletRequest;

import jp.whitenoise.jftest.ui.TopPage;

/**
 * 認証・認可サービス.
 */
@Service
public class AuthService {

    /** ユーザ情報DAO. */
    private final UserDao userDao;
    /** パスワードエンコーダ. */
    private final PasswordEncoder encoder;

    /**
     * コンストラクタ.
     * 
     * @param userDao ユーザ情報DAO
     */
    public AuthService(UserDao userDao, PasswordEncoder encoder) {
        this.userDao = userDao;
        this.encoder = encoder;
    }

    /**
     * ユーザ情報取得用プロバイダ作成.
     * 
     * @return データプロバイダ
     */
    public AbstractBackEndDataProvider<User, Void> createDataProvider() {
        return new AbstractBackEndDataProvider<>() {
            @Override
            protected Stream<User> fetchFromBackEnd(Query<User, Void> query) {
                Pageable pageable = PageRequest.of(query.getPage(), query.getPageSize());
                return userDao.findAll(pageable).stream();
            }

            @Override
            protected int sizeInBackEnd(Query<User, Void> query) {
                return (int) userDao.count();
            }
        };
    }

    /**
     * ログイン済みユーザ取得.
     * 
     * @return ユーザ情報（未ログインの場合はnull）
     */
    public Optional<User> getAuthenticatedUser() {
        SecurityContext context = SecurityContextHolder.getContext();
        Object principal = context.getAuthentication().getPrincipal();
        // 未ログインの場合はnull
        if (principal == null || !(principal instanceof User)) {
            return Optional.empty();
        }
        return Optional.of((User) principal);
    }

    /**
     * ログイン済みユーザ名取得.
     * 
     * @return ユーザ名（未ログインの場合はnull）
     */
    public Optional<String> getAuthUsername() {
        Optional<User> user = getAuthenticatedUser();
        if (user.isPresent()) {
            return Optional.ofNullable(user.get().getUsername());
        }
        return Optional.empty();
    }

    /**
     * 管理者権限判定.
     * 
     * @return 管理者ログイン済みの場合、true
     */
    public boolean isAdmin() {
        Optional<User> user = getAuthenticatedUser();
        return user.isPresent() && user.get().getRoles().contains(EUserRole.ADMIN.name());
    }

    /**
     * ユーザ権限判定.
     * 
     * @return ユーザログイン済みの場合、true
     */
    public boolean isUser() {
        Optional<User> user = getAuthenticatedUser();
        return user.isPresent() && user.get().getRoles().contains(EUserRole.USER.name());
    }

    /**
     * TOP画面へ遷移してログオフ.
     */
    public void logout() {
        UI.getCurrent().navigate(TopPage.class);
        SecurityContextLogoutHandler handler = new SecurityContextLogoutHandler();
        handler.logout(VaadinServletRequest.getCurrent().getHttpServletRequest(), null, null);
    }

    /**
     * ユーザ情報取得.
     * 
     * @param username ユーザ名
     * @return ユーザ情報
     */
    public Optional<User> findByUsername(String username) {
        return userDao.findById(username);
    }

    /**
     * ユーザ情報登録・更新.
     * 
     * @param targetUser 対象ユーザ(新規ユーザの場合null)
     * @param username ユーザ名
     * @param password パスワード(更新時のみ指定)
     * @param emailAddr メールアドレス(任意)
     * @param roles ユーザ権限
     * @param isEnabled 有効フラグ
     * @throws OptimisticLockingFailureException 楽観排他エラー
     */
    public void save(Optional<User> targetUser, String username,
            Optional<String> password, Optional<String> emailAddr, Set<EUserRole> roles, boolean isEnabled)
            throws OptimisticLockingFailureException {

        User entity = targetUser.orElse(new User());
        // ユーザ名設定(編集時のみ念のため)
        if (targetUser.isEmpty()) {
            entity.setUsername(username);
        }
        password.ifPresent(str -> entity.setPassword(encoder.encode(str)));
        entity.setEmailAddr(emailAddr);
        entity.getRoles().clear();
        entity.getRoles().addAll(roles.stream().map(EUserRole::name).toList());
        entity.setEnabled(isEnabled);
        // ユーザ情報保存
        userDao.save(entity);
    }
}