package jp.whitenoise.jftest.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.DataProvider;

import jp.whitenoise.jftest.dao.漁船Dao;
import jp.whitenoise.jftest.model.漁船;

/**
 * 漁船サービス.
 */
@Component
public class BoatListService {

    /** 漁船DAO. */
    private 漁船Dao dao;

    /**
     * コンストラクタ.
     * 
     * @param dao 漁船DAO
     */
    public BoatListService(漁船Dao dao) {
        this.dao = dao;
    }

    /**
     * 漁船一覧取得用データプロバイダ作成.
     * 
     * @return データプロバイダ
     */
    public DataProvider<漁船, Void> createDataProvider() {
        return new CallbackDataProvider<漁船, Void>(
                query -> dao.findAll(PageRequest.of(query.getPage(), query.getPageSize())).stream(),
                query -> (int) dao.count());
    }

    /**
     * 漁船保存.
     * 
     * @param item 保存対象
     * @return 保存完了後の漁船
     */
    public 漁船 save漁船(漁船 item) {
        return dao.save(item);
    }

    /**
     * 漁船削除.
     * 
     * @param id 削除対象漁船のID
     */
    public void delete漁船(String id) {
        dao.deleteById(id);
    }
}
