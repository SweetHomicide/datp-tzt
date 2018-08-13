package com.ruizton.main.controller.front;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.ruizton.main.Enum.EntrustStatusEnum;
import com.ruizton.main.Enum.VirtualCoinTypeStatusEnum;
import com.ruizton.main.auto.KlinePeriodData;
import com.ruizton.main.auto.RealTimeData;
import com.ruizton.main.comm.ConstantMap;
import com.ruizton.main.comm.KeyValues;
import com.ruizton.main.controller.BaseController;
import com.ruizton.main.model.Fabout;
import com.ruizton.main.model.Fentrust;
import com.ruizton.main.model.Flimittrade;
import com.ruizton.main.model.Fuser;
import com.ruizton.main.model.Fvirtualcointype;
import com.ruizton.main.service.admin.AboutService;
import com.ruizton.main.service.admin.UserService;
import com.ruizton.main.service.front.FrontOthersService;
import com.ruizton.main.service.front.FrontTradeService;
import com.ruizton.main.service.front.FrontUserService;
import com.ruizton.main.service.front.FrontVirtualCoinService;
import com.ruizton.util.Comm;
import com.ruizton.util.Constant;
import com.ruizton.util.Utils;

import net.sf.json.JSONObject;

/**
 * @author   Dylan
 * @data     2018年8月13日
 * @typeName FrontTradeController
 * 说明 ：前台交易
 *
 */
@Controller
public class FrontTradeController extends BaseController {
	@Autowired
	private FrontVirtualCoinService frontVirtualCoinService ;
	@Autowired
	private FrontTradeService frontTradeService ;
	@Autowired
	private RealTimeData realTimeData ;
	@Autowired
	private FrontUserService frontUserService ;
	@Autowired
	private KlinePeriodData klinePeriodData ;
	@Autowired
	private UserService userService;
	@Autowired
	private ConstantMap constantMap ;
	
	@Autowired
	private AboutService aboutService;
	
	@Autowired
	private FrontOthersService frontOthersService ;
	
