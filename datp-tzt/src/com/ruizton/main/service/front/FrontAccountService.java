package com.ruizton.main.service.front;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ditp.service.StationMailService;
import com.ruizton.main.Enum.CapitalOperationInStatus;
import com.ruizton.main.Enum.CapitalOperationOutStatus;
import com.ruizton.main.Enum.CapitalOperationTypeEnum;
import com.ruizton.main.Enum.RemittanceTypeEnum;
import com.ruizton.main.Enum.VirtualCapitalOperationOutStatusEnum;
import com.ruizton.main.Enum.WithdrawalsEnum;
import com.ruizton.main.dao.FcapitaloperationDAO;
import com.ruizton.main.dao.FscoreDAO;
import com.ruizton.main.dao.FsystemargsDAO;
import com.ruizton.main.dao.FuserDAO;
import com.ruizton.main.dao.FvirtualcaptualoperationDAO;
import com.ruizton.main.dao.FvirtualcointypeDAO;
import com.ruizton.main.dao.FvirtualwalletDAO;
import com.ruizton.main.dao.FwalletDAO;
import com.ruizton.main.dao.FwithdrawfeesDAO;
import com.ruizton.main.model.FbankinfoWithdraw;
import com.ruizton.main.model.Fcapitaloperation;
import com.ruizton.main.model.Fscore;
import com.ruizton.main.model.Fsystemargs;
import com.ruizton.main.model.Fuser;
import com.ruizton.main.model.Fvirtualcaptualoperation;
import com.ruizton.main.model.Fvirtualcointype;
import com.ruizton.main.model.Fvirtualwallet;
import com.ruizton.main.model.Fwallet;
import com.ruizton.main.model.Fwithdrawfees;
import com.ruizton.util.Utils;

@Service
public class FrontAccountService {
	private static final Logger log = LoggerFactory.getLogger(FrontAccountService.class);
	@Autowired
	private FcapitaloperationDAO fcapitaloperationDAO ;
	@Autowired
	private FwalletDAO fwalletDAO ;
	@Autowired
	private FwithdrawfeesDAO fwithdrawfeesDAO ;
	@Autowired
	private FscoreDAO fscoreDAO ;
	@Autowired
	private FuserDAO fuserDAO ;
	@Autowired
	private FvirtualwalletDAO fvirtualwalletDAO ;
	@Autowired
	private FvirtualcaptualoperationDAO fvirtualcaptualoperationDAO ;
	@Autowired
	private FsystemargsDAO fsystemargsDAO ;
	@Autowired
	private FvirtualcointypeDAO fvirtualcointypeDAO ;
	@Autowired
	private StationMailService stationMailService;
	
	public void addFcapitaloperation(Fcapitaloperation fcapitaloperation){
		this.fcapitaloperationDAO.save(fcapitaloperation) ;
		
	}
	
	public List<Fcapitaloperation> findCapitalList(int firstResult, int maxResults,String filter,boolean isFY){
		return this.fcapitaloperationDAO.findByParam(firstResult, maxResults, filter,isFY) ;
	}
	
	public int findCapitalCount(Map<String, Object> param){
		return this.fcapitaloperationDAO.findByParamCount(param) ;
	}
	
	public int findCapitalCounts(Map<String, Object> param, String dates, int fstatus){
		return this.fcapitaloperationDAO.findByParamCounts(param, dates, fstatus);
	}
	
	public Fcapitaloperation findCapitalOperationById(String id){
		Fcapitaloperation fcapitaloperation = this.fcapitaloperationDAO.findById(id) ;
		return fcapitaloperation ;
	}
	
	public void updateCapitalOperation(Fcapitaloperation fcapitaloperation){
		this.fcapitaloperationDAO.attachDirty(fcapitaloperation) ;
	}
	
	public void updateSaveCapitalOperation(Fcapitaloperation fcapitaloperation){
		this.fcapitaloperationDAO.save(fcapitaloperation) ;
	}
	
	public Fwallet findWalletByFuser(Fuser fuser){
		List<Fwallet> list = this.fwalletDAO.findByProperty("fuser.fid", fuser.getFid()) ;
		if(list.size()<=0){
			log.error("Fuser:"+fuser.getFid()+" has no Fwallet.") ;
		}else if(list.size()>1){
			log.error("Fuser:"+fuser.getFid()+" has more than one Fwallet.") ;
		}
		return list.get(0) ;
	}
	
