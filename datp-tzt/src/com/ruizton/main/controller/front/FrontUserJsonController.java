package com.ruizton.main.controller.front;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.HtmlUtils;

import com.ruizton.main.Enum.BankInfoStatusEnum;
import com.ruizton.main.Enum.BankTypeEnum;
import com.ruizton.main.Enum.CountLimitTypeEnum;
import com.ruizton.main.Enum.LogTypeEnum;
import com.ruizton.main.Enum.MessageTypeEnum;
import com.ruizton.main.Enum.RegTypeEnum;
import com.ruizton.main.Enum.SendMailTypeEnum;
import com.ruizton.main.Enum.UserStatusEnum;
import com.ruizton.main.Enum.VirtualCoinTypeStatusEnum;
import com.ruizton.main.auto.RealTimeData;
import com.ruizton.main.comm.ConstantMap;
import com.ruizton.main.comm.ValidateMap;
import com.ruizton.main.controller.BaseController;
import com.ruizton.main.dao.FpoolDAO;
import com.ruizton.main.dao.FvirtualaddressDAO;
import com.ruizton.main.model.FbankinfoWithdraw;
import com.ruizton.main.model.Fpool;
import com.ruizton.main.model.Fuser;
import com.ruizton.main.model.Fvirtualaddress;
import com.ruizton.main.model.FvirtualaddressWithdraw;
import com.ruizton.main.model.Fvirtualcointype;
import com.ruizton.main.service.admin.AdminService;
import com.ruizton.main.service.admin.SystemArgsService;
import com.ruizton.main.service.admin.UserService;
import com.ruizton.main.service.admin.VirtualWalletService;
import com.ruizton.main.service.front.FrontAccountService;
import com.ruizton.main.service.front.FrontUserService;
import com.ruizton.main.service.front.FrontValidateService;
import com.ruizton.main.service.front.FrontVirtualCoinService;
import com.ruizton.main.service.front.UtilsService;
import com.ruizton.util.Constant;
import com.ruizton.util.GoogleAuth;
import com.ruizton.util.Utils;

@Controller
public class FrontUserJsonController extends BaseController {

	@Autowired
	private FrontUserService frontUserService ;
	@Autowired
	private FrontValidateService frontValidateService ;
	@Autowired
	private FrontVirtualCoinService frontVirtualCoinService ;
	@Autowired
	private RealTimeData realTimeData ;
	@Autowired
	private FrontAccountService frontAccountService ;
	@Autowired
	private ConstantMap constantMap;
	@Autowired
	private AdminService adminService;
	@Autowired
	private ValidateMap validateMap;
	@Autowired
	private SystemArgsService systemArgsService;
	@Autowired
	private UtilsService utilsService ;
	@Autowired
	private HttpServletRequest request;
	@Autowired
	private UserService userService;
	@Autowired
	private FvirtualaddressDAO fvirtualaddressDAO ;
	@Autowired
	private FpoolDAO fpoolDAO ;
	/* 邮箱或者和手机号码是否重复是否重复
	 * @param type:0手机，1邮箱
	 * @Return 0重复，1正常
	 * */
	@ResponseBody
	@RequestMapping(value="/user/reg/chcekregname")
	public String chcekregname(
			@RequestParam(required=false,defaultValue="0") String name,
			@RequestParam(required=false,defaultValue="1") int type,
			@RequestParam(required=false,defaultValue="0") int random
			) throws Exception{		
		JSONObject jsonObject = new JSONObject() ;

		if(type==0){
			//手机账号
			boolean flag = this.frontUserService.isTelephoneExists(name) ;
			if(flag){
				jsonObject.accumulate("code", -1) ;
				jsonObject.accumulate("msg", "手机号码错误或已存在");
			}else{
				jsonObject.accumulate("code", 0) ;
			}
		}else{
			//邮箱账号
			boolean flag = this.frontUserService.isEmailExists(name) ;
			if(flag){
				jsonObject.accumulate("code", -1) ;
				jsonObject.accumulate("msg", "邮箱错误或已存在");
			}else{
				jsonObject.accumulate("code", 0) ;
			}
		}
		
		
		return jsonObject.toString() ;
	
	}
	/**
	 * 增加钱包地址
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/ssadmin/addfvirtualaddress")
	public ModelAndView addfvirtualaddress() throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("ssadmin/comm/ajaxDone");
		String fid = request.getParameter("uid");
		Fuser fuser = this.userService.findById(fid);
		String addAddress = this.frontUserService.addAddress(fuser);

		modelAndView.addObject("statusCode", 200);
//	
		modelAndView.addObject("message", addAddress);
	//	}
		return modelAndView;
	}
	
	/**
	 *  作者：           Dylan
	 *  标题：           regIndex 
	 *  时间：           2018年8月13日
	 *  描述：           首页注册用户
	 *  
	 *  @param request
	 *  @param response
	 *  @param random
	 *  @param password
	 *  @param regName
	 *  @param regType :0  手机   1 email
	 *  @param vcode
	 *  @param ecode
	 *  @param phoneCode
	 *  @return 1正常，-2名字重复，-4邮箱格式不对，-5客户端你没打开cookie
	 *  @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value="/user/reg/index",produces=JsonEncode)
	public String regIndex(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required=false,defaultValue="0") int random,
			@RequestParam(required=false,defaultValue="0") String password,
			@RequestParam(required=false,defaultValue="0") String regName,
			@RequestParam(required=false,defaultValue="0") int regType,//0手机、1邮箱
			@RequestParam(required=false,defaultValue="0") String vcode,
			@RequestParam(required=false,defaultValue="0") String ecode,
			@RequestParam(required=true,defaultValue="0") String phoneCode
			) throws Exception{
		String areaCode = "86" ;
		JSONObject jsonObject = new JSONObject() ;
		
		String phone = HtmlUtils.htmlEscape(regName);
		phoneCode = HtmlUtils.htmlEscape(phoneCode);
		String isOpenReg = constantMap.get("isOpenReg").toString().trim();
		if(!isOpenReg.equals("1")){
			jsonObject.accumulate("code", -888) ;
			jsonObject.accumulate("msg", "暂停注册") ;
			return jsonObject.toString() ;
		}
		if(vcodeValidate(request, vcode) == false ){
			jsonObject.accumulate("code", -1) ;
			jsonObject.accumulate("msg", "请输入正确的图片验证码");
			return jsonObject.toString() ;
		}
		//邮箱
		if(regType==0){
			//手机注册
			
			boolean flag1 = this.frontUserService.isTelephoneExists(regName) ;
			if(flag1){
				jsonObject.accumulate("code", -22) ;
				jsonObject.accumulate("msg", "手机号码已经被注册") ;
				return jsonObject.toString() ;
			}
			
			if(!phone.matches(Constant.PhoneReg)){
				jsonObject.accumulate("code", -44) ;
				jsonObject.accumulate("msg", "手机格式错误") ;
				return jsonObject.toString() ;
			}
			
			boolean mobilValidate = validateMessageCode(Utils.getIp(request),areaCode,phone, MessageTypeEnum.REG_CODE, phoneCode) ;
			if(!mobilValidate){
				jsonObject.accumulate("code", -20) ;
				jsonObject.accumulate("msg", "短信验证码错误") ;
				return jsonObject.toString() ;
			}
			
		}else{
			//邮箱注册
			boolean flag = this.frontUserService.isEmailExists(regName) ;
			if(flag){
				jsonObject.accumulate("code", -2) ;
				jsonObject.accumulate("msg", "邮箱已经存在") ;
				return jsonObject.toString() ;
			}
			
			boolean mailValidate = validateMailCode(Utils.getIp(request), phone, SendMailTypeEnum.RegMail, ecode);
			if(!mailValidate){
				jsonObject.accumulate("code", -20) ;
				jsonObject.accumulate("msg", "邮箱验证码错误") ;
				return jsonObject.toString() ;
			}
			
			if(!regName.matches(Constant.EmailReg)){
				jsonObject.accumulate("code", -4) ;
				jsonObject.accumulate("msg", "邮箱格式错误") ;
				return jsonObject.toString() ;
			}
			
		}
		
		
		//推广 邀请码
		Fuser intro = null ;
		String intro_user = request.getParameter("intro_user") ;
		
		try {
			if(intro_user!=null && !"".equals(intro_user.trim())){
				//intro = this.frontUserService.findById(intro_user.trim()) ;
				intro = this.frontUserService.findUserByProperty("floginName", intro_user).get(0) ;
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		if(intro==null){
			String isMustIntrol = constantMap.get("isMustIntrol").toString().trim();
			if(isMustIntrol.equals("1")){
				jsonObject.accumulate("code", -200) ;
				jsonObject.accumulate("msg", "请填写正确的邀请码") ;
				return jsonObject.toString() ;
			}
		}
		
		
		Fuser fuser = new Fuser() ;
		if(intro!=null){
			fuser.setfIntroUser_id(intro) ;
		}
		if(regType == 0){
			//手机注册
			fuser.setFregType(RegTypeEnum.TEL_VALUE);
			fuser.setFtelephone(phone);
			fuser.setFareaCode(areaCode);
			fuser.setFisTelephoneBind(true);
			
			fuser.setFnickName(phone) ;
			fuser.setFloginName(phone) ;
		}else{
			fuser.setFregType(RegTypeEnum.EMAIL_VALUE);
			fuser.setFemail(regName) ;
			fuser.setFisMailValidate(true) ;
			fuser.setFnickName(regName.split("@")[0]) ;
			fuser.setFloginName(regName) ;
		}
		
		
		fuser.setFregisterTime(Utils.getTimestamp()) ;
		fuser.setFloginPassword(Utils.MD5(password)) ;
		fuser.setFtradePassword(null) ;
		String ip = Utils.getIp(request) ;
		fuser.setFregIp(ip);
		fuser.setFlastLoginIp(ip);
		fuser.setFstatus(UserStatusEnum.NORMAL_VALUE) ;
		fuser.setFlastLoginTime(Utils.getTimestamp()) ;
		fuser.setFlastUpdateTime(Utils.getTimestamp()) ;
		boolean saveFlag = false ;
		try {
			saveFlag = this.frontUserService.saveRegister(fuser) ;
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(regType==0){
				String key1 = ip+"message_"+MessageTypeEnum.REG_CODE ;
				String key2 = ip+"_"+phone+"_"+MessageTypeEnum.REG_CODE ;
				this.validateMap.removeMessageMap(key1);
				this.validateMap.removeMessageMap(key2);
			}else{
				String key1 = ip+"mail_"+SendMailTypeEnum.RegMail ;
				String key2 = ip+"_"+phone+"_"+SendMailTypeEnum.RegMail ;
				this.validateMap.removeMailMap(key1);
				this.validateMap.removeMailMap(key2);
			}
		}
		getSession(request).removeAttribute("checkcode") ;
		if(saveFlag){
			this.SetSession(fuser,request,response) ;
			//公共方法取了email做了登陆名
			fuser.setFemail(fuser.getFloginName()) ;
			fuser.setFloginPassword(password) ;
			fuser = this.frontUserService.updateCheckLogin(fuser, ip,regType==1) ;
			jsonObject.accumulate("code", 0) ;
			jsonObject.accumulate("msg", "注册成功") ;
			return jsonObject.toString() ;
		
		}else{
			jsonObject.accumulate("code", -10) ;
			jsonObject.accumulate("msg", "网络错误，请稍后再试") ;
			return jsonObject.toString() ;
		}
	}
	
	
	/*
	 *  http://localhost:8090/user/login/index.html?random=78
	 *   loginName=asdjf@adf.cn longLogin=0 password=adsfasdf
	 * */
	
