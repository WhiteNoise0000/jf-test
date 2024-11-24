package jp.whitenoise.common.auth;

import org.springframework.stereotype.Repository;

import com.azure.spring.data.cosmos.repository.CosmosRepository;

/**
 * ユーザ情報DAO.
 */
@Repository
public interface UserDao extends CosmosRepository<User, String> {
}
