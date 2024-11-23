package jp.whitenoise.common.security;

import java.util.Optional;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Component;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinServletRequest;

import jp.whitenoise.jftest.ui.TopPage;

/**
 * セキュリティサービス.
 */
@Component
public class SecurityService {

	/** 管理者権限. */
	public static final GrantedAuthority ADMIN = new SimpleGrantedAuthority("ROLE_ADMIN");
	/** ユーザ権限. */
	public static final GrantedAuthority USER = new SimpleGrantedAuthority("ROLE_USER");

	/**
	 * ログイン済みユーザ取得.
	 * 
	 * @return ユーザ情報（未ログインの場合はnull）
	 */
	public Optional<UserDetails> getAuthenticatedUser() {
		SecurityContext context = SecurityContextHolder.getContext();
		Object principal = context.getAuthentication().getPrincipal();
		// 未ログインの場合はnull
		return Optional.ofNullable((UserDetails) principal);
	}

	/**
	 * 管理者権限判定.
	 * 
	 * @return 管理者ログイン済みの場合、true
	 */
	public boolean isAdmin() {
		Optional<UserDetails> user = getAuthenticatedUser();
		return user.isPresent() && user.get().getAuthorities().contains(ADMIN);
	}

	/**
	 * ユーザ権限判定.
	 * 
	 * @return ユーザログイン済みの場合、true
	 */
	public boolean isUser() {
		Optional<UserDetails> user = getAuthenticatedUser();
		return user.isPresent() && user.get().getAuthorities().contains(USER);
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