	@ResponseBody
	@RequestMapping(value="/user/login/index",produces=JsonEncode)
	public String loginIndex(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required=false,defaultValue="0")int random,
			@RequestParam(required=true) String loginName,
			@RequestParam(required=true) String password
			) throws Exception {

		JSONObject jsonObject = new JSONObject() ;
		
		int longLogin = 0;//0是手机，1是邮箱
		if(loginName.matches(Constant.PhoneReg) == false  ){
			longLogin = -1 ;
		}
		if(loginName.matches(Constant.EmailReg) == true){
			longLogin = 1 ;
		}
		if(longLogin == -1){
			jsonObject.accumulate("code", -1) ;
			jsonObject.accumulate("msg", "用户名错误") ;
			return jsonObject.toString() ;
		}

		Fuser fuser = new Fuser() ;
		fuser.setFemail(loginName) ;
		fuser.setFloginPassword(password) ;
		String ip = Utils.getIp(request) ;
		int limitedCount = this.frontValidateService.getLimitCount(ip,CountLimitTypeEnum.LOGIN_PASSWORD) ;
		if(limitedCount>0){
			
			fuser = this.frontUserService.updateCheckLogin(fuser, ip,longLogin==1) ;
			if(fuser!=null){
				
				
				String isCanlogin = constantMap.get("isCanlogin").toString().trim();
				if(!isCanlogin.equals("1")){
					boolean isCanLogin = false;
					String[] canLoginUsers = constantMap.get("canLoginUsers").toString().split("#");
					for(int i=0;i<canLoginUsers.length;i++){
						if(canLoginUsers[i].equals(String.valueOf(fuser.getFid()))){
							isCanLogin = true;
							break;
						}
					}
					if(!isCanLogin){
						jsonObject.accumulate("code", -1) ;
						jsonObject.accumulate("msg", "网站暂时不允许登陆") ;
						return jsonObject.toString() ;
					}
				}
				
				if(fuser.getFstatus()==UserStatusEnum.NORMAL_VALUE){
					this.frontValidateService.deleteCountLimite(ip,CountLimitTypeEnum.LOGIN_PASSWORD) ;
					if(fuser.getFopenSecondValidate()){
						SetSecondLoginSession(request,fuser);
					}else{
						SetSession(fuser,request,response) ;
						jsonObject.accumulate("code", 0) ;
						jsonObject.accumulate("msg", "登陆成功") ;
						return jsonObject.toString() ;
					}
				}else{
					jsonObject.accumulate("code", -1) ;
					jsonObject.accumulate("msg", "账户出现安全隐患被冻结，请尽快联系客服") ;
					return jsonObject.toString() ;
				}
			}else{
				//错误的用户名或密码
				if(limitedCount==Constant.ErrorCountLimit){
					jsonObject.accumulate("code", -1) ;
					jsonObject.accumulate("msg", "用户名或密码错误<a href=\"/validate/reset.html\">找回密码&gt;&gt;</a>") ;
				}else{
					jsonObject.accumulate("code", -1) ;
					jsonObject.accumulate("msg", "用户名或密码错误，您还有"+(limitedCount-1)+"次机会") ;
				}
				this.frontValidateService.updateLimitCount(ip,CountLimitTypeEnum.LOGIN_PASSWORD) ;
				return jsonObject.toString() ;
			}
		}else{
			jsonObject.accumulate("code", -1) ;
			jsonObject.accumulate("msg", "网络异常") ;
			return jsonObject.toString() ;
		}
		return null;
	}
	
	/*
	 * 添加谷歌设备
	 * */
	@ResponseBody
	@RequestMapping(value="/user/googleAuth",produces={JsonEncode})
	public String googleAuth(
			HttpServletRequest request
			) throws Exception{
		JSONObject jsonObject = new JSONObject() ;
		
		Fuser fuser = this.frontUserService.findById(GetSession(request).getFid()) ;
		if(fuser.getFgoogleBind()){
			//已经绑定机器了，属于非法提交
			jsonObject.accumulate("code", -1) ;
			return jsonObject.toString() ;
		}
		
		Map<String, String> map = GoogleAuth.genSecret(fuser.getFemail()) ;
		String totpKey = map.get("secret") ;
		String qecode = map.get("url") ;
		
		fuser.setFgoogleAuthenticator(totpKey) ;
		fuser.setFgoogleurl(qecode) ;
		fuser.setFlastUpdateTime(Utils.getTimestamp()) ;
		this.frontUserService.updateFUser(fuser,getSession(request),-1,null) ;
		
		jsonObject.accumulate("qecode", qecode) ;
		jsonObject.accumulate("code", 0) ;
		jsonObject.accumulate("totpKey", totpKey) ;
				
		return jsonObject.toString() ;
	}
	
	/*
	 * 添加设备认证
	 * */
	@ResponseBody
	@RequestMapping(value="/user/validateAuthenticator",produces={JsonEncode})
	public String validateAuthenticator(
			HttpServletRequest request,
			@RequestParam(required=true)String code,
			@RequestParam(required=true)String totpKey
			) throws Exception{
		JSONObject jsonObject = new JSONObject() ;
		
		String ip = Utils.getIp(request) ;
		Fuser fuser = this.frontUserService.findById(GetSession(request).getFid()) ;

		boolean b_status = fuser.getFgoogleBind()==false
							&& totpKey.equals(fuser.getFgoogleAuthenticator())
							&& !totpKey.trim().equals("");
		
		if(!b_status){
			//非法提交
			jsonObject.accumulate("code", -1) ;
			jsonObject.accumulate("msg", "您已绑定GOOGLE验证器，请勿重复操作") ;
			return jsonObject.toString() ;
		}
		
		int limitedCount = this.frontValidateService.getLimitCount(ip, CountLimitTypeEnum.GOOGLE) ;
		if(limitedCount>0){
			boolean auth = GoogleAuth.auth(Long.parseLong(code), fuser.getFgoogleAuthenticator()) ;
			if(auth){
				jsonObject.accumulate("code", 0) ;//0成功，-1，错误
				jsonObject.accumulate("msg", "绑定成功") ;
				this.frontValidateService.deleteCountLimite(ip, CountLimitTypeEnum.GOOGLE) ;
				
				fuser.setFgoogleBind(true) ;
				fuser.setFgoogleValidate(false) ;
				this.frontUserService.updateFUser(fuser, getSession(request),LogTypeEnum.User_BIND_GOOGLE,ip) ;
				return jsonObject.toString() ;
			}else{
				this.frontValidateService.updateLimitCount(ip, CountLimitTypeEnum.GOOGLE) ;
				jsonObject.accumulate("code", -1) ;
				jsonObject.accumulate("msg", "GOOGLE验证码有误，您还有"+(limitedCount-1)+"次机会") ;
				return jsonObject.toString() ;
			}
		}else{
			jsonObject.accumulate("code", -1) ;
			jsonObject.accumulate("msg", "GOOGLE验证码有误，请稍候再试") ;
			return jsonObject.toString() ;
		}
		
	}
	
	/*
	 * 查看谷歌密匙
	 * */
	@ResponseBody
	@RequestMapping(value="/user/getGoogleTotpKey")
	public String getGoogleTotpKey(
			HttpServletRequest request,
			@RequestParam(required=true) String totpCode
			) throws Exception{
		JSONObject jsonObject = new JSONObject() ;
		
		Fuser fuser = this.frontUserService.findById(GetSession(request).getFid()) ;
		String ip = Utils.getIp(request) ;
		int limit = this.frontValidateService.getLimitCount(ip, CountLimitTypeEnum.GOOGLE ) ;
		if(limit<=0){
			jsonObject.accumulate("code", -1) ;
			jsonObject.accumulate("msg", "GOOGLE验证码有误，请稍候再试") ;
			return jsonObject.toString() ;
		}else{
			if(fuser.getFgoogleBind()){
				if(GoogleAuth.auth(Long.parseLong(totpCode), fuser.getFgoogleAuthenticator())){
					this.frontValidateService.deleteCountLimite(ip, CountLimitTypeEnum.GOOGLE) ;
					
					jsonObject.accumulate("qecode", fuser.getFgoogleurl()) ;
					jsonObject.accumulate("code", 0) ;
					jsonObject.accumulate("totpKey", fuser.getFgoogleAuthenticator()) ;
					jsonObject.accumulate("msg", "验证成功") ;
					this.frontValidateService.deleteCountLimite(ip, CountLimitTypeEnum.GOOGLE) ;
					return jsonObject.toString() ;
				}else{
					jsonObject.accumulate("code", -1) ;
					jsonObject.accumulate("msg", "GOOGLE验证码有误，您还有"+(limit-1)+"次机会") ;
					this.frontValidateService.updateLimitCount(ip, CountLimitTypeEnum.GOOGLE) ;
					return jsonObject.toString() ;
				}
			}else{
				jsonObject.accumulate("code", -1) ;
				jsonObject.accumulate("msg", "您未绑定GOOGLE验证器") ;
				return jsonObject.toString() ;
			}
		}
	}
	
	/*
	 * 发送手机验证码
	 * http://localhost:8899/user/sendValidateCode.html?random=46&areaCode=86&msgType=0&phone=15017549972
	 * */
	@ResponseBody
	@RequestMapping(value="/user/sendValidateCode",produces={JsonEncode})
	public String sendValidateCode(
			HttpServletRequest request,
			@RequestParam(required=true,defaultValue="0")String areaCode,
			@RequestParam(required=true,defaultValue="0")String phone
			) throws Exception{
		if(!areaCode.equals("86") || phone.matches("^\\d{10,14}$")){
			boolean isPhoneExists = this.frontUserService.isTelephoneExists(phone) ;
			if(isPhoneExists){
				return String.valueOf(-3) ;//手机账号存在
			}else{
				Fuser fuser = this.frontUserService.findById(GetSession(request).getFid()) ;
				super.SendMessage(fuser,fuser.getFid(), areaCode ,phone, MessageTypeEnum.BANGDING_MOBILE) ;
				return String.valueOf(0) ;
			}
		}else{
			return String.valueOf(-2) ;//手机号码格式不正确
		}
	}
	
	
	/*
	 * 綁定手機
	 * http://localhost:8899/user/validatePhone.html?random=35
	 * &areaCode=86&code=333333&phone=15017549972&totpCode=333333
	 * */
	@ResponseBody
	@RequestMapping(value="/user/validatePhone",produces={JsonEncode})
	public String validatePhone(
			HttpServletRequest request,
			@RequestParam(required=true)int isUpdate,//0是绑定手机，1是换手机号码
			@RequestParam(required=true)String areaCode,
			@RequestParam(required=true)String imgcode,
			@RequestParam(required=true)String phone,
			@RequestParam(required=true)String oldcode,
			@RequestParam(required=true)String newcode,
			@RequestParam(required=false,defaultValue="0")String totpCode
			) throws Exception{
		JSONObject jsonObject = new JSONObject() ;
		areaCode = areaCode.replace("+", "");
		if(!phone.matches("^\\d{10,14}$")){//手機格式不對
			jsonObject.accumulate("code", -1) ;
			jsonObject.accumulate("msg", "手机号码格式不正确，请重新输入");
			return jsonObject.toString() ;
		}
		
		Fuser fuser = this.frontUserService.findById(GetSession(request).getFid()) ;
		
		if(isUpdate ==0){
			if(fuser.isFisTelephoneBind()){
				jsonObject.accumulate("code", -1) ;
				jsonObject.accumulate("msg", "您已绑定了手机号码");
				return jsonObject.toString() ;
			}
		}else{
			if(!fuser.isFisTelephoneBind()){
				jsonObject.accumulate("code", -1) ;
				jsonObject.accumulate("msg", "您还未绑定手机号码");
				return jsonObject.toString() ;
			}
		}

		
		String ip = Utils.getIp(request) ;
		int google_limit = this.frontValidateService.getLimitCount(ip, CountLimitTypeEnum.GOOGLE) ;
		int tel_limit = this.frontValidateService.getLimitCount(ip, CountLimitTypeEnum.TELEPHONE) ;
		if(google_limit<=0){
			jsonObject.accumulate("code", -1) ;
			jsonObject.accumulate("msg", "您的GOOGLE验证码错误次数已达上限，请稍候再试");
			return jsonObject.toString() ;
		}
		if(tel_limit<=0){
			jsonObject.accumulate("code", -1) ;
			jsonObject.accumulate("msg", "您的手机验证码错误次数已达上限，请稍候再试");
			return jsonObject.toString() ;
		}
		
		if(fuser.getFgoogleBind()){
			boolean googleAuth = GoogleAuth.auth(Long.parseLong(totpCode),fuser.getFgoogleAuthenticator()) ;
			if(!googleAuth){
				//谷歌驗證失敗
				this.frontValidateService.updateLimitCount(ip, CountLimitTypeEnum.GOOGLE) ;
				jsonObject.accumulate("code", -1) ;
				jsonObject.accumulate("msg", "GOOGLE验证码有误，您还有"+(google_limit-1)+"次机会");
				return jsonObject.toString() ;
			}else{
				this.frontValidateService.deleteCountLimite(ip, CountLimitTypeEnum.GOOGLE) ;
			}
		}
		
		if(isUpdate ==1){
			if(!validateMessageCode(fuser, fuser.getFareaCode(), fuser.getFtelephone(), MessageTypeEnum.JIEBANG_MOBILE, oldcode)){
				//手機驗證錯誤
				 this.frontValidateService.updateLimitCount(ip, CountLimitTypeEnum.TELEPHONE) ;
				jsonObject.accumulate("code", -1) ;
				jsonObject.accumulate("msg", "旧手机验证码有误，您还有"+(tel_limit-1)+"次机会");
				return jsonObject.toString() ;
			}else{
				this.frontValidateService.deleteCountLimite(ip, CountLimitTypeEnum.TELEPHONE) ;
			}
		}
		
		if(validateMessageCode(fuser, areaCode, phone, MessageTypeEnum.BANGDING_MOBILE, newcode)){
			//判斷手機是否被綁定了
			List<Fuser> fusers = this.frontUserService.findUserByProperty("ftelephone", phone) ;
			if(fusers.size()>0){//手機號碼已經被綁定了
				jsonObject.accumulate("code", -1) ;
				jsonObject.accumulate("msg", "手机号码已存在");
				return jsonObject.toString() ;
			}
			
			
			if(vcodeValidate(request, imgcode) == false ){
				jsonObject.accumulate("code", -1) ;
				jsonObject.accumulate("msg", "请输入正确的图片验证码");
				return jsonObject.toString() ;
			}
			
			fuser.setFareaCode(areaCode) ;
			fuser.setFtelephone(phone) ;
			if(fuser.getFregType() == RegTypeEnum.TEL_VALUE){
				fuser.setFloginName(phone);
				fuser.setFnickName(phone);
			}
			fuser.setFlastUpdateTime(Utils.getTimestamp()) ;
			try {
				if(fuser.isFisTelephoneBind()){
					fuser.setFisTelephoneBind(true) ;
					this.frontUserService.updateFUser(fuser, getSession(request),LogTypeEnum.User_UPDATE_PHONE,ip) ;
				}else{
					fuser.setFisTelephoneBind(true) ;
					this.frontUserService.updateFUser(fuser, getSession(request),LogTypeEnum.User_BIND_PHONE,ip) ;
				}
			} catch (Exception e) {
				jsonObject.accumulate("code", -1) ;
				jsonObject.accumulate("msg", "网络异常");
				return jsonObject.toString() ;
			}finally{
				//成功
				this.validateMap.removeMessageMap(fuser.getFid()+"_"+MessageTypeEnum.BANGDING_MOBILE);
				this.validateMap.removeMessageMap(fuser.getFid()+"_"+MessageTypeEnum.JIEBANG_MOBILE);
			}
			

			this.frontValidateService.deleteCountLimite(ip, CountLimitTypeEnum.TELEPHONE) ;
			jsonObject.accumulate("code", 0) ;
			jsonObject.accumulate("msg", "绑定成功");
			return jsonObject.toString() ;
		}else{
			//手機驗證錯誤
			 this.frontValidateService.updateLimitCount(ip, CountLimitTypeEnum.TELEPHONE) ;
			jsonObject.accumulate("code", -1) ;
			jsonObject.accumulate("msg", "新手机验证码有误，您还有"+(tel_limit-1)+"次机会");
			return jsonObject.toString() ;
		}
	}
	
	/*
	 * 發送驗證短信
	 * http://localhost:8899/user/sendMsg.html?random=86&areaCode=86&msgType=0&type=17
	 * */
	@ResponseBody
	@RequestMapping(value="/user/sendMsg")
	public String sendMsg(
			HttpServletRequest request,
			@RequestParam(required=true) int type
			) throws Exception{
		
		String areaCode = "86" ;
		String phone = request.getParameter("phone") ;
		String vcode = request.getParameter("vcode") ;
		
		//注册类型免登陆可以发送
		JSONObject jsonObject = new JSONObject() ;
		
		if(type != MessageTypeEnum.REG_CODE &&type != MessageTypeEnum.FIND_PASSWORD && GetSession(request)==null){
			jsonObject.accumulate("code", -1) ;
			jsonObject.accumulate("msg", "非法提交");
			return jsonObject.toString() ;
		}
		
		if(type<MessageTypeEnum.BEGIN || type>MessageTypeEnum.END){
			jsonObject.accumulate("code", -1) ;
			jsonObject.accumulate("msg", "非法提交");
			return jsonObject.toString() ;
		}
		
		//注册需要验证码
		if(type == MessageTypeEnum.REG_CODE||type == MessageTypeEnum.FIND_PASSWORD){
			if(vcodeValidate(request, vcode) == false ){
				jsonObject.accumulate("code", -1) ;
				jsonObject.accumulate("msg", "请输入正确的图片验证码");
				return jsonObject.toString() ;
			}
		}
		
		String ip = Utils.getIp(request) ;
		int tel_limit = this.frontValidateService.getLimitCount(ip, CountLimitTypeEnum.TELEPHONE) ;
		if(tel_limit<=0){
			jsonObject.accumulate("code", -1) ;
			jsonObject.accumulate("msg", "验证码多次错误，请稍后再试");
			return jsonObject.toString() ;
		}
		
		Fuser fuser = null ;
		if(type !=MessageTypeEnum.REG_CODE){
			if(type == MessageTypeEnum.FIND_PASSWORD){
				List<Fuser> fusers = this.utilsService.list(0, 0, " where ftelephone=? ", false, Fuser.class,new Object[]{phone}) ;
				if(fusers.size()==1){
					fuser = fusers.get(0) ;
				}else{
					jsonObject.accumulate("code", -1) ;
					jsonObject.accumulate("msg", "手机号码错误");
					return jsonObject.toString() ;
				}
			}else{
				fuser = this.frontUserService.findById(GetSession(request).getFid()) ;
				if(type == MessageTypeEnum.BANGDING_MOBILE){
//					if(fuser.isFisTelephoneBind()){
//						jsonObject.accumulate("code", -1) ;
//						jsonObject.accumulate("msg", "您已绑定手机");
//						return jsonObject.toString() ;
//					}
					if(phone == null || phone.trim().length() ==0){
						jsonObject.accumulate("code", -1) ;
						jsonObject.accumulate("msg", "绑定的手机号码不能为空");
						return jsonObject.toString() ;
					}
					if(!phone.matches("^\\d{10,14}$")){//手機格式不對
						jsonObject.accumulate("code", -1) ;
						jsonObject.accumulate("msg", "手机号码格式不正确，请重新输入");
						return jsonObject.toString() ;
					}
					List<Fuser> fusers = this.utilsService.list(0, 0, " where ftelephone=? ", false, Fuser.class,new Object[]{phone}) ;
					if(fusers.size() >0){
						jsonObject.accumulate("code", -1) ;
						jsonObject.accumulate("msg", "手机号码已存在");
						return jsonObject.toString() ;
					}
				}else{
					if(!fuser.isFisTelephoneBind()){
						jsonObject.accumulate("code", -1) ;
						jsonObject.accumulate("msg", "您没有绑定手机");
						return jsonObject.toString() ;
					}else{
						phone = fuser.getFtelephone() ;
					}
				}
		}
		}else{
			boolean flag = this.frontUserService.isTelephoneExists(phone) ;
			if(flag){
				jsonObject.accumulate("code", -1) ;
				jsonObject.accumulate("msg", "手机号码已存在");
				return jsonObject.toString() ;
			}
		}
		
		if(MessageTypeEnum.REG_CODE == type ){
			//注册
			SendMessage(Utils.getIp(request), areaCode, phone, type) ;
		}else if(MessageTypeEnum.BANGDING_MOBILE == type ){
			//注册
			SendMessage(fuser,fuser.getFid(), areaCode, phone, type) ;
		}else{
			boolean sendMessage = SendMessage(fuser,fuser.getFid(), fuser.getFareaCode(), fuser.getFtelephone(), type) ;
			if(!sendMessage){
				jsonObject.accumulate("code", -1) ;
				jsonObject.accumulate("msg", "短信验证码获取频繁，请2分钟之后再试！");
				return jsonObject.toString() ;
			}
		}


		jsonObject.accumulate("code", 0) ;
		jsonObject.accumulate("msg", "验证码已经发送，请查收");
		return jsonObject.toString() ;
	
	}
	
	//发送邮件验证码
	@ResponseBody
	@RequestMapping(value="/user/sendMailCode")
	public String sendMailCode(
			HttpServletRequest request,
			@RequestParam(required=true) int type
			) throws Exception{
		String email = request.getParameter("email") ;
		String vcode = request.getParameter("vcode") ;
		
		//注册类型免登陆可以发送
		JSONObject jsonObject = new JSONObject() ;
		
		if(type != SendMailTypeEnum.RegMail && GetSession(request)==null){
			jsonObject.accumulate("code", -1) ;
			jsonObject.accumulate("msg", "非法提交");
			return jsonObject.toString() ;
		}
		
		if(type<SendMailTypeEnum.BEGIN || type>SendMailTypeEnum.END){
			jsonObject.accumulate("code", -1) ;
			jsonObject.accumulate("msg", "非法提交");
			return jsonObject.toString() ;
		}
		
		//注册需要验证码
		if(type == SendMailTypeEnum.RegMail){
			if(vcodeValidate(request, vcode) == false ){
				jsonObject.accumulate("code", -1) ;
				jsonObject.accumulate("msg", "请输入正确的图片验证码");
				return jsonObject.toString() ;
			}
		}
		
		String ip = Utils.getIp(request) ;
		int tel_limit = this.frontValidateService.getLimitCount(ip, CountLimitTypeEnum.REG_MAIL) ;
		if(tel_limit<=0){
			jsonObject.accumulate("code", -1) ;
			jsonObject.accumulate("msg", "验证码多次错误，请稍后再试");
			return jsonObject.toString() ;
		}
		
		
		SendMail(Utils.getIp(request), email, type) ;
		
		jsonObject.accumulate("code", 0) ;
		jsonObject.accumulate("msg", "验证码已经发送，请查收");
		return jsonObject.toString() ;
		
	}
	
	/**
	 * 增加提现支付宝
	 * **/
