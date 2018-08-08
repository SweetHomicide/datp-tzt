package com.ruizton.main.controller.front;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

import com.ruizton.main.Enum.CapitalOperationInStatus;
import com.ruizton.main.Enum.CapitalOperationOutStatus;
import com.ruizton.main.Enum.CapitalOperationTypeEnum;
import com.ruizton.main.Enum.CountLimitTypeEnum;
import com.ruizton.main.Enum.MessageTypeEnum;
import com.ruizton.main.Enum.RemittanceTypeEnum;
import com.ruizton.main.Enum.SystemBankInfoEnum;
import com.ruizton.main.Enum.TransportlogStatusEnum;
import com.ruizton.main.Enum.VirtualCapitalOperationOutStatusEnum;
import com.ruizton.main.Enum.VirtualCapitalOperationTypeEnum;
import com.ruizton.main.Enum.VirtualCoinTypeStatusEnum;
import com.ruizton.main.comm.ConstantMap;
import com.ruizton.main.comm.ValidateMap;
import com.ruizton.main.controller.BaseController;
import com.ruizton.main.model.FbankinfoWithdraw;
import com.ruizton.main.model.Fcapitaloperation;
import com.ruizton.main.model.Fsubscription;
import com.ruizton.main.model.Ftransportlog;
import com.ruizton.main.model.Fuser;
import com.ruizton.main.model.FvirtualaddressWithdraw;
import com.ruizton.main.model.Fvirtualcaptualoperation;
import com.ruizton.main.model.Fvirtualcointype;
import com.ruizton.main.model.Fvirtualwallet;
import com.ruizton.main.model.Fwallet;
import com.ruizton.main.model.Systembankinfo;
import com.ruizton.main.service.admin.AdminService;
import com.ruizton.main.service.admin.SubscriptionService;
import com.ruizton.main.service.admin.SystemArgsService;
import com.ruizton.main.service.admin.VirtualCoinService;
import com.ruizton.main.service.admin.WalletService;
import com.ruizton.main.service.front.FrontAccountService;
import com.ruizton.main.service.front.FrontBankInfoService;
import com.ruizton.main.service.front.FrontUserService;
import com.ruizton.main.service.front.FrontValidateService;
import com.ruizton.main.service.front.FrontVirtualCoinService;
import com.ruizton.util.Comm;
import com.ruizton.util.Constant;
import com.ruizton.util.GoogleAuth;
import com.ruizton.util.Utils;

@Controller
public class FrontAccountJsonController extends BaseController{

	@Autowired
	private FrontAccountService frontAccountService ;
	@Autowired
	private FrontBankInfoService frontBankInfoService ;
	@Autowired
	private FrontUserService frontUserService ;
	@Autowired
	private FrontValidateService frontValidateService ;
	@Autowired
	private ValidateMap messageValidateMap ;
	@Autowired
	private FrontVirtualCoinService frontVirtualCoinService ;
	@Autowired
	private ConstantMap map;
	@Autowired
	private AdminService adminService;
	@Autowired
	private ValidateMap validateMap ;
	@Autowired
	private WalletService walletService;
	@Autowired
	private SubscriptionService subscriptionService;
	
