package com.ruizton.main.service.front;

import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruizton.main.auto.RealTimeData;
import com.ruizton.main.dao.FassetDAO;
import com.ruizton.main.dao.FvirtualcointypeDAO;
import com.ruizton.main.dao.FvirtualwalletDAO;
import com.ruizton.main.dao.FwalletDAO;
import com.ruizton.main.model.Fasset;
import com.ruizton.main.model.Fuser;
import com.ruizton.main.model.Fvirtualcointype;
import com.ruizton.main.model.Fvirtualwallet;
import com.ruizton.main.model.Fwallet;
import com.ruizton.util.Utils;

@Service
public class AssetService {

	@Autowired
	private FassetDAO fassetDAO ;
	@Autowired
	private FvirtualcointypeDAO fvirtualcointypeDAO ;
	@Autowired
	private FwalletDAO fwalletDAO ;
	@Autowired
	private FvirtualwalletDAO fvirtualwalletDAO ;
	@Autowired
	private RealTimeData realTimeData ;
	@Autowired
	private FrontUserService frontUserService ;
	
	//记录所有用户的资产明细
	public boolean updateAllAssets(){
		String sql = " insert into fasset(fid,fuser, ftotal, fcreatetime, flastupdatetime, version, status ) select REPLACE (UUID(), '-', '') as fa_fid,fid,0,now(),now(),1,0 from fuser " ;
		int count = this.fassetDAO.executeSQL(sql) ;
		return count>0 ;
	}
	

	//更新明细
	public boolean updateAllAssetsDetail() {
		List<Fvirtualcointype> fvirtualcointypes = this.fvirtualcointypeDAO.findByParam(0, 0, "", false, Fvirtualcointype.class.getName()) ;
		List<Fasset> fassets = this.fassetDAO.findByParam(0, 300, " where status=0 ", true, Fasset.class.getName()) ;
		for (Fasset fasset : fassets) {
			JSONObject jsonObject = new JSONObject() ;
			Double total = 0D ;//预估总资产
			
			Fuser fuser = this.frontUserService.findById(fasset.getFuser().getFid()) ;
			Fwallet fwallet = fuser.getFwallet() ;
			List<Fvirtualwallet> fvirtualwallets = this.fvirtualwalletDAO.findByParam(0, 0, " where fuser.fid='"+fasset.getFuser().getFid()+"'", false, Fvirtualwallet.class.getName()) ;
			
			JSONObject cny = new JSONObject() ;
			cny.accumulate("total", fwallet.getFtotalRmb()) ;
			cny.accumulate("frozen", fwallet.getFfrozenRmb()) ;
			jsonObject.accumulate("0", cny) ;
			total+=fwallet.getFtotalRmb()+fwallet.getFfrozenRmb();
			
			for (Fvirtualwallet fvirtualwallet : fvirtualwallets) {
				JSONObject item = new JSONObject() ;
				item.accumulate("total", fvirtualwallet.getFtotal()) ;
				item.accumulate("frozen", fvirtualwallet.getFfrozen()) ;
				jsonObject.accumulate(String.valueOf(fvirtualwallet.getFvirtualcointype().getFid()), item) ;
				total+=(fvirtualwallet.getFtotal()+fvirtualwallet.getFfrozen())*this.realTimeData.getLatestDealPrize(fvirtualwallet.getFvirtualcointype().getFid()) ;
			}
			
			fasset.setDetail(jsonObject.toString()) ;
			fasset.setFtotal(Utils.getDouble(total, 2)) ;
			fasset.setStatus(true) ;
			this.fassetDAO.attachDirty(fasset) ;
		}
		return fassets.size()>0 ;
	}
}
