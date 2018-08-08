package com.ruizton.main.controller.front;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ruizton.main.auto.KlinePeriodData;
import com.ruizton.main.auto.LatestKlinePeroid;
import com.ruizton.main.auto.OneDayData;
import com.ruizton.main.auto.RealTimeData;
import com.ruizton.main.controller.BaseController;
import com.ruizton.main.model.Fentrust;
import com.ruizton.main.model.Fentrustlog;
import com.ruizton.main.model.Fperiod;
import com.ruizton.main.model.Fvirtualcointype;
import com.ruizton.main.service.front.FrontTradeService;
import com.ruizton.main.service.front.FrontVirtualCoinService;
import com.ruizton.util.Constant;
import com.ruizton.util.Utils;

@Controller
public class FrontMarketJsonController extends BaseController {

	@Autowired
	private RealTimeData realTimeData ;
	@Autowired
	private FrontVirtualCoinService frontVirtualCoinService ;
	@Autowired
	private KlinePeriodData klinePeriodData ;
	@Autowired
	private FrontTradeService frontTradeService ;
	@Autowired
	private OneDayData oneDayData;
	@Autowired
	private LatestKlinePeroid latestKlinePeroid ;
	
	@ResponseBody
	@RequestMapping(value="/real/market",produces={JsonEncode})
	public String marketRefresh(
			HttpServletRequest request,
			@RequestParam(required=false,defaultValue="")String symbol
			) throws Exception{
		JSONObject jsonObject = new JSONObject() ;
		
		Fvirtualcointype fvirtualcointype = null ;
		if(symbol==""){
			fvirtualcointype = this.frontVirtualCoinService.findFirstFirtualCoin(0) ;
			symbol = fvirtualcointype.getFid() ;
		}
		else{
			fvirtualcointype = this.frontVirtualCoinService.findFvirtualCoinById(symbol) ;
		}
		jsonObject.accumulate("p_new", Utils.getDouble(this.realTimeData.getLatestDealPrize(fvirtualcointype.getFid()), fvirtualcointype.getFcount()));
		jsonObject.accumulate("high", Utils.getDouble(this.oneDayData.getHighest(fvirtualcointype.getFid()), fvirtualcointype.getFcount()));
		jsonObject.accumulate("low", Utils.getDouble(this.oneDayData.getLowest(fvirtualcointype.getFid()), fvirtualcointype.getFcount()));
		jsonObject.accumulate("vol", Utils.getDouble(this.oneDayData.getTotal(fvirtualcointype.getFid()), fvirtualcointype.getFcount()));
		jsonObject.accumulate("buy1", Utils.getDouble(this.realTimeData.getHighestBuyPrize(fvirtualcointype.getFid()), fvirtualcointype.getFcount()));
		jsonObject.accumulate("sell1", Utils.getDouble(this.realTimeData.getLowestSellPrize(fvirtualcointype.getFid()), fvirtualcointype.getFcount()));
		
		
		Object[] buyEntrusts = null ;
		Object[] sellEntrusts = null ;
//		Object[] successEntrusts = this.realTimeData.getEntrustSuccessMap(fvirtualcointype.getFid()).toArray() ;
		if(Constant.CombinedDepth){
			buyEntrusts = this.realTimeData.getBuyDepthMap(fvirtualcointype.getFid()).toArray() ;
			sellEntrusts = this.realTimeData.getSellDepthMap(fvirtualcointype.getFid()).toArray() ;
		}else{
			buyEntrusts = this.realTimeData.getEntrustBuyMap(fvirtualcointype.getFid()).toArray() ;
			sellEntrusts = this.realTimeData.getEntrustSellMap(fvirtualcointype.getFid()).toArray() ;
		}
		
		
		JSONArray sellDepthList = new JSONArray() ;
		for (int i = 0; i < sellEntrusts.length && i<=21; i++) {
			Fentrust fentrust = (Fentrust) sellEntrusts[i] ;
			JSONObject js1 = new JSONObject();
			js1.accumulate("id",i+1) ;
			js1.accumulate("price",fentrust.getFprize()) ;
			js1.accumulate("amount",fentrust.getFleftCount()) ;
			sellDepthList.add(js1);
		}
		jsonObject.accumulate("sells", sellDepthList);
		
		
		JSONArray buyDepthList = new JSONArray() ;
		for (int i = 0; i < buyEntrusts.length && i<=21; i++) {
			JSONObject js1 = new JSONObject();
			Fentrust fentrust = (Fentrust) buyEntrusts[i] ;
			js1.accumulate("id",i+1) ;
			js1.accumulate("price",fentrust.getFprize()) ;
			js1.accumulate("amount",fentrust.getFleftCount()) ;
			
			buyDepthList.add(js1);
		}
		jsonObject.accumulate("buys", buyDepthList);
		
//		JSONArray recentDealList = new JSONArray() ;
//		for (int i = 0; i < successEntrusts.length && i<=21; i++) {
//			JSONObject js1 = new JSONObject();
//			Fentrustlog fentrust = (Fentrustlog) successEntrusts[i] ;
//			
//			js1.accumulate("id",i+1) ;
//			js1.accumulate("time",String.valueOf(new SimpleDateFormat("HH:mm:ss").format(fentrust.getFcreateTime()))) ;
//			js1.accumulate("price",String.valueOf(fentrust.getFprize())) ;
//			js1.accumulate("amount",String.valueOf(fentrust.getFcount())) ;
//			String en_type="";
//			String cn_type = "";
//			if(fentrust.getfEntrustType() ==0){
//				en_type="bid";
//				cn_type="买入";
//			}else{
//				en_type="ask";
//				cn_type="卖出";
//			}
//			js1.accumulate("en_type",en_type) ;
//			js1.accumulate("type",cn_type) ;
//			recentDealList.add(js1);
//		}
//		jsonObject.accumulate("trades", recentDealList);
		
		return jsonObject.toString() ;
	}
	
