package com.ruizton.main.service.admin;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruizton.main.dao.FtradehistoryDAO;
import com.ruizton.main.model.Ftradehistory;

@Service
public class TradehistoryService {
	@Autowired
	private FtradehistoryDAO tradehistoryDAO;
	@Autowired
	private HttpServletRequest request;

	public Ftradehistory findById(int id) {
		return this.tradehistoryDAO.findById(id);
	}
	
	public void updateUser(String sql){
		this.tradehistoryDAO.updateUser(sql);
	}

	public void saveObj(Ftradehistory obj) {
		this.tradehistoryDAO.save(obj);
	}

	public void deleteObj(int id) {
		Ftradehistory obj = this.tradehistoryDAO.findById(id);
		this.tradehistoryDAO.delete(obj);
	}

	public void updateObj(Ftradehistory obj) {
		this.tradehistoryDAO.attachDirty(obj);
	}

	public List<Ftradehistory> findByProperty(String name, Object value) {
		return this.tradehistoryDAO.findByProperty(name, value);
	}

	public List<Ftradehistory> findAll() {
		return this.tradehistoryDAO.findAll();
	}

	public List<Ftradehistory> list(int firstResult, int maxResults,
			String filter,boolean isFY) {
		return this.tradehistoryDAO.list(firstResult, maxResults, filter,isFY);
	}
}