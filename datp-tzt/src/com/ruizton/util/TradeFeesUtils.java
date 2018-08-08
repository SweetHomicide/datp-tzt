package com.ruizton.util;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.ruizton.main.Enum.EntrustStatusEnum;
import com.ruizton.main.model.Fentrust;
import com.ruizton.main.model.Fintrolinfo;
import com.ruizton.main.model.Fuser;
import com.ruizton.main.model.Fvirtualcointype;
import com.ruizton.main.model.Fwallet;
import com.ruizton.main.service.admin.CapitaloperationService;
import com.ruizton.main.service.admin.SystemArgsService;
import com.ruizton.main.service.admin.UserService;
import com.ruizton.main.service.admin.VirtualCoinService;
import com.ruizton.main.service.front.FrontTradeService;
import com.ruizton.main.service.front.FrontUserService;
import com.ruizton.main.service.front.FrontVirtualCoinService;

public class TradeFeesUtils {
	
	@Autowired
	private SystemArgsService systemArgsService;
	@Autowired
	private UserService userService;
	@Autowired
	private FrontUserService frontUserService;
	@Autowired
	private CapitaloperationService capitaloperationService;
	@Autowired
	private FrontTradeService frontTradeService ;
	@Autowired
	private FrontVirtualCoinService frontVirtualCoinService ;
	@Autowired
	private VirtualCoinService virtualCoinService;
	
	public void work(){	
		synchronized (this) {
            String TXDays = this.systemArgsService.getValue("TXDays").trim();
            double rate = Double.valueOf(this.systemArgsService.getValue("TXrate").trim());
			String filter = " where (fstatus="+EntrustStatusEnum.AllDeal+" or fstatus="+EntrustStatusEnum.Cancel+")" +
					" and fentrustType=1 and fhasSubscription=0 and DATEDIFF(NOW(),fuser.fregisterTime) <="+TXDays;
			List<Fentrust> fentrusts = this.frontTradeService.findFentrustsByParam(0, 0, filter, false) ;
			for (Fentrust fentrust : fentrusts) {
				try {
					if(fentrust.isFhasSubscription()) continue;
					if(fentrust.getFstatus()==EntrustStatusEnum.AllDeal||fentrust.getFstatus()==EntrustStatusEnum.Cancel){
						double fee = Utils.getDouble(fentrust.getFfees()-fentrust.getFleftfees(), 4) ;
					    double total = Utils.getDouble(fee*rate, 4);
					    fentrust.setFhasSubscription(true);
					    String userid = fentrust.getFuser().getFid();
					    Fuser fuser = this.frontUserService.findById(userid);
					    Fvirtualcointype cointype = this.virtualCoinService.findById(fentrust.getFvirtualcointype().getFid());
					    if(fuser.getfIntroUser_id() != null && total >0){
					    	Fuser introluser = this.frontUserService.findById(fuser.getfIntroUser_id().getFid());
					    	Fwallet fwallet = introluser.getFwallet();
					    	fwallet.setFtotalRmb(fwallet.getFtotalRmb()+total);
					    	Fintrolinfo introlInfo = new Fintrolinfo();
							introlInfo.setFcreatetime(Utils.getTimestamp());
							introlInfo.setFiscny(true);
							introlInfo.setFqty(total);
							introlInfo.setFtitle("用户UID："+fuser.getFid()+"交易"+cointype.getFname()+",奖励￥:"+total+"！");
							introlInfo.setFuser(introluser);
							this.frontTradeService.updateFentrust(fentrust,fwallet,introlInfo);
					    }else{
					    	this.frontTradeService.updateFentrust(fentrust);
					    }
					}
				} catch (Exception e) {}
			}
	    }
	}
}
