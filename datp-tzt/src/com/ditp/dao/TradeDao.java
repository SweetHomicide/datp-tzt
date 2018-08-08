package com.ditp.dao;

import java.math.BigInteger;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.stereotype.Repository;

import com.ditp.entity.TradeLog;
import com.ditp.entity.TradeLogRead;
import com.ruizton.main.dao.comm.HibernateDaoSupport;

@Repository
public class TradeDao extends HibernateDaoSupport {

	public void save(TradeLog tradeLog) {
		try {
			getSession().saveOrUpdate(tradeLog);
		} catch (Exception e) {

		}
	}

	public List<TradeLogRead> search(String filter, int first, int max) {
		try {
			String sql = "select  tbfvi.fSymbol as fsymbol,str_to_date(tbp.fcreateTime,'%Y-%m-%d %H:%i:%s') as a, tbf.fbeginTime as beginTime, tbf.fendTime as endTime, fu.floginname as fuserName, tbf.fname  finaName,tbp.* from tb_fina_tradelog as tbp INNER JOIN tb_fina_wallet tbw on tbp.ffinaWalletid=tbw.fid "
					+ "INNER JOIN tb_fina_finacing as tbf on tbw.ffinaId=tbf.fid INNER JOIN  fuser as fu on fu.fid = tbp.fuserId "
					+ " inner join fvirtualcointype as tbfvi on tbf.fvitypeId=tbfvi.fId "
					+ "where " + filter+" and tbf.ftype in ('00101','00102') and tbf.fstatus = '00501' order by a desc";
			SQLQuery queryObject = getSession().createSQLQuery(sql);
			queryObject.setFirstResult(first);
			queryObject.setMaxResults(max);
			return queryObject.addEntity(TradeLogRead.class).list();
		} catch (RuntimeException re) {
			throw re;
		}
	}

	
	public int findCount(String filter) {
		try {
			String sql = "select COUNT(*) FROM (  select tbp.* from tb_fina_tradelog as tbp "
					+ " INNER JOIN tb_fina_wallet tbw on tbp.ffinaWalletid=tbw.fid "
					+ " INNER JOIN tb_fina_finacing tbf on tbw.ffinaId=tbf.fid where "
					+ filter+" and tbf.ftype in ('00101','00102') and tbf.fstatus = '00501' )  as count ";
			SQLQuery SQLQuery = getSession().createSQLQuery(sql);
			List<BigInteger> list = SQLQuery.list();
			int count = list.get(0).intValue();
			return count;
		} catch (Exception e) {
			throw e;
		} 
	}
	
}
