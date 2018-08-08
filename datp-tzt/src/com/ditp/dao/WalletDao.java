package com.ditp.dao;

import java.math.BigInteger;
import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.stereotype.Repository;
import com.ditp.entity.Wallet;
import com.ditp.entity.WalletRead;
import com.ruizton.main.dao.comm.HibernateDaoSupport;

@Repository
public class WalletDao extends HibernateDaoSupport {
	public List<Wallet> get(String filter) {

		try {
			String sql = "select * from tb_fina_wallet " + filter;
			SQLQuery queryObject = getSession().createSQLQuery(sql);
			List<Wallet> listWallet = queryObject.addEntity(Wallet.class).list();
			return listWallet;
		} catch (DataAccessResourceFailureException e) {
		} catch (HibernateException e) {
		} catch (IllegalStateException e) {
		}

		return null;
	}

	/**
	 * 保存
	 * 
	 * @param wallet
	 */
	public void Save(Wallet wallet) {
		try {
			getSession().saveOrUpdate(wallet);
		} catch (RuntimeException e) {
			throw e;
		}
	}

	@SuppressWarnings("unchecked")
	public List<WalletRead> get(int firstResult,int maxResults,String fuserId) {
		try {
			String sql = "select sum(tbw.ftotal)as ftotal,tbfvi.fSymbol as fsymbol,tbf.fid,tbf.fname finaName,tbf.ftype fintype,IFNULL((select famount from tb_fina_profitlog where  "
					+ "  fuserid= ? and fcreateTime<NOW() and ffinaid=tbf.fid  order by fcreateTime desc limit 1),0) as profamount, IFNULL((select sum(famount) from "
					+ " tb_fina_profitlog where  fuserid=? and fcreateTime<NOW() and ffinaid=tbf.fid order by "
					+ " fcreateTime),0) as sumprofamount,tbw.* from tb_fina_wallet as tbw INNER JOIN tb_fina_finacing as tbf on tbw.ffinaId=tbf.fid "
					+ " inner join fvirtualcointype tbfvi on tbf.fvitypeId=tbfvi.fid "
					+ " and tbf.fstatus = '00501' and tbw.fuserid=? GROUP BY(tbf.fname) having tbf.ftype in('00102','00101') and  ftotal!=0 ";
			SQLQuery queryObject = getSession().createSQLQuery(sql);
			queryObject.setParameter(0, fuserId);
			queryObject.setParameter(1, fuserId);
			queryObject.setParameter(2, fuserId);
			queryObject.setFirstResult(firstResult);
			queryObject.setMaxResults(maxResults);
			return queryObject.addEntity(WalletRead.class).list();
		} catch (RuntimeException e) {
			throw e;
		}
	}

	
	@SuppressWarnings("unchecked")
	public List<WalletRead> getDetails(int firstResult, int pageSize, String ffinaId, String fuserId) {
		try {
			String sql = " select tbf.fname as finaName,tbw.ftotal,tbfvi.fSymbol as fsymbol, "
					+ " (select (if(isnull(sum(tbp.famount)),0,sum(tbp.famount))) from tb_fina_profitlog as tbp where tbp.ffinaWalletid=tbw.fid "
					+ " and tbp.fcreateTime<CONCAT(DATE_FORMAT(NOW(),'%Y-%m-%d'),' 00:00') and "
					+ " tbp.fcreateTime>CONCAT(date_sub(curdate(),interval 1 day),' 00:00')) as profamount , "
					+ " (select sum(tbp.famount) from tb_fina_profitlog as tbp where tbp.ffinaWalletid=tbw.fid  "
					+ " and tbp.fcreateTime<NOW()) as sumFamount,'' as fintype,'' as sumprofamount,tbw.* from tb_fina_wallet as tbw  "
					+ " inner join tb_fina_finacing tbf on tbw.ffinaId=tbf.fid inner join fvirtualcointype tbfvi on tbf.fvitypeId=tbfvi.fid "
					+ "where tbw.fuserId= ? and tbw.ffinaId= ?  and tbw.ftotal!=0 ORDER BY fcreateTime desc ";
			SQLQuery SQLQuery = getSession().createSQLQuery(sql);
			SQLQuery.setParameter(0, fuserId);
			SQLQuery.setParameter(1, ffinaId);
			SQLQuery.setFirstResult(firstResult);
			SQLQuery.setMaxResults(pageSize);
			return SQLQuery.addEntity(WalletRead.class).list();
		} catch (Exception e) {
			throw e;
		}
	}

	public int findTount(String fuserId) {
		try {
			String sql = "select count(*) from (select tfw.ftotal,tff.* from tb_fina_finacing tff "
					+ "INNER  JOIN tb_fina_wallet tfw on tfw.ffinaid = tff.fid and tfw.fuserId = ?  "
					+ " and tff.fstatus = '00501' and tfw.ftotal!=0 GROUP BY tff.fname  ) as total ";
			SQLQuery SQLQuery = getSession().createSQLQuery(sql);
			SQLQuery.setParameter(0, fuserId);
			List<BigInteger> list = SQLQuery.list();
			int count = list.get(0).intValue();
			return count;
		} catch (DataAccessResourceFailureException e) {
			throw e;
		}
	}

	public int findTount(String fid, String ffinaId) {
		try {
			String sql = "select count(*) from tb_fina_wallet where fuserid = ? and ffinaid = ? and ftotal!=0 ";
			SQLQuery query = getSession().createSQLQuery(sql);
			query.setParameter(0, fid);
			query.setParameter(1, ffinaId);
			List<BigInteger> list = query.list();
			int count = list.get(0).intValue();
			return count;
		} catch (DataAccessResourceFailureException e) {
			throw e;
		}	
	}

	
	public Wallet findById(String fid,String fuserId) {
		try {
			String sql = "select * from tb_fina_wallet where ffinaid = ? and fuserid = ? ";
			SQLQuery query = getSession().createSQLQuery(sql);
			query.setParameter(0, fid);
			query.setParameter(1, fuserId);
			return (Wallet) query.addEntity(Wallet.class).list().get(0);
		} catch (Exception e) {
			//throw e;
		}
		return null;
	}

}
