package jp.whitenoise.jfapp.dao;

import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.azure.spring.data.cosmos.repository.CosmosRepository;

import jp.whitenoise.jfapp.model.漁船;

/**
 * 漁船Dao.
 */
@Repository
public interface 漁船Dao extends CosmosRepository<漁船, String> {

    /**
     * 漁船名でフィルタ取得.
     * 
     * @param filter 漁船名(中間一致)
     * @param pageable ページネーション
     * @return 漁船一覧
     */
    Stream<漁船> findBy漁船名Containing(Optional<String> filter, Pageable pageable);

    /**
     * 漁船名でフィルタ後カウント.
     * 
     * @param filter 漁船名(中間一致)
     * @return カウント結果
     */
    int countBy漁船名Containing(Optional<String> filter);
}
