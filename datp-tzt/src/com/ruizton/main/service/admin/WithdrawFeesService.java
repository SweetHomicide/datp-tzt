package com.ruizton.main.service.admin;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruizton.main.dao.FwithdrawfeesDAO;
import com.ruizton.main.model.Fwithdrawfees;

@Service
public class WithdrawFeesService {
	@Autowired
	private FwithdrawfeesDAO withdrawfeesDAO;
	@Autowired
	private HttpServletRequest request;

	public Fwithdrawfees findById(String id) {
		return this.withdrawfeesDAO.findById(id);
	}

	public void saveObj(Fwithdrawfees obj) {
		this.withdrawfeesDAO.save(obj);
	}

	public void deleteObj(String id) {
		Fwithdrawfees obj = this.withdrawfeesDAO.findById(id);
		this.withdrawfeesDAO.delete(obj);
	}

	public void updateObj(Fwithdrawfees obj) {
		this.withdrawfeesDAO.attachDirty(obj);
	}

	public List<Fwithdrawfees> findByProperty(String name, Object value) {
		return this.withdrawfeesDAO.findByProperty(name, value);
	}

	public List<Fwithdrawfees> findAll() {
		return this.withdrawfeesDAO.findAll();
	}
	
	public List<Fwithdrawfees> list(int firstResult, int maxResults,
			String filter,boolean isFY) {
		List<Fwithdrawfees> all = this.withdrawfeesDAO.list(firstResult, maxResults, filter,isFY);
		return all;
	}
}