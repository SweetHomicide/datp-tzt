package com.ruizton.main.controller.front;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ruizton.main.Enum.SubStatusEnum;
import com.ruizton.main.Enum.SubscriptionTypeEnum;
import com.ruizton.main.comm.ConstantMap;
import com.ruizton.main.controller.BaseController;
import com.ruizton.main.model.Fintrolinfo;
import com.ruizton.main.model.Fsubscription;
import com.ruizton.main.model.Fsubscriptionlog;
import com.ruizton.main.model.Fuser;
import com.ruizton.main.model.Fvirtualwallet;
import com.ruizton.main.model.Fwallet;
import com.ruizton.main.service.front.FrontTradeService;
import com.ruizton.main.service.front.FrontUserService;
import com.ruizton.util.Utils;

@Controller
public class FrontZhongchouJsonController extends BaseController{
	@Autowired
	private FrontUserService frontUserService;
	@Autowired
	private FrontTradeService frontTradeService;
	@Autowired
	private ConstantMap map;
	
	@ResponseBody
	@RequestMapping(value="json/crowd/submit",produces={JsonEncode})
	public String crowd(
			HttpServletRequest request,
			@RequestParam(required=true)String fid,
			@RequestParam(required=true)int buyAmount,
			@RequestParam(required=true)String pwd
			) throws Exception{
		JSONObject jsonObject = new JSONObject() ;
//		
//		try{
//			Thread.sleep(1000);
//		}catch (Exception e) {}
//		
		Fuser fuser = this.frontUserService.findById(GetSession(request).getFid()) ;
		Fsubscription fsubscription = this.frontTradeService.findFsubscriptionById(fid) ;
		if(fsubscription==null || fsubscription.getFtype() !=SubscriptionTypeEnum.RMB || buyAmount<=0){
			jsonObject.accumulate("code", -1);
			jsonObject.accumulate("msg", "操作不合法");
			return jsonObject.toString();
		}
		String type="份";
		String type1="份数";
		if(fsubscription.isFisICO()){
			type = fsubscription.getSymbol();
			type1="金额";
		}
		
		if(fuser.getFtradePassword() == null || fuser.getFtradePassword().trim().length() ==0){
			jsonObject.accumulate("code", -1);
			jsonObject.accumulate("msg", "请先设置交易密码");
			return jsonObject.toString();
		}
		
		if(!fuser.getFtradePassword().equals(Utils.MD5(pwd))){
			jsonObject.accumulate("code", -1);
			jsonObject.accumulate("msg", "交易密码不正确");
			return jsonObject.toString();
		}
		
		if(buyAmount <fsubscription.getFminbuyCount()){
			jsonObject.accumulate("code", -1);
			jsonObject.accumulate("msg", "最少众筹"+fsubscription.getFminbuyCount()+type);
			return jsonObject.toString();
		}
		
		int begin = 0 ;
		long now = Utils.getTimestamp().getTime() ;
		if(fsubscription.getFbeginTime().getTime()>now){
			//没开始
			begin = 0 ;
		}
		
		if(fsubscription.getFbeginTime().getTime()<now && fsubscription.getFendTime().getTime()>now){
			//进行中
			begin = 1 ;
		}
		
		if(fsubscription.getFendTime().getTime()<now){
			//结束
			begin = 2 ;
		}
		
		if(begin==0){
			jsonObject.accumulate("code", -1);
			jsonObject.accumulate("msg", "众筹未开始");
			return jsonObject.toString();
		}else if(begin==2){
			jsonObject.accumulate("code", -1);
			jsonObject.accumulate("msg", "众筹已结束");
			return jsonObject.toString();
		}
		
		if( !fsubscription.getFisopen()){
			jsonObject.accumulate("code", -1);
			jsonObject.accumulate("msg", "众筹未对外开放");
			return jsonObject.toString();
		}
		
		//认购记录
		List<Fsubscriptionlog> fsubscriptionlogs = this.frontTradeService.findFsubScriptionLog(fuser, fsubscription.getFid()) ;
		//可购买数量
		int buyCount = fsubscription.getFbuyCount() ;
		if(fsubscriptionlogs.size() >0){
			for (int i = 0; i < fsubscriptionlogs.size(); i++) {
				buyCount -=fsubscriptionlogs.get(i).getFcount() ;
			}
		}

		buyCount = buyCount<0?0:buyCount ;
		//可购买次数
		int buyTimes = fsubscription.getFbuyTimes()-fsubscriptionlogs.size() ;
		buyTimes = buyTimes<0?0:buyTimes ;
		
		String subUserId = this.map.getString("subUserId");
		if(fuser.getFid() != subUserId){
			if(fsubscription.getFbuyCount()!=0&& buyCount<buyAmount){
				jsonObject.accumulate("code", -1);
				jsonObject.accumulate("msg", "您已超出可众筹的"+type1);
				return jsonObject.toString();
			}
			
			if(fsubscription.getFbuyTimes()!=0 && buyTimes==0){
				jsonObject.accumulate("code", -1);
				jsonObject.accumulate("msg", "您已超出可众筹的次数");
				return jsonObject.toString();
			}
		}
		
		Double cost = 0d;
		if(fsubscription.isFisICO()){
			cost = Double.valueOf(buyAmount);
		}else{
			cost = buyAmount * fsubscription.getFprice() ;
		}
		
		//可以购买了
		Fwallet fwallet1 = null;//this.frontUserService.findFwalletById(fuser.getFwallet().getFid()) ;
		Fvirtualwallet fvirtualwallet1 = null;
		Fvirtualwallet fvirtualwallet = this.frontUserService.findVirtualWalletByUser(fuser.getFid(), fsubscription.getFvirtualcointype().getFid()) ;
		if(fsubscription.getFvirtualcointypeCost() == null){
			fwallet1 = this.frontUserService.findFwalletById(fuser.getFwallet().getFid()) ;
			if(fwallet1.getFtotalRmb()<cost){
				jsonObject.accumulate("code", -1);
				jsonObject.accumulate("msg", "人民币余额不足");
				return jsonObject.toString();
			}
			fwallet1.setFtotalRmb(fwallet1.getFtotalRmb()-cost) ;
			fwallet1.setFfrozenRmb(fwallet1.getFfrozenRmb()+cost);
		}else{
			fvirtualwallet1 = this.frontUserService.findVirtualWalletByUser(fuser.getFid(), fsubscription.getFvirtualcointypeCost().getFid()) ;
			if(fvirtualwallet1.getFtotal() < cost){
				jsonObject.accumulate("code", -1);
				jsonObject.accumulate("msg", "您的"+fsubscription.getFvirtualcointypeCost().getFname()+"余额不足");
				return jsonObject.toString();
			}
			fvirtualwallet1.setFtotal(fvirtualwallet1.getFtotal()-cost);
			fvirtualwallet1.setFfrozen(fvirtualwallet1.getFfrozen()+cost);
		}

		Fsubscriptionlog fsubscriptionlog = new Fsubscriptionlog() ;
		fsubscriptionlog.setFcount(buyAmount+0.0) ;
		fsubscriptionlog.setFcreatetime(Utils.getTimestamp()) ;
		fsubscriptionlog.setFprice(fsubscription.getFprice()) ;
		fsubscriptionlog.setFsubscription(fsubscription) ;
		fsubscriptionlog.setFtotalCost(cost) ;
		fsubscriptionlog.setFlastcount(0d);
		fsubscriptionlog.setFstatus(SubStatusEnum.INIT);
		fsubscriptionlog.setFuser(fuser) ;
		fsubscriptionlog.setFissend(false);
		fsubscriptionlog.setFischarge(null);
		fsubscriptionlog.setFoneqty(fsubscription.getFtotalqty());
		fsubscriptionlog.setFlastqty(0d);
		fsubscription.setfAlreadyByCount(fsubscription.getfAlreadyByCount()+buyAmount) ;
		try {
			this.frontTradeService.updateSubscription(fwallet1,fvirtualwallet1, fvirtualwallet, fsubscriptionlog,
					fsubscription) ;
		} catch (Exception e) {
			jsonObject.accumulate("code", -1);
			jsonObject.accumulate("msg", "网络异常");
			return jsonObject.toString();
		}
		jsonObject.accumulate("code", 0);
		jsonObject.accumulate("msg", "操作成功");
		return jsonObject.toString();
	}
	
}