/*	@ResponseBody
	@RequestMapping("/user/updateOutAlipayAddress")
	public String updateOutAlipayAddress(
			HttpServletRequest request,
			@RequestParam(required=true)String account,
			@RequestParam(required=false,defaultValue="0")String phoneCode,
			@RequestParam(required=false,defaultValue="0")String totpCode,
			@RequestParam(required=true)String payeeAddr//开户姓名
			) throws Exception{
		JSONObject jsonObject = new JSONObject() ;
		
		Fuser fuser = this.frontUserService.findById(GetSession(request).getFid()) ;
		boolean isGoogleBind = fuser.getFgoogleBind() ;
		boolean isTelephoneBind = fuser.isFisTelephoneBind() ;
		if(!isGoogleBind && !isTelephoneBind){
			jsonObject.accumulate("code", -1) ;
			jsonObject.accumulate("msg", "请先绑定GOOGLE验证或手机号码") ;
			return jsonObject.toString();
		}
		
		account = HtmlUtils.htmlEscape(account);
		phoneCode = HtmlUtils.htmlEscape(phoneCode);
		totpCode = HtmlUtils.htmlEscape(totpCode);
		payeeAddr = HtmlUtils.htmlEscape(payeeAddr);
		
		if(fuser.getFrealName().equals(payeeAddr)){
			jsonObject.accumulate("code", -1) ;
			jsonObject.accumulate("msg", "支付宝姓名必须与您的实名认证姓名一致") ;
			return jsonObject.toString();
		}
		
		if(account.length() >200){
			jsonObject.accumulate("code", -1) ;
			jsonObject.accumulate("msg", "支付宝帐号有误") ;
			return jsonObject.toString();
		}
		
		String ip = Utils.getIp(request) ;
		int google_limit = this.frontValidateService.getLimitCount(ip, CountLimitTypeEnum.GOOGLE) ;
		int tel_limit = this.frontValidateService.getLimitCount(ip, CountLimitTypeEnum.TELEPHONE) ;
		
		if(fuser.isFisTelephoneBind()){
			if(tel_limit<=0){
				jsonObject.accumulate("code", -1) ;
				jsonObject.accumulate("msg", "手机验证码错误，请稍候再试") ;
				return jsonObject.toString();
			}else{
				if(!validateMessageCode(fuser, fuser.getFareaCode(), fuser.getFtelephone(), MessageTypeEnum.CNY_ACCOUNT_WITHDRAW, phoneCode)){
					//手機驗證錯誤
					this.frontValidateService.updateLimitCount(ip, CountLimitTypeEnum.TELEPHONE) ;
					jsonObject.accumulate("code", -1) ;
					jsonObject.accumulate("msg", "手机验证码错误，您还有"+(tel_limit-1)+"次机会") ;
					return jsonObject.toString();
				}else{
					this.frontValidateService.deleteCountLimite(ip, CountLimitTypeEnum.TELEPHONE) ;
				}
			}
		}
		
		
		if(fuser.getFgoogleBind()){
			if(google_limit<=0){
				jsonObject.accumulate("code", -1) ;
				jsonObject.accumulate("msg", "GOOGLE验证码错误，请稍候再试") ;
				return jsonObject.toString();
			}else{
				boolean googleAuth = GoogleAuth.auth(Long.parseLong(totpCode),fuser.getFgoogleAuthenticator()) ;
				
				if(!googleAuth){
					//谷歌驗證失敗
					this.frontValidateService.updateLimitCount(ip, CountLimitTypeEnum.GOOGLE) ;
					jsonObject.accumulate("code", -1) ;
					jsonObject.accumulate("msg", "GOOGLE验证码错误，您还有"+(google_limit-1)+"次机会") ;
					return jsonObject.toString();
				}else{
					this.frontValidateService.deleteCountLimite(ip, CountLimitTypeEnum.GOOGLE) ;
				}
			}
			
		}
		
		//成功
		try {
			FbankinfoWithdraw fbankinfoWithdraw = new FbankinfoWithdraw();
			fbankinfoWithdraw.setFbankNumber(account) ;
			fbankinfoWithdraw.setFbankType(0) ;
			fbankinfoWithdraw.setFcreateTime(Utils.getTimestamp()) ;
			fbankinfoWithdraw.setFname("支付宝帐号") ;
			fbankinfoWithdraw.setFstatus(BankInfoStatusEnum.NORMAL_VALUE) ;
			fbankinfoWithdraw.setFaddress(payeeAddr);
			fbankinfoWithdraw.setFothers(null);
			fbankinfoWithdraw.setFuser(fuser);
			this.frontUserService.updateBankInfoWithdraw(fbankinfoWithdraw) ;
			jsonObject.accumulate("code", 0) ;
			jsonObject.accumulate("msg", "操作成功") ;
			this.validateMap.removeMessageMap(fuser.getFid()+"_"+MessageTypeEnum.CNY_ACCOUNT_WITHDRAW);
		} catch (Exception e) {
			jsonObject.accumulate("code", -1) ;
			jsonObject.accumulate("msg", "网络异常") ;
		}
		
		return jsonObject.toString() ;
	}
	*/
	
	/**
	 * 增加提现银行 
	 * */
	@ResponseBody
	@RequestMapping("/user/updateOutAddress")
	public String updateOutAddress(
			HttpServletRequest request,
			@RequestParam(required=true)String account,
			@RequestParam(required=false,defaultValue="0")String phoneCode,
			@RequestParam(required=false,defaultValue="0")String totpCode,
			@RequestParam(required=true)int openBankType,
			@RequestParam(required=true)String address,
			@RequestParam(required=true)String prov,
			@RequestParam(required=true)String city,
			@RequestParam(required=true,defaultValue="0")String dist,
			@RequestParam(required=true)String payeeAddr//开户姓名
			) throws Exception{
		JSONObject jsonObject = new JSONObject() ;
		
		Fuser fuser = this.frontUserService.findById(GetSession(request).getFid()) ;
		boolean isGoogleBind = fuser.getFgoogleBind() ;
		boolean isTelephoneBind = fuser.isFisTelephoneBind() ;
		if(!isGoogleBind && !isTelephoneBind){
			jsonObject.accumulate("code", -1) ;
			jsonObject.accumulate("msg", "请先绑定GOOGLE验证或手机号码") ;
			return jsonObject.toString();
		}
		
		address = HtmlUtils.htmlEscape(address);
		account = HtmlUtils.htmlEscape(account);
		phoneCode = HtmlUtils.htmlEscape(phoneCode);
		totpCode = HtmlUtils.htmlEscape(totpCode);
		prov = HtmlUtils.htmlEscape(prov);
		city = HtmlUtils.htmlEscape(city);
		dist = HtmlUtils.htmlEscape(dist);
		payeeAddr = fuser.getFrealName();
		
		String last = prov+city;
		if(!dist.equals("0")){
			last = last+dist;
		}
		
		if(account.length() < 10){
			jsonObject.accumulate("code", -1) ;
			jsonObject.accumulate("msg", "银行帐号不合法") ;
			return jsonObject.toString();
		}
		
		if(address.length() > 300){
			jsonObject.accumulate("code", -1) ;
			jsonObject.accumulate("msg", "详细地址太长") ;
			return jsonObject.toString();
		}
		
		if(last.length() > 50){
			jsonObject.accumulate("code", -1) ;
			jsonObject.accumulate("msg", "非法操作") ;
			return jsonObject.toString();
		}
		
//		if(fuser.getFrealName().equals(payeeAddr)){
//			jsonObject.accumulate("code", -1) ;
//			jsonObject.accumulate("msg", "银行卡账号名必须与您的实名认证姓名一致") ;
//			return jsonObject.toString();
//		}
		
		String bankName = BankTypeEnum.getEnumString(openBankType);
		if(bankName == null || bankName.trim().length() ==0){
			jsonObject.accumulate("code", -1) ;
			jsonObject.accumulate("msg", "开户银行有误") ;
			return jsonObject.toString();
		}
		boolean bankB = false;
		String filter = "where fuser.fid='"+fuser.getFid()+"' and fbankType >0";
		List<FbankinfoWithdraw> bankinfos = this.frontUserService.findFbankinfoWithdrawByFuser(0, 0, filter, false);
		for (FbankinfoWithdraw fbankinfoWithdraw : bankinfos) {
			if(fbankinfoWithdraw.getFbankNumber().equals(account)){
				bankB = true;
				break;
			}
		}
		if(bankB){
			jsonObject.accumulate("code", -1);
			jsonObject.accumulate("msg", "此银行卡号已存在，不可重复添加") ;
			return jsonObject.toString();
		}
		String ip = Utils.getIp(request) ;
		int google_limit = this.frontValidateService.getLimitCount(ip, CountLimitTypeEnum.GOOGLE) ;
		int tel_limit = this.frontValidateService.getLimitCount(ip, CountLimitTypeEnum.TELEPHONE) ;
		//2017-02-21 liuruichen 去掉短信验证与谷歌验证
//		if(fuser.isFisTelephoneBind()){
//			if(tel_limit<=0){
//				jsonObject.accumulate("code", -1) ;
//				jsonObject.accumulate("msg", "手机验证码错误，请稍候再试") ;
//				return jsonObject.toString();
//			}else{
//				if(!validateMessageCode(fuser, fuser.getFareaCode(), fuser.getFtelephone(), MessageTypeEnum.CNY_ACCOUNT_WITHDRAW, phoneCode)){
//					//手機驗證錯誤
//					this.frontValidateService.updateLimitCount(ip, CountLimitTypeEnum.TELEPHONE) ;
//					jsonObject.accumulate("code", -1) ;
//					jsonObject.accumulate("msg", "手机验证码错误，您还有"+(tel_limit-1)+"次机会") ;
//					return jsonObject.toString();
//				}else{
//					this.frontValidateService.deleteCountLimite(ip, CountLimitTypeEnum.TELEPHONE) ;
//				}
//			}
//		}
//		
//		
//		if(fuser.getFgoogleBind()){
//			if(google_limit<=0){
//				jsonObject.accumulate("code", -1) ;
//				jsonObject.accumulate("msg", "GOOGLE验证码错误，请稍候再试") ;
//				return jsonObject.toString();
//			}else{
//				boolean googleAuth = GoogleAuth.auth(Long.parseLong(totpCode),fuser.getFgoogleAuthenticator()) ;
//				
//				if(!googleAuth){
//					//谷歌驗證失敗
//					this.frontValidateService.updateLimitCount(ip, CountLimitTypeEnum.GOOGLE) ;
//					jsonObject.accumulate("code", -1) ;
//					jsonObject.accumulate("msg", "GOOGLE验证码错误，您还有"+(google_limit-1)+"次机会") ;
//					return jsonObject.toString();
//				}else{
//					this.frontValidateService.deleteCountLimite(ip, CountLimitTypeEnum.GOOGLE) ;
//				}
//			}
//			
//		}
		
		//成功
		try {
			FbankinfoWithdraw fbankinfoWithdraw = new FbankinfoWithdraw();
			fbankinfoWithdraw.setFbankNumber(account) ;
			fbankinfoWithdraw.setFbankType(openBankType) ;
			fbankinfoWithdraw.setFcreateTime(Utils.getTimestamp()) ;
			fbankinfoWithdraw.setFname(bankName) ;
			fbankinfoWithdraw.setFstatus(BankInfoStatusEnum.NORMAL_VALUE) ;
			fbankinfoWithdraw.setFaddress(last);
			fbankinfoWithdraw.setFothers(address);
			fbankinfoWithdraw.setFuser(fuser);
			this.frontUserService.updateBankInfoWithdraw(fbankinfoWithdraw) ;
			jsonObject.accumulate("code", 0) ;
			jsonObject.accumulate("msg", "操作成功") ;
		} catch (Exception e) {
			jsonObject.accumulate("code", -1) ;
			jsonObject.accumulate("msg", "网络异常") ;
		}finally{
			this.validateMap.removeMessageMap(fuser.getFid()+"_"+MessageTypeEnum.CNY_ACCOUNT_WITHDRAW);
		}
		
		return jsonObject.toString() ;
	}
	

	@ResponseBody
	@RequestMapping("/user/deleteBankAddress")
	public String deleteBankAddress(
			HttpServletRequest request,
			@RequestParam(required=true)String fid
			) throws Exception{
		JSONObject jsonObject = new JSONObject() ;
		
		Fuser fuser = this.frontUserService.findById(GetSession(request).getFid()) ;
		FbankinfoWithdraw fbankinfoWithdraw = this.frontUserService.findByIdWithBankInfos(fid);
		if(fbankinfoWithdraw == null){
			jsonObject.accumulate("code", -1) ;
			jsonObject.accumulate("msg", "记录不存在") ;
			return jsonObject.toString() ;
		}
		boolean s = fuser.getFid().equals(fbankinfoWithdraw.getFuser().getFid());
		boolean ss = !fuser.getFid().equals(fbankinfoWithdraw.getFuser().getFid());
		if(!fuser.getFid().equals(fbankinfoWithdraw.getFuser().getFid())){
			jsonObject.accumulate("code", -1) ;
			jsonObject.accumulate("msg", "非法操作") ;
			return jsonObject.toString() ;
		}
		//成功
		try {
			this.frontUserService.updateDelBankInfoWithdraw(fbankinfoWithdraw);
			jsonObject.accumulate("code", 0) ;
			jsonObject.accumulate("msg", "操作成功") ;
		} catch (Exception e) {
			jsonObject.accumulate("code", -1) ;
			jsonObject.accumulate("msg", "网络异常") ;
		}
		
		return jsonObject.toString() ;
	}
	
	@ResponseBody
	@RequestMapping("/user/detelCoinAddress")
	public String detelCoinAddress(
			HttpServletRequest request,
			@RequestParam(required=true)String fid
			) throws Exception{
		JSONObject jsonObject = new JSONObject() ;
		
		Fuser fuser = this.frontUserService.findById(GetSession(request).getFid()) ;
		FvirtualaddressWithdraw virtualaddressWithdraw = this.frontVirtualCoinService.findFvirtualaddressWithdraw(fid);
		if(virtualaddressWithdraw == null){
			jsonObject.accumulate("code", -1) ;
			jsonObject.accumulate("msg", "记录不存在") ;
			return jsonObject.toString() ;
		}
		if(!fuser.getFid() .equals(virtualaddressWithdraw.getFuser().getFid())){
			jsonObject.accumulate("code", -1) ;
			jsonObject.accumulate("msg", "非法操作") ;
			return jsonObject.toString() ;
		}
		//成功
		try {
			this.frontVirtualCoinService.updateDelFvirtualaddressWithdraw(virtualaddressWithdraw);
			jsonObject.accumulate("code", 0) ;
			jsonObject.accumulate("msg", "操作成功") ;
		} catch (Exception e) {
			jsonObject.accumulate("code", -1) ;
			jsonObject.accumulate("msg", "网络异常") ;
		}
		
		return jsonObject.toString() ;
	}

	@ResponseBody
	@RequestMapping("/user/modifyPwd")
	public String modifyPwd(
			HttpServletRequest request,
			@RequestParam(required=true) String newPwd,
			@RequestParam(required=false,defaultValue="0") String originPwd,
			@RequestParam(required=false,defaultValue="0")String phoneCode,
			@RequestParam(required=false,defaultValue="0")int pwdType,
			@RequestParam(required=true) String reNewPwd,
			@RequestParam(required=false,defaultValue="0")String totpCode
			) throws Exception{
		JSONObject jsonObject = new JSONObject() ;
		Fuser fuser = this.frontUserService.findById(GetSession(request).getFid()) ;
		
		if(!newPwd.equals(reNewPwd)){
			jsonObject.accumulate("code", -3) ;
			jsonObject.accumulate("msg", "两次输入密码不一样") ;
			return jsonObject.toString() ;//两次输入密码不一样
		}
		
		if(!fuser.isFisTelephoneBind() && !fuser.getFgoogleBind()){
			jsonObject.accumulate("resultCode", -13) ;
			jsonObject.accumulate("msg", "需要绑定绑定谷歌或者手机号码才能修改密码") ;
			return jsonObject.toString() ;//需要绑定绑定谷歌或者电话才能修改密码
		}
		
		if(pwdType==0){
			//修改登陆密码
			if(!fuser.getFloginPassword().equals(Utils.MD5(originPwd))){
				jsonObject.accumulate("code", -5) ;
				jsonObject.accumulate("msg", "原始登陆密码错误") ;
				return jsonObject.toString() ;//原始密码错误
			}
			
		}else{
			//修改交易密码
			if(fuser.getFtradePassword()!=null && fuser.getFtradePassword().trim().length() >0){
				if(!fuser.getFtradePassword().equals(Utils.MD5(originPwd))){
					jsonObject.accumulate("code", -5) ;
					jsonObject.accumulate("msg", "原始交易密码错误") ;
					return jsonObject.toString() ;//原始密码错误
				}
			}
		}
		
		if(pwdType==0){
			//修改交易密码
			if(fuser.getFloginPassword().equals(Utils.MD5(newPwd))){
				jsonObject.accumulate("code", -10) ;
				jsonObject.accumulate("msg", "新的登陆密码与原始密码相同，修改失败") ;
				return jsonObject.toString() ;
			}
		}else{
			//修改交易密码
			if(fuser.getFtradePassword()!=null && fuser.getFtradePassword().trim().length() >0
					&&fuser.getFtradePassword().equals(Utils.MD5(newPwd))){
				jsonObject.accumulate("code", -10) ;
				jsonObject.accumulate("msg", "新的交易密码与原始密码相同，修改失败") ;
				return jsonObject.toString() ;
			}
		}
		
		String ip = Utils.getIp(request) ;
		if(fuser.isFisTelephoneBind()){
			int mobil_limit = this.frontValidateService.getLimitCount(ip, CountLimitTypeEnum.TELEPHONE) ;
			if(mobil_limit<=0){
				jsonObject.accumulate("code", -7) ;
				jsonObject.accumulate("msg", "手机验证码有误，请稍候再试") ;
				return jsonObject.toString() ;
			}else{
				boolean mobilValidate = false ;
				if(pwdType==0){
					mobilValidate = validateMessageCode(fuser, fuser.getFareaCode(), fuser.getFtelephone(), MessageTypeEnum.CHANGE_LOGINPWD, phoneCode) ;
				}else{
					mobilValidate = validateMessageCode(fuser, fuser.getFareaCode(), fuser.getFtelephone(), MessageTypeEnum.CHANGE_TRADEPWD, phoneCode) ;
				}
				if(!mobilValidate){
					jsonObject.accumulate("code", -7) ;
					jsonObject.accumulate("msg", "手机验证码有误，您还有"+(mobil_limit-1)+"次机会") ;
					this.frontValidateService.updateLimitCount(ip, CountLimitTypeEnum.TELEPHONE) ;
					return jsonObject.toString() ;
				}else{
					this.frontValidateService.deleteCountLimite(ip, CountLimitTypeEnum.TELEPHONE) ;
				}
			}
		}
		
		if(fuser.getFgoogleBind()){
			int google_limit = this.frontValidateService.getLimitCount(ip, CountLimitTypeEnum.GOOGLE) ;
			if(google_limit<=0){
				jsonObject.accumulate("code", -6) ;
				jsonObject.accumulate("msg", "GOOGLE验证码有误，请稍候再试") ;
				return jsonObject.toString() ;
			}else{
				if(!GoogleAuth.auth(Long.parseLong(totpCode), fuser.getFgoogleAuthenticator())){
					jsonObject.accumulate("code", -6) ;
					jsonObject.accumulate("msg", "GOOGLE验证码有误，您还有"+(google_limit-1)+"次机会") ;
					this.frontValidateService.updateLimitCount(ip, CountLimitTypeEnum.GOOGLE) ;
					return jsonObject.toString() ;
				}else{
					this.frontValidateService.deleteCountLimite(ip, CountLimitTypeEnum.GOOGLE) ;
				}
			}
		}
		
		try {
			if(pwdType==0){
				//修改登陆密码
				fuser.setFloginPassword(Utils.MD5(newPwd)) ;
				this.frontUserService.updateFUser(fuser, getSession(request),LogTypeEnum.User_UPDATE_LOGIN_PWD,ip) ;
				request.getSession().removeAttribute("login_user");
			}else{
				//修改交易密码
				int logType=0;
				if(fuser.getFtradePassword()!=null && fuser.getFtradePassword().trim().length() >0){
					logType = LogTypeEnum.User_UPDATE_TRADE_PWD;
				}else{
					logType = LogTypeEnum.User_SET_TRADE_PWD;
				}
				fuser.setFtradePassword(Utils.MD5(newPwd)) ;
				this.frontUserService.updateFUser(fuser, getSession(request),logType,ip) ;
			}
		} catch (Exception e) {
			jsonObject.accumulate("code", -3) ;
			jsonObject.accumulate("msg", "网络异常") ;
			return jsonObject.toString() ;
		}finally{
			this.validateMap.removeMessageMap(fuser.getFid()+"_"+MessageTypeEnum.CHANGE_LOGINPWD);
			this.validateMap.removeMessageMap(fuser.getFid()+"_"+MessageTypeEnum.CHANGE_TRADEPWD);
		}
		
		jsonObject.accumulate("code", 0) ;
		jsonObject.accumulate("msg", "操作成功") ;
		return jsonObject.toString() ;
	}
	
	
	@ResponseBody
	@RequestMapping("/user/modifyWithdrawBtcAddr")
	public String modifyWithdrawBtcAddr(
			HttpServletRequest request,
			@RequestParam(required=false,defaultValue="0")String phoneCode,
			@RequestParam(required=false,defaultValue="0")String totpCode,
			@RequestParam(required=true)String symbol,
			@RequestParam(required=true)String withdrawAddr,
			@RequestParam(required=true)String withdrawBtcPass,
			@RequestParam(required=false,defaultValue="REMARK")String withdrawRemark
			) throws Exception{
		JSONObject jsonObject = new JSONObject() ;
		withdrawAddr = HtmlUtils.htmlEscape(withdrawAddr.trim());
		withdrawRemark = HtmlUtils.htmlEscape(withdrawRemark.trim());
		Fuser fuser = this.frontUserService.findById(GetSession(request).getFid()) ;
//		if(!fuser.getFgoogleBind() && !fuser.isFisTelephoneBind()){
//			jsonObject.accumulate("code", -1) ;
//			jsonObject.accumulate("msg","请先绑定GOOGLE验证或手机号码") ;
//			return jsonObject.toString() ;
//		}
		
		if(fuser.getFtradePassword() == null || fuser.getFtradePassword().trim().length() ==0){
			jsonObject.accumulate("code", -4) ;
			jsonObject.accumulate("msg","请先设置交易密码") ;
			return jsonObject.toString() ;
		}
		
		if(!fuser.getFtradePassword().equals(Utils.MD5(withdrawBtcPass))){
			jsonObject.accumulate("code", -4) ;
			jsonObject.accumulate("msg","交易密码不正确") ;
			return jsonObject.toString() ;
		}
		
		if(withdrawRemark.length() >100){
			jsonObject.accumulate("code", -4) ;
			jsonObject.accumulate("msg","备注超出长度") ;
			return jsonObject.toString() ;
		}
		
		if(withdrawAddr.length()<26||withdrawAddr.length()>34){
			jsonObject.accumulate("code", -4) ;
			jsonObject.accumulate("msg","提现地址格式有误") ;
			return jsonObject.toString() ;
		}
		
		Fvirtualcointype fvirtualcointype = this.frontVirtualCoinService.findFvirtualCoinById(symbol) ;
		if(fvirtualcointype==null 
				|| fvirtualcointype.getFstatus()==VirtualCoinTypeStatusEnum.Abnormal
				|| !fvirtualcointype.isFIsWithDraw()){
			jsonObject.accumulate("code", -4) ;
			jsonObject.accumulate("msg","该币种不存在") ;
			return jsonObject.toString() ;
		}
		boolean addrB =false;
		String filter = "where fuser.fid='"+fuser.getFid()+"' and fvirtualcointype.fid='"+symbol+"'";
		
		List<FvirtualaddressWithdraw> alls = this.frontVirtualCoinService.findFvirtualaddressWithdraws(0, 0, filter, false);
		for (FvirtualaddressWithdraw fvirtualaddressWithdraw : alls) {
			if(fvirtualaddressWithdraw.getFadderess().equals(withdrawAddr)){
				addrB = true;
				break;
			}
		}
		if(addrB){
			jsonObject.accumulate("code", -4) ;
			jsonObject.accumulate("msg","此虚拟地址已存在，不允许重复添加") ;
			return jsonObject.toString() ;
		}
		//2017-02-21 liuruichen 去掉短信验证与谷歌验证
//		String ip = Utils.getIp(request) ;		
//		if(fuser.isFisTelephoneBind()){
//			int mobil_limit = this.frontValidateService.getLimitCount(ip, CountLimitTypeEnum.TELEPHONE) ;
//			if(mobil_limit<=0){
//				jsonObject.accumulate("code", -3) ;
//				jsonObject.accumulate("msg","手机验证码不正确,请稍候再试") ;
//				return jsonObject.toString() ;
//			}else if(!validateMessageCode(fuser, fuser.getFareaCode(), fuser.getFtelephone(), MessageTypeEnum.VIRTUAL_WITHDRAW_ACCOUNT, phoneCode)){
//				jsonObject.accumulate("code", -3) ;
//				jsonObject.accumulate("msg","手机验证码不正确,您还有"+(mobil_limit-1)+"次机会") ;
//				this.frontValidateService.updateLimitCount(ip, CountLimitTypeEnum.TELEPHONE) ;
//				return jsonObject.toString() ;
//			}else if(mobil_limit<Constant.ErrorCountLimit){
//				this.frontValidateService.deleteCountLimite(ip, CountLimitTypeEnum.TELEPHONE) ;
//			}
//		}
//		
//		if(fuser.getFgoogleBind()){
//			int google_limit = this.frontValidateService.getLimitCount(ip, CountLimitTypeEnum.GOOGLE) ;
//			if(google_limit<=0){
//				jsonObject.accumulate("code", -2) ;
//				jsonObject.accumulate("msg","GOOGLE验证码不正确,请稍候再试") ;
//				return jsonObject.toString() ;
//			}else if(!GoogleAuth.auth(Long.parseLong(totpCode.trim()), fuser.getFgoogleAuthenticator())){
//				jsonObject.accumulate("code", -2) ;
//				jsonObject.accumulate("msg","GOOGLE验证码不正确,您还有"+(google_limit-1)+"次机会") ;
//				this.frontValidateService.updateLimitCount(ip, CountLimitTypeEnum.GOOGLE) ;
//				return jsonObject.toString() ;
//			}else if(google_limit<Constant.ErrorCountLimit){
//				this.frontValidateService.deleteCountLimite(ip, CountLimitTypeEnum.GOOGLE) ;
//			}
//		}
//		
		FvirtualaddressWithdraw fvirtualaddressWithdraw = new FvirtualaddressWithdraw();
		fvirtualaddressWithdraw.setFadderess(withdrawAddr) ;
		fvirtualaddressWithdraw.setFcreateTime(Utils.getTimestamp());
		fvirtualaddressWithdraw.setFremark(withdrawRemark);
		fvirtualaddressWithdraw.setFuser(fuser);
		fvirtualaddressWithdraw.setFvirtualcointype(fvirtualcointype);
		try {
			this.frontVirtualCoinService.updateFvirtualaddressWithdraw(fvirtualaddressWithdraw) ;
			jsonObject.accumulate("code", 0) ;
			jsonObject.accumulate("msg","操作成功") ;
		} catch (Exception e) {
			jsonObject.accumulate("code", -4) ;
			jsonObject.accumulate("msg","网络异常") ;
		}finally{
			this.validateMap.removeMessageMap(fuser.getFid()+"_"+MessageTypeEnum.VIRTUAL_WITHDRAW_ACCOUNT);
		}
 		
		return jsonObject.toString() ;
	}
	

	@ResponseBody
	@RequestMapping(value="/user/validateIdentity",produces={JsonEncode})
	public String validateIdentity(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required=true)String identityNo,
			@RequestParam(required=false,defaultValue="0")int identityType,
			@RequestParam(required=true)String realName
			) throws Exception {
		JSONObject js = new JSONObject();
		realName = HtmlUtils.htmlEscape(realName);
		Fuser fuser = this.frontUserService.findById(GetSession(request).getFid()) ;
		identityNo = identityNo.toLowerCase();
		 String[] ValCodeArr = { "1", "0", "x", "9", "8", "7", "6", "5", "4",  
	                "3", "2" };  
	        String[] Wi = { "7", "9", "10", "5", "8", "4", "2", "1", "6", "3", "7",  
	                "9", "10", "5", "8", "4", "2" };  
			JSONObject jsonObject = new JSONObject();
			if (identityNo.trim().length() != 15 && identityNo.trim().length() != 18) {
				jsonObject.accumulate("code", 1);
				jsonObject.accumulate("msg", "身份证号码长度应该为15位或18位!");
				return jsonObject.toString();
			}
			
			String Ai = "";
	        if (identityNo.length() == 18) {  
	            Ai = identityNo.substring(0, 17);  
	        } else if (identityNo.length() == 15) {  
	            Ai = identityNo.substring(0, 6) + "19" + identityNo.substring(6, 15);  
	        }  
	        if (Utils.isNumeric(Ai) == false) {  
	            jsonObject.accumulate("code", 1);
				jsonObject.accumulate("msg", "身份证号码有误!");
				return jsonObject.toString();
	        }
	        // ================ 出生年月是否有效 ================  
	        String strYear = Ai.substring(6, 10);// 年份  
	        String strMonth = Ai.substring(10, 12);// 月份  
	        String strDay = Ai.substring(12, 14);// 月份  
	        if (Utils.isDate(strYear + "-" + strMonth + "-" + strDay) == false) {  
	        	jsonObject.accumulate("code", 1);
				jsonObject.accumulate("msg", "身份证号码有误!");
				return jsonObject.toString();
	        }  
	        GregorianCalendar gc = new GregorianCalendar();  
	        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");  
	        try {  
	            if ((gc.get(Calendar.YEAR) - Integer.parseInt(strYear)) > 150  
	                    || (gc.getTime().getTime() - s.parse(  
	                            strYear + "-" + strMonth + "-" + strDay).getTime()) < 0) {  
	            	jsonObject.accumulate("code", 1);
					jsonObject.accumulate("msg", "身份证号码有误!");
					return jsonObject.toString();
	            }
	        } catch (NumberFormatException e) {
	            e.printStackTrace();
	        } catch (java.text.ParseException e) {
	            e.printStackTrace();
	        }
	        if (Integer.parseInt(strMonth) > 12 || Integer.parseInt(strMonth) == 0) {
	        	jsonObject.accumulate("code", 1);
				jsonObject.accumulate("msg", "身份证号码有误!");
				return jsonObject.toString();
	        }
	        if (Integer.parseInt(strDay) > 31 || Integer.parseInt(strDay) == 0) {
	        	jsonObject.accumulate("code", 1);
				jsonObject.accumulate("msg", "身份证号码有误!");
				return jsonObject.toString();
	        }  
	        // =====================(end)=====================  
	  
	        // ================ 地区码时候有效 ================  
	        Hashtable h = Utils.getAreaCode();
	        if (h.get(Ai.substring(0, 2)) == null) {
	        	jsonObject.accumulate("code", 1);
				jsonObject.accumulate("msg", "身份证号码有误!");
				return jsonObject.toString();
	        }
	        // ==============================================
	  
	        // ================ 判断最后一位的值 ================  
	        int TotalmulAiWi = 0;
	        for (int i = 0; i < 17; i++) {
	            TotalmulAiWi = TotalmulAiWi
	                    + Integer.parseInt(String.valueOf(Ai.charAt(i)))
	                    * Integer.parseInt(Wi[i]);
	        }
	        int modValue = TotalmulAiWi % 11;
	        String strVerifyCode = ValCodeArr[modValue];
	        Ai = Ai + strVerifyCode;
	        
	        if (identityNo.length() == 18) {
	            if (Ai.equals(identityNo) == false) {
	            	jsonObject.accumulate("code", 1);
					jsonObject.accumulate("msg", "身份证号码有误!");
					return jsonObject.toString();
	            }
	        } else if(identityNo.length() == 15) {
	        	String newId = identityNo.substring(0, 6) + "19" + identityNo.substring(6, 15)+strVerifyCode;
	        	if(!Ai.equals(newId)){
	        		jsonObject.accumulate("code", 1);
					jsonObject.accumulate("msg", "身份证号码有误!");
					return jsonObject.toString();
	        	}
	        }
	        
			if (realName.trim().length() > 50) {
				jsonObject.accumulate("code", 1);
				jsonObject.accumulate("msg", "真实姓名不合法!");
				return jsonObject.toString();
			}
			
			String sql = "where fidentityNo='"+identityNo+"'";
			int count = this.adminService.getAllCount("Fuser", sql);
			if(count >0){
				jsonObject.accumulate("code", 1);
				jsonObject.accumulate("msg", "身份证号码已存在!");
				return jsonObject.toString();
			}
		
		fuser.setFidentityType(0) ;
		fuser.setFidentityNo(identityNo) ;
		fuser.setFrealName(realName) ;
		fuser.setFpostRealValidate(true) ;
		fuser.setFpostRealValidateTime(Utils.getTimestamp()) ;
		
		try {
			String ip = Utils.getIp(request) ;
			this.frontUserService.updateFUser(fuser,getSession(request),LogTypeEnum.User_CERT,ip) ;
			this.SetSession(fuser,request,response) ;
		} catch (Exception e) {
			js.accumulate("code", 1);
			js.accumulate("msg", "证件号码已存在");
			return js.toString();
		}
		js.accumulate("code", 0);
		return js.toString();
	}
	
