package jp.whitenoise.jftest.dao;

import org.springframework.stereotype.Repository;

import com.azure.spring.data.cosmos.repository.CosmosRepository;

import jp.whitenoise.jftest.model.魚種;

@Repository
public interface 魚種Dao extends CosmosRepository<魚種, String> {
}
