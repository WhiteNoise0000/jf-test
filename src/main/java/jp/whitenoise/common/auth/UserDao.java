package jp.whitenoise.common.auth;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.azure.spring.data.cosmos.repository.CosmosRepository;

/**
 * ユーザ情報DAO.
 */
@Repository
public interface UserDao extends CosmosRepository<User, String> {

    /**
     * ユーザ情報取得.
     * 
     * @param username ユーザ名
     * @return ユーザ情報
     */
    Optional<User> findByUsername(String username);
}
