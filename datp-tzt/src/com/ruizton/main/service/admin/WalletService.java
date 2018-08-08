package com.ruizton.main.service.admin;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruizton.main.dao.FwalletDAO;
import com.ruizton.main.model.Fwallet;

@Service
public class WalletService {
	@Autowired
	private FwalletDAO fwalletDAO;
	@Autowired
	private HttpServletRequest request;

	public Fwallet findById(String id) {
		return this.fwalletDAO.findById(id);
	}

	public void saveObj(Fwallet obj) {
		this.fwalletDAO.save(obj);
	}

	public void deleteObj(String id) {
		Fwallet obj = this.fwalletDAO.findById(id);
		this.fwalletDAO.delete(obj);
	}

	public void updateObj(Fwallet obj) {
		this.fwalletDAO.attachDirty(obj);
	}

	public List<Fwallet> findByProperty(String name, Object value) {
		return this.fwalletDAO.findByProperty(name, value);
	}

	public List<Fwallet> findAll() {
		return this.fwalletDAO.findAll();
	}

	public List<Fwallet> list(int firstResult, int maxResults,
			String filter,boolean isFY) {
		List<Fwallet> all = this.fwalletDAO.list(firstResult, maxResults, filter,isFY);
		return all;
	}
	
	public Map getTotalMoney(){
		return this.fwalletDAO.getTotalMoney();
	}
	
}