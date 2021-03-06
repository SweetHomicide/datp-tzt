package com.ruizton.main.dao;

// default package

import static org.hibernate.criterion.Example.create;

import java.util.List;

import org.hibernate.LockMode;
import org.hibernate.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.ruizton.main.dao.comm.HibernateDaoSupport;
import com.ruizton.main.model.Fsystemargs;
import com.ruizton.main.model.Ftradehistory;

/**
 * A data access object (DAO) providing persistence and search support for
 * Ftradehistory entities. Transaction control of the save(), update() and
 * delete() operations can directly support Spring container-managed
 * transactions or they can be augmented to handle user-managed Spring
 * transactions. Each of these methods provides additional information for how
 * to configure it for the desired type of transaction control.
 * 
 * @see .Ftradehistory
 * @author MyEclipse Persistence Tools
 */
@Repository
public class FtradehistoryDAO extends HibernateDaoSupport {
	private static final Logger log = LoggerFactory
			.getLogger(FtradehistoryDAO.class);
	// property constants
	public static final String FPRICE = "fprice";

	public void save(Ftradehistory transientInstance) {
		log.debug("saving Ftradehistory instance");
		try {
			getSession().save(transientInstance);
			log.debug("save successful");
		} catch (RuntimeException re) {
			log.error("save failed", re);
			throw re;
		}
	}

	public void delete(Ftradehistory persistentInstance) {
		log.debug("deleting Ftradehistory instance");
		try {
			getSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public Ftradehistory findById(java.lang.Integer id) {
		log.debug("getting Ftradehistory instance with id: " + id);
		try {
			Ftradehistory instance = (Ftradehistory) getSession().get(
					"com.ruizton.main.model.Ftradehistory", id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public List<Ftradehistory> findByExample(Ftradehistory instance) {
		log.debug("finding Ftradehistory instance by example");
		try {
			List<Ftradehistory> results = (List<Ftradehistory>) getSession()
					.createCriteria("com.ruizton.main.model.Ftradehistory")
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
		log.debug("finding Ftradehistory instance with property: "
				+ propertyName + ", value: " + value);
		try {
			String queryString = "from Ftradehistory as model where model."
					+ propertyName + "= ?";
			Query queryObject = getSession().createQuery(queryString);
			queryObject.setParameter(0, value);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}

	public List<Ftradehistory> findByFprice(Object fprice) {
		return findByProperty(FPRICE, fprice);
	}

	public List findAll() {
		log.debug("finding all Ftradehistory instances");
		try {
			String queryString = "from Ftradehistory";
			Query queryObject = getSession().createQuery(queryString);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}

	public Ftradehistory merge(Ftradehistory detachedInstance) {
		log.debug("merging Ftradehistory instance");
		try {
			Ftradehistory result = (Ftradehistory) getSession().merge(
					detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public void attachDirty(Ftradehistory instance) {
		log.debug("attaching dirty Ftradehistory instance");
		try {
			getSession().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(Ftradehistory instance) {
		log.debug("attaching clean Ftradehistory instance");
		try {
			getSession().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}
	
	public List<Ftradehistory> list(int firstResult, int maxResults,
			String filter,boolean isFY) {
		List<Ftradehistory> list = null;
		log.debug("finding Ftradehistory instance with filter");
		try {
			String queryString = "from Ftradehistory "+filter;
			Query queryObject = getSession().createQuery(queryString);
			queryObject.setCacheable(true) ;
			if(isFY){
				queryObject.setFirstResult(firstResult);
				queryObject.setMaxResults(maxResults);
			}
			list = queryObject.list();
		} catch (RuntimeException re) {
			log.error("find by Ftradehistory name failed", re);
			throw re;
		}
		return list;
	}
	
	public void updateUser(String sql){
		try {
			Query queryObject = getSession().createSQLQuery(sql);
			queryObject.executeUpdate();
		} catch (RuntimeException re) {
			log.error("find by Ftradehistory name failed", re);
			throw re;
		}
	}
	

}