	@ResponseBody
	@RequestMapping("/kline/fullperiod")
	public String period(
			@RequestParam(required=true)int step,
			@RequestParam(required=true)String symbol
			) throws Exception{
		
		int key = 0 ;
		switch (step) {
		case 60:
			key = 0 ;
			break;
		case 60*3:
			key = 1 ;
			break;
		case 60*5:
			key = 2 ;
			break;
		case 60*15:
			key = 3 ;
			break;
		case 60*30:
			key = 4 ;
			break;
		case 60*60:
			key = 5 ;
			break;
		case 60*60*2:
			key = 6 ;
			break;
		case 60*60*4:
			key = 7 ;
			break;
		case 60*60*6:
			key = 8 ;
			break;
		case 60*60*12:
			key = 9 ;
			break;
		case 60*60*24:
			key = 10 ;
			break;
		case 60*60*24*3:
			key = 11 ;
			break;
		case 60*60*24*7:
			key = 12 ;
			break;
		}
		
		
		try {
			String jsonstr = this.klinePeriodData.getJsonString(symbol, key) ;
			StringBuffer ret = new StringBuffer(this.klinePeriodData.getJsonString(symbol, key)) ;
			
			long lastUpdateTime = this.klinePeriodData.lastUpdateTime.getTime() ;
			long now = this.latestKlinePeroid.parseTime(Utils.getTimestamp()).getTime() ;
			List<Fperiod> arr = new ArrayList<Fperiod>() ;
			
			while((lastUpdateTime = lastUpdateTime+60*1000L)<=now){
				Fperiod fperiod = this.latestKlinePeroid.get(this.latestKlinePeroid.parseKey(symbol, new Timestamp(lastUpdateTime))) ;
				if(fperiod!=null){
					arr.add(fperiod) ;
				}
			}
			if(arr.size() == 0 ){
				return jsonstr ;
			}
			
			if(step == 60){
				//分，直接加入
				StringBuffer sb = new StringBuffer() ;
				for (int i = 0; i < arr.size(); i++) {
					Fperiod fperiod =arr.get(i) ;
					sb.append(",["+(fperiod.getFtime().getTime())
							+","+new BigDecimal(fperiod.getFkai()).setScale(6, BigDecimal.ROUND_HALF_UP)
							+","+new BigDecimal(fperiod.getFgao()).setScale(6, BigDecimal.ROUND_HALF_UP)
							+","+new BigDecimal(fperiod.getFdi()).setScale(6, BigDecimal.ROUND_HALF_UP)
							+","+new BigDecimal(fperiod.getFshou()).setScale(6, BigDecimal.ROUND_HALF_UP)
							+","+new BigDecimal(fperiod.getFliang()).setScale(4, BigDecimal.ROUND_HALF_UP)+"]") ;
				}
				
				if(sb.length()>0){
					return ret.substring(0, ret.length()-1)+sb.toString()+"]" ;
				}
				return ret.toString() ;
				
			}else{
				Fperiod newFperiod = null ;
				for (int i = 0; i < arr.size(); i++) {
					Fperiod fperiod =arr.get(i) ;
					if(newFperiod == null ){
						newFperiod = fperiod ;
					}else{
						newFperiod.setFliang(newFperiod.getFliang()+fperiod.getFliang()) ;
						newFperiod.setFgao(newFperiod.getFgao()<fperiod.getFgao()?fperiod.getFgao():newFperiod.getFgao()) ;
						newFperiod.setFdi(newFperiod.getFdi()>fperiod.getFdi()?fperiod.getFdi():newFperiod.getFdi()) ;
						newFperiod.setFshou(fperiod.getFshou()) ;
 					}
				}
				
				
				JSONArray jsonArray = JSONArray.fromObject(jsonstr) ;
				JSONArray item = jsonArray.getJSONArray(jsonArray.size()-1) ;
				
			/*	+","+new BigDecimal(fperiod.getFkai()).setScale(6, BigDecimal.ROUND_HALF_UP)
				+","+new BigDecimal(fperiod.getFgao()).setScale(6, BigDecimal.ROUND_HALF_UP)
				+","+new BigDecimal(fperiod.getFdi()).setScale(6, BigDecimal.ROUND_HALF_UP)
				+","+new BigDecimal(fperiod.getFshou()).setScale(6, BigDecimal.ROUND_HALF_UP)
				+","+new BigDecimal(fperiod.getFliang()).setScale(4, BigDecimal.ROUND_HALF_UP)+"]") ;*/
				
				long newTime = newFperiod.getFtime().getTime() ;
				long oldTime = item.getLong(0) ;
				long createTime = oldTime+step*1000L ;
				if(newTime<createTime){
					//旧的
					jsonArray.remove(jsonArray.size()-1) ;
					
					JSONArray oldItem = new JSONArray() ;
					oldItem.add(oldTime) ;
					oldItem.add(item.getDouble(1)) ;
					oldItem.add(item.getDouble(2)<newFperiod.getFgao()?newFperiod.getFgao():item.getDouble(2)) ;
					oldItem.add(item.getDouble(3)>newFperiod.getFdi()?newFperiod.getFdi():item.getDouble(3)) ;
					oldItem.add(newFperiod.getFshou()) ;
					oldItem.add(item.getDouble(5)+newFperiod.getFliang()) ;
					jsonArray.add(oldItem) ;
					return jsonArray.toString() ;
				}else{
					//创建新的
					JSONArray oldItem = new JSONArray() ;
					oldItem.add(createTime) ;
					oldItem.add(item.getDouble(1)) ;
					oldItem.add(newFperiod.getFgao()) ;
					oldItem.add(newFperiod.getFdi()) ;
					oldItem.add(newFperiod.getFshou()) ;
					oldItem.add(newFperiod.getFliang()) ;
					jsonArray.add(oldItem) ;
					return jsonArray.toString() ;
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		}
		return null ;
	}
	
	@ResponseBody
	@RequestMapping("/kline/fulldepth")
	public String depth(
			@RequestParam(required=true)String symbol,
			@RequestParam(required=true)int step
			) throws Exception{
		JSONObject jsonObject = new JSONObject() ;
		
		TreeSet<Fentrust> buy = this.realTimeData.getEntrustBuyMap(symbol) ;
		TreeSet<Fentrust> sell = this.realTimeData.getEntrustSellMap(symbol) ;
		if(Constant.CombinedDepth){
			buy = this.realTimeData.getBuyDepthMap(symbol);
			sell = this.realTimeData.getSellDepthMap(symbol);
		}else{
			buy = this.realTimeData.getEntrustBuyMap(symbol) ;
			sell = this.realTimeData.getEntrustSellMap(symbol);
		}
		
		List<List<BigDecimal>> buyList = new ArrayList<List<BigDecimal>>() ;
		List<List<BigDecimal>> sellList = new ArrayList<List<BigDecimal>>() ;
		for (Fentrust fentrust:buy) {
			List<BigDecimal> list = new ArrayList<BigDecimal>() ;
			list.add(new BigDecimal(fentrust.getFprize()).setScale(6, BigDecimal.ROUND_DOWN)) ;
			list.add(new BigDecimal(fentrust.getFleftCount()).setScale(4, BigDecimal.ROUND_DOWN)) ;
			buyList.add(list) ;
		}
		for (Fentrust fentrust:sell) {
			List<BigDecimal> list = new ArrayList<BigDecimal>() ;
			list.add(new BigDecimal(fentrust.getFprize()).setScale(6, BigDecimal.ROUND_DOWN)) ;
			list.add(new BigDecimal(fentrust.getFleftCount()).setScale(4, BigDecimal.ROUND_DOWN)) ;
			sellList.add(list) ;
		}
		
		JSONObject askBidJson = new JSONObject() ;
		askBidJson.accumulate("bids", buyList) ;
		askBidJson.accumulate("asks", sellList) ;
		askBidJson.accumulate("date", Utils.getTimestamp().getTime()/1000L) ;
		jsonObject.accumulate("depth", askBidJson) ;
		
		
		JSONObject periodJson = new JSONObject() ;
		periodJson.accumulate("marketFrom", symbol);
		periodJson.accumulate("coinVol", symbol);
		periodJson.accumulate("type", step);
		
		String data = this.period(step, symbol) ;
		periodJson.accumulate("data", data);
		
		jsonObject.accumulate("period", periodJson) ;		
		
		return jsonObject.toString() ;
	}
	

	/*public JSONArray period2(
			int step,
			int symbol
			) throws Exception{
		int key = 0 ;
		switch (step) {
		case 60:
			key = 0 ;
			break;
		case 60*3:
			key = 1 ;
			break;
		case 60*5:
			key = 2 ;
			break;
		case 60*15:
			key = 3 ;
			break;
		case 60*30:
			key = 4 ;
			break;
		case 60*60:
			key = 5 ;
			break;
		case 60*60*2:
			key = 6 ;
			break;
		case 60*60*4:
			key = 7 ;
			break;
		case 60*60*6:
			key = 8 ;
			break;
		case 60*60*12:
			key = 9 ;
			break;
		case 60*60*24:
			key = 10 ;
			break;
		case 60*60*24*3:
			key = 11 ;
			break;
		case 60*60*24*7:
			key = 12 ;
			break;
		}

		String content = this.klinePeriodData.getJsonString(symbol, key) ;
		JSONArray jsonArray = JSONArray.fromObject(content) ;
		if(jsonArray == null || jsonArray.size() ==0) return new JSONArray();
		
		
		try {
			JSONArray item = jsonArray.getJSONArray(jsonArray.size()-1) ;
			JSONArray retItem = new JSONArray() ;
			
			JSONArray addItem = new JSONArray() ;
			addItem.add(Long.parseLong(item.getString(0))) ;
			addItem.add(new BigDecimal(item.getString(1)).setScale(6, BigDecimal.ROUND_DOWN)) ;
			addItem.add(new BigDecimal(item.getString(2)).setScale(6, BigDecimal.ROUND_DOWN)) ;
			addItem.add(new BigDecimal(item.getString(3)).setScale(6, BigDecimal.ROUND_DOWN)) ;
			addItem.add(new BigDecimal(item.getString(4)).setScale(6, BigDecimal.ROUND_DOWN)) ;
			addItem.add(new BigDecimal(item.getString(5)).setScale(4, BigDecimal.ROUND_DOWN)) ;
			
			retItem.add(addItem) ;
			return retItem ;
		} catch (Exception e) {}
		
		return new JSONArray() ;
	}*/
	
	
	@ResponseBody
	@RequestMapping("/kline/trade_json")
	public String trade_json(
			@RequestParam(required=true )String id
			) throws Exception {
		
		StringBuffer content = new StringBuffer() ;
		content.append(
				"chart_1h = {symbol:\"BTC_CNY\",symbol_view:\"CNY/CNY\",ask:1.2,time_line:"+this.klinePeriodData.getIndexJsonString(id, 5) +"};"
				) ;
		
		content.append(
				"chart_5m = {time_line:"+this.klinePeriodData.getIndexJsonString(id, 2) +"};" 
				) ;
		content.append(
				"chart_15m = {time_line:"+this.klinePeriodData.getIndexJsonString(id, 3) +"};"
				) ;
		content.append(
				"chart_30m = {time_line:"+this.klinePeriodData.getIndexJsonString(id, 4) +"};"
				) ;
		content.append(
				"chart_8h = {time_line:"+this.klinePeriodData.getIndexJsonString(id, 8) +"};"
				) ;
		content.append(
				"chart_1d = {time_line:"+this.klinePeriodData.getIndexJsonString(id, 10) +"};"
				) ;
		
		return content.toString() ;
	}
}
