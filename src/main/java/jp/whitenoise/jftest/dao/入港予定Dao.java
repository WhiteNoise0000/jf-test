package jp.whitenoise.jftest.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import jp.whitenoise.jftest.model.入港予定;

@Repository
public interface 入港予定Dao extends JpaRepository<入港予定, Integer> {
}
