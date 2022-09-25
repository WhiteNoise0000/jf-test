package jp.whitenoise.jftest.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import jp.whitenoise.jftest.model.魚種;

@Repository
public interface 魚種Dao extends JpaRepository<魚種, Long> {
}