	public boolean updateWithdrawCNY(double withdrawBanlance,Fuser fuser,FbankinfoWithdraw fbankinfoWithdraw) throws Exception{
		boolean flag = false ;
		try {
			Fwallet fwallet = fuser.getFwallet() ;
			
			double feesRate = this.findWithdrawFeesByLevel(fuser.getFscore().getFlevel()) ;
			
			Fcapitaloperation fcapitaloperation = new Fcapitaloperation() ;
			fcapitaloperation.setfBank(fbankinfoWithdraw.getFname()) ;
			fcapitaloperation.setfAccount(fbankinfoWithdraw.getFbankNumber()) ;
			fcapitaloperation.setFaddress(fbankinfoWithdraw.getFaddress()+fbankinfoWithdraw.getFothers());
			double fees = Utils.getSubDouble(withdrawBanlance*feesRate,2);
			double amt = Utils.getSubDouble((withdrawBanlance-fees),2);

			fcapitaloperation.setFamount(amt) ;
			fcapitaloperation.setFfees(fees) ;
			fcapitaloperation.setFcreateTime(Utils.getTimestamp()) ;
			fcapitaloperation.setFtype(CapitalOperationTypeEnum.RMB_OUT) ;
			fcapitaloperation.setFuser(fuser) ;
			fcapitaloperation.setfLastUpdateTime(Utils.getTimestamp()) ;
			fcapitaloperation.setfPhone(fuser.getFtelephone()) ;
			fcapitaloperation.setfPayee(fuser.getFrealName()) ;
			fcapitaloperation.setFremittanceType(null) ;
			fcapitaloperation.setFstatus(CapitalOperationOutStatus.WaitForOperation) ;
			this.fcapitaloperationDAO.save(fcapitaloperation) ;
				
			fwallet.setFtotalRmb(fwallet.getFtotalRmb()-amt-fees) ;
			fwallet.setFfrozenRmb(fwallet.getFfrozenRmb()+amt+fees) ;
			this.fwalletDAO.attachDirty(fwallet) ;
			flag = true ;
		} catch (Exception e) {
			throw new RuntimeException();
		}
		return flag ;
	}
	
