package com.ruizton.main.service.admin;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruizton.main.dao.FlimittradeDAO;
import com.ruizton.main.dao.FadminDAO;
import com.ruizton.main.dao.FlimittradeDAO;
import com.ruizton.main.model.Flimittrade;
import com.ruizton.main.model.Fadmin;
import com.ruizton.main.model.Flimittrade;

@Service
public class LimittradeService {
	@Autowired
	private FlimittradeDAO limittradeDAO;
	@Autowired
	private HttpServletRequest request;

	public Flimittrade findById(String id) {
		return this.limittradeDAO.findById(id);
	}

	public void saveObj(Flimittrade obj) {
		this.limittradeDAO.save(obj);
	}

	public void deleteObj(String id) {
		Flimittrade obj = this.limittradeDAO.findById(id);
		this.limittradeDAO.delete(obj);
	}

	public void updateObj(Flimittrade obj) {
		this.limittradeDAO.attachDirty(obj);
	}

	public List<Flimittrade> findByProperty(String name, Object value) {
		return this.limittradeDAO.findByProperty(name, value);
	}

	public List<Flimittrade> findAll() {
		return this.limittradeDAO.findAll();
	}

	public List<Flimittrade> list(int firstResult, int maxResults,
			String filter,boolean isFY) {
		return this.limittradeDAO.list(firstResult, maxResults, filter,isFY);
	}
}