	@RequestMapping(value="/account/alipayManual",produces = {JsonEncode})
	@ResponseBody
	public String alipayManual(
			HttpServletRequest request,
			@RequestParam(required=true) double money,
			@RequestParam(required=true,defaultValue="0") int type,
			@RequestParam(required=true) String sbank,
			@RequestParam(required=true,defaultValue="") String fviId//代表充值的虚拟资产类型ID
			) throws Exception{
		JSONObject jsonObject = new JSONObject() ;
		money = Utils.getDouble(money, 2);
		double minRecharge = Double.parseDouble(this.map.get("minrechargecny").toString()) ;
		if(money < minRecharge){
			//非法
			jsonObject.accumulate("code", -1);
			jsonObject.accumulate("msg", "最小充值金额为￥"+minRecharge);
			return jsonObject.toString();
		}
		
		if(type != 0){
			jsonObject.accumulate("code", -1);
			jsonObject.accumulate("msg", "非法操作");
			return jsonObject.toString();
		}
		
		Systembankinfo systembankinfo = this.frontBankInfoService.findSystembankinfoById(sbank) ;
		if(systembankinfo==null || systembankinfo.getFstatus()==SystemBankInfoEnum.ABNORMAL ){
			//银行账号停用
			jsonObject.accumulate("code", -1);
			jsonObject.accumulate("msg", "银行帐户不存在");
			return jsonObject.toString();
		}
//		String fviId = "402880c959b6cf2a0159b6d213b80002";
		Fcapitaloperation fcapitaloperation = new Fcapitaloperation() ;
		Fvirtualcointype f = new Fvirtualcointype();
		if (!"".equals(fviId)) {
			f.setFid(fviId);
			fcapitaloperation.setFviType(f);//虚拟币类型ID
			Fsubscription fsubscription = subscriptionService.findByFviId(fviId);//人民币兑换  fisRM是1
			fcapitaloperation.setFtotalBTC(money*fsubscription.getFprice());//充值金额*兑换比例
		}
		fcapitaloperation.setFamount(money) ;
		fcapitaloperation.setSystembankinfo(systembankinfo) ;
		fcapitaloperation.setFcreateTime(Utils.getTimestamp()) ;
		fcapitaloperation.setFtype(CapitalOperationTypeEnum.RMB_IN) ;
		fcapitaloperation.setFuser(this.GetSession(request)) ;
		fcapitaloperation.setFstatus(CapitalOperationInStatus.NoGiven) ;
		fcapitaloperation.setFremittanceType(RemittanceTypeEnum.getEnumString(type));
		fcapitaloperation.setfLastUpdateTime(Utils.getTimestamp());
		this.frontAccountService.addFcapitaloperation(fcapitaloperation) ;
		jsonObject.accumulate("code", 0);
		jsonObject.accumulate("money", String.valueOf(fcapitaloperation.getFamount())) ;
		jsonObject.accumulate("tradeId", fcapitaloperation.getFid()) ;
		jsonObject.accumulate("fbankName", systembankinfo.getFbankName()) ;
		jsonObject.accumulate("fownerName", systembankinfo.getFownerName()) ;
		jsonObject.accumulate("fbankAddress", systembankinfo.getFbankAddress()) ;
		jsonObject.accumulate("fbankNumber", systembankinfo.getFbankNumber()) ;
		return jsonObject.toString() ;  
	}
	
	
	@ResponseBody
	@RequestMapping(value="/account/rechargeCnySubmit",produces={JsonEncode})
	public String rechargeCnySubmit(
			HttpServletRequest request,
			@RequestParam(required=false) String bank,
			@RequestParam(required=false) String account,
			@RequestParam(required=false) String payee,
			@RequestParam(required=false) String phone,
			@RequestParam(required=false) int type,
			@RequestParam(required=true) String desc//记录的id
			) throws Exception{
		JSONObject jsonObject = new JSONObject() ;
		
		Fcapitaloperation fcapitaloperation = this.frontAccountService.findCapitalOperationById(desc) ;
		String fid = fcapitaloperation.getFuser().getFid();
		String fid2 = GetSession(request).getFid();
		if(fcapitaloperation==null || !fcapitaloperation.getFuser().getFid().equals(GetSession(request).getFid()) ){
			jsonObject.accumulate("code", -1);
			jsonObject.accumulate("msg", "非法操作");
			return jsonObject.toString();
		}
		
		if(fcapitaloperation.getFstatus() != CapitalOperationInStatus.NoGiven
				|| fcapitaloperation.getFtype() != CapitalOperationTypeEnum.RMB_IN){
			jsonObject.accumulate("code", -1);
			jsonObject.accumulate("msg", "网络异常");
			return jsonObject.toString();
		}
		
//		fcapitaloperation.setfBank(bank) ;
//		fcapitaloperation.setfAccount(account) ;
//		fcapitaloperation.setfPayee(payee) ;
//		fcapitaloperation.setfPhone(phone) ;
		fcapitaloperation.setFstatus(CapitalOperationInStatus.WaitForComing) ;
		fcapitaloperation.setFischarge(false);
		fcapitaloperation.setfLastUpdateTime(Utils.getTimestamp());
		try {
			this.frontAccountService.updateCapitalOperation(fcapitaloperation) ;
			jsonObject.accumulate("code", 0);
			jsonObject.accumulate("msg", "操作成功");
		} catch (Exception e) {
			jsonObject.accumulate("code", -1);
			jsonObject.accumulate("msg", "网络异常");
		}
		
		return jsonObject.toString();
	}
	