	public boolean updateWithdrawCNY(String symbol, Fvirtualwallet fvirtualwallet, double fprice, double withdrawBanlance,Fuser fuser,FbankinfoWithdraw fbankinfoWithdraw) throws Exception{
		boolean flag = false ;
		try {
			double feesRate = this.findWithdrawFeesByLevel(fuser.getFscore().getFlevel()) ;
			//double totalBTC= withdrawBanlance*fprice;
			Fcapitaloperation fcapitaloperation = new Fcapitaloperation() ;
			fcapitaloperation.setfBank(fbankinfoWithdraw.getFname()) ;
			fcapitaloperation.setfAccount(fbankinfoWithdraw.getFbankNumber()) ;
			fcapitaloperation.setFaddress(fbankinfoWithdraw.getFaddress()+fbankinfoWithdraw.getFothers());
			double fees = Utils.getSubDouble(withdrawBanlance/fprice*feesRate,2);
			double amt = Utils.getSubDouble((withdrawBanlance/fprice-fees),2);
			Fvirtualcointype findById = this.fvirtualcointypeDAO.findById(symbol);
			fcapitaloperation.setFtotalBTC(withdrawBanlance);
			fcapitaloperation.setFviType(findById);
			fcapitaloperation.setFamount(amt) ;
			fcapitaloperation.setFfees(fees) ;
			fcapitaloperation.setFcreateTime(Utils.getTimestamp()) ;
			fcapitaloperation.setFtype(4) ;
			fcapitaloperation.setFuser(fuser) ;
			fcapitaloperation.setfLastUpdateTime(Utils.getTimestamp()) ;
			fcapitaloperation.setfPhone(fuser.getFtelephone()) ;
			fcapitaloperation.setfPayee(fuser.getFrealName()) ;
			fcapitaloperation.setFremittanceType(null) ;
			fcapitaloperation.setFstatus(CapitalOperationOutStatus.WaitForOperation) ;
			this.fcapitaloperationDAO.save(fcapitaloperation) ;
				
			fvirtualwallet.setFtotal(fvirtualwallet.getFtotal()-withdrawBanlance) ;
			fvirtualwallet.setFfrozen(fvirtualwallet.getFfrozen()+withdrawBanlance) ;
			fvirtualwallet.setFlastUpdateTime(Utils.getTimestamp()) ;
			this.fvirtualwalletDAO.attachDirty(fvirtualwallet) ;
			flag = true ;
		} catch (Exception e) {
			throw new RuntimeException();
		}
		return flag ;
	}
	
	
	public boolean updateWithdrawCNY(double withdrawBanlance,Fuser fuser,FbankinfoWithdraw fbankinfoWithdraw, int WithdrawType, String FolderIds) throws Exception{
		boolean flag = false ;
		try {
			Fwallet fwallet = fuser.getFwallet() ;
			if(fwallet.getFtotalRmb()<withdrawBanlance){
				return false ;
			}
			
			double feesRate = this.findWithdrawFeesByLevel(fuser.getFscore().getFlevel()) ;
			
			Fcapitaloperation fcapitaloperation = new Fcapitaloperation() ;

			fcapitaloperation.setfBank(fbankinfoWithdraw.getFname()) ;
			fcapitaloperation.setfAccount(fbankinfoWithdraw.getFbankNumber()) ;
			fcapitaloperation.setFaddress(fbankinfoWithdraw.getFaddress()+fbankinfoWithdraw.getFothers());
			double amt = Utils.getDouble(withdrawBanlance*(1.0F-feesRate),2);
			double fees = Utils.getDouble(withdrawBanlance*feesRate,2);
			fcapitaloperation.setFamount(amt) ;
			fcapitaloperation.setFfees(fees) ;
			fcapitaloperation.setFcreateTime(Utils.getTimestamp()) ;
			fcapitaloperation.setFtype(CapitalOperationTypeEnum.RMB_OUT) ;
			fcapitaloperation.setFuser(fuser) ;
			fcapitaloperation.setfLastUpdateTime(Utils.getTimestamp()) ;
			fcapitaloperation.setfPhone(fuser.getFtelephone()) ;
			fcapitaloperation.setfPayee(fuser.getFrealName()) ;
			fcapitaloperation.setFremittanceType(WithdrawalsEnum.getEnumString(WithdrawType));
			fcapitaloperation.setForderId(FolderIds);
			fcapitaloperation.setFstatus(CapitalOperationOutStatus.WaitForOperation) ;
			this.fcapitaloperationDAO.save(fcapitaloperation) ;
				
			fwallet.setFtotalRmb(fwallet.getFtotalRmb()-amt-fees) ;
			fwallet.setFfrozenRmb(fwallet.getFfrozenRmb()+amt+fees) ;
			this.fwalletDAO.attachDirty(fwallet) ;
			flag = true ;
		} catch (Exception e) {
			throw new RuntimeException();
		}
		return flag ;
	}
	public double findWithdrawFeesByLevel(int level){
		List<Fwithdrawfees> fwithdrawfees = this.fwithdrawfeesDAO.findByProperty("flevel", level) ;
		if(fwithdrawfees.size()==0 || fwithdrawfees.isEmpty()){
               return 0;
		}
		return fwithdrawfees.get(0).getFfee() ;
	}
	public Fscore findFscoreById(int id){
		return this.fscoreDAO.findById(id) ;
	}
	
