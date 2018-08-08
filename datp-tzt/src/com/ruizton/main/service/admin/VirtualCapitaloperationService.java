package com.ruizton.main.service.admin;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruizton.main.dao.FvirtualcaptualoperationDAO;
import com.ruizton.main.dao.FvirtualwalletDAO;
import com.ruizton.main.dao.FwalletDAO;
import com.ruizton.main.model.Fvirtualcaptualoperation;
import com.ruizton.main.model.Fvirtualwallet;
import com.ruizton.main.model.Fwallet;
import com.ruizton.util.BTCUtils;

@Service
public class VirtualCapitaloperationService {
	@Autowired
	private FvirtualcaptualoperationDAO virtualcaptualoperationDAO;
	@Autowired
	private FvirtualwalletDAO virtualwalletDAO;
	@Autowired
	private SystemArgsService systemArgsService;

	public Fvirtualcaptualoperation findById(String id) {
		Fvirtualcaptualoperation info = this.virtualcaptualoperationDAO.findById(id);
		info.getFuser().getFnickName();
		info.getFvirtualcointype().getFname();
		return info;
	}

	public void saveObj(Fvirtualcaptualoperation obj) {
		this.virtualcaptualoperationDAO.save(obj);
	}

	public void deleteObj(String id) {
		Fvirtualcaptualoperation obj = this.virtualcaptualoperationDAO
				.findById(id);
		this.virtualcaptualoperationDAO.delete(obj);
	}

	public void updateObj(Fvirtualcaptualoperation obj) {
		this.virtualcaptualoperationDAO.attachDirty(obj);
	}

	public List<Fvirtualcaptualoperation> findByProperty(String name,
			Object value) {
		return this.virtualcaptualoperationDAO.findByProperty(name, value);
	}

	public List<Fvirtualcaptualoperation> findAll() {
		return this.virtualcaptualoperationDAO.findAll();
	}

	public List<Fvirtualcaptualoperation> list(int firstResult, int maxResults,
			String filter, boolean isFY) {
		List<Fvirtualcaptualoperation> all = this.virtualcaptualoperationDAO
				.list(firstResult, maxResults, filter, isFY);
		for (Fvirtualcaptualoperation virtualcaptualoperation : all) {
			if(virtualcaptualoperation.getFuser() != null){
				virtualcaptualoperation.getFuser().getFemail();
			}
			if(virtualcaptualoperation.getFvirtualcointype() != null){
				virtualcaptualoperation.getFvirtualcointype().getfShortName();
			}
			
		}
		return all;
	}

	public void updateCapital(Fvirtualcaptualoperation virtualcaptualoperation,
			Fvirtualwallet virtualwallet,BTCUtils btcUtils) throws RuntimeException {
		if(virtualcaptualoperation.getFtradeUniqueNumber() != null
				&& virtualcaptualoperation.getFtradeUniqueNumber().trim().length()>0){
			return;
		}
		
		try {
			boolean isPasswordTrue = btcUtils.walletpassphrase(10);
			if(!isPasswordTrue){
				throw new RuntimeException("钱包验证失败");
			}
		} catch (Exception e2) {
			throw new RuntimeException("钱包验证失败");
		}
		
		
		String address = virtualcaptualoperation.getWithdraw_virtual_address();
		double amount = virtualcaptualoperation.getFamount();
		try {
			this.virtualcaptualoperationDAO.attachDirty(virtualcaptualoperation);
			this.virtualwalletDAO.attachDirty(virtualwallet);
		} catch (Exception e1) {
			throw new RuntimeException("发送失败");
		}
		
		try {
			String isOpenAutoWidthCoin = this.systemArgsService.getValue("isOpenAutoWidthCoin").trim();
			if(isOpenAutoWidthCoin.equals("1")){
				String flag = btcUtils.sendtoaddressValue(address,amount,virtualcaptualoperation.getFid().toString());
				if(flag != null && !"".equals(flag)){
					virtualcaptualoperation.setFtradeUniqueNumber(flag);
					this.virtualcaptualoperationDAO.attachDirty(virtualcaptualoperation);
				}
			}
		} catch (Exception e) {}
	}
	
	public List<Map> getTotalAmount(int type,String fstatus,boolean isToday) {
		return this.virtualcaptualoperationDAO.getTotalQty(type, fstatus,isToday);
	}
	
	public List getTotalGroup(String filter) {
		return this.virtualcaptualoperationDAO.getTotalGroup(filter);
	}
	
	public void updateCharge(Fvirtualcaptualoperation v,Fvirtualwallet w) {
		try {
			this.virtualcaptualoperationDAO.attachDirty(v);
			this.virtualwalletDAO.attachDirty(w);
		} catch (Exception e) {
			throw new RuntimeException();
		}

	}
}