	@ResponseBody
	@RequestMapping(value="/account/alipayTransfer",produces={JsonEncode})
	public String alipayTransfer(
			HttpServletRequest request,
			@RequestParam(required=true) double money,
			@RequestParam(required=true) String accounts,
			@RequestParam(required=true) String imageCode,
			@RequestParam(required=true) int type
			) throws Exception{
		JSONObject jsonObject = new JSONObject() ;
		accounts= HtmlUtils.htmlEscape(accounts.trim());
		money = Utils.getDouble(money, 2);
		if(type !=3 && type !=4){
			jsonObject.accumulate("code", -1);
			jsonObject.accumulate("msg", "非法操作");
			return jsonObject.toString();
		}
		if(accounts.length() >100){
			jsonObject.accumulate("code", -1);
			jsonObject.accumulate("msg", "账号有误");
			return jsonObject.toString();
		}
		double minRecharge = Double.parseDouble(this.map.get("minrechargecny").toString()) ;
		if(money < minRecharge){
			//非法
			jsonObject.accumulate("code", -1);
			jsonObject.accumulate("msg", "最小充值金额为￥"+minRecharge);
			return jsonObject.toString();
		}
		
		if(vcodeValidate(request, imageCode) == false ){
			jsonObject.accumulate("code", -1) ;
			jsonObject.accumulate("msg", "请输入正确的图片验证码");
			return jsonObject.toString() ;
		}
		
		Fcapitaloperation fcapitaloperation = new Fcapitaloperation();
		fcapitaloperation.setFuser(GetSession(request));
		fcapitaloperation.setFamount(money);
		fcapitaloperation.setFremittanceType(RemittanceTypeEnum.getEnumString(type));
		fcapitaloperation.setfBank(RemittanceTypeEnum.getEnumString(type)) ;
		fcapitaloperation.setfAccount(accounts) ;
		fcapitaloperation.setfPayee(null) ;
		fcapitaloperation.setfPhone(null) ;
		fcapitaloperation.setFstatus(CapitalOperationInStatus.WaitForComing) ;
		fcapitaloperation.setFcreateTime(Utils.getTimestamp());
		fcapitaloperation.setfLastUpdateTime(Utils.getTimestamp());
		fcapitaloperation.setFtype(CapitalOperationTypeEnum.RMB_IN);
		fcapitaloperation.setFfees(0d);
		fcapitaloperation.setFischarge(false);
		try {
			this.frontAccountService.updateSaveCapitalOperation(fcapitaloperation) ;
			jsonObject.accumulate("code", 0);
			jsonObject.accumulate("msg", "操作成功");
		} catch (Exception e) {
			jsonObject.accumulate("code", -1);
			jsonObject.accumulate("msg", "网络异常");
		}
		
		return jsonObject.toString();
	}
	
