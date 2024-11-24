package jp.whitenoise.common.auth;

import java.util.Optional;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Component;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinServletRequest;

import jp.whitenoise.jftest.ui.TopPage;

/**
 * 認証・認可サービス.
 */
@Component
public class AuthService {

    /**
     * ログイン済みユーザ取得.
     * 
     * @return ユーザ情報（未ログインの場合はnull）
     */
    public Optional<UserDetails> getAuthenticatedUser() {
        SecurityContext context = SecurityContextHolder.getContext();
        Object principal = context.getAuthentication().getPrincipal();
        // 未ログインの場合はnull
        if (principal == null || !(principal instanceof UserDetails)) {
            return Optional.empty();
        }
        return Optional.of((UserDetails) principal);
    }

    /**
     * 管理者権限判定.
     * 
     * @return 管理者ログイン済みの場合、true
     */
    public boolean isAdmin() {
        Optional<UserDetails> user = getAuthenticatedUser();
        return user.isPresent() && user.get().getAuthorities().contains(EUserRole.ADMIN.getAuthObj());
    }

    /**
     * ユーザ権限判定.
     * 
     * @return ユーザログイン済みの場合、true
     */
    public boolean isUser() {
        Optional<UserDetails> user = getAuthenticatedUser();
        return user.isPresent() && user.get().getAuthorities().contains(EUserRole.USER.getAuthObj());
    }

    /**
     * TOP画面へ遷移してログオフ.
     */
    public void logout() {
        UI.getCurrent().navigate(TopPage.class);
        SecurityContextLogoutHandler handler = new SecurityContextLogoutHandler();
        handler.logout(VaadinServletRequest.getCurrent().getHttpServletRequest(), null, null);
    }
}