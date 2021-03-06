package com.ruizton.main.service.admin;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ditp.service.StationMailService;
import com.ruizton.main.dao.FintrolinfoDAO;
import com.ruizton.main.dao.FsubscriptionDAO;
import com.ruizton.main.dao.FsubscriptionlogDAO;
import com.ruizton.main.dao.FvirtualwalletDAO;
import com.ruizton.main.dao.FwalletDAO;
import com.ruizton.main.model.Fintrolinfo;
import com.ruizton.main.model.Fsubscription;
import com.ruizton.main.model.Fsubscriptionlog;
import com.ruizton.main.model.Fvirtualwallet;
import com.ruizton.main.model.Fwallet;

@Service
public class SubscriptionLogService {
	@Autowired
	private FsubscriptionlogDAO subscriptionlogDAO;
	@Autowired
	private FwalletDAO fwalletDAO;
	@Autowired
	private FvirtualwalletDAO virtualwalletDAO;
	@Autowired
	private FintrolinfoDAO introlinfoDAO;
	@Autowired
	private HttpServletRequest request;
	@Autowired
	private FsubscriptionDAO subscriptionDAO;
	@Autowired
	private StationMailService stationMailService;

	public Fsubscriptionlog findById(String id) {
		return this.subscriptionlogDAO.findById(id);
	}

	public void saveObj(Fsubscriptionlog obj) {
		this.subscriptionlogDAO.save(obj);
	}

	public void deleteObj(String id) {
		Fsubscriptionlog obj = this.subscriptionlogDAO.findById(id);
		this.subscriptionlogDAO.delete(obj);
	}

	public void updateObj(Fsubscriptionlog obj) {
		this.subscriptionlogDAO.attachDirty(obj);
	}

	public List<Fsubscriptionlog> findByProperty(String name, Object value) {
		return this.subscriptionlogDAO.findByProperty(name, value);
	}

	public List<Fsubscriptionlog> findAll() {
		return this.subscriptionlogDAO.findAll();
	}
	
	public void updateChargeLog(List<Fwallet> fallets,
			List<Fintrolinfo> fintrolinfos,Fsubscriptionlog fsubscriptionlog) {
		try {
			for (Fintrolinfo fintrolinfo : fintrolinfos) {
				this.introlinfoDAO.save(fintrolinfo);
			}
			for (Fwallet fwallet : fallets) {
				this.fwalletDAO.attachDirty(fwallet);
			}
			this.subscriptionlogDAO.attachDirty(fsubscriptionlog);
		} catch (Exception e) {
			throw new RuntimeException();
		}
	}
	
	public void updateChargeLog1(List<Fvirtualwallet> fvirtualwallets,
			List<Fintrolinfo> fintrolinfos,Fsubscriptionlog fsubscriptionlog) {
		try {
			for (Fintrolinfo fintrolinfo : fintrolinfos) {
				this.introlinfoDAO.save(fintrolinfo);
			}
			for (Fvirtualwallet fvirtualwallet : fvirtualwallets) {
				this.virtualwalletDAO.attachDirty(fvirtualwallet);
			}
			this.subscriptionlogDAO.attachDirty(fsubscriptionlog);
		} catch (Exception e) {
			throw new RuntimeException();
		}
	}

	public List<Fsubscriptionlog> list(int firstResult, int maxResults,
			String filter,boolean isFY) {
		List<Fsubscriptionlog> all = this.subscriptionlogDAO.list(firstResult, maxResults, filter,isFY);
		for (Fsubscriptionlog fsubscriptionlog : all) {
			if(fsubscriptionlog.getFuser() != null){
				fsubscriptionlog.getFuser().getFnickName();
			}
		}
		return all;
	}
	
	
	public void updateChargeLog(Fsubscriptionlog fsubscriptionlog,Fwallet w,Fvirtualwallet v) {
		try {
			this.subscriptionlogDAO.attachDirty(fsubscriptionlog);
			this.fwalletDAO.attachDirty(w);
			this.virtualwalletDAO.attachDirty(v);
		} catch (Exception e) {
			throw new RuntimeException();
		}
	}
	
	public void updateChargeLog(Fsubscriptionlog fsubscriptionlog,Fwallet w1,Fvirtualwallet v1,Fvirtualwallet v) {
		try {
			this.subscriptionlogDAO.attachDirty(fsubscriptionlog);
			if(w1 != null){
				this.fwalletDAO.attachDirty(w1);
			}
			if(v1 != null){
				this.virtualwalletDAO.attachDirty(v1);
			}
			this.virtualwalletDAO.attachDirty(v);
		} catch (Exception e) {
			throw new RuntimeException();
		}
	}
	
	public void updateChargeLog(Fsubscriptionlog fsubscriptionlog,Fsubscription sub) {
		try {
			this.subscriptionlogDAO.attachDirty(fsubscriptionlog);
			this.subscriptionDAO.attachDirty(sub);
			//发送站内消息
			stationMailService.sendStationMail(fsubscriptionlog);
		} catch (Exception e) {
			throw new RuntimeException();
		}
	}
}