//	@ResponseBody
//	@RequestMapping(value="/user/sendRegMsg")
//	public String sendRegMsg(
//			HttpServletRequest request,
//			@RequestParam(required=true) int type,
//			@RequestParam(required=true,defaultValue="0") String numbers,
//			@RequestParam(required=true) String validateCode
//			) throws Exception{
//
//		Object session_code = getSession(request).getAttribute("checkcode") ;
//		if(session_code==null || !validateCode.equalsIgnoreCase((String)session_code)){
//			return String.valueOf(-1) ;//验证码错误
//		}
//		if(numbers == null || numbers.trim().length() ==0 || numbers.equals("0")){
//			return "-3";
//		}
//		//手机账号
//		boolean flag = this.frontUserService.isTelephoneExists(numbers) ;
//		if(flag){
//			return "-4";
//		}
//		
//		if(!numbers.matches(Constant.PhoneReg)){
//			return String.valueOf(-44) ;
//		}
//
//		String ip = Utils.getIp(request) ;
//		int tel_limit = this.frontValidateService.getLimitCount(ip, CountLimitTypeEnum.TELEPHONE) ;
//		if(tel_limit<=0){
//			return String.valueOf(-3) ;//多次錯誤，兩小時候在發送吧。
//		}
//		SendMessage(numbers, "86", numbers, type) ;
//		getSession(request).removeAttribute("checkcode") ;
//		return String.valueOf(0) ;
//	}
	
