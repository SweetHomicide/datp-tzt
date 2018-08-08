package com.ruizton.main.service.admin;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruizton.main.dao.FsubscriptionDAO;
import com.ruizton.main.model.Fsubscription;

@Service
public class SubscriptionService {
	@Autowired
	private FsubscriptionDAO subscriptionDAO;
	@Autowired
	private HttpServletRequest request;

	public Fsubscription findById(String id) {
		return this.subscriptionDAO.findById(id);
	}

	public void saveObj(Fsubscription obj) {
		this.subscriptionDAO.save(obj);
	}

	public void deleteObj(String id) {
		Fsubscription obj = this.subscriptionDAO.findById(id);
		this.subscriptionDAO.delete(obj);
	}

	public void updateObj(Fsubscription obj) {
		this.subscriptionDAO.attachDirty(obj);
	}

	public List<Fsubscription> findByProperty(String name, Object value) {
		return this.subscriptionDAO.findByProperty(name, value);
	}

	public List<Fsubscription> findAll() {
		return this.subscriptionDAO.findAll();
	}

	public List<Fsubscription> list(int firstResult, int maxResults,
			String filter,boolean isFY) {
		List<Fsubscription> all =  this.subscriptionDAO.list(firstResult, maxResults, filter,isFY);
		for (Fsubscription fsubscription : all) {
			if(fsubscription.getFvirtualcointype() != null){
				fsubscription.getFvirtualcointype().getFname();
			}
			
			if(fsubscription.getFvirtualcointypeCost() != null){
				fsubscription.getFvirtualcointypeCost().getFname();
			}
		}
		return all;
	}
	public List<Fsubscription> findByParam(int firstResult, int maxResults,String filter, boolean isFY, String CLASS){
		List<Fsubscription> findByParam = this.subscriptionDAO.findByParam(firstResult, maxResults, filter, isFY, CLASS);
		return findByParam;
	}
	
	public int findCount(String sql) {
		int count = this.subscriptionDAO.findCount(sql);
		return count;
	}


	public int selectCount(String countSize) {
		int count = subscriptionDAO.selectCount(countSize);
		return count;
	}

	public int findCountBysql(String sql) {
		int count = this.subscriptionDAO.findCountBysql(sql);
		return count;
	}

	public Fsubscription findByFviId(String fviId) {
		return this.subscriptionDAO.findByFviId(fviId);
	}
	

}