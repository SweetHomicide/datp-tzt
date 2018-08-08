package com.ruizton.main.controller.front.pay;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.jasper.tagplugins.jstl.core.Redirect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.ruizton.main.Enum.CapitalOperationInStatus;
import com.ruizton.main.Enum.CapitalOperationTypeEnum;
import com.ruizton.main.Enum.CountLimitTypeEnum;
import com.ruizton.main.Enum.MessageTypeEnum;
import com.ruizton.main.Enum.RemittanceTypeEnum;
import com.ruizton.main.comm.ConstantMap;
import com.ruizton.main.comm.ValidateMap;
import com.ruizton.main.controller.BaseController;
import com.ruizton.main.model.FbankinfoWithdraw;
import com.ruizton.main.model.Fcapitaloperation;
import com.ruizton.main.model.Fsubscription;
import com.ruizton.main.model.Fuser;
import com.ruizton.main.model.Fvirtualcointype;
import com.ruizton.main.model.Fwallet;
import com.ruizton.main.model.Systembankinfo;
import com.ruizton.main.service.admin.AdminService;
import com.ruizton.main.service.admin.BankinfoWithdrawService;
import com.ruizton.main.service.admin.CapitaloperationService;
import com.ruizton.main.service.admin.SubscriptionService;
import com.ruizton.main.service.admin.SystembankService;
import com.ruizton.main.service.admin.WalletService;
import com.ruizton.main.service.front.FrontAccountService;
import com.ruizton.main.service.front.FrontUserService;
import com.ruizton.main.service.front.FrontValidateService;
import com.ruizton.main.service.front.UtilsService;
import com.ruizton.main.service.front.pay.PayService;
import com.ruizton.util.AcpBase;
import com.ruizton.util.Comm;
import com.ruizton.util.Constant;
import com.ruizton.util.GoogleAuth;
import com.ruizton.util.Utils;
import com.unionpay.acp.sdk.SDKConfig;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
@Controller
public class PayController  extends BaseController {
	@Autowired
	private PayService payService ;
	@Autowired
	private FrontAccountService frontAccountService ;
	@Autowired
	private FrontUserService frontUserService ;
	@Autowired
	private FrontValidateService frontValidateService ;
	@Autowired
	SystembankService  systembankService;
	@Autowired
	CapitaloperationService capitaloperationService;
	@Autowired
	BankinfoWithdrawService bankinfoWithdrawService;
	@Autowired
	private UtilsService utilsService ;
	@Autowired
	private ConstantMap map;
	@Autowired
	private ValidateMap messageValidateMap ;
	@Autowired
	private ValidateMap validateMap ;
	@Autowired
	private WalletService walletService;
	@Autowired
	private SubscriptionService subscriptionService;
	@Autowired
	private AdminService adminService;
	Fcapitaloperation fcapitaloperation;
	Systembankinfo systembankinfo;
	