/*	@ResponseBody
	@RequestMapping(value="/user/submitApply",produces={JsonEncode})
	public String submitApply(
			@RequestParam(required=true)int gradeType,
			@RequestParam(required=true)String fphone,
			@RequestParam(required=true)String fdesc,
			@RequestParam(required=true)String frealName
			) throws Exception{
		JSONObject jsonObject = new JSONObject() ;
		Fuser fuser = this.frontUserService.findById(GetSession(request).getFid()) ;
		String filter = "where fstatus="+ApplyinfoStatusEnum.WAIT+" and fuser.fid='"+fuser.getFid()+"'";
		List<Fapplyinfo> fapplyinfos = this.applyinfoService.list(0, 0, filter, false);
		if(fapplyinfos.size() >0){
			jsonObject.accumulate("resultValue", "你已提交申请，请耐心等待审核！") ;
			jsonObject.accumulate("resultCode", 1) ;
			return jsonObject.toString() ;
		}
		if(gradeType !=2 &&gradeType !=3 &&gradeType !=4 &&gradeType !=5){
			jsonObject.accumulate("resultValue", "非法操作！") ;
			jsonObject.accumulate("resultCode", 1) ;
			return jsonObject.toString() ;
		}
		
		if(fdesc.trim().length() >500){
			jsonObject.accumulate("resultValue", "申请描述不能大于500个字符！") ;
			jsonObject.accumulate("resultCode", 1) ;
			return jsonObject.toString() ;
		}
		
		if(frealName.trim().length() >100){
			jsonObject.accumulate("resultValue", "姓名不能大于100个字符！") ;
			jsonObject.accumulate("resultCode", 1) ;
			return jsonObject.toString() ;
		}
		
		fuser.setFlastUpdateTime(Utils.getTimestamp());
		Fapplyinfo applyinfo = new Fapplyinfo();
		applyinfo.setFcreatetime(Utils.getTimestamp());
		applyinfo.setFgrade(gradeType);
		applyinfo.setFphone(HTMLSpirit.delHTMLTag(fphone));
		applyinfo.setFrealname(HTMLSpirit.delHTMLTag(frealName));
		applyinfo.setFreason(HTMLSpirit.delHTMLTag(fdesc));
		applyinfo.setFuser(fuser);
		applyinfo.setFstatus(ApplyinfoStatusEnum.WAIT);
		
		try {
			this.applyinfoService.updateApplyinfo(fuser, applyinfo);
		} catch (Exception e) {
			jsonObject.accumulate("resultValue", "网络异常！") ;
			jsonObject.accumulate("resultCode", 1) ;
			return jsonObject.toString() ;
		}
		
		jsonObject.accumulate("resultCode", 0) ;
		return jsonObject.toString() ;
	}*/
