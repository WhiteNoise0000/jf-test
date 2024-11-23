package jp.whitenoise.jftest.service;

import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.vaadin.flow.data.provider.AbstractBackEndDataProvider;
import com.vaadin.flow.data.provider.Query;

import jp.whitenoise.jftest.dao.入港予定Dao;
import jp.whitenoise.jftest.model.入港予定;

@Service
//@Transactional
public class ListService {

	@Autowired
	private 入港予定Dao 入港予定Dao;

	/**
	 * 入港予定取得用データプロバイダ返却.
	 * 
	 * @return データプロバイダ
	 */
	public AbstractBackEndDataProvider<入港予定, Void> createDataProvider() {
		return new AbstractBackEndDataProvider<>() {

			@Override
			protected Stream<入港予定> fetchFromBackEnd(Query<入港予定, Void> query) {
				Pageable pageable = PageRequest.of(query.getPage(), query.getPageSize());
				return 入港予定Dao.list入港予定(pageable);
			}

			@Override
			protected int sizeInBackEnd(Query<入港予定, Void> query) {
				return (int) 入港予定Dao.count();
			}
		};
	}

	public void delete入港予定(String id) {
		入港予定Dao.deleteById(id);
	}
}
