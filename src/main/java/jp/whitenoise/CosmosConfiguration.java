package jp.whitenoise;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;

import com.azure.spring.data.cosmos.config.AbstractCosmosConfiguration;
import com.azure.spring.data.cosmos.core.mapping.EnableCosmosAuditing;
import com.azure.spring.data.cosmos.repository.config.EnableCosmosRepositories;

import jp.whitenoise.common.auth.AuthService;

/**
 * CosmosDB設定.
 */
@Configuration
@EnableCosmosRepositories
@EnableCosmosAuditing
public class CosmosConfiguration extends AbstractCosmosConfiguration {

    /** 認証サービス. */
    private AuthService authService;

    /**
     * コンストラクタ.
     * 
     * @param authService 認証サービス
     */
    public CosmosConfiguration(AuthService authService) {
        this.authService = authService;
    }

    /**
     * 接続先データベース名取得.
     * 
     * @return 接続先データベース名
     */
    @Override
    protected String getDatabaseName() {
        return "JFDB";
    }

    /**
     * DB登録更新ユーザ名取得.
     * 
     * @return ログイン中ユーザ名
     */
    @Bean
    AuditorAware<String> auditorAware() {
        return () -> authService.getAuthUsername();
    }
}
