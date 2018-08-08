package com.ruizton.util;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.ruizton.main.Enum.CapitalOperationInStatus;
import com.ruizton.main.Enum.CapitalOperationTypeEnum;
import com.ruizton.main.auto.TaskList;
import com.ruizton.main.model.Fbankin;
import com.ruizton.main.model.Fcapitaloperation;
import com.ruizton.main.model.Fuser;
import com.ruizton.main.model.Fvirtualcointype;
import com.ruizton.main.model.Fwallet;
import com.ruizton.main.service.admin.AdminService;
import com.ruizton.main.service.admin.CapitaloperationService;
import com.ruizton.main.service.admin.EntrustlogService;
import com.ruizton.main.service.admin.SystemArgsService;
import com.ruizton.main.service.admin.VirtualCoinService;
import com.ruizton.main.service.admin.VirtualWalletService;
import com.ruizton.main.service.front.FrontOthersService;
import com.ruizton.main.service.front.FrontUserService;

public class AutoRecharge {

	@Autowired
	private FrontOthersService frontOthersService ;
	@Autowired
	private FrontUserService frontUserService ;
	@Autowired
	private SystemArgsService systemArgsService;
	@Autowired
	private EntrustlogService entrustlogService ;
	@Autowired
	private AdminService adminService;
	@Autowired
	private TaskList taskList;
	@Autowired
	private VirtualWalletService virtualWalletService;
	@Autowired
	private VirtualCoinService virtualCoinService;
	@Autowired
	private CapitaloperationService capitaloperationService;
	
	public void work() {
		synchronized (this) {
			try{
				List<Fbankin> fbankins = this.frontOthersService.findFbankin() ;
				for (Fbankin fbankin : fbankins) {
					if(fbankin!=null 
							&& fbankin.getUid()!=null 
							&& fbankin.getRmb()!=null 
							&& fbankin.getOk().intValue()==0){
						
						fbankin.setOk(1) ;
						Fuser fuser = this.frontUserService.findById(fbankin.getUid().toString()) ;
						if(fuser!=null){
							Fwallet fwallet = frontUserService.findFwalletById(fuser.getFwallet().getFid()) ;
							double rate = Double.valueOf(this.systemArgsService.getValue("rechargeRate"));
							double amt = Utils.getDouble(fbankin.getRmb()+fbankin.getRmb()*rate, 4);
							fwallet.setFtotalRmb(fwallet.getFtotalRmb()+amt) ;
							
							Fcapitaloperation fcapitaloperation = new Fcapitaloperation() ;
							fcapitaloperation.setFamount(fbankin.getRmb()) ;
							fcapitaloperation.setFcreateTime(Utils.getTimestamp()) ;
							fcapitaloperation.setFtype(CapitalOperationTypeEnum.RMB_IN) ;
							fcapitaloperation.setFuser(fuser) ;
							fcapitaloperation.setFstatus(CapitalOperationInStatus.Come) ;
							fcapitaloperation.setFremark("支付宝");
							fcapitaloperation.setfLastUpdateTime(Utils.getTimestamp());
							try {
								this.frontOthersService.updateAutoRechargeCny(fbankin,fwallet,fcapitaloperation) ;
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						
					}
				}
			}catch(Exception e){
				e.printStackTrace() ;
			}
		}
		
	}

}
