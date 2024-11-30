package jp.whitenoise;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.vaadin.flow.server.VaadinServiceInitListener;

@SpringBootApplication
public class JfTestApplication {

    public static final String APP_NAME = "漁獲予定量集計";

    public static void main(String[] args) {
        SpringApplication.run(JfTestApplication.class, args);
    }

    @Bean
    VaadinServiceInitListener vaadinServiceInitListener() {
        return event -> {
            event.addIndexHtmlRequestListener(response -> {
                // htmlタグのlang属性を"ja"に固定
                response.getDocument().head().parent().attr("lang", "ja");

                // Prodction環境の場合、Strict CSP有効
                // TODO 有効化しても想定通り動かず様子見
                // https://vaadin.com/forum/t/vaadin-flow-content-security-policy-csp/167899
                //                if (response.getVaadinRequest().getService().getDeploymentConfiguration().isProductionMode()) {
                //                    String nonce = UUID.randomUUID().toString();
                //                    VaadinSession.getCurrent().setAttribute("csp-nonce", nonce);
                //                    response.getVaadinResponse().setHeader("Content-Security-Policy",
                //                            "script-src 'self' 'unsafe-eval' 'nonce-" + nonce + "';");
                //                    response.getDocument().getElementsByTag("script").attr("nonce", nonce);
                //                }
            });
        };
    }
}
