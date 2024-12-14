package jp.whitenoise.jfapp.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.stream.Streams;
import org.springframework.stereotype.Service;

import jp.whitenoise.common.auth.UserDao;
import jp.whitenoise.jfapp.dao.マスタDao;
import jp.whitenoise.jfapp.dao.入港予定Dao;
import jp.whitenoise.jfapp.dao.漁船Dao;
import jp.whitenoise.jfapp.model.マスタ;
import jp.whitenoise.jfapp.model.入港予定;
import jp.whitenoise.jfapp.model.漁船;

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
        return マスタDao.findValuesById(マスタ.ID_魚種);
    }

    public 入港予定 save入港予定(入港予定 entity) {
        return 入港予定Dao.save(entity);
    }

    public Optional<入港予定> select入港予定(String id) {
        return 入港予定Dao.findById(id);
    }
}
