package jp.whitenoise.jftest.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import jp.whitenoise.jftest.model.漁船;

@Repository
public interface 漁船Dao extends JpaRepository<漁船, String> {
}
