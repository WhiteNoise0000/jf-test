package jp.whitenoise.jftest.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.stream.Streams;
import org.springframework.stereotype.Service;

import jp.whitenoise.common.auth.UserDao;
import jp.whitenoise.jftest.dao.マスタDao;
import jp.whitenoise.jftest.dao.入港予定Dao;
import jp.whitenoise.jftest.dao.漁船Dao;
import jp.whitenoise.jftest.model.マスタ;
import jp.whitenoise.jftest.model.入港予定;
import jp.whitenoise.jftest.model.漁船;

@Service
public class EditService {

    private マスタDao マスタDao;
    private 漁船Dao 漁船Dao;
    private 入港予定Dao 入港予定Dao;

    public EditService(UserDao userDao, マスタDao マスタDao, 漁船Dao 漁船Dao, 入港予定Dao 入港予定Dao) {
        this.マスタDao = マスタDao;
        this.漁船Dao = 漁船Dao;
        this.入港予定Dao = 入港予定Dao;
    }

    public List<漁船> select漁船() {
        // TODO debug
        if (漁船Dao.count() == 0) {
            漁船Dao.save(new 漁船("○○丸"));
            漁船Dao.save(new 漁船("△△丸"));
            漁船Dao.save(new 漁船("■■号"));
        }

        return Streams.of(漁船Dao.findAll()).toList();
    }

    /**
     * 魚種リスト取得.
     * 
     * @return 魚種リスト
     */
    public List<String> select魚種() {
        // TODO debug
        if (!マスタDao.existsById(マスタ.ID_魚種)) {
            マスタ 魚種 = new マスタ();
            魚種.setId(マスタ.ID_魚種);
            魚種.getValues().add("マグロ");
            魚種.getValues().add("サバ");
            魚種.getValues().add("アジ");
            マスタDao.save(魚種);
        }

        return マスタDao.findValuesById(マスタ.ID_魚種);
    }

    public 入港予定 save入港予定(入港予定 entity) {
        return 入港予定Dao.save(entity);
    }

    public Optional<入港予定> select入港予定(String id) {
        return 入港予定Dao.findById(id);
    }
}