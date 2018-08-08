package com.ruizton.main.service.admin;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruizton.main.dao.FaboutDAO;
import com.ruizton.main.dao.FadminDAO;
import com.ruizton.main.dao.FpoolDAO;
import com.ruizton.main.model.Fabout;
import com.ruizton.main.model.Fadmin;
import com.ruizton.main.model.Fpool;

@Service
public class PoolService {
	@Autowired
	private FpoolDAO poolDAO;
	@Autowired
	private HttpServletRequest request;

	public Fpool findById(int id) {
		return this.poolDAO.findById(id);
	}

	public void saveObj(Fpool obj) {
		this.poolDAO.save(obj);
	}

	public void deleteObj(int id) {
		Fpool obj = this.poolDAO.findById(id);
		this.poolDAO.delete(obj);
	}

	public void updateObj(Fpool obj) {
		this.poolDAO.attachDirty(obj);
	}

	public List<Fpool> findByProperty(String name, Object value) {
		return this.poolDAO.findByProperty(name, value);
	}

	public List<Fpool> findAll() {
		return this.poolDAO.findAll();
	}
	
	public List list(int firstResult, int maxResults, String filter,boolean isFY) {
		return this.poolDAO.list(firstResult, maxResults, filter, isFY);
	}
	public Map mapFpool(int firstResult, int maxResults, String filter,boolean isFY) {
		return this.poolDAO.mapFpool(firstResult, maxResults, filter, isFY);
	}

}