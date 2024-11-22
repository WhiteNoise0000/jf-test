package jp.whitenoise.jftest.service;

import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jp.whitenoise.jftest.dao.入港予定Dao;
import jp.whitenoise.jftest.model.入港予定明細;

@Service
//@Transactional
public class TopService {
    
    @Autowired
    private 入港予定Dao 入港予定Dao;

    /**
     * 出荷予定集計.
     * 
     * @return 集計済み出荷予定
     */
    public List<入港予定明細> summary出荷予定() {
    	List<入港予定明細> list = 入港予定Dao.selectSummary();
    	// 出荷予定日、魚種名昇順でソート
    	list.sort(Comparator.comparing(入港予定明細::get出荷予定日).thenComparing(入港予定明細::get魚種));
        return list; 
    }
}
