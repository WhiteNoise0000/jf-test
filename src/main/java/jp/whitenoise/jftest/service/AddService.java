package jp.whitenoise.jftest.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.stream.Streams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jp.whitenoise.jftest.dao.入港予定Dao;
import jp.whitenoise.jftest.dao.漁船Dao;
import jp.whitenoise.jftest.dao.魚種Dao;
import jp.whitenoise.jftest.model.入港予定;
import jp.whitenoise.jftest.model.漁船;
import jp.whitenoise.jftest.model.魚種;

@Service
//@Transactional
public class AddService {

    @Autowired
    private 漁船Dao 漁船Dao;
    @Autowired
    private 魚種Dao 魚種Dao;
    @Autowired
    private 入港予定Dao 入港予定Dao;

    public List<漁船> select漁船() {
        // TODO debug
        if (漁船Dao.count() == 0) {
            漁船Dao.save(new 漁船("○○丸"));
            漁船Dao.save(new 漁船("△△丸"));
            漁船Dao.save(new 漁船("■■号"));
        }

        return Streams.of(漁船Dao.findAll()).toList();
    }

    public List<魚種> select魚種() {
        // TODO debug
        if (魚種Dao.count() == 0) {
            魚種Dao.save(new 魚種("マグロ"));
            魚種Dao.save(new 魚種("サバ"));
            魚種Dao.save(new 魚種("アジ"));
        }

        return Streams.of(魚種Dao.findAll()).toList();
    }

    public 入港予定 save入港予定(入港予定 entity) {
        return 入港予定Dao.save(entity);
    }

    public Optional<入港予定> select入港予定(String id) {
        return 入港予定Dao.findById(id);
    }
}