	/*
	 * var param={tradePwd:tradePwd,withdrawBalance:withdrawBalance,phoneCode:phoneCode,totpCode:totpCode};
	 * 
	 * */
	@ResponseBody
	@RequestMapping("/account/withdrawCnySubmit")
	public String withdrawCnySubmit(
			HttpServletRequest request,
			@RequestParam(required=false,defaultValue="0")String tradePwd,
			@RequestParam(required=true)double withdrawBalance,
			@RequestParam(required=false,defaultValue="0")String phoneCode,
			@RequestParam(required=false,defaultValue="0")String totpCode,
			@RequestParam(required=true)String withdrawBlank,
			@RequestParam(required=false,defaultValue="")String symbol,
			@RequestParam(required=false,defaultValue="0")double fprice 
			) throws Exception{
		JSONObject jsonObject = new JSONObject() ;
		double withdrawBalance1=0;
		//如果是虚拟币提现则RMB=虚拟币/比例
		if(fprice!=0){
			withdrawBalance1=withdrawBalance;
			withdrawBalance = Utils.getDouble(withdrawBalance/fprice,2);
		}
		//最大提现人民币
		double max_double = Double.parseDouble(this.map.get("maxwithdrawcny").toString()) ;
		double min_double = Double.parseDouble(this.map.get("minwithdrawcny").toString()) ;
		
		if(withdrawBalance<min_double){
			//提现金额不能小于10
			jsonObject.accumulate("code", -1) ;
			jsonObject.accumulate("msg","提现金额不能小于"+min_double) ;
			return jsonObject.toString() ;
		}
		
		if(withdrawBalance>max_double){
			//提现金额不能大于指定值
			jsonObject.accumulate("code", -1) ;
			jsonObject.accumulate("msg","提现金额不能大于"+max_double) ;
			return jsonObject.toString() ;
		}
		
		
		Fuser fuser = this.frontUserService.findById(GetSession(request).getFid()) ;
		Fwallet fwallet = fuser.getFwallet() ;
		Fvirtualwallet fvirtualwallet = null;
		if(!"".equals(symbol)){
			 fvirtualwallet = this.frontUserService.findVirtualWalletByUser(fuser.getFid(), symbol) ;
		}
		FbankinfoWithdraw fbankinfoWithdraw = this.frontUserService.findByIdWithBankInfos(withdrawBlank);
		if(fbankinfoWithdraw == null || !fbankinfoWithdraw.getFuser().getFid().equals(fuser.getFid()) ){
			jsonObject.accumulate("code", -1) ;
			jsonObject.accumulate("msg","提现账号不合法") ;
			return jsonObject.toString() ;
		}
		if("".equals(symbol)){
			if(fwallet.getFtotalRmb()<withdrawBalance){
				//资金不足
				jsonObject.accumulate("code", -1) ;
				jsonObject.accumulate("msg","您的余额不足") ;
				return jsonObject.toString() ;
			}
		}else{
			if(fvirtualwallet.getFtotal()<withdrawBalance){
				//资金不足
				jsonObject.accumulate("code", -1) ;
				jsonObject.accumulate("msg","您的余额不足") ;
				return jsonObject.toString() ;
			}
		}
		
		if(fuser.getFtradePassword()==null){
			//没有设置交易密码
			jsonObject.accumulate("code", -1) ;
			jsonObject.accumulate("msg","请先设置交易密码") ;
			return jsonObject.toString() ;
		}

		if(!fuser.getFgoogleBind() && !fuser.isFisTelephoneBind()){
			//没有绑定谷歌或者手机
			jsonObject.accumulate("code", -1) ;
			jsonObject.accumulate("msg","请先绑定GOOGLE验证或者绑定手机号码") ;
			return jsonObject.toString() ;
		}
		
		String ip = Utils.getIp(request) ;
		if(fuser.getFtradePassword()!=null){
			int trade_limit = this.frontValidateService.getLimitCount(ip, CountLimitTypeEnum.TRADE_PASSWORD) ;
			if(trade_limit<=0){
				jsonObject.accumulate("code", -1) ;
				jsonObject.accumulate("msg","交易密码有误，请稍候再试") ;
				return jsonObject.toString() ;
			}else{
				boolean flag = fuser.getFtradePassword().equals(Utils.MD5(tradePwd)) ;
				if(!flag){
					jsonObject.accumulate("code", -1) ;
					jsonObject.accumulate("msg","交易密码有误，您还有"+(trade_limit-1)+"次机会") ;
					this.frontValidateService.updateLimitCount(ip, CountLimitTypeEnum.TRADE_PASSWORD) ;
					return jsonObject.toString() ;
				}else if(trade_limit<Constant.ErrorCountLimit){
					this.frontValidateService.deleteCountLimite(ip, CountLimitTypeEnum.TRADE_PASSWORD) ;
				}
			}
		}
		
		if(fuser.getFgoogleBind()){
			int google_limit = this.frontValidateService.getLimitCount(ip, CountLimitTypeEnum.GOOGLE) ;
			if(google_limit<=0){
				jsonObject.accumulate("code", -1) ;
				jsonObject.accumulate("msg","谷歌验证码有误，请稍候再试") ;
				return jsonObject.toString() ;
			}else{
				boolean flag = GoogleAuth.auth(Long.parseLong(totpCode.trim()), fuser.getFgoogleAuthenticator()) ;
				if(!flag){
					jsonObject.accumulate("code", -1) ;
					jsonObject.accumulate("msg","谷歌验证码有误，您还有"+(google_limit-1)+"次机会") ;
					this.frontValidateService.updateLimitCount(ip, CountLimitTypeEnum.GOOGLE) ;
					return jsonObject.toString() ;
				}else if(google_limit<Constant.ErrorCountLimit){
					this.frontValidateService.deleteCountLimite(ip, CountLimitTypeEnum.GOOGLE) ;
				}
			}
		}
		
		if(fuser.isFisTelephoneBind()){
			int tel_limit = this.frontValidateService.getLimitCount(ip, CountLimitTypeEnum.TELEPHONE) ;
			if(tel_limit<=0){
				jsonObject.accumulate("code", -1) ;
				jsonObject.accumulate("msg","手机验证码有误，请稍候再试") ;
				return jsonObject.toString() ;
			}else{
				boolean flag = validateMessageCode(fuser, fuser.getFareaCode(), fuser.getFtelephone(), MessageTypeEnum.CNY_TIXIAN, phoneCode) ;
				if(!flag){
					jsonObject.accumulate("code", -1) ;
					jsonObject.accumulate("msg","手机验证码有误，您还有"+(tel_limit-1)+"机会") ;
					this.frontValidateService.updateLimitCount(ip, CountLimitTypeEnum.TELEPHONE) ;
					return jsonObject.toString() ;
				}else if(tel_limit<Constant.ErrorCountLimit){
					this.frontValidateService.deleteCountLimite(ip, CountLimitTypeEnum.TELEPHONE) ;
				}
			}
		}
		String fid = fuser.getFid();
		Map<String, Object> param =new HashMap<String, Object>();
		param.put("f.fuser.fid", fid);
		//param.put("f.fstatus", 3);
		// 状态小于等于3的记录
		int fstatus = 3;
		//获取系统当前时间 格式为 yy-mm-dd
		String dates = getTimes();
		//当日交易次数
		int findCapitalCount = this.frontAccountService.findCapitalCounts(param, dates, fstatus);
		if(findCapitalCount < Comm.times()){
			boolean withdraw = false ;
			try {
				if(fprice==0){
				withdraw = this.frontAccountService.updateWithdrawCNY(withdrawBalance, fuser,fbankinfoWithdraw) ;
				} else{
					withdraw = this.frontAccountService.updateWithdrawCNY(symbol, fvirtualwallet, fprice, withdrawBalance1, fuser,fbankinfoWithdraw) ;
				}
			} catch (Exception e) {
				
			}finally{
				if(withdraw){
					this.messageValidateMap.removeMessageMap(MessageTypeEnum.getEnumString(MessageTypeEnum.CNY_TIXIAN)) ;
					this.validateMap.removeMessageMap(fuser.getFid()+"_"+MessageTypeEnum.CNY_TIXIAN);
				}
			}
			
			if(withdraw){
				jsonObject.accumulate("code", 0) ;
				jsonObject.accumulate("msg", "提现成功") ;
			}else{
				jsonObject.accumulate("code", -1) ;
				jsonObject.accumulate("msg", "网络异常") ;
			}
			
		}else{
			jsonObject.accumulate("code", -1) ;
			jsonObject.accumulate("msg","每日交易次数不能大于"+Comm.times()+"次") ;
			return jsonObject.toString() ;
		}
		
		return jsonObject.toString() ;
	}
	