	/***
	 * 
	 *  作者：           Dylan
	 *  标题：           coin 
	 *  时间：           2018年8月13日
	 *  描述：           去交易 跳转到交易详情页面
	 *  
	 *  @param request
	 *  @param coinType  币种ID
	 *  @param tradeType 交易类型
	 *  @return 
	 *  @throws Exception
	 */
	@RequestMapping("/trade/coin")
	public ModelAndView coin(HttpServletRequest request,
			@RequestParam(required=false,defaultValue="0")String coinType,//VirtualCoinTypeEnum
			@RequestParam(required=false,defaultValue="0")int tradeType) throws Exception{
		
		String currentPage="1";//页码
		int pageSize = Comm.getPAGE_NUM();
		int limitNum = (Integer.valueOf(currentPage)-1)*pageSize;
		String filter = " and fisShare=1";
		//查询出可以交易的币种类集合
		List<Fvirtualcointype> fList = this.frontVirtualCoinService.findByParam(limitNum, pageSize, filter);
		//查询有效币种数量 count
		String sql = "select count(*) from Fvirtualcointype where fstatus=1"+ filter;
		int count = this.frontVirtualCoinService.findCount(sql);
		//分页
		String pagin = this.generatePagin1(count/pageSize+( (count%pageSize)==0?0:1), Integer.valueOf(currentPage)) ;
		ModelAndView modelAndView = new ModelAndView() ;
		//modelAndView.addObject("count", count);
		String userid = null;//用户id 从session中获取
		Fuser fuser = null;//系统用户
		boolean isTelephoneBind =false; //是否绑定手机
		boolean fpostRealValidate=false;//是否已经提交身份认证
		//判断用户是否为null
		if(GetSession(request) != null){
			fuser = this.userService.findById(GetSession(request).getFid());
			userid = fuser.getFid();
			isTelephoneBind 	= fuser.isFisTelephoneBind();
			fpostRealValidate	= fuser.getFpostRealValidate();
		}
		
		//交易类型
		tradeType = tradeType < 0? 0: tradeType ;
		tradeType = tradeType > 1? 1: tradeType ;
		//根据币种id获取币种信息
		Fvirtualcointype fvirtualcointype = this.frontVirtualCoinService.findFvirtualCoinById(coinType) ;
		//如果不可以交易
		if(fvirtualcointype==null 
				 || fvirtualcointype.getFstatus()==VirtualCoinTypeStatusEnum.Abnormal
				 || !fvirtualcointype.isFisShare()){
			
			fvirtualcointype = this.frontVirtualCoinService.findFirstFirtualCoin(0) ;
			if(fvirtualcointype==null){
				modelAndView.setViewName("redirect:/") ;
			}else{
				modelAndView.setViewName("redirect:/trade/coin.html?coinType="+fvirtualcointype.getFid()+"&tradeType=0") ;
			}
			return modelAndView ;
		}
		
		coinType = fvirtualcointype.getFid();
		
		Flimittrade limittrade = this.isLimitTrade(fvirtualcointype.getFid());
		boolean isLimittrade = false;
		double upPrice = 0d;
		double downPrice = 0d;
		if(limittrade != null){
			isLimittrade = true;
			upPrice = Utils.getDouble(limittrade.getFupprice()+limittrade.getFupprice()*limittrade.getFpercent(), fvirtualcointype.getFcount());
			downPrice = Utils.getDouble(limittrade.getFdownprice()-limittrade.getFdownprice()*limittrade.getFpercent(), fvirtualcointype.getFcount());
			if(downPrice <0) downPrice=0;
		}
		modelAndView.addObject("fList", fList);
		modelAndView.addObject("pagins", pagin);
		modelAndView.addObject("cur_page", currentPage) ;
		modelAndView.addObject("isLimittrade", isLimittrade) ;
		modelAndView.addObject("upPrice", upPrice) ;
		modelAndView.addObject("downPrice", downPrice) ;

		
		
		boolean isTradePassword = false;
		if(userid != null && fuser.getFtradePassword() != null && fuser.getFtradePassword().trim().length() >0){
			isTradePassword = true;
		}
		
		//委托记录
		List<Fentrust> fentrusts = this.frontTradeService.findFentrustHistory(
				userid, coinType,null, 0, 10, " fCreateTime desc ", new int[]{EntrustStatusEnum.Going,EntrustStatusEnum.PartDeal}) ;
		
		//是否需要输入交易密码
		modelAndView.addObject("needTradePasswd", super.isNeedTradePassword(request)) ;
		String recommendPrizesell = new BigDecimal( this.realTimeData.getHighestBuyPrize(coinType)).setScale(2, BigDecimal.ROUND_HALF_UP).toString();
		String recommendPrizebuy = new BigDecimal( this.realTimeData.getLowestSellPrize(coinType)).setScale(2, BigDecimal.ROUND_HALF_UP).toString();
		String tradestock = this.klinePeriodData.getJsonString(coinType, 0) ; 
		modelAndView.addObject("tradestock", tradestock) ;
		modelAndView.addObject("fentrusts", fentrusts) ;
		modelAndView.addObject("fuser", fuser) ;
		modelAndView.addObject("userid", userid) ;
		modelAndView.addObject("isTradePassword", isTradePassword) ;
		modelAndView.addObject("isTelephoneBind", isTelephoneBind) ;
		modelAndView.addObject("fpostRealValidate", fpostRealValidate) ;
		modelAndView.addObject("recommendPrizesell",recommendPrizesell) ;
		modelAndView.addObject("recommendPrizebuy", recommendPrizebuy) ;
		modelAndView.addAllObjects(this.setRealTimeData(coinType)) ;
		modelAndView.addObject("fvirtualcointype",fvirtualcointype) ;
		modelAndView.addObject("coinType", coinType) ;
		modelAndView.addObject("tradeType", tradeType) ;
		modelAndView.setViewName("front/trade/trade_coin") ;
		return modelAndView ;
	}
	@ResponseBody
	@RequestMapping("/trade/cointype")
	public String getCoinType(
			HttpServletRequest request,
			@RequestParam(required=false,defaultValue="1")String currentPage,
			@RequestParam(required=false,defaultValue="")String hidlog
			){
		JSONObject js = new JSONObject();
		String result ="";
		int pageSize = Comm.getPAGE_NUM();
		int count = 0;
		String sql;
		List<Fvirtualcointype> fList;
		int limitNum = (Integer.valueOf(currentPage)-1)*pageSize;
		String filter1 = " and fisShare = 1";
		if("".equals(hidlog) || hidlog.equals(null)){
			
		
		fList = this.frontVirtualCoinService.findByParam(limitNum, pageSize, filter1);
		
		sql = "select count(*) from Fvirtualcointype where fstatus="+VirtualCoinTypeStatusEnum.Normal+filter1;
		} else{
			String filter = " where fstatus="+VirtualCoinTypeStatusEnum.Normal+filter1+" and fname like '%"+hidlog+"%'";
			String className = "Fvirtualcointype";
			fList = this.frontVirtualCoinService.findByParam1(limitNum, pageSize, filter, true, className);
			sql = "select count(*) from Fvirtualcointype where fstatus="+VirtualCoinTypeStatusEnum.Normal+filter1+" and fname like '%"+hidlog+"%'";
			
		}
		count = this.frontVirtualCoinService.findCount(sql);
		for (Fvirtualcointype fvirtualcointype : fList) {
			
			String fa = "<div class='col-xs-4' style='line-height:40px;'><a href='/trade/coin.html?coinType="+fvirtualcointype.getFid()+"'"+"><i class='lefticon col-xs-2' style='margin-right:5px;top:5px;width:20px;height:30px;background-size:100%;background-image: url("+fvirtualcointype.getFurl()+")'></i><span>&nbsp;"+fvirtualcointype.getFname()+"</span></a></div>";
			
			result = result + fa;
			}
		String pagin = this.generatePagin1(count/pageSize+( (count%pageSize)==0?0:1), Integer.valueOf(currentPage)) ;
		js.accumulate("page", pagin);
		js.accumulate("result", result);
		return js.toString();
	}
	@ResponseBody
	@RequestMapping("/account/cointype")
	public String getCoinType1(
			HttpServletRequest request,
			@RequestParam(required=false,defaultValue="1")String currentPage,
			@RequestParam(required=false,defaultValue="")String hidlog
			){
		JSONObject js = new JSONObject();
		String result ="";
		int pageSize = Comm.getPAGE_NUM();
		int count = 0;
		String sql;
		List<Fvirtualcointype> fList;
		String filter1 = " and FIsWithDraw = 1 ";
		int limitNum = (Integer.valueOf(currentPage)-1)*pageSize;
		if("".equals(hidlog) || hidlog.equals(null)){
			
		
		fList = this.frontVirtualCoinService.findByParam(limitNum, pageSize, filter1);
		
		sql = "select count(*) from Fvirtualcointype where fstatus="+VirtualCoinTypeStatusEnum.Normal+filter1;
		} else{
			String filter = " where fstatus="+VirtualCoinTypeStatusEnum.Normal+filter1+" and fname like '%"+hidlog+"%'";
			String className = "Fvirtualcointype";
			fList = this.frontVirtualCoinService.findByParam1(limitNum, pageSize, filter, true, className);
			sql = "select count(*) from Fvirtualcointype where fstatus="+VirtualCoinTypeStatusEnum.Normal+filter1+" and fname like '%"+hidlog+"%'";
			
		}
		count = this.frontVirtualCoinService.findCount(sql);
		for (Fvirtualcointype fvirtualcointype : fList) {
			
			String fa = "<div class='col-xs-4' style='line-height:40px;'><a href='/account/rechargeBtc.html?symbol="+fvirtualcointype.getFid()+"'"+"><i class='lefticon col-xs-2' style='margin-right:5px;top:5px;width:20px;height:30px;background-size:100%;background-image: url("+fvirtualcointype.getFurl()+")'></i><span>&nbsp;"+fvirtualcointype.getFname()+"</span></a></div>";
			
			result = result + fa;
			}
		String pagin = this.generatePagin2(count/pageSize+( (count%pageSize)==0?0:1), Integer.valueOf(currentPage)) ;
		js.accumulate("page", pagin);
		js.accumulate("result", result);
		return js.toString();
	}
	@ResponseBody
	@RequestMapping("/account/cointype1")
	public String getCoinType2(
			HttpServletRequest request,
			@RequestParam(required=false,defaultValue="1")String currentPage,
			@RequestParam(required=false,defaultValue="")String hidlog
			){
		JSONObject js = new JSONObject();
		String result ="";
		int pageSize = Comm.getPAGE_NUM();
		int count = 0;
		String sql;
		List<Fvirtualcointype> fList;
		int limitNum = (Integer.valueOf(currentPage)-1)*pageSize;
		String filter1 = " and FIsWithDraw = 1";
		if("".equals(hidlog) || hidlog.equals(null)){
			
		
		fList = this.frontVirtualCoinService.findByParam(limitNum, pageSize, filter1);
		
		sql = "select count(*) from Fvirtualcointype where fstatus="+VirtualCoinTypeStatusEnum.Normal+ filter1;
		} else{
			String filter = " where fstatus="+VirtualCoinTypeStatusEnum.Normal+filter1+" and fname like '%"+hidlog+"%'";
			String className = "Fvirtualcointype";
			fList = this.frontVirtualCoinService.findByParam1(limitNum, pageSize, filter, true, className);
			sql = "select count(*) from Fvirtualcointype where fstatus="+VirtualCoinTypeStatusEnum.Normal+filter1+" and fname like '%"+hidlog+"%'";
			
		}
		count = this.frontVirtualCoinService.findCount(sql);
		for (Fvirtualcointype fvirtualcointype : fList) {
			
			String fa = "<div class='col-xs-4' style='line-height:40px;'><a href='/account/withdrawBtc.html?symbol="+fvirtualcointype.getFid()+"'"+"><i class='lefticon col-xs-2' style='margin-right:5px;top:5px;width:20px;height:30px;background-size:100%;background-image: url("+fvirtualcointype.getFurl()+")'></i><span>&nbsp;"+fvirtualcointype.getFname()+"</span></a></div>";
			
			result = result + fa;
			}
		String pagin = this.generatePagin3(count/pageSize+( (count%pageSize)==0?0:1), Integer.valueOf(currentPage)) ;
		js.accumulate("page", pagin);
		js.accumulate("result", result);
		return js.toString();
	}
	/*https://www.okcoin.com/trade/entrust.do?symbol=1
	 * */
	@RequestMapping("/trade/entrust")
	public ModelAndView entrust(
			HttpServletRequest request,
			@RequestParam(required=false,defaultValue="0")String symbol,
			@RequestParam(required=false,defaultValue="0")int status,
			@RequestParam(required=false,defaultValue="1")int currentPage
			)throws Exception{
		String currentPages="1";
		int pageSize = Comm.getPAGE_NUM();
		int limitNum = (Integer.valueOf(currentPages)-1)*pageSize;
		String filter = " and fisShare = 1";
		List<Fvirtualcointype> fList = this.frontVirtualCoinService.findByParam(limitNum, pageSize, filter);
		String sql = "select count(*) from Fvirtualcointype where fstatus=1"+filter;
		int count = this.frontVirtualCoinService.findCount(sql);
		String pagins = this.generatePagin1(count/pageSize+( (count%pageSize)==0?0:1), Integer.valueOf(currentPages)) ;
		
		ModelAndView modelAndView = new ModelAndView() ;
		modelAndView.addObject("fList", fList);
		modelAndView.addObject("pagins", pagins);
		Fvirtualcointype fvirtualcointype = null ;
		if(symbol.equals("0")){
			fvirtualcointype = this.frontVirtualCoinService.findFirstFirtualCoin(0) ;
			symbol = fvirtualcointype.getFid() ;
		}else{
			fvirtualcointype = this.frontVirtualCoinService.findFvirtualCoinById(symbol) ;
		}
		if(fvirtualcointype==null){
			modelAndView.setViewName("front/trade/trade_entrust") ;
			return modelAndView ;
		}
		
		List<Fvirtualcointype> fvirtualcointypes = (List<Fvirtualcointype>)this.constantMap.get("virtualCoinType") ;
		
		int fstatus[] = null ;
		if(status==0){
			//正在委托
			fstatus = new int[]{EntrustStatusEnum.Going,EntrustStatusEnum.PartDeal} ;
		}else{
			//委托完成
			fstatus = new int[]{EntrustStatusEnum.AllDeal,EntrustStatusEnum.Cancel} ;
		}
		
		List<Fentrust> fentrusts = 
				this.frontTradeService.findFentrustHistory(
						GetSession(request).getFid(), 
						fvirtualcointype.getFid(),
						null, (currentPage-1)*Constant.RecordPerPage, 
						Constant.RecordPerPage, 
						"fcreateTime desc ", fstatus) ;
		int total = this.frontTradeService.findFentrustHistoryCount(
				GetSession(request).getFid(), 
				fvirtualcointype.getFid(),
				null,fstatus) ;
		String pagin = this.generatePagin((int)(total/Constant.RecordPerPage+(total%Constant.RecordPerPage==0?0:1) ), currentPage, "/trade/entrust.html?symbol="+symbol+"&status="+status+"&") ;

		modelAndView.addObject("currentPage", currentPage) ;
		modelAndView.addObject("pagin",pagin) ;
		modelAndView.addObject("fentrusts", fentrusts) ;
		modelAndView.addObject("fvirtualcointype", fvirtualcointype) ;
		modelAndView.addObject("symbol", symbol) ;
		modelAndView.addObject("fvirtualcointypes", fvirtualcointypes) ;
		modelAndView.setViewName("front/trade/trade_entrust") ;
		return modelAndView ;
	}
	@RequestMapping("/trade/entrust2")
	public ModelAndView entrust2(
			HttpServletRequest request,
			@RequestParam(required=false,defaultValue="0")String symbol,
			@RequestParam(required=false,defaultValue="0")int status,
			@RequestParam(required=false,defaultValue="1")int currentPage
			)throws Exception{
		String currentPages="1";
		int pageSize = Comm.getPAGE_NUM();
		int limitNum = (Integer.valueOf(currentPages)-1)*pageSize;
		String filter = " and fisShare = 1";
		List<Fvirtualcointype> fList = this.frontVirtualCoinService.findByParam(limitNum, pageSize, filter);
		String sql = "select count(*) from Fvirtualcointype where fstatus=1"+filter;
		int count = this.frontVirtualCoinService.findCount(sql);
		String pagins = this.generatePagin1(count/pageSize+( (count%pageSize)==0?0:1), Integer.valueOf(currentPages)) ;
		
		ModelAndView modelAndView = new ModelAndView() ;
		modelAndView.addObject("fList",	fList);
		modelAndView.addObject("pagins", pagins);
		Fvirtualcointype fvirtualcointype = null ;
		if(symbol.equals("0")){
			fvirtualcointype = this.frontVirtualCoinService.findFirstFirtualCoin(0) ;
			symbol = fvirtualcointype.getFid() ;
		}else{
			fvirtualcointype = this.frontVirtualCoinService.findFvirtualCoinById(symbol) ;
		}
		if(fvirtualcointype==null){
			modelAndView.setViewName("front/trade/trade_entrust") ;
			return modelAndView ;
		}
		
		List<Fvirtualcointype> fvirtualcointypes = (List<Fvirtualcointype>)this.constantMap.get("virtualCoinType") ;
		
		int fstatus[] = null ;
		if(status==0){
			//正在委托
			fstatus = new int[]{EntrustStatusEnum.Going,EntrustStatusEnum.PartDeal} ;
		}else{
			//委托完成
			fstatus = new int[]{EntrustStatusEnum.AllDeal,EntrustStatusEnum.Cancel} ;
		}
		
		List<Fentrust> fentrusts = 
				this.frontTradeService.findFentrustHistory(
						GetSession(request).getFid(), 
						fvirtualcointype.getFid(),
						null, (currentPage-1)*Constant.RecordPerPage, 
						Constant.RecordPerPage, 
						"fcreateTime desc ", fstatus) ;
		int total = this.frontTradeService.findFentrustHistoryCount(
				GetSession(request).getFid(), 
				fvirtualcointype.getFid(),
				null,fstatus) ;
		String pagin = this.generatePagin((int)(total/Constant.RecordPerPage+(total%Constant.RecordPerPage==0?0:1) ), currentPage, "/trade/entrust2.html?symbol="+symbol+"&status="+status+"&") ;

		modelAndView.addObject("currentPage", currentPage) ;
		modelAndView.addObject("pagin",pagin) ;
		modelAndView.addObject("fentrusts", fentrusts) ;
		modelAndView.addObject("fvirtualcointype", fvirtualcointype) ;
		modelAndView.addObject("symbol", symbol) ;
		modelAndView.addObject("fvirtualcointypes", fvirtualcointypes) ;
		modelAndView.setViewName("front/trade/trade_entrust2") ;
		return modelAndView ;
	}
	
/*	@RequestMapping("/trade/plan")
	public ModelAndView plan(
			@RequestParam(required=false,defaultValue="0")int symbol,
			@RequestParam(required=false,defaultValue="1")int currentPage
			)throws Exception{
		
		ModelAndView modelAndView = new ModelAndView() ;
		Fvirtualcointype fvirtualcointype = null ;
		if(symbol == 0){
			fvirtualcointype = this.frontVirtualCoinService.findFirstFirtualCoin() ;
			symbol = fvirtualcointype.getFid() ;
		}else{
			fvirtualcointype = this.frontVirtualCoinService.findFvirtualCoinById(symbol) ;
		}
		
		
		if(fvirtualcointype==null || !fvirtualcointype.isFisShare()){
			modelAndView.setViewName("redirect:/") ;
			return modelAndView ;
		}
		
		List<Fentrustplan> fentrustplans = this.frontTradeService.findFentrustplan(
				GetSession(request).getFid(), 
				symbol, 
				new int[]{EntrustPlanStatusEnum.Cancel,EntrustPlanStatusEnum.Entrust,EntrustPlanStatusEnum.Not_entrust}, 
				(currentPage-1)*Constant.EntrustPlanRecordPerPage, 
				Constant.EntrustPlanRecordPerPage, 
				"fcreateTime desc ") ;
		int total = (int)this.frontTradeService.findFentrustplanCount(GetSession(request).getFid(), 
				symbol, 
				new int[]{EntrustPlanStatusEnum.Cancel,EntrustPlanStatusEnum.Entrust,EntrustPlanStatusEnum.Not_entrust});
		String pagin = this.generatePagin((int)(total/Constant.EntrustPlanRecordPerPage+(total%Constant.EntrustPlanRecordPerPage==0?0:1) ), currentPage, "/trade/plan.html?symbol="+symbol+"&") ;
		
		List<Fvirtualcointype> fvirtualcointypes = (List<Fvirtualcointype>)this.constantMap.get("virtualCoinType") ;
		
		modelAndView.addAllObjects(setRealTimeData(fvirtualcointype.getFid()));
		modelAndView.addObject("fvirtualcointype", fvirtualcointype) ;
		modelAndView.addObject("symbol", symbol) ;
		modelAndView.addObject("fvirtualcointypes", fvirtualcointypes) ;
		modelAndView.addObject("pagin",pagin) ;
		modelAndView.addObject("fentrustplans", fentrustplans) ;
		modelAndView.setViewName("front/trade/trade_plan") ;
		return modelAndView ;
	}*/
	
