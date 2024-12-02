package jp.whitenoise.jftest.dao;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
    default List<String> findValuesById(String id) {
        Optional<マスタ> ret = findById(id);
        return ret.isEmpty() ? Collections.emptyList() : ret.get().getValues();
    }
}
