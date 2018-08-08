package com.ruizton.main.service.admin;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruizton.main.dao.FentrustDAO;
import com.ruizton.main.model.Fentrust;

@Service
public class EntrustService {
	@Autowired
	private FentrustDAO entrustDAO;
	@Autowired
	private HttpServletRequest request;

	public Fentrust findById(String id) {
		return this.entrustDAO.findById(id);
	}

	public void saveObj(Fentrust obj) {
		this.entrustDAO.save(obj);
	}

	public void deleteObj(String id) {
		Fentrust obj = this.entrustDAO.findById(id);
		this.entrustDAO.delete(obj);
	}

	public void updateObj(Fentrust obj) {
		this.entrustDAO.attachDirty(obj);
	}

	public List<Fentrust> findByProperty(String name, Object value) {
		return this.entrustDAO.findByProperty(name, value);
	}

	public List<Fentrust> findAll() {
		return this.entrustDAO.findAll();
	}

	public List<Fentrust> list(int firstResult, int maxResults,
			String filter,boolean isFY) {
		List<Fentrust> all = this.entrustDAO.list(firstResult, maxResults, filter,isFY);
		for (Fentrust fentrust : all) {
			if(fentrust.getFuser() != null){
				fentrust.getFuser().getFnickName();
			}
			if(fentrust.getFvirtualcointype() != null){
				fentrust.getFvirtualcointype().getFname();
			}
		}
		return all;
	}
	
	public List<Map> getTotalQty(int fentrustType) {
		return this.entrustDAO.getTotalQty(fentrustType);
	}
	
	public Map getTotalBuyFees(int fentrustType,String startDate,String endDate) {
		return this.entrustDAO.getTotalBuyFees(fentrustType, startDate, endDate);
	}
	
	public Map<String,Double> getTotalBuyCoin(int fentrustType,String startDate,String endDate) {
		return this.entrustDAO.getTotalBuyCoin(fentrustType, startDate, endDate);
	}
	
	public double getTotalBuyCoin(int fentrustType,String startDate,String endDate,int fid) {
		return this.entrustDAO.getTotalBuyCoin(fentrustType, startDate, endDate, fid);
	}

	public List<Map> getTotalQty(int fentrustType,boolean isToady) {
		return this.entrustDAO.getTotalQty(fentrustType,isToady);
	}
}