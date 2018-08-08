package com.ditp.dao;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.stereotype.Repository;

import com.ditp.entity.StationMail;
import com.ditp.entity.StationMailRead;
import com.ruizton.main.dao.comm.HibernateDaoSupport;

@Repository
public class StationMailDao extends HibernateDaoSupport{
	private static final Logger log = LoggerFactory.getLogger(StationMailDao.class);
	/**
	 * 分页查询站内信
	 * @param page
	 * @param pagesize
	 * @param filter
	 * @return
	 */
	public List<StationMailRead> get(Integer page,int pagesize,String filter)
	{
		try {
			String queryString = "select ifnull(tu.fNickName,'系统') as fsendUserName,tbs.* from tb_website_stationmail as tbs"
					+ " left join fuser as tu on tbs.fsendUserid=tu.fId"
					+ " "+filter;
			SQLQuery queryObject = getSession().createSQLQuery(queryString);
			queryObject.setFirstResult(page*pagesize);
			queryObject.setMaxResults(pagesize);
			return queryObject.addEntity(StationMailRead.class).list();
			
		} catch (RuntimeException re) {
			log.error("find by property "+filter+" failed", re);
			throw re;
		}	
	}
	public List<StationMail> get(String queryString)
	{
		try {
			SQLQuery queryObject = getSession().createSQLQuery(queryString);
			return queryObject.addEntity(StationMail.class).list();
			
		} catch (RuntimeException re) {
			log.error(queryString+" failed", re);
			throw re;
		}	
	}
	public List<StationMailRead> getBySql(Integer page,int pagesize,String queryString)
	{
		try {
			SQLQuery queryObject = getSession().createSQLQuery(queryString);
			queryObject.setFirstResult(page*pagesize);
			queryObject.setMaxResults(pagesize);
			return queryObject.addEntity(StationMailRead.class).list();
			
		} catch (RuntimeException re) {
			log.error(queryString+" failed", re);
			throw re;
		}	
	}
	
	/**
	 * 分页查询站内信 tb_website_stationmail别名 tbs
	 * @param page
	 * @param pagesize
	 * @param filter
	 * @return
	 */
	public String  getCount(String filter)
	{
		try {
			String queryString = "select count(*) from tb_website_stationmail as tbs "+filter;
			SQLQuery queryObject = getSession().createSQLQuery(queryString);
			return queryObject.uniqueResult().toString();
		} catch (RuntimeException re) {
			log.error("find by property "+filter+" failed", re);
			throw re;
		}	
	}
	/**
	 * 保存 站内信
	 * @param stationMailRead
	 */
	public void save(StationMail stationMail) {
		try {
			getSession().saveOrUpdate(stationMail);
		} catch (Exception e) {

		}
	}
	/**
	 * 删除
	 * @param filter
	 * @return
	 */
	public String del(String filter)
	{
		try {
			String queryString="update tb_website_stationmail  "+filter;
			//String queryString="delete from tb_website_stationmail "+filter;
			SQLQuery queryObject = getSession().createSQLQuery(queryString);
			queryObject.executeUpdate();
			return "1";
		} catch (RuntimeException re) {
			// TODO Auto-generated catch block
			log.error("find by property "+filter+" failed", re);
			throw re;
		}
		
	}
}
