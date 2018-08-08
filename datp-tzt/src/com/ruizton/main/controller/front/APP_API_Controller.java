package com.ruizton.main.controller.front;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.HtmlUtils;

import com.ruizton.main.Enum.BankInfoStatusEnum;
import com.ruizton.main.Enum.BankInfoWithdrawStatusEnum;
import com.ruizton.main.Enum.BankTypeEnum;
import com.ruizton.main.Enum.CapitalOperationInStatus;
import com.ruizton.main.Enum.CapitalOperationTypeEnum;
import com.ruizton.main.Enum.CountLimitTypeEnum;
import com.ruizton.main.Enum.EntrustStatusEnum;
import com.ruizton.main.Enum.EntrustTypeEnum;
import com.ruizton.main.Enum.MessageTypeEnum;
import com.ruizton.main.Enum.RegTypeEnum;
import com.ruizton.main.Enum.RemittanceTypeEnum;
import com.ruizton.main.Enum.SendMailTypeEnum;
import com.ruizton.main.Enum.SubStatusEnum;
import com.ruizton.main.Enum.SubscriptionTypeEnum;
import com.ruizton.main.Enum.SystemBankInfoEnum;
import com.ruizton.main.Enum.UserStatusEnum;
import com.ruizton.main.Enum.VirtualCapitalOperationInStatusEnum;
import com.ruizton.main.Enum.VirtualCapitalOperationOutStatusEnum;
import com.ruizton.main.Enum.VirtualCapitalOperationTypeEnum;
import com.ruizton.main.Enum.VirtualCoinTypeStatusEnum;
import com.ruizton.main.auto.OneDayData;
import com.ruizton.main.auto.RealTimeData;
import com.ruizton.main.comm.ConstantMap;
import com.ruizton.main.comm.ValidateMap;
import com.ruizton.main.controller.BaseController;
import com.ruizton.main.model.Farticle;
import com.ruizton.main.model.FbankinfoWithdraw;
import com.ruizton.main.model.Fcapitaloperation;
import com.ruizton.main.model.Fentrust;
import com.ruizton.main.model.Ffriendlink;
import com.ruizton.main.model.Fintrolinfo;
import com.ruizton.main.model.Flimittrade;
import com.ruizton.main.model.Fscore;
import com.ruizton.main.model.Fsubscription;
import com.ruizton.main.model.Fsubscriptionlog;
import com.ruizton.main.model.Ftradehistory;
import com.ruizton.main.model.Fuser;
import com.ruizton.main.model.Fvirtualaddress;
import com.ruizton.main.model.FvirtualaddressWithdraw;
import com.ruizton.main.model.Fvirtualcaptualoperation;
import com.ruizton.main.model.Fvirtualcointype;
import com.ruizton.main.model.Fvirtualwallet;
import com.ruizton.main.model.Fwallet;
import com.ruizton.main.model.Fwebbaseinfo;
import com.ruizton.main.model.Systembankinfo;
import com.ruizton.main.service.admin.AdminService;
import com.ruizton.main.service.admin.CapitaloperationService;
import com.ruizton.main.service.admin.IntrolinfoService;
import com.ruizton.main.service.admin.SubscriptionLogService;
import com.ruizton.main.service.admin.SubscriptionService;
import com.ruizton.main.service.admin.TransportlogService;
import com.ruizton.main.service.admin.UserService;
import com.ruizton.main.service.admin.VirtualCoinService;
import com.ruizton.main.service.admin.VirtualWalletService;
import com.ruizton.main.service.admin.WalletService;
import com.ruizton.main.service.front.FrontAccountService;
import com.ruizton.main.service.front.FrontBankInfoService;
import com.ruizton.main.service.front.FrontTradeService;
import com.ruizton.main.service.front.FrontUserService;
import com.ruizton.main.service.front.FrontValidateService;
import com.ruizton.main.service.front.FrontVirtualCoinService;
import com.ruizton.main.service.front.UtilsService;
import com.ruizton.util.Comm;
import com.ruizton.util.Constant;
import com.ruizton.util.HTMLSpirit;
import com.ruizton.util.Utils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Controller
@Scope("prototype")
public class APP_API_Controller extends BaseController {

	@Autowired
	private FrontUserService frontUserService;
	@Autowired
	private RealTimeData realTimeData;
	@Autowired
	private FrontVirtualCoinService frontVirtualCoinService;
	@Autowired
	private FrontTradeService frontTradeService;
	@Autowired
	private OneDayData oneDayData;
	@Autowired
	private AdminService adminService;
	@Autowired
	private ConstantMap constantMap;
	@Autowired
	private UserService userService;
	@Autowired
	private VirtualCoinService virtualCoinService;
	@Autowired
	private IntrolinfoService introlinfoService;
	@Autowired
	private UtilsService utilsService;
	@Autowired
	private FrontAccountService frontAccountService;
	@Autowired
	private FrontValidateService frontValidateService;
	@Autowired
	private CapitaloperationService capitaloperationService;
	@Autowired
	private WalletService walletService;
	@Autowired
	private TransportlogService transportlogService;
	@Autowired
	private ValidateMap validateMap;
	@Autowired
	private FrontBankInfoService frontBankInfoService;
	@Autowired
	private SubscriptionService subscriptionService;
	@Autowired
	private UserService userSerivce;
	@Autowired
	private SubscriptionLogService subscriptionLogService;
	@Autowired
	private ConstantMap map;
	@Autowired
	private VirtualWalletService virtualWalletService;

	public final static String Result = "result";
	public final static String ErrorCode = "code";
	public final static String Value = "value";
	public final static String LoginToken = "loginToken";
	public final static String CurrentPage = "currentPage";
	public final static String TotalPage = "totalPage";
	public final static String LastUpdateTime = "lastUpdateTime";
	public final static String Fid = "Fid";

	private String curLoginToken = null;
	int maxResult = Constant.AppRecordPerPage;

	@ResponseBody
	@RequestMapping(value = "/appApi", produces = JsonEncode)
	public String appApi(HttpServletRequest request, @RequestParam(required = false, defaultValue = "0") String action)
			throws Exception {
		request.setCharacterEncoding("UTF-8");
		curLoginToken = request.getParameter(LoginToken);
		Integer actionInteger = ApiConstant.getInteger(action);

		// 判断是否需要登录才能操作
		if (actionInteger / 100 == 2) {
			boolean isLogin = this.realTimeData.isAppLogin(this.curLoginToken, false);
			if (isLogin == false) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.accumulate(Result, false);
				jsonObject.accumulate(ErrorCode, -10001);// 未登录
				jsonObject.accumulate(Value, "未登录");// 未登录
				return jsonObject.toString();
			}
		}

		String ret = "";
		switch (actionInteger) {
		case 0: {
			JSONObject jsonObject = new JSONObject();
			jsonObject.accumulate(Result, false);
			jsonObject.accumulate(ErrorCode, -10000);// 非法提交
			jsonObject.accumulate(Value, "API不存在");// 未登录
			ret = jsonObject.toString();
		}
			break;

		default:
			try {
				Method method = this.getClass().getDeclaredMethod(action, HttpServletRequest.class);
				ret = (String) method.invoke(this, request);
			} catch (Exception e) {
				ret = ApiConstant.getUnknownError(e);
			}
			break;
		}

