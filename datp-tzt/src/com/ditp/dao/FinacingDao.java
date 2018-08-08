package com.ditp.dao;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.stereotype.Repository;
import com.ditp.entity.Finacing;
import com.ditp.entity.FinacingRead;
import com.ruizton.main.dao.comm.HibernateDaoSupport;

@Repository
public class FinacingDao extends HibernateDaoSupport{
	private static int pagesize = 10;
	@SuppressWarnings("unchecked")
	public List<FinacingRead> get(Integer page) {
		String queryFilter = "select  c.fSymbol as fsymbol,c.fname as fvitypeName,b.fvalue as fdtype,'' as tenThsPro,"
				+ "a.* from tb_fina_finacing as a left join tb_sys_dict as b on a.ftype = b.fcode LEFT JOIN fvirtualcointype as c ON a.fvitypeId=c.fid";
		SQLQuery query = getSession().createSQLQuery(queryFilter);
		query.setFirstResult((page-1)*pagesize);
		query.setMaxResults(pagesize);
		List<FinacingRead> list= query.addEntity(FinacingRead.class).list();
		//List<Finacing> list = query.addEntity(Finacing.class).list();
		return list;
	}
	
	public List<FinacingRead> get(Integer page,String filter) {
		String queryFilter = "select  c.fSymbol as fsymbol,c.fname as fvitypeName,b.fvalue as fdtype,ROUND((10000*a.fproportion/100),4)  as tenThsPro"
				+ ",a.* from tb_fina_finacing as a left join tb_sys_dict as b on a.ftype = b.fcode LEFT JOIN fvirtualcointype as c ON a.fvitypeId=c.fid "+filter;
		SQLQuery query = getSession().createSQLQuery(queryFilter);
		query.setFirstResult((page-1)*pagesize);
		query.setMaxResults(pagesize);
		List<FinacingRead> list= query.addEntity(FinacingRead.class).list();
		//List<Finacing> list = query.addEntity(Finacing.class).list();
		return list;
	}
	public List<FinacingRead> get(Integer page,int pagesize,String filter) {
		String queryFilter = "select  c.fSymbol as fsymbol,c.fname as fvitypeName,b.fvalue as fdtype,ROUND((10000*a.fproportion/100),4)  as tenThsPro"
				+ ",a.* from tb_fina_finacing as a left join tb_sys_dict as b on a.ftype = b.fcode LEFT JOIN fvirtualcointype as c ON a.fvitypeId=c.fid "+filter;
		SQLQuery query = getSession().createSQLQuery(queryFilter);
		query.setFirstResult((page-1)*pagesize);
		query.setMaxResults(pagesize);
		List<FinacingRead> list= query.addEntity(FinacingRead.class).list();
		//List<Finacing> list = query.addEntity(Finacing.class).list();
		return list;
	}
	
	@SuppressWarnings("unchecked")
	public List<Finacing> getRecommendProduct(int flag) {
		Query query;
		try {
			String sql = "from Finacing where fstatus = 1 order by fproportion desc ";
			query = getSession().createQuery(sql);
			query.setFirstResult(0);
			query.setMaxResults(flag);
			return query.list();
		} catch (DataAccessResourceFailureException e) {
			e.printStackTrace();
		} catch (HibernateException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
		return null;
	}
	public List<Finacing> getByfilter(String filter) {
		try {
			String queryFilter = "select b.fvalue as fdtype,a.* from tb_fina_finacing as a left join tb_sys_dict as b on a.ftype = b.fcode "+filter;		
			SQLQuery queryObject=getSession().createSQLQuery(queryFilter);
			@SuppressWarnings("unchecked")
			List<Finacing> list =queryObject.addEntity(Finacing.class).list();
			return list;
		} catch (DataAccessResourceFailureException e) {
		} catch (HibernateException e) {
		} catch (IllegalStateException e) {
		}
		return null;
	}

	public void save(Finacing finacing) {
		try {
			getSession().saveOrUpdate(finacing);
		} catch (Exception e) {
		}
		
	}

	public Finacing getById(String fid) {
		Finacing finacing = null;
		try {
			 finacing =	(Finacing)getSession().get(Finacing.class, fid);
		} catch (Exception e) {
		}
		return finacing;
	}

	public void del(String fids) {
		try {
			//后台删除修改为假删除
			String quertString ="update  tb_fina_finacing set fstatus=0 where fid ='"+fids+"'";
			//String quertString ="delete from tb_fina_finacing where fid ='"+fids+"'";
			int executeUpdate = getSession().createSQLQuery(quertString).executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public List<Finacing> toList(int firstResult, int maxResults, String filter) {
		Query query;
		try {
			query = getSession().createQuery(filter);
			query.setFirstResult(firstResult);
			query.setMaxResults(maxResults);
			return query.list();
		} catch (DataAccessResourceFailureException e) {
			e.printStackTrace();
		} catch (HibernateException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Finacing getByFname(String fname) {
		Finacing finacing = null;
		try {
			String queryString = "from Finacing where fname='"+fname+"'";
			Query query = getSession().createQuery(queryString);
			finacing = (Finacing)query.list().get(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return finacing;
	}
	
	public FinacingRead getByFid(String fid) {
		String queryFilter = "select c.fSymbol as fsymbol,c.fname as fvitypeName,b.fvalue as fdtype,ROUND((10000*a.fproportion/100),4) as tenThsPro,"
				+ "a.* from tb_fina_finacing as a left join tb_sys_dict as b on a.ftype = b.fcode LEFT JOIN fvirtualcointype as c ON a.fvitypeId=c.fid where a.fid='"+fid+"'";
		SQLQuery query = getSession().createSQLQuery(queryFilter);
		List<FinacingRead> list= query.addEntity(FinacingRead.class).list();
		//List<Finacing> list = query.addEntity(Finacing.class).list();
		return list.get(0);
	}

	
	public int findTount() {
		try {
			Date date = new Date();
			String formatDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
			String query = "select count(*) from Finacing where fstatus =501 and fbeginTime<'"+formatDate+"' AND fendTime>'"+formatDate+"' order by fproportion desc ";
			Query queryObject = getSession().createQuery(query);
			queryObject.setCacheable(true) ;
			long l=(Long) queryObject.list().get(0) ;
			return (int) l ;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

}
