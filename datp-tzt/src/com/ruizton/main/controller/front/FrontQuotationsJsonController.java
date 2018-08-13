package com.ruizton.main.controller.front;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.ruizton.main.auto.KlinePeriodData;
import com.ruizton.main.auto.OneDayData;
import com.ruizton.main.auto.RealTimeData;
import com.ruizton.main.comm.ConstantMap;
import com.ruizton.main.controller.BaseController;
import com.ruizton.main.model.Ftradehistory;
import com.ruizton.main.model.Fuser;
import com.ruizton.main.model.Fvirtualcointype;
import com.ruizton.main.model.Fvirtualwallet;
import com.ruizton.main.model.Fwallet;
import com.ruizton.main.service.admin.VirtualWalletService;
import com.ruizton.main.service.front.FrontUserService;
import com.ruizton.main.service.front.FrontVirtualCoinService;
import com.ruizton.util.Comm;
import com.ruizton.util.Utils;



/**
 * 前台价目
 * @author   Dylan
 * @data     2018年8月9日
 * @typeName FrontQuotationsJsonController
 * 说明 ：该类实现了前台交易信息列表的查询功能
 *
 */
@Controller
public class FrontQuotationsJsonController extends BaseController {
	@Autowired
	private RealTimeData realTimeData ;
	@Autowired
	private FrontVirtualCoinService frontVirtualCoinService ;
	@Autowired
	private OneDayData oneDayData ;
	@Autowired
	private ConstantMap constantMap ;
	@Autowired
	private FrontUserService frontUserService ;
	@Autowired
	private KlinePeriodData klinePeriodData;
	@Autowired
	private VirtualWalletService virtualWalletService;
	
//	//行情
//	@RequestMapping(value="/real/ticker")
//	@ResponseBody
//	public String ticker(
//			HttpServletRequest request,
//			@RequestParam(value="symbol",required=false,defaultValue="0") int symbol
//			) throws Exception{
//		JSONObject jsonObject = new JSONObject() ;
//		List<Fvirtualcointype> fvirtualcointypes = (List<Fvirtualcointype>)this.constantMap.get("virtualCoinType") ;
//		
//		for (Fvirtualcointype fvirtualcointype : fvirtualcointypes) {
//			jsonObject.accumulate("LastDealPrize-"+fvirtualcointype.getfShortName(), Utils.number2String(this.realTimeData.getLatestDealPrize(fvirtualcointype.getFid()))) ;
//		}
//		
//		Fvirtualcointype fvirtualcointype = null ;
//		if(symbol!=0){
//			fvirtualcointype = this.frontVirtualCoinService.findFvirtualCoinById(symbol) ;
//		}
//		if(fvirtualcointype==null){
//			fvirtualcointype = this.frontVirtualCoinService.findFirstFirtualCoin() ;
//		}
//		
//		int id = fvirtualcointype.getFid() ;
//		jsonObject.accumulate("last", String.valueOf(this.realTimeData.getLatestDealPrize(id))) ;
//		jsonObject.accumulate("buy", String.valueOf(this.realTimeData.getHighestBuyPrize(id))) ;
//		jsonObject.accumulate("sell", String.valueOf(this.realTimeData.getLowestSellPrize(id))) ;
//		jsonObject.accumulate("high", String.valueOf(this.oneDayData.getHighest(id))) ;
//		jsonObject.accumulate("low", String.valueOf(this.oneDayData.getLowest(id))) ;
//		jsonObject.accumulate("vol", String.valueOf(this.oneDayData.getTotal(id))) ;
//		
//		return jsonObject.toString() ;
//	}
//
//	@ResponseBody
//	@RequestMapping(value="/real/get10day",produces={JsonEncode})
//	public String get10day(
//			HttpServletRequest request,
//			String jsoncallback
//			){
//		
//		return jsoncallback+"([[\"02-19\",3808.88,3885.0,3776.0,3812.0,15717.3480,3825.51],[\"02-20\",3812.0,3847.0,3610.0,3663.0,17073.2140,3745.96],[\"02-21\",3640.0,3666.0,3360.7,3437.0,16049.9580,3502.19],[\"02-22\",3444.99,3563.13,3400.0,3527.0,6198.9720,3480.18],[\"02-23\",3527.0,3907.0,3518.0,3799.0,21563.9280,3769.07],[\"02-24\",3799.0,3850.99,3560.0,3620.0,44802.8780,3701.14],[\"02-25\",3628.01,3634.73,3028.57,3094.0,24101.9960,3427.35]])" ;
//	}
//	
//	
//	@ResponseBody
//	@RequestMapping("/json/topQuotations")
//	public String topQuotations(
//			HttpServletRequest request
//			) throws Exception {
//		int financeType = Integer.parseInt(request.getParameter("symbol")) ;
//		Fvirtualcointype t = this.frontVirtualCoinService.findFvirtualCoinById(financeType);
//		if(t == null){
//			return "";
//		}
//		
//		JSONObject jsonObject = new JSONObject() ;
//		jsonObject.accumulate("result", true) ;
//		
//		Double last = Utils.getDouble(this.realTimeData.getLatestDealPrize(financeType), 4) ;
//		Double high = Utils.getDouble(this.oneDayData.getHighest(financeType), 4) ;
//		Double low = Utils.getDouble(this.oneDayData.getLowest(financeType), 4) ;
//		Double volume = Utils.getDouble(this.oneDayData.getTotal(financeType), 4) ;
//		Double buyone = Utils.getDouble(this.realTimeData.getHighestBuyPrize(financeType), 4) ;
//		Double sellone = Utils.getDouble(this.realTimeData.getLowestSellPrize(financeType), 4) ;
//		
//		jsonObject.accumulate("last", last) ;
//		jsonObject.accumulate("high", high) ;
//		jsonObject.accumulate("low", low) ;
//		jsonObject.accumulate("volume", volume) ;
//		jsonObject.accumulate("buyone", buyone) ;
//		jsonObject.accumulate("sellone", sellone) ;
//		
//		return jsonObject.toString() ;
//	}
//	
//	@ResponseBody
//	@RequestMapping(value="/json/depth",produces=JsonEncode)
//	public String depth(HttpServletRequest request) throws Exception {
//		int financeType = Integer.parseInt(request.getParameter("symbol")) ;
//		Fvirtualcointype t = this.frontVirtualCoinService.findFvirtualCoinById(financeType);
//		if(t == null){
//			return "";
//		}
//		
//		JSONObject jsonObject = new JSONObject() ;
//		TreeSet<Fentrust> buy = this.realTimeData.getBuyDepthMap(financeType) ;
//		TreeSet<Fentrust> sell = this.realTimeData.getSellDepthMap(financeType) ;
//		StringBuffer content = new StringBuffer(0) ;
//
////		if("view_marketdepth_btc".equals(request.getParameter("ruizton_callback"))){
////			content.append("view_marketdepth_btc(") ;
////		}
////		
//		JSONArray buyArray = new JSONArray() ;
//		JSONArray sellArray = new JSONArray() ;
//		
//		Iterator<Fentrust> buyIterator = buy.iterator() ;
//		Iterator<Fentrust> sellIterator = sell.iterator() ;
//		while (buyIterator.hasNext()) {
//			Fentrust fentrust = buyIterator.next() ;
//			JSONArray i = new JSONArray() ;
//			i.add(Utils.getDouble(fentrust.getFprize(), 4)) ;
//			i.add(Utils.getDouble(fentrust.getFleftCount(), 4)) ;
//			buyArray.add(i) ;
//		}
//		
//		while (sellIterator.hasNext()) {
//			Fentrust fentrust = sellIterator.next() ;
//			JSONArray i = new JSONArray() ;
//			i.add(Utils.getDouble(fentrust.getFprize(), 4)) ;
//			i.add(Utils.getDouble(fentrust.getFleftCount(), 4)) ;
//			sellArray.add(i) ;
//		}
//
//		JSONObject buyObject = new JSONObject() ;
//		buyObject.accumulate("data", buyArray);
//		JSONObject sellObject = new JSONObject() ;
//		sellObject.accumulate("data", sellArray) ;
//		
//		JSONArray marketdepth = new JSONArray() ;
//		marketdepth.add(buyObject) ;
//		marketdepth.add(sellObject) ;
//		jsonObject.accumulate("marketdepth", marketdepth) ;
//		content.append(jsonObject.toString()) ;
////		
////		if("view_marketdepth_btc".equals(request.getParameter("ruizton_callback"))){
////			content.append(")") ;
////		}
//		
//		return content.toString() ;
//	}
//	
//	@ResponseBody
//	@RequestMapping(value="/json/depthTable",produces=JsonEncode)
//	public String depthTable(HttpServletRequest request) throws Exception {
//		int financeType = Integer.parseInt(request.getParameter("symbol")) ;
//		Fvirtualcointype t = this.frontVirtualCoinService.findFvirtualCoinById(financeType);
//		if(t == null){
//			return "";
//		}
//		
//		JSONObject jsonObject = new JSONObject() ;
//		TreeSet<Fentrust> buy = this.realTimeData.getBuyDepthMap(financeType) ;
//		TreeSet<Fentrust> sell = this.realTimeData.getSellDepthMap(financeType) ;
//
//
//		JSONArray buyArray = new JSONArray() ;
//		JSONArray sellArray = new JSONArray() ;
//		Iterator<Fentrust> buyIterator = buy.iterator()  ;
//		Iterator<Fentrust> sellIterator = sell.iterator() ;
//
//		int index = 7 ;
//		while (buyIterator.hasNext()&&index>0) {
//			index-- ;
//			Fentrust fentrust = buyIterator.next() ;
//			JSONArray i = new JSONArray() ;
//			i.add(Utils.getDouble(fentrust.getFprize(), 4)) ;
//			i.add(Utils.getDouble(fentrust.getFleftCount(), 4)) ;
//			buyArray.add(i) ;
//		}
//		
//		index = 7 ;
//		while (sellIterator.hasNext()&&index>0) {
//			index++ ;
//			Fentrust fentrust = sellIterator.next() ;
//			JSONArray i = new JSONArray() ;
//			i.add(Utils.getDouble(fentrust.getFprize(), 4)) ;
//			i.add(Utils.getDouble(fentrust.getFleftCount(), 4)) ;
//			sellArray.add(i) ;
//		}
//
//		jsonObject.accumulate("buy", buyArray) ;
//		jsonObject.accumulate("sell", sellArray) ;
//		
//		return jsonObject.toString() ;
//	}
//	
//	
//	@ResponseBody
//	@RequestMapping("/json/recentDealList")
//	public String marketRefresh(HttpServletRequest request) throws Exception{
//		int financeType = Integer.parseInt(request.getParameter("symbol")) ;
//		Fvirtualcointype t = this.frontVirtualCoinService.findFvirtualCoinById(financeType);
//		if(t == null){
//			return "";
//		}
//		
//		JSONObject jsonObject = new JSONObject() ;
//		
//		
//		TreeSet<Fentrustlog> successEntrusts = this.realTimeData.getEntrustSuccessMap(financeType) ;
//		
//		
//		JSONArray recentDealList = new JSONArray() ;
//		int index = 8 ;
//		Iterator<Fentrustlog> successEntrustsIterator = successEntrusts.iterator() ;
//		while (successEntrustsIterator.hasNext()&&index>0) {
//			index--;
//			Fentrustlog fentrust = successEntrustsIterator.next() ;
//			JSONArray itemList = new JSONArray() ;
//			itemList.add(Utils.getDouble(fentrust.getFprize(), 4)) ;
//			itemList.add(Utils.getDouble(fentrust.getFcount(), 4)) ;
//			itemList.add(Utils.getDouble(fentrust.getFprize()*fentrust.getFcount(), 4)) ;
//			itemList.add(String.valueOf(Utils.dateFormat(fentrust.getFcreateTime()))) ;
////			itemList.add(fentrust.getfEntrustType()) ;
//			recentDealList.add(itemList) ;
//		}
//		
//		jsonObject.accumulate("recentDealList", recentDealList) ;
//		
//		
//		return jsonObject.toString() ;
//	}
//	
	
	
	
	
	/**
	 *  
	 *  作者：           Dylan
	 *  标题：           indexmarket 
	 *  时间：           2018年8月9日
	 *  描述：           实现前台交易数据的输出
	 *  1.查询出所有可以交易的币种信息 fvirtualcointypes
	 *  2.获取24小时开盘价 s
	 *  3.获取最新价 last
	 *  @param request
	 *  @param totalAmt 
	 *  @param price
	 *  @param total
	 *  @param rose
	 *  @param current
	 *  @param keyWord
	 *  @return 返回utf-8 格式的json串
	 */
	@ResponseBody
	@RequestMapping(value="/real/indexmarket",produces={JsonEncode})
	public String indexmarket(HttpServletRequest request,
			@RequestParam(required=false,defaultValue="")String totalAmt,
			@RequestParam(required=false,defaultValue="")String price,
			@RequestParam(required=false,defaultValue="")String total,
			@RequestParam(required=false,defaultValue="")String rose,
			@RequestParam(required=false,defaultValue="1")int current,
			@RequestParam(required=false,defaultValue="")String keyWord){
		
		JSONObject jsonObject = new JSONObject() ;
		List<JSONObject> list = new ArrayList<>();
		//TreeSet<Object> treeSet = new TreeSet<>();
		
		//从缓存中取出所有虚拟币类型
		List<Fvirtualcointype> fvirtualcointypes = (List)this.constantMap.get("virtualCoinType");
		for (Fvirtualcointype fvirtualcointype : fvirtualcointypes) {
			//判断币种是否可以交易
			if(!fvirtualcointype.isFisShare()) 
			{
				continue;
			}
			
			JSONObject js = new JSONObject() ;
			js.accumulate("id", fvirtualcointype.getFid());
			js.accumulate("name", fvirtualcointype.getFname());
			js.accumulate("url", fvirtualcointype.getFurl());
			js.accumulate("coinname", fvirtualcointype.getFname());
			js.accumulate("price", Utils.getDouble(this.realTimeData.getLatestDealPrize(fvirtualcointype.getFid()), fvirtualcointype.getFcount()));
			js.accumulate("total", Utils.getDouble(this.oneDayData.getTotal(fvirtualcointype.getFid()), 2));
//			js.accumulate("high", Utils.getDouble(this.oneDayData.getHighest(fvirtualcointype.getFid()), fvirtualcointype.getFcount()));
//			js.accumulate("low", Utils.getDouble(this.oneDayData.getLowest(fvirtualcointype.getFid()), fvirtualcointype.getFcount()));
			js.accumulate("totalAmt", Utils.getDouble(this.oneDayData.get24Total(fvirtualcointype.getFid()), 2));
//			
//			double total15D = 0d;
//			Map<String,Double> map = (Map<String,Double>)this.constantMap.get("coinsTotalMap");
//			if(map.containsKey(fvirtualcointype.getfShortName())){
//				total15D = total15D+map.get(fvirtualcointype.getfShortName());
//			}
//			js.accumulate("total15D", total15D);
			
//			js.accumulate("istrade", Utils.openTrade(fvirtualcointype));
			double s = this.oneDayData.get24Price(fvirtualcointype.getFid());
//			double s7 = -8888d;
			//获取交易记录
			List<Ftradehistory> ftradehistorys = (List<Ftradehistory>)constantMap.get("tradehistory");
			for (Ftradehistory ftradehistory : ftradehistorys) {
				if(ftradehistory.getFvid() == fvirtualcointype.getFid()){
					s= ftradehistory.getFprice();
					break;
				}
			}
//			List<Ftradehistory> ftradehistoryss = (List<Ftradehistory>)constantMap.get("ftradehistory7D");
//			for (Ftradehistory ftradehistory : ftradehistoryss) {
//				if(ftradehistory.getFvid().intValue() == fvirtualcointype.getFid().intValue()){
//					s7= ftradehistory.getFprice();
//					break;
//				}
//			}
			double last = 0d;
//			double last7 = 0d;
			try {
				last = Utils.getDouble((this.realTimeData.getLatestDealPrize(fvirtualcointype.getFid())-s)/s*100, 2);
			} catch (Exception e) {}
//			try {
//				if(s7==-8888d){
//					last7=-999999d;
//				}else{
//					last7 = Utils.getDouble((this.realTimeData.getLatestDealPrize(fvirtualcointype.getFid())-s7)/s7*100, 2);
//				}
//			} catch (Exception e) {}
			js.accumulate("rose", last);
//			js.accumulate("rate7",last7) ;
			JSONArray dataArray = new JSONArray();
			try {
				String content = this.klinePeriodData.getJsonString(fvirtualcointype.getFid(), 5) ;
				JSONArray jsonArray = JSONArray.fromObject(content) ;
				if(jsonArray != null && jsonArray.size() >0){
					for(int i=(jsonArray.size()-72 >=0?jsonArray.size()-72:0);i<jsonArray.size();i++){
						JSONArray retItem = new JSONArray() ;
						JSONArray item = jsonArray.getJSONArray(i) ;
						retItem.add(item.getString(0)) ;
						retItem.add(item.getString(1)) ;
						retItem.add(item.getString(2)) ;
						retItem.add(item.getString(3)) ;
						retItem.add(item.getString(4)) ;
						retItem.add(item.getString(5)) ;
						dataArray.add(retItem);
					}
				}
			} catch (Exception e) {}
			
			js.accumulate("data", dataArray);
			//jsonObject.accumulate(fvirtualcointype.getfShortName(), js);
			try {
				if("".equals(keyWord)){
					list.add(js);
					} else {
						String name = (String)js.get("name");
						if(name.indexOf(keyWord)>-1){
							list.add(js);
						}
					}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		/*for (int i = 0; i < list.size(); i++) {
			String object = (String)list.get(i).get("name");
			if(object.indexOf(keyWord)<=-1){
				list.remove(i);
			}
		}*/
		if (!"".equals(price)) {
			if (price.equals("aes")) {
				try{
				// 最新成交价升序排序
				Collections.sort(list, new Comparator<JSONObject>() {
					@Override
					public int compare(JSONObject o1, JSONObject o2) {
						double price1 = (double) o1.get("price");
						double price2 = (double) o2.get("price");
						if (price1 < price2)
							return -1;
						else if (price1 == price2)
							return 0;
						else
							return 1;
					}
				});
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			} else if (price.equals("desc")) {
				// 最新成交价降序排序
				try {
					
				
				Collections.sort(list, new Comparator<JSONObject>() {

					@Override
					public int compare(JSONObject o1, JSONObject o2) {
						double price1 = (double) o1.get("price");
						double price2 = (double) o2.get("price");
						if (price1 < price2)
							return 1;
						else if (price1 == price2)
							return 0;
						else
							return -1;

					}
				});
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else if (!"".equals(total)) {
			if (total.equals("aes")) {
				try{
				// 24小时成交量升序排序
				Collections.sort(list, new Comparator<JSONObject>() {

					@Override
					public int compare(JSONObject o1, JSONObject o2) {
						double total1 = (double) o1.get("total");
						double total2 = (double) o2.get("total");
						if (total1 < total2)
							return -1;
						else if (total1 == total2)
							return 0;
						else
							return 1;

					}
				});
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			} else if (total.equals("desc")) {
				try{
				// 24小时成交量降序排序
				Collections.sort(list, new Comparator<JSONObject>() {

					@Override
					public int compare(JSONObject o1, JSONObject o2) {
						double total1 = (double) o1.get("total");
						double total2 = (double) o2.get("total");
						if (total1 < total2)
							return 1;
						else if (total1 == total2)
							return 0;
						else
							return -1;

					}
				});
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
		} else if (!"".equals(totalAmt)) {
			if (totalAmt.equals("aes")) {
				try{
				// 24小时成交金额升序排序
				Collections.sort(list, new Comparator<JSONObject>() {

					@Override
					public int compare(JSONObject o1, JSONObject o2) {
						double totalAmt1 = (double) o1.get("totalAmt");
						double totalAmt2 = (double) o2.get("totalAmt");
						if (totalAmt1 < totalAmt2)
							return -1;
						else if (totalAmt1 == totalAmt2)
							return 0;
						else
							return 1;

					}
				});
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			} else if (totalAmt.equals("desc")) {
				try{
				// 24小时成交金额降序排序
				Collections.sort(list, new Comparator<JSONObject>() {

					@Override
					public int compare(JSONObject o1, JSONObject o2) {
						double totalAmt1 = (double) o1.get("totalAmt");
						double totalAmt2 = (double) o2.get("totalAmt");
						if (totalAmt1 < totalAmt2)
							return 1;
						else if (totalAmt1 == totalAmt2)
							return 0;
						else
							return -1;

					}
				});
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
		} else if (!"".equals(rose)) {
			if (rose.equals("aes")) {
				try{
				// 24小时涨幅升序排序
				Collections.sort(list, new Comparator<JSONObject>() {

					@Override
					public int compare(JSONObject o1, JSONObject o2) {
						double rose1 = (double) o1.get("rose");
						double rose2 = (double) o2.get("rose");
						if (rose1 < rose2)
							return -1;
						else if (rose1 == rose2)
							return 0;
						else
							return 1;

					}
				});
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			} else if (rose.equals("desc")) {
				try{
				// 24小时涨幅降序排序
				Collections.sort(list, new Comparator<JSONObject>() {

					@Override
					public int compare(JSONObject o1, JSONObject o2) {
						double rose1 = (double) o1.get("rose");
						double rose2 = (double) o2.get("rose");
						if (rose1 < rose2)
							return 1;
						else if (rose1 == rose2)
							return 0;
						else
							return -1;

					}
				});
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		int size = list.size();
		int pageCount = size/Comm.getFIRST_PAGE_NUM() + ((size%Comm.getFIRST_PAGE_NUM())==0?0:1);
		if(current*Comm.getFIRST_PAGE_NUM()<=size){
			List<JSONObject> subList = list.subList((current-1)*Comm.getFIRST_PAGE_NUM(), current*Comm.getFIRST_PAGE_NUM());
			jsonObject.accumulate("result", subList);
		}else{
			List<JSONObject> subList = list.subList((current-1)*Comm.getFIRST_PAGE_NUM(), size);
			jsonObject.accumulate("result", subList);
		}
		String pagin = this.generatePagin6(pageCount, current) ;
		//jsonObject.accumulate("result", list);
		jsonObject.accumulate("pagin", pagin);
		jsonObject.accumulate("currentPage", current);
		return jsonObject.toString();
	}
	
	
	@ResponseBody
	@RequestMapping("/real/userassets")
	public String userassets(
			HttpServletRequest request,
			@RequestParam(required=true)String symbol
			) throws Exception {
		JSONObject jsonObject = new JSONObject() ;
		
		Fuser fuser = GetSession(request) ;
		if(fuser==null){
			//可用
			jsonObject.accumulate("availableCny", 0) ;
			jsonObject.accumulate("availableCoin", 0) ;
			jsonObject.accumulate("frozenCny", 0) ;
			jsonObject.accumulate("frozenCoin", 0) ;
			//借貸明細
			JSONObject leveritem = new JSONObject() ;
			leveritem.accumulate("total", 0) ;
			leveritem.accumulate("asset", 0) ;
			leveritem.accumulate("score", 0) ;
			jsonObject.accumulate("leveritem", leveritem) ;
			//人民幣明細
			JSONObject cnyitem = new JSONObject() ;
			cnyitem.accumulate("total", 0) ;
			cnyitem.accumulate("frozen",0) ;
			cnyitem.accumulate("borrow", 0) ;
			jsonObject.accumulate("cnyitem", cnyitem) ;
			//人民幣明細
			JSONObject coinitem = new JSONObject() ;
			coinitem.accumulate("id", symbol) ;
			coinitem.accumulate("total", 0) ;
			coinitem.accumulate("frozen",0) ;
			coinitem.accumulate("borrow", 0) ;
			jsonObject.accumulate("coinitem", coinitem) ;
		}else{
			fuser = this.frontUserService.findById(fuser.getFid()) ;
			Fwallet fwallet = fuser.getFwallet() ;
			//this.frontUserService.findVirtualWallet		
			Fvirtualwallet fvirtualwallet = this.frontUserService.findVirtualWalletByUser(fuser.getFid(), symbol) ;
			//获取默认资产
			double price=1;//人民币与默认资产的比例
			String filter=" where fisDefAsset=1";
			List<Fvirtualcointype> listFvirtualcointype=frontVirtualCoinService.findByParam1(0, 0, filter, false, "Fvirtualcointype");
			Fvirtualcointype fvirtualcointype=null;
			if(null!=listFvirtualcointype&&listFvirtualcointype.size()>0)
			{
				fvirtualcointype=listFvirtualcointype.get(0);
			}
			if(null!=fvirtualcointype)
			{
				Fvirtualwallet fvirtualDefWallet = this.virtualWalletService.findVirtualWallet(fuser.getFid(), fvirtualcointype.getFid());
				price=fvirtualcointype.getFprice();
				
				//可用
				jsonObject.accumulate("availableCny", fvirtualDefWallet.getFtotal()) ;
				jsonObject.accumulate("availableCoin", fvirtualwallet.getFtotal()) ;
				jsonObject.accumulate("frozenCny", fvirtualDefWallet.getFfrozen()) ;
				jsonObject.accumulate("frozenCoin", fvirtualwallet.getFfrozen()) ;
				//借貸明細
				JSONObject leveritem = new JSONObject() ;
				leveritem.accumulate("total", 0) ;
				leveritem.accumulate("asset", 0) ;
				leveritem.accumulate("score", 0) ;
				jsonObject.accumulate("leveritem", leveritem) ;
				
				//人民幣明細
				JSONObject coinitem = new JSONObject() ;
				coinitem.accumulate("id", symbol) ;
				coinitem.accumulate("total", fvirtualwallet.getFtotal()) ;
				coinitem.accumulate("frozen", fvirtualwallet.getFfrozen()) ;
				coinitem.accumulate("borrow", 0) ;
				jsonObject.accumulate("coinitem", coinitem) ;
			}else{
			//可用
			jsonObject.accumulate("availableCny", fwallet.getFtotalRmb()) ;
			jsonObject.accumulate("availableCoin", fvirtualwallet.getFtotal()) ;
			jsonObject.accumulate("frozenCny", fwallet.getFfrozenRmb()) ;
			jsonObject.accumulate("frozenCoin", fvirtualwallet.getFfrozen()) ;
			//借貸明細
			JSONObject leveritem = new JSONObject() ;
			leveritem.accumulate("total", 0) ;
			leveritem.accumulate("asset", 0) ;
			leveritem.accumulate("score", 0) ;
			jsonObject.accumulate("leveritem", leveritem) ;

			//人民幣明細
			JSONObject coinitem = new JSONObject() ;
			coinitem.accumulate("id", symbol) ;
			coinitem.accumulate("total", fvirtualwallet.getFtotal()) ;
			coinitem.accumulate("frozen", fvirtualwallet.getFfrozen()) ;
			coinitem.accumulate("borrow", 0) ;
			jsonObject.accumulate("coinitem", coinitem) ;
			}
			//人民幣明細
			JSONObject cnyitem = new JSONObject() ;
			cnyitem.accumulate("total", fwallet.getFtotalRmb()) ;
			cnyitem.accumulate("frozen", fwallet.getFfrozenRmb()) ;
			cnyitem.accumulate("borrow", 0) ;
			jsonObject.accumulate("cnyitem", cnyitem) ;
		}

	    
		
		return jsonObject.toString() ;
		
	}
	public class ss {
		private int name;
		private String id;
		public int getName() {
			return name;
		}

		public void setName(int name) {
			this.name = name;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}
		
	}
	
}
