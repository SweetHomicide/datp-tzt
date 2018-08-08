package com.ruizton.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.ruizton.main.Enum.EntrustStatusEnum;
import com.ruizton.main.Enum.EntrustTypeEnum;
import com.ruizton.main.Enum.SubscriptionTypeEnum;
import com.ruizton.main.dao.FvirtualcointypeDAO;
import com.ruizton.main.model.Fentrust;
import com.ruizton.main.model.Fentrustlog;
import com.ruizton.main.model.Fintrolinfo;
import com.ruizton.main.model.Fsubscription;
import com.ruizton.main.model.Fuser;
import com.ruizton.main.model.Fvirtualcointype;
import com.ruizton.main.model.Fvirtualwallet;
import com.ruizton.main.model.Fwallet;
import com.ruizton.main.service.admin.SubscriptionService;
import com.ruizton.main.service.admin.SystemArgsService;
import com.ruizton.main.service.admin.VirtualCoinService;
import com.ruizton.main.service.front.FrontTradeService;
import com.ruizton.main.service.front.FrontUserService;

public class AutoFentrustSubscription {

	@Autowired
	private FrontTradeService frontTradeService ;
	@Autowired
	private FrontUserService frontUserService ;
	@Autowired
	private SystemArgsService systemArgsService ;
	@Autowired
	private VirtualCoinService virtualCoinService;
	@Autowired
	private FvirtualcointypeDAO fvirtualcointypeDAO;
	@Autowired
	private SubscriptionService subscriptionService;
	
	private double rate1=0d;
	private double rate2=0d;
	private double rate3=0d;
	
