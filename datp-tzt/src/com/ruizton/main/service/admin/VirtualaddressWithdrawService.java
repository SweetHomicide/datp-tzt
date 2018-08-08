package com.ruizton.main.service.admin;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruizton.main.dao.FvirtualaddressWithdrawDAO;
import com.ruizton.main.model.FvirtualaddressWithdraw;

@Service
public class VirtualaddressWithdrawService {
	@Autowired
	private FvirtualaddressWithdrawDAO virtualaddressWithdrawDAO;
	@Autowired
	private HttpServletRequest request;

	public FvirtualaddressWithdraw findById(String id) {
		return this.virtualaddressWithdrawDAO.findById(id);
	}

	public void saveObj(FvirtualaddressWithdraw obj) {
		this.virtualaddressWithdrawDAO.save(obj);
	}

	public void deleteObj(String id) {
		FvirtualaddressWithdraw obj = this.virtualaddressWithdrawDAO.findById(id);
		this.virtualaddressWithdrawDAO.delete(obj);
	}

	public void updateObj(FvirtualaddressWithdraw obj) {
		this.virtualaddressWithdrawDAO.attachDirty(obj);
	}

	public List<FvirtualaddressWithdraw> findByProperty(String name, Object value) {
		return this.virtualaddressWithdrawDAO.findByProperty(name, value);
	}

	public List<FvirtualaddressWithdraw> findAll() {
		return this.virtualaddressWithdrawDAO.findAll();
	}

	public List<FvirtualaddressWithdraw> list(int firstResult, int maxResults,
			String filter,boolean isFY) {
		return this.virtualaddressWithdrawDAO.list(firstResult, maxResults, filter,isFY);
	}
}