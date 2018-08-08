package com.ditp.dao;

import java.math.BigInteger;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.stereotype.Repository;

import com.ditp.entity.ProfitLog;
import com.ditp.entity.ProfitLogRead;
import com.ruizton.main.dao.comm.HibernateDaoSupport;

@Repository
public class ProfitlogDAO extends HibernateDaoSupport {
	public List<ProfitLogRead> search(String filter, int first, int max) {

		try {
			String sql = "select tbfvi.fSymbol as fsymbol,str_to_date(tbp.fcreateTime,'%Y-%m-%d %H:%i:%s') as a"
					+ ",'' as endTime,'' as beginTime, tbf.fname as finaName,tbp.* from tb_fina_profitlog as tbp INNER JOIN tb_fina_wallet tbw on tbp.ffinaWalletid=tbw.fid "
					+ "INNER JOIN tb_fina_finacing tbf on tbw.ffinaId=tbf.fid "
					+ "inner join fvirtualcointype as tbfvi on tbf.fvitypeId=tbfvi.fId "
					+ "where " + filter+" ORDER BY a desc  ";
			SQLQuery queryObject = getSession().createSQLQuery(sql);
			queryObject.setFirstResult(first);
			queryObject.setMaxResults(max);
			return queryObject.addEntity(ProfitLogRead.class).list();
		} catch (RuntimeException re) {
			throw re;
		}

	}
	public void Save(ProfitLog profitLog)
	{
		try{
			getSession().saveOrUpdate(profitLog);
		}catch(RuntimeException re){
			throw re;
		}
	}
	
	
	public int findCount(String filter) {
		try {
			String sql = " select COUNT(*) FROM (select tbp.* from tb_fina_profitlog as tbp "
					+ " INNER JOIN tb_fina_wallet tbw on tbp.ffinaWalletid=tbw.fid "
					+ " INNER JOIN tb_fina_finacing tbf on tbw.ffinaId=tbf.fid where  "
					+ filter+" ) as count ";
			SQLQuery SQLQuery = getSession().createSQLQuery(sql);
			List<BigInteger> list = SQLQuery.list();
			int count = list.get(0).intValue();
			return count;
		} catch (Exception e) {
			throw e;
		} 
	}
}
