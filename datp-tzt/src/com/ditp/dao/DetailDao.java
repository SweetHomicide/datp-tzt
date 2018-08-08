package com.ditp.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.springframework.stereotype.Repository;

import com.ditp.entity.Detail;
import com.ruizton.main.dao.comm.HibernateDaoSupport;

@Repository
public class DetailDao extends HibernateDaoSupport{

	public List<Detail> getByfinaId(String fid) {
		List<Detail> list = null;
		try {
			String queryString = "select * from tb_fina_detail where ffinaId ='"+fid+"'";
			SQLQuery query = getSession().createSQLQuery(queryString);
			 list = query.addEntity(Detail.class).list();
		} catch (Exception e) {
			// TODO: handle exception
		}
		return list;
	}

	public void save(Detail detail) {
		try {
			getSession().saveOrUpdate(detail);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

}
