package jp.whitenoise.jftest.dao;

import java.util.List;
import java.util.stream.Stream;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.azure.spring.data.cosmos.repository.CosmosRepository;
import com.azure.spring.data.cosmos.repository.Query;

import jp.whitenoise.jftest.model.入港予定;
import jp.whitenoise.jftest.model.入港予定明細;

@Repository
public interface 入港予定Dao extends CosmosRepository<入港予定, String> {

    @Query("""
            SELECT
            	SUM(d.数量) as 数量,
            	d.出荷予定日 as 出荷予定日,
            	d.魚種 as 魚種
            FROM 入港予定 c
            JOIN d IN c.明細
            GROUP BY d.出荷予定日, d.魚種
            """)
    public List<入港予定明細> selectSummary();

    @Query("SELECT * FROM 入港予定 c ORDER BY c.入港予定日, c.入港漁船.漁船名")
    public Stream<入港予定> list入港予定(Pageable pageable);
}
