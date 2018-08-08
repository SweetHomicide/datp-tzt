package com.ruizton.main.controller.front;

import java.util.List;
import java.util.Set;

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
import com.ruizton.main.model.Fvirtualcointype;
import com.ruizton.main.service.front.FrontVirtualCoinService;

@Controller
public class FrontQuotationsController extends BaseController {

	@Autowired
	private RealTimeData realTimeData ;
	@Autowired
	private FrontVirtualCoinService frontVirtualCoinService ;
	@Autowired
	private ConstantMap constantMap ;
	
	/*
	 * https://www.okcoin.com/real/handleEntrust.do
	 * ?symbol=0&tradetype=0&random=50&_=1392886088122
	 * */
	@RequestMapping("/real/handleEntrust")
	public ModelAndView handleEntrust(
			@RequestParam(required=true)String symbol
			) throws Exception{
		Fvirtualcointype fvirtualcointype = this.frontVirtualCoinService.findFvirtualCoinById(symbol) ;
		ModelAndView modelAndView = new ModelAndView() ;
		modelAndView.addAllObjects(this.setRealTimeData(symbol)) ;
		modelAndView.addObject("fvirtualcointype", fvirtualcointype) ;
		modelAndView.setViewName("front/real/handel_entrust") ;
		return modelAndView ;
	}
	
	@RequestMapping("/real/indexDepth")
	public ModelAndView indexDepth(
			@RequestParam(required=true)String symbol
			) throws Exception{
		ModelAndView modelAndView = new ModelAndView() ;
		Fvirtualcointype fvirtualcointype = this.frontVirtualCoinService.findFvirtualCoinById(symbol) ;
		
		List<Fvirtualcointype> fvirtualcointypes = (List<Fvirtualcointype>)this.constantMap.get("virtualCoinType") ;
		
		Set<Fentrust> buy = this.realTimeData.getEntrustBuyMap(symbol) ;
		Set<Fentrust> sell = this.realTimeData.getEntrustSellMap(symbol) ;
		Set<Fentrustlog> success = this.realTimeData.getEntrustSuccessMap(symbol) ;
		
		modelAndView.addObject("buy", buy) ;
		modelAndView.addObject("sell", sell) ;
		modelAndView.addObject("success", success) ;
		modelAndView.addObject("fvirtualcointypes", fvirtualcointypes) ;
		modelAndView.addObject("fvirtualcointype", fvirtualcointype) ;
		modelAndView.setViewName("front/real/index_depth") ;
		return modelAndView ;
	}
	
	@RequestMapping("/json")
	public ModelAndView json() throws Exception{
		ModelAndView modelAndView = new ModelAndView() ;
		modelAndView.setViewName("front/real/json") ;
		return modelAndView ;
	}
}