	static{
		SDKConfig.getConfig().loadPropertiesFromSrc();//从classpath加载acp_sdk.properties文件	
	}
	@ResponseBody
	@RequestMapping(value="/account/payOnline",produces=JsonEncode)
	public String rechargeOnline(			
			HttpServletRequest request,
			@RequestParam(required=false,defaultValue="")String realMoney,
			@RequestParam(required=false,defaultValue="")String orderId,
			@RequestParam(required=false,defaultValue="")String accNo,
			@RequestParam(required=false,defaultValue="")String phoneNum,
			@RequestParam(required=false,defaultValue="")String currentPage,
			@RequestParam(required=false,defaultValue="")String fviId
			) throws Exception{
		//ModelAndView modelAndView = new ModelAndView() ;
		JSONObject js = new JSONObject();
		//金额处理：银联交单位为1分，前台显示为1元
		String accNoR=bankinfoWithdrawService.findById(accNo).getFbankNumber();
		 BigDecimal b1 = new BigDecimal(Comm.getWX_PAYNUMBER()); 
	     BigDecimal b2 = new BigDecimal(realMoney); 
		String txnAmtF=String.format("%.0f", b1.multiply(b2).doubleValue());//银联专用
		
		
		Timestamp timestamp=Utils.getTimestamp();
		String txnTime="";
		try {
			DateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss"); //银联专用
//		 DateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");   
			txnTime = sdf.format(timestamp);  
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		//插入充值记录
		systembankinfo=new Systembankinfo();
		fcapitaloperation=new Fcapitaloperation();
		
		Fvirtualcointype f = new Fvirtualcointype();
//		String fviId = "402880c959b6cf2a0159b6d213b80002";
		if (!"".equals(fviId)) {
			f.setFid(fviId);
			fcapitaloperation.setFviType(f);//虚拟币类型ID
			Fsubscription fsubscription = subscriptionService.findByFviId(fviId);//查看兑换比例-人民币兑换  fisRM是1的记录 
			fcapitaloperation.setFtotalBTC(Double.parseDouble(realMoney)*fsubscription.getFprice());//充值金额*兑换比例
		}
		
		fcapitaloperation.setFuser(this.GetSession(request)) ;
		fcapitaloperation.setFamount(Double.parseDouble(realMoney));
		fcapitaloperation.setFremittanceType(RemittanceTypeEnum.getEnumString(2));////汇款方式
		fcapitaloperation.setFremark(RemittanceTypeEnum.getEnumString(2));
		fcapitaloperation.setForderId(orderId);
		fcapitaloperation.setFtype(CapitalOperationTypeEnum.RMB_IN) ;
		fcapitaloperation.setFstatus(CapitalOperationInStatus.NoGiven);//尚未付款
		systembankinfo=systembankService.findById("1");
		fcapitaloperation.setSystembankinfo(systembankinfo);
		fcapitaloperation.setFcreateTime(timestamp) ;
		fcapitaloperation.setfLastUpdateTime(Utils.getTimestamp());
		fcapitaloperation.setFfees(0);
		fcapitaloperation.setVersion(0);
		fcapitaloperation.setFischarge(true);
		capitaloperationService.saveObj(fcapitaloperation);
		
		js.accumulate("realMoney", realMoney);
		//modelAndView.addObject("realMoney", realMoney);
		
		//返回订单号供前台页面展示
		String orderIdR=fcapitaloperation.getFid()+"";
		js.accumulate("orderIdR", orderIdR);
		//modelAndView.addObject("orderIdR", orderIdR);
		
		/*// 钱包里面冻结币
		Fwallet fwallet;
		fwallet =walletService.findById(this.GetSession(request).getFwallet().getFid());
		double frozenRMB = fwallet.getFfrozenRmb();
		double totalFrozenRMB = frozenRMB +Double.parseDouble(realMoney);
		fwallet.setFfrozenRmb(totalFrozenRMB);
		walletService.updateObj(fwallet);
		//---------钱包增加操作记录  钱包更新钱--end
*/
		
		// 查询用户是否有卡
		//1.如果有卡进行支付
		//2.如果无卡进行绑卡
		Map<String, String> reqData;
		String requestFrontUrl = SDKConfig.getConfig().getFrontRequestUrl();
		String isOpen=payService.openCardState(AcpBase.merId,orderId, txnTime, accNoR);	
		Boolean isCum=false;//用于页面判断是否为开卡
		if(isOpen.equals("0")){
			reqData=payService.openCardAndConsume(AcpBase.merId, orderId, txnTime, accNoR, txnAmtF);
			js.accumulate("reqData", reqData);
			//modelAndView.addObject("reqData", reqData);
			System.out.println("reqData==="+reqData);
			js.accumulate("requestFrontUrl", requestFrontUrl);
			//modelAndView.addObject("requestFrontUrl", requestFrontUrl);	
			js.accumulate("data", "您的银行卡尚未开通银联在线支付功能，请前往银联页面完成开通支付功能！");
			//modelAndView.addObject("data", "您的银行卡尚未开通银联在线支付功能，请前往银联页面完成开通支付功能！");
			isCum=true;
		}else if(isOpen.equals("1")){	
			String result=payService.consume(AcpBase.merId,orderId,txnTime, txnAmtF,accNoR,phoneNum,fcapitaloperation);
			if(result.equals("00")){
				js.accumulate("data", "支付成功！");
				js.accumulate("flag", true);
				//modelAndView.addObject("data", "支付成功！");
				//modelAndView.addObject("flag", true);
				
			}else{
				js.accumulate("data", result);
				js.accumulate("flag", false);
				//modelAndView.addObject("data", isOpen);
				//modelAndView.addObject("flag", false);
			}	
				
		}else{
			js.accumulate("data", isOpen);
			js.accumulate("flag", false);
			//modelAndView.addObject("flag", false);
			//modelAndView.addObject("data", isOpen);
			
		}
		
		//记录列表开始
		int cur = 1 ;
		int type=2;
		if(currentPage==null||"".equals(currentPage)){
			cur = 1; 
		}else{
			cur = Integer.parseInt(currentPage) ;
		}
		Fuser fuser = this.GetSession(request) ;
		String frealname = fuser.getFrealName();
		StringBuffer filter = new StringBuffer();
		filter.append("where fuser.fid='"+fuser.getFid()+"' \n");
		filter.append("and ftype ="+CapitalOperationTypeEnum.RMB_IN+"\n");
		if(type ==2){
			filter.append("and fremittanceType='"+RemittanceTypeEnum.getEnumString(type)+"' \n");
		}else{
			filter.append("and systembankinfo is not null \n");
		}
		
		filter.append(" order by fLastUpdateTime desc \n");
		List<Fcapitaloperation> list = this.capitaloperationService.list((cur-1)*Constant.AppRecordPerPage, Constant.RecordPerPage, filter.toString(), true);
		JSONArray jsonArray = new JSONArray();
		for (Fcapitaloperation fcapitaloperation : list) {
			JSONObject js1 = new JSONObject();
			js1.accumulate("fid" , fcapitaloperation.getFid());
			js1.accumulate("fcreateTime" , fcapitaloperation.getFcreateTime());
			js1.accumulate("famount" , fcapitaloperation.getFamount());
			js1.accumulate("fstatus_s" , fcapitaloperation.getFstatus_s());
			js1.accumulate("fstatus" , fcapitaloperation.getFstatus());
			jsonArray.add(js1);
		}
		int totalCount = this.adminService.getAllCount("Fcapitaloperation", filter.toString());
		String url = "/account/rechargeOnline.html?type="+type+"&";
		String pagin = this.generatePagin(totalCount/Constant.RecordPerPage+( (totalCount%Constant.RecordPerPage)==0?0:1), cur,url) ;
		//记录结束
		js.accumulate("list", jsonArray);
		js.accumulate("pagin", pagin);
		js.accumulate("cur_page", cur);
		js.accumulate("frealname", frealname);
		js.accumulate("isCum", isCum);
		/*modelAndView.addObject("list", list) ;
		modelAndView.addObject("pagin", pagin) ;
		modelAndView.addObject("cur_page", cur) ;
		modelAndView.addObject("fuser",GetSession(request)) ;
		
		modelAndView.addObject("isCum", isCum);
		modelAndView.setViewName("front/payment/resultPay");*/
		return js.toString();
		
		
	}
	@ResponseBody
	@RequestMapping("/account/consumeSMS")
	public String getSMS(HttpServletRequest request,
			@RequestParam(required=false,defaultValue="")String realMoney,
			@RequestParam(required=false,defaultValue="")String orderId,
			@RequestParam(required=false,defaultValue="")String accNo,
			@RequestParam(required=false,defaultValue="")String phone
			) throws Exception{
		
		
		//注册类型免登陆可以发送
		JSONObject jsonObject = new JSONObject() ;
		String ip = Utils.getIp(request) ;
		int tel_limit = this.frontValidateService.getLimitCount(ip, CountLimitTypeEnum.TELEPHONE) ;
		if(tel_limit<=0){
			jsonObject.accumulate("code", -1) ;
			jsonObject.accumulate("msg", "验证码多次错误，请稍后再试");
			return jsonObject.toString() ;
		}
		
		Fuser fuser = null ;
		fuser = this.frontUserService.findById(GetSession(request).getFid()) ;
		if(phone == null || phone.trim().length() ==0){
			jsonObject.accumulate("code", -1) ;
			jsonObject.accumulate("msg", "绑定的手机号码不能为空");
			return jsonObject.toString() ;
		}
		List<Fuser> fusers = this.utilsService.list(0, 0, " where ftelephone=? ", false, Fuser.class,new Object[]{phone}) ;
		if(fusers.size() >0){
			jsonObject.accumulate("code", -1) ;
			jsonObject.accumulate("msg", "手机号码已存在");
			return jsonObject.toString() ;
		}
		if(!fuser.isFisTelephoneBind()){
			jsonObject.accumulate("code", -1) ;
			jsonObject.accumulate("msg", "您没有绑定手机");
			return jsonObject.toString() ;
		}else{
			phone = fuser.getFtelephone() ;
		}




		String accNoR=bankinfoWithdrawService.findById(accNo).getFbankNumber();
		 BigDecimal b1 = new BigDecimal(1);
	     BigDecimal b2 = new BigDecimal(realMoney); 
	     DateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");   
			Timestamp timestamp=Utils.getTimestamp();
			String txnTime = sdf.format(timestamp); 
		String txnAmtF=String.format("%.0f", b1.multiply(b2).doubleValue());
		
		payService.consumeSMS(AcpBase.merId,orderId,txnTime, txnAmtF,accNoR,phone);
		jsonObject.accumulate("msg", "验证码已经发送，请查收");
		jsonObject.accumulate("code", "0");
		return jsonObject.toString() ;  
	}
	/**
	 * 
	 * @param request
	 * @param id
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value="/account/reloadRechargeOnLine",produces=JsonEncode)
	public String ReloadRechargeOnLine(HttpServletRequest request,
			String id) throws Exception{
		JSONObject js = new JSONObject();
		Fuser fuser = this.frontUserService.findById(GetSession(request).getFid()) ;
		Fcapitaloperation fcapitaloperation = this.frontAccountService.findCapitalOperationById(id) ;
		if(fcapitaloperation.getFuser().getFid().equals(fuser.getFid())
			&&fcapitaloperation.getFtype()==CapitalOperationTypeEnum.RMB_IN
			&&fcapitaloperation.getFstatus()==CapitalOperationInStatus.WaitForComing){
			
			DateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");   
			fcapitaloperation.getFcreateTime();
			String txnTime = sdf.format(fcapitaloperation.getFcreateTime()); 
			
			String remark = payService.queryState(AcpBase.merId, fcapitaloperation.getForderId(), txnTime);
			js.accumulate("result", remark);
		}	
		return js.toString();
	}
	/*
	 * var param={tradePwd:tradePwd,withdrawBalance:withdrawBalance,phoneCode:phoneCode,totpCode:totpCode};
	 * 
	 * */
	@ResponseBody
	@RequestMapping("/account/withdrawOnlineSubmit")
	public String withdrawCnySubmit(
			HttpServletRequest request,
			@RequestParam(required=false,defaultValue="0")String tradePwd,
			@RequestParam(required=true)double withdrawBalance,
			@RequestParam(required=false,defaultValue="0")String phoneCode,
			@RequestParam(required=false,defaultValue="0")String totpCode,
			@RequestParam(required=true)String withdrawBlank,
			@RequestParam(required=true,defaultValue="")String accNo
			
			) throws Exception{
		JSONObject jsonObject = new JSONObject() ;
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
		FbankinfoWithdraw fbankinfoWithdraw = this.frontUserService.findByIdWithBankInfos(withdrawBlank);
		if(fbankinfoWithdraw == null || !fbankinfoWithdraw.getFuser().getFid().equals(fuser.getFid()) ){
			jsonObject.accumulate("code", -1) ;
			jsonObject.accumulate("msg","提现账号不合法") ;
			return jsonObject.toString() ;
		}
		
		if(fwallet.getFtotalRmb()<withdrawBalance){
			//资金不足
			jsonObject.accumulate("code", -1) ;
			jsonObject.accumulate("msg","您的余额不足") ;
			return jsonObject.toString() ;
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
		
		/*if(fuser.getFgoogleBind()){
			int google_limit = this.frontValidateService.getLimitCount(ip, CountLimitTypeEnum.GOOGLE) ;
			if(google_limit<=0){
				jsonObject.accumulate("code", -1) ;
				jsonObject.accumulate("msg","谷歌验证码有误，请稍候再试") ;
				//return jsonObject.toString() ;
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
		}*/
		
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
		 
		String FolderIds = "";
		//Comm.MAX_Withdrawals_Time()加载comm.properties中的数据

		if(findCapitalCount <  Comm.times()){

			//增加一条提现记录
			boolean withdraw = false ;
			try {
				String FoldIdhead = fuser.getFid();
				String FoldIdbody = getTime();
				String Foldtail=Integer.toString((int)(Math.random()*100));
				FolderIds = FoldIdhead + FoldIdbody + Foldtail;
				withdraw = this.frontAccountService.updateWithdrawCNY(withdrawBalance, fuser,fbankinfoWithdraw, 2, FolderIds) ;
				List<Fcapitaloperation> findByFolderId = this.frontAccountService.findByFolderId(FolderIds);
				
					jsonObject.accumulate("folderId", findByFolderId.get(0));
				
				
			} catch (Exception e) {}finally{
				this.messageValidateMap.removeMessageMap(MessageTypeEnum.getEnumString(MessageTypeEnum.CNY_TIXIAN)) ;
				this.validateMap.removeMessageMap(fuser.getFid()+"_"+MessageTypeEnum.CNY_TIXIAN);
			}
		
		
		} else {
			jsonObject.accumulate("code", -1) ;
			jsonObject.accumulate("msg","每日交易次数不能大于"+findCapitalCount+"次") ;
			return jsonObject.toString() ;
		}
		
		String identity=fuser.getFidentityNo();//身份证
		String txnAmt=ConvertTxnAmtF(withdrawBalance);
		fcapitaloperation=new Fcapitaloperation();
		//post发送给银联
		String result=payService.df(AcpBase.merId, txnAmt, FolderIds, getTime(), fbankinfoWithdraw.getFbankNumber(),identity, fcapitaloperation);
		if(result.equals("成功")){
			
//			jsonObject.accumulate("orderId1", orderId1) ;
			jsonObject.accumulate("code", 0) ;
			jsonObject.accumulate("result", result) ;
			
		}else{
			jsonObject.accumulate("code", -1) ;
			jsonObject.accumulate("result", result) ;
			
		}
		return jsonObject.toString() ;
	}
	public String ConvertTxnAmtF(Double realMoney){
		 BigDecimal b1 = new BigDecimal(1);
	     BigDecimal b2 = new BigDecimal(realMoney); 
	     DateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");   
			Timestamp timestamp=Utils.getTimestamp();
			String txnTime = sdf.format(timestamp); 
		
		return String.format("%.0f", b1.multiply(b2).doubleValue());
	}
		
	public String getTime(){
		Timestamp timestamp=Utils.getTimestamp();
		String txnTime="";
		
		try {
			
			DateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss"); 
//		 DateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");   
			txnTime = sdf.format(timestamp);  
		} catch (Exception e) {
			// TODO: handle exception
		}
		return txnTime;
	}
	
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