//	
//	@ResponseBody
//	@RequestMapping(value="/user/sign",produces={JsonEncode})
//	public String sign(
//			HttpServletRequest request
//			) throws Exception{
//		JSONObject jsonObject = new JSONObject() ;
//		Fuser fuser = this.frontUserService.findById(GetSession(request).getFid()) ;
//		Fscore fscore = fuser.getFscore();
//		if(fscore.isFissign()){
//			jsonObject.accumulate("resultCode", "1");
//			jsonObject.accumulate("resultValue", "您已签到！");
//			return jsonObject.toString() ;
//		}
//		if(!fscore.isFisopen()){
//			jsonObject.accumulate("resultCode", "1");
//			jsonObject.accumulate("resultValue", "您未开通挖矿功能！");
//			return jsonObject.toString() ;
//		}
//		
//		try {
//			Fsystemargs sys = this.systemArgsService.findById(211);
//			double last = Double.valueOf(sys.getFvalue());
//			double oneMiningQty = Double.valueOf(this.systemArgsService.getValue("oneMiningQty").trim());
//			String filter = "where fuser.fid='"+fuser.getFid()+"' and fvirtualcointype.fid='2'";
//			Fvirtualwallet fvirtualwallet = this.virtualWalletService.list(0, 0, filter, false).get(0);
//			fvirtualwallet.setFtotal(fvirtualwallet.getFtotal()+oneMiningQty);
//			fscore.setFissign(true);
//			Fsignlog signlog = new Fsignlog();
//			signlog.setFcreatetime(Utils.getTimestamp());
//			signlog.setFuser(fuser);
//			signlog.setFqty(oneMiningQty);
//			if(last < oneMiningQty){
//				jsonObject.accumulate("resultCode", "1");
//				jsonObject.accumulate("resultValue", "矿池余额不足！");
//				return jsonObject.toString() ;
//			}
//			last = Utils.getDouble(last -oneMiningQty, 2);
//			sys.setFvalue(String.valueOf(last));
//			try {
//				this.userService.updateUser(fvirtualwallet,fscore,signlog,sys);
//				constantMap.put(sys.getFkey(), sys.getFvalue());
//			} catch (Exception e) {
//				jsonObject.accumulate("resultCode", "1");
//				jsonObject.accumulate("resultValue", "网络异常,请重试");
//				return jsonObject.toString() ;
//			}
//		} catch (Exception e) {
//			jsonObject.accumulate("resultCode", "1");
//			jsonObject.accumulate("resultValue", "系统参数异常，请联系管理员");
//			return jsonObject.toString() ;
//		}
//		
//		jsonObject.accumulate("resultCode", "0");
//		return jsonObject.toString() ;
//	}
//	
//	@ResponseBody
//	@RequestMapping(value="/user/open",produces={JsonEncode})
//	public String open(
//			HttpServletRequest request
//			) throws Exception{
//		JSONObject jsonObject = new JSONObject() ;
//		Fuser fuser = this.frontUserService.findById(GetSession(request).getFid()) ;
//		Fscore fscore = fuser.getFscore();
//		if(fscore.isFisopen()){
//			jsonObject.accumulate("resultCode", "1");
//			jsonObject.accumulate("resultValue", "您已开通挖矿功能！");
//			return jsonObject.toString() ;
//		}
//		
//		try {
//			double oneMiningQty = Double.valueOf(this.systemArgsService.getValue("takeMLCQty").trim());
//			String filter = "where fuser.fid='"+fuser.getFid()+"' and fvirtualcointype.fid='1'";
//			Fvirtualwallet fvirtualwallet = this.virtualWalletService.list(0, 0, filter, false).get(0);
//            if(fvirtualwallet.getFtotal() <oneMiningQty){
//            	jsonObject.accumulate("resultCode", "1");
//    			jsonObject.accumulate("resultValue", "开通需要消耗米粒币"+oneMiningQty+"个，您的余额不足！");
//    			return jsonObject.toString() ;
//            }
//            fvirtualwallet.setFtotal(fvirtualwallet.getFtotal()-oneMiningQty);
//            fscore.setFisopen(true);
//			try {
//				this.userService.updateUser(fvirtualwallet,fscore);
//			} catch (Exception e) {
//				jsonObject.accumulate("resultCode", "1");
//				jsonObject.accumulate("resultValue", "网络异常,请重试");
//				return jsonObject.toString() ;
//			}
//		} catch (Exception e) {
//			jsonObject.accumulate("resultCode", "1");
//			jsonObject.accumulate("resultValue", "系统参数异常，请联系管理员");
//			return jsonObject.toString() ;
//		}
//		
//		jsonObject.accumulate("resultCode", "0");
//		return jsonObject.toString() ;
//	}
}
