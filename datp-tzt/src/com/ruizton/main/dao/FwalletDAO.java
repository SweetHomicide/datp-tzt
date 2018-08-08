package com.ruizton.main.dao;

import static org.hibernate.criterion.Example.create;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.ruizton.main.dao.comm.HibernateDaoSupport;
import com.ruizton.main.model.Fcapitaloperation;
import com.ruizton.main.model.Fuser;
import com.ruizton.main.model.Fwallet;
import com.ruizton.util.Utils;

/**
 * A data access object (DAO) providing persistence and search support for
 * Fwallet entities. Transaction control of the save(), update() and delete()
 * operations can directly support Spring container-managed transactions or they
 * can be augmented to handle user-managed Spring transactions. Each of these
 * methods provides additional information for how to configure it for the
 * desired type of transaction control.
 * 
 * @see com.ruizton.main.com.ruizton.main.model.Fwallet
 * @author MyEclipse Persistence Tools
 */
@Repository
public class FwalletDAO extends HibernateDaoSupport {
	private static final Logger log = LoggerFactory.getLogger(FwalletDAO.class);
	// property constants
	public static final String FTOTAL_RMB = "ftotalRmb";
	public static final String FFROZEN_RMB = "ffrozenRmb";

	public void save(Fwallet transientInstance) {
		log.debug("saving Fwallet instance");
		try {
			getSession().save(transientInstance);
			log.debug("save successful");
		} catch (RuntimeException re) {
			log.error("save failed", re);
			throw re;
		}
	}

	public void delete(Fwallet persistentInstance) {
		log.debug("deleting Fwallet instance");
		try {
			getSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public Fwallet findById(java.lang.String id) {
		log.debug("getting Fwallet instance with id: " + id);
		try {
			Fwallet instance = (Fwallet) getSession().get(
					"com.ruizton.main.model.Fwallet", id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public List<Fwallet> findByExample(Fwallet instance) {
		log.debug("finding Fwallet instance by example");
		try {
			List<Fwallet> results = (List<Fwallet>) getSession()
					.createCriteria("com.ruizton.main.model.Fwallet")
					.add(create(instance)).list();
			log.debug("find by example successful, result size: "
					+ results.size());
			return results;
		} catch (RuntimeException re) {
			log.error("find by example failed", re);
			throw re;
		}
	}

	public List findByProperty(String propertyName, Object value) {
		log.debug("finding Fwallet instance with property: " + propertyName
				+ ", value: " + value);
		try {
			String queryString = "from Fwallet as model where model."
					+ propertyName + "= ?";
			Query queryObject = getSession().createQuery(queryString);
			queryObject.setParameter(0, value);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}

	public List<Fwallet> findByFtotalRmb(Object ftotalRmb) {
		return findByProperty(FTOTAL_RMB, ftotalRmb);
	}

	public List<Fwallet> findByFfrozenRmb(Object ffrozenRmb) {
		return findByProperty(FFROZEN_RMB, ffrozenRmb);
	}

	public List findAll() {
		log.debug("finding all Fwallet instances");
		try {
			String queryString = "from Fwallet";
			Query queryObject = getSession().createQuery(queryString);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}

	public Fwallet merge(Fwallet detachedInstance) {
		log.debug("merging Fwallet instance");
		try {
			Fwallet result = (Fwallet) getSession().merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public void attachDirty(Fwallet instance) {
		log.debug("attaching dirty Fwallet instance");
		try {
			getSession().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(Fwallet instance) {
		log.debug("attaching clean Fwallet instance");
		try {
			getSession().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public List<Fwallet> list(int firstResult, int maxResults, String filter,
			boolean isFY) {
		List<Fwallet> list = null;
		log.debug("finding Fwallet instance with filter");
		try {
			String queryString = "from Fwallet " + filter;
			Query queryObject = getSession().createQuery(queryString);
			if (isFY) {
				queryObject.setFirstResult(firstResult);
				queryObject.setMaxResults(maxResults);
			}
			list = queryObject.list();
		} catch (RuntimeException re) {
			log.error("find by filter name failed", re);
			throw re;
		}
		return list;
	}

	public boolean withdrawCNY(Fuser fuser,
			Fcapitaloperation fcapitaloperation, long time) {
		log.debug("finding all withdrawCNY instances");
		try {
			String queryString = "update Fwallet set ftotalRmb=ftotalRmb-"
					+ fcapitaloperation.getFamount()
					+ "-"
					+ fcapitaloperation.getFfees()
					+ " ,ffrozenRmb=ffrozenRmb+"
					+ fcapitaloperation.getFamount()
					+ "+"
					+ fcapitaloperation.getFfees()
					+ ",flastUpdateTime=?  where  flastUpdateTime=? and FUs_fId=?";
			SQLQuery queryObject = getSession().createSQLQuery(queryString);
			queryObject.setTimestamp(0, Utils.getTimestamp());
			queryObject.setTimestamp(1, new Timestamp(time));
			queryObject.setString(2, fuser.getFid());//setInteger(2, fuser.getFid());

			int count = queryObject.executeUpdate();
			if (count == 0) {
				return false;
			} else {
				return true;
			}
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}

	public Map getTotalMoney() {
		Map map = new HashMap();
		String sql = "select sum(IFNULL(ftotalRmb,0)+IFNULL(ffrozenRmb,0)) ftotalRmb,sum(IFNULL(ffrozenRmb,0)) ffrozenRmb from Fwallet";
		SQLQuery queryObject = getSession().createSQLQuery(sql);
		List all = queryObject.list();
		if (all.size() > 0) {
			Object[] o = (Object[]) all.get(0);
			map.put("totalRmb", o[0]);
			map.put("frozenRmb", o[1]);
		}
		return map;
	}

}