	public void work() {
		synchronized (this) {
		try{
			String[] introlTradeRate = this.systemArgsService.getValue("introlTradeRate").trim().split("#");
			rate1 = Double.valueOf(introlTradeRate[0]);
			rate2 = Double.valueOf(introlTradeRate[1]);
			rate3 = Double.valueOf(introlTradeRate[2]);
//			String filter = " where (fstatus="+EntrustStatusEnum.AllDeal+" or fstatus="+EntrustStatusEnum.Cancel+") and fhasSubscription=0  " ;
//			List<Fentrust> fentrusts = this.frontTradeService.findFentrustsByParam(0, 0, filter, false) ;
			String filter = " where  fhasSubscription=0  " ;
			List<Fentrustlog> Fentrustlogs = this.frontTradeService.findFentrustLogsByParam(0, 0, filter, false) ;
			if (Fentrustlogs.size()>0) {
				for (Fentrustlog Fentrustlog : Fentrustlogs) {
					try {
						if(Fentrustlog.isFhasSubscription()) continue;
						//if(fentrust.getFstatus()==EntrustStatusEnum.AllDeal||fentrust.getFstatus()==EntrustStatusEnum.Cancel){
							double fee = Utils.getDouble(Fentrustlog.getFfees(), 4) ;
							Fentrustlog.setFhasSubscription(true) ;
							if (null!=Fentrustlog.getfus_fId()) {
								Fuser fuser = this.frontUserService.findById(Fentrustlog.getfus_fId());
								String vid = Fentrustlog.getFvirtualcointype().getFid();
								Fvirtualcointype cointype = this.virtualCoinService.findById(vid);
								if(fee>0.0001D){
									if(Fentrustlog.getfEntrustType() == EntrustTypeEnum.BUY){
										List<Fvirtualwallet> fvirtualwallets = new ArrayList<Fvirtualwallet>();
										List<Fintrolinfo> fintrolinfos = new ArrayList<Fintrolinfo>();
										getCoinList(fvirtualwallets, fintrolinfos, fuser, fee, 1, fuser.getFid(), cointype.getFname(), vid);
										this.frontTradeService.updateCoinFentrustLog(Fentrustlog,fvirtualwallets, fintrolinfos);
									}else{
										//获取默认兑换rmb种类
										List<Fvirtualcointype> findByParam = this.fvirtualcointypeDAO.findByParam(0, 0, " where fisDefAsset=1", false, "Fvirtualcointype");
										boolean flags = findByParam.size()!=0;
										String filter2 = "where fisRMB=1 and ftype=" + SubscriptionTypeEnum.COIN +" and fvirtualcointype.fid='"+findByParam.get(0).getFid()+"' order by fcreateTime desc";;
										List<Fsubscription> list = this.subscriptionService.list(0, 0, filter2, false);
										Fsubscription fsubscription = list.get(0);
										if(flags){
											List<Fvirtualwallet> fvirtualwallets = new ArrayList<Fvirtualwallet>();
											List<Fintrolinfo> fintrolinfos = new ArrayList<Fintrolinfo>();
											getAmtLists(fsubscription, findByParam.get(0), fvirtualwallets, fintrolinfos, fuser, fee, 1, fuser.getFid(), cointype.getFname(), vid);
											this.frontTradeService.updateCoinFentrustLog(Fentrustlog,fvirtualwallets, fintrolinfos);
										}else{
											List<Fwallet> fwallets = new ArrayList<Fwallet>();
											List<Fintrolinfo> fintrolinfos = new ArrayList<Fintrolinfo>();
											getAmtList(fwallets, fintrolinfos, fuser, fee, 1, fuser.getFid(), cointype.getFname(), vid);
											this.frontTradeService.updateFentrustLog(Fentrustlog,fwallets, fintrolinfos);
										}
										
										
									}
								}else{
									this.frontTradeService.updateFentrustLog(Fentrustlog);
								}
							}
					//	}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}catch(Exception e){}
	}
	}
	
	private void getAmtList(List<Fwallet> fwallets,List<Fintrolinfo> fintrolinfos,
			Fuser fuser,double amt,int i,String userId,String name,String vid) {
		if(i <= 3){
			if(fuser.getfIntroUser_id() != null){
				double total = 0d;
				if(i == 1){
					total = Utils.getDouble(amt*rate1, 4);
				}
				else if(i == 2){
					total = Utils.getDouble(amt*rate2, 4);
				}
				else if(i == 3){
					total = Utils.getDouble(amt*rate3, 4);
				}
				String introlUserId = fuser.getfIntroUser_id().getFid();
				Fuser intro = this.frontUserService.findById(introlUserId) ;
				Fwallet fwallet = intro.getFwallet();
				if(total >= 0.0001){
					fwallet.setFtotalRmb(fwallet.getFtotalRmb()+total);
					fwallets.add(fwallet);
					
					Fintrolinfo fintrolinfo = new Fintrolinfo() ;
					fintrolinfo.setFcreatetime(Utils.getTimestamp()) ;
					fintrolinfo.setFqty(total) ;
					//fintrolinfo.setFtitle("用户UID："+fuser.getFid()+"卖出"+name+",奖励￥:"+new BigDecimal(total).setScale(4,BigDecimal.ROUND_HALF_UP)+"");
					fintrolinfo.setFtitle("用户："+fuser.getFloginName()+"卖出"+name+",奖励￥:"+new BigDecimal(total).setScale(4,BigDecimal.ROUND_HALF_UP)+"");
					fintrolinfo.setFuser(intro) ;
					fintrolinfo.setFiscny(true);
					fintrolinfos.add(fintrolinfo);
				}
				i = i + 1;
				getAmtList(fwallets, fintrolinfos, intro, amt,i,userId,name,vid);
			}
		}
	}
	
	private void getAmtLists(Fsubscription fsubscription, Fvirtualcointype findByParam, List<Fvirtualwallet> fvirtualwallets,List<Fintrolinfo> fintrolinfos,
			Fuser fuser,double amt,int i,String userId,String name,String vid) {
		if(i <= 3){
			if(fuser.getfIntroUser_id() != null){
				double total = 0d;
				if(i == 1){
					total = Utils.getDouble(amt*rate1, 4);
				}
				else if(i == 2){
					total = Utils.getDouble(amt*rate2, 4);
				}
				else if(i == 3){
					total = Utils.getDouble(amt*rate3, 4);
				}
				String introlUserId = fuser.getfIntroUser_id().getFid();
				Fuser intro = this.frontUserService.findById(introlUserId) ;
				Fvirtualwallet fvirtualwallet = this.frontUserService.findVirtualWalletByUser(intro.getFid(), findByParam.getFid()) ;
				if(total >= 0.0001){
					fvirtualwallet.setFtotal(fvirtualwallet.getFtotal()+total*fsubscription.getFprice());
					fvirtualwallets.add(fvirtualwallet);
					Fintrolinfo fintrolinfo = new Fintrolinfo() ;
					fintrolinfo.setFcreatetime(Utils.getTimestamp()) ;
					fintrolinfo.setFqty(total*fsubscription.getFprice()) ;
					fintrolinfo.setFtitle("用户："+fuser.getFloginName()+"卖出"+name+",奖励"+findByParam.getfSymbol()+":"+new BigDecimal(total*fsubscription.getFprice()).setScale(4,BigDecimal.ROUND_HALF_UP)+"");
					fintrolinfo.setFuser(intro) ;
					fintrolinfo.setFiscny(true);
					fintrolinfos.add(fintrolinfo);
				}
				i = i + 1;
				getAmtLists(fsubscription, findByParam, fvirtualwallets, fintrolinfos, intro, amt,i,userId,name,vid);
			}
		}
	}
	
	private void getCoinList(List<Fvirtualwallet> fvirtualwallets,List<Fintrolinfo> fintrolinfos,
			Fuser fuser,double amt,int i,String userId,String name,String vid) {
		if(i <= 3){
			if(fuser.getfIntroUser_id() != null){
				double total = 0d;
				if(i == 1){
					total = Utils.getDouble(amt*rate1, 4);
				}
				else if(i == 2){
					total = Utils.getDouble(amt*rate2, 4);
				}
				else if(i == 3){
					total = Utils.getDouble(amt*rate3, 4);
				}
				//查询邀请码（邀请用户id）
				String introlUserId = fuser.getfIntroUser_id().getFid();
				Fuser intro = this.frontUserService.findById(introlUserId) ;
				Fvirtualwallet fvirtualwallet = this.frontUserService.findVirtualWalletByUser(intro.getFid(), vid);
				if(total >= 0.0001){
					fvirtualwallet.setFtotal(fvirtualwallet.getFtotal()+total);
					fvirtualwallets.add(fvirtualwallet);
					
					Fintrolinfo fintrolinfo = new Fintrolinfo() ;
					fintrolinfo.setFcreatetime(Utils.getTimestamp()) ;
					fintrolinfo.setFqty(total) ;
					//fintrolinfo.setFtitle("用户UID："+fuser.getFid()+"买入"+name+",奖励:"+name+" "+new BigDecimal(total).setScale(4,BigDecimal.ROUND_HALF_UP)+"个");
					fintrolinfo.setFtitle("用户："+fuser.getFloginName()+"买入"+name+",奖励:"+name+" "+new BigDecimal(total).setScale(4,BigDecimal.ROUND_HALF_UP)+"个");
					fintrolinfo.setFuser(intro) ;
					fintrolinfo.setFiscny(true);
					fintrolinfos.add(fintrolinfo);
				}
				i = i + 1;
				getCoinList(fvirtualwallets, fintrolinfos, intro, amt,i,userId,name,vid);
			}
		}
	}

}
