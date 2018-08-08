package com.ruizton.main.controller.front;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.ruizton.main.auto.KlinePeriodData;
import com.ruizton.main.auto.RealTimeData;
import com.ruizton.main.controller.BaseController;
import com.ruizton.main.model.Farticle;
import com.ruizton.main.model.Fentrust;
import com.ruizton.main.model.Fvirtualcointype;
import com.ruizton.main.service.admin.SystemArgsService;
import com.ruizton.main.service.front.FrontOthersService;
import com.ruizton.main.service.front.FrontVirtualCoinService;

@Controller
public class AppController extends BaseController {

	@Autowired
	private FrontOthersService frontOthersService ;
	@Autowired
	private SystemArgsService systemArgsService ;
	@Autowired
	private FrontVirtualCoinService frontVirtualCoinService ;
	@Autowired
	private RealTimeData realTimeData ;
	@Autowired
	private KlinePeriodData klinePeriodData ;
	
	@RequestMapping("/app/article")
	public ModelAndView article(
			@RequestParam(required=true )String id
			) throws Exception {
		ModelAndView modelAndView = new ModelAndView() ;
		String appNameNews = this.systemArgsService.getValue("appNameNews") ;
		modelAndView.addObject("appNameNews",appNameNews) ;
		
		Farticle farticle = this.frontOthersService.findFarticleById(id) ;
		modelAndView.addObject("farticle",farticle) ;
		modelAndView.setViewName("app/article") ;
		return modelAndView ;
	}
	/*@ResponseBody
	@RequestMapping(value="/real/appkline",produces=JsonEncode)
	public String appkline(
			@RequestParam(required=true )String fid
			) throws Exception {
		JSONObject js = new JSONObject();
		
		Fvirtualcointype fvirtualcointype = this.frontVirtualCoinService.findFvirtualCoinById(fid) ;
		
		js.accumulate("id", fvirtualcointype.getFid());
		js.accumulate("fshortname", fvirtualcointype.getfShortName());
			return "jsonpCallback("+js.toString()+")";
	}*/

	@RequestMapping("/real/appkline")
	public ModelAndView appkline(
			@RequestParam(required=true )String symbol
			) throws Exception {
		ModelAndView modelAndView = new ModelAndView() ;
		
		Fvirtualcointype fvirtualcointype = this.frontVirtualCoinService.findFvirtualCoinById(symbol) ;
		modelAndView.addObject("fvirtualcointype", fvirtualcointype) ;
		
		modelAndView.setViewName("app/kline/kline") ;
		return modelAndView ;
	}
	

	@ResponseBody
	@RequestMapping("/real/appdepth")
	public String appdepth(
			@RequestParam(required=true )String symbol
			) throws Exception {
		//Fvirtualcointype fvirtualcointype = this.frontVirtualCoinService.findFvirtualCoinById(symbol) ;
		JSONArray ret = new JSONArray() ;
		JSONArray buy = new JSONArray() ;
		JSONArray sell = new JSONArray() ;
		
		double val = this.realTimeData.getLatestDealPrize(symbol) ;
		double low = val*0.3D ;
		double high = val*3D ;
		
		Object[] buyArray = this.realTimeData.getBuyDepthMap(symbol).toArray() ;
		for (Object object : buyArray) {
			Fentrust fentrust = (Fentrust)object ;
			
			if(fentrust.getFprize()<low||fentrust.getFprize()>high){
				continue ;
			}
			
			JSONArray item = new JSONArray() ;
			item.add(fentrust.getFprize()) ;
			item.add(fentrust.getFleftCount()) ;
			item.add(0) ;
			
			buy.add(item) ;
		}
		
		Object[] sellArray = this.realTimeData.getSellDepthMap(symbol).toArray() ;
		for (Object object : sellArray) {
			Fentrust fentrust = (Fentrust)object ;
			
			if(fentrust.getFprize()<low||fentrust.getFprize()>high){
				continue ;
			}
			
			JSONArray item = new JSONArray() ;
			item.add(fentrust.getFprize()) ;
			item.add(fentrust.getFleftCount()) ;
			item.add(0) ;
			
			sell.add(item) ;
		}
		
		
		ret.add(buy) ;
		ret.add(sell) ;
		return ret.toString() ;
	}
	
	@ResponseBody
	@RequestMapping("/real/appperiod")
	public String appperiod(
			@RequestParam(required=true )String symbol,
			@RequestParam(required=true )String fShortName
			) throws Exception {
		//Fvirtualcointype fvirtualcointype = this.frontVirtualCoinService.findFvirtualCoinById(symbol) ;
		
		StringBuffer content = new StringBuffer(
				"chart = {" +
				" symbol : \""+fShortName+"_CNY\"," +
				" symbol_view : \""+fShortName+"/CNY\"," +
				" ask : 1.2," +
				" time_line :" ) ;

//		time,liang,open,high,low,close
		JSONObject timeLine = new JSONObject() ;
		String m5 = this.klinePeriodData.getJsonString(symbol, 2) ;
		String m15 = this.klinePeriodData.getJsonString(symbol, 3) ;
		String m30 = this.klinePeriodData.getJsonString(symbol, 4) ;
		String h1 = this.klinePeriodData.getJsonString(symbol, 5) ;
		String h8 = this.klinePeriodData.getJsonString(symbol, 8) ;
		String d1 = this.klinePeriodData.getJsonString(symbol, 10) ;
		timeLine.accumulate("5m", m5) ;
		timeLine.accumulate("15m", m15) ;
		timeLine.accumulate("30m", m30) ;
		timeLine.accumulate("1h", h1) ;
		timeLine.accumulate("8h", h8) ;
		timeLine.accumulate("1d", d1) ;
		
		content.append(timeLine.toString()) ;
		
		content.append("};") ;
		
		return content.toString() ;
	}
}
