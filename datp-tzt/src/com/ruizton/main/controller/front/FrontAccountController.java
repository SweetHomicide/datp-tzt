package com.ruizton.main.controller.front;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.HtmlUtils;

import com.ruizton.main.Enum.BankTypeEnum;
import com.ruizton.main.Enum.CapitalOperationTypeEnum;
import com.ruizton.main.Enum.EntrustTypeEnum;
import com.ruizton.main.Enum.RemittanceTypeEnum;
import com.ruizton.main.Enum.SubscriptionTypeEnum;
import com.ruizton.main.Enum.TradeRecordTypeEnum;
import com.ruizton.main.Enum.VirtualCapitalOperationTypeEnum;
import com.ruizton.main.Enum.VirtualCoinTypeStatusEnum;
import com.ruizton.main.comm.ConstantMap;
import com.ruizton.main.comm.KeyValues;
import com.ruizton.main.controller.BaseController;
import com.ruizton.main.model.Fasset;
import com.ruizton.main.model.FbankinfoWithdraw;
import com.ruizton.main.model.Fcapitaloperation;
import com.ruizton.main.model.Fsubscription;
import com.ruizton.main.model.Fuser;
import com.ruizton.main.model.Fvirtualaddress;
import com.ruizton.main.model.FvirtualaddressWithdraw;
import com.ruizton.main.model.Fvirtualcaptualoperation;
import com.ruizton.main.model.Fvirtualcointype;
import com.ruizton.main.model.Fvirtualwallet;
import com.ruizton.main.model.Systembankinfo;
import com.ruizton.main.service.admin.AdminService;
import com.ruizton.main.service.admin.CapitaloperationService;
import com.ruizton.main.service.admin.SubscriptionService;
import com.ruizton.main.service.admin.SystemArgsService;
import com.ruizton.main.service.admin.TransportlogService;
import com.ruizton.main.service.admin.VirtualCoinService;
import com.ruizton.main.service.front.FrontAccountService;
import com.ruizton.main.service.front.FrontBankInfoService;
import com.ruizton.main.service.front.FrontTradeService;
import com.ruizton.main.service.front.FrontUserService;
import com.ruizton.main.service.front.FrontVirtualCoinService;
import com.ruizton.main.service.front.UtilsService;
import com.ruizton.util.Comm;
import com.ruizton.util.Constant;
import com.ruizton.util.Utils;

import net.sf.json.JSONObject;

@Controller
public class FrontAccountController extends BaseController{
	
	@Autowired
	private FrontBankInfoService frontBankInfoService ;
	@Autowired
	private FrontAccountService frontAccountService ;
	@Autowired
	private FrontUserService frontUserService ;
	@Autowired
	private FrontVirtualCoinService frontVirtualCoinService ;
	@Autowired
	private FrontTradeService frontTradeService ;
	@Autowired
	private SystemArgsService systemArgsService ;
	@Autowired
	private AdminService adminService;
	@Autowired
	private CapitaloperationService capitaloperationService;
	@Autowired
	private VirtualCoinService virtualCoinService;
	@Autowired
	private TransportlogService transportlogService;
	@Autowired
	private ConstantMap constantMap;
	@Autowired
	private UtilsService utilsService ;
	@Autowired
	private SubscriptionService subscriptionService;

	@RequestMapping("account/rechargeCny")
	public ModelAndView rechargeCny(
			HttpServletRequest request,
			@RequestParam(required=false,defaultValue="")String currentPage,
			@RequestParam(required=false,defaultValue="0")int type,
			@RequestParam(required=false,defaultValue="")String fviId
			) throws Exception{
		int cur = 1 ;
		if(currentPage==null||"".equals(currentPage)){
			cur = 1; 
		}else{
			cur = Integer.parseInt(currentPage) ;
		}
		
		if(type !=0 && /*type !=2 &&*/ type !=3/* && type !=4*/){
			type =0;
		}
		
		ModelAndView modelAndView = new ModelAndView() ;
		//人民币随机尾数
		//int randomMoney = (new Random().nextInt(80)+11) ;
		//系统银行账号
		List<Systembankinfo> systembankinfos = this.frontBankInfoService.findAllSystemBankInfo() ;
		//modelAndView.addObject("randomMoney",randomMoney) ;
		modelAndView.addObject("bankInfo",systembankinfos) ;
		
		//record
		Fuser fuser = this.GetSession(request) ;
		StringBuffer filter = new StringBuffer();
		filter.append("where fuser.fid='"+fuser.getFid()+"' \n");
		filter.append("and ftype ="+CapitalOperationTypeEnum.RMB_IN+"\n");
		if(type ==0){
			filter.append("and fremittanceType='"+RemittanceTypeEnum.getEnumString(type)+"' \n");
		}else{
			filter.append("and systembankinfo is not null \n");
		}
		if ("".equals(fviId)) {
			filter.append(" and fviType is null  order by fcreateTime desc \n");
		}else{
			filter.append(" and fviType = '"+fviId+"'  order by fcreateTime desc \n");
		}
		
		List<Fcapitaloperation> list = this.capitaloperationService.list((cur-1)*Constant.AppRecordPerPage, Constant.RecordPerPage, filter.toString(), true);
		
		int totalCount = this.adminService.getAllCount("Fcapitaloperation", filter.toString());
		String url = "/account/rechargeCny.html?type="+type+"&";
		String pagin = this.generatePagin(totalCount/Constant.RecordPerPage+( (totalCount%Constant.RecordPerPage)==0?0:1), cur,url) ;
		
		//最小充值金额
		double minRecharge = Double.parseDouble(this.constantMap.get("minrechargecny").toString().trim()) ;
		modelAndView.addObject("minRecharge", minRecharge) ;
		
		Map<Integer,String> bankTypes = new HashMap<Integer,String>() ;
		for(int i=1;i<=BankTypeEnum.QT;i++){
			if(BankTypeEnum.getEnumString(i) != null && BankTypeEnum.getEnumString(i).trim().length() >0){
				bankTypes.put(i,BankTypeEnum.getEnumString(i)) ;
			}			
		}
		//可以充值的数字资产
		List<Fsubscription> listFsubscription=getRechargeTypeList();
		modelAndView.addObject("listRechargeType", listFsubscription);	//可以充值的数字资产
		modelAndView.addObject("bankTypes", bankTypes) ;		
		modelAndView.addObject("list", list) ;
		modelAndView.addObject("pagin", pagin) ;
		modelAndView.addObject("cur_page", cur) ;
		modelAndView.addObject("fuser",GetSession(request)) ;
		modelAndView.addObject("type", type) ;
		modelAndView.addObject("selectType", fviId) ;
		modelAndView.setViewName("front/account/account_rechargecny"+type) ;
		return modelAndView ;
	}
	
	
	@RequestMapping("account/withdrawCny")
	public ModelAndView withdrawCny(
			@RequestParam(required=false,defaultValue="")String currentPage,
			HttpServletRequest request
			) throws Exception{
		ModelAndView modelAndView = new ModelAndView() ;
		int cur = 1 ;
		if(currentPage==null||"".equals(currentPage)){
			cur = 1; 
		}else{
			cur = Integer.parseInt(currentPage) ;
		}
		
		Fuser fuser = this.frontUserService.findById4WithDraw(GetSession(request).getFid()) ;
		String filter = "where fuser.fid='"+fuser.getFid()+"' and fbankType >0";
		List<FbankinfoWithdraw> fbankinfoWithdraws =this.frontUserService.findFbankinfoWithdrawByFuser(0, 0, filter, false);
		for (FbankinfoWithdraw fbankinfoWithdraw : fbankinfoWithdraws) {
			int l = fbankinfoWithdraw.getFbankNumber().length();
			fbankinfoWithdraw.setFbankNumber(fbankinfoWithdraw.getFbankNumber().substring(l-4, l));
		}
		
		Map<Integer,String> bankTypes = new HashMap<Integer,String>() ;
		for(int i=1;i<=BankTypeEnum.QT;i++){
			if(BankTypeEnum.getEnumString(i) != null && BankTypeEnum.getEnumString(i).trim().length() >0){
				bankTypes.put(i,BankTypeEnum.getEnumString(i)) ;
			}			
		}
		modelAndView.addObject("bankTypes", bankTypes) ;
		
		double fee = this.frontAccountService.findWithdrawFeesByLevel(fuser.getFscore().getFlevel());
		modelAndView.addObject("fee", fee) ;
		
		//獲得千12條交易記錄
		String param = "where fuser.fid='"+fuser.getFid()+"' and ftype="+CapitalOperationTypeEnum.RMB_OUT+" order by fCreateTime desc";
		List<Fcapitaloperation> fcapitaloperations = this.frontAccountService.findCapitalList((cur-1)*Constant.RecordPerPage, Constant.RecordPerPage, param, true) ;
		int totalCount = this.adminService.getAllCount("Fcapitaloperation", param.toString());
		String url = "/account/withdrawCny.html?";
		String pagin = this.generatePagin(totalCount/Constant.RecordPerPage+( (totalCount%Constant.RecordPerPage)==0?0:1), cur,url) ;
		int MAX_Withdrawals_Time=Comm.times();
		boolean isBindGoogle = fuser.getFgoogleBind() ;
		boolean isBindTelephone = fuser.isFisTelephoneBind() ;
		//可以充值的数字资产
		List<Fsubscription> listFsubscription=getRechargeTypeList();
		modelAndView.addObject("listRechargeType", listFsubscription);	//可以充值的数字资产
		modelAndView.addObject("selectType", "") ;
		modelAndView.addObject("isBindGoogle", isBindGoogle) ;
        modelAndView.addObject("isBindTelephone", isBindTelephone) ;
        modelAndView.addObject("MAX_Withdrawals_Time", MAX_Withdrawals_Time) ;
		modelAndView.addObject("pagin", pagin) ;
		modelAndView.addObject("fcapitaloperations", fcapitaloperations) ;
		modelAndView.addObject("fuser",fuser) ;
		modelAndView.addObject("fbankinfoWithdraws",fbankinfoWithdraws) ;
		modelAndView.setViewName("front/account/account_withdrawcny") ;
		return modelAndView ;
	}
	
	@RequestMapping("account/withdrawbtcToCny")
	public ModelAndView withdrawCny1(
			@RequestParam(required=false,defaultValue="")String currentPage,
			HttpServletRequest request
			) throws Exception{
		ModelAndView modelAndView = new ModelAndView() ;
		int cur = 1 ;
		if(currentPage==null||"".equals(currentPage)){
			cur = 1; 
		}else{
			cur = Integer.parseInt(currentPage) ;
		}
		String btcId = request.getParameter("VirtualId");
		//获取可兑换rmb种类
		String filter1 = "where fisRMB=1 and ftype=" + SubscriptionTypeEnum.COIN +"  order by fcreateTime desc";
		List<Fsubscription> fsubscription1 = this.subscriptionService.list(0, 0, filter1, false);
		modelAndView.addObject("fsubscription1", fsubscription1);
		
		if("".equals(btcId)){
			 btcId = fsubscription1.get(0).getFvirtualcointype().getFid();
		}
		String filter2 = "where fisRMB=1 and ftype=" + SubscriptionTypeEnum.COIN +" and fvirtualcointype.fid='"+btcId+"' order by fcreateTime desc";;
		List<Fsubscription> list = this.subscriptionService.list(0, 0, filter2, false);
		Fsubscription fsubscription = list.get(0);
		//获取兑换比例
		modelAndView.addObject("fsubscription", fsubscription);
		Fuser fuser = this.frontUserService.findById4WithDraw(GetSession(request).getFid()) ;
		//获取钱包账户
		Fvirtualwallet fvirtualwallet = this.frontUserService.findVirtualWalletByUser(fuser.getFid(), btcId) ;
		modelAndView.addObject("fvirtualwallet", fvirtualwallet);
		//获取银行卡信息
		String filter = "where fuser.fid='"+fuser.getFid()+"' and fbankType >0";
		List<FbankinfoWithdraw> fbankinfoWithdraws =this.frontUserService.findFbankinfoWithdrawByFuser(0, 0, filter, false);
		for (FbankinfoWithdraw fbankinfoWithdraw : fbankinfoWithdraws) {
			int l = fbankinfoWithdraw.getFbankNumber().length();
			fbankinfoWithdraw.setFbankNumber(fbankinfoWithdraw.getFbankNumber().substring(l-4, l));
		}
		
		Map<Integer,String> bankTypes = new HashMap<Integer,String>() ;
		for(int i=1;i<=BankTypeEnum.QT;i++){
			if(BankTypeEnum.getEnumString(i) != null && BankTypeEnum.getEnumString(i).trim().length() >0){
				bankTypes.put(i,BankTypeEnum.getEnumString(i)) ;
			}			
		}
		modelAndView.addObject("bankTypes", bankTypes) ;
		
		double fee = this.frontAccountService.findWithdrawFeesByLevel(fuser.getFscore().getFlevel());
		modelAndView.addObject("fee", fee) ;
		
		//獲得千12條交易記錄
		String param = "where fuser.fid='"+fuser.getFid()+"' and ftype=4 and fviType.fid='"+btcId+"' order by fCreateTime desc";
		List<Fcapitaloperation> fcapitaloperations = this.frontAccountService.findCapitalList((cur-1)*Constant.RecordPerPage, Constant.RecordPerPage, param, true) ;
		int totalCount = this.adminService.getAllCount("Fcapitaloperation", param.toString());
		String url = "/account/withdrawCny.html?";
		String pagin = this.generatePagin(totalCount/Constant.RecordPerPage+( (totalCount%Constant.RecordPerPage)==0?0:1), cur,url) ;
		int MAX_Withdrawals_Time=Comm.times();
		boolean isBindGoogle = fuser.getFgoogleBind() ;
		boolean isBindTelephone = fuser.isFisTelephoneBind() ;
		//可以充值的数字资产
		String selectType=fvirtualwallet.getFvirtualcointype().getFid();
		List<Fsubscription> listFsubscription=getRechargeTypeList();
		modelAndView.addObject("selectType", selectType) ;
		modelAndView.addObject("listRechargeType", listFsubscription);	//可以充值的数字资产
		modelAndView.addObject("isBindGoogle", isBindGoogle) ;
        modelAndView.addObject("isBindTelephone", isBindTelephone) ;
        modelAndView.addObject("MAX_Withdrawals_Time", MAX_Withdrawals_Time) ;
		modelAndView.addObject("pagin", pagin) ;
		modelAndView.addObject("fcapitaloperations", fcapitaloperations) ;
		modelAndView.addObject("fuser",fuser) ;
		modelAndView.addObject("fbankinfoWithdraws",fbankinfoWithdraws) ;
		modelAndView.setViewName("front/account/account_withdrawcny") ;
		return modelAndView ;
	}
	@RequestMapping("account/rechargeBtc")
	public ModelAndView rechargeBtc(
			HttpServletRequest request,
			@RequestParam(required=false,defaultValue="")String currentPage,
			@RequestParam(required=false,defaultValue="1")String currentPage1,
			@RequestParam(required=false,defaultValue="0")String symbol
			) throws Exception{
		int pageSize = Comm.getPAGE_NUM();
		int count = 0;
		String sql;
		List<Fvirtualcointype> fList;
		int limitNum = (Integer.valueOf(currentPage1)-1)*pageSize;
		String filter1 = " and FIsWithDraw = 1 ";
		fList = this.frontVirtualCoinService.findByParam(limitNum, pageSize, filter1);
		sql = "select count(*) from Fvirtualcointype where fstatus=1 "+filter1;
		count = this.frontVirtualCoinService.findCount(sql);
		String pagin1 = this.generatePagin2(count/pageSize+( (count%pageSize)==0?0:1), Integer.valueOf(currentPage1)) ;
		
		ModelAndView modelAndView = new ModelAndView() ;
		modelAndView.addObject("fList", fList);
		modelAndView.addObject("page", pagin1);
		int cur = 1 ;
		if(currentPage==null||"".equals(currentPage)){
			cur = 1; 
		}else{
			cur = Integer.parseInt(currentPage) ;
		}
		Fuser fuser = this.frontUserService.findById(GetSession(request).getFid()) ;
		
		Fvirtualcointype fvirtualcointype = this.frontVirtualCoinService.findFvirtualCoinById(symbol) ;
		if(fvirtualcointype==null 
				|| fvirtualcointype.getFstatus()==VirtualCoinTypeStatusEnum.Abnormal 
				|| !fvirtualcointype.isFIsWithDraw()){
			String filter = "where fstatus=1 and FIsWithDraw=1 order by fid asc";
			List<Fvirtualcointype> alls = this.virtualCoinService.list(0, 1, filter, true);
			if(alls==null || alls.size() ==0){
				modelAndView.setViewName("redirect:/") ;
				return modelAndView ;
			}
			fvirtualcointype = alls.get(0);
			symbol = fvirtualcointype.getFid();
		}
		Fvirtualaddress fvirtualaddress = this.frontVirtualCoinService.findFvirtualaddress(fuser, fvirtualcointype) ;
		
		//最近十次充值记录
		String filter ="where fuser.fid='"+fuser.getFid()+"' and ftype="+VirtualCapitalOperationTypeEnum.COIN_IN
				+" and fvirtualcointype.fid='"+fvirtualcointype.getFid()+"' order by fCreateTime desc";
		List<Fvirtualcaptualoperation> fvirtualcaptualoperations = this.frontVirtualCoinService.findFvirtualcaptualoperation((cur-1)*Constant.RecordPerPage, Constant.RecordPerPage, filter, true);
		int totalCount = this.adminService.getAllCount("Fvirtualcaptualoperation", filter.toString());
		String url = "/account/rechargeBtc.html?symbol="+symbol+"&";
		String pagin = this.generatePagin(totalCount/Constant.RecordPerPage+( (totalCount%Constant.RecordPerPage)==0?0:1), cur,url) ;
		
		modelAndView.addObject("pagin", pagin) ;
		modelAndView.addObject("fvirtualcaptualoperations",fvirtualcaptualoperations) ;
		modelAndView.addObject("fvirtualcointype",fvirtualcointype) ;
		modelAndView.addObject("symbol", symbol) ;
		modelAndView.addObject("fvirtualaddress", fvirtualaddress) ;
		modelAndView.addObject("virConfirs", Comm.getVIR_CONFIRS()) ;
		modelAndView.setViewName("front/account/account_rechargebtc") ;
		return modelAndView ;
	}
	
	
	
	@RequestMapping("account/withdrawBtc")
	public ModelAndView withdrawBtc(
			HttpServletRequest request,
			@RequestParam(required=false,defaultValue="")String currentPage,
			@RequestParam(required=false,defaultValue="0")String symbol
			) throws Exception{
		String currentPage1 = "1";
		int pageSize = Comm.getPAGE_NUM();
		int count = 0;
		String sqlx1;
		List<Fvirtualcointype> fList;
		String filter1 = " and FIsWithDraw = 1 ";
		int limitNum = (Integer.valueOf(currentPage1)-1)*pageSize;
		fList = this.frontVirtualCoinService.findByParam(limitNum, pageSize, filter1);
		sqlx1 = "select count(*) from Fvirtualcointype where fstatus=1 "+filter1;
		count = this.frontVirtualCoinService.findCount(sqlx1);
		String pagin1 = this.generatePagin3(count/pageSize+( (count%pageSize)==0?0:1), Integer.valueOf(currentPage1)) ;
		ModelAndView modelAndView = new ModelAndView() ;
		modelAndView.addObject("fList", fList);
		modelAndView.addObject("page", pagin1);
		int cur = 1 ;
		if(currentPage==null||"".equals(currentPage)){
			cur = 1; 
		}else{
			cur = Integer.parseInt(currentPage) ;
		}
		Fuser fuser = this.frontUserService.findById(GetSession(request).getFid()) ;
		Fvirtualcointype fvirtualcointype = this.frontVirtualCoinService.findFvirtualCoinById(symbol) ;
		if(fvirtualcointype==null ||fvirtualcointype.getFstatus()==VirtualCoinTypeStatusEnum.Abnormal
				 || !fvirtualcointype.isFIsWithDraw()){
			String filter = "where fstatus=1 and FIsWithDraw=1 order by fid asc";
			List<Fvirtualcointype> alls = this.virtualCoinService.list(0, 1, filter, true);
			if(alls==null || alls.size() ==0){
				modelAndView.setViewName("redirect:/") ;
				return modelAndView ;
			}
			fvirtualcointype = alls.get(0);
			symbol = fvirtualcointype.getFid();
		}
		
		
		Fvirtualwallet fvirtualwallet = this.frontUserService.findVirtualWalletByUser(fuser.getFid(), fvirtualcointype.getFid()) ;
		String sql ="where fuser.fid='"+fuser.getFid()+"' and fvirtualcointype.fid='"+symbol+"'";
		List<FvirtualaddressWithdraw> fvirtualaddressWithdraws = this.frontVirtualCoinService.findFvirtualaddressWithdraws(0, 0, sql, false);
		
		//近10条提现记录
		String filter ="where fuser.fid='"+fuser.getFid()+"' and ftype="+VirtualCapitalOperationTypeEnum.COIN_OUT
				+" and fvirtualcointype.fid='"+fvirtualcointype.getFid()+"' order by fCreateTime desc";
		List<Fvirtualcaptualoperation> fvirtualcaptualoperations = this.frontVirtualCoinService.findFvirtualcaptualoperation((cur-1)*Constant.RecordPerPage, Constant.RecordPerPage, filter, true);
		int totalCount = this.adminService.getAllCount("Fvirtualcaptualoperation", filter.toString());
		String url = "/account/withdrawBtc.html?symbol="+symbol+"&";
		String pagin = this.generatePagin(totalCount/Constant.RecordPerPage+( (totalCount%Constant.RecordPerPage)==0?0:1), cur,url) ;
		
		modelAndView.addObject("pagin", pagin) ;
		
		int isEmptyAuth = 0;
		if(fuser.isFisTelephoneBind() || fuser.getFgoogleBind()){
			isEmptyAuth = 1;
		}
		modelAndView.addObject("isEmptyAuth",isEmptyAuth) ;
		
		boolean isBindGoogle = fuser.getFgoogleBind() ;
		boolean isBindTelephone = fuser.isFisTelephoneBind() ;
		modelAndView.addObject("isBindGoogle", isBindGoogle) ;
        modelAndView.addObject("isBindTelephone", isBindTelephone) ;
		
		modelAndView.addObject("symbol",symbol) ;
		modelAndView.addObject("fvirtualcaptualoperations", fvirtualcaptualoperations) ;
		modelAndView.addObject("fvirtualwallet",fvirtualwallet) ;
		modelAndView.addObject("fuser",fuser) ;
		modelAndView.addObject("fvirtualaddressWithdraws", fvirtualaddressWithdraws) ;
		modelAndView.addObject("fvirtualcointype",fvirtualcointype) ;
		modelAndView.setViewName("front/account/account_withdrawbtc") ;
		return modelAndView ;
	}
	@ResponseBody
	@RequestMapping(value="/account/recordCointype",produces=JsonEncode)
	public String recordCointype(
			@RequestParam(required=false,defaultValue="1")int currentPage,
			@RequestParam(required=false,defaultValue="")String hidlog
			)throws Exception{
		int pageSize = Comm.getPAGE_NUM();
		int limitNum = (Integer.valueOf(currentPage)-1)*pageSize;
		List<Fvirtualcointype> fList;
		String sql;
		int count = 0;
		String result ="";
		JSONObject js = new JSONObject();
		if("".equals(hidlog) || hidlog.equals(null)){
			
		fList = this.frontVirtualCoinService.findByParam(limitNum, pageSize);
		
		sql = "select count(*) from Fvirtualcointype where fstatus="+VirtualCoinTypeStatusEnum.Normal;
		} else{
			String filter = " where fstatus="+VirtualCoinTypeStatusEnum.Normal+" and fname like '%"+hidlog+"%'";
			String className = "Fvirtualcointype";
			fList = this.frontVirtualCoinService.findByParam1(limitNum, pageSize, filter, true, className);
			sql = "select count(*) from Fvirtualcointype where fstatus="+VirtualCoinTypeStatusEnum.Normal+" and fname like '%"+hidlog+"%'";
			
		}
		count = this.frontVirtualCoinService.findCount(sql);
		String url = "ajaxPage6";
		String pagin = this.generatePaginX(count/Comm.getPAGE_NUM()+( (count%Comm.getPAGE_NUM())==0?0:1), currentPage,url);
		for (Fvirtualcointype fvirtualcointype : fList) {
			
			String fa = "<div class='col-xs-4' style='line-height:40px;'><a href='/account/record.html?isRMB=1&recordType=3&symbol="+fvirtualcointype.getFid()+"'"+"><i class='lefticon col-xs-2' style='margin-right:5px;top:5px;width:20px;height:30px;background-size:100%;background-image: url("+fvirtualcointype.getFurl()+")'></i><span>&nbsp;"+fvirtualcointype.getFname()+"</span></a></div>";
			
			result = result + fa;
		}
		js.accumulate("result", result);
		js.accumulate("page", pagin);
		return js.toString();
	}
	
	
	@RequestMapping("/account/record")
	public ModelAndView record(
			HttpServletRequest request,
			@RequestParam(required=false,defaultValue="1")int recordType,
			@RequestParam(required=false,defaultValue="0")String symbol,
			@RequestParam(required=false,defaultValue="1")int currentPage,
			@RequestParam(required=false,defaultValue="2")int datetype,
			@RequestParam(required=false,defaultValue="")String begindate,
			@RequestParam(required=false,defaultValue="")String enddate,
			@RequestParam(required=false,defaultValue="0")String isRMB
			) throws Exception{
		if(symbol.equals("0")&&isRMB.equals("1")){
			recordType = 3;
		}
		ModelAndView modelAndView = new ModelAndView() ;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		if(!(datetype >=1 && datetype <=4)){
			datetype =2;
		}
		if(enddate == null || enddate.trim().length() ==0){
			enddate = sdf.format(new Date());
		}else{
			try {
				enddate = sdf.format(sdf.parse(HtmlUtils.htmlEscape(enddate)));
			} catch (Exception e) {
				enddate = "";
			}
		}
		if(begindate == null || begindate.trim().length() ==0){
			switch (datetype) {
			case 1:
				begindate = sdf.format(new Date());
				break;
	        case 2:
	        	begindate = Utils.getAfterDay(7);
				break;
	        case 3:
	        	begindate = Utils.getAfterDay(15);
	    	    break;
	        case 4:
	        	begindate = Utils.getAfterDay(30);
		       break;
			}
		}else{
			try {
				begindate = sdf.format(sdf.parse(HtmlUtils.htmlEscape(begindate)));
			} catch (Exception e) {
				begindate = "";
			}
		}
		
		
		
		Fvirtualcointype fvirtualcointype = this.frontVirtualCoinService.findFvirtualCoinById(symbol) ;
		if(fvirtualcointype==null){
			fvirtualcointype = this.frontVirtualCoinService.findFirstFirtualCoin(0) ;
			symbol = fvirtualcointype.getFid() ;
		}
		
		if(recordType>TradeRecordTypeEnum.BTC_SELL){
			recordType = TradeRecordTypeEnum.BTC_SELL ;
		}
		
		List<Fvirtualcointype> fvirtualcointypes = (List)this.constantMap.get("virtualCoinType");
		
		if(recordType!=1 && recordType!=2){
			int cur = 1;
			String result = "";
			int count = fvirtualcointypes.size();
			int pageSize = Comm.getPAGE_NUM();
			String filter = "where fstatus=1";
			String className = "Fvirtualcointype";
			List<Fvirtualcointype> fList = this.frontVirtualCoinService.findByParam1(0, pageSize, filter, true, className);
			String url = "ajaxPage6";
			String page = this.generatePaginX(count/Comm.getPAGE_NUM()+( (count%Comm.getPAGE_NUM())==0?0:1), cur,url);
			
			
			modelAndView.addObject("result1", fList);
			modelAndView.addObject("page", page);
		}
		//过滤器
		List<KeyValues> filters = new ArrayList<KeyValues>() ;
		List<KeyValues> filters1 = new ArrayList<KeyValues>() ;
		for (int i = 1; i <= TradeRecordTypeEnum.BTC_SELL; i++) {
			if(i==1 || i==2){
				KeyValues keyValues = new KeyValues() ;
				String key = "/account/record.html?recordType="+i+"&symbol=0" ;
				String value = TradeRecordTypeEnum.getEnumString(i) ;
				keyValues.setKey(key) ;
				keyValues.setValue(value) ;
				filters1.add(keyValues) ;
			}else{
				/*String key = "/account/record.html?recordType="+i+"&symbol=" ;
				for (int j = 0; j < fvirtualcointypes.size(); j++) {
					String value = TradeRecordTypeEnum.getEnumString(i) ;
					Fvirtualcointype vc = fvirtualcointypes.get(j) ;
					
					if(i==TradeRecordTypeEnum.BTC_RECHARGE || i==TradeRecordTypeEnum.BTC_WITHDRAW){
						if(!vc.isFIsWithDraw()){
							continue ;
						}
					}
					
					value = vc.getfShortName()+value ;
					KeyValues keyValues = new KeyValues() ;
					keyValues.setKey(key+vc.getFid()) ;
					keyValues.setValue(value) ;
					filters.add(keyValues) ;
				}*/
				if(Comm.getISHIDDEN_DEAL().equals("true")&&(i==5||i==6))
				{
					continue;
				}
				KeyValues keyValues = new KeyValues() ;
				String value = TradeRecordTypeEnum.getEnumString(i) ;
				keyValues.setKey(i) ;
				keyValues.setValue(value) ;
				filters.add(keyValues) ;
				
			}
		}
		
		//内容
		List list = new ArrayList() ;
		int totalCount = 0 ;
		String pagin = "" ;
		String param = "";
		Fuser fuser = this.frontUserService.findById(GetSession(request).getFid()) ;
		switch (recordType) {
		case TradeRecordTypeEnum.CNY_RECHARGE:
			param = "where fuser.fid='"+fuser.getFid()+"' and ftype="+CapitalOperationTypeEnum.RMB_IN
			+" and date_format(fcreatetime,'%Y-%m-%d')>='"+begindate+"'"+" and date_format(fcreatetime,'%Y-%m-%d')<='"+enddate+"' order by fLastUpdateTime desc";
			list = this.frontAccountService.findCapitalList((currentPage-1)*Constant.RecordPerPage, Constant.RecordPerPage, param,true) ;
			totalCount = this.adminService.getAllCount("Fcapitaloperation", param);
			break;
			
		case TradeRecordTypeEnum.CNY_WITHDRAW:
			param = "where fuser.fid='"+fuser.getFid()+"' and ftype="+CapitalOperationTypeEnum.RMB_OUT
					+" and date_format(fcreatetime,'%Y-%m-%d')>='"+begindate+"'"+" and date_format(fcreatetime,'%Y-%m-%d')<='"+enddate+"' order by fLastUpdateTime desc";
			list = this.frontAccountService.findCapitalList((currentPage-1)*Constant.RecordPerPage, Constant.RecordPerPage, param,true) ;
			totalCount = this.adminService.getAllCount("Fcapitaloperation", param);

			break;
		case TradeRecordTypeEnum.BTC_RECHARGE:
			param = "where fuser.fid='"+fuser.getFid()+"' and fvirtualcointype.fid='"+fvirtualcointype.getFid()+"' and ftype ="+VirtualCapitalOperationTypeEnum.COIN_IN
					+" and date_format(fcreatetime,'%Y-%m-%d')>='"+begindate+"'"+" and date_format(fcreatetime,'%Y-%m-%d')<='"+enddate+"' order by fLastUpdateTime desc";
			list =  this.frontVirtualCoinService.findFvirtualcaptualoperation((currentPage-1)*Constant.RecordPerPage, Constant.RecordPerPage,param,true) ;
			totalCount = this.adminService.getAllCount("Fvirtualcaptualoperation", param);
			
			break;
		case TradeRecordTypeEnum.BTC_WITHDRAW:
			param = "where fuser.fid='"+fuser.getFid()+"' and fvirtualcointype.fid='"+fvirtualcointype.getFid()+"' and ftype ="+VirtualCapitalOperationTypeEnum.COIN_OUT
			+" and date_format(fcreatetime,'%Y-%m-%d')>='"+begindate+"'"+" and date_format(fcreatetime,'%Y-%m-%d')<='"+enddate+"' order by fLastUpdateTime desc";
			list =  this.frontVirtualCoinService.findFvirtualcaptualoperation((currentPage-1)*Constant.RecordPerPage, Constant.RecordPerPage,param,true) ;
			totalCount = this.adminService.getAllCount("Fvirtualcaptualoperation", param);
			
			break;
		case TradeRecordTypeEnum.BTC_BUY:
			param = "where fuser.fid='"+fuser.getFid()+"' and fentrustType="+EntrustTypeEnum.BUY+" and fvirtualcointype.fid='"+fvirtualcointype.getFid()
					+"' and date_format(fcreatetime,'%Y-%m-%d')>='"+begindate+"'"+" and date_format(fcreatetime,'%Y-%m-%d')<='"+enddate+"' order by flastUpdatTime desc";
			list = this.frontTradeService.findFentrustHistory((currentPage-1)*Constant.RecordPerPage, Constant.RecordPerPage, param, true);
			totalCount = this.adminService.getAllCount("Fentrust", param);
			
			break;
		case TradeRecordTypeEnum.BTC_SELL:
			param = "where fuser.fid='"+fuser.getFid()+"' and fentrustType="+EntrustTypeEnum.SELL+" and fvirtualcointype.fid='"+fvirtualcointype.getFid()
			+"' and date_format(fcreatetime,'%Y-%m-%d')>='"+begindate+"'"+" and date_format(fcreatetime,'%Y-%m-%d')<='"+enddate+"' order by flastUpdatTime desc";
			list = this.frontTradeService.findFentrustHistory((currentPage-1)*Constant.RecordPerPage, Constant.RecordPerPage, param, true);
			totalCount = this.adminService.getAllCount("Fentrust", param);
			
			break;
		}
		
		String url = "/account/record.html?recordType="+recordType+"&symbol="+symbol+"&datetype="+datetype+"&begindate="+begindate+"&enddate="+enddate+"&";
		pagin = this.generatePagin(totalCount/Constant.RecordPerPage+( (totalCount%Constant.RecordPerPage)==0?0:1), currentPage,url) ;
		modelAndView.addObject("datetype", datetype) ;
		modelAndView.addObject("begindate", begindate) ;
		modelAndView.addObject("enddate", enddate) ;
		modelAndView.addObject("list", list) ;
		modelAndView.addObject("pagin",pagin) ;
		modelAndView.addObject("recordType",recordType ) ;
		modelAndView.addObject("symbol" ,symbol) ;
		modelAndView.addObject("filters1", filters1);
		modelAndView.addObject("filters", filters) ;
		modelAndView.addObject("fvirtualcointype", fvirtualcointype) ;
		modelAndView.addObject("fvirtualcointypes", fvirtualcointypes);
		modelAndView.addObject("select", TradeRecordTypeEnum.getEnumString(recordType));
		if(isRMB.equals("1")){
			modelAndView.setViewName("front/account/account_record_virucoin") ;
			modelAndView.addObject("selectEnum", fvirtualcointype.getfShortName()+TradeRecordTypeEnum.getEnumString(recordType));
		}else{
			
			modelAndView.setViewName("front/account/account_record") ;
		}
		return modelAndView ;
	}
	
	
	@RequestMapping(value="/account/refTenbody")
	public ModelAndView refTenbody(
			HttpServletRequest request,
			@RequestParam(required=false,defaultValue="1")String currentPage,
			@RequestParam(required=false,defaultValue="0")int type,
			@RequestParam(required=false,defaultValue="")String fviId
			) throws Exception{
		int cur = 1 ;
		if(currentPage==null || "".equals(currentPage)){
			cur = 1 ;
		}else{
			cur = Integer.parseInt(currentPage) ;
		}
		if(type !=0 && /*type !=2 &&*/ type !=3 && type !=4){
			type =0;
		}
		
		ModelAndView modelAndView = new ModelAndView() ;
		
		Fuser fuser = this.GetSession(request) ;
		StringBuffer filter = new StringBuffer();
		filter.append("where fuser.fid='"+fuser.getFid()+"' \n");
		filter.append("and ftype ="+CapitalOperationTypeEnum.RMB_IN+"\n");
		if(type !=0){
			filter.append("and fremittanceType='"+RemittanceTypeEnum.getEnumString(type)+"' \n");
		}else{
			filter.append("and fremittanceType='"+RemittanceTypeEnum.getEnumString(type)+"' \n");//systembankinfo is not null 
		}
		if ("".equals(fviId)) {
			filter.append(" and fviType is null  order by fcreateTime desc \n");
		}else{
			filter.append(" and fviType = '"+fviId+"'  order by fcreateTime desc \n");
		}
		List<Fcapitaloperation> list = this.capitaloperationService.list((cur-1)*Constant.AppRecordPerPage, Constant.RecordPerPage, filter.toString(), true);
		
		int totalCount = this.adminService.getAllCount("Fcapitaloperation", filter.toString());
		String url = "/account/rechargeCny.html?type="+type+"&";
		String pagin = this.generatePagin(totalCount/Constant.RecordPerPage+( (totalCount%Constant.RecordPerPage)==0?0:1), cur,url) ;
		
		modelAndView.addObject("list", list) ;
		modelAndView.addObject("pagin", pagin) ;
		modelAndView.addObject("cur_page", currentPage) ;
		modelAndView.setViewName("front/account/reftenbody") ;
		return modelAndView ;
	}
		
		@RequestMapping("financial/assetsrecord")
		public ModelAndView assetsrecord(
				HttpServletRequest request,
				@RequestParam(required=false,defaultValue="1")int currentPage,
				@RequestParam(required=false,defaultValue="0")int datetype,
				@RequestParam(required=false,defaultValue="")String begindate,
				@RequestParam(required=false,defaultValue="")String enddate
				)  throws Exception {
			ModelAndView modelAndView = new ModelAndView() ;
			Fuser fuser = this.frontUserService.findById(GetSession(request).getFid()) ;
			String lastdate = "";
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			if(datetype!=0 ){
				switch (datetype) {
				case 1:
					lastdate = sdf.format(new Date());
					break;
				case 2:
					lastdate = Utils.getAfterDay(7);
					break;
				case 3:
					lastdate = Utils.getAfterDay(15);
					break;
				case 4:
					lastdate = Utils.getAfterDay(30);
					break;
				}
				
			}
			
			
			
			List<Fvirtualcointype> fvirtualcointypes = (List<Fvirtualcointype>)this.constantMap.get("virtualCoinType");
			modelAndView.addObject("fvirtualcointypes", fvirtualcointypes) ;
			
			int maxResults = Constant.RecordPerPage ;
			int firstResult = (currentPage-1)*maxResults ;
			StringBuffer filter = new StringBuffer() ;
			filter.append(" where fuser.fid='"+fuser.getFid()+"' \t");
			if(!"".equals(lastdate)){
				filter.append(" and date_format(fcreatetime,'%Y-%m-%d')>='"+lastdate+"' \t");
			}
			if(!"".equals(begindate)){
				begindate = sdf.format(sdf.parse(HtmlUtils.htmlEscape(begindate)));
				filter.append(" and date_format(fcreatetime,'%Y-%m-%d')>='"+begindate+"' \t");
			}
			if(!"".equals(enddate)){
				enddate = sdf.format(sdf.parse(HtmlUtils.htmlEscape(enddate)));
				filter.append(" and date_format(fcreatetime,'%Y-%m-%d')<='"+enddate+"' \t");
			}
			filter.append( "and status=1 order by fLastUpdateTime desc");
			List<Fasset> list= this.utilsService.list(firstResult, maxResults,filter.toString(), true, Fasset.class) ;
			int total = this.utilsService.count(filter.toString(), Fasset.class) ;
			String pagin = generatePagin(total/maxResults+(total%maxResults==0?0:1), currentPage, "/financial/assetsrecord.html?begindate="+begindate+"&enddate="+enddate+"&datetype="+datetype+"&") ;
			modelAndView.addObject("list",list) ;
			modelAndView.addObject("pagin",pagin);
			modelAndView.addObject("begindate", begindate);
			modelAndView.addObject("enddate", enddate);
			modelAndView.addObject("datetype", datetype);
			//处理json
			for (Fasset fasset : list) {
				fasset.parseJson(fvirtualcointypes);
			}
			
			modelAndView.setViewName("front/financial/assetsrecord") ;
			return modelAndView ;
		}
		@RequestMapping("financial/assetsrecordvirual")
		public ModelAndView virulassetsrecord(
				HttpServletRequest request,
				@RequestParam(required=false,defaultValue="")String time,
				@RequestParam(required=false,defaultValue="1")int currentPage
				//@RequestParam(required=false,defaultValue="1")String symbol
				)  throws Exception {
			int limit_Page = Comm.getLIMIT_PAGE();
			int beginPage = (currentPage-1)*limit_Page;
			ModelAndView modelAndView = new ModelAndView() ;
			List<Fvirtualcointype> fvirtualcointypes = new ArrayList<Fvirtualcointype>();
			List<Fvirtualcointype> fvirtualcointypes1 = (List<Fvirtualcointype>)this.constantMap.get("virtualCoinType");
			if(beginPage+limit_Page>=fvirtualcointypes1.size()){
				for (int i = beginPage; i < fvirtualcointypes1.size(); i++) {
					fvirtualcointypes.add(fvirtualcointypes1.get(i));
				}
			}else{
				for (int i = beginPage; i < limit_Page+beginPage; i++) {
					fvirtualcointypes.add(fvirtualcointypes1.get(i));
				}
				
			}
			modelAndView.addObject("fvirtualcointypes", fvirtualcointypes) ;
			Fuser fuser = this.frontUserService.findById(GetSession(request).getFid()) ;
			String filter =  " where fuser.fid='"+fuser.getFid()+"' and status=1 and  date_format(flastupdatetime,'%Y-%m-%d')='"+time+"' order by fLastUpdateTime desc " ;
			List<Fasset> list= this.utilsService.list(0, 0,filter, false, Fasset.class) ;
			list.get(0).parseJsonByVir(fvirtualcointypes);
			
			int virulAssetsRecordCount = fvirtualcointypes1.size();
			String pagin = generatePagin(virulAssetsRecordCount/limit_Page+(virulAssetsRecordCount%limit_Page==0?0:1), currentPage,"/financial/assetsrecordvirual.html?time="+time+"&");
			modelAndView.addObject("list",list) ;
			modelAndView.addObject("pagin", pagin);
			modelAndView.setViewName("front/financial/virualassetsrecord") ;
			return modelAndView ;
		}
}
