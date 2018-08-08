package com.ruizton.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.ruizton.main.Enum.SubStatusEnum;
import com.ruizton.main.model.Fintrolinfo;
import com.ruizton.main.model.Fsubscription;
import com.ruizton.main.model.Fsubscriptionlog;
import com.ruizton.main.model.Fuser;
import com.ruizton.main.model.Fvirtualwallet;
import com.ruizton.main.model.Fwallet;
import com.ruizton.main.service.admin.SubscriptionLogService;
import com.ruizton.main.service.admin.SubscriptionService;
import com.ruizton.main.service.admin.SystemArgsService;
import com.ruizton.main.service.front.FrontUserService;

public class SubIntrolUtils {
	@Autowired
	private SubscriptionLogService subscriptionLogService;
	@Autowired
	private FrontUserService frontUserService;
	@Autowired
	private SystemArgsService systemArgsService;
	@Autowired
	private SubscriptionService subscriptionService;
	
	private double rate1 = 0d;
	private double rate2 = 0d;
	private double rate3 = 0d;

	public void work() {
		try {
			String[] subRates = this.systemArgsService.getValue("subRate").split("#");
			rate1 = Double.valueOf(subRates[0]);
			rate2 = Double.valueOf(subRates[1]);
			rate3 = Double.valueOf(subRates[2]);
			String filter = "where (fischarge='' or fischarge is null) and fstatus="+SubStatusEnum.YES;
			List<Fsubscriptionlog> fsubscriptionlogs = this.subscriptionLogService.list(0, 0, filter, false);
			for (Fsubscriptionlog fsubscriptionlog : fsubscriptionlogs) {
				if(fsubscriptionlog.getFischarge() != null && fsubscriptionlog.getFischarge().length()>0) continue;
				Fsubscription sub = this.subscriptionService.findById(fsubscriptionlog.getFsubscription().getFid());
				fsubscriptionlog.setFischarge("true");//设置记账
				double amt = fsubscriptionlog.getFprice()*fsubscriptionlog.getFlastcount();//购买价*中签数量
				String userid = fsubscriptionlog.getFuser().getFid();
				Fuser fuser = this.frontUserService.findById(userid);
				if(sub.getFvirtualcointypeCost() == null){
					List<Fwallet> fwallets = new ArrayList<Fwallet>();
					List<Fintrolinfo> fintrolinfos = new ArrayList<Fintrolinfo>();
					if(amt >= 0.0001){
						getAmtList(fwallets, fintrolinfos, fuser, amt, 1, userid);
					}
					
					try {
						this.subscriptionLogService.updateChargeLog(fwallets, fintrolinfos, fsubscriptionlog);
					} catch (Exception e) {
						continue;
					}
				}else{
					List<Fvirtualwallet> fwallets = new ArrayList<Fvirtualwallet>();
					List<Fintrolinfo> fintrolinfos = new ArrayList<Fintrolinfo>();
					if(amt >= 0.0001){
						getAmtList(fwallets, fintrolinfos, fuser, amt, 1, userid,sub);
					}
					
					try {
						this.subscriptionLogService.updateChargeLog1(fwallets, fintrolinfos, fsubscriptionlog);
					} catch (Exception e) {
						continue;
					}
				}
				
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void getAmtList(List<Fwallet> fwallets,List<Fintrolinfo> fintrolinfos,
			Fuser fuser,double amt,int i,String userId) {
		if(i <= 3){
			if(fuser.getfIntroUser_id() != null){
				double total = 0d;
				if(i == 1){
					total = Utils.getDouble(amt*rate1, 4);
				}else if(i == 2){
					total = Utils.getDouble(amt*rate2, 4);
				}else if(i == 3){
					total = Utils.getDouble(amt*rate3, 4);
				}
				String introlUserId = fuser.getfIntroUser_id().getFid();
				Fuser intro = this.frontUserService.findById(introlUserId) ;
				if(total >= 0.0001){
					Fwallet fwallet = this.frontUserService.findFwalletById(intro.getFwallet().getFid()) ;
					fwallet.setFtotalRmb(fwallet.getFtotalRmb()+total);
					
					Fintrolinfo fintrolinfo = new Fintrolinfo() ;
					fintrolinfo.setFcreatetime(Utils.getTimestamp()) ;
					fintrolinfo.setFqty(total) ;
					fintrolinfo.setFtitle("用户UID:"+userId+"在参与众筹，奖励：￥"+new BigDecimal(total).setScale(4,BigDecimal.ROUND_HALF_UP)) ;
					fintrolinfo.setFuser(intro) ;
					fintrolinfo.setFiscny(true);
					fwallets.add(fwallet);
					fintrolinfos.add(fintrolinfo);
				}
				i = i + 1;
				getAmtList(fwallets, fintrolinfos, intro, amt,i,userId);
			}
		}
	}
	
	private void getAmtList(List<Fvirtualwallet> fwallets,List<Fintrolinfo> fintrolinfos,
			Fuser fuser,double amt,int i,String userId,Fsubscription sub) {
		if(i <= 3){
			if(fuser.getfIntroUser_id() != null){
				double total = 0d;
				if(i == 1){
					total = Utils.getDouble(amt*rate1, 4);
				}else if(i == 2){
					total = Utils.getDouble(amt*rate2, 4);
				}else if(i == 3){
					total = Utils.getDouble(amt*rate3, 4);
				}
				String introlUserId = fuser.getfIntroUser_id().getFid();
				Fuser intro = this.frontUserService.findById(introlUserId) ;
				if(total >= 0.0001){
					Fvirtualwallet v = this.frontUserService.findVirtualWalletByUser(userId, sub.getFvirtualcointypeCost().getFid());
					v.setFtotal(v.getFtotal()+total);
					
					Fintrolinfo fintrolinfo = new Fintrolinfo() ;
					fintrolinfo.setFcreatetime(Utils.getTimestamp()) ;
					fintrolinfo.setFqty(total) ;
					fintrolinfo.setFtitle("用户UID:"+userId+"在参与众筹，奖励："+sub.getFvirtualcointypeCost().getFname()+" "+new BigDecimal(total).setScale(4,BigDecimal.ROUND_HALF_UP)) ;
					fintrolinfo.setFuser(intro) ;
					fintrolinfo.setFiscny(false);
					fwallets.add(v);
					fintrolinfos.add(fintrolinfo);
				}
				i = i + 1;
				getAmtList(fwallets, fintrolinfos, intro, amt,i,userId,sub);
			}
		}
	}
	
}