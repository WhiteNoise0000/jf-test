package jp.whitenoise.jftest.service;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jp.whitenoise.jftest.dao.入港予定Dao;
import jp.whitenoise.jftest.dao.入港予定明細Dao;
import jp.whitenoise.jftest.dao.漁船Dao;
import jp.whitenoise.jftest.dao.魚種Dao;
import jp.whitenoise.jftest.model.入港予定;
import jp.whitenoise.jftest.model.漁船;
import jp.whitenoise.jftest.model.魚種;

@Service
@Transactional
public class AddService {
    
    @Autowired
    private 漁船Dao 漁船Dao;
    @Autowired
    private 魚種Dao 魚種Dao;
    @Autowired
    private 入港予定Dao 入港予定Dao;
    @Autowired
    private 入港予定明細Dao 入港予定明細Dao;

    public List<漁船> select漁船() {
        return 漁船Dao.findAll();
    }

    public List<String> select魚種() {
        return 魚種Dao.findAll().stream().map(魚種::get名称).toList();
    }

    public 入港予定 save入港予定(入港予定 entity) {
        入港予定 ret = 入港予定Dao.saveAndFlush(entity);
        entity.get明細().forEach(item -> item.set予定ID(ret.get予定ID()));
        入港予定明細Dao.saveAllAndFlush(ret.get明細());
        return ret;
    }

    public Optional<入港予定> select入港予定(Integer id) {
        return 入港予定Dao.findById(id);
    }
}
