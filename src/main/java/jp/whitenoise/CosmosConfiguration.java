package jp.whitenoise;

import org.springframework.context.annotation.Configuration;

import com.azure.spring.data.cosmos.config.AbstractCosmosConfiguration;
import com.azure.spring.data.cosmos.core.mapping.EnableCosmosAuditing;
import com.azure.spring.data.cosmos.repository.config.EnableCosmosRepositories;

/**
 * CosmosDB設定.
 */
@Configuration
@EnableCosmosRepositories
@EnableCosmosAuditing
public class CosmosConfiguration extends AbstractCosmosConfiguration {

    /**
     * 接続先データベース名取得.
     * 
     * @return 接続先データベース名
     */
    @Override
    protected String getDatabaseName() {
        return "JFDB";
    }
}
