package jp.whitenoise.common.security;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import com.vaadin.flow.spring.security.VaadinWebSecurity;

import jp.whitenoise.jftest.ui.LoginPage;

@EnableWebSecurity
@Configuration
public class SecurityConfig extends VaadinWebSecurity {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		super.configure(http);
		setLoginView(http, LoginPage.class);
	}

	/**
	 * Allows access to static resources, bypassing Spring security.
	 */
	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().requestMatchers(HttpMethod.GET, "/images/**");
		super.configure(web);
	}

	/**
	 * Demo UserDetailService, which only provides two hardcoded
	 * in-memory users and their roles.
	 * NOTE: This should not be used in real-world applications.
	 */
	@Bean
	UserDetailsService userDetailsService() {
		List<UserDetails> users = new ArrayList<>();
		users.add(User.withUsername("user")
				.password("{noop}userpass")
				.roles("USER").build());
		users.add(User.withUsername("admin")
				.password("{noop}adminpass")
				.roles("ADMIN").build());
		return new InMemoryUserDetailsManager(users);
	}
}