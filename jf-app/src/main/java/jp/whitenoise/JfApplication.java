package jp.whitenoise;

import java.util.UUID;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.vaadin.flow.server.VaadinServiceInitListener;
import com.vaadin.flow.server.VaadinSession;

@SpringBootApplication
public class JfApplication {

    public static final String APP_NAME = "漁獲予定量集計";

    public static void main(String[] args) {
        SpringApplication.run(JfApplication.class, args);
    }

    @Bean
    VaadinServiceInitListener vaadinServiceInitListener() {
        return event -> {
            event.addIndexHtmlRequestListener(response -> {
                // htmlタグのlang属性を"ja"に固定
                response.getDocument().head().parent().attr("lang", "ja");

                // Prodction環境の場合、Strict CSP有効
                // ※有効化のためにHillaを除外（24.5.8時点）
                // @see https://vaadin.com/forum/t/vaadin-flow-content-security-policy-csp/167899
                if (response.getVaadinRequest().getService().getDeploymentConfiguration().isProductionMode()) {
                    String nonce = UUID.randomUUID().toString();
                    VaadinSession.getCurrent().setAttribute("csp-nonce", nonce);
                    response.getVaadinResponse().setHeader("Content-Security-Policy",
                            "script-src 'self' 'unsafe-eval' 'nonce-" + nonce + "';" +
                                    "script-src 'self' ajax.cloudflare.com;" +
                                    "script-src static.cloudflareinsights.com;" +
                                    "connect-src cloudflareinsights.com;");
                    response.getDocument().getElementsByTag("script").attr("nonce", nonce);
                }
            });
        };
    }
}