		if (Constant.isRelease == false) {
			System.out.println(ret);
		}
		return ret;
	}

	// 登陆
	public String UserLogin(HttpServletRequest request) throws Exception {
		try {
			String email = request.getParameter("email").trim().toLowerCase();
			String password = request.getParameter("password").trim();

			JSONObject jsonObject = new JSONObject();
			jsonObject.accumulate(Result, true);

			int longLogin = 0;// 0是手机，1是邮箱
			Fuser fuser = new Fuser();
			if (email.matches(Constant.EmailReg)) {
				fuser.setFemail(email);
				longLogin = 1;
			} else if (email.matches(Constant.PhoneReg)) {
				longLogin = 0;
				fuser.setFtelephone(email);
			} else {
				jsonObject.accumulate(ErrorCode, -1);
				jsonObject.accumulate(Value, "用户名格式输入错误");
				return jsonObject.toString();
			}

			fuser.setFemail(email);
			fuser.setFloginPassword(password);
			// String ip = Utils.getIp(request);
			String ip = request.getHeader("x-forwarded-for");
			if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("Proxy-Client-IP");
			}
			if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("WL-Proxy-Client-IP");
			}
			if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getRemoteAddr();
			}

			int limitedCount = this.frontValidateService.getLimitCount(ip, CountLimitTypeEnum.LOGIN_PASSWORD);
			if (limitedCount > 0) {

				fuser = this.frontUserService.updateCheckLogin(fuser, ip, longLogin == 1);
				if (fuser != null) {

					String isCanlogin = constantMap.get("isCanlogin").toString().trim();
					if (!isCanlogin.equals("1")) {
						boolean isCanLogin = false;
						String[] canLoginUsers = constantMap.get("canLoginUsers").toString().split("#");
						for (int i = 0; i < canLoginUsers.length; i++) {
							if (canLoginUsers[i].equals(String.valueOf(fuser.getFid()))) {
								isCanLogin = true;
								break;
							}
						}
						if (!isCanLogin) {
							jsonObject.accumulate(ErrorCode, -1);
							jsonObject.accumulate(Value, "网站暂时不允许登陆");
							return jsonObject.toString();
						}
					}

					if (fuser.getFstatus() == UserStatusEnum.NORMAL_VALUE) {
						this.frontValidateService.deleteCountLimite(ip, CountLimitTypeEnum.LOGIN_PASSWORD);
						if (fuser.getFopenSecondValidate()) {
							SetSecondLoginSession(request, fuser);
						} else {
							jsonObject.accumulate(ErrorCode, 0);// 登陆成功
							jsonObject.accumulate(Value, "登陆成功");
							String loginToken = this.realTimeData.putAppSession(getSession(request), fuser);
							jsonObject.accumulate(LoginToken, loginToken);
							jsonObject.accumulate("postRealValidate", fuser.getFpostRealValidate());
							if (fuser.getFtradePassword() == null || fuser.getFtradePassword().trim().length() == 0) {
								jsonObject.accumulate("isHasTradePWD", 0);
							} else {
								jsonObject.accumulate("isHasTradePWD", 1);
							}

							jsonObject.accumulate(Fid, fuser.getFid());
							return jsonObject.toString();
						}
					} else {
						jsonObject.accumulate(ErrorCode, -1);
						jsonObject.accumulate(Value, "账户出现安全隐患被冻结，请尽快联系客服");
						return jsonObject.toString();
					}
				} else {
					// 错误的用户名或密码
					if (limitedCount == Constant.ErrorCountLimit) {
						jsonObject.accumulate(ErrorCode, -1);
						jsonObject.accumulate(Value, "用户名或密码错误");
					} else {
						jsonObject.accumulate(ErrorCode, -1);
						jsonObject.accumulate(Value, "用户名或密码错误，您还有" + (limitedCount - 1) + "次机会");
					}
					this.frontValidateService.updateLimitCount(ip, CountLimitTypeEnum.LOGIN_PASSWORD);
					return jsonObject.toString();
				}
			} else {
				jsonObject.accumulate(ErrorCode, -1);
				jsonObject.accumulate(Value, "您的ip已被锁定，请在两小时之后重试！");
				return jsonObject.toString();
			}
		} catch (Exception e) {
			return ApiConstant.getUnknownError(e);
		}
		return ApiConstant.getUnknownError(null);
	}

	// 注册
	public String UserRegister(HttpServletRequest request) throws Exception {
		try {
			int type = Integer.parseInt(request.getParameter("type").trim());// 1phone2mail
			String email = request.getParameter("email").trim();
			String password = request.getParameter("password").trim();
			String code = request.getParameter("code");
			String intro_user = request.getParameter("intro_user");
			String areaCode = "86";
			String ip = Utils.getIp(request);

			JSONObject jsonObject = new JSONObject();
			jsonObject.accumulate(Result, true);

			String isOpenReg = constantMap.get("isOpenReg").toString().trim();
			if (!isOpenReg.equals("1")) {
				jsonObject.accumulate(ErrorCode, -1);
				jsonObject.accumulate(Value, "暂停注册");
				return jsonObject.toString();
			}

			if (password.length() < 6) {
				jsonObject.accumulate(ErrorCode, -1);
				jsonObject.accumulate(Value, "密码最小6位");
				return jsonObject.toString();
			}
			if (type == 1) {
				// 手机注册

				boolean flag1 = this.frontUserService.isTelephoneExists(email);
				if (flag1) {
					jsonObject.accumulate(ErrorCode, -1);
					jsonObject.accumulate(Value, "手机号码已经被注册");
					return jsonObject.toString();
				}

				if (!email.matches(Constant.PhoneReg)) {
					jsonObject.accumulate(ErrorCode, -1);
					jsonObject.accumulate(Value, "手机格式错误");
					return jsonObject.toString();
				}

				boolean mobilValidate = validateMessageCode(ip, areaCode, email, MessageTypeEnum.REG_CODE, code);
				if (!mobilValidate) {
					jsonObject.accumulate(ErrorCode, -1);
					jsonObject.accumulate(Value, "短信验证码错误");
					return jsonObject.toString();
				}

			} else {
				// 邮箱注册
				boolean flag = this.frontUserService.isEmailExists(email);
				if (flag) {
					jsonObject.accumulate(ErrorCode, -1);
					jsonObject.accumulate(Value, "邮箱已经存在");
					return jsonObject.toString();
				}

				boolean mailValidate = validateMailCode(ip, email, SendMailTypeEnum.RegMail, code);
				if (!mailValidate) {
					jsonObject.accumulate(ErrorCode, -1);
					jsonObject.accumulate(Value, "邮箱验证码错误");
					return jsonObject.toString();
				}

				if (!email.matches(Constant.EmailReg)) {
					jsonObject.accumulate(ErrorCode, -1);
					jsonObject.accumulate(Value, "邮箱格式错误");
					return jsonObject.toString();
				}

			}

			// 推广
			Fuser intro = null;

			try {
				if (intro_user != null && !"".equals(intro_user.trim())) {
					intro = this.frontUserService.findById(intro_user.trim());
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			if (intro == null) {
				String isMustIntrol = constantMap.get("isMustIntrol").toString().trim();
				if (isMustIntrol.equals("1")) {
					jsonObject.accumulate(ErrorCode, -1);
					jsonObject.accumulate(Value, "请填写正确的邀请码");
					return jsonObject.toString();
				}
			}

			Fuser fuser = new Fuser();
			if (intro != null) {
				fuser.setfIntroUser_id(intro);
			}
			if (type == 1) {
				// 手机注册
				fuser.setFregType(RegTypeEnum.TEL_VALUE);
				fuser.setFtelephone(email);
				fuser.setFareaCode(areaCode);
				fuser.setFisTelephoneBind(true);

				fuser.setFnickName(email);
				fuser.setFloginName(email);
			} else {
				fuser.setFregType(RegTypeEnum.EMAIL_VALUE);
				fuser.setFemail(email);
				fuser.setFisMailValidate(true);
				fuser.setFnickName(email.split("@")[0]);
				fuser.setFloginName(email);
			}

			fuser.setFregisterTime(Utils.getTimestamp());
			fuser.setFloginPassword(Utils.MD5(password));
			fuser.setFtradePassword(null);
			fuser.setFregIp(ip);
			fuser.setFlastLoginIp(ip);
			fuser.setFstatus(UserStatusEnum.NORMAL_VALUE);
			fuser.setFlastLoginTime(Utils.getTimestamp());
			fuser.setFlastUpdateTime(Utils.getTimestamp());
			boolean saveFlag = false;
			try {
				saveFlag = this.frontUserService.saveRegister(fuser);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (type == 1) {
					String key1 = ip + "message_" + MessageTypeEnum.REG_CODE;
					String key2 = ip + "_" + email + "_" + MessageTypeEnum.REG_CODE;
					this.validateMap.removeMessageMap(key1);
					this.validateMap.removeMessageMap(key2);
				} else {
					String key1 = ip + "mail_" + SendMailTypeEnum.RegMail;
					String key2 = ip + "_" + email + "_" + SendMailTypeEnum.RegMail;
					this.validateMap.removeMailMap(key1);
					this.validateMap.removeMailMap(key2);
				}
			}

			if (saveFlag) {
				String loginToken = this.realTimeData.putAppSession(getSession(request), fuser);
				jsonObject.accumulate(ErrorCode, 0);// 注册成功
				jsonObject.accumulate(Value, "注册成功");// 注册成功
				jsonObject.accumulate(LoginToken, loginToken);
				jsonObject.accumulate("postRealValidate", fuser.getFpostRealValidate());
				jsonObject.accumulate(Fid, fuser.getFid());
				return jsonObject.toString();
			} else {
				jsonObject.accumulate(ErrorCode, -1);
				jsonObject.accumulate(Value, "网络错误，请稍后再试");
				return jsonObject.toString();
			}

		} catch (Exception e) {

			return ApiConstant.getUnknownError(e);
		}
	}

	public String FindLoginPassword(HttpServletRequest request) throws Exception {
		try {
			int type = Integer.parseInt(request.getParameter("type").trim());// 1phone2mail
			String email = request.getParameter("email").trim().toLowerCase();
			String password = request.getParameter("newpassword").trim();
			String code = request.getParameter("code");
			String areaCode = "86";
			String ip = Utils.getIp(request);

			JSONObject jsonObject = new JSONObject();
			jsonObject.accumulate(Result, true);

			if (password == null || password.length() < 6 || password.length() > 20) {
				jsonObject.accumulate(ErrorCode, -1);
				jsonObject.accumulate(Value, "密码长度必须为6~20位");
				return jsonObject.toString();
			}

			if (type == 1) {
				// 手机

				if (!email.matches(Constant.PhoneReg)) {
					jsonObject.accumulate(ErrorCode, -1);
					jsonObject.accumulate(Value, "手机格式错误");
					return jsonObject.toString();
				}

				boolean flag1 = this.frontUserService.isTelephoneExists(email);
				if (flag1 == false) {
					jsonObject.accumulate(ErrorCode, -1);
					jsonObject.accumulate(Value, "手机不存在");
					return jsonObject.toString();
				}

				boolean mobilValidate = validateMessageCode(ip, areaCode, email, MessageTypeEnum.FIND_PASSWORD, code);
				if (!mobilValidate) {
					jsonObject.accumulate(ErrorCode, -1);
					jsonObject.accumulate(Value, "短信验证码错误");
					return jsonObject.toString();
				}

			} else {
				// 邮箱注册
				boolean flag = this.frontUserService.isEmailExists(email);

				if (email.matches(Constant.EmailReg) == false) {
					jsonObject.accumulate(ErrorCode, -1);
					jsonObject.accumulate(Value, "邮箱格式错误");
					return jsonObject.toString();
				}

				if (flag == false) {
					jsonObject.accumulate(ErrorCode, -1);
					jsonObject.accumulate(Value, "邮箱不存在");
					return jsonObject.toString();
				}

				boolean mailValidate = validateMailCode(ip, email, SendMailTypeEnum.FindPassword, code);
				if (!mailValidate) {
					jsonObject.accumulate(ErrorCode, -1);
					jsonObject.accumulate(Value, "邮箱验证码错误");
					return jsonObject.toString();
				}

			}

			boolean flag = false;
			Fuser fuser = null;

			fuser = this.frontUserService.findUserByProperty(type == 1 ? "ftelephone" : "femail", email).get(0);
			if (Utils.MD5(password).equals(fuser.getFloginPassword())) {
				jsonObject.accumulate(ErrorCode, -1);
				jsonObject.accumulate(Value, "新密码不能与旧密码一致！");
				return jsonObject.toString();
			}
			if (fuser != null) {
				fuser.setFloginPassword(Utils.MD5(password));
				this.frontUserService.updateFuser(fuser);
				flag = true;
			}
			if (flag == true) {
				jsonObject.accumulate(ErrorCode, 0);
				jsonObject.accumulate(Value, "密码修改成功");
				return jsonObject.toString();
			} else {
				jsonObject.accumulate(ErrorCode, -1);
				jsonObject.accumulate(Value, "网络错误，请稍后再试");
				return jsonObject.toString();
			}

		} catch (Exception e) {

			return ApiConstant.getUnknownError(e);
		}
	}

	// 发送手机验证码
	public String SendMessageCode(HttpServletRequest request) throws Exception {
		try {
			int type = Integer.parseInt(request.getParameter("type"));
			String phone = request.getParameter("phone");
			String ip = Utils.getIp(request);

			JSONObject jsonObject = new JSONObject();
			jsonObject.accumulate(Result, true);

			boolean isLogin = this.realTimeData.isAppLogin(this.curLoginToken, false);

			if (type <= MessageTypeEnum.BEGIN || type >= MessageTypeEnum.END) {
				jsonObject.accumulate(ErrorCode, -1);
				jsonObject.accumulate(Value, "类型错误");
				return jsonObject.toString();
			}

			if ((type == MessageTypeEnum.FIND_PASSWORD || type == MessageTypeEnum.REG_CODE)) {
				if (phone.matches(Constant.PhoneReg) == false) {
					jsonObject.accumulate(ErrorCode, -1);
					jsonObject.accumulate(Value, "手机格式错误");
					return jsonObject.toString();
				}

			} else if (isLogin == false) {
				jsonObject.accumulate(ErrorCode, -1);
				jsonObject.accumulate(Value, "请登录后继续操作");
				return jsonObject.toString();
			}

			Fuser fuser = this.realTimeData.getAppFuser(this.curLoginToken);
			if (fuser != null && fuser.isFisTelephoneBind()) {
				phone = fuser.getFtelephone();
			}

			// 注册，绑定手机
			if ((type == MessageTypeEnum.REG_CODE || type == MessageTypeEnum.BANGDING_MOBILE)) {
				boolean isPhoneExists = this.frontUserService.isTelephoneExists(phone);
				if (isPhoneExists) {
					jsonObject.accumulate(ErrorCode, -1);
					jsonObject.accumulate(Value, "手机号码已经存在");
					return jsonObject.toString();
				}
			}
			// 找回密码
			boolean isPhoneExists = this.frontUserService.isTelephoneExists(phone);
			if (type == MessageTypeEnum.FIND_PASSWORD) {
				if (isPhoneExists == false) {
					jsonObject.accumulate(ErrorCode, -1);
					jsonObject.accumulate(Value, "该账户不存在");
					return jsonObject.toString();
				}
			}

			if (phone.matches(Constant.PhoneReg)) {
				if (type == MessageTypeEnum.BANGDING_MOBILE) {
					if (isPhoneExists) {
						jsonObject.accumulate(ErrorCode, 2);
						jsonObject.accumulate(Value, "手机号码已经存在");
						return jsonObject.toString();
					}
				} else {
					if (isLogin && fuser.isFisTelephoneBind() == false) {
						jsonObject.accumulate(ErrorCode, 3);
						jsonObject.accumulate(Value, "该账号没有绑定手机号码");
						return jsonObject.toString();
					}
				}
				if (isLogin) {
					super.SendMessage(fuser, fuser.getFid(), "86", phone, type);
				} else {
					super.SendMessage(ip, "86", phone, type);
				}

				jsonObject.accumulate(ErrorCode, 0);
				jsonObject.accumulate(Value, "验证码已发送，请查收");
				return jsonObject.toString();

			} else {
				jsonObject.accumulate(ErrorCode, -1);
				jsonObject.accumulate(Value, "手机号码格式错误");
				return jsonObject.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ApiConstant.getUnknownError(e);
		}
	}

	// 发送邮件验证码
	public String SendMailCode(HttpServletRequest request) throws Exception {
		try {
			String email = request.getParameter("email").toLowerCase();
			int type = Integer.parseInt(request.getParameter("type"));

			String ip = Utils.getIp(request);
			boolean isLogin = this.realTimeData.isAppLogin(this.curLoginToken, false);
			Fuser fuser = this.realTimeData.getAppFuser(this.curLoginToken);
			// 注册类型免登陆可以发送
			JSONObject jsonObject = new JSONObject();

			if (type <= SendMailTypeEnum.BEGIN || type >= SendMailTypeEnum.END) {
				jsonObject.accumulate(Result, -1);
				jsonObject.accumulate(Value, "非法提交");
				return jsonObject.toString();
			}

			if (type == SendMailTypeEnum.ValidateMail && isLogin == false) {
				jsonObject.accumulate(Result, -1);
				jsonObject.accumulate(Value, "请登录后继续操作");
				return jsonObject.toString();
			}

			if (type < SendMailTypeEnum.BEGIN || type > SendMailTypeEnum.END) {
				jsonObject.accumulate(Result, -1);
				jsonObject.accumulate(Value, "非法提交");
				return jsonObject.toString();
			}

			if (email.matches(Constant.EmailReg) == false) {
				jsonObject.accumulate(Result, -1);
				jsonObject.accumulate(Value, "邮箱格式错误");
				return jsonObject.toString();
			}

			boolean flag = this.frontUserService.isEmailExists(email);
			if (type == SendMailTypeEnum.FindPassword) {
				if (flag == false) {
					jsonObject.accumulate(Result, -1);
					jsonObject.accumulate(Value, "邮箱不存在");
					return jsonObject.toString();
				}
			}

			if (type == SendMailTypeEnum.RegMail || type == SendMailTypeEnum.ValidateMail) {
				if (flag == true) {
					jsonObject.accumulate(Result, -1);
					jsonObject.accumulate(Value, "邮箱已经存在");
					return jsonObject.toString();
				}
			}

			if (type == SendMailTypeEnum.ValidateMail) {
				SendMail(fuser, email, type);
			} else {
				SendMail(Utils.getIp(request), email, type);
			}

			jsonObject.accumulate(Result, 0);
			jsonObject.accumulate(Value, "验证码已经发送，请查收");
			return jsonObject.toString();

		} catch (Exception e) {
			return ApiConstant.getUnknownError(e);
		}

	}

	// 验证交易密码
	public String TradePassword(HttpServletRequest request) throws Exception {
		try {
			JSONObject jsonObject = new JSONObject();

			String passwd = request.getParameter("passwd").trim();
			Fuser fuser = this.realTimeData.getAppFuser(this.curLoginToken);
			if (fuser.getFtradePassword() == null) {
				jsonObject.accumulate(Result, true);
				jsonObject.accumulate(ErrorCode, -1);
				jsonObject.accumulate(Value, "未设置交易密码");
				return jsonObject.toString();
			} else if (fuser.getFtradePassword().endsWith(Utils.getMD5_32(passwd))) {
				jsonObject.accumulate(Result, true);
				jsonObject.accumulate(ErrorCode, 0);
				jsonObject.accumulate(Value, "交易密码正确");
				return jsonObject.toString();
			} else {
				jsonObject.accumulate(Result, true);
				jsonObject.accumulate(ErrorCode, -1);
				jsonObject.accumulate(Value, "未设置交易密码");
				return jsonObject.toString();
			}

		} catch (Exception e) {
			return ApiConstant.getUnknownError(e);
		}
	}

	// 交易
	public String BtcTradeSubmit(HttpServletRequest request) throws Exception {
		try {
			// 如果隐藏交易中心则禁止交易
			if (Comm.getISHIDDEN_DEAL().equals("true")) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.accumulate(Result, false);
				jsonObject.accumulate(Value, "禁止交易!");
				jsonObject.accumulate(ErrorCode, 0);
				return jsonObject.toString();
			}
			int type = Integer.parseInt(request.getParameter("type"));
			if (type == 0) {
				return this.buyBtcSubmit(request);
			} else if (type == 1) {
				return this.sellBtcSubmit(request);
			} else {
				throw new Exception();
			}
		} catch (Exception e) {
			return ApiConstant.getUnknownError(e);
		}
	}

	public String GetEntrustInfo(HttpServletRequest request) throws Exception {
		try {
			Fuser fuser = this.realTimeData.getAppFuser(this.curLoginToken);
			int type = Integer.parseInt(request.getParameter("type"));
			String symbol = request.getParameter("symbol");
			int currentPage = 1;
			int totalPage = 0;
			try {
				currentPage = Integer.parseInt(request.getParameter("currentPage"));
				currentPage = currentPage < 1 ? 1 : currentPage;
			} catch (Exception e) {
			}

			StringBuffer fstatus = new StringBuffer();
			if (type == 0) {
				// 未成交
				fstatus.append(
						" (fstatus=" + EntrustStatusEnum.Going + " or fstatus=" + EntrustStatusEnum.PartDeal + ") ");
			} else {
				// 成交
				fstatus.append(
						" (fstatus=" + EntrustStatusEnum.AllDeal + " or fstatus=" + EntrustStatusEnum.Cancel + ") ");
			}
			String filter = " where fvirtualcointype.fid='" + symbol + "' and " + fstatus.toString()
					+ " and fuser.fid='" + fuser.getFid() + "' order by flastUpdatTime desc ";

			List<Fentrust> list = this.frontTradeService.findFentrustByParam((currentPage - 1) * maxResult, maxResult,
					filter, true);
			int total = this.frontTradeService.findFentrustByParamCount(filter);
			totalPage = total / maxResult + (total % maxResult == 0 ? 0 : 1);

			JSONObject jsonObject = new JSONObject();
			jsonObject.accumulate(Result, true);
			jsonObject.accumulate("totalPage", totalPage);
			jsonObject.accumulate("currentPage", currentPage);
			jsonObject.accumulate("lastUpdateTime", Utils.dateFormat(Utils.getTimestamp()));

			JSONArray jsonArray = new JSONArray();
			for (int i = 0; i < list.size(); i++) {
				JSONObject item = new JSONObject();
				Fentrust fentrust = list.get(i);
				String title = null;
				if (fentrust.isFisLimit() == true) {
					if (fentrust.getFentrustType() == EntrustTypeEnum.BUY) {
						title = "市价买入";
					} else {
						title = "市价卖出";
					}
				} else {
					if (fentrust.getFentrustType() == EntrustTypeEnum.BUY) {
						title = "限价买入";
					} else {
						title = "限价卖出";
					}
				}

				item.accumulate("fid", fentrust.getFid());
				item.accumulate("type", fentrust.getFentrustType());
				item.accumulate("title", title);

				item.accumulate("fstatus", fentrust.getFstatus());
				item.accumulate("fstatus_s", fentrust.getFstatus_s());

				item.accumulate("flastUpdatTime", Utils.dateFormat(fentrust.getFlastUpdatTime()));

				item.accumulate("fprice", new BigDecimal(fentrust.getFprize()).setScale(6, BigDecimal.ROUND_HALF_UP));
				item.accumulate("fcount", new BigDecimal(fentrust.getFcount()).setScale(4, BigDecimal.ROUND_HALF_UP));
				item.accumulate("fsuccessCount", new BigDecimal(fentrust.getFcount() - fentrust.getFleftCount())
						.setScale(4, BigDecimal.ROUND_HALF_UP));
				if (fentrust.getFcount() - fentrust.getFleftCount() < 0.0001D) {
					item.accumulate("fsuccessprice", 0D);
				} else {
					item.accumulate("fsuccessprice",
							new BigDecimal(
									fentrust.getFsuccessAmount() / (fentrust.getFcount() - fentrust.getFleftCount()))
											.setScale(4, BigDecimal.ROUND_HALF_UP));
				}

				jsonArray.add(item);
			}
			jsonObject.accumulate("list", jsonArray);

			return jsonObject.toString();
		} catch (Exception e) {
			return ApiConstant.getUnknownError(e);
		}
	}

	public String CancelEntrust(HttpServletRequest request) throws Exception {
		try {
			Fuser fuser1 = this.realTimeData.getAppFuser(this.curLoginToken);
			Fuser fuser = this.frontUserService.findById(fuser1.getFid());
			String id = request.getParameter("id");

			Fentrust fentrust = this.frontTradeService.findFentrustById(id);

			JSONObject jsonObject = new JSONObject();
			jsonObject.accumulate(Result, true);
			if (fentrust != null
					&& (fentrust.getFstatus() == EntrustStatusEnum.Going
							|| fentrust.getFstatus() == EntrustStatusEnum.PartDeal)
					&& fentrust.getFuser().getFid().equals(fuser.getFid())) {
				boolean flag = false;
				try {
					this.frontTradeService.updateCancelFentrust(fentrust, fuser);
					flag = true;
				} catch (Exception e) {

				}
				if (flag == true) {
					if (fentrust.getFentrustType() == EntrustTypeEnum.BUY) {

						// 买
						if (fentrust.isFisLimit()) {
							this.realTimeData.removeEntrustLimitBuyMap(fentrust.getFvirtualcointype().getFid(),
									fentrust);
						} else {
							this.realTimeData.removeEntrustBuyMap(fentrust.getFvirtualcointype().getFid(), fentrust);
						}
					} else {

						// 卖
						if (fentrust.isFisLimit()) {
							this.realTimeData.removeEntrustLimitSellMap(fentrust.getFvirtualcointype().getFid(),
									fentrust);
						} else {
							this.realTimeData.removeEntrustSellMap(fentrust.getFvirtualcointype().getFid(), fentrust);
						}

					}

					jsonObject.accumulate(ErrorCode, 0);// 成功
					jsonObject.accumulate(Value, "取消成功");// 成功
					return jsonObject.toString();
				} else {
					jsonObject.accumulate(ErrorCode, -1);// 失敗
					jsonObject.accumulate(Value, "取消失败");// 成功
					return jsonObject.toString();
				}
			} else {
				jsonObject.accumulate(ErrorCode, -1);// 失敗
				jsonObject.accumulate(Value, "取消失败");// 成功
				return jsonObject.toString();
			}

		} catch (Exception e) {
			return ApiConstant.getUnknownError(e);
		}
	}

	public String GetMarketData(HttpServletRequest request) throws Exception {
		try {

			String filter = "where fstatus=" + VirtualCoinTypeStatusEnum.Normal
					+ " and fisShare=1 order by faddTime asc";
			List<Fvirtualcointype> list = this.virtualCoinService.list(0, 0, filter, false);

			JSONObject jsonObject = new JSONObject();
			jsonObject.accumulate(Result, true);
			jsonObject.accumulate("lastUpdateTime", Utils.dateFormat(Utils.getTimestamp()));

			JSONArray jsonArray = new JSONArray();
			for (int i = 0; i < list.size(); i++) {
				Fvirtualcointype fvirtualcointype = list.get(i);
				String id = fvirtualcointype.getFid();
				int count = fvirtualcointype.getFcount();// 小数点

				JSONObject item = new JSONObject();
				item.accumulate("id", id);
				item.accumulate("vir_id", fvirtualcointype.getFid());
				item.accumulate("coinName", fvirtualcointype.getfShortName());
				item.accumulate("cnName", fvirtualcointype.getFname());
				item.accumulate("currency", "CNY");
				item.accumulate("title", this.constantMap.get("webName"));
				item.accumulate("hasKline", true);
				item.accumulate("isWithDraw", fvirtualcointype.isFIsWithDraw());

				boolean isLimittrade = false;
				boolean isLimitbuy = false;
				boolean isLimitSell = false;
				BigDecimal upPrice = BigDecimal.ZERO;
				BigDecimal downPrice = BigDecimal.ZERO;

				Flimittrade limittrade = this.isLimitTrade(fvirtualcointype.getFid());
				if (limittrade != null) {
					isLimittrade = true;
					if (limittrade.getFpercent() <= 0) {
						isLimittrade = false;
					}
					upPrice = new BigDecimal(
							limittrade.getFupprice() + limittrade.getFupprice() * limittrade.getFpercent())
									.setScale(fvirtualcointype.getFcount(), BigDecimal.ROUND_HALF_UP);
					downPrice = new BigDecimal(
							limittrade.getFdownprice() - limittrade.getFdownprice() * limittrade.getFpercent())
									.setScale(fvirtualcointype.getFcount(), BigDecimal.ROUND_HALF_UP);
					if (downPrice.compareTo(BigDecimal.ZERO) < 0)
						downPrice = BigDecimal.ZERO;
				}

				item.accumulate("isLimittrade", isLimittrade);
				item.accumulate("upPrice", upPrice);
				item.accumulate("downPrice", downPrice);
				item.accumulate("count", count);

				// 成交量，买一，卖一，最高，最低，现价
				item.accumulate("LatestDealPrice", new BigDecimal(this.realTimeData.getLatestDealPrize(id))
						.setScale(count, BigDecimal.ROUND_HALF_UP));
				item.accumulate("SellOnePrice", new BigDecimal(this.realTimeData.getLowestSellPrize(id)).setScale(count,
						BigDecimal.ROUND_HALF_UP));
				item.accumulate("BuyOnePrice", new BigDecimal(this.realTimeData.getHighestBuyPrize(id)).setScale(count,
						BigDecimal.ROUND_HALF_UP));
				item.accumulate("OneDayLowest",
						new BigDecimal(this.oneDayData.getLowest(id)).setScale(count, BigDecimal.ROUND_HALF_UP));
				item.accumulate("OneDayHighest",
						new BigDecimal(this.oneDayData.getHighest(id)).setScale(count, BigDecimal.ROUND_HALF_UP));
				item.accumulate("OneDayTotal",
						new BigDecimal(this.oneDayData.getTotal(id)).setScale(4, BigDecimal.ROUND_HALF_UP));
				item.accumulate("OneDayTotalAmt", Utils.getDouble(this.oneDayData.get24Total(id), 2));
				List<Ftradehistory> ftradehistorys = (List<Ftradehistory>) constantMap.get("tradehistory");
				Ftradehistory ftradehistory = null;
				for (Ftradehistory th : ftradehistorys) {
					if (th.getFvid().equals(fvirtualcointype.getFid())) {
						ftradehistory = th;
						break;
					}
				}

				// 24小时涨跌幅
				double priceRaiseRate = 0;
				double sx = this.oneDayData.get24Price(fvirtualcointype.getFid());
				for (Ftradehistory th : ftradehistorys) {
					if (th.getFvid().equals(fvirtualcointype.getFid())) {
						sx = ftradehistory.getFprice();
						break;
					}
				}
				try {
					priceRaiseRate = Utils.getDouble(
							(this.realTimeData.getLatestDealPrize(fvirtualcointype.getFid()) - sx) / sx * 100, 2);
				} catch (Exception e) {
				}

				item.accumulate("priceRaiseRate", priceRaiseRate);

				// logo
				item.accumulate("logo", fvirtualcointype.getFurl());

				{
					JSONObject push = new JSONObject();
					push.accumulate("isopen", false);
					push.accumulate("high", 0);
					push.accumulate("low", 0);

					item.accumulate("push", push);
				}

				// 七日涨跌幅数据
				JSONArray day7 = new JSONArray();

				Map ftradehistory7D = (Map) this.constantMap.get("ftradehistory7D");
				if (ftradehistory7D.containsKey(fvirtualcointype.getFid())) {
					List ss = (List) ftradehistory7D.get(fvirtualcointype.getFid());
					for (int m = 0; m < ss.size(); m++) {
						if (m == ss.size() - 1) {
							day7.add(this.realTimeData.getLatestDealPrize(fvirtualcointype.getFid()));
						} else {
							day7.add(ss.get(m));
						}
					}
				}
				item.accumulate("day7", day7);

				jsonArray.add(item);
			}

			jsonObject.accumulate("list", jsonArray);
			return jsonObject.toString();

		} catch (Exception e) {

			return ApiConstant.getUnknownError(e);
		}

	}

	public String GetMarketDepth(HttpServletRequest request) throws Exception {

		try {
			String symbol = request.getParameter("id");
			Fuser fuser = this.realTimeData.getAppFuser(this.curLoginToken);

			Fvirtualcointype fvirtualcointype = this.frontVirtualCoinService.findFvirtualCoinById(symbol);
			int count = fvirtualcointype.getFcount();
			if (fvirtualcointype == null || fvirtualcointype.getFstatus() == VirtualCoinTypeStatusEnum.Abnormal) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.accumulate(Result, false);
				jsonObject.accumulate(ErrorCode, -10000);// 虚拟币不存在
				return jsonObject.toString();
			}

			JSONObject jsonObject = new JSONObject();
			jsonObject.accumulate(Result, true);
			jsonObject.accumulate("lastUpdateTime", Utils.dateFormat(Utils.getTimestamp()));

			Object[] buyMap = null;
			Object[] sellMap = null;
			if (Constant.CombinedDepth) {
				buyMap = this.realTimeData.getBuyDepthMap(symbol).toArray();
				sellMap = this.realTimeData.getSellDepthMap(symbol).toArray();
			} else {
				buyMap = this.realTimeData.getEntrustBuyMap(symbol).toArray();
				sellMap = this.realTimeData.getEntrustSellMap(symbol).toArray();
			}

			JSONArray buyArray = new JSONArray();
			JSONArray sellArray = new JSONArray();
			for (int i = 0; i < buyMap.length && i <= 8; i++) {
				Fentrust fentrust = (Fentrust) buyMap[i];
				JSONObject item = new JSONObject();
				item.accumulate("price", fentrust.getFprize());
				item.accumulate("amount", fentrust.getFleftCount());
				buyArray.add(item);
			}
			for (int i = 0; i < sellMap.length && i <= 8; i++) {
				Fentrust fentrust = (Fentrust) sellMap[i];
				JSONObject item = new JSONObject();
				item.accumulate("price", fentrust.getFprize());
				item.accumulate("amount", fentrust.getFleftCount());
				sellArray.add(item);
			}

			jsonObject.accumulate("sellDepth", sellArray);
			jsonObject.accumulate("buyDepth", buyArray);

			// quotation
			JSONObject quotation = new JSONObject();
			// 成交量，买一，卖一，最高，最低，现价
			BigDecimal LatestDealPrice = new BigDecimal(this.realTimeData.getLatestDealPrize(symbol)).setScale(count,
					BigDecimal.ROUND_HALF_UP);
			BigDecimal SellOnePrice = new BigDecimal(this.realTimeData.getLowestSellPrize(symbol)).setScale(count,
					BigDecimal.ROUND_HALF_UP);
			BigDecimal BuyOnePrice = new BigDecimal(this.realTimeData.getHighestBuyPrize(symbol)).setScale(count,
					BigDecimal.ROUND_HALF_UP);

			quotation.accumulate("LatestDealPrice", LatestDealPrice);
			quotation.accumulate("SellOnePrice", SellOnePrice);
			quotation.accumulate("BuyOnePrice", BuyOnePrice);
			quotation.accumulate("OneDayLowest",
					new BigDecimal(this.oneDayData.getLowest(symbol)).setScale(count, BigDecimal.ROUND_HALF_UP));
			quotation.accumulate("OneDayHighest",
					new BigDecimal(this.oneDayData.getHighest(symbol)).setScale(count, BigDecimal.ROUND_HALF_UP));
			quotation.accumulate("OneDayTotal",
					new BigDecimal(this.oneDayData.getTotal(symbol)).setScale(4, BigDecimal.ROUND_HALF_UP));

			List<Ftradehistory> ftradehistorys = (List<Ftradehistory>) constantMap.get("tradehistory");
			Ftradehistory ftradehistory = null;
			for (Ftradehistory th : ftradehistorys) {
				if (th.getFvid().equals(fvirtualcointype.getFid())) {
					ftradehistory = th;
					break;
				}
			}

			// 24小时涨跌幅
			double priceRaiseRate = 0;
			double sx = this.oneDayData.get24Price(fvirtualcointype.getFid());
			for (Ftradehistory th : ftradehistorys) {
				if (th.getFvid().equals(fvirtualcointype.getFid())) {
					sx = ftradehistory.getFprice();
					break;
				}
			}
			try {
				priceRaiseRate = Utils.getDouble(
						(this.realTimeData.getLatestDealPrize(fvirtualcointype.getFid()) - sx) / sx * 100, 2);
			} catch (Exception e) {
			}

			quotation.accumulate("priceRaiseRate", priceRaiseRate);

			jsonObject.accumulate("quotation", quotation);

			// coinInfo
			JSONObject coinInfo = new JSONObject();
			coinInfo.accumulate("id", fvirtualcointype.getFid());
			coinInfo.accumulate("fname", fvirtualcointype.getFname());
			coinInfo.accumulate("fShortName", fvirtualcointype.getfShortName());
			coinInfo.accumulate("fSymbol", fvirtualcointype.getfSymbol());
			jsonObject.accumulate("coinInfo", coinInfo);

			// asset
			if (fuser != null) {

				JSONObject asset = new JSONObject();
				JSONObject rmb = new JSONObject();
				Fvirtualwallet fvirtualwallet = this.frontUserService.findVirtualWalletByUser(fuser.getFid(), symbol);
				JSONObject coin = new JSONObject();
				// 获取默认资产
				double price = 0;// 人民币与默认资产的比例
				String defSymbol="";//默认资产符号
				String filter = " where fisDefAsset=1";
			
				List<Fvirtualcointype> listFvirtualcointype = frontVirtualCoinService.findByParam1(0, 0, filter, false,
						"Fvirtualcointype");
				Fvirtualcointype fvirtualcointypeDef = null;
				if (null != listFvirtualcointype && listFvirtualcointype.size() > 0) {
					fvirtualcointypeDef = listFvirtualcointype.get(0);
				}

				if (null != fvirtualcointypeDef) {
					Fvirtualwallet fvirtualDefWallet = this.virtualWalletService.findVirtualWallet(fuser.getFid(),
							fvirtualcointypeDef.getFid());
					Fsubscription f = subscriptionService.findByFviId(fvirtualcointypeDef.getFid());
					price = f.getFprice();
					defSymbol=fvirtualDefWallet.getFvirtualcointype().getfSymbol();
					rmb.accumulate("total",
							new BigDecimal(fvirtualDefWallet.getFtotal()).setScale(4, BigDecimal.ROUND_HALF_UP));
					rmb.accumulate("frozen",
							new BigDecimal(fvirtualDefWallet.getFfrozen()).setScale(4, BigDecimal.ROUND_HALF_UP));
					rmb.accumulate("canBuy",
							new BigDecimal((fvirtualDefWallet.getFtotal() / price / SellOnePrice.doubleValue()))
									.setScale(4, BigDecimal.ROUND_HALF_UP));
					asset.accumulate("totalAsset",
							new BigDecimal((fvirtualDefWallet.getFtotal() + fvirtualDefWallet.getFfrozen()))
									.add(this.getVirtualCoinAsset(fuser)).setScale(4, BigDecimal.ROUND_HALF_UP));
					// 卖出虚拟资产可以转换默认资产的数量
					coin.accumulate("canSell",
							new BigDecimal(fvirtualwallet.getFtotal() * BuyOnePrice.doubleValue() * price).setScale(4,
									BigDecimal.ROUND_HALF_UP));
				} else {

					Fwallet fwallet = fuser.getFwallet();
					rmb.accumulate("total",
							new BigDecimal(fwallet.getFtotalRmb()).setScale(4, BigDecimal.ROUND_HALF_UP));
					rmb.accumulate("frozen",
							new BigDecimal(fwallet.getFfrozenRmb()).setScale(4, BigDecimal.ROUND_HALF_UP));
					rmb.accumulate("canBuy", new BigDecimal((fwallet.getFtotalRmb() / SellOnePrice.doubleValue()))
							.setScale(4, BigDecimal.ROUND_HALF_UP));
					asset.accumulate("totalAsset", new BigDecimal((fwallet.getFtotalRmb() + fwallet.getFfrozenRmb()))
							.add(this.getVirtualCoinAsset(fuser)).setScale(4, BigDecimal.ROUND_HALF_UP));
					coin.accumulate("canSell", new BigDecimal(fvirtualwallet.getFtotal() * BuyOnePrice.doubleValue())
							.setScale(4, BigDecimal.ROUND_HALF_UP));
				}

				coin.accumulate("total",
						new BigDecimal(fvirtualwallet.getFtotal()).setScale(4, BigDecimal.ROUND_HALF_UP));
				coin.accumulate("frozen",
						new BigDecimal(fvirtualwallet.getFfrozen()).setScale(4, BigDecimal.ROUND_HALF_UP));

				// 默认资产比例
				asset.accumulate("price", price);
				asset.accumulate("defSymbol", defSymbol);
				asset.accumulate("rmb", rmb);
				asset.accumulate("coin", coin);
				jsonObject.accumulate("asset", asset);

			}

			return jsonObject.toString();

		} catch (Exception e) {
			return ApiConstant.getUnknownError(e);
		}

	}

	// 获取app首页信息
	public String GetNews(HttpServletRequest request) throws Exception {

		try {

			int currentPage = ApiConstant.getInt(request.getParameter(CurrentPage));

			JSONObject jsonObject = new JSONObject();
			jsonObject.accumulate(Result, true);
			jsonObject.accumulate("lastUpdateTime", Utils.dateFormat(Utils.getTimestamp()));

			// banner图片
			JSONObject banners = new JSONObject();

			String banner1 = constantMap.getString("bigImage1");
			String banner2 = constantMap.getString("bigImage2");
			String banner3 = constantMap.getString("bigImage3");
			String bigImageURL1 = constantMap.getString("bigImageURL1");
			String bigImageURL2 = constantMap.getString("bigImageURL2");
			String bigImageURL3 = constantMap.getString("bigImageURL3");
			banner1 = "".equals(banner1) ? null : (banner1);
			banner2 = "".equals(banner2) ? null : (banner2);
			banner3 = "".equals(banner3) ? null : (banner3);
			bigImageURL1 = "".equals(bigImageURL1) ? null : (bigImageURL1);
			bigImageURL2 = "".equals(bigImageURL2) ? null : (bigImageURL2);
			bigImageURL3 = "".equals(bigImageURL3) ? null : (bigImageURL3);
			banners.accumulate("banner1", banner1);
			banners.accumulate("banner2", banner2);
			banners.accumulate("banner3", banner3);
			banners.accumulate("bigImageURL1", bigImageURL1);
			banners.accumulate("bigImageURL2", bigImageURL2);
			banners.accumulate("bigImageURL3", bigImageURL3);
			jsonObject.accumulate("banners", banners);

			// 新闻

			String appNameNews = this.constantMap.getString("webName");
			List<Farticle> farticles = this.utilsService.list((currentPage - 1) * maxResult, maxResult,
					" order by fcreateDate desc ", true, Farticle.class);
			int total = this.utilsService.count("", Farticle.class);
			JSONArray jsonArray = new JSONArray();
			String urlHead = Comm.getURLHEAD();
			for (Farticle farticle : farticles) {
				JSONObject item = new JSONObject();
				String aid = farticle.getFid();
				item.accumulate("id", aid);
				// String img = farticle.getFappimg() ;
				// img = ("".equals(img)||img==null)?null:(img) ;
				// item.accumulate("img", img) ;
				item.accumulate("title", farticle.getFtitle());
				item.accumulate("date", Utils.dateFormat(farticle.getFlastModifyDate()));
				item.accumulate("from", appNameNews);
				item.accumulate("img", farticle.getFurl());
				jsonArray.add(item);
			}
			jsonObject.accumulate("news", jsonArray);
			jsonObject.accumulate(CurrentPage, currentPage);
			jsonObject.accumulate(TotalPage, totalPage(total, maxResult));
			// System.out.println(jsonObject.toString());
			return jsonObject.toString();

		} catch (Exception e) {
			e.printStackTrace();
			return ApiConstant.getUnknownError(e);
		}

	}

	private BigDecimal getVirtualCoinAsset(Fuser fuser) {

		double total = 0D;
		String filter = "where fstatus=" + VirtualCoinTypeStatusEnum.Normal + " and fisShare=1 order by faddTime asc";
		List<Fvirtualcointype> fvirtualcointypes = this.virtualCoinService.list(0, 0, filter, false);
		for (int i = 0; i < fvirtualcointypes.size(); i++) {
			Fvirtualcointype fvirtualcointype = fvirtualcointypes.get(i);
			Fvirtualwallet fvirtualwallet = this.frontUserService.findVirtualWalletByUser(fuser.getFid(),
					fvirtualcointype.getFid());

			double curPrice = this.realTimeData.getLatestDealPrize(fvirtualcointype.getFid());
			double asset = curPrice * (fvirtualwallet.getFtotal() + fvirtualwallet.getFfrozen());

			total += asset;
		}
		return new BigDecimal(total).setScale(4, BigDecimal.ROUND_HALF_UP);
	}

	public String GetVersion(HttpServletRequest request) throws Exception {
		try {
			String android_version = this.constantMap.getString("android_version");
			String ios_version = this.constantMap.getString("ios_version");
			JSONObject jsonObject = new JSONObject();
			jsonObject.accumulate(Result, true);
			jsonObject.accumulate("ios_version", ios_version);
			jsonObject.accumulate("android_version", android_version);
			jsonObject.accumulate("android_downurl", this.constantMap.getString("android_downurl"));
			jsonObject.accumulate("ios_downurl", this.constantMap.getString("ios_downurl"));
			return jsonObject.toString();

		} catch (Exception e) {
			return ApiConstant.getUnknownError(e);
		}
	}

	public String GetAbout(HttpServletRequest request) throws Exception {
		try {
			String webName = this.constantMap.getString("webName");
			String telephone = this.constantMap.getString("telephone");
			String serviceQQ1 = this.constantMap.getString("serviceQQ");
			webName = webName == null ? "暂无数据" : webName;
			telephone = telephone == null ? "暂无数据" : telephone;
			serviceQQ1 = serviceQQ1 == null ? "暂无数据" : serviceQQ1;
			// String qun1 = this.constantMap.getString("weibo");
			// String qun2 = this.constantMap.getString("QQQUN");
			String qun = "";
			try {
				List<Ffriendlink> quns = (List<Ffriendlink>) this.constantMap.get("quns");
				String qunx = "";
				if (quns.isEmpty()) {
					qun = "暂无数据";
				} else {
					for (Ffriendlink ffriendlink : quns) {
						qunx += ffriendlink.getFurl() + " / ";
					}
					if (StringUtils.isNotBlank(qunx) && qunx.length() >= 3) {
						qun = qunx.substring(0, qunx.length() - 3);
					}
				}
			} catch (Exception e) {
				qun = "暂无数据";
			}
			// String serviceQQ2 = this.constantMap.getString("serviceQQ1");
			String fulldomain = this.constantMap.getString("fulldomain");
			// serviceQQ2 = serviceQQ2 == null ? "暂无数据" : serviceQQ2;
			fulldomain = fulldomain == null ? "暂无数据" : fulldomain;
			// Fwebbaseinfo webinfo = (Fwebbaseinfo)
			// this.constantMap.get("webinfo");
			String copyRights = "";
			try {
				if (this.constantMap.get("webinfo") == null) {
					copyRights = "暂无数据";
				} else {
					Fwebbaseinfo webinfo = (Fwebbaseinfo) this.constantMap.get("webinfo");
					copyRights = webinfo.getFcopyRights();
				}
			} catch (Exception e) {
				copyRights = "暂无数据";
			}
			JSONObject jsonObject = new JSONObject();
			jsonObject.accumulate(Result, true);
			jsonObject.accumulate("fulldomain", fulldomain);
			jsonObject.accumulate("copyRights", copyRights);
			jsonObject.accumulate("webName", webName);
			jsonObject.accumulate("telephone", telephone);
			jsonObject.accumulate("serviceQQ1", serviceQQ1);
			// jsonObject.accumulate("qun1", qun1);
			// jsonObject.accumulate("qun2", qun2);
			jsonObject.accumulate("quns", qun);
			// jsonObject.accumulate("serviceQQ2", serviceQQ2);
			return jsonObject.toString();
		} catch (Exception e) {
			return ApiConstant.getUnknownError(e);
		}
	}
	//
	// public String GetMyCenter(HttpServletRequest request) throws Exception {
	// JSONObject jsonObject = new JSONObject() ;
	// jsonObject.accumulate(Result, true) ;
	// return jsonObject.toString() ;
	// }

	public String GetBtcRechargeListRecord(HttpServletRequest request) throws Exception {

		try {
			String fvirtualCointypeId = request.getParameter("id");
			int currentPage = 1;
			try {
				currentPage = Integer.parseInt(request.getParameter(CurrentPage));
				currentPage = currentPage < 1 ? 1 : currentPage;
			} catch (Exception e) {
			}

			JSONObject jsonObject = new JSONObject();
			jsonObject.accumulate(Result, true);
			int totalPage = 0;

			Fvirtualcointype fvirtualcointype = this.frontVirtualCoinService.findFvirtualCoinById(fvirtualCointypeId);
			if (fvirtualcointype == null || fvirtualcointype.getFstatus() == VirtualCoinTypeStatusEnum.Abnormal) {
				return ApiConstant.getUnknownError(null);
			}

			Fuser fuser = this.realTimeData.getAppFuser(this.curLoginToken);
			Fvirtualaddress fvirtualaddress = this.frontVirtualCoinService.findFvirtualaddress(fuser, fvirtualcointype);

			jsonObject.accumulate("address", fvirtualaddress.getFadderess());

			JSONArray jsonArray = new JSONArray();
			String filter = " where ftype=" + VirtualCapitalOperationTypeEnum.COIN_IN + " and fvirtualcointype.fid="
					+ fvirtualcointype.getFid() + " order by flastUpdateTime desc ";
			int totalCount = this.frontVirtualCoinService.findFvirtualcaptualoperationsCount(filter);
			totalPage = totalCount / maxResult + (totalCount % maxResult == 0 ? 0 : 1);

			jsonObject.accumulate("totalPage", totalPage);
			jsonObject.accumulate("currentPage", currentPage);
			if (currentPage > totalPage) {
				jsonObject.accumulate("list", jsonArray);
				return jsonObject.toString();
			}

			List<Fvirtualcaptualoperation> list = this.frontVirtualCoinService
					.findFvirtualcaptualoperations((currentPage - 1) * maxResult, maxResult, filter, true);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			for (int i = 0; i < list.size(); i++) {
				Fvirtualcaptualoperation fvirtualcaptualoperation = list.get(i);

				JSONObject item = new JSONObject();
				item.accumulate("id", fvirtualcaptualoperation.getFid());
				item.accumulate("fvi_id", fvirtualcaptualoperation.getFvirtualcointype().getFid());
				item.accumulate("fcreateTime", sdf.format(fvirtualcaptualoperation.getFcreateTime()));
				item.accumulate("flastUpdateTime", sdf.format(fvirtualcaptualoperation.getFlastUpdateTime()));
				item.accumulate("famount", fvirtualcaptualoperation.getFamount());
				item.accumulate("ffees", fvirtualcaptualoperation.getFfees());
				item.accumulate("ftype", fvirtualcaptualoperation.getFtype());
				if (fvirtualcaptualoperation.getFtype() == VirtualCapitalOperationTypeEnum.COIN_IN) {
					item.accumulate("fstatus",
							VirtualCapitalOperationInStatusEnum.getEnumString(fvirtualcaptualoperation.getFstatus()));
				} else {
					item.accumulate("fstatus",
							VirtualCapitalOperationOutStatusEnum.getEnumString(fvirtualcaptualoperation.getFstatus()));
				}
				item.accumulate("recharge_virtual_address", fvirtualcaptualoperation.getRecharge_virtual_address());
				jsonArray.add(item);
			}
			jsonObject.accumulate("list", jsonArray);

			return jsonObject.toString();
		} catch (Exception e) {

			return ApiConstant.getUnknownError(e);
		}
	}

	/*
	 * public String GetAccountRecord(HttpServletRequest request) throws
	 * Exception { JSONObject jsonObject = new JSONObject() ;
	 * jsonObject.accumulate(Result, true) ; try{ int currentPage = 1 ; int type
	 * = 0 ; int symbol = 0 ; Fvirtualcointype fvirtualcointype = null ; try{
	 * symbol = Integer.parseInt(request.getParameter("symbol")) ;
	 * fvirtualcointype =
	 * this.frontVirtualCoinService.findFvirtualCoinById(symbol) ; type =
	 * Integer.parseInt(request.getParameter("type")) ; currentPage =
	 * Integer.parseInt(request.getParameter("currentPage")) ; currentPage =
	 * currentPage < 1?1:currentPage ; }catch (Exception e) {}
	 * 
	 * //内容 Fuser fuser = this.realTimeData.getAppFuser(this.curLoginToken) ;
	 * int maxReulsts = Constant.AppRecordPerPage ; StringBuffer sb = new
	 * StringBuffer(" where fuser.fid="+fuser.getFid()
	 * +" and fvirtualcointype.fid="+symbol+"  ") ; if(type == 0 ){ //未成交
	 * sb.append(" and (fstatus="+EntrustStatusEnum.PartDeal+"  or fstatus="
	 * +EntrustStatusEnum.Going+") ") ; }else{
	 * sb.append(" and (fstatus="+EntrustStatusEnum.Cancel+"  or fstatus="
	 * +EntrustStatusEnum.AllDeal+") ") ; } sb.append(" order by fid desc ") ;
	 * List<Fentrust> fentrusts =
	 * this.utilsService.list((currentPage-1)*maxReulsts, maxReulsts,
	 * sb.toString(), true, Fentrust.class) ; int count =
	 * this.utilsService.count(sb.toString(), Fentrust.class) ;
	 * jsonObject.accumulate("currentPage", currentPage) ;
	 * jsonObject.accumulate("totalPage",
	 * count/maxReulsts+(count%maxReulsts==0?0:1)) ;
	 * 
	 * JSONArray jsonArray = new JSONArray() ; for (int i = 0; i <
	 * fentrusts.size(); i++) { Fentrust fentrust = fentrusts.get(i) ;
	 * 
	 * JSONObject item = new JSONObject() ; item.accumulate("fid",
	 * fentrust.getFid()) ; item.accumulate("date",
	 * Utils.dateFormat(fentrust.getFcreateTime())) ; item.accumulate("title",
	 * fvirtualcointype.getfShortName()+fentrust.getFentrustType_s()) ;
	 * item.accumulate("count", fentrust.getFcount()) ;
	 * item.accumulate("leftCount", new BigDecimal(fentrust.getFleftCount(), 4))
	 * ; item.accumulate("price", fentrust.getFprize()) ; item.accumulate("fee",
	 * fentrust.getFfees()) ; item.accumulate("status", fentrust.getFstatus()) ;
	 * item.accumulate("status_s", fentrust.getFstatus_s()) ;
	 * item.accumulate("type", fentrust.getFentrustType()) ;
	 * item.accumulate("type_s", fentrust.getFentrustType_s()) ;
	 * 
	 * jsonArray.add(item) ; }
	 * 
	 * jsonObject.accumulate("list", jsonArray) ; return jsonObject.toString() ;
	 * 
	 * 
	 * }catch(Exception e){
	 * 
	 * } return null ; }
	 */

	public String GetCoinListInfo(HttpServletRequest request) throws Exception {
		JSONObject jsonObject = new JSONObject();
		jsonObject.accumulate(Result, true);
		try {
			JSONArray jsonArray = new JSONArray();
			String filter = "where fstatus=" + VirtualCoinTypeStatusEnum.Normal
					+ " and fisShare=1 order by faddTime asc";
			List<Fvirtualcointype> fvirtualcointypes = this.virtualCoinService.list(0, 0, filter, false);
			for (int i = 0; i < fvirtualcointypes.size(); i++) {
				JSONObject item = new JSONObject();
				Fvirtualcointype fvirtualcointype = fvirtualcointypes.get(i);

				item.accumulate("id", fvirtualcointype.getFid());
				item.accumulate("fname", fvirtualcointype.getFname());
				item.accumulate("fShortName", fvirtualcointype.getfShortName());
				item.accumulate("fSymbol", fvirtualcointype.getfSymbol());
				jsonArray.add(item);
			}
			jsonObject.accumulate("coinList", jsonArray);
			return jsonObject.toString();
		} catch (Exception e) {

			return ApiConstant.getUnknownError(e);
		}
	}

	// 用户信息、资产
	public String GetAccountInfo(HttpServletRequest request) throws Exception {

		try {

			JSONObject jsonObject = new JSONObject();
			jsonObject.accumulate(Result, true);

			Fuser fuser = this.realTimeData.getAppFuser(this.curLoginToken);
			JSONObject userInfoObject = new JSONObject();
			userInfoObject.accumulate("nickName", fuser.getFnickName());
			userInfoObject.accumulate("isGoogleBind", fuser.getFgoogleBind());
			userInfoObject.accumulate("isSecondValidate", fuser.getFopenSecondValidate());
			userInfoObject.accumulate("fid", fuser.getFid());
			// 真实姓名，手机号码
			userInfoObject.accumulate("isRealName", fuser.getFpostRealValidate());
			if (fuser.getFpostRealValidate() == true) {
				userInfoObject.accumulate("realName", fuser.getFrealName());
			} else {
				userInfoObject.accumulate("realName", null);
			}
			userInfoObject.accumulate("isBindMobil", fuser.isFisTelephoneBind());
			if (fuser.isFisTelephoneBind() == true) {
				userInfoObject.accumulate("tel", fuser.getFtelephone());
			} else {
				userInfoObject.accumulate("tel", null);
			}

			jsonObject.accumulate("userInfo", userInfoObject);

			JSONObject assetInfoObject = new JSONObject();
			JSONArray tradeAccountArray = new JSONArray();
			JSONArray marginAccountArray = new JSONArray();

			JSONObject tradeCnyOjbect = new JSONObject();
			JSONObject marginCnyObject = new JSONObject();

			Fwallet fwallet = fuser.getFwallet();
			tradeCnyOjbect.accumulate("id", 0);
			tradeCnyOjbect.accumulate("cnName", "人民币");
			tradeCnyOjbect.accumulate("shortName", "CNY");
			tradeCnyOjbect.accumulate("total",
					new BigDecimal(fwallet.getFtotalRmb()).setScale(4, BigDecimal.ROUND_HALF_UP));
			tradeCnyOjbect.accumulate("frozen",
					new BigDecimal(fwallet.getFfrozenRmb()).setScale(4, BigDecimal.ROUND_HALF_UP));
			tradeAccountArray.add(tradeCnyOjbect);

			marginCnyObject.accumulate("id", 0);
			marginCnyObject.accumulate("total", 0);
			marginCnyObject.accumulate("frozen", 0);
			marginCnyObject.accumulate("borrow", 0);
			marginAccountArray.add(marginCnyObject);

			double totalAsset = 0D;// 总资产
			double netAsset = 0D;// 净资产
			double tradeTotalAsset = 0D;// 交易总资产
			double tradeNetAsset = 0D;// 交易净资产
			double marginTotalAsset = 0D;// 放款总资产
			// 总借金额
			double totalBorrow = 0d;

			totalAsset += fwallet.getFtotalRmb() + fwallet.getFfrozenRmb() + marginTotalAsset;
			netAsset += fwallet.getFtotalRmb() + fwallet.getFfrozenRmb() + marginTotalAsset;

			tradeTotalAsset += fwallet.getFtotalRmb() + fwallet.getFfrozenRmb();
			tradeNetAsset += fwallet.getFtotalRmb() + fwallet.getFfrozenRmb();

			// 虚拟币地址：
			JSONArray coinAddress = new JSONArray();

			String filter = "where fstatus=" + VirtualCoinTypeStatusEnum.Normal + " order by faddTime desc";
			List<Fvirtualcointype> fvirtualcointypes = this.virtualCoinService.list(0, 0, filter, false);
			for (int i = 0; i < fvirtualcointypes.size(); i++) {
				Fvirtualcointype fvirtualcointype = fvirtualcointypes.get(i);
				int count = fvirtualcointype.getFcount();
				Fvirtualwallet fvirtualwallet = this.frontUserService.findVirtualWalletByUser(fuser.getFid(),
						fvirtualcointype.getFid());
				JSONObject tradeObj = new JSONObject();
				JSONObject marginObj = new JSONObject();

				double curPrice = this.realTimeData.getLatestDealPrize(fvirtualcointype.getFid());
				double asset = curPrice * (fvirtualwallet.getFtotal() + fvirtualwallet.getFfrozen());

				tradeObj.accumulate("id", fvirtualcointype.getFid());
				tradeObj.accumulate("logo", fvirtualcointype.getFurl());
				tradeObj.accumulate("cnName", fvirtualcointype.getFname());
				tradeObj.accumulate("shortName", fvirtualcointype.getfShortName());
				tradeObj.accumulate("total",
						new BigDecimal(fvirtualwallet.getFtotal()).setScale(4, BigDecimal.ROUND_HALF_UP));
				tradeObj.accumulate("frozen",
						new BigDecimal(fvirtualwallet.getFfrozen()).setScale(4, BigDecimal.ROUND_HALF_UP));
				tradeObj.accumulate("borrow", 0);
				tradeObj.accumulate("zhehe", Utils.getDouble(asset, 4));
				tradeObj.accumulate("curPrice", Utils.getDouble(curPrice, count));
				tradeAccountArray.add(tradeObj);

				marginObj.accumulate("id", fvirtualcointype.getFid());
				marginObj.accumulate("total", 0);
				marginObj.accumulate("frozen", 0);
				marginObj.accumulate("borrow", 0);
				marginAccountArray.add(marginObj);

				totalAsset = totalAsset + asset;
				netAsset = netAsset + asset;
				tradeTotalAsset += asset;
				tradeNetAsset += asset;

				// 虚拟币地址
				if (fvirtualcointype.isFIsWithDraw()) {
					Fvirtualaddress fvirtualaddress = this.frontVirtualCoinService.findFvirtualaddress(fuser,
							fvirtualcointype);
					JSONObject item = new JSONObject();
					item.accumulate("coinId", fvirtualcointype.getFid());
					item.accumulate("coinName", fvirtualcointype.getFname());
					item.accumulate("coinShortName", fvirtualcointype.getfShortName());
					item.accumulate("address", fvirtualaddress.getFadderess());
					coinAddress.add(item);
				}
			}
			assetInfoObject.accumulate("totalAsset", new BigDecimal(totalAsset).setScale(4, BigDecimal.ROUND_HALF_UP));

			JSONObject tradeAccountObject = new JSONObject();
			tradeAccountObject.accumulate("coins", tradeAccountArray);
			tradeAccountObject.accumulate("totalAsset",
					new BigDecimal(tradeTotalAsset).setScale(4, BigDecimal.ROUND_HALF_UP));
			tradeAccountObject.accumulate("netAsset",
					new BigDecimal(tradeNetAsset - totalBorrow).setScale(4, BigDecimal.ROUND_HALF_UP));
			assetInfoObject.accumulate("tradeAccount", tradeAccountObject);

			JSONObject marginAccountObject = new JSONObject();
			marginAccountObject.accumulate("coins", marginAccountArray);
			marginAccountObject.accumulate("totalAsset",
					new BigDecimal(marginTotalAsset).setScale(4, BigDecimal.ROUND_HALF_UP));
			assetInfoObject.accumulate("marginAccount", marginAccountObject);

			jsonObject.accumulate("asset", assetInfoObject);
			// 获取可兑换rmb种类
			List<Fsubscription> fsubscription = getRechargeTypeList();
			JSONArray fsubscriptionArray = new JSONArray();
			for (Fsubscription fsubscription2 : fsubscription) {
				Fvirtualwallet fvirtualwallet = this.frontUserService.findVirtualWalletByUser(fuser.getFid(),
						fsubscription2.getFvirtualcointype().getFid());
				JSONObject item = new JSONObject();
				item.accumulate("id", fsubscription2.getFvirtualcointype().getFid());
				item.accumulate("name", fsubscription2.getFvirtualcointype().getFname());
				item.accumulate("exprice", fsubscription2.getFprice());
				item.accumulate("fsymbol", fsubscription2.getFvirtualcointype().getfSymbol());
				item.accumulate("famount", fvirtualwallet.getFtotal());
				fsubscriptionArray.add(item);
			}
			jsonObject.accumulate("fsubscription", fsubscriptionArray);
			// 获取提现手续费
			double fee = this.frontAccountService.findWithdrawFeesByLevel(fuser.getFscore().getFlevel());
			jsonObject.accumulate("fee", fee);
			// 提现银行卡信息

			List<FbankinfoWithdraw> fbankinfoWithdraw = this.utilsService.list(0, 0, " where fuser.fid='"
					+ fuser.getFid() + "' and  fstatus=" + BankInfoWithdrawStatusEnum.NORMAL_VALUE + " ", false,
					FbankinfoWithdraw.class);
			JSONArray withdrawInfos = new JSONArray();
			for (FbankinfoWithdraw withdrawInfo : fbankinfoWithdraw) {
				JSONObject item = new JSONObject();
				item.accumulate("id", withdrawInfo.getFid());
				item.accumulate("bankType", withdrawInfo.getFbankType());
				item.accumulate("address", withdrawInfo.getFaddress());
				item.accumulate("bankNumber", BankTypeEnum.getEnumString(withdrawInfo.getFbankType()) + " 尾号"
						+ (withdrawInfo.getFbankNumber().length() >= 4
								? (withdrawInfo.getFbankNumber().substring(withdrawInfo.getFbankNumber().length() - 4))
								: withdrawInfo.getFbankNumber()));

				withdrawInfos.add(item);
			}
			jsonObject.accumulate("withdrawInfos", withdrawInfos);

			// 虚拟币地址
			jsonObject.accumulate("coinAddress", coinAddress);
			return jsonObject.toString();

		} catch (Exception e) {

			return ApiConstant.getUnknownError(e);
		}

	}

	// 买币
	public String buyBtcSubmit(HttpServletRequest request) throws Exception {
		try {
			JSONObject jsonObject = new JSONObject();
			jsonObject.accumulate(Result, true);

			Fuser fuser = this.realTimeData.getAppFuser(this.curLoginToken);
			int limited = 0;// Integer.parseInt(request.getParameter("limited"))
							// ;//是否按照市场价买入
			String symbol = request.getParameter("symbol");// 币种

			Fvirtualcointype fvirtualcointype = this.frontVirtualCoinService.findFvirtualCoinById(symbol);
			if (fvirtualcointype == null || !fvirtualcointype.isFisShare()
					|| fvirtualcointype.getFstatus() == VirtualCoinTypeStatusEnum.Abnormal) {
				jsonObject.accumulate(Value, "该数字货币已暂停交易!");
				jsonObject.accumulate(ErrorCode, 1);
				return jsonObject.toString();
			}
			int count = fvirtualcointype.getFcount();
			double tradeAmount = Utils.getDouble(Double.parseDouble(request.getParameter("tradeAmount")), 4);// 数量
			double tradeCnyPrice = Utils.getDouble(Double.parseDouble(request.getParameter("tradeCnyPrice")), count);// 单价
			String tradePwd = request.getParameter("tradePwd");

			if (tradeAmount < 0.0001D) {
				jsonObject.accumulate(Value, "数量不能小于0.0001!");
				jsonObject.accumulate(ErrorCode, 1);
				return jsonObject.toString();
			}

			if (tradeCnyPrice <= 0D) {
				jsonObject.accumulate(Value, "挂单价格不能为0!");
				jsonObject.accumulate(ErrorCode, 1);
				return jsonObject.toString();
			}

			Flimittrade limittrade = this.isLimitTrade(fvirtualcointype.getFid());
			double upPrice = 0d;
			double downPrice = 0d;

			boolean isLimitBuy = false;
			if (limittrade != null) {
				upPrice = Utils.getDouble(
						limittrade.getFupprice() + limittrade.getFupprice() * limittrade.getFpercent(), count);
				downPrice = Utils.getDouble(
						limittrade.getFdownprice() - limittrade.getFdownprice() * limittrade.getFpercent(), count);
				if (downPrice < 0)
					downPrice = 0;
				if (limittrade.getFpercent() > 0) {
					if (tradeCnyPrice > upPrice) {
						jsonObject.accumulate(ErrorCode, 1);
						jsonObject.accumulate(Value, "挂单价格不能高于涨停价￥" + upPrice);
						return jsonObject.toString();
					}
					if (tradeCnyPrice < downPrice) {
						jsonObject.accumulate(ErrorCode, 1);
						jsonObject.accumulate(Value, "挂单价格不能低于跌停价￥" + downPrice);
						return jsonObject.toString();
					}
				}

			}
			if (!Comm.getISTRAD_OWNBYOWN()) {
				double price = 0;
				String filter = " where fuser.fid='" + fuser.getFid() + "' AND fEntrustType=1 "
						+ "and fStatus!=3 and fStatus!=4 " + "ORDER BY fPrize";
				List<Fentrust> fenList = frontTradeService.findFentrustByParam(0, 1, filter, true);

				if (fenList != null && fenList.size() > 0) {
					price = fenList.get(0).getFprize();
					if (tradeCnyPrice >= price) {
						jsonObject.accumulate("ErrorCode", 1);
						jsonObject.accumulate("Value", "您不能购买自己的订单");
						return jsonObject.toString();
					}
				}
			}

			double totalTradePrice = 0F;
			if (limited == 0) {
				totalTradePrice = tradeAmount * tradeCnyPrice;
			} else {
				totalTradePrice = tradeAmount;
			}

			Fwallet fwallet = fuser.getFwallet();

			if (tradePwd == null || tradePwd.trim().length() == 0) {
				jsonObject.accumulate(Value, "交易密码不能为空!");
				jsonObject.accumulate(ErrorCode, 1);
				return jsonObject.toString();
			}

			if (fuser.getFtradePassword() == null) {
				jsonObject.accumulate(Value, "请先设置交易密码!");
				jsonObject.accumulate(ErrorCode, 1);
				return jsonObject.toString();
			}
			if (!Utils.getMD5_32(tradePwd).equals(fuser.getFtradePassword())) {
				jsonObject.accumulate(Value, "交易密码不正确!");
				jsonObject.accumulate(ErrorCode, 1);
				return jsonObject.toString();
			}

			if (fwallet.getFtotalRmb() < totalTradePrice) {
				jsonObject.accumulate(Value, "余额不足!");
				jsonObject.accumulate(ErrorCode, 1);
				return jsonObject.toString();
			}
			boolean flag = false;
			Fentrust fentrust = null;
			try {
				fentrust = this.frontTradeService.updateEntrustBuy(symbol, tradeAmount, tradeCnyPrice, fuser,
						limited == 1);
				flag = true;
			} catch (Exception e) {

			}
			if (flag) {
				if (limited == 1) {
					this.realTimeData.addEntrustLimitBuyMap(symbol, fentrust);
				} else {
					this.realTimeData.addEntrustBuyMap(symbol, fentrust);
				}

				jsonObject.accumulate(Value, "挂单成功!");
				jsonObject.accumulate(ErrorCode, 0);
			} else {
				jsonObject.accumulate(Value, "网络异常!");
				jsonObject.accumulate(ErrorCode, 1);
			}

			return jsonObject.toString();

		} catch (Exception e) {
			return ApiConstant.getUnknownError(e);
		}
	}

	// 卖币
	public String sellBtcSubmit(HttpServletRequest request) throws Exception {
		try {

			JSONObject jsonObject = new JSONObject();
			jsonObject.accumulate(Result, true);
			int limited = 0;// Integer.parseInt(request.getParameter("limited"))
							// ;//是否按照市场价买入
			String symbol = request.getParameter("symbol");// 币种
			Fuser fuser = this.realTimeData.getAppFuser(this.curLoginToken);

			Fvirtualcointype fvirtualcointype = this.frontVirtualCoinService.findFvirtualCoinById(symbol);
			if (fvirtualcointype == null || !fvirtualcointype.isFisShare()
					|| fvirtualcointype.getFstatus() != VirtualCoinTypeStatusEnum.Normal) {
				jsonObject.accumulate(Value, "该数字货币已暂停交易!");
				jsonObject.accumulate(ErrorCode, 1);
				return jsonObject.toString();
			}
			int count = fvirtualcointype.getFcount();

			double tradeAmount = Utils.getDouble(Double.parseDouble(request.getParameter("tradeAmount")), 4);// 数量
			double tradeCnyPrice = Utils.getDouble(Double.parseDouble(request.getParameter("tradeCnyPrice")), 2);// 单价
			String tradePwd = request.getParameter("tradePwd");

			if (tradeAmount < 0.0001D) {
				jsonObject.accumulate(Value, "挂单数量不能小于0.0001!");
				jsonObject.accumulate(ErrorCode, 1);
				return jsonObject.toString();
			}

			if (tradeCnyPrice <= 0D) {
				jsonObject.accumulate(Value, "挂单价格不能为0!");
				jsonObject.accumulate(ErrorCode, 1);
				return jsonObject.toString();
			}

			Fvirtualwallet fvirtualwallet = this.frontUserService.findVirtualWalletByUser(fuser.getFid(), symbol);
			if (fvirtualwallet == null) {
				jsonObject.accumulate(Value, "帐户异常");
				jsonObject.accumulate(ErrorCode, 1);
				return jsonObject.toString();
			}

			boolean isLimitSell = false;
			Flimittrade limittrade = this.isLimitTrade(fvirtualcointype.getFid());
			double upPrice = 0d;
			double downPrice = 0d;
			if (limittrade != null) {
				upPrice = Utils.getDouble(
						limittrade.getFupprice() + limittrade.getFupprice() * limittrade.getFpercent(), count);
				downPrice = Utils.getDouble(
						limittrade.getFdownprice() - limittrade.getFdownprice() * limittrade.getFpercent(), count);
				if (downPrice < 0)
					downPrice = 0;
				if (limittrade.getFpercent() > 0) {
					if (tradeCnyPrice > upPrice) {
						jsonObject.accumulate(ErrorCode, 1);
						jsonObject.accumulate(Value, "挂单价格不能高于涨停价￥" + upPrice);
						return jsonObject.toString();
					}
					if (tradeCnyPrice < downPrice) {
						jsonObject.accumulate(ErrorCode, 1);
						jsonObject.accumulate(Value, "挂单价格不能低于跌停价￥" + downPrice);
						return jsonObject.toString();
					}
				}
			}
			if (!Comm.getISTRAD_OWNBYOWN()) {
				double price = 0;
				String filter = " where fuser.fid='" + fuser.getFid() + "' AND fEntrustType=0 "
						+ "and fStatus!=3 and fStatus!=4 " + "ORDER BY fPrize DESC";
				List<Fentrust> fenList = frontTradeService.findFentrustByParam(0, 1, filter, true);

				if (fenList != null && fenList.size() > 0) {
					price = fenList.get(0).getFprize();
					if (tradeCnyPrice <= price) {
						jsonObject.accumulate("ErrorCode", 1);
						jsonObject.accumulate("Value", "您不能和自己的订单成交");
						return jsonObject.toString();
					}
				}
			}

			if (tradePwd == null || tradePwd.trim().length() == 0) {
				jsonObject.accumulate(Value, "交易密码不能为空!");
				jsonObject.accumulate(ErrorCode, 1);
				return jsonObject.toString();
			}

			if (fuser.getFtradePassword() == null) {
				jsonObject.accumulate(Value, "请先设置交易密码!");
				jsonObject.accumulate(ErrorCode, 1);
				return jsonObject.toString();
			}
			if (!Utils.getMD5_32(tradePwd).equals(fuser.getFtradePassword())) {
				jsonObject.accumulate(Value, "交易密码不正确!");
				jsonObject.accumulate(ErrorCode, 1);
				return jsonObject.toString();
			}
			if (fvirtualwallet.getFtotal() < tradeAmount) {
				jsonObject.accumulate(Value, "余额不足!");
				jsonObject.accumulate(ErrorCode, 1);
				return jsonObject.toString();
			}

			boolean flag = false;
			Fentrust fentrust = null;
			try {
				fentrust = this.frontTradeService.updateEntrustSell(symbol, tradeAmount, tradeCnyPrice, fuser,
						limited == 1);
				flag = true;
			} catch (Exception e) {

			}
			if (flag) {
				if (limited == 1) {
					this.realTimeData.addEntrustLimitSellMap(symbol, fentrust);
				} else {
					this.realTimeData.addEntrustSellMap(symbol, fentrust);
				}

				jsonObject.accumulate(Value, "挂单成功!");
				jsonObject.accumulate(ErrorCode, 0);
			} else {
				jsonObject.accumulate(Value, "网络异常!");
				jsonObject.accumulate(ErrorCode, 1);
			}

			return jsonObject.toString();
		} catch (Exception e) {

			return ApiConstant.getUnknownError(e);
		}
	}

	// 推广收益明细
	public String GetIntrolinfo(HttpServletRequest request) throws Exception {
		try {
			JSONObject jsonObject = new JSONObject();
			jsonObject.accumulate(Result, true);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			int currentPage = 1;
			try {
				currentPage = Integer.parseInt(request.getParameter("currentPage"));
				currentPage = currentPage < 1 ? 1 : currentPage;
			} catch (Exception e) {
			}

			Fuser fuser = this.realTimeData.getAppFuser(this.curLoginToken);

			String filter = "where fuser.fid='" + fuser.getFid() + "' order by fcreatetime desc";
			int total = this.adminService.getAllCount("Fintrolinfo", filter);
			int totalPage = total / Constant.AppRecordPerPage + ((total % Constant.AppRecordPerPage) == 0 ? 0 : 1);
			List<Fintrolinfo> fintrolinfos = this.introlinfoService.list((currentPage - 1) * Constant.AppRecordPerPage,
					Constant.AppRecordPerPage, filter, true);

			jsonObject.accumulate("totalPage", totalPage);
			jsonObject.accumulate("currentPage", currentPage);
			JSONArray jsonArray = new JSONArray();
			if (currentPage > totalPage) {
				jsonObject.accumulate("list", jsonArray);
				return jsonObject.toString();
			}
			for (int i = 0; i < fintrolinfos.size(); i++) {
				Fintrolinfo fintrolinfo = fintrolinfos.get(i);
				JSONObject item = new JSONObject();
				item.accumulate("createTime", sdf.format(fintrolinfo.getFcreatetime()));
				item.accumulate("title", fintrolinfo.getFtitle());
				jsonArray.add(item);
			}
			jsonObject.accumulate("list", jsonArray);
			return jsonObject.toString();
		} catch (Exception e) {

			return ApiConstant.getUnknownError(e);
		}
	}

	// 取得线下明细
	public String GetIntrolDetail(HttpServletRequest request) throws Exception {
		try {
			JSONObject jsonObject = new JSONObject();
			jsonObject.accumulate(Result, true);
			int currentPage = 1;
			try {
				currentPage = Integer.parseInt(request.getParameter("currentPage"));
				currentPage = currentPage < 1 ? 1 : currentPage;
			} catch (Exception e) {
			}
			Fuser fuser = this.realTimeData.getAppFuser(this.curLoginToken);
			String filter = "where fIntroUser_id.fid='" + fuser.getFid() + "' order by fid desc";
			int total = this.adminService.getAllCount("Fuser", filter);
			int totalPage = total / Constant.AppRecordPerPage + ((total % Constant.AppRecordPerPage) == 0 ? 0 : 1);
			List<Fuser> fusers = this.userService.list((currentPage - 1) * Constant.AppRecordPerPage,
					Constant.AppRecordPerPage, filter, true);
			jsonObject.accumulate("totalPage", totalPage);
			jsonObject.accumulate("currentPage", currentPage);
			JSONArray jsonArray = new JSONArray();
			if (currentPage > totalPage) {
				jsonObject.accumulate("list", jsonArray);
				return jsonObject.toString();
			}
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			for (int i = 0; i < fusers.size(); i++) {
				Fuser user = fusers.get(i);
				JSONObject item = new JSONObject();
				item.accumulate("id", user.getFid());
				item.accumulate("loginName", user.getFloginName());
				item.accumulate("time", sdf.format(user.getFregisterTime()));
				jsonArray.add(item);
			}
			jsonObject.accumulate("list", jsonArray);

			return jsonObject.toString();
		} catch (Exception e) {

			return ApiConstant.getUnknownError(e);
		}
	}

	// 实名认证
	public String ValidateIdentity(HttpServletRequest request) throws Exception {
		try {
			JSONObject jsonObject = new JSONObject();
			jsonObject.accumulate(Result, true);
			// int identityType =
			// Integer.parseInt(request.getParameter("identityType"));
			String identityNo = request.getParameter("identityNo").toLowerCase();
			String realName = request.getParameter("realName");

			String[] ValCodeArr = { "1", "0", "x", "9", "8", "7", "6", "5", "4", "3", "2" };
			String[] Wi = { "7", "9", "10", "5", "8", "4", "2", "1", "6", "3", "7", "9", "10", "5", "8", "4", "2" };
			if (identityNo.trim().length() != 15 && identityNo.trim().length() != 18) {
				jsonObject.accumulate(ErrorCode, 1);
				jsonObject.accumulate(Value, "身份证号码长度应该为15位或18位!");
				return jsonObject.toString();
			}

			String Ai = "";
			if (identityNo.length() == 18) {
				Ai = identityNo.substring(0, 17);
			} else if (identityNo.length() == 15) {
				Ai = identityNo.substring(0, 6) + "19" + identityNo.substring(6, 15);
			}
			if (Utils.isNumeric(Ai) == false) {
				jsonObject.accumulate(ErrorCode, 1);
				jsonObject.accumulate(Value, "身份证号码有误!");
				return jsonObject.toString();
			}
			// ================ 出生年月是否有效 ================
			String strYear = Ai.substring(6, 10);// 年份
			String strMonth = Ai.substring(10, 12);// 月份
			String strDay = Ai.substring(12, 14);// 月份
			if (Utils.isDate(strYear + "-" + strMonth + "-" + strDay) == false) {
				jsonObject.accumulate(ErrorCode, 1);
				jsonObject.accumulate(Value, "身份证号码有误!");
				return jsonObject.toString();
			}
			GregorianCalendar gc = new GregorianCalendar();
			SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
			try {
				if ((gc.get(Calendar.YEAR) - Integer.parseInt(strYear)) > 150
						|| (gc.getTime().getTime() - s.parse(strYear + "-" + strMonth + "-" + strDay).getTime()) < 0) {
					jsonObject.accumulate(ErrorCode, 1);
					jsonObject.accumulate(Value, "身份证号码有误!");
					return jsonObject.toString();
				}
			} catch (NumberFormatException e) {

			} catch (java.text.ParseException e) {

			}
			if (Integer.parseInt(strMonth) > 12 || Integer.parseInt(strMonth) == 0) {
				jsonObject.accumulate(ErrorCode, 1);
				jsonObject.accumulate(Value, "身份证号码有误!");
				return jsonObject.toString();
			}
			if (Integer.parseInt(strDay) > 31 || Integer.parseInt(strDay) == 0) {
				jsonObject.accumulate(ErrorCode, 1);
				jsonObject.accumulate(Value, "身份证号码有误!");
				return jsonObject.toString();
			}
			// =====================(end)=====================

			// ================ 地区码时候有效 ================
			Hashtable h = Utils.getAreaCode();
			if (h.get(Ai.substring(0, 2)) == null) {
				jsonObject.accumulate(ErrorCode, 1);
				jsonObject.accumulate(Value, "身份证号码有误!");
				return jsonObject.toString();
			}
			// ==============================================

			// ================ 判断最后一位的值 ================
			int TotalmulAiWi = 0;
			for (int i = 0; i < 17; i++) {
				TotalmulAiWi = TotalmulAiWi + Integer.parseInt(String.valueOf(Ai.charAt(i))) * Integer.parseInt(Wi[i]);
			}
			int modValue = TotalmulAiWi % 11;
			String strVerifyCode = ValCodeArr[modValue];
			Ai = Ai + strVerifyCode;

			if (identityNo.length() == 18) {
				if (Ai.equals(identityNo) == false) {
					jsonObject.accumulate(ErrorCode, 1);
					jsonObject.accumulate(Value, "身份证号码有误!");
					return jsonObject.toString();
				}
			}

			if (realName.trim().length() > 50) {
				jsonObject.accumulate(ErrorCode, 1);
				jsonObject.accumulate(Value, "真实姓名不合法!");
				return jsonObject.toString();
			}

			String sql = "where fidentityNo='" + identityNo + "'";
			int count = this.adminService.getAllCount("Fuser", sql);
			if (count > 0) {
				jsonObject.accumulate(ErrorCode, 1);
				jsonObject.accumulate(Value, "身份证号码已存在!");
				return jsonObject.toString();
			}

			Fuser fuser = this.realTimeData.getAppFuser(this.curLoginToken);

			fuser.setFidentityType(0);
			fuser.setFidentityNo(identityNo);
			fuser.setFrealName(realName);
			fuser.setFpostRealValidate(true);
			fuser.setFpostRealValidateTime(Utils.getTimestamp());

			this.frontUserService.updateFuser(fuser);
			jsonObject.accumulate("realName", realName);
			jsonObject.accumulate("identityNo", identityNo);
			jsonObject.accumulate(ErrorCode, 0);
			jsonObject.accumulate(Value, "提交成功");
			return jsonObject.toString();
		} catch (Exception e) {

			return ApiConstant.getUnknownError(e);
		}
	}

	// 查看实名认证信息
	public String ViewValidateIdentity(HttpServletRequest request) throws Exception {
		try {
			JSONObject jsonObject = new JSONObject();
			jsonObject.accumulate(Result, true);
			jsonObject.accumulate(ErrorCode, 0);

			Fuser fuser = this.realTimeData.getAppFuser(this.curLoginToken);
			if (fuser.getFpostRealValidate()) {
				jsonObject.accumulate("realName", fuser.getFrealName());
				jsonObject.accumulate("identityNo", fuser.getFidentityNo());
				jsonObject.accumulate("postRealValidate", fuser.getFpostRealValidate());
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				jsonObject.accumulate("postRealValidateTime", sdf.format(fuser.getFpostRealValidateTime()));
			} else {
				jsonObject.accumulate("realName", null);
				jsonObject.accumulate("identityNo", null);
				jsonObject.accumulate("postRealValidate", fuser.getFpostRealValidate());
				jsonObject.accumulate("postRealValidateTime", null);
			}
			return jsonObject.toString();
		} catch (Exception e) {

			return ApiConstant.getUnknownError(e);
		}
	}

	// 绑定手机号码
	public String bindPhone(HttpServletRequest request) throws Exception {
		try {
			String areaCode = "86";

			String phone = request.getParameter("phone");
			String code = request.getParameter("code");

			JSONObject jsonObject = new JSONObject();
			jsonObject.accumulate(Result, true);

			if (!phone.matches("^\\d{10,14}$")) {// 手機格式不對
				jsonObject.accumulate(ErrorCode, 1);
				jsonObject.accumulate(Value, "手机格式错误");
				return jsonObject.toString();
			}

			Fuser fuser = this.realTimeData.getAppFuser(this.curLoginToken);
			if (fuser.isFisTelephoneBind()) {// 已經綁定過手機了
				jsonObject.accumulate(ErrorCode, 1);
				jsonObject.accumulate(Value, "您的账号已经绑定手机了");
				return jsonObject.toString();
			}

			String ip = Utils.getIp(request);
			int tel_limit = this.frontValidateService.getLimitCount(ip, CountLimitTypeEnum.TELEPHONE);
			if (tel_limit <= 0) {
				jsonObject.accumulate(ErrorCode, 1);
				jsonObject.accumulate(Value, "短信验证码错误次数超限，请稍后再试！");
				return jsonObject.toString();
			}

			if (validateMessageCode(fuser, areaCode, phone, MessageTypeEnum.BANGDING_MOBILE, code)) {
				// 判斷手機是否被綁定了
				List<Fuser> fusers = this.frontUserService.findUserByProperty("ftelephone", phone);
				if (fusers.size() > 0) {
					jsonObject.accumulate(ErrorCode, 1);
					jsonObject.accumulate(Value, "该手机号码已经绑定到其他账号了");
					return jsonObject.toString();
				}

				fuser.setFareaCode(areaCode);
				fuser.setFtelephone(phone);
				fuser.setFisTelephoneBind(true);
				fuser.setFlastUpdateTime(Utils.getTimestamp());
				this.frontUserService.updateFUser(fuser, getSession(request), 0, Utils.getIp(request));

				// 推广数量+1
				Fuser introFuser = fuser.getfIntroUser_id();
				Fintrolinfo introlInfo = null;
				Fscore fintrolscore = null;
				Fscore fscore = fuser.getFscore();
				this.frontUserService.updateFuser(introFuser, introlInfo, fintrolscore, fscore);

				// 成功
				this.frontValidateService.deleteCountLimite(ip, CountLimitTypeEnum.TELEPHONE);

				jsonObject.accumulate(ErrorCode, 0);
				jsonObject.accumulate(Value, "绑定手机成功");
				return jsonObject.toString();
			} else {
				// 手機驗證錯誤
				this.frontValidateService.updateLimitCount(ip, CountLimitTypeEnum.TELEPHONE);
				jsonObject.accumulate(ErrorCode, 1);
				jsonObject.accumulate(Value, "验证码错误");
				return jsonObject.toString();
			}
		} catch (Exception e) {

			return ApiConstant.getUnknownError(e);
		}
	}

	// 解除绑定
	public String UnbindPhone(HttpServletRequest request) throws Exception {
		try {
			String phoneCode = request.getParameter("phoneCode");

			Fuser fuser = this.realTimeData.getAppFuser(this.curLoginToken);
			JSONObject jsonObject = new JSONObject();
			jsonObject.accumulate(Result, true);

			if (fuser.isFisTelephoneBind()) {
				String ip = Utils.getIp(request);
				int mobil_limit = this.frontValidateService.getLimitCount(ip, CountLimitTypeEnum.TELEPHONE);
				if (mobil_limit <= 0) {
					jsonObject.accumulate(ErrorCode, 1);
					jsonObject.accumulate(Value, "验证码错误次数超限，请稍后再试！");
					return jsonObject.toString();
				} else {
					if (super.validateMessageCode(fuser, fuser.getFareaCode(), fuser.getFtelephone(),
							MessageTypeEnum.JIEBANG_MOBILE, phoneCode)) {
						boolean flag = false;
						try {
							fuser.setFisTelephoneBind(false);
							fuser.setFtelephone(null);
							fuser.setFlastUpdateTime(Utils.getTimestamp());
							this.frontUserService.updateFUser(fuser, getSession(request), 0, ip);

							// 推广数量-1
							Fuser introFuser = fuser.getfIntroUser_id();
							if (introFuser != null) {
								introFuser = this.frontUserService.findById(fuser.getfIntroUser_id().getFid());
								introFuser.setfInvalidateIntroCount(introFuser.getfInvalidateIntroCount() - 1);
								this.frontUserService.updateFuser(introFuser);
							}

							flag = true;
						} catch (Exception e) {

						}
						if (flag) {

							jsonObject.accumulate(ErrorCode, 0);
							jsonObject.accumulate(Value, "解除绑定成功！");
							return jsonObject.toString();
						} else {
							jsonObject.accumulate(ErrorCode, 1);
							jsonObject.accumulate(Value, "网络错误，请稍后再试！");
							return jsonObject.toString();
						}
					} else {
						this.frontValidateService.updateLimitCount(ip, CountLimitTypeEnum.TELEPHONE);
						jsonObject.accumulate(ErrorCode, 1);
						jsonObject.accumulate(Value, "验证码错误");
						return jsonObject.toString();
					}
				}
			} else {
				jsonObject.accumulate(ErrorCode, 1);
				jsonObject.accumulate(Value, "您的账号没有绑定手机号码");
				return jsonObject.toString();
			}
		} catch (Exception e) {

			return ApiConstant.getUnknownError(e);
		}

	}

	// 修改绑定
	public String ChangebindPhone(HttpServletRequest request) throws Exception {
		try {
			String phoneCode1 = request.getParameter("phoneCode1");
			String phoneCode2 = request.getParameter("phoneCode2");
			String phone = request.getParameter("phone");
			String areaCode = "86";
			String ip = Utils.getIp(request);
			Fuser fuser = this.realTimeData.getAppFuser(this.curLoginToken);
			JSONObject jsonObject = new JSONObject();
			jsonObject.accumulate(Result, true);

			if (phone.matches(Constant.PhoneReg) == false) {
				jsonObject.accumulate(ErrorCode, -1);
				jsonObject.accumulate(Value, "新手机号码格式错误");
				return jsonObject.toString();
			}

			if (fuser.isFisTelephoneBind()) {
				if (super.validateMessageCode(fuser, areaCode, fuser.getFtelephone(), MessageTypeEnum.JIEBANG_MOBILE,
						phoneCode1) == false) {
					jsonObject.accumulate(ErrorCode, -1);
					jsonObject.accumulate(Value, "原手机验证码错误");
					return jsonObject.toString();
				}
				if (super.validateMessageCode(fuser, areaCode, fuser.getFtelephone(), MessageTypeEnum.UPDATE_MOBILE,
						phoneCode2) == false) {
					jsonObject.accumulate(ErrorCode, -1);
					jsonObject.accumulate(Value, "新手机验证码错误");
					return jsonObject.toString();
				}

				if (this.frontUserService.isTelephoneExists(phone) == true) {
					jsonObject.accumulate(ErrorCode, -1);
					jsonObject.accumulate(Value, "该手机号码已经绑定到其他账户");
					return jsonObject.toString();
				}

				boolean flag = false;
				try {
					if (fuser.getFloginName() != null && fuser.getFloginName().matches(Constant.PhoneReg)) {
						fuser.setFloginName(phone);
					}
					fuser.setFtelephone(phone);
					fuser.setFlastUpdateTime(Utils.getTimestamp());
					this.frontUserService.updateFUser(fuser, getSession(request), 0, ip);

					flag = true;
				} catch (Exception e) {

				}
				if (flag) {

					jsonObject.accumulate(ErrorCode, 0);
					jsonObject.accumulate(Value, "修改手机成功！");
					return jsonObject.toString();
				} else {
					jsonObject.accumulate(ErrorCode, -1);
					jsonObject.accumulate(Value, "网络错误，请稍后再试！");
					return jsonObject.toString();
				}
			} else {
				jsonObject.accumulate(ErrorCode, -1);
				jsonObject.accumulate(Value, "您的账号没有绑定手机号码");
				return jsonObject.toString();
			}
		} catch (Exception e) {

			return ApiConstant.getUnknownError(e);
		}

	}

	// 提现银行列表
	public String GetWithdrawBankList(HttpServletRequest request) throws Exception {
		try {
			JSONObject jsonObject = new JSONObject();
			jsonObject.accumulate(Result, true);

			JSONArray jsonArray = new JSONArray();
			for (int i = BankTypeEnum.GH; i <= BankTypeEnum.QT; i++) {
				JSONObject item = new JSONObject();
				item.accumulate("id", i);
				item.accumulate("name", BankTypeEnum.getEnumString(i));
				jsonArray.add(item);

			}
			jsonObject.accumulate("list", jsonArray);
			return jsonObject.toString();
		} catch (Exception e) {

			return ApiConstant.getUnknownError(e);
		}
	}

	// http://test.ruizton.com:8080/user/updateOutAddress.html?random=4&account=3333&address=55555&cnyOutType=1&openBankType=1&phoneCode=333333&totpCode=0&type=1
	// 设置提现银行卡信息
	public String SetWithdrawCnyBankInfo(HttpServletRequest request) throws Exception {

		try {
			String account = request.getParameter("account");
			// String phoneCode = request.getParameter("phoneCode");
			int openBankType = Integer.parseInt(request.getParameter("openBankType"));
			String address = request.getParameter("address");
			JSONObject jsonObject = new JSONObject();
			jsonObject.accumulate(Result, true);

			Fuser fuser = this.realTimeData.getAppFuser(this.curLoginToken);
			// boolean isTelephoneBind = fuser.isFisTelephoneBind();
			/*
			 * if (!isTelephoneBind) { jsonObject.accumulate(ErrorCode, 1);
			 * jsonObject.accumulate(Value, "没有绑定手机"); return
			 * jsonObject.toString(); }
			 */
			address = HtmlUtils.htmlEscape(address);

			// String ip = Utils.getIp(request);
			// int tel_limit = this.frontValidateService.getLimitCount(ip,
			// CountLimitTypeEnum.TELEPHONE);

			/*
			 * if (fuser.isFisTelephoneBind()) { if (tel_limit <= 0) {
			 * jsonObject.accumulate(ErrorCode, 1); jsonObject.accumulate(Value,
			 * "短信验证码错误次数超限，请稍后再试！"); return jsonObject.toString(); } else { if
			 * (!validateMessageCode(fuser, fuser.getFareaCode(),
			 * fuser.getFtelephone(), MessageTypeEnum.CNY_ACCOUNT_WITHDRAW,
			 * phoneCode)) { // 手機驗證錯誤
			 * this.frontValidateService.updateLimitCount(ip,
			 * CountLimitTypeEnum.TELEPHONE); jsonObject.accumulate(ErrorCode,
			 * 1); jsonObject.accumulate(Value, "手机验证码错误"); return
			 * jsonObject.toString();
			 * 
			 * } else { this.frontValidateService.deleteCountLimite(ip,
			 * CountLimitTypeEnum.TELEPHONE); } } }
			 */

			// 成功
			try {
				FbankinfoWithdraw fbankinfoWithdraw = new FbankinfoWithdraw();
				fbankinfoWithdraw.setFbankNumber(account);
				fbankinfoWithdraw.setFuser(fuser);
				fbankinfoWithdraw.setFbankType(openBankType);
				fbankinfoWithdraw.setFcreateTime(Utils.getTimestamp());
				fbankinfoWithdraw.setFname(BankTypeEnum.getEnumString(openBankType));
				fbankinfoWithdraw.setFstatus(BankInfoStatusEnum.NORMAL_VALUE);
				fbankinfoWithdraw.setFaddress(address);
				this.frontUserService.updateBankInfoWithdraw(fbankinfoWithdraw);

				jsonObject.accumulate(ErrorCode, 0);
				jsonObject.accumulate(Value, "提现银行卡信息设置成功");
				return jsonObject.toString();

			} catch (Exception e) {
				jsonObject.accumulate(ErrorCode, 1);
				jsonObject.accumulate(Value, "网络错误，请稍后再试");
				return jsonObject.toString();
			}
		} catch (Exception e) {

			return ApiConstant.getUnknownError(e);
		}

	}

	// 人民币提现
	public String WithDrawCny(HttpServletRequest request) throws Exception {

		try {
			String tradePwd = request.getParameter("tradePwd");
			double withdrawBalance = Utils.getDouble(request.getParameter("withdrawBalance"), 2);
			String phoneCode = request.getParameter("phoneCode");
			String withdrawBank = request.getParameter("withdrawBank");
			String symbol = request.getParameter("symbol");
			// double fprice =
			// Utils.getDouble(request.getParameter("fprice")==null?0:req, 2);
			String fprice1 = request.getParameter("fprice");
			if ("".equals(fprice1) || fprice1 == null) {
				fprice1 = "0";
			}
			double fprice = Utils.getDouble(fprice1, 2);
			JSONObject jsonObject = new JSONObject();
			jsonObject.accumulate(Result, true);
			double withdrawBalance1 = 0;
			// 如果是虚拟币提现则RMB=虚拟币/比例
			if (fprice != 0) {
				withdrawBalance1 = withdrawBalance;
				withdrawBalance = Utils.getDouble(withdrawBalance / fprice, 2);
			}
			// 最大提现人民币
			double max_double = Double.parseDouble(this.constantMap.getString("maxwithdrawcny"));
			double min_double = Double.parseDouble(this.constantMap.getString("minwithdrawcny"));

			if (withdrawBalance <min_double) {
				// 提现金额不能小于10
				jsonObject.accumulate(ErrorCode, 1);
				jsonObject.accumulate(Value, "最小提现金额：￥" + min_double);
				return jsonObject.toString();
			}

			if (withdrawBalance > max_double) {
				// 提现金额不能大于指定值
				jsonObject.accumulate(ErrorCode, 1);
				jsonObject.accumulate(Value, "最大提现金额￥：" + max_double);
				return jsonObject.toString();
			}

			Fuser fuser = this.realTimeData.getAppFuser(this.curLoginToken);
			Fwallet fwallet = fuser.getFwallet();
			Fvirtualwallet fvirtualwallet = null;
			if (!"".equals(symbol)) {
				fvirtualwallet = this.frontUserService.findVirtualWalletByUser(fuser.getFid(), symbol);
			}
			FbankinfoWithdraw fbankinfoWithdraw = this.frontUserService.findFbankinfoWithdraw(withdrawBank);
			if (fbankinfoWithdraw == null) {
				jsonObject.accumulate(ErrorCode, 1);
				jsonObject.accumulate(Value, "请先设置提现银行卡信息");
				return jsonObject.toString();
			}
			if ("".equals(symbol)) {
				if (fwallet.getFtotalRmb() < withdrawBalance) {
					// 资金不足
					jsonObject.accumulate(ErrorCode, 1);
					jsonObject.accumulate(Value, "余额不足");
					return jsonObject.toString();
				}
			} else {
				if (fvirtualwallet.getFtotal() < withdrawBalance) {
					// 资金不足
					jsonObject.accumulate("code", -1);
					jsonObject.accumulate("msg", "您的余额不足");
					return jsonObject.toString();
				}
			}
			if (fuser.getFtradePassword() == null) {
				// 没有设置交易密码
				jsonObject.accumulate(ErrorCode, 1);
				jsonObject.accumulate(Value, "交易密码错误");
				return jsonObject.toString();
			}

			if (!fuser.isFisTelephoneBind()) {
				// 没有绑定谷歌或者手机
				jsonObject.accumulate(ErrorCode, 1);
				jsonObject.accumulate(Value, "请先绑定手机");
				return jsonObject.toString();
			}

			String ip = Utils.getIp(request);
			if (fuser.getFtradePassword() != null) {
				int trade_limit = this.frontValidateService.getLimitCount(ip, CountLimitTypeEnum.TRADE_PASSWORD);
				if (trade_limit <= 0) {
					jsonObject.accumulate(ErrorCode, 1);
					jsonObject.accumulate(Value, "交易密码错误超限，请稍后再试！");
					return jsonObject.toString();
				} else {
					boolean flag = fuser.getFtradePassword().equals(Utils.getMD5_32(tradePwd));
					if (!flag) {
						this.frontValidateService.updateLimitCount(ip, CountLimitTypeEnum.TRADE_PASSWORD);
						jsonObject.accumulate(ErrorCode, 1);
						jsonObject.accumulate(Value, "交易密码错误");
						return jsonObject.toString();
					} else if (trade_limit < Constant.ErrorCountLimit) {
						this.frontValidateService.deleteCountLimite(ip, CountLimitTypeEnum.TRADE_PASSWORD);
					}
				}
			}

			if (fuser.isFisTelephoneBind()) {
				int tel_limit = this.frontValidateService.getLimitCount(ip, CountLimitTypeEnum.TELEPHONE);
				if (tel_limit <= 0) {
					jsonObject.accumulate(ErrorCode, 1);
					jsonObject.accumulate(Value, "手机验证码错误超限，请稍后再试");
					return jsonObject.toString();
				} else {
					boolean flag = validateMessageCode(fuser, fuser.getFareaCode(), fuser.getFtelephone(),
							MessageTypeEnum.CNY_TIXIAN, phoneCode);

					if (!flag) {
						this.frontValidateService.updateLimitCount(ip, CountLimitTypeEnum.TELEPHONE);
						jsonObject.accumulate(ErrorCode, 1);
						jsonObject.accumulate(Value, "手机验证码错误");
						return jsonObject.toString();
					} else if (tel_limit < Constant.ErrorCountLimit) {
						this.frontValidateService.deleteCountLimite(ip, CountLimitTypeEnum.TELEPHONE);
					}
				}
			}

			int time = this.frontAccountService.getTodayCnyWithdrawTimes(fuser);
			if (time >= Constant.CnyWithdrawTimes) {
				jsonObject.accumulate(ErrorCode, 1);
				jsonObject.accumulate(Value, "每天只能提现" + Constant.CnyWithdrawTimes + "次，您已经超限");
				return jsonObject.toString();
			}

			boolean withdraw = false;
			try {
				if (fprice == 0) {
					withdraw = this.frontAccountService.updateWithdrawCNY(withdrawBalance, fuser, fbankinfoWithdraw);
				} else {
					withdraw = this.frontAccountService.updateWithdrawCNY(symbol, fvirtualwallet, fprice,
							withdrawBalance1, fuser, fbankinfoWithdraw);
				}
			} catch (Exception e) {

			}

			if (withdraw) {
				jsonObject.accumulate(ErrorCode, 0);
				jsonObject.accumulate(Value, "提现请求成功，请耐心等待管理员处理");
				return jsonObject.toString();
			} else {
				jsonObject.accumulate(ErrorCode, 1);
				jsonObject.accumulate(Value, "网络错误，请稍后再试");
				return jsonObject.toString();
			}
		} catch (Exception e) {

			return ApiConstant.getUnknownError(e);
		}

	}

	// 获取虚拟币提现地址
	public String GetWithdrawBtcAddress(HttpServletRequest request) throws Exception {
		try {
			String coinId = request.getParameter("coinId");

			JSONObject jsonObject = new JSONObject();
			jsonObject.accumulate(Result, true);

			Fuser fuser = this.realTimeData.getAppFuser(this.curLoginToken);
			Fvirtualcointype fvirtualcointype = this.frontVirtualCoinService.findFvirtualCoinById(coinId);
			List<FvirtualaddressWithdraw> fvirtualaddressWithdraws = this.utilsService.list(0, 0,
					" where fvirtualcointype.fid='" + fvirtualcointype.getFid() + "' and fuser.fid='" + fuser.getFid()
							+ "' ",
					false, FvirtualaddressWithdraw.class);
			JSONArray jsonArray = new JSONArray();
			for (FvirtualaddressWithdraw fvirtualaddressWithdraw : fvirtualaddressWithdraws) {
				JSONObject item = new JSONObject();
				item.accumulate("id", fvirtualaddressWithdraw.getFid());
				item.accumulate("address", fvirtualaddressWithdraw.getFadderess());
				item.accumulate("remark", fvirtualaddressWithdraw.getFremark());
				jsonArray.add(item);
			}
			jsonObject.accumulate("list", jsonArray);
			return jsonObject.toString();
		} catch (Exception e) {

			return ApiConstant.getUnknownError(e);
		}
	}

	// 设置虚拟币提现地址
	public String SetWithdrawBtcAddr(HttpServletRequest request) throws Exception {

		try {
			String remark = request.getParameter("remark");
			String phoneCode = request.getParameter("phoneCode");
			String symbol = request.getParameter("symbol");
			String withdrawAddr = request.getParameter("withdrawAddr");
			String withdrawRemark = request.getParameter("withdrawRemark");
			JSONObject jsonObject = new JSONObject();
			jsonObject.accumulate(Result, true);
			Fuser fuser = this.realTimeData.getAppFuser(this.curLoginToken);
			if (!fuser.isFisTelephoneBind()) {
				jsonObject.accumulate(ErrorCode, 1);
				jsonObject.accumulate(Value, "请先绑定手机");
				return jsonObject.toString();
			}

			Fvirtualcointype fvirtualcointype = this.frontVirtualCoinService.findFvirtualCoinById(symbol);
			if (fvirtualcointype == null || fvirtualcointype.getFstatus() == VirtualCoinTypeStatusEnum.Abnormal) {
				jsonObject.accumulate(ErrorCode, 1);
				jsonObject.accumulate(Value, "该虚拟币不支持提现");
				return jsonObject.toString();
			}

			String ip = Utils.getIp(request);

			if (fuser.isFisTelephoneBind()) {
				int mobil_limit = this.frontValidateService.getLimitCount(ip, CountLimitTypeEnum.TELEPHONE);
				if (mobil_limit <= 0) {
					jsonObject.accumulate(ErrorCode, 1);
					jsonObject.accumulate(Value, "手机验证码错误超限，请稍后再试！");
					return jsonObject.toString();
				} else if (!validateMessageCode(fuser, fuser.getFareaCode(), fuser.getFtelephone(),
						MessageTypeEnum.VIRTUAL_WITHDRAW_ACCOUNT, phoneCode)) {
					this.frontValidateService.updateLimitCount(ip, CountLimitTypeEnum.TELEPHONE);
					jsonObject.accumulate(ErrorCode, 1);
					jsonObject.accumulate(Value, "短信验证码错误！");
					return jsonObject.toString();
				} else if (mobil_limit < Constant.ErrorCountLimit) {
					this.frontValidateService.deleteCountLimite(ip, CountLimitTypeEnum.TELEPHONE);
				}
			}

			FvirtualaddressWithdraw fvirtualaddressWithdraw = new FvirtualaddressWithdraw();
			fvirtualaddressWithdraw.setFadderess(withdrawAddr);
			fvirtualaddressWithdraw.setFcreateTime(Utils.getTimestamp());
			fvirtualaddressWithdraw.setFremark(withdrawRemark);
			fvirtualaddressWithdraw.setFuser(fuser);
			fvirtualaddressWithdraw.setFvirtualcointype(fvirtualcointype);
			fvirtualaddressWithdraw.setFremark(remark);
			try {
				this.frontVirtualCoinService.updateFvirtualaddressWithdraw(fvirtualaddressWithdraw);
				jsonObject.accumulate(ErrorCode, 0);
				jsonObject.accumulate(Value, "地址设置成功");
				return jsonObject.toString();
			} catch (Exception e) {

				jsonObject.accumulate(ErrorCode, 1);
				jsonObject.accumulate(Value, "网络错误，请稍后再试");
				return jsonObject.toString();
			}
		} catch (Exception e) {

			return ApiConstant.getUnknownError(e);
		}

	}

	// 虚拟币提现
	public String WithdrawBtcSubmit(HttpServletRequest request) throws Exception {

		try {
			double withdrawAmount = Utils.getDouble(request.getParameter("withdrawAmount"), 2);
			String tradePwd = request.getParameter("tradePwd").trim();
			String phoneCode = request.getParameter("phoneCode");
			String symbol = request.getParameter("symbol");
			String withdrawAddr = request.getParameter("virtualaddres");
			JSONObject jsonObject = new JSONObject();
			jsonObject.accumulate(Result, true);
			Fuser fuser = this.realTimeData.getAppFuser(this.curLoginToken);
			Fvirtualcointype fvirtualcointype = this.frontVirtualCoinService.findFvirtualCoinById(symbol);
			if (fvirtualcointype == null || !fvirtualcointype.isFIsWithDraw()
					|| fvirtualcointype.getFstatus() == VirtualCoinTypeStatusEnum.Abnormal) {
				jsonObject.accumulate(ErrorCode, 1);
				jsonObject.accumulate(Value, "该虚拟币不支持提现");
				return jsonObject.toString();
			}
			Fvirtualwallet fvirtualwallet = this.frontUserService.findVirtualWalletByUser(fuser.getFid(),
					fvirtualcointype.getFid());
			FvirtualaddressWithdraw fvirtualaddressWithdraw = this.frontVirtualCoinService
					.findFvirtualaddressWithdraw(withdrawAddr);

			if (fvirtualaddressWithdraw == null) {
				jsonObject.accumulate(ErrorCode, 1);
				jsonObject.accumulate(Value, "请设置提现地址");
				return jsonObject.toString();
			}
			if (!fuser.isFisTelephoneBind()) {
				jsonObject.accumulate(ErrorCode, 1);
				jsonObject.accumulate(Value, "请先绑定手机");
				return jsonObject.toString();
			}

			String ip = Utils.getIp(request);
			int google_limit = this.frontValidateService.getLimitCount(ip, CountLimitTypeEnum.GOOGLE);
			int mobil_limit = this.frontValidateService.getLimitCount(ip, CountLimitTypeEnum.TELEPHONE);

			if (fuser.getFtradePassword() == null) {
				jsonObject.accumulate(ErrorCode, 1);
				jsonObject.accumulate(Value, "交易密码错误");
				return jsonObject.toString();
			} else {
				int trade_limit = this.frontValidateService.getLimitCount(ip, CountLimitTypeEnum.TRADE_PASSWORD);
				if (trade_limit <= 0) {
					jsonObject.accumulate(ErrorCode, 1);
					jsonObject.accumulate(Value, "交易密码错误次数超限，请稍后再试");
					return jsonObject.toString();
				}

				if (!fuser.getFtradePassword().equals(Utils.getMD5_32(tradePwd))) {
					this.frontValidateService.updateLimitCount(ip, CountLimitTypeEnum.TRADE_PASSWORD);
					jsonObject.accumulate(ErrorCode, 1);
					jsonObject.accumulate(Value, "交易密码错误");
					return jsonObject.toString();
				} else if (trade_limit < Constant.ErrorCountLimit) {
					this.frontValidateService.deleteCountLimite(ip, CountLimitTypeEnum.TRADE_PASSWORD);
				}
			}

			if (fuser.isFisTelephoneBind()) {
				if (mobil_limit <= 0) {
					jsonObject.accumulate(ErrorCode, 1);
					jsonObject.accumulate(Value, "短信验证码错误超限，请稍后再试");
					return jsonObject.toString();
				}

				boolean mobilValidate = validateMessageCode(fuser, fuser.getFareaCode(), fuser.getFtelephone(),
						MessageTypeEnum.VIRTUAL_TIXIAN, phoneCode);
				if (!mobilValidate) {
					this.frontValidateService.updateLimitCount(ip, CountLimitTypeEnum.TELEPHONE);
					jsonObject.accumulate(ErrorCode, 1);
					jsonObject.accumulate(Value, "短信验证码错误");
					return jsonObject.toString();
				} else if (mobil_limit < Constant.ErrorCountLimit) {
					this.frontValidateService.deleteCountLimite(ip, CountLimitTypeEnum.TELEPHONE);
				}
			}

			// 最大提现人民币
			double max_double = Double.parseDouble(this.constantMap.getString("maxwithdrawbtc"));
			double min_double = Double.parseDouble(this.constantMap.getString("minwithdrawbtc"));

			if (withdrawAmount < min_double) {
				// 提现金额不能小于100
				jsonObject.accumulate(ErrorCode, 1);
				jsonObject.accumulate(Value, "最小提现数量：" + min_double);
				return jsonObject.toString();
			}

			if (withdrawAmount > max_double) {
				// 提现金额不能大于指定值
				jsonObject.accumulate(ErrorCode, 1);
				jsonObject.accumulate(Value, "最大提现数量：" + max_double);
				return jsonObject.toString();
			}

			// 余额不足
			if (fvirtualwallet.getFtotal() < withdrawAmount) {
				jsonObject.accumulate(ErrorCode, 1);
				jsonObject.accumulate(Value, "余额不足");
				return jsonObject.toString();
			}

			String filter = "where fadderess='" + fvirtualaddressWithdraw.getFadderess() + "' and fuser.fid='"
					+ fuser.getFid() + "'";
			int cc = this.adminService.getAllCount("Fvirtualaddress", filter);
			if (cc > 0) {
				jsonObject.accumulate(ErrorCode, 1);
				jsonObject.accumulate(Value, "不允许选择您本人的地址");
				return jsonObject.toString();
			}

			int time = this.frontAccountService.getTodayVirtualCoinWithdrawTimes(fuser);
			if (time >= Constant.VirtualCoinWithdrawTimes) {
				jsonObject.accumulate(ErrorCode, 1);
				jsonObject.accumulate(Value, "每天最多提现" + Constant.VirtualCoinWithdrawTimes + "次，您已经超限");
				return jsonObject.toString();
			}

			try {
				this.frontVirtualCoinService.updateWithdrawBtc(fvirtualaddressWithdraw, fvirtualcointype,
						fvirtualwallet, withdrawAmount, fuser);
				jsonObject.accumulate(ErrorCode, 0);
				jsonObject.accumulate(Value, "提现请求成功，请耐心等待管理员处理");
				return jsonObject.toString();
			} catch (Exception e) {

				jsonObject.accumulate(ErrorCode, 1);
				jsonObject.accumulate(Value, "网络错误，请稍后再试");
				return jsonObject.toString();
			}
		} catch (Exception e) {

			return ApiConstant.getUnknownError(e);
		}

	}

	public String changePassword(HttpServletRequest request) throws Exception {

		try {
			Integer type = Integer.parseInt(request.getParameter("type"));
			String password1 = request.getParameter("password1");
			String password2 = request.getParameter("password2");
			String vcode = request.getParameter("vcode");
			Fuser fuser = this.realTimeData.getAppFuser(this.curLoginToken);

			JSONObject jsonObject = new JSONObject();
			jsonObject.accumulate(Result, true);

			if (type == 1) {
				if (super.validateMessageCode(fuser, "86", fuser.getFtelephone(), MessageTypeEnum.CHANGE_LOGINPWD,
						vcode) == false) {
					jsonObject.accumulate(ErrorCode, 1);
					jsonObject.accumulate(Value, "短信验证码错误");
					return jsonObject.toString();
				}

				// 登录密码
				if (fuser.getFloginPassword().equals(Utils.getMD5_32(password1))) {
					fuser.setFloginPassword(Utils.getMD5_32(password2));
					this.frontUserService.updateFuser(fuser);

					jsonObject.accumulate(ErrorCode, 0);
					jsonObject.accumulate(Value, "登录密码修改成功");
					return jsonObject.toString();
				} else {
					jsonObject.accumulate(ErrorCode, 1);
					jsonObject.accumulate(Value, "原始登录密码错误");
					return jsonObject.toString();
				}
			} else {
				if (super.validateMessageCode(fuser, "86", fuser.getFtelephone(), MessageTypeEnum.CHANGE_TRADEPWD,
						vcode) == false) {
					jsonObject.accumulate(ErrorCode, 1);
					jsonObject.accumulate(Value, "短信验证码错误");
					return jsonObject.toString();
				}

				// 交易
				if (fuser.getFtradePassword() == null || Utils.getMD5_32(password1).equals(fuser.getFtradePassword())) {
					fuser.setFtradePassword(Utils.getMD5_32(password2));
					this.frontUserService.updateFuser(fuser);

					jsonObject.accumulate(ErrorCode, 0);
					jsonObject.accumulate(Value, "交易密码修改成功");
					return jsonObject.toString();
				} else {
					jsonObject.accumulate(ErrorCode, 1);
					jsonObject.accumulate(Value, "原始交易密码错误");
					return jsonObject.toString();
				}
			}
		} catch (Exception e) {

			return ApiConstant.getUnknownError(e);
		}
	}

	// 充值提现记录,type:1人民币充值，2人民币提现，3虚拟币充值，4虚拟币提现
	public String GetAllRecords(HttpServletRequest request) throws Exception {
		try {
			Integer type = Integer.parseInt(request.getParameter("type"));
			Integer currentPage = Integer.parseInt(request.getParameter("currentPage"));
			String symbol = request.getParameter("symbol");
			Fuser fuser = this.realTimeData.getAppFuser(this.curLoginToken);
			JSONObject jsonObject = new JSONObject();
			jsonObject.accumulate(Result, true);

			try {
				if (type == 1) {// 人民币充值
					StringBuffer filter = new StringBuffer();
					filter.append("where fuser.fid='" + fuser.getFid() + "' \n");
					filter.append("and ftype =" + CapitalOperationTypeEnum.RMB_IN + "\n");
					if ("".equals(symbol)) {
						filter.append(" and fviType is null  order by fcreateTime desc \n");
					}else{
						filter.append(" and fviType = '"+symbol+"'  order by fcreateTime desc \n");
					}
					List<Fcapitaloperation> list = this.capitaloperationService.list(
							(currentPage - 1) * Constant.AppRecordPerPage, Constant.RecordPerPage, filter.toString(),
							true);
					int totalCount = this.adminService.getAllCount("Fcapitaloperation", filter.toString());
					int totalPage = totalCount / Constant.RecordPerPage
							+ ((totalCount % Constant.RecordPerPage) == 0 ? 0 : 1);
					jsonObject.accumulate("currentPage", currentPage);
					jsonObject.accumulate("totalPage", totalPage);
					JSONArray jsonArray = new JSONArray();
					for (int i = 0; i < list.size(); i++) {
						Fcapitaloperation fcapitaloperation = list.get(i);
						JSONObject item = new JSONObject();
						if (null != fcapitaloperation.getFviType()) {
							item.accumulate("fsymbolBTC",
									fcapitaloperation.getFviType().getfSymbol() + new DecimalFormat("#").format(fcapitaloperation.getFtotalBTC()));
						}
						item.accumulate("bank", fcapitaloperation.getFremittanceType());
						item.accumulate("amount", new DecimalFormat("#").format(fcapitaloperation.getFamount()));
						item.accumulate("date", Utils.dateFormat(fcapitaloperation.getFcreateTime()));
						item.accumulate("status", fcapitaloperation.getFstatus_s());
						jsonArray.add(item);
					}

					jsonObject.accumulate("list", jsonArray);
				} else if (type == 2) {// 人民币提现

					StringBuffer filter = new StringBuffer();
					filter.append("where fuser.fid='" + fuser.getFid() + "' \n");
					filter.append("and ftype =" + CapitalOperationTypeEnum.RMB_OUT + "\n");
					filter.append(" order by fcreateTime desc \n");
					List<Fcapitaloperation> list = this.capitaloperationService.list(
							(currentPage - 1) * Constant.AppRecordPerPage, Constant.RecordPerPage, filter.toString(),
							true);
					int totalCount = this.adminService.getAllCount("Fcapitaloperation", filter.toString());
					int totalPage = totalCount / Constant.RecordPerPage
							+ ((totalCount % Constant.RecordPerPage) == 0 ? 0 : 1);

					jsonObject.accumulate("currentPage", currentPage);
					jsonObject.accumulate("totalPage", totalPage);

					JSONArray jsonArray = new JSONArray();

					for (int i = 0; i < list.size(); i++) {
						Fcapitaloperation fcapitaloperation = list.get(i);
						JSONObject item = new JSONObject();
						item.accumulate("bank", fcapitaloperation.getfBank());
						item.accumulate("amount", fcapitaloperation.getFamount());
						item.accumulate("date", Utils.dateFormat(fcapitaloperation.getFcreateTime()));
						item.accumulate("status", fcapitaloperation.getFstatus_s());
						jsonArray.add(item);
					}

					jsonObject.accumulate("list", jsonArray);

				} else if (type == 3) {
					// 虚拟币充值
					Fvirtualcointype fvirtualcointype = this.frontVirtualCoinService.findFvirtualCoinById(symbol);
					StringBuffer filter = new StringBuffer();
					filter.append("where fuser.fid='" + fuser.getFid() + "' \n");
					filter.append("and fvirtualcointype.fid='" + symbol + "' \n");
					filter.append("and ftype =" + VirtualCapitalOperationTypeEnum.COIN_IN + "\n");
					filter.append(" order by fcreateTime desc \n");

					List<Fvirtualcaptualoperation> list = this.frontVirtualCoinService.findFvirtualcaptualoperation(
							(currentPage - 1) * Constant.AppRecordPerPage, Constant.AppRecordPerPage, filter.toString(),
							true);
					int totalCount = this.adminService.getAllCount("Fvirtualcaptualoperation", filter.toString());
					int totalPage = totalCount / Constant.RecordPerPage
							+ ((totalCount % Constant.RecordPerPage) == 0 ? 0 : 1);

					jsonObject.accumulate("currentPage", currentPage);
					jsonObject.accumulate("totalPage", totalPage);

					JSONArray jsonArray = new JSONArray();

					for (int i = 0; i < list.size(); i++) {
						Fvirtualcaptualoperation fvirtualcaptualoperation = list.get(i);
						JSONObject item = new JSONObject();
						item.accumulate("bank", fvirtualcaptualoperation.getRecharge_virtual_address());
						item.accumulate("amount", fvirtualcaptualoperation.getFamount());
						item.accumulate("date", Utils.dateFormat(fvirtualcaptualoperation.getFcreateTime()));
						item.accumulate("status", fvirtualcaptualoperation.getFstatus_s());
						jsonArray.add(item);
					}

					jsonObject.accumulate("list", jsonArray);
				} else if (type == 4) {

					// 虚拟币提现
					Fvirtualcointype fvirtualcointype = this.frontVirtualCoinService.findFvirtualCoinById(symbol);
					StringBuffer filter = new StringBuffer();
					filter.append("where fuser.fid='" + fuser.getFid() + "' \n");
					filter.append("and fvirtualcointype.fid='" + symbol + "' \n");
					filter.append("and ftype =" + VirtualCapitalOperationTypeEnum.COIN_OUT + "\n");
					filter.append(" order by fcreateTime desc \n");

					List<Fvirtualcaptualoperation> list = this.frontVirtualCoinService.findFvirtualcaptualoperation(
							(currentPage - 1) * Constant.AppRecordPerPage, Constant.AppRecordPerPage,
							" where ftype=" + VirtualCapitalOperationTypeEnum.COIN_OUT + " and fvirtualcointype.fid='"
									+ symbol + "' and fuser.fid='" + fuser.getFid() + "' order by fcreateTime desc",
							true);
					int totalCount = this.adminService.getAllCount("Fvirtualcaptualoperation", filter.toString());
					int totalPage = totalCount / Constant.RecordPerPage
							+ ((totalCount % Constant.RecordPerPage) == 0 ? 0 : 1);

					jsonObject.accumulate("currentPage", currentPage);
					jsonObject.accumulate("totalPage", totalPage);

					JSONArray jsonArray = new JSONArray();

					for (int i = 0; i < list.size(); i++) {
						Fvirtualcaptualoperation fvirtualcaptualoperation = list.get(i);
						JSONObject item = new JSONObject();
						item.accumulate("bank", fvirtualcaptualoperation.getWithdraw_virtual_address());
						item.accumulate("amount", fvirtualcaptualoperation.getFamount());
						item.accumulate("date", Utils.dateFormat(fvirtualcaptualoperation.getFcreateTime()));
						item.accumulate("status", fvirtualcaptualoperation.getFstatus_s());
						jsonArray.add(item);
					}

					jsonObject.accumulate("list", jsonArray);

				} else if (type == 5) {
					String param = "where fuser.fid='" + fuser.getFid() + "' and ftype=4 and fviType.fid='" + symbol
							+ "' order by fCreateTime desc";
					List<Fcapitaloperation> fcapitaloperations = this.frontAccountService.findCapitalList(
							(currentPage - 1) * Constant.AppRecordPerPage, Constant.AppRecordPerPage, param, true);
					JSONArray jsonArray = new JSONArray();
					for (Fcapitaloperation fcapitaloperation : fcapitaloperations) {
						JSONObject item = new JSONObject();
						item.accumulate("fid", fcapitaloperation.getFid());
						item.accumulate("ftime", fcapitaloperation.getfLastUpdateTime());
						item.accumulate("fsymbol",
								fcapitaloperation.getFviType().getfSymbol() + fcapitaloperation.getFtotalBTC());
						item.accumulate("fbank", fcapitaloperation.getfBank());
						item.accumulate("famount", fcapitaloperation.getFamount());
						item.accumulate("ffee", fcapitaloperation.getFfees());
						item.accumulate("fstatus", fcapitaloperation.getFstatus_s());
						jsonArray.add(item);
					}

					int totalCount = this.adminService.getAllCount("Fcapitaloperation", param);
					int totalPage = totalCount / Constant.RecordPerPage
							+ ((totalCount % Constant.RecordPerPage) == 0 ? 0 : 1);

					jsonObject.accumulate("currentPage", currentPage);
					jsonObject.accumulate("totalPage", totalPage);
					jsonObject.accumulate("list", jsonArray);
				}
			} catch (Exception e) {

			}

			return jsonObject.toString();
		} catch (Exception e) {

			return ApiConstant.getUnknownError(e);
		}

	}

	// 充值银行列表
	public String RechargeBanks(HttpServletRequest request) throws Exception {
		try {
			JSONObject jsonObject = new JSONObject();
			jsonObject.accumulate(Result, true);

			JSONArray arr = new JSONArray();
			List<Systembankinfo> list = this.utilsService.list(0, 0, " where fstatus=1 ", false, Systembankinfo.class);
			for (Systembankinfo info : list) {
				JSONObject item = new JSONObject();
				item.accumulate("id", info.getFid());
				item.accumulate("bankAddress", info.getFbankAddress());
				item.accumulate("bankName", info.getFbankName());
				item.accumulate("bankNumber", info.getFbankNumber());
				item.accumulate("ownerName", info.getFownerName());
				arr.add(item);
			}
			jsonObject.accumulate("list", arr);
			return jsonObject.toString();
		} catch (Exception e) {
			return ApiConstant.getUnknownError(e);
		}
	}

	// 银行卡充值修改状态
	public String rechargeCnySubmit(HttpServletRequest request) throws Exception {
		String desc = request.getParameter("desc");
		JSONObject jsonObject = new JSONObject();

		Fcapitaloperation fcapitaloperation = this.frontAccountService.findCapitalOperationById(desc);
		String fid = fcapitaloperation.getFuser().getFid();
		// String fid2 = GetSession(request).getFid();
		if (fcapitaloperation == null) {
			jsonObject.accumulate("code", -1);
			jsonObject.accumulate("msg", "非法操作");
			return jsonObject.toString();
		}

		if (fcapitaloperation.getFstatus() != CapitalOperationInStatus.NoGiven
				|| fcapitaloperation.getFtype() != CapitalOperationTypeEnum.RMB_IN) {
			jsonObject.accumulate("code", -1);
			jsonObject.accumulate("msg", "网络异常");
			return jsonObject.toString();
		}

		fcapitaloperation.setFstatus(CapitalOperationInStatus.WaitForComing);
		fcapitaloperation.setFischarge(false);
		fcapitaloperation.setfLastUpdateTime(Utils.getTimestamp());
		try {
			this.frontAccountService.updateCapitalOperation(fcapitaloperation);
			jsonObject.accumulate("code", 0);
			jsonObject.accumulate("msg", "操作成功");
		} catch (Exception e) {
			jsonObject.accumulate("code", -1);
			jsonObject.accumulate("msg", "网络异常");
		}

		return jsonObject.toString();
	}

	// 银行充值
	public String RechargeCny(HttpServletRequest request) throws Exception {
		try {
			JSONObject jsonObject = new JSONObject();
			jsonObject.accumulate(Result, true);
			String fid = request.getParameter("fviId");
			String bankid = request.getParameter("bankid");
			double money = Utils.getDouble(request.getParameter("money"), 2);
			Fuser fuser = this.realTimeData.getAppFuser(this.curLoginToken);

			double minRecharge = Double.parseDouble(this.constantMap.get("minrechargecny").toString());

			if (money < minRecharge) {
				// 非法
				jsonObject.accumulate(ErrorCode, -1);
				jsonObject.accumulate(Value, "最小充值金额为￥" + minRecharge);
				return jsonObject.toString();
			}

			Systembankinfo systembankinfo = this.frontBankInfoService.findSystembankinfoById(bankid);
			if (systembankinfo == null || systembankinfo.getFstatus() == SystemBankInfoEnum.ABNORMAL) {
				// 银行账号停用
				jsonObject.accumulate(ErrorCode, -1);
				jsonObject.accumulate(Value, "银行帐户不存在");
				return jsonObject.toString();
			}
			Timestamp timestamp = Utils.getTimestamp();
			Fcapitaloperation fcapitaloperation = new Fcapitaloperation();
			Fvirtualcointype f = new Fvirtualcointype();
			if (!"".equals(fid)) {
				f.setFid(fid);
				fcapitaloperation.setFviType(f);// 虚拟币类型ID
				Fsubscription fsubscription = subscriptionService.findByFviId(fid);// 人民币兑换
																					// fisRM是1
				fcapitaloperation.setFtotalBTC(money * fsubscription.getFprice());// 充值金额*兑换比例
			}
			fcapitaloperation.setFamount(money);
			fcapitaloperation.setSystembankinfo(systembankinfo);
			fcapitaloperation.setFcreateTime(timestamp);
			fcapitaloperation.setFtype(CapitalOperationTypeEnum.RMB_IN);
			fcapitaloperation.setFuser(fuser);
			fcapitaloperation.setFstatus(CapitalOperationInStatus.NoGiven);
			fcapitaloperation.setFremittanceType(RemittanceTypeEnum.getEnumString(RemittanceTypeEnum.Type1));
			this.frontAccountService.addFcapitaloperation(fcapitaloperation);
			jsonObject.accumulate(ErrorCode, 0);
			jsonObject.accumulate(Value, String.valueOf(fcapitaloperation.getFamount()));
			jsonObject.accumulate("tradeId", fcapitaloperation.getFid());
			jsonObject.accumulate("fbankName", systembankinfo.getFbankName());
			jsonObject.accumulate("fownerName", systembankinfo.getFownerName());
			jsonObject.accumulate("fbankAddress", systembankinfo.getFbankAddress());
			jsonObject.accumulate("fbankNumber", systembankinfo.getFbankNumber());
			jsonObject.accumulate("time", Utils.dateFormat(timestamp));
			return jsonObject.toString();
		} catch (Exception e) {
			return ApiConstant.getUnknownError(e);
		}
	}

	public String GetKline(HttpServletRequest request) throws Exception {
		JSONObject js = new JSONObject();
		try {
			// 获取虚拟币类型id
			String symbol = request.getParameter("symbol");
			// 根据虚拟币id获取虚拟币信息
			Fvirtualcointype fvirtualcointype = this.frontVirtualCoinService.findFvirtualCoinById(symbol);
			// 根据虚拟币id获取市场深度
			JSONArray ret = new JSONArray();
			JSONArray buy = new JSONArray();
			JSONArray sell = new JSONArray();

			double val = this.realTimeData.getLatestDealPrize(symbol);
			double low = val * 0.3D;
			double high = val * 3D;

			Object[] buyArray = this.realTimeData.getBuyDepthMap(symbol).toArray();
			for (Object object : buyArray) {
				Fentrust fentrust = (Fentrust) object;

				if (fentrust.getFprize() < low || fentrust.getFprize() > high) {
					continue;
				}

				JSONArray item = new JSONArray();
				item.add(fentrust.getFprize());
				item.add(fentrust.getFleftCount());
				item.add(0);

				buy.add(item);
			}

			Object[] sellArray = this.realTimeData.getSellDepthMap(symbol).toArray();
			for (Object object : sellArray) {
				Fentrust fentrust = (Fentrust) object;

				if (fentrust.getFprize() < low || fentrust.getFprize() > high) {
					continue;
				}

				JSONArray item = new JSONArray();
				item.add(fentrust.getFprize());
				item.add(fentrust.getFleftCount());
				item.add(0);

				sell.add(item);
			}

			ret.add(buy);
			ret.add(sell);

			js.accumulate(ErrorCode, 0);
			js.accumulate("fvirtualcointype", fvirtualcointype);
			js.accumulate("ret", ret);
		} catch (Exception e) {
			js.accumulate(ErrorCode, -1).toString();
		}
		return js.toString();
	}

	public String IsShiel(HttpServletRequest request) throws Exception {
		try {
			JSONObject js = new JSONObject();
			String isshiel = request.getParameter("isshiel");
			String isShiel = constantMap.getString("isshiel");
			isShiel = isShiel == null ? "无此版本" : isShiel;
			if (isshiel.equals(isShiel)) {
				js.accumulate(ErrorCode, 0);
				return js.toString();
			}
			js.accumulate(ErrorCode, -1);
			return js.toString();
		} catch (Exception e) {
			return ApiConstant.getUnknownError(e);
		}
	}

	// 众筹列表 type 0未开始 1进行中 2已结束
	public String GetZhongChouList(HttpServletRequest request) throws Exception {
		Timestamp time = Utils.getTimestamp();
		JSONObject js = new JSONObject();
		int pageSize = 10;
		String status = "";
		try {
			if (Comm.getISHIDDEN_CROWDFUNDING()) {
				js.accumulate(Result, false);
				js.accumulate(ErrorCode, -1);
				js.accumulate(Value, "禁止众筹");
				return js.toString();
			}
			int type = Integer.valueOf(request.getParameter("type"));
			int currentPage = Integer.valueOf(request.getParameter("currentPage"));
			String filter = "where ftype=" + SubscriptionTypeEnum.RMB;
			if (type == 0) {
				status = "未开始";
				filter += " and FbeginTime > '" + time.toString() + "'";
			} else if (type == 1) {
				status = "众筹中";
				filter += " and FbeginTime < '" + time.toString() + "' and FendTime > '" + time.toString() + "'";
			} else {
				status = "已结束";
				filter += " and FendTime < '" + time.toString() + "'";
			}
			filter += "  order by fcreateTime desc";
			String countSize = "select count(*) from Fsubscription " + filter;
			int totalCount = this.subscriptionService.selectCount(countSize);
			int totalPageNum = (totalCount + pageSize - 1) / pageSize;
			List<Fsubscription> fsubscriptions = this.subscriptionService
					.list((Integer.valueOf(currentPage) - 1) * pageSize, pageSize, filter, true);
			if (fsubscriptions == null || fsubscriptions.size() == 0) {
				js.accumulate(Result, true);
				js.accumulate(ErrorCode, 0);
				js.accumulate(Value, "未发起众筹，请等待");
				return js.toString();
			}
			JSONArray json = new JSONArray();
			for (Fsubscription fsubscription : fsubscriptions) {
				fsubscription.setFstatus(status);
				JSONObject jsonObject = new JSONObject();
				jsonObject.accumulate("fbuyCount", fsubscription.getFbuyCount());
				jsonObject.accumulate("fminbuyCount", fsubscription.getFminbuyCount());
				jsonObject.accumulate("falreadybyCount", fsubscription.getfAlreadyByCount());
				jsonObject.accumulate("fid", fsubscription.getFid());
				jsonObject.accumulate("fprice", fsubscription.getFprice());
				jsonObject.accumulate("fqty", fsubscription.getFqty());
				jsonObject.accumulate("fstatus", fsubscription.getFstatus());
				jsonObject.accumulate("ftitle", fsubscription.getFtitle());
				jsonObject.accumulate("ftotal", fsubscription.getFtotal());
				jsonObject.accumulate("ftotalqty", fsubscription.getFtotalqty());
				jsonObject.accumulate("fshortName", fsubscription.getFvirtualcointype().getfShortName());
				jsonObject.accumulate("furl", fsubscription.getFvirtualcointype().getFurl());
				jsonObject.accumulate("symbol", fsubscription.getSymbol());
				jsonObject.accumulate("symbol1", fsubscription.getSymbol1());
				jsonObject.accumulate("fbuyTimes", fsubscription.getFbuyTimes());
				jsonObject.accumulate("fisICO", fsubscription.isFisICO());
				jsonObject.accumulate("zcsum", fsubscription.getFtotalqty() * fsubscription.getFtotal());// 众筹总分数
				jsonObject.accumulate("iszhongchou", fsubscription.getFprice() * fsubscription.getfAlreadyByCount());// 已参与
				json.add(jsonObject);
			}
			js.accumulate("fsubscriptions", json);
			js.accumulate("totalPageNum", totalPageNum);
			js.accumulate("totalCount", totalCount);
			js.accumulate(Result, true);
			js.accumulate(ErrorCode, 0);
			js.accumulate(Value, "查询众筹列表成功");
			return js.toString();
		} catch (Exception e) {
			return ApiConstant.getUnknownError(e);
		}
	}

	// 众筹详情信息
	public String GetZhongChouResource(HttpServletRequest request) throws Exception {
		JSONObject js = new JSONObject();
		try {
			if (Comm.getISHIDDEN_CROWDFUNDING()) {
				js.accumulate(Result, false);
				js.accumulate(ErrorCode, -1);
				js.accumulate(Value, "禁止众筹");
				return js.toString();
			}
			String fid = String.valueOf(request.getParameter("fid"));
			Fsubscription fsubscription = this.frontTradeService.findFsubscriptionById(fid);
			if (fsubscription == null || fsubscription.getFtype() != SubscriptionTypeEnum.RMB) {
				js.accumulate(Result, true);
				js.accumulate(ErrorCode, 0);
				js.accumulate(Value, "");
				return js.toString();
			}
			String status = "";
			long now = Utils.getTimestamp().getTime();
			if (fsubscription.getFbeginTime().getTime() > now) {
				status = "未开始";
			}
			if (fsubscription.getFbeginTime().getTime() < now && fsubscription.getFendTime().getTime() > now) {
				status = "众筹中";
			}
			if (fsubscription.getFendTime().getTime() < now) {
				status = "已结束";
			}
			fsubscription.setFstatus(status);
			long s = fsubscription.getFbeginTime().getTime();
			long e = fsubscription.getFendTime().getTime();
			long start = fsubscription.getFbeginTime().getTime() - now;
			long end = fsubscription.getFendTime().getTime() - now;
			js.accumulate("nowtime", now / 1000L);// 服务器现在时间
			js.accumulate("start", s / 1000L);// 众筹开始时间戳
			js.accumulate("end", e / 1000L);// 众筹结束时间戳
			js.accumulate("s", start / 1000L);// 开始时间减去当前时间的时间戳
			js.accumulate("e", end / 1000L);// 结束时间减去当前时间的时间戳
			double totalAmt = 0d;
			String url = null;
			Fuser fuser = this.realTimeData.getAppFuser(this.curLoginToken);
			// Fuser fuser =
			// this.userSerivce.findById(GetSession(request).getFid());
			if (fsubscription.getFvirtualcointypeCost() == null) {
				url = "/account/rechargeCny.html";
				totalAmt = fuser.getFwallet().getFtotalRmb();
			} else {
				url = "/account/rechargeBtc.html?symbol=" + fsubscription.getFvirtualcointypeCost().getFid();
				Fvirtualwallet fvirtualwallet = this.frontUserService.findVirtualWalletByUser(fuser.getFid(),
						fsubscription.getFvirtualcointypeCost().getFid());
				totalAmt = fvirtualwallet.getFtotal();
			}
			js.accumulate("rechargeUrl", url);// 去充值url
			js.accumulate("totalAmt", new java.text.DecimalFormat("#.00").format(totalAmt));// 您目前可用CNY
			js.accumulate("fbuyCount", fsubscription.getFbuyCount());// 等于0时显示
																		// '不限制'
																		// 否则显示值
			js.accumulate("falreadybyCount", fsubscription.getfAlreadyByCount());// 已众筹份数
			js.accumulate("fid", fsubscription.getFid());// ID
			js.accumulate("fprice", fsubscription.getFprice());// 单价/份
			js.accumulate("fstatus", fsubscription.getFstatus());// 众筹状态
			js.accumulate("ftitle", fsubscription.getFtitle());// 众筹币种类型
			js.accumulate("ftotal", fsubscription.getFtotal());// 众筹总份数
			js.accumulate("fshortName", fsubscription.getFvirtualcointype().getfShortName());// 众筹币种小名
			js.accumulate("furl", fsubscription.getFvirtualcointype().getFurl());// 图片路径
			js.accumulate("symbol", fsubscription.getSymbol());// CNY
			js.accumulate("symbol1", fsubscription.getSymbol1());// ￥
			js.accumulate("fisICO", fsubscription.isFisICO());// true(代表金额) 或
																// false(代表份数)
			js.accumulate("fqty", fsubscription.getFqty());// 中签份数
			js.accumulate("fcontent", HTMLSpirit.delHTMLTag(fsubscription.getFcontent()));// 众筹规则
			js.accumulate("isSupport", fsubscription.getFprice() * fsubscription.getfAlreadyByCount());// 已支持
			js.accumulate("zcsum", fsubscription.getFtotal() * fsubscription.getFtotalqty());// 众筹总量
			js.accumulate("ftotalqty", fsubscription.getFtotalqty());
			js.accumulate(Result, true);
			js.accumulate(ErrorCode, 0);
			js.accumulate(Value, "查询众筹详情成功");
			return js.toString();
		} catch (Exception e) {
			return ApiConstant.getUnknownError(e);
		}
	}

	// 立即众筹接口
	public String nowCrowd(HttpServletRequest request) throws Exception {
		JSONObject jsonObject = new JSONObject();
		try {
			if (Comm.getISHIDDEN_CROWDFUNDING()) {
				jsonObject.accumulate(Result, false);
				jsonObject.accumulate(ErrorCode, -1);
				jsonObject.accumulate(Value, "禁止众筹");
				return jsonObject.toString();
			}
			String fid = String.valueOf(request.getParameter("fid"));// id
			int buyAmount = Integer.valueOf(request.getParameter("buyAmount"));// 要参与的份数
			String pwd = String.valueOf(request.getParameter("pwd"));// 密码
			Fuser fuser = this.realTimeData.getAppFuser(this.curLoginToken);// 登录凭证
			// Fuser fuser =
			// this.frontUserService.findById(GetSession(request).getFid()) ;
			Fsubscription fsubscription = this.frontTradeService.findFsubscriptionById(fid);
			if (fsubscription == null || fsubscription.getFtype() != SubscriptionTypeEnum.RMB || buyAmount <= 0) {
				jsonObject.accumulate(Result, true);
				jsonObject.accumulate(ErrorCode, -1);
				jsonObject.accumulate(Value, "操作不合法");
				return jsonObject.toString();
			}
			String type = "份";
			String type1 = "份数";
			if (fsubscription.isFisICO()) {
				type = fsubscription.getSymbol();
				type1 = "金额";
			}

			if (fuser.getFtradePassword() == null || fuser.getFtradePassword().trim().length() == 0) {
				jsonObject.accumulate(Result, true);
				jsonObject.accumulate(ErrorCode, -1);
				jsonObject.accumulate(Value, "请先设置交易密码");
				return jsonObject.toString();
			}

			if (!fuser.getFtradePassword().equals(Utils.MD5(pwd))) {
				jsonObject.accumulate(Result, true);
				jsonObject.accumulate(ErrorCode, -1);
				jsonObject.accumulate(Value, "交易密码不正确");
				return jsonObject.toString();
			}

			if (buyAmount < fsubscription.getFminbuyCount()) {
				jsonObject.accumulate(Result, true);
				jsonObject.accumulate(ErrorCode, -1);
				jsonObject.accumulate(Value, "最少众筹" + fsubscription.getFminbuyCount() + type);
				return jsonObject.toString();
			}

			int begin = 0;
			long now = Utils.getTimestamp().getTime();
			if (fsubscription.getFbeginTime().getTime() > now) {
				// 没开始
				begin = 0;
			}

			if (fsubscription.getFbeginTime().getTime() < now && fsubscription.getFendTime().getTime() > now) {
				// 进行中
				begin = 1;
			}

			if (fsubscription.getFendTime().getTime() < now) {
				// 结束
				begin = 2;
			}

			if (begin == 0) {
				jsonObject.accumulate(Result, true);
				jsonObject.accumulate(ErrorCode, -1);
				jsonObject.accumulate(Value, "众筹未开始");
				return jsonObject.toString();
			} else if (begin == 2) {
				jsonObject.accumulate(Result, true);
				jsonObject.accumulate(ErrorCode, -1);
				jsonObject.accumulate(Value, "众筹已结束");
				return jsonObject.toString();
			}

			if (!fsubscription.getFisopen()) {
				jsonObject.accumulate(Result, true);
				jsonObject.accumulate(ErrorCode, -1);
				jsonObject.accumulate(Value, "众筹未对外开放");
				return jsonObject.toString();
			}

			// 认购记录
			List<Fsubscriptionlog> fsubscriptionlogs = this.frontTradeService.findFsubScriptionLog(fuser,
					fsubscription.getFid());
			// 可购买数量
			int buyCount = fsubscription.getFbuyCount();
			if (fsubscriptionlogs.size() > 0) {
				for (int i = 0; i < fsubscriptionlogs.size(); i++) {
					buyCount -= fsubscriptionlogs.get(i).getFcount();
				}
			}

			buyCount = buyCount < 0 ? 0 : buyCount;
			// 可购买次数
			int buyTimes = fsubscription.getFbuyTimes() - fsubscriptionlogs.size();
			buyTimes = buyTimes < 0 ? 0 : buyTimes;

			String subUserId = this.map.getString("subUserId");
			if (fuser.getFid() != subUserId) {
				if (fsubscription.getFbuyCount() != 0 && buyCount < buyAmount) {
					jsonObject.accumulate(Result, true);
					jsonObject.accumulate(ErrorCode, -1);
					jsonObject.accumulate(Value, "您已超出可众筹的" + type1);
					return jsonObject.toString();
				}

				if (fsubscription.getFbuyTimes() != 0 && buyTimes == 0) {
					jsonObject.accumulate(Result, true);
					jsonObject.accumulate(ErrorCode, -1);
					jsonObject.accumulate(Value, "您已超出可众筹的次数");
					return jsonObject.toString();
				}
			}

			Double cost = 0d;
			if (fsubscription.isFisICO()) {
				cost = Double.valueOf(buyAmount);
			} else {
				cost = buyAmount * fsubscription.getFprice();
			}

			// 可以购买了
			Fwallet fwallet1 = null;// this.frontUserService.findFwalletById(fuser.getFwallet().getFid())
									// ;
			Fvirtualwallet fvirtualwallet1 = null;
			Fvirtualwallet fvirtualwallet = this.frontUserService.findVirtualWalletByUser(fuser.getFid(),
					fsubscription.getFvirtualcointype().getFid());
			if (fsubscription.getFvirtualcointypeCost() == null) {
				fwallet1 = this.frontUserService.findFwalletById(fuser.getFwallet().getFid());
				if (fwallet1.getFtotalRmb() < cost) {
					jsonObject.accumulate(Result, true);
					jsonObject.accumulate(ErrorCode, -1);
					jsonObject.accumulate(Value, "人民币余额不足");
					return jsonObject.toString();
				}
				fwallet1.setFtotalRmb(fwallet1.getFtotalRmb() - cost);
				fwallet1.setFfrozenRmb(fwallet1.getFfrozenRmb() + cost);
			} else {
				fvirtualwallet1 = this.frontUserService.findVirtualWalletByUser(fuser.getFid(),
						fsubscription.getFvirtualcointypeCost().getFid());
				if (fvirtualwallet1.getFtotal() < cost) {
					jsonObject.accumulate(Result, true);
					jsonObject.accumulate(ErrorCode, -1);
					jsonObject.accumulate(Value, "您的" + fsubscription.getFvirtualcointypeCost().getFname() + "余额不足");
					return jsonObject.toString();
				}
				fvirtualwallet1.setFtotal(fvirtualwallet1.getFtotal() - cost);
				fvirtualwallet1.setFfrozen(fvirtualwallet1.getFfrozen() + cost);
			}

			Fsubscriptionlog fsubscriptionlog = new Fsubscriptionlog();
			fsubscriptionlog.setFcount(buyAmount + 0.0);
			fsubscriptionlog.setFcreatetime(Utils.getTimestamp());
			fsubscriptionlog.setFprice(fsubscription.getFprice());
			fsubscriptionlog.setFsubscription(fsubscription);
			fsubscriptionlog.setFtotalCost(cost);
			fsubscriptionlog.setFlastcount(0d);
			fsubscriptionlog.setFstatus(SubStatusEnum.INIT);
			fsubscriptionlog.setFuser(fuser);
			fsubscriptionlog.setFissend(false);
			fsubscriptionlog.setFischarge(null);
			fsubscriptionlog.setFoneqty(fsubscription.getFtotalqty());
			fsubscriptionlog.setFlastqty(0d);
			fsubscription.setfAlreadyByCount(fsubscription.getfAlreadyByCount() + buyAmount);
			try {
				this.frontTradeService.updateSubscription(fwallet1, fvirtualwallet1, fvirtualwallet, fsubscriptionlog,
						fsubscription);
			} catch (Exception e) {
				jsonObject.accumulate(Result, true);
				jsonObject.accumulate(ErrorCode, -1);
				jsonObject.accumulate(Value, "网络异常");
				return jsonObject.toString();
			}
			jsonObject.accumulate(Result, true);
			jsonObject.accumulate(ErrorCode, 0);
			jsonObject.accumulate(Value, "操作成功");
			return jsonObject.toString();
		} catch (Exception e) {
			return ApiConstant.getUnknownError(e);
		}
	}

	// 众筹历史记录
	public String GetZhongChouLogs(HttpServletRequest request) throws Exception {
		JSONObject json = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		int pageSize = 10;
		try {
			if (Comm.getISHIDDEN_CROWDFUNDING()) {
				json.accumulate(Result, false);
				json.accumulate(ErrorCode, -1);
				json.accumulate(Value, "禁止众筹");
				return json.toString();
			}
			int currentPage = Integer.valueOf(request.getParameter("currentPage"));
			String fid = String.valueOf(request.getParameter("fid"));
			String filter2 = "";
			if (null != fid && !"".equals(fid)) {
				filter2 += " and fsubscription.fid = '" + fid + "' ";
			}
			// Fuser fuser =
			// this.frontUserService.findById(GetSession(request).getFid()) ;
			Fuser fuser = this.realTimeData.getAppFuser(this.curLoginToken);
			String filter = "where fuser.fid='" + fuser.getFid() + "' and fsubscription.ftype !='"
					+ SubscriptionTypeEnum.COIN + "' " + filter2 + " order by fcreatetime desc";
			List<Fsubscriptionlog> subscriptionlogs = this.subscriptionLogService.list((currentPage - 1) * pageSize,
					pageSize, filter, true);
			int total = this.adminService.getAllCount("Fsubscriptionlog", filter);
			int totalPageNum = (total + pageSize - 1) / pageSize;
			for (Fsubscriptionlog fsubscriptionlog : subscriptionlogs) {
				JSONObject js = new JSONObject();
				js.accumulate("fstatus_s", fsubscriptionlog.getFstatus_s());
				js.accumulate("fcount", fsubscriptionlog.getFcount());
				js.accumulate("fid", fsubscriptionlog.getFsubscription().getFid());
				js.accumulate("fprice", fsubscriptionlog.getFprice());
				js.accumulate("ftitle", fsubscriptionlog.getFsubscription().getFtitle());
				js.accumulate("ftotalCost", fsubscriptionlog.getFtotalCost());
				js.accumulate("flastcount", fsubscriptionlog.getFlastcount());
				js.accumulate("foneqty", fsubscriptionlog.getFoneqty());
				js.accumulate("symbol", fsubscriptionlog.getFsubscription().getSymbol());
				js.accumulate("flastqty", fsubscriptionlog.getFlastqty());
				js.accumulate("fcreatetime", new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
						.format(fsubscriptionlog.getFcreatetime()));
				jsonArray.add(js);
			}
			json.accumulate("subscriptionlogs", jsonArray);
			json.accumulate("totalCount", total);
			json.accumulate("totalPageNum", totalPageNum);
			json.accumulate(Result, true);
			json.accumulate(ErrorCode, 0);
			json.accumulate(Value, "查询众筹历史记录成功");
			return json.toString();
		} catch (Exception e) {
			return ApiConstant.getUnknownError(e);
		}
	}

}