	/*
	 * http://localhost:8899/trade/entrustInfo.html?type=0&random=74&_=1393130976495
	 * */
	/*
	 * @param type:0未成交前十条，1成交前10条
	 * @param symbol:1币种
	 * */
	@RequestMapping("/trade/entrustInfo")
	public ModelAndView entrustInfo(
			HttpServletRequest request,
			@RequestParam(required=true)int type,
			@RequestParam(required=true)String symbol,
			@RequestParam(required=true)int tradeType
			) throws Exception{
		
		
		ModelAndView modelAndView = new ModelAndView() ;
		
		String userid = "0";
		if(GetSession(request) != null){
			userid = GetSession(request).getFid();
		}
		

		Fvirtualcointype fvirtualcointype = this.frontVirtualCoinService.findFvirtualCoinById(symbol) ;
		
		List<Fentrust> fentrusts1 = null ;
		fentrusts1 = this.frontTradeService.findFentrustHistory(
				userid, symbol,null, 0, Comm.getMAX_NUMBER(), " fCreateTime desc ", new int[]{EntrustStatusEnum.Going,EntrustStatusEnum.PartDeal}) ;
		/*fentrusts1 = this.frontTradeService.findFentrustHistory1(
				userid, symbol,null, 0, Comm.getMAX_NUMBER(), " fid desc ", new int[]{EntrustStatusEnum.Going,EntrustStatusEnum.PartDeal}) ;*/
	
		Object[] ses;
		int sesCount=0;
		try {
			ses = this.realTimeData.getEntrustSuccessMap(fvirtualcointype.getFid()).toArray();
			int i=0;
			if(ses.length<Comm.getMAX_NUMBERS())
			{
				sesCount=ses.length;
			}else{
				sesCount=Comm.getMAX_NUMBERS();
			}
			Object[] successEntrusts=new Object[sesCount];
			for (Object object : ses) {

				if(i<sesCount)
				{
					successEntrusts[i]=object;
					i++;
				}
			}
			modelAndView.addObject("successEntrusts", successEntrusts) ;
		} catch (Exception e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		}
		
		
		modelAndView.addObject("fvirtualcointype", fvirtualcointype) ;
		modelAndView.addObject("tradeType", tradeType) ;
		modelAndView.addObject("symbol",symbol) ;
		modelAndView.addObject("type", type) ;
		modelAndView.addObject("fentrusts1", fentrusts1) ;
//		modelAndView.addObject("fentrusts2", fentrusts2) ;
		modelAndView.setViewName("front/trade/entrust_info") ;
		return modelAndView ;
	}
//	
//	@RequestMapping("/trade/subscriptionrmb")
//	public ModelAndView subscriptionRMB(
//			HttpServletRequest request,
//			@RequestParam(required=false,defaultValue="0")int id
//			) throws Exception {
//		ModelAndView modelAndView = new ModelAndView() ;
//		Fuser fuser = this.frontUserService.findById(GetSession(request).getFid()) ;
//		String filter = "where fisopen=1 and ftype="+SubscriptionTypeEnum.RMB;
//		List<Fsubscription> fsubscriptions = this.subscriptionService.list(0, 0, filter, false);
//		if(fsubscriptions.size()==0){
//			modelAndView.setViewName("redirect:/") ;
//			return modelAndView ;
//		}
//		
//		Fsubscription fsubscription = this.frontTradeService.findFsubscriptionById(id) ;
//		if(fsubscription==null){
//			fsubscription = this.frontTradeService.findFirstSubscription(SubscriptionTypeEnum.RMB) ;
//			if(fsubscription==null){
//				modelAndView.setViewName("redirect:/") ;
//				return modelAndView ;
//			}
//		}
//		
//		int begin = 0 ;
//		long now = Utils.getTimestamp().getTime() ;
//		if(fsubscription.getFbeginTime().getTime()>now){
//			//没开始
//			begin = 0 ;
//		}
//		
//		if(fsubscription.getFbeginTime().getTime()<now && fsubscription.getFendTime().getTime()>now){
//			//进行中
//			begin = 1 ;
//		}
//		
//		if(fsubscription.getFendTime().getTime()<now){
//			//结束
//			begin = 2 ;
//		}
//		
//		//认购记录
//		List<Fsubscriptionlog> fsubscriptionlogs = this.frontTradeService.findFsubScriptionLog(fuser, fsubscription.getFid()) ;
//
//		//可购买数量
//		int buyCount = fsubscription.getFbuyCount() ;
//		
//		if(fsubscriptionlogs.size() >0){
//			for (int i = 0; i < fsubscriptionlogs.size(); i++) {
//				buyCount -=fsubscriptionlogs.get(i).getFcount() ;
//			}
//		}
//
//		buyCount = buyCount<0?0:buyCount ;
//		
//		//可购买次数
//		int buyTimes = fsubscription.getFbuyTimes()-fsubscriptionlogs.size() ;
//		buyTimes = buyTimes<0?0:buyTimes ;
//		
//		modelAndView.addObject("buyCount", buyCount) ;
//		modelAndView.addObject("buyTimes", buyTimes) ;
//		modelAndView.addObject("begin", begin) ;
//		modelAndView.addObject("fid", fsubscription.getFid()) ;
//		modelAndView.addObject("fsubscriptionlogs", fsubscriptionlogs) ;
//		modelAndView.addObject("fsubscription", fsubscription) ;
//		modelAndView.addObject("fsubscriptions", fsubscriptions) ;
//		modelAndView.setViewName("front/trade/subscriptionrmb") ;
//		return modelAndView ;
//	}
	/*
	@RequestMapping("/trade/subscriptioncoin")
	public ModelAndView subscriptionCOIN(
			@RequestParam(required=false,defaultValue="0")int id
			) throws Exception {
		ModelAndView modelAndView = new ModelAndView() ;
		Fuser fuser = this.frontUserService.findById(GetSession(request).getFid()) ;
		String filter = "where fisopen=1 and ftype="+SubscriptionTypeEnum.COIN;
		List<Fsubscription> fsubscriptions = this.subscriptionService.list(0, 0, filter, false);
		if(fsubscriptions.size()==0){
			modelAndView.setViewName("redirect:/") ;
			return modelAndView ;
		}
		
		Fsubscription fsubscription = this.frontTradeService.findFsubscriptionById(id) ;
		if(fsubscription==null){
			fsubscription = this.frontTradeService.findFirstSubscription(SubscriptionTypeEnum.COIN) ;
			if(fsubscription==null){
				modelAndView.setViewName("redirect:/") ;
				return modelAndView ;
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
		
		//认购记录
		List<Fsubscriptionlog> fsubscriptionlogs = this.frontTradeService.findFsubScriptionLog(fuser, fsubscription.getFid()) ;

		//可购买数量
		int buyCount = fsubscription.getFbuyCount() ;
		for (int i = 0; i < fsubscriptionlogs.size(); i++) {
			buyCount -=fsubscriptionlogs.get(i).getFcount() ;
		}
		buyCount = buyCount<0?0:buyCount ;

		//可购买次数
		int buyTimes = fsubscription.getFbuyTimes()-fsubscriptionlogs.size() ;
		buyTimes = buyTimes<0?0:buyTimes ;
		
		modelAndView.addObject("buyCount", buyCount) ;
		modelAndView.addObject("buyTimes", buyTimes) ;
		modelAndView.addObject("begin", begin) ;
		modelAndView.addObject("fid", fsubscription.getFid()) ;
		modelAndView.addObject("fsubscriptionlogs", fsubscriptionlogs) ;
		modelAndView.addObject("fsubscription", fsubscription) ;
		modelAndView.addObject("fsubscriptions", fsubscriptions) ;
		modelAndView.setViewName("front/trade/subscriptioncoin") ;
		return modelAndView ;
	}
	*/
	@RequestMapping("/about/index1")
	public ModelAndView index1(
			@RequestParam(required=false,defaultValue="1")String id,
			@RequestParam(required=false,defaultValue="0")String symbol
			) throws Exception{
		ModelAndView modelAndView = new ModelAndView() ;
		String currentPage="1";
		int pageSize = Comm.getPAGE_NUM();
		int limitNum = (Integer.valueOf(currentPage)-1)*pageSize;
		String filter1 = " and fisShare=1";
		String sql = "select count(*) from Fvirtualcointype where fstatus=1 "+filter1;
		int count = this.frontVirtualCoinService.findCount(sql);
		String method="ajaxPage5";
		String pagin = this.generatePaginX(count/pageSize+( (count%pageSize)==0?0:1), Integer.valueOf(currentPage),method) ;
		List<Fvirtualcointype> fList = this.frontVirtualCoinService.findByParam(limitNum, pageSize, filter1);
		Fabout fabout = this.frontOthersService.findFabout(id) ;
		if(fabout == null){
			modelAndView.setViewName("redirect:/") ;
			return modelAndView;
		}
		
		List<KeyValues> abouts = new ArrayList<KeyValues>() ;
		String[] args = this.constantMap.get("helpType").toString().split("#");
		for (int i = 0; i < args.length; i++) {
			KeyValues keyValues = new KeyValues() ;
			keyValues.setKey(i) ;
			keyValues.setName(args[i]);
			String filter = "where ftype='"+args[i]+"'";
			keyValues.setValue(this.aboutService.list(0, 0, filter, false)) ;
			abouts.add(keyValues) ;
		}
		
		Fvirtualcointype fvirtualcointype = this.frontVirtualCoinService.findFvirtualCoinById(symbol) ;
		modelAndView.addObject("fList", fList);
		modelAndView.addObject("pagins", pagin);
		modelAndView.addObject("cur_page", currentPage) ;
		modelAndView.addObject("abouts", abouts) ;
		modelAndView.addObject("fabout", fabout) ;
		modelAndView.addObject("fvirtualcointype", fvirtualcointype) ;
		modelAndView.setViewName("front/trade/introduce") ;
		return modelAndView ;
	}
	@ResponseBody
	@RequestMapping("/about/index2")
	public String getCoinType3(
			HttpServletRequest request,
			@RequestParam(required=false,defaultValue="1")String currentPage,
			@RequestParam(required=false,defaultValue="")String hidlog
			){
		JSONObject js = new JSONObject();
		String result ="";
		int pageSize = Comm.getPAGE_NUM();
		int count = 0;
		String filter1 = " and fisShare=1";
		String sql;
		List<Fvirtualcointype> fList;
		int limitNum = (Integer.valueOf(currentPage)-1)*pageSize;
		if("".equals(hidlog) || hidlog.equals(null)){
			
		
		fList = this.frontVirtualCoinService.findByParam(limitNum, pageSize, filter1);
		
		sql = "select count(*) from Fvirtualcointype  where fstatus=1 "+filter1;
		} else{
			String filter = " where fstatus=1 and "+filter1+" fname like '%"+hidlog+"%'";
			String className = "Fvirtualcointype";
			fList = this.frontVirtualCoinService.findByParam1(limitNum, pageSize, filter, true, className);
			sql = "select count(*) from Fvirtualcointype where fstatus=1 and "+filter1+" fname like '%"+hidlog+"%'";
			
		}
		count = this.frontVirtualCoinService.findCount(sql);
		for (Fvirtualcointype fvirtualcointype : fList) {
			
			String fa = "<div class='col-xs-4' style='line-height:40px;'><a href='/trade/coin.html?coinType="+fvirtualcointype.getFid()+"'"+"><i class='lefticon col-xs-2' style='margin-right:5px;top:5px;width:20px;height:30px;background-size:100%;background-image: url("+fvirtualcointype.getFurl()+")'></i><span>&nbsp;"+fvirtualcointype.getFname()+"</span></a></div>";
			
			result = result + fa;
			}
		String method="ajaxPage5";
		String pagin = this.generatePaginX(count/pageSize+( (count%pageSize)==0?0:1), Integer.valueOf(currentPage),method) ;
		js.accumulate("page", pagin);
		js.accumulate("result", result);
		return js.toString();
	}
}
