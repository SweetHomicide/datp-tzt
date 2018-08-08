package com.ruizton.main.controller.front;

import java.sql.Timestamp;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.hibernate.transaction.JOnASTransactionManagerLookup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.ruizton.main.Enum.CountLimitTypeEnum;
import com.ruizton.main.Enum.LogTypeEnum;
import com.ruizton.main.Enum.MessageTypeEnum;
import com.ruizton.main.Enum.SendMailTypeEnum;
import com.ruizton.main.comm.MultipleValues;
import com.ruizton.main.comm.ValidateMap;
import com.ruizton.main.controller.BaseController;
import com.ruizton.main.model.Emailvalidate;
import com.ruizton.main.model.Fuser;
import com.ruizton.main.model.Fvalidateemail;
import com.ruizton.main.service.front.FrontUserService;
import com.ruizton.main.service.front.FrontValidateService;
import com.ruizton.util.Constant;
import com.ruizton.util.GoogleAuth;
import com.ruizton.util.Utils;

@Controller
public class FrontValidateJsonController extends BaseController {

	@Autowired
	private FrontUserService frontUserService ;
	@Autowired
	private FrontValidateService frontValidateService ;
	@Autowired
	private ValidateMap validateMap ;
	
	//通过邮箱找回登录密码
	@ResponseBody
	@RequestMapping(value = "/validate/sendEmail",produces=JsonEncode)
	public String queryEmail(
			HttpServletRequest request,
			@RequestParam(required=true)String imgcode,
			@RequestParam(required=true)String idcardno,
			@RequestParam(required=true)String email
			) throws Exception{
		JSONObject jsonObject = new JSONObject() ;
		
		if(vcodeValidate(request, imgcode) == false ){
			jsonObject.accumulate("code", -1) ;
			jsonObject.accumulate("msg", "图片验证码错误") ;
			return jsonObject.toString() ;
		}
		
		if(!email.matches(Constant.EmailReg)){
			jsonObject.accumulate("code", -1) ;
			jsonObject.accumulate("msg", "邮箱格式不正确") ;
			return jsonObject.toString() ;
		}
		
		List<Fuser> fusers = this.frontUserService.findUserByProperty("femail", email) ;
		if(fusers.size()==1){
			Fuser fuser = fusers.get(0) ;
			if(fuser.getFisMailValidate()){
				//验证身份证号码
				if(fuser.getFhasRealValidate() == true ){
					if(idcardno.trim().equals(fuser.getFidentityNo()) == false ){
						jsonObject.accumulate("code", -1) ;
						jsonObject.accumulate("msg", "证件号码错误") ;
						return jsonObject.toString() ;
					}
				}
				
				
				String ip = Utils.getIp(request) ;
				Emailvalidate ev = this.validateMap.getMailMap(ip+"_"+SendMailTypeEnum.FindPassword) ;
				if(ev==null || Utils.getTimestamp().getTime()-ev.getFcreateTime().getTime()>5*60*1000L){
					boolean flag = false ;
					try {
						flag = this.frontValidateService.saveSendFindPasswordMail(ip,fuser,email, request) ;
					} catch (Exception e) {
						e.printStackTrace();
					}
					if(flag){
						jsonObject.accumulate("code", 0) ;
						jsonObject.accumulate("msg", "找回密码邮件已经发送，请及时查收") ;
						getSession(request).removeAttribute("checkcode") ;
						return jsonObject.toString() ;
					}else{
						jsonObject.accumulate("code", -4) ;
						jsonObject.accumulate("msg", "网络错误，请稍后再试") ;
						return jsonObject.toString() ;
					}
				}else{
					jsonObject.accumulate("code", -4) ;
					jsonObject.accumulate("msg", "请求过于频繁，请稍后再试") ;
					return jsonObject.toString() ;
				}
			}else{
				jsonObject.accumulate("code", -3) ;
				jsonObject.accumulate("msg", "该邮箱没有通过验证，不能用于找回密码") ;
				return jsonObject.toString() ;
			}
		}else{
			jsonObject.accumulate("code", -2) ;
			jsonObject.accumulate("msg", "用户不存在") ;
			return jsonObject.toString() ;
		}
		
	}
	
	
	//通过手机找回登录密码
	@ResponseBody
	@RequestMapping(value = "/validate/resetPhoneValidation",produces=JsonEncode)
	public String resetPhoneValidation(
			HttpServletRequest request,
			@RequestParam(required=true)String phone,
			@RequestParam(required=true)String msgcode,
			@RequestParam(required=true)String idcardno
			) throws Exception{
		String areaCode = "86" ;
		
		JSONObject jsonObject = new JSONObject() ;
		
		if(!phone.matches(Constant.PhoneReg)){
			jsonObject.accumulate("code", -1) ;
			jsonObject.accumulate("msg", "手机格式不正确") ;
			return jsonObject.toString() ;
		}
		
		List<Fuser> fusers = this.frontUserService.findUserByProperty("ftelephone", phone) ;
		
		if(fusers.size()==1){
			Fuser fuser = fusers.get(0) ;
			
			//短信验证码
			boolean validate = validateMessageCode(fuser, areaCode, phone, MessageTypeEnum.FIND_PASSWORD, msgcode) ;
			if(validate == false ){
				jsonObject.accumulate("code", -1) ;
				jsonObject.accumulate("msg", "短信验证码错误") ;
				return jsonObject.toString() ;
			}
			
			if(fuser.isFisTelephoneBind()){
				//验证身份证号码
				if(fuser.getFhasRealValidate() == true ){
					if(idcardno == null || "".equals(idcardno) || "null".equals(idcardno)){
						jsonObject.accumulate("code", -1) ;
						jsonObject.accumulate("msg", "证件号码不能为空") ;
						return jsonObject.toString() ;
					}
					if(idcardno.trim().equals(fuser.getFidentityNo()) == false ){
						jsonObject.accumulate("code", -1) ;
						jsonObject.accumulate("msg", "证件号码错误") ;
						return jsonObject.toString() ;
					}
				}
				getSession(request).removeAttribute("checkcode") ;
				//往session放数据
				MultipleValues values = new MultipleValues() ;
				values.setValue1(fuser.getFid()) ;
				values.setValue2(Utils.getTimestamp()) ;
				request.getSession().setAttribute("resetPasswordByPhone", values) ;
				
				this.validateMap.removeMessageMap(fuser.getFid()+"_"+MessageTypeEnum.FIND_PASSWORD);
				jsonObject.accumulate("code", 0) ;
				jsonObject.accumulate("msg", "验证成功，将跳转到修改密码界面") ;
				return jsonObject.toString() ;
			}else{
				jsonObject.accumulate("code", -3) ;
				jsonObject.accumulate("msg", "该手机没有通过验证，不能用于找回密码") ;
				return jsonObject.toString() ;
			}
		}else{
			jsonObject.accumulate("code", -2) ;
			jsonObject.accumulate("msg", "用户不存在") ;
			return jsonObject.toString() ;
		}
		
	}
	
	
	//邮件重置密码最后一步
	@ResponseBody
	@RequestMapping(value = "/validate/resetPassword",produces=JsonEncode)
	public String resetPassword(
			HttpServletRequest request,
			@RequestParam(required=true)String newPassword,
			@RequestParam(required=true)String newPassword2,
			@RequestParam(required=true)String fid,
			@RequestParam(required=true)String ev_id,
			@RequestParam(required=true)String newuuid
			) throws Exception{
		JSONObject jsonObject = new JSONObject() ;
		
		boolean flag = false ;
		try {
			flag = this.frontValidateService.canSendFindPwdMsg(fid, ev_id, newuuid) ;
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(!flag){
			jsonObject.accumulate("code", -6) ;
			jsonObject.accumulate("msg", "重置密码页面已经失效") ;
			return jsonObject.toString() ;
		}
		
		//密码
		if(newPassword.length()<6){
			jsonObject.accumulate("code", -2) ;
			jsonObject.accumulate("msg", "密码必须6~15位") ;
			return jsonObject.toString() ;
		}
		
		if(!newPassword.equals(newPassword)){
			jsonObject.accumulate("code", -3) ;
			jsonObject.accumulate("msg", "两次密码输入不一样") ;
			return jsonObject.toString() ;
		}
		
		Fuser fuser = this.frontUserService.findById(fid) ;
		
		if(Utils.MD5(newPassword).equals(fuser.getFloginPassword())){
			jsonObject.accumulate("code", -7);
			jsonObject.accumulate("msg", "新密码不能与旧密码一致！");
			return jsonObject.toString() ;
		}
		
		if(Utils.MD5(newPassword).equals(fuser.getFtradePassword())){
			jsonObject.accumulate("code", -4) ;
			jsonObject.accumulate("msg", "登录密码不能和交易密码一样") ;
			return jsonObject.toString() ;
		}
		
		boolean updateFlag = false ;
		fuser.setFloginPassword(Utils.MD5(newPassword)) ;
		try {
			String ip = Utils.getIp(request) ;
			this.frontUserService.updateFUser(fuser, null,LogTypeEnum.User_FIND_PWD,ip) ;
			updateFlag = true ;
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(updateFlag){
			jsonObject.accumulate("code", 0) ;
			jsonObject.accumulate("msg", "登录密码重置成功") ;
			return jsonObject.toString() ;
		}else{
			jsonObject.accumulate("code", -5) ;
			jsonObject.accumulate("msg", "网络错误，请稍后再试") ;
			return jsonObject.toString() ;
		}
		
	}
	
	
	//手机重置密码最后一步
	@ResponseBody
	@RequestMapping(value = "/validate/resetPasswordPhone",produces=JsonEncode)
	public String resetPasswordPhone(
			HttpServletRequest request,
			@RequestParam(required=true)String newPassword,
			@RequestParam(required=true)String newPassword2
			) throws Exception{
		JSONObject jsonObject = new JSONObject() ;
		
		boolean isValidate = false ;
		Fuser fuser = null ;
		Object resetPasswordByPhone = request.getSession().getAttribute("resetPasswordByPhone") ;
		if(resetPasswordByPhone!=null ){
			MultipleValues values = (MultipleValues)resetPasswordByPhone ;
			String fuserid = (String)values.getValue1() ;
			Timestamp time = (Timestamp)values.getValue2() ;
			
			if(Utils.timeMinus(Utils.getTimestamp(), time)<300){
				fuser = this.frontUserService.findById(fuserid) ;
				isValidate = true ;
			}
		}
		
		if(!isValidate){
			jsonObject.accumulate("code", -6) ;
			jsonObject.accumulate("msg", "重置密码页面已经失效") ;
			return jsonObject.toString() ;
		}
		
		//密码
		if(newPassword.length()<6){
			jsonObject.accumulate("code", -2) ;
			jsonObject.accumulate("msg", "密码必须6~15位") ;
			return jsonObject.toString() ;
		}
		
		if(!newPassword.equals(newPassword)){
			jsonObject.accumulate("code", -3) ;
			jsonObject.accumulate("msg", "两次密码输入不一样") ;
			return jsonObject.toString() ;
		}
		
		if(Utils.MD5(newPassword).equals(fuser.getFloginPassword())){
			jsonObject.accumulate("code", -7);
			jsonObject.accumulate("msg", "新密码不能与旧密码一致！");
			return jsonObject.toString() ;
		}
		
		if(Utils.MD5(newPassword).equals(fuser.getFtradePassword())){
			jsonObject.accumulate("code", -4) ;
			jsonObject.accumulate("msg", "登录密码不能和交易密码一样") ;
			return jsonObject.toString() ;
		}
		
		boolean updateFlag = false ;
		fuser.setFloginPassword(Utils.MD5(newPassword)) ;
		String ip = "";
		try {
			ip = Utils.getIp(request) ;
			this.frontUserService.updateFUser(fuser, null,LogTypeEnum.User_FIND_PWD,ip) ;
			updateFlag = true ;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(updateFlag){
			//this.frontUserService.updatelogFindPwd(fuser,ip);
			request.getSession().removeAttribute("resetPasswordByPhone") ;
			
			jsonObject.accumulate("code", 0) ;
			jsonObject.accumulate("msg", "登录密码重置成功") ;
			return jsonObject.toString() ;
		}else{
			jsonObject.accumulate("code", -5) ;
			jsonObject.accumulate("msg", "网络错误，请稍后再试") ;
			return jsonObject.toString() ;
		}
		
	}
	
	//绑定邮箱
	@ResponseBody
	@RequestMapping("/validate/postValidateMail")
	public String postMail(
			HttpServletRequest request,
			@RequestParam(required=false,defaultValue="0") String email
			) throws Exception{
		JSONObject js = new JSONObject();
		if(GetSession(request) == null){
			js.accumulate("code", -1);
			js.accumulate("msg", "非法操作");
			return js.toString();
		}
		
		//邮箱注册
		boolean isExists = this.frontUserService.isEmailExists(email) ;
		if(isExists){
			js.accumulate("code", -1);
			js.accumulate("msg", "邮箱地址已存在");
			return js.toString();
		}
		
		if(!email.matches(Constant.EmailReg)){
			js.accumulate("code", -1);
			js.accumulate("msg", "邮箱格式不正确");
			return js.toString();
		}
		
		Fuser fuser = this.frontUserService.findById(GetSession(request).getFid()) ;
		
		if(fuser.getFisMailValidate()){
			js.accumulate("code", -1);
			js.accumulate("msg", "您的邮箱已经绑定成功");
			return js.toString();
		}
		
		boolean flag = false ;
		try {
			flag = this.frontUserService.saveValidateEmail(fuser,email,false) ;
		} catch (Exception e) {
			js.accumulate("code", -1);
			js.accumulate("msg", "网络异常");
			return js.toString();
		}
		
		if(flag){
			js.accumulate("code", 0);
			js.accumulate("msg", "发送成功");
			return js.toString();
		}else{
			js.accumulate("code", -1);
			js.accumulate("msg", "半小时内只能发送一次");
			return js.toString();
		}
	}
	
}
