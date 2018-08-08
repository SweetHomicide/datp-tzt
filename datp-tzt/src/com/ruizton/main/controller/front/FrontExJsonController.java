package com.ruizton.main.controller.front;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ruizton.main.Enum.CountLimitTypeEnum;
import com.ruizton.main.Enum.SubStatusEnum;
import com.ruizton.main.Enum.SubscriptionTypeEnum;
import com.ruizton.main.controller.BaseController;
import com.ruizton.main.model.Fsubscription;
import com.ruizton.main.model.Fsubscriptionlog;
import com.ruizton.main.model.Fuser;
import com.ruizton.main.model.Fvirtualwallet;
import com.ruizton.main.service.front.FrontTradeService;
import com.ruizton.main.service.front.FrontUserService;
import com.ruizton.main.service.front.FrontValidateService;
import com.ruizton.util.Constant;
import com.ruizton.util.Utils;

@Controller
public class FrontExJsonController extends BaseController{

	@Autowired
	private FrontUserService frontUserService ;
	@Autowired
	private FrontTradeService frontTradeService ;
	@Autowired
	private FrontValidateService frontValidateService ;
	
	
	@ResponseBody
	@RequestMapping(value="/json/ex/submitEx",produces={JsonEncode})
	public String submitEx(
			HttpServletRequest request,
			@RequestParam(required=true) String fid,
			@RequestParam(required=true) double buyAmount,
			@RequestParam(required=true,defaultValue="0") String tradePwd
			) throws Exception{
		JSONObject js = new JSONObject();
		Fuser fuser = this.frontUserService.findById(GetSession(request).getFid()) ;
		Fsubscription fsubscription = this.frontTradeService.findFsubscriptionById(fid) ;
		if(fsubscription==null){
			fsubscription = this.frontTradeService.findFirstSubscription(SubscriptionTypeEnum.COIN) ;
			if(fsubscription==null){
				js.accumulate("code", -1);
				js.accumulate("msg", "系统异常");
				return js.toString();
			}
		}
		if(buyAmount<=0){
			js.accumulate("code", -1);
			js.accumulate("msg", "兑换数量异常");
			return js.toString();
		}
		
		if(fsubscription.getFtotal() - fsubscription.getfAlreadyByCount()-buyAmount < 0d){
			js.accumulate("code", -1);
			js.accumulate("msg", "兑换池余额不足");
			return js.toString();
		}
		
		if(fuser.getFtradePassword() == null){
			js.accumulate("code", -1);
			js.accumulate("msg", "请先设置交易密码");
			return js.toString();
		}
		
		String ip = Utils.getIp(request) ;
		if(fuser.getFtradePassword()!=null){
			int trade_limit = this.frontValidateService.getLimitCount(ip, CountLimitTypeEnum.TRADE_PASSWORD) ;
			if(trade_limit<=0){
				js.accumulate("code", -1) ;
				js.accumulate("msg","交易密码有误，请稍候再试") ;
				return js.toString() ;
			}else{
				boolean flag = fuser.getFtradePassword().equals(Utils.MD5(tradePwd)) ;
				if(!flag){
					js.accumulate("code", -1) ;
					js.accumulate("msg","交易密码有误，您还有"+(trade_limit-1)+"次机会") ;
					this.frontValidateService.updateLimitCount(ip, CountLimitTypeEnum.TRADE_PASSWORD) ;
					return js.toString() ;
				}else if(trade_limit<Constant.ErrorCountLimit){
					this.frontValidateService.deleteCountLimite(ip, CountLimitTypeEnum.TRADE_PASSWORD) ;
				}
			}
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
			js.accumulate("code", -1);
			js.accumulate("msg", "兑换未开始");
			return js.toString();
		}else if(begin==2){
			js.accumulate("code", -1);
			js.accumulate("msg", "兑换已结束");
			return js.toString();
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
		
		if(fsubscription.getFbuyCount()!=0&& buyAmount>buyCount){
			js.accumulate("code", -1);
			js.accumulate("msg", "您超出可兑换的最大数量");
			return js.toString();
		}
		
		if(fsubscription.getFbuyTimes()!=0 && buyTimes==0){
			js.accumulate("code", -1);
			js.accumulate("msg", "您超出可兑换的最大次数");
			return js.toString();
		}
		
		Double cost = buyAmount * fsubscription.getFprice() ;
		//可以购买了
		Fvirtualwallet fvirtualwalletCost = this.frontUserService.findVirtualWalletByUser(fuser.getFid(), fsubscription.getFvirtualcointypeCost().getFid()) ;
		Fvirtualwallet fvirtualwallet = this.frontUserService.findVirtualWalletByUser(fuser.getFid(), fsubscription.getFvirtualcointype().getFid()) ;
		if(fvirtualwalletCost.getFtotal()<cost){
			js.accumulate("code", -1);
			js.accumulate("msg", "余额不足");
			return js.toString();
		}
		
		fvirtualwalletCost.setFtotal(fvirtualwalletCost.getFtotal()-cost) ;
		fvirtualwallet.setFtotal(fvirtualwallet.getFtotal()+buyAmount) ;
		Fsubscriptionlog fsubscriptionlog = new Fsubscriptionlog() ;
		fsubscriptionlog.setFcount(buyAmount+0.0) ;
		fsubscriptionlog.setFcreatetime(Utils.getTimestamp()) ;
		fsubscriptionlog.setFprice(fsubscription.getFprice()) ;
		fsubscriptionlog.setFsubscription(fsubscription) ;
		fsubscriptionlog.setFtotalCost(cost) ;
		fsubscriptionlog.setFischarge("true");
		fsubscriptionlog.setFuser(fuser) ;
		fsubscriptionlog.setFissend(true);
		fsubscriptionlog.setFstatus(SubStatusEnum.YES);
		fsubscription.setfAlreadyByCount(fsubscription.getfAlreadyByCount()+buyAmount) ;
		try {
			this.frontTradeService.updateSubscription(fvirtualwalletCost, fvirtualwallet, fsubscriptionlog,fsubscription) ;
		} catch (Exception e) {
			js.accumulate("code", -1);
			js.accumulate("msg", "网络异常");
			return js.toString();
		}
		js.accumulate("code", 0);
		js.accumulate("msg", "兑换成功");
		return js.toString();
	}
	
}
