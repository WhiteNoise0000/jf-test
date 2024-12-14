package jp.whitenoise;

import java.util.Optional;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.vaadin.flow.spring.security.VaadinWebSecurity;

import jp.whitenoise.common.auth.User;
import jp.whitenoise.common.auth.ui.LoginPage;

@EnableWebSecurity
@Configuration
public class SecurityConfig extends VaadinWebSecurity {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);
        setLoginView(http, LoginPage.class);
    }

    /**
     * 静的リソースはSpring Serucirtyの制御対象外.
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().requestMatchers(HttpMethod.GET, "/images/**");
        super.configure(web);
    }

    /**
     * パスワードエンコーダ.
     * 
     * @return BCryptエンコーダ
     */
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * DB登録更新ユーザ名取得.
     * 
     * @return ログイン中ユーザ名
     */
    @Bean
    AuditorAware<String> auditorAware() {
        return () -> {
            // AuthServiceを利用するとDIループとなるため、同一ロジックで取得
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated()) {
                return Optional.of("anonymousUser");
            }
            if (auth.getPrincipal() instanceof String) {
                return Optional.of((String) auth.getPrincipal());
            }
            return Optional.of(((User) auth.getPrincipal()).getUsername());
        };
    }
}