package com.ditp.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.ditp.entity.Dict;
import com.ruizton.main.dao.comm.HibernateDaoSupport;
@Repository
public class DictDAO extends HibernateDaoSupport {
	private static final Logger log = LoggerFactory.getLogger(DictDAO.class);
	/**
	 * 返回dict
	 * @param code
	 * @return
	 */
	public Dict get(String code)
	{
		try {
			String queryString = "from Dict as model where model.fcode=?";
			Query queryObject = getSession().createQuery(queryString);
			queryObject.setParameter(0, code);
			return (Dict)queryObject.list().get(0);
			
		} catch (RuntimeException re) {
			log.error("find by property code failed", re);
			throw re;
		}	
	}
	public List<Dict> get(){
		List<Dict> list = null;
		try {
			String queryString = "from Dict";
			List<Dict> list1 = (List<Dict>)getSession().createQuery(queryString);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return list;
	}
	public List<Dict> getByPid(String pid) {
		String quertString = "select * from tb_sys_dict where fparentId='"+pid+"'";
		//List<Dict> list = getSession().createSQLQuery(quertString).addEntity(Dict.class).list();
		SQLQuery query = getSession().createSQLQuery(quertString);
		List<Dict> list = query.addEntity(Dict.class).list();
		return list;
	}
}
