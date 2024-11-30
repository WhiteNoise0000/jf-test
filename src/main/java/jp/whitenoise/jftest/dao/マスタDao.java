package jp.whitenoise.jftest.dao;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.azure.spring.data.cosmos.repository.CosmosRepository;

import jp.whitenoise.jftest.model.マスタ;

@Repository
public interface マスタDao extends CosmosRepository<マスタ, String> {

    /**
     * 設定値取得.
     * 
     * @param id マスタID
     * @return 設定値リスト
     */
    default Set<String> findValuesById(String id) {
        Optional<マスタ> ret = findById(id);
        return ret.isEmpty() ? Collections.emptySet() : ret.get().getValues();
    }
}
