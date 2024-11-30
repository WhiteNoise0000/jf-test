package jp.whitenoise.jftest.service;

import java.util.Collection;
import java.util.Set;

import org.springframework.stereotype.Service;

import jp.whitenoise.jftest.dao.マスタDao;
import jp.whitenoise.jftest.model.マスタ;

/**
 * システム設定サービス.
 */
@Service
public class SettingsService {

    private final マスタDao dao;

    public SettingsService(マスタDao dao) {
        this.dao = dao;
    }

    /**
     * サイト名取得.
     * 
     * @return サイト名
     */
    public String selectサイト名() {
        return dao.findById(マスタ.ID_サイト名).orElse(new マスタ(マスタ.ID_サイト名)).getValue();
    }

    /**
     * サイト名保存.
     * 
     * @param siteName 魚種リスト
     */
    public void saveサイト名(String siteName) {
        マスタ entity = dao.findById(マスタ.ID_サイト名).orElse(new マスタ(マスタ.ID_サイト名));
        entity.setValue(siteName);
        dao.save(entity);
    }

    /**
     * 魚種リスト取得.
     * 
     * @return 魚種リスト
     */
    public Set<String> select魚種() {
        return dao.findValuesById(マスタ.ID_魚種);
    }

    /**
     * 魚種リスト保存.
     * 
     * @param list 魚種リスト
     */
    public void save魚種(Collection<String> list) {
        マスタ entity = dao.findById(マスタ.ID_魚種).orElse(new マスタ(マスタ.ID_サイト名));
        entity.getValues().clear();
        entity.getValues().addAll(list);
        dao.save(entity);
    }
}