	@ResponseBody
	@RequestMapping(value="/account/withdrawBtcSubmit",produces={JsonEncode})
	public String withdrawBtcSubmit(
			HttpServletRequest request,
			@RequestParam(required=true)String withdrawAddr,
			@RequestParam(required=true)double withdrawAmount,
			@RequestParam(required=true)String tradePwd,
			@RequestParam(required=false,defaultValue="0")String totpCode,
			@RequestParam(required=false,defaultValue="0")String phoneCode,
			@RequestParam(required=true)String symbol
			) throws Exception{
		JSONObject jsonObject = new JSONObject() ;
		Fuser fuser = this.frontUserService.findById(GetSession(request).getFid()) ;
		Fvirtualcointype fvirtualcointype = this.frontVirtualCoinService.findFvirtualCoinById(symbol) ;
		if(fvirtualcointype==null 
				|| !fvirtualcointype.isFIsWithDraw() 
				|| fvirtualcointype.getFstatus()==VirtualCoinTypeStatusEnum.Abnormal){
			jsonObject.accumulate("code", -1) ;
			jsonObject.accumulate("msg", "非法操作") ;
			return jsonObject.toString() ;
		}
		Fvirtualwallet fvirtualwallet = this.frontUserService.findVirtualWalletByUser(fuser.getFid(), fvirtualcointype.getFid()) ;
		FvirtualaddressWithdraw fvirtualaddressWithdraw = this.frontVirtualCoinService.findFvirtualaddressWithdraw(withdrawAddr);
		if(fvirtualaddressWithdraw == null
				|| !fvirtualaddressWithdraw.getFuser().getFid().equals( fuser.getFid())
				|| !fvirtualaddressWithdraw.getFvirtualcointype().getFid().equals(symbol)){
			jsonObject.accumulate("code", -1) ;
			jsonObject.accumulate("msg", "非法操作") ;
			return jsonObject.toString() ;
		}
		
		if(!fuser.isFisTelephoneBind() && !fuser.getFgoogleBind()){
			jsonObject.accumulate("code", -1) ;
			jsonObject.accumulate("msg", "请先绑定谷歌验证或者手机号码") ;
			return jsonObject.toString() ;
		}
		
		String ip = Utils.getIp(request) ;
		int google_limit = this.frontValidateService.getLimitCount(ip, CountLimitTypeEnum.GOOGLE ) ;
		int mobil_limit = this.frontValidateService.getLimitCount(ip, CountLimitTypeEnum.TELEPHONE) ;
		
		if(fuser.getFtradePassword()==null){
			jsonObject.accumulate("code", -1) ;
			jsonObject.accumulate("msg", "请先设置交易密码") ;
			return jsonObject.toString() ;
		}else{
			int trade_limit = this.frontValidateService.getLimitCount(ip, CountLimitTypeEnum.TRADE_PASSWORD) ;
			if(trade_limit<=0){
				jsonObject.accumulate("code", -1) ;
				jsonObject.accumulate("msg", "交易密码有误，请稍候再试") ;
				return jsonObject.toString() ;
			}
			
			if(!fuser.getFtradePassword().equals(Utils.MD5(tradePwd))){
				jsonObject.accumulate("code", -1) ;
				jsonObject.accumulate("msg", "交易密码有误，您还有"+(trade_limit-1)+"次机会") ;
				this.frontValidateService.updateLimitCount(ip, CountLimitTypeEnum.TRADE_PASSWORD) ;
				return jsonObject.toString() ;
			}else if(trade_limit<Constant.ErrorCountLimit){
				this.frontValidateService.deleteCountLimite(ip, CountLimitTypeEnum.TRADE_PASSWORD) ;
			}
		}
		

		double max_double = Double.parseDouble(this.map.get("maxwithdrawbtc").toString()) ;
		double min_double = Double.parseDouble(this.map.get("minwithdrawbtc").toString()) ;
		if(withdrawAmount<min_double){
			jsonObject.accumulate("code", -1) ;
			jsonObject.accumulate("msg", "最小提现数量为："+min_double) ;
			return jsonObject.toString() ;
		}
		
		if(withdrawAmount>max_double){
			jsonObject.accumulate("code", -1) ;
			jsonObject.accumulate("msg", "最大提现数量为："+max_double) ;
			return jsonObject.toString() ;
		}
		
		//余额不足
		if(fvirtualwallet.getFtotal()<withdrawAmount){
			jsonObject.accumulate("code", -1) ;
			jsonObject.accumulate("msg", "您的余额不足") ;
			return jsonObject.toString() ;
		}
			
			String filter = "where fadderess='"+fvirtualaddressWithdraw.getFadderess()+"' and fuser.fid='"+fuser.getFid()+"'";
			
			int cc = this.adminService.getAllCount("Fvirtualaddress", filter);
			if(cc >0){
				jsonObject.accumulate("code", -1) ;
				jsonObject.accumulate("msg", "不允许选择您本人的地址") ;
				//jsonObject.accumulate("msg", "站内会员不允许互转") ;
				return jsonObject.toString() ;
			}
		
		if(fuser.getFgoogleBind()){
			if(google_limit<=0){
				jsonObject.accumulate("code", -1) ;
				jsonObject.accumulate("msg", "谷歌验证码有误，请稍候再试") ;
				return jsonObject.toString() ;
			}
			
			boolean googleValidate = GoogleAuth.auth(Long.parseLong(totpCode.trim()), fuser.getFgoogleAuthenticator()) ;
			if(!googleValidate){
				jsonObject.accumulate("code", -1) ;
				jsonObject.accumulate("msg", "谷歌验证码有误，您还有"+(google_limit-1)+"次机会") ;
				this.frontValidateService.updateLimitCount(ip, CountLimitTypeEnum.GOOGLE) ;
				return jsonObject.toString() ;
			}else if(google_limit<Constant.ErrorCountLimit){
				this.frontValidateService.deleteCountLimite(ip, CountLimitTypeEnum.GOOGLE) ;
			}
		}
		
		if(fuser.isFisTelephoneBind()){
			if(mobil_limit<=0){
				jsonObject.accumulate("code", -1) ;
				jsonObject.accumulate("msg", "手机验证码有误，请稍候再试") ;
				return jsonObject.toString() ;
			}
			
			boolean mobilValidate = validateMessageCode(fuser, fuser.getFareaCode(), fuser.getFtelephone(), MessageTypeEnum.VIRTUAL_TIXIAN, phoneCode) ;
			if(!mobilValidate){
				jsonObject.accumulate("code", -1) ;
				jsonObject.accumulate("msg", "手机验证码有误，您还有"+(mobil_limit-1)+"次机会") ;
				this.frontValidateService.updateLimitCount(ip, CountLimitTypeEnum.TELEPHONE) ;
				return jsonObject.toString() ;
			}else if(mobil_limit<Constant.ErrorCountLimit){
				this.frontValidateService.deleteCountLimite(ip, CountLimitTypeEnum.TELEPHONE) ;
			}
		}
		
		try{
			this.frontVirtualCoinService.updateWithdrawBtc(fvirtualaddressWithdraw,fvirtualcointype, fvirtualwallet, withdrawAmount, fuser) ;
			jsonObject.accumulate("code", 0) ;
			jsonObject.accumulate("msg", "操作成功") ;
		}catch(Exception e){
			e.printStackTrace() ;
			jsonObject.accumulate("code", -1) ;
			jsonObject.accumulate("msg", "网络异常") ;
		}finally{
			this.validateMap.removeMessageMap(fuser.getFid()+"_"+MessageTypeEnum.VIRTUAL_TIXIAN);
		}
		
		return jsonObject.toString() ;
	}
	
