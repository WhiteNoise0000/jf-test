package jp.whitenoise.jfapp.dao;

import org.springframework.stereotype.Repository;

import com.azure.spring.data.cosmos.repository.CosmosRepository;

import jp.whitenoise.jfapp.model.メール配信;

/**
 * メール配信DAO.
 */
@Repository
public interface メール配信Dao extends CosmosRepository<メール配信, String> {

    メール配信 findByアドレス(String address);
}
