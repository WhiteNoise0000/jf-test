package jp.whitenoise.jftest;

import org.springframework.context.annotation.Configuration;

import com.azure.spring.data.cosmos.config.AbstractCosmosConfiguration;
import com.azure.spring.data.cosmos.core.mapping.EnableCosmosAuditing;
import com.azure.spring.data.cosmos.repository.config.EnableCosmosRepositories;

@Configuration
@EnableCosmosRepositories
@EnableCosmosAuditing
public class CosmosConfiguration extends AbstractCosmosConfiguration {

    @Override
    protected String getDatabaseName() {
        return "TEST";
    }

}
