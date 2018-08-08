package com.ruizton.main.service.admin;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruizton.main.dao.FautotradeDAO;
import com.ruizton.main.dao.FadminDAO;
import com.ruizton.main.dao.FautotradeDAO;
import com.ruizton.main.model.Fautotrade;
import com.ruizton.main.model.Fadmin;
import com.ruizton.main.model.Fautotrade;

@Service
public class AutotradeService {
	@Autowired
	private FautotradeDAO autotradeDAO;
	@Autowired
	private HttpServletRequest request;

	public Fautotrade findById(String id) {
		return this.autotradeDAO.findById(id);
	}

	public void saveObj(Fautotrade obj) {
		this.autotradeDAO.save(obj);
	}

	public void deleteObj(String id) {
		Fautotrade obj = this.autotradeDAO.findById(id);
		this.autotradeDAO.delete(obj);
	}

	public void updateObj(Fautotrade obj) {
		this.autotradeDAO.attachDirty(obj);
	}

	public List<Fautotrade> findByProperty(String name, Object value) {
		return this.autotradeDAO.findByProperty(name, value);
	}

	public List<Fautotrade> findAll() {
		return this.autotradeDAO.findAll();
	}

	public List<Fautotrade> list(int firstResult, int maxResults,
			String filter,boolean isFY) {
		return this.autotradeDAO.list(firstResult, maxResults, filter,isFY);
	}
}