	@ResponseBody
	@RequestMapping("/account/cancelRechargeCnySubmit")
	public String cancelRechargeCnySubmit(
			HttpServletRequest request,
			String id,
			String flag
			) throws Exception{
		Fuser fuser = this.frontUserService.findById(GetSession(request).getFid()) ;
		Fcapitaloperation fcapitaloperation = this.frontAccountService.findCapitalOperationById(id) ;
		if(fcapitaloperation.getFuser().getFid().equals(fuser.getFid()) 
			&&fcapitaloperation.getFtype()==CapitalOperationTypeEnum.RMB_IN
			&&fcapitaloperation.getFstatus()==CapitalOperationInStatus.NoGiven){
			if(flag.equals("cancel")){
				
				fcapitaloperation.setFstatus(CapitalOperationInStatus.Invalidate) ;
			}else{
				fcapitaloperation.setFstatus(CapitalOperationInStatus.WaitForComing) ;
			}
			fcapitaloperation.setfLastUpdateTime(Utils.getTimestamp()) ;
			
			
			//String type=fcapitaloperation.getFremittanceType();
			//if(RemittanceTypeEnum.getEnumString(3).equals(type)||RemittanceTypeEnum.getEnumString(4).equals(type)){
				// 钱包里面解冻币
				 
//				Fwallet fwallet =walletService.findById(this.GetSession(request).getFwallet().getFid());
//				double frozenRMB = fwallet.getFfrozenRmb();
//				double totalFrozenRMB = frozenRMB -fcapitaloperation.getFamount();;
//				fwallet.setFfrozenRmb(totalFrozenRMB);
//				walletService.updateObj(fwallet);
				//---------钱包增加操作记录  钱包更新钱--end
			//}
			try {
				this.frontAccountService.updateCapitalOperation(fcapitaloperation) ;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return String.valueOf(0) ;
	}
	
	@ResponseBody
	@RequestMapping("/account/cancelWithdrawcny")
	public String cancelWithdrawcny(
			HttpServletRequest request,
			String id
			) throws Exception{
		Fcapitaloperation fcapitaloperation = this.frontAccountService.findCapitalOperationById(id) ;
		if(fcapitaloperation!=null
				&&fcapitaloperation.getFuser().getFid().equals(GetSession(request).getFid())
				&&(fcapitaloperation.getFtype()==CapitalOperationTypeEnum.RMB_OUT||fcapitaloperation.getFtype()==4)
				&&fcapitaloperation.getFstatus()==CapitalOperationOutStatus.WaitForOperation){
			try {
				this.frontAccountService.updateCancelWithdrawCny(fcapitaloperation, this.frontUserService.findById(GetSession(request).getFid())) ;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return String.valueOf(0) ;
	}
	
	@ResponseBody
	@RequestMapping("/account/cancelWithdrawBtc")
	public String cancelWithdrawBtc(
			HttpServletRequest request,
			@RequestParam(required=true)String id
			) throws Exception{
		Fuser fuser = this.frontUserService.findById(GetSession(request).getFid()) ;
		Fvirtualcaptualoperation fvirtualcaptualoperation = this.frontVirtualCoinService.findFvirtualcaptualoperationById(id) ;
		if(fvirtualcaptualoperation!=null
				&&fvirtualcaptualoperation.getFuser().getFid().equals(fuser.getFid()) 
				&&fvirtualcaptualoperation.getFtype()==VirtualCapitalOperationTypeEnum.COIN_OUT
				&&fvirtualcaptualoperation.getFstatus()==VirtualCapitalOperationOutStatusEnum.WaitForOperation
				){
			
			try{
				this.frontAccountService.updateCancelWithdrawBtc(fvirtualcaptualoperation, fuser) ;
			}catch(Exception e){
				e.printStackTrace() ;
			}
			
		}
		return String.valueOf(0) ;
	}
	
//	
//	@ResponseBody
//	@RequestMapping("/account/json/btcTransport")
//	public String btcTransport(
//			HttpServletRequest request,
//			@RequestParam(required=true)int address,
//			@RequestParam(required=true)int fid,
//			@RequestParam(required=true)double amount,
//			@RequestParam(required=true)String passwd/*,
//			@RequestParam(required=false,defaultValue="0")String phoneCode*/
//			) throws Exception {
//		amount = Utils.getDouble(amount, 4) ;
//		Fvirtualcointype type = this.virtualCoinService.findById(fid);
//		String openTransport = this.map.get("openTransport").toString();
//		if(!openTransport.equals("1")){
//			return String.valueOf(-2000) ;//用户不存在
//		}
//		
//		if(type == null || type.getFstatus() != 1){
//			return String.valueOf(-1000) ;//用户不存在
//		}
//
//		Fuser fuser = this.frontUserService.findById(address);
//		if(fuser == null){
//			return String.valueOf(-2) ;//用户不存在
//		}
//		
//		if(amount <0.1D){
//			return String.valueOf(-1) ;//最小转出0.1
//		}
//		
//		Fuser preUser = this.frontUserService.findById(GetSession(request).getFid()) ;
//		if(!preUser.isFisTelephoneBind()){
//			return String.valueOf(-11) ;//未绑定手机
//		}
//		
//		if(preUser.getFtradePassword() == null 
//				|| preUser.getFtradePassword().trim().length()==0){
//			return String.valueOf(-12) ;//未設置交易密碼
//		}
//		
//		if(!preUser.getFtradePassword().equals(Utils.MD5(passwd))){
//			return String.valueOf(-4) ;//交易密码错误
//		}
//		
//		Fvirtualwallet fvirtualwallet = this.frontUserService.findVirtualWalletByUser(GetSession(request).getFid(), fid) ;
//		if(fvirtualwallet.getFtotal()<amount){
//			return String.valueOf(-5) ;//余额不足
//		}
//		
//
//		int userId = fuser.getFid();
//		if(userId == GetSession(request).getFid()){
//			return String.valueOf(-13) ;//用户不存在
//		}
//		double transferRate = Double.valueOf(this.map.get("transferRate").toString());
//		double ffee = Utils.getDouble(amount*transferRate, 4);
//		double last = Utils.getDouble(amount-ffee, 4);
//		Ftransportlog ftransportlog = new Ftransportlog() ;
//		ftransportlog.setFaddress(String.valueOf(address)) ;
//		ftransportlog.setFamount(last) ;
//		ftransportlog.setFfees(ffee);
//		ftransportlog.setFcreatetime(Utils.getTimestamp()) ;
//		ftransportlog.setFvirtualcointype(type);
//		ftransportlog.setFuser(preUser) ;
//		ftransportlog.setFstatus(TransportlogStatusEnum.NORMAL_VALUE);
//		fvirtualwallet.setFtotal(fvirtualwallet.getFtotal()-amount) ;
//		fvirtualwallet.setFfrozen(fvirtualwallet.getFfrozen()+amount);
//		//接收人的钱包
//		Fvirtualwallet fw2 = this.frontUserService.findVirtualWalletByUser(fuser.getFid(), fid) ;
//		fw2.setFfrozen(fw2.getFfrozen()+last);
//		
//		boolean flag = false ;
//		
//		try {
//			this.frontUserService.updateTransport(ftransportlog, fvirtualwallet,fw2) ;
//			flag = true ;
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//		if(flag==true){
//			return String.valueOf(0) ;//成功
//		}else{
//			return String.valueOf(-6) ;//网络错误，请重试
//		}
//		
//	}
	public String getTimes(){
		Timestamp timestamp=Utils.getTimestamp();
		String txnTime="";
		
		try {
			
			//DateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss"); 
		 DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");   
			txnTime = sdf.format(timestamp);  
		} catch (Exception e) {
			// TODO: handle exception
		}
		return txnTime;
	}
}
