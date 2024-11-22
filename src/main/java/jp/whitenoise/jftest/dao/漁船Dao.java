package jp.whitenoise.jftest.dao;

import org.springframework.stereotype.Repository;

import com.azure.spring.data.cosmos.repository.CosmosRepository;

import jp.whitenoise.jftest.model.漁船;

@Repository
public interface 漁船Dao extends CosmosRepository<漁船, String> {
}
