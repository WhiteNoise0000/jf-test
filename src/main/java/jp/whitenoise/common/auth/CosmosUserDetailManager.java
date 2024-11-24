package jp.whitenoise.common.auth;

import java.util.Optional;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Component;

/**
 * CosmosDB向けユーザ情報マネージャ.
 */
@Component
public class CosmosUserDetailManager implements UserDetailsManager {

    /** セキュリティ情報サービス. */
    private final AuthService service;
    /** ユーザ情報DAO. */
    private final UserDao userDao;
    /** パスワードエンコーダ. */
    private final PasswordEncoder encoder;

    /**
     * コンストラクタ.
     * 
     * @param userDao ユーザ情報DAO
     */
    public CosmosUserDetailManager(AuthService service, UserDao userDao, PasswordEncoder encoder) {
        this.service = service;
        this.userDao = userDao;
        this.encoder = encoder;

        // ユーザ未登録の場合、初期管理ユーザ作成
        if (userDao.count() == 0) {
            userDao.save(new User("admin", encoder.encode("adminpass"), EUserRole.ADMIN));
            userDao.save(new User("user", encoder.encode("userpass"), EUserRole.USER));
        }
    }

    /**
     * ユーザ追加.
     */
    @Override
    public void createUser(UserDetails userDetails) {

        // 既に存在するユーザは追加不可
        if (userExists(userDetails.getUsername())) {
            throw new IllegalArgumentException("User already exists: " + userDetails.getUsername());
        }
        User user = new User();
        user.setUsername(userDetails.getUsername());
        user.setPassword(encoder.encode(userDetails.getPassword()));
        user.getRoles().addAll(userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList());
        userDao.save(user);
    }

    /**
     * ユーザ更新.
     */
    @Override
    public void updateUser(UserDetails userDetails) {
        User user = userDao.findById(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userDetails.getUsername()));
        user.setPassword(userDetails.getPassword());
        user.getRoles().addAll(userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList());
        user.setEnabled(userDetails.isEnabled());
        user.setAccountNonLocked(userDetails.isAccountNonLocked());
        userDao.save(user);
    }

    /**
     * ユーザ削除.
     */
    @Override
    public void deleteUser(String username) {
        Optional<User> user = userDao.findById(username);
        user.ifPresent(userDao::delete);
    }

    /**
     * 認証済みユーザのパスワード変更.
     */
    @Override
    public void changePassword(String oldPassword, String newPassword) {

        String currentUsername = service.getAuthenticatedUser().map(UserDetails::getUsername)
                .orElseThrow(() -> new IllegalStateException("Not authenticated."));
        User user = userDao.findById(currentUsername)
                .orElseThrow(() -> new IllegalStateException("User not found: " + currentUsername));

        // パスワード検証
        if (!user.getPassword().equals(encoder.encode(oldPassword))) {
            throw new IllegalArgumentException("Old password does not match");
        }
        user.setPassword(encoder.encode(newPassword));
        userDao.save(user);
    }

    /**
     * ユーザ有無チェック.
     */
    @Override
    public boolean userExists(String username) {
        return userDao.findById(username).isPresent();
    }

    /**
     * ユーザ情報取得.
     */
    @Override
    public UserDetails loadUserByUsername(String username) {

        User user = userDao.findById(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword()) // BCrypt ハッシュ済みのパスワード
                .roles(user.getRoles().toArray(new String[0]))
                .accountLocked(!user.isAccountNonLocked())
                .disabled(!user.isEnabled())
                .build();
    }
}
