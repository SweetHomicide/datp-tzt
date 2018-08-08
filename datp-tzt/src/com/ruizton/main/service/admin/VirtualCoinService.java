package com.ruizton.main.service.admin;

import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruizton.main.dao.FfeesDAO;
import com.ruizton.main.dao.FuserDAO;
import com.ruizton.main.dao.FvirtualaddressDAO;
import com.ruizton.main.dao.FvirtualaddressWithdrawDAO;
import com.ruizton.main.dao.FvirtualcointypeDAO;
import com.ruizton.main.dao.FvirtualwalletDAO;
import com.ruizton.main.dao.FwithdrawfeesDAO;
import com.ruizton.main.model.BTCMessage;
import com.ruizton.main.model.Ffees;
import com.ruizton.main.model.Fuser;
import com.ruizton.main.model.Fvirtualcointype;
import com.ruizton.main.model.Fwithdrawfees;
import com.ruizton.util.BTCUtils;


@Service
public class VirtualCoinService {
	@Autowired
	private FvirtualcointypeDAO virtualcointypeDAO;
	@Autowired
	private FvirtualaddressDAO virtualaddressDAO;
	@Autowired
	private FvirtualaddressWithdrawDAO virtualaddressWithdrawDAO;
	@Autowired
	private FuserDAO userDAO;
	@Autowired
	private FvirtualwalletDAO virtualwalletDAO;
	@Autowired
	private FwithdrawfeesDAO withdrawfeesDAO;
	@Autowired
	private FfeesDAO feesDAO;
	@Autowired
	private HttpServletRequest request;

	public Fvirtualcointype findById(String id) {
		return this.virtualcointypeDAO.findById(id);
	}

	public void saveObj(Fvirtualcointype obj) {
		this.virtualcointypeDAO.save(obj);
	}

	public void deleteObj(String id) {
		Fvirtualcointype obj = this.virtualcointypeDAO.findById(id);
		this.virtualcointypeDAO.delete(obj);
	}

	public void updateObj(Fvirtualcointype obj) {
		this.virtualcointypeDAO.attachDirty(obj);
	}

	public List<Fvirtualcointype> findByProperty(String name, Object value) {
		return this.virtualcointypeDAO.findByProperty(name, value);
	}

	public List<Fvirtualcointype> findAll() {
		return this.virtualcointypeDAO.findAll();
	}

	public List<Fvirtualcointype> list(int firstResult, int maxResults,
			String filter,boolean isFY) {
		List<Fvirtualcointype> all = this.virtualcointypeDAO.list(firstResult, maxResults, filter,isFY);
		for (Fvirtualcointype fvirtualcointype : all) {
			Set<Ffees> set = fvirtualcointype.getFfees();
			for (Ffees ffees : set) {
				ffees.getWithdraw();
			}
		}
		return all;
	}
	
	public boolean updateCoinType(Fvirtualcointype virtualcointype,String password) throws RuntimeException{
		String fid = virtualcointype.getFid();
//		List<Fuser> all = this.userDAO.findAll();
//		try {
//			if(virtualcointype.isFIsWithDraw()){//分红币不判断
//				BTCMessage btcMessage = new BTCMessage() ;
//				btcMessage.setACCESS_KEY(virtualcointype.getFaccess_key()) ;
//				btcMessage.setIP(virtualcointype.getFip()) ;
//				btcMessage.setPORT(virtualcointype.getFport()) ;
//				btcMessage.setSECRET_KEY(virtualcointype.getFsecrt_key()) ;
//				btcMessage.setPASSWORD(password);
//				if(btcMessage.getACCESS_KEY()==null
//						||btcMessage.getIP()==null
//						||btcMessage.getPORT()==null
//						||btcMessage.getSECRET_KEY()==null){
//					throw new RuntimeException() ;
//				}
//				BTCUtils btcUtils = new BTCUtils(btcMessage) ;
//				btcUtils.getbalanceValue();
//			}
//		} catch (Exception e1) {
//			throw new RuntimeException() ;
//		}
		
		String result = "";
		try {
			result = this.virtualcointypeDAO.startCoinType(virtualcointype.getFid());
			if(!result.equals("SUCCESS")){
				throw new RuntimeException(result) ;
			}
		} catch (Exception e) {
			throw new RuntimeException(result) ;
		}
		
		List<Fwithdrawfees> allWithDrawFees = withdrawfeesDAO.findAll();
		for (Fwithdrawfees fwithdrawfees : allWithDrawFees) {
		    String filter = "where flevel="+fwithdrawfees.getFlevel()+" and fvirtualcointype.fid='"+fid+"'";
			List<Ffees> feesList = this.feesDAO.list(0, 0, filter, false);
			if(feesList == null || feesList.size() == 0){
				Ffees fees = new Ffees();
				fees.setWithdraw(0);
				fees.setFfee(0);
				fees.setFbuyfee(0);
				fees.setFlevel(fwithdrawfees.getFlevel());
				fees.setFvirtualcointype(virtualcointype);
				feesDAO.attachDirty(fees);
			}
		}
		
		this.virtualcointypeDAO.attachDirty(virtualcointype);
		return true;
	}
}