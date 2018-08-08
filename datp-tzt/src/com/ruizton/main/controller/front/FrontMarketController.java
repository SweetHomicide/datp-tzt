package com.ruizton.main.controller.front;

import java.util.List;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.ruizton.main.Enum.VirtualCoinTypeStatusEnum;
import com.ruizton.main.auto.RealTimeData;
import com.ruizton.main.comm.ConstantMap;
import com.ruizton.main.controller.BaseController;
import com.ruizton.main.model.Fentrust;
import com.ruizton.main.model.Fentrustlog;
import com.ruizton.main.model.Fuser;
import com.ruizton.main.model.Fvirtualcointype;
import com.ruizton.main.service.front.FrontUserService;
import com.ruizton.main.service.front.FrontVirtualCoinService;
import com.ruizton.util.Constant;
import com.ruizton.util.Utils;

@Controller
public class FrontMarketController extends BaseController {

	@Autowired
	private RealTimeData realTimeData ;
	@Autowired
	private FrontVirtualCoinService frontVirtualCoinService ;
	@Autowired
	private FrontUserService frontUserService;
	@Autowired
	private ConstantMap constantMap;
	/*
	 * https://www.okcoin.com/market.do?symbol=0
	 * */
/*	@RequestMapping("/market")
	public ModelAndView market(
			HttpServletRequest request,
			@RequestParam(required=false,defaultValue="0")int symbol,
			@RequestParam(required=false,defaultValue="0")int isAbout
			) throws Exception{
		ModelAndView modelAndView = new ModelAndView() ;
		Fvirtualcointype fvirtualcointype = null ;
		if(isAbout !=0 && isAbout!=1){
			isAbout=0;
		}
		if(symbol==0){
			fvirtualcointype = this.frontVirtualCoinService.findFirstFirtualCoin() ;
		}else{
			fvirtualcointype = this.frontVirtualCoinService.findFvirtualCoinById(symbol) ;
		}
		
		if(fvirtualcointype == null || fvirtualcointype.getFstatus() != 1 || !fvirtualcointype.isFisShare()){
			modelAndView.setViewName("redirect:/") ;
			return modelAndView;
		}
		
		symbol = fvirtualcointype.getFid() ;
		List<Fvirtualcointype> fvirtualcointypes = (List<Fvirtualcointype>)this.constantMap.get("virtualCoinType") ;
		for (Fvirtualcointype fvirtualcointype2 : fvirtualcointypes) {
			fvirtualcointype2.setLastDealPrize(this.realTimeData.getLatestDealPrize(fvirtualcointype2.getFid()));
		}
		
		boolean isopen = false;
		boolean tradeType = false;
		boolean tradePassword = false;
		boolean isTelephoneBind = false;
		boolean login = false;
		String coinshortName = fvirtualcointype.getfShortName();
		if(GetSession(request) != null){
			login = true;
			Fuser fuser = this.frontUserService.findById(GetSession(request).getFid());
			if(fuser.isFisTelephoneBind()){
				isTelephoneBind = true;
			}else{
				isTelephoneBind = false;
			}
			
			if(fuser.getFtradePassword() != null && fuser.getFtradePassword().trim().length() >0){
				tradePassword = true;
			}else{
				tradePassword = false;
			}
		}
		modelAndView.addObject("coinshortName", coinshortName) ;
		modelAndView.addObject("isopen", isopen) ;
		modelAndView.addObject("login", login) ;
		modelAndView.addObject("tradeType", tradeType) ;
		modelAndView.addObject("tradePassword", tradePassword) ;
		modelAndView.addObject("isTelephoneBind", isTelephoneBind) ;
		
//		
//		TreeSet<Fentrust> buyEntrusts = null ;
//		TreeSet<Fentrust> sellEntrusts = null ;
//		TreeSet<Fentrustlog> successEntrusts = null ;
//		if(Constant.CombinedDepth){
//			buyEntrusts = this.realTimeData.getBuyDepthMap(fvirtualcointype.getFid()) ;
//			sellEntrusts = this.realTimeData.getSellDepthMap(fvirtualcointype.getFid()) ;
//		}else{
//			buyEntrusts = this.realTimeData.getEntrustBuyMap(fvirtualcointype.getFid()) ;
//			sellEntrusts = this.realTimeData.getEntrustSellMap(fvirtualcointype.getFid()) ;
//		}
//		successEntrusts = this.realTimeData.getEntrustSuccessMap(fvirtualcointype.getFid()) ;
//		
//		modelAndView.addAllObjects(super.setRealTimeData(fvirtualcointype.getFid())) ;
//		
//		modelAndView.addObject("recommendPrizebuy", Utils.getDouble(this.realTimeData.getHighestBuyPrize(symbol), fvirtualcointype.getFcount())) ;
//		modelAndView.addObject("recommendPrizesell", Utils.getDouble(this.realTimeData.getLowestSellPrize(symbol), fvirtualcointype.getFcount())) ;
//		modelAndView.addObject("latestDealPrize", Utils.getDouble(this.realTimeData.getLatestDealPrize(symbol), fvirtualcointype.getFcount())) ;
//		modelAndView.addObject("buyEntrusts",buyEntrusts ) ;
//		modelAndView.addObject("sellEntrusts",sellEntrusts) ;
//		modelAndView.addObject("successEntrusts",successEntrusts) ;
		

		modelAndView.addObject("fvirtualcointypes", fvirtualcointypes) ;
		modelAndView.addObject("fvirtualcointype", fvirtualcointype) ;
		modelAndView.addObject("symbol", symbol) ;
		modelAndView.setViewName("front/market/market") ;
		return modelAndView ;
	}*/
	
	@RequestMapping("/kline/fullstart")
	public ModelAndView start(
			@RequestParam(required=false,defaultValue="")String symbol
			) throws Exception{
		ModelAndView modelAndView = new ModelAndView() ;
		Fvirtualcointype fvirtualcointype = this.frontVirtualCoinService.findFvirtualCoinById(symbol) ;
		if(fvirtualcointype == null || fvirtualcointype.getFstatus() != 1 || !fvirtualcointype.isFisShare()){
			modelAndView.setViewName("redirect:/") ;
			return modelAndView;
		}
		
		List<Fvirtualcointype> allcoins = (List)this.constantMap.get("virtualCoinType");
		modelAndView.addObject("allcoins", allcoins) ;
		modelAndView.addObject("fvirtualcointype", fvirtualcointype) ;
		modelAndView.setViewName("front/market/start") ;
		return modelAndView ;
	}
//	
//	@RequestMapping("/kline/appstart")
//	public ModelAndView appStart(
//			@RequestParam(required=false,defaultValue="0")int symbol
//			) throws Exception{
//		ModelAndView modelAndView = new ModelAndView() ;
//		Fvirtualcointype fvirtualcointype = this.frontVirtualCoinService.findFvirtualCoinById(symbol) ;
//		
//		modelAndView.addObject("fvirtualcointype", fvirtualcointype) ;
//		modelAndView.setViewName("front/market/appStart") ;
//		return modelAndView ;
//	}
//	
	@RequestMapping("/kline/trade")
	public ModelAndView trade(
			@RequestParam(required=true )String id
			) throws Exception {
		ModelAndView modelAndView = new ModelAndView() ;
		
		Fvirtualcointype fvirtualcointype = this.frontVirtualCoinService.findFvirtualCoinById(id) ;
		modelAndView.addObject("fvirtualcointype", fvirtualcointype) ;
		
		modelAndView.setViewName("front/market/kline") ;
		return modelAndView ;
	}
	
}