	public void updateCancelWithdrawCny(Fcapitaloperation fcapitaloperation,Fuser fuser){
		try {
			fcapitaloperation.setFstatus(CapitalOperationOutStatus.Cancel) ;
			fcapitaloperation.setfLastUpdateTime(Utils.getTimestamp()) ;
			if(fcapitaloperation.getFviType()!=null){
				Fvirtualwallet fvirtualwallet = this.fvirtualwalletDAO.findVirtualWallet(fuser.getFid(), fcapitaloperation.getFviType().getFid()) ;
				fvirtualwallet.setFfrozen(fvirtualwallet.getFfrozen()-fcapitaloperation.getFtotalBTC());
				fvirtualwallet.setFtotal(fvirtualwallet.getFtotal()+fcapitaloperation.getFtotalBTC());
				this.fvirtualwalletDAO.attachDirty(fvirtualwallet) ;
			}else{
				
				Fwallet fwallet = fuser.getFwallet() ;
				fwallet.setFfrozenRmb(fwallet.getFfrozenRmb()-fcapitaloperation.getFfees()-fcapitaloperation.getFamount()) ;
				fwallet.setFtotalRmb(fwallet.getFtotalRmb()+fcapitaloperation.getFfees()+fcapitaloperation.getFamount()) ;
				this.fwalletDAO.attachDirty(fwallet) ;
			
			}
			
			this.fcapitaloperationDAO.attachDirty(fcapitaloperation) ;
		} catch (Exception e) {
			throw new RuntimeException();
		}
	}
	
	public void updateCancelWithdrawBtc(Fvirtualcaptualoperation fvirtualcaptualoperation,Fuser fuser){
		try {
			fvirtualcaptualoperation.setFstatus(VirtualCapitalOperationOutStatusEnum.Cancel) ;
			fvirtualcaptualoperation.setFlastUpdateTime(Utils.getTimestamp()) ;
			
			double amount = fvirtualcaptualoperation.getFamount()+fvirtualcaptualoperation.getFfees() ;
			Fvirtualwallet fvirtualwallet = this.fvirtualwalletDAO.findVirtualWallet(fuser.getFid(), fvirtualcaptualoperation.getFvirtualcointype().getFid()) ;
			fvirtualwallet.setFtotal(fvirtualwallet.getFtotal()+amount) ;
			fvirtualwallet.setFfrozen(fvirtualwallet.getFfrozen()-amount) ;
			fvirtualwallet.setFlastUpdateTime(Utils.getTimestamp()) ;
			
			this.fvirtualwalletDAO.attachDirty(fvirtualwallet) ;
			this.fvirtualcaptualoperationDAO.attachDirty(fvirtualcaptualoperation) ;
		} catch (Exception e) {
			throw new RuntimeException();
		}
	}
	public void savefvirtualwallet(Fcapitaloperation capitaloperation, Fvirtualwallet fvirtualwallet){
		this.fcapitaloperationDAO.attachDirty(capitaloperation);
		this.fvirtualwalletDAO.attachDirty(fvirtualwallet) ;
		if(capitaloperation.getFstatus()==3)
		{
		stationMailService.sendStationMail(capitaloperation);
		}
	}
	public int getTodayCnyWithdrawTimes(Fuser fuser){
		return this.fcapitaloperationDAO.getTodayCnyWithdrawTimes(fuser) ;
	}
	public int getTodayVirtualCoinWithdrawTimes(Fuser fuser){
		return this.fvirtualcaptualoperationDAO.getTodayVirtualCoinWithdrawTimes(fuser) ;
	}
	
	//第三方支付充值加钱
	public void updateCapitalRecharge3(Fcapitaloperation fcapitaloperation){
		try {
			//System.out.println(".....................xxxx.fuck");
			Fuser fuser = this.fuserDAO.findById(fcapitaloperation.getFuser().getFid()) ;
			Fwallet fwallet = this.fwalletDAO.findById(fuser.getFwallet().getFid()) ;
			
			
			fwallet.setFtotalRmb(fwallet.getFtotalRmb()+fcapitaloperation.getFamount()) ;
			fwallet.setFlastUpdateTime(Utils.getTimestamp()) ;
			this.fwalletDAO.attachDirty(fwallet) ;
			
			fcapitaloperation.setFstatus(CapitalOperationInStatus.Come) ;
			fcapitaloperation.setfLastUpdateTime(Utils.getTimestamp()) ;
			this.fcapitaloperationDAO.attachDirty(fcapitaloperation) ;
		} catch (Exception e) {
			throw new RuntimeException();
		}
	}

	public List<Fcapitaloperation> findByFolderId(String folderIds) {
		return this.fcapitaloperationDAO.findByFolderId(folderIds) ;
	}
		
}
