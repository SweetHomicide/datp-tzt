package com.ruizton.main.controller;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.ditp.dao.RedisDAO;
import com.ruizton.main.Enum.MessageTypeEnum;
import com.ruizton.main.Enum.ValidateMailStatusEnum;
import com.ruizton.main.Enum.ValidateMessageStatusEnum;
import com.ruizton.main.Enum.VirtualCoinTypeStatusEnum;
import com.ruizton.main.auto.OneDayData;
import com.ruizton.main.auto.RealTimeData;
import com.ruizton.main.auto.TaskList;
import com.ruizton.main.comm.ConstantMap;
import com.ruizton.main.comm.MessageValidate;
import com.ruizton.main.comm.ValidateMap;
import com.ruizton.main.model.Emailvalidate;
import com.ruizton.main.model.Fadmin;
import com.ruizton.main.model.Flimittrade;
import com.ruizton.main.model.Fsubscription;
import com.ruizton.main.model.Fuser;
import com.ruizton.main.model.Fvalidateemail;
import com.ruizton.main.model.Fvalidatemessage;
import com.ruizton.main.model.Fvirtualcointype;
import com.ruizton.main.model.Fvirtualwallet;
import com.ruizton.main.model.Fwallet;
import com.ruizton.main.service.admin.LimittradeService;
import com.ruizton.main.service.admin.SubscriptionService;
import com.ruizton.main.service.admin.SystemArgsService;
import com.ruizton.main.service.front.FrontAccountService;
import com.ruizton.main.service.front.FrontUserService;
import com.ruizton.main.service.front.FrontValidateService;
import com.ruizton.main.service.front.FrontVirtualCoinService;
import com.ruizton.util.Comm;
import com.ruizton.util.Constant;
import com.ruizton.util.ConstantKeys;
import com.ruizton.util.Utils;

public class BaseController {
	public static final String JsonEncode = "application/json;charset=UTF-8";
	@Autowired
	private RealTimeData realTimeData;
	@Autowired
	private OneDayData oneDayData;
	@Autowired
	private ConstantMap constantMap;
	@Autowired
	private FrontValidateService frontValidateService;
	@Autowired
	private FrontUserService frontUserService;
	@Autowired
	private ValidateMap validateMap;
	@Autowired
	private FrontAccountService frontAccountService;
	@Autowired
	private FrontVirtualCoinService frontVirtualCoinService;
	@Autowired
	private TaskList taskList;
	@Autowired
	private LimittradeService limittradeService;
	@Autowired
	private SystemArgsService systemArgsService;
	@Autowired
	private RedisDAO redisDAO;
	@Autowired
	private SubscriptionService subscriptionService;

	public HttpSession getSession(HttpServletRequest request) {
		return request.getSession();
	}

	// 获取用户
	public Fuser getFuserSession(HttpServletRequest request) {
		String sessionid = getCookie(request, "sessionId");
		if (!sessionid.equals("")) {
			Fuser Fuser = redisDAO.getFuser(sessionid);
			return Fuser;
		} else {
			return null;
		}
	}

	public void setAdminSession(HttpServletRequest request, Fadmin fadmin) {
		getSession(request).setAttribute("login_admin", fadmin);
		this.CleanSession(request);
	}

	public void removeAdminSession(HttpServletRequest request) {
		getSession(request).removeAttribute("login_admin");
	}

	// 获得管理员session
	public Fadmin getAdminSession(HttpServletRequest request) {
		Object session = getSession(request).getAttribute("login_admin");
		Fadmin fadmin = null;
		if (session != null) {
			fadmin = (Fadmin) session;
		}
		return fadmin;
	}

	// 获得session中的用户
	public Fuser GetSession(HttpServletRequest request) {
		Fuser fuser = null;
		if (!Comm.getISREDIS()) {
			HttpSession session = getSession(request);
			Object session_user = session.getAttribute("login_user");
			if (session_user != null) {
				fuser = (Fuser) session_user;
			}
		} else {
			fuser = getFuserSession(request);
		}

		return fuser;
	}

	public void updateKey(HttpServletRequest request) {
		if (Comm.getISREDIS()) {
			String sessionid = getCookie(request, "sessionId");
			redisDAO.updateKeyTime(sessionid);
		}
	}

	// //获得session中的用户 redis
	// public Fuser GetSession(HttpServletRequest request){
	// Fuser fuser = null ;
	// fuser=getFuserSession(request);
	// return fuser ;
	// }
	public Fuser GetSecondLoginSession(HttpServletRequest request) {
		HttpSession session = getSession(request);
		return (Fuser) session.getAttribute("second_login_user");
	}

	public void SetSecondLoginSession(HttpServletRequest request, Fuser fuser) {
		HttpSession session = getSession(request);
		session.setAttribute("second_login_user", fuser);
	}

	public void RemoveSecondLoginSession(HttpServletRequest request) {
		HttpSession session = getSession(request);
		session.setAttribute("second_login_user", null);
	}

	public void SetSession(Fuser fuser, HttpServletRequest request, HttpServletResponse response) {
		if (!Comm.getISREDIS()) {
			HttpSession session = getSession(request);
			session.setAttribute("login_user", fuser);
		} else {
			// 保存到redis中
			redisDAO.update(fuser.getFid(), fuser.getFloginName(), response);
			redisDAO.updateFuser(fuser);
		}

	}

	public void CleanSession(HttpServletRequest request) {
		try {
			String sessionid = getCookie(request, "sessionId");
			HttpSession session = getSession(request);
			String key = GetSession(request).getFid() + "trade";
			getSession(request).removeAttribute(key);
			session.setAttribute("login_user", null);
			redisDAO.delete(sessionid);
			Cookie cookie = new Cookie("sessionId", null);
			cookie.setMaxAge(0);
		} catch (Exception e) {
		}
	}

	public String getCookie(HttpServletRequest request, String key) {
		String cookieValue = "";
		Cookie[] cookies = request.getCookies();
		if (cookies != null && cookies.length > 0) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals(key)) {
					cookieValue = cookie.getValue();
				}
			}
		}
		return cookieValue;
	}

	public boolean isNeedTradePassword(HttpServletRequest request) {
		if (GetSession(request) == null)
			return true;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String key = GetSession(request).getFid() + "trade";
		Object obj = getSession(request).getAttribute(key);

		if (obj == null) {
			return true;
		} else {
			try {
				double hour = Double.valueOf(this.systemArgsService.getValue("tradePasswordHour"));
				double lastHour = Utils
						.getDouble((sdf.parse(obj.toString()).getTime() - new Date().getTime()) / 1000 / 60 / 60, 2);
				if (lastHour >= hour) {
					getSession(request).removeAttribute(key);
					return true;
				} else {
					return false;
				}
			} catch (ParseException e) {
				return false;
			}
		}
	}

	public void setNoNeedPassword(HttpServletRequest request) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String key = GetSession(request).getFid() + "trade";
		getSession(request).setAttribute(key, sdf.format(new Date()));
	}

	public String generatePagin(int total, int currentPage, String path) {
		if (total <= 0) {
			return "";
		}

		StringBuffer sb = new StringBuffer();
		sb.append("<ul class='pagination' style='cursor:pointer'>");
		if (currentPage == 1) {
			sb.append("<li class='active'><a  href='javascript:void(0)'>1</a></li>");
		} else {
			sb.append("<li><a href='" + path + "currentPage=1'>&lt</a></li>");
			sb.append("<li><a href='" + path + "currentPage=1'>1</a></li>");
		}

		if (currentPage == 2) {
			sb.append("<li class='active'><a href='javascript:void(0)'>2</a></li>");
		} else if (total >= 2) {
			sb.append("<li><a href='" + path + "currentPage=2'>2</a></li>");
		}

		if (currentPage >= 7) {
			sb.append("<li><a href='javascript:void(0)'>...</a></li>");
		}

		// 前三页
		int begin = currentPage - 3;
		begin = begin <= 2 ? 3 : begin;
		for (int i = begin; i < currentPage; i++) {
			sb.append("<li><a href='" + path + "currentPage=" + i + "'>" + i + "</a></li>");
		}

		if (currentPage != 1 && currentPage != 2) {
			sb.append("<li class='active'><a href='javascript:void(0)'>" + currentPage + "</a></li>");
		}

		// 后三页
		begin = currentPage + 1;
		begin = begin <= 2 ? 3 : begin;
		int end = currentPage + 4;
		if (currentPage < 6) {
			int tInt = 6 - currentPage;
			end = end + ((tInt > 3 ? 3 : tInt));
		}
		for (int i = begin; i < end && i <= total; i++) {
			sb.append("<li><a href='" + path + "currentPage=" + i + "'>" + i + "</a></li>");
		}

		/*
		 * if(total-currentPage==4){
		 * sb.append("<li><a href='"+path+"currentPage="+total+"'>"+total+
		 * "</a></li>") ; }else
		 */ if (total - currentPage > 3 && total > 7) {
			sb.append("<li><a href='javascript:void(0)'>...</a></li>");
		}

		if (total >= 11 && total - currentPage > 4) {
			sb.append("<li><a href='" + path + "currentPage=" + total + "'>" + total + "</a></li>");
		}

		if (currentPage < total) {
			sb.append("<li><a href='" + path + "currentPage=" + total + "'>&gt</a></li>");
		}

		sb.append("</ul>");

		return sb.toString();
	}

	/**
	 * 兑换中心分页
	 * 
	 * @param total
	 * @param currentPage
	 * @return
	 */
	public String generatePaginForEx(int total, int currentPage) {
		if (total <= 0) {
			return "";
		}

		StringBuffer sb = new StringBuffer();
		sb.append("<ul class='pagination' style='cursor:pointer'>");
		if (currentPage == 1) {
			sb.append("<li class='active'><a  href='javascript:void(0)'>1</a></li>");
		} else {
			sb.append("<li><a onclick='pageForEx(1)'>&lt</a></li>");
			sb.append("<li><a onclick='pageForEx(1)'>1</a></li>");
		}

		if (currentPage == 2) {
			sb.append("<li class='active'><a href='javascript:void(0)'>2</a></li>");
		} else if (total >= 2) {
			sb.append("<li><a onclick='pageForEx(2)'>2</a></li>");
		}

		if (currentPage >= 7) {
			sb.append("<li><a href='javascript:void(0)'>...</a></li>");
		}

		// 前三页
		int begin = currentPage - 3;
		begin = begin <= 2 ? 3 : begin;
		for (int i = begin; i < currentPage; i++) {
			sb.append("<li><a onclick='pageForEx(" + i + ")'>" + i + "</a></li>");
		}

		if (currentPage != 1 && currentPage != 2) {
			sb.append("<li class='active'><a href='javascript:void(0)'>" + currentPage + "</a></li>");
		}

		// 后三页
		begin = currentPage + 1;
		begin = begin <= 2 ? 3 : begin;
		int end = currentPage + 4;
		if (currentPage < 6) {
			int tInt = 6 - currentPage;
			end = end + ((tInt > 3 ? 3 : tInt));
		}
		for (int i = begin; i < end && i <= total; i++) {
			sb.append("<li><a onclick='pageForEx(" + i + ")'>" + i + "</a></li>");
		}

		/*
		 * if(total-currentPage==4){
		 * sb.append("<li><a href='"+path+"currentPage="+total+"'>"+total+
		 * "</a></li>") ; }else
		 */ if (total - currentPage > 3 && total > 7) {
			sb.append("<li><a href='javascript:void(0)'>...</a></li>");
		}

		if (total >= 11 && total - currentPage > 4) {
			sb.append("<li><a >" + total + "</a></li>");
		}

		if (currentPage < total) {
			sb.append("<li><a onclick='pageForEx(" + total + ")'>&gt</a></li>");
		}

		sb.append("</ul>");

		return sb.toString();
	}

	public String generatePagin1(int total, int currentPage) {
		if (total <= 0) {
			return "";
		}

		StringBuffer sb = new StringBuffer();
		sb.append("<ul class='pagination' style='cursor:pointer'>");
		if (currentPage == 1) {
			sb.append("<li class='active'><a  href='javascript:void(0)'>1</a></li>");
		} else {
			sb.append("<li><a onclick='ajaxPage(1)'>&lt</a></li>");
			sb.append("<li><a onclick='ajaxPage(1)'>1</a></li>");
		}

		if (currentPage == 2) {
			sb.append("<li class='active'><a href='javascript:void(0)'>2</a></li>");
		} else if (total >= 2) {
			sb.append("<li><a onclick='ajaxPage(2)'>2</a></li>");
		}

		if (currentPage >= 7) {
			sb.append("<li><a href='javascript:void(0)'>...</a></li>");
		}

		// 前三页
		int begin = currentPage - 3;
		begin = begin <= 2 ? 3 : begin;
		for (int i = begin; i < currentPage; i++) {
			sb.append("<li><a onclick='ajaxPage(" + i + ")'>" + i + "</a></li>");
		}

		if (currentPage != 1 && currentPage != 2) {
			sb.append("<li class='active'><a href='javascript:void(0)'>" + currentPage + "</a></li>");
		}

		// 后三页
		begin = currentPage + 1;
		begin = begin <= 2 ? 3 : begin;
		int end = currentPage + 4;
		if (currentPage < 6) {
			int tInt = 6 - currentPage;
			end = end + ((tInt > 3 ? 3 : tInt));
		}
		for (int i = begin; i < end && i <= total; i++) {
			sb.append("<li><a onclick='ajaxPage(" + i + ")'>" + i + "</a></li>");
		}

		/*
		 * if(total-currentPage==4){
		 * sb.append("<li><a href='"+path+"currentPage="+total+"'>"+total+
		 * "</a></li>") ; }else
		 */ if (total - currentPage > 3 && total > 7) {
			sb.append("<li><a href='javascript:void(0)'>...</a></li>");
		}

		if (total >= 11 && total - currentPage > 4) {
			sb.append("<li><a >" + total + "</a></li>");
		}

		if (currentPage < total) {
			sb.append("<li><a onclick='ajaxPage(" + total + ")'>&gt</a></li>");
		}

		sb.append("</ul>");

		return sb.toString();
	}

	public String generatePagin2(int total, int currentPage) {
		if (total <= 0) {
			return "";
		}

		StringBuffer sb = new StringBuffer();
		sb.append("<ul class='pagination' style='cursor:pointer'>");
		if (currentPage == 1) {
			sb.append("<li class='active'><a  href='javascript:void(0)'>1</a></li>");
		} else {
			sb.append("<li><a onclick='ajaxPage1(1)'>&lt</a></li>");
			sb.append("<li><a onclick='ajaxPage1(1)'>1</a></li>");
		}

		if (currentPage == 2) {
			sb.append("<li class='active'><a href='javascript:void(0)'>2</a></li>");
		} else if (total >= 2) {
			sb.append("<li><a onclick='ajaxPage1(2)'>2</a></li>");
		}

		if (currentPage >= 7) {
			sb.append("<li><a href='javascript:void(0)'>...</a></li>");
		}

		// 前三页
		int begin = currentPage - 3;
		begin = begin <= 2 ? 3 : begin;
		for (int i = begin; i < currentPage; i++) {
			sb.append("<li><a onclick='ajaxPage1(" + i + ")'>" + i + "</a></li>");
		}

		if (currentPage != 1 && currentPage != 2) {
			sb.append("<li class='active'><a href='javascript:void(0)'>" + currentPage + "</a></li>");
		}

		// 后三页
		begin = currentPage + 1;
		begin = begin <= 2 ? 3 : begin;
		int end = currentPage + 4;
		if (currentPage < 6) {
			int tInt = 6 - currentPage;
			end = end + ((tInt > 3 ? 3 : tInt));
		}
		for (int i = begin; i < end && i <= total; i++) {
			sb.append("<li><a onclick='ajaxPage1(" + i + ")'>" + i + "</a></li>");
		}

		/*
		 * if(total-currentPage==4){
		 * sb.append("<li><a href='"+path+"currentPage="+total+"'>"+total+
		 * "</a></li>") ; }else
		 */ if (total - currentPage > 3 && total > 7) {
			sb.append("<li><a href='javascript:void(0)'>...</a></li>");
		}

		if (total >= 11 && total - currentPage > 4) {
			sb.append("<li><a >" + total + "</a></li>");
		}

		if (currentPage < total) {
			sb.append("<li><a onclick='ajaxPage1(" + total + ")'>&gt</a></li>");
		}

		sb.append("</ul>");

		return sb.toString();
	}

	public String generatePagin3(int total, int currentPage) {
		if (total <= 0) {
			return "";
		}

		StringBuffer sb = new StringBuffer();
		sb.append("<ul class='pagination' style='cursor:pointer'>");
		if (currentPage == 1) {
			sb.append("<li class='active'><a  href='javascript:void(0)'>1</a></li>");
		} else {
			sb.append("<li><a onclick='ajaxPage2(1)'>&lt</a></li>");
			sb.append("<li><a onclick='ajaxPage2(1)'>1</a></li>");
		}

		if (currentPage == 2) {
			sb.append("<li class='active'><a href='javascript:void(0)'>2</a></li>");
		} else if (total >= 2) {
			sb.append("<li><a onclick='ajaxPage2(2)'>2</a></li>");
		}

		if (currentPage >= 7) {
			sb.append("<li><a href='javascript:void(0)'>...</a></li>");
		}

		// 前三页
		int begin = currentPage - 3;
		begin = begin <= 2 ? 3 : begin;
		for (int i = begin; i < currentPage; i++) {
			sb.append("<li><a onclick='ajaxPage2(" + i + ")'>" + i + "</a></li>");
		}

		if (currentPage != 1 && currentPage != 2) {
			sb.append("<li class='active'><a href='javascript:void(0)'>" + currentPage + "</a></li>");
		}

		// 后三页
		begin = currentPage + 1;
		begin = begin <= 2 ? 3 : begin;
		int end = currentPage + 4;
		if (currentPage < 6) {
			int tInt = 6 - currentPage;
			end = end + ((tInt > 3 ? 3 : tInt));
		}
		for (int i = begin; i < end && i <= total; i++) {
			sb.append("<li><a onclick='ajaxPage2(" + i + ")'>" + i + "</a></li>");
		}

		/*
		 * if(total-currentPage==4){
		 * sb.append("<li><a href='"+path+"currentPage="+total+"'>"+total+
		 * "</a></li>") ; }else
		 */ if (total - currentPage > 3 && total > 7) {
			sb.append("<li><a href='javascript:void(0)'>...</a></li>");
		}

		if (total >= 11 && total - currentPage > 4) {
			sb.append("<li><a >" + total + "</a></li>");
		}

		if (currentPage < total) {
			sb.append("<li><a onclick='ajaxPage2(" + total + ")'>&gt</a></li>");
		}

		sb.append("</ul>");

		return sb.toString();
	}

	public String generatePagin4(int total, int currentPage) {
		if (total <= 0) {
			return "";
		}

		StringBuffer sb = new StringBuffer();
		sb.append("<ul class='pagination' style='cursor:pointer'>");
		if (currentPage == 1) {
			sb.append("<li class='active'><a  href='javascript:void(0)'>1</a></li>");
		} else {
			sb.append("<li><a onclick='ajaxPage3(1)'>&lt</a></li>");
			sb.append("<li><a onclick='ajaxPage3(1)'>1</a></li>");
		}

		if (currentPage == 2) {
			sb.append("<li class='active'><a href='javascript:void(0)'>2</a></li>");
		} else if (total >= 2) {
			sb.append("<li><a onclick='ajaxPage3(2)'>2</a></li>");
		}

		if (currentPage >= 7) {
			sb.append("<li><a href='javascript:void(0)'>...</a></li>");
		}

		// 前三页
		int begin = currentPage - 3;
		begin = begin <= 2 ? 3 : begin;
		for (int i = begin; i < currentPage; i++) {
			sb.append("<li><a onclick='ajaxPage3(" + i + ")'>" + i + "</a></li>");
		}

		if (currentPage != 1 && currentPage != 2) {
			sb.append("<li class='active'><a href='javascript:void(0)'>" + currentPage + "</a></li>");
		}

		// 后三页
		begin = currentPage + 1;
		begin = begin <= 2 ? 3 : begin;
		int end = currentPage + 4;
		if (currentPage < 6) {
			int tInt = 6 - currentPage;
			end = end + ((tInt > 3 ? 3 : tInt));
		}
		for (int i = begin; i < end && i <= total; i++) {
			sb.append("<li><a onclick='ajaxPage3(" + i + ")'>" + i + "</a></li>");
		}

		/*
		 * if(total-currentPage==4){
		 * sb.append("<li><a href='"+path+"currentPage="+total+"'>"+total+
		 * "</a></li>") ; }else
		 */ if (total - currentPage > 3 && total > 7) {
			sb.append("<li><a href='javascript:void(0)'>...</a></li>");
		}

		if (total >= 11 && total - currentPage > 4) {
			sb.append("<li><a >" + total + "</a></li>");
		}

		if (currentPage < total) {
			sb.append("<li><a onclick='ajaxPage3(" + total + ")'>&gt</a></li>");
		}

		sb.append("</ul>");

		return sb.toString();
	}

	public String generatePagin5(int total, int currentPage) {
		if (total <= 0) {
			return "";
		}

		StringBuffer sb = new StringBuffer();
		sb.append("<ul class='pagination' style='cursor:pointer'>");
		if (currentPage == 1) {
			sb.append("<li class='active'><a  href='javascript:void(0)'>1</a></li>");
		} else {
			sb.append("<li><a onclick='ajaxPage4(1)'>&lt</a></li>");
			sb.append("<li><a onclick='ajaxPage4(1)'>1</a></li>");
		}

		if (currentPage == 2) {
			sb.append("<li class='active'><a href='javascript:void(0)'>2</a></li>");
		} else if (total >= 2) {
			sb.append("<li><a onclick='ajaxPage4(2)'>2</a></li>");
		}

		if (currentPage >= 7) {
			sb.append("<li><a href='javascript:void(0)'>...</a></li>");
		}

		// 前三页
		int begin = currentPage - 3;
		begin = begin <= 2 ? 3 : begin;
		for (int i = begin; i < currentPage; i++) {
			sb.append("<li><a onclick='ajaxPage4(" + i + ")'>" + i + "</a></li>");
		}

		if (currentPage != 1 && currentPage != 2) {
			sb.append("<li class='active'><a href='javascript:void(0)'>" + currentPage + "</a></li>");
		}

		// 后三页
		begin = currentPage + 1;
		begin = begin <= 2 ? 3 : begin;
		int end = currentPage + 4;
		if (currentPage < 6) {
			int tInt = 6 - currentPage;
			end = end + ((tInt > 3 ? 3 : tInt));
		}
		for (int i = begin; i < end && i <= total; i++) {
			sb.append("<li><a onclick='ajaxPage4(" + i + ")'>" + i + "</a></li>");
		}

		/*
		 * if(total-currentPage==4){
		 * sb.append("<li><a href='"+path+"currentPage="+total+"'>"+total+
		 * "</a></li>") ; }else
		 */ if (total - currentPage > 3 && total > 7) {
			sb.append("<li><a href='javascript:void(0)'>...</a></li>");
		}

		if (total >= 11 && total - currentPage > 4) {
			sb.append("<li><a >" + total + "</a></li>");
		}

		if (currentPage < total) {
			sb.append("<li><a onclick='ajaxPage4(" + total + ")'>&gt</a></li>");
		}

		sb.append("</ul>");

		return sb.toString();
	}

	public String generatePagin6(int total, int currentPage) {
		if (total <= 0) {
			return "";
		}

		StringBuffer sb = new StringBuffer();
		sb.append("<ul class='pagination' style='cursor:pointer'>");
		if (currentPage == 1) {
			sb.append("<li class='active'><a  href='javascript:void(0)'>1</a></li>");
		} else {
			sb.append("<li><a onclick='firstAjaxPage(1)'>&lt</a></li>");
			sb.append("<li><a onclick='firstAjaxPage(1)'>1</a></li>");
		}

		if (currentPage == 2) {
			sb.append("<li class='active'><a href='javascript:void(0)'>2</a></li>");
		} else if (total >= 2) {
			sb.append("<li><a onclick='firstAjaxPage(2)'>2</a></li>");
		}

		if (currentPage >= 7) {
			sb.append("<li><a href='javascript:void(0)'>...</a></li>");
		}

		// 前三页
		int begin = currentPage - 3;
		begin = begin <= 2 ? 3 : begin;
		for (int i = begin; i < currentPage; i++) {
			sb.append("<li><a onclick='firstAjaxPage(" + i + ")'>" + i + "</a></li>");
		}

		if (currentPage != 1 && currentPage != 2) {
			sb.append("<li class='active'><a href='javascript:void(0)'>" + currentPage + "</a></li>");
		}

		// 后三页
		begin = currentPage + 1;
		begin = begin <= 2 ? 3 : begin;
		int end = currentPage + 4;
		if (currentPage < 6) {
			int tInt = 6 - currentPage;
			end = end + ((tInt > 3 ? 3 : tInt));
		}
		for (int i = begin; i < end && i <= total; i++) {
			sb.append("<li><a onclick='firstAjaxPage(" + i + ")'>" + i + "</a></li>");
		}

		/*
		 * if(total-currentPage==4){
		 * sb.append("<li><a href='"+path+"currentPage="+total+"'>"+total+
		 * "</a></li>") ; }else
		 */ if (total - currentPage > 3 && total > 7) {
			sb.append("<li><a href='javascript:void(0)'>...</a></li>");
		}

		if (total >= 11 && total - currentPage > 4) {
			sb.append("<li><a >" + total + "</a></li>");
		}

		if (currentPage < total) {
			sb.append("<li><a onclick='firstAjaxPage(" + total + ")'>&gt</a></li>");
		}

		sb.append("</ul>");

		return sb.toString();
	}

	public String generatePaginX(int total, int currentPage, String methods) {
		if (total <= 0) {
			return "";
		}

		StringBuffer sb = new StringBuffer();
		sb.append("<ul class='pagination' style='cursor:pointer'>");
		if (currentPage == 1) {
			sb.append("<li class='active'><a  href='javascript:void(0)'>1</a></li>");
		} else {
			sb.append("<li><a onclick='" + methods + "(1)'>&lt</a></li>");
			sb.append("<li><a onclick='" + methods + "(1)'>1</a></li>");
		}

		if (currentPage == 2) {
			sb.append("<li class='active'><a href='javascript:void(0)'>2</a></li>");
		} else if (total >= 2) {
			sb.append("<li><a onclick='" + methods + "(2)'>2</a></li>");
		}

		if (currentPage >= 7) {
			sb.append("<li><a href='javascript:void(0)'>...</a></li>");
		}

		// 前三页
		int begin = currentPage - 3;
		begin = begin <= 2 ? 3 : begin;
		for (int i = begin; i < currentPage; i++) {
			sb.append("<li><a onclick='" + methods + "(" + i + ")'>" + i + "</a></li>");
		}

		if (currentPage != 1 && currentPage != 2) {
			sb.append("<li class='active'><a href='javascript:void(0)'>" + currentPage + "</a></li>");
		}

		// 后三页
		begin = currentPage + 1;
		begin = begin <= 2 ? 3 : begin;
		int end = currentPage + 4;
		if (currentPage < 6) {
			int tInt = 6 - currentPage;
			end = end + ((tInt > 3 ? 3 : tInt));
		}
		for (int i = begin; i < end && i <= total; i++) {
			sb.append("<li><a onclick='" + methods + "(" + i + ")'>" + i + "</a></li>");
		}

		/*
		 * if(total-currentPage==4){
		 * sb.append("<li><a href='"+path+"currentPage="+total+"'>"+total+
		 * "</a></li>") ; }else
		 */ if (total - currentPage > 3 && total > 7) {
			sb.append("<li><a href='javascript:void(0)'>...</a></li>");
		}

		if (total >= 11 && total - currentPage > 4) {
			sb.append("<li><a >" + total + "</a></li>");
		}

		if (currentPage < total) {
			sb.append("<li><a onclick='" + methods + "(" + total + ")'>&gt</a></li>");
		}

		sb.append("</ul>");

		return sb.toString();
	}

	// for menu
	@ModelAttribute
	public void menuSelect(HttpServletRequest request) {
		// banner菜单
		String uri = request.getRequestURI();
		// 左侧菜单
		if (uri.startsWith("/trade") || uri.startsWith("/user") || uri.startsWith("/shop/")
				|| uri.startsWith("/divide/") || uri.startsWith("/crowd/") || uri.startsWith("/balance/")
				|| uri.startsWith("/introl/mydivide") || uri.startsWith("/account") || uri.startsWith("/financial")
				|| uri.startsWith("/coinage/") || uri.startsWith("/free/")// 融资融币
				|| uri.startsWith("/question")||uri.startsWith("/stationMail")) {
			String leftMenu = null;
			int selectGroup = 1;

			if (uri.startsWith("/question/questionColumn")) {
				leftMenu = "questionColumn";
				selectGroup = 4;
			} else if (uri.startsWith("/question/question")) {
				leftMenu = "question";
				selectGroup = 4;
			} else if (uri.startsWith("/user/apply")) {
				leftMenu = "apply";
				selectGroup = 4;
			}
			if (uri.startsWith("/question/message")) {
				leftMenu = "message";
				selectGroup = 4;
			} else if (uri.startsWith("/user/")) {
				leftMenu = "userinfo";
				selectGroup = 3;
			} else if (uri.startsWith("/user/security")) {
				leftMenu = "security";
				selectGroup = 3;
			} else if (uri.startsWith("/user/api")) {
				leftMenu = "api";
				selectGroup = 3;
			} else if (uri.startsWith("/account/record")) {
				leftMenu = "record";
				selectGroup = 2;
			} else if (uri.startsWith("/financial/accountalipay") || uri.startsWith("/financial/accountbank")
					|| uri.startsWith("/financial/accountcoin")) {
				leftMenu = "accountAdd";
				selectGroup = 2;
			} else if (uri.startsWith("/crowd/mylogs") || uri.startsWith("/crowd/logs")) {
				leftMenu = "mylogs";
				selectGroup = 2;
			} else if (uri.startsWith("/account/deduct")) {
				leftMenu = "record";
				selectGroup = 2;
			} else if (uri.startsWith("/account/rechargeCny") || uri.startsWith("/account/proxyCode")
					|| uri.startsWith("/account/payCode")) {
				leftMenu = "recharge";
				selectGroup = 2;
			} else if (uri.startsWith("/payment/resultPay")) {
				leftMenu = "recharge";
				selectGroup = 2;
			} else if (uri.startsWith("/account/payOnline.html")) {
				leftMenu = "recharge";
				selectGroup = 2;
			}
			// 微信支付 支付宝支付
			else if (uri.startsWith("/account/weChatPay") || uri.startsWith("/account/AliPay")
					|| uri.startsWith("/account/rechargeOnline")) {
				leftMenu = "recharge";
				selectGroup = 2;
			} else if (uri.startsWith("/account/withdrawCny")||uri.startsWith("/account/withdrawbtcToCny")) {
				leftMenu = "withdraw";
				selectGroup = 2;
			} else if (uri.startsWith("/account/withdrawOnline")) {
				leftMenu = "withdraw";
				selectGroup = 2;
			} else if (uri.startsWith("/account/rechargeBtc")) {
				leftMenu = "rechargeBtc";
				selectGroup = 2;
			} else if (uri.startsWith("/account/withdrawBtc")) {
				leftMenu = "withdrawBtc";
				selectGroup = 2;
			} else if (uri.startsWith("/account/btcTransport")) {
				leftMenu = "btcTransport";
				selectGroup = 2;
			} else if (uri.startsWith("/trade/entrust")) {
				leftMenu = "entrust";
				selectGroup = 1;
			} else if (uri.startsWith("/divide/")) {
				leftMenu = "divide";
				selectGroup = 6;
			} else if (uri.startsWith("/financial/")) {
				leftMenu = "financial";
				selectGroup = 2;
			} else if (uri.startsWith("/introl/mydivide")) {
				leftMenu = "introl";
				selectGroup = 2;
			}else if (uri.startsWith("/stationMail/")) {
				leftMenu = "userinfo";
				selectGroup = 3;
			}
			request.setAttribute("selectGroup", selectGroup);
			request.setAttribute("leftMenu", leftMenu);
		}
	}

	@ModelAttribute
	public void addConstant(HttpServletRequest request) {// 此方法会在每个controller前执行
		Cookie[] cookies = request.getCookies();
		int okhelp = 0;
		if (cookies != null && cookies.length > 0) {
			for (Cookie cookie : cookies) {
				if ("okhelp".equalsIgnoreCase(cookie.getName())) {
					try {
						okhelp = Integer.parseInt(cookie.getValue());
					} catch (Exception e) {

					}
				}
			}
		}
		if (okhelp != 0 && okhelp != 1) {
			okhelp = 1;
		}
		request.setAttribute("okhelp", okhelp);
		// 前端常量
		request.setAttribute("constant", constantMap.getMap());
		// 最新成交价格
		List<Fvirtualcointype> realTimePrize = new ArrayList<Fvirtualcointype>();
		List<Fvirtualcointype> fvirtualcointypes = (List) this.constantMap.get("virtualCoinType");
		for (Fvirtualcointype fvirtualcointype : fvirtualcointypes) {
			fvirtualcointype.setLastDealPrize(this.realTimeData.getLatestDealPrize(fvirtualcointype.getFid()));
			fvirtualcointype.setHigestBuyPrize(this.realTimeData.getHighestBuyPrize(fvirtualcointype.getFid()));
			fvirtualcointype.setLowestSellPrize(this.realTimeData.getLowestSellPrize(fvirtualcointype.getFid()));
			realTimePrize.add(fvirtualcointype);

		}
		request.setAttribute("realTimePrize", realTimePrize);

		if (GetSession(request) == null)
			return;// 用户没登陆不需执行以下内容
		//获取默认资产
		String filter=" where fisDefAsset=1";
		List<Fvirtualcointype> listFvirtualcointype=frontVirtualCoinService.findByParam1(0, 0, filter, false, "Fvirtualcointype");
		Fvirtualcointype fvirtualcointype=null;
		Fsubscription fsubscription=null;
		if(null!=listFvirtualcointype&&listFvirtualcointype.size()>0)
		{
			fvirtualcointype=listFvirtualcointype.get(0);
			fsubscription= subscriptionService.findByFviId(fvirtualcointype.getFid());//人民币兑换  fisRM是1
		}
		
		// 用户钱包
		Fwallet fwallet = this.frontUserService.findFwalletById(GetSession(request).getFwallet().getFid());
		request.setAttribute("fwallet", fwallet);
		// 虚拟钱包
		// Map<Timestamp,Fvirtualwallet> fvirtualwallets =
		// this.frontUserService.findVirtualWallet(GetSession(request).getFid())
		// ;
		List<Fvirtualwallet> fvirtualwallets = this.frontUserService.findVirtualWallet(GetSession(request).getFid(),
				true);
		request.setAttribute("fvirtualwallets", fvirtualwallets);
		// 估计总资产
		// CNY+冻结CNY+（币种+冻结币种）*最高买价
		double totalCapital = 0F;
		totalCapital += fwallet.getFtotalRmb() + fwallet.getFfrozenRmb();
		/*
		 * for (Map.Entry<Timestamp, Fvirtualwallet> entry :
		 * fvirtualwallets.entrySet()) { totalCapital += (
		 * entry.getValue().getFfrozen()+entry.getValue().getFtotal() )*
		 * this.realTimeData.getLatestDealPrize(entry.getValue().
		 * getFvirtualcointype().getFid()) ; }
		 */
		List<Fvirtualwallet> fvirtualwallets1 = this.frontUserService.findVirtualWallet(GetSession(request).getFid(),
				false);
		for (Fvirtualwallet fvirtualwallet : fvirtualwallets1) {
			//获取默认资产类型与默认资产钱包
			if(null!=fvirtualcointype&&fvirtualcointype.getFid().equals(fvirtualwallet.getFvirtualcointype().getFid()))
			{
				request.setAttribute("defAssetType",fvirtualcointype);
				request.setAttribute("defAssetWallet",fvirtualwallet);
				request.setAttribute("fsubscription",fsubscription);
				
			}
			totalCapital += (fvirtualwallet.getFfrozen() + fvirtualwallet.getFtotal())
					* this.realTimeData.getLatestDealPrize(fvirtualwallet.getFvirtualcointype().getFid());
		}

		request.setAttribute("totalNet", Utils.getDouble(totalCapital, 2));
		request.setAttribute("totalCapital", Utils.getDouble(totalCapital, 2));

		request.setAttribute("totalNetTrade", Utils.getDouble(totalCapital, 2));
		request.setAttribute("totalCapitalTrade", Utils.getDouble(totalCapital, 2));
		request.setAttribute("login_user", GetSession(request));
		//查询默认资产钱包
		updateKey(request);

	}

	public Map<String, Object> setRealTimeData(String id) {
		Map<String, Object> map = new HashMap<String, Object>();

		if (Constant.CombinedDepth) {
			map.put("BuyMap", this.realTimeData.getBuyDepthMap(id).toArray());
			map.put("SellMap", this.realTimeData.getSellDepthMap(id).toArray());
			// redis

		} else {
			map.put("BuyMap", this.realTimeData.getEntrustBuyMap(id).toArray());
			map.put("SellMap", this.realTimeData.getEntrustSellMap(id).toArray());
		}
		map.put("BuySuccessMap", this.realTimeData.getEntrustSuccessMap(id).toArray());

		map.put("LatestDealPrize", this.realTimeData.getLatestDealPrize(id));
		map.put("LowestSellPrize", this.realTimeData.getLowestSellPrize(id));
		map.put("HighestBuyPrize", this.realTimeData.getHighestBuyPrize(id));

		map.put("OneDayLowest", this.oneDayData.getLowest(id));
		map.put("OneDayHighest", this.oneDayData.getHighest(id));
		map.put("OneDayTotal", this.oneDayData.getTotal(id));
		return map;
	}

	// 发送短信验证码，已登录用户
	public boolean SendMessage(Fuser fuser, String fuserid, String areaCode, String phone, int type) {
		boolean canSend = true;
		MessageValidate messageValidate = this.validateMap.getMessageMap(fuserid + "_" + type);
		if (messageValidate != null && Utils.timeMinus(Utils.getTimestamp(), messageValidate.getCreateTime()) < 120) {
			canSend = false;
		}

		if (canSend) {
			MessageValidate messageValidate2 = new MessageValidate();
			messageValidate2.setAreaCode(areaCode);
			messageValidate2.setCode(Utils.randomInteger(6));
			messageValidate2.setPhone(phone);
			messageValidate2.setCreateTime(Utils.getTimestamp());
			this.validateMap.putMessageMap(fuserid + "_" + type, messageValidate2);

			Fvalidatemessage fvalidatemessage = new Fvalidatemessage();
			fvalidatemessage.setFcontent(this.constantMap.getString(ConstantKeys.VALIDATE_MESSAGE_CONTENT)
					.replace("#code#", messageValidate2.getCode()));
			fvalidatemessage.setFcreateTime(Utils.getTimestamp());
			fvalidatemessage.setFphone(phone);
			fvalidatemessage.setFuser(fuser);
			fvalidatemessage.setFstatus(ValidateMessageStatusEnum.Not_send);
			this.frontValidateService.addFvalidateMessage(fvalidatemessage);

			this.taskList.returnMessageList(fvalidatemessage.getFid());
		}
		return canSend;
	}

	public boolean validateMessageCode(Fuser fuser, String areaCode, String phone, int type, String code) {
		if(!Comm.getISVALIDATE())
		{
			return true;
		}
		boolean match = true;
		MessageValidate messageValidate = this.validateMap.getMessageMap(fuser.getFid() + "_" + type);
		if (messageValidate == null) {
			match = false;
		} else {
			if (!messageValidate.getAreaCode().equals(areaCode) || !messageValidate.getPhone().equals(phone)
					|| !messageValidate.getCode().equals(code)) {
				match = false;
			} else {
				match = true;
				// this.validateMap.removeMessageMap(fuser.getFid()+"_"+type);
			}
		}

		return match;
	}

	// 发送短信验证码，未登录用户
	public boolean SendMessage(String ip, String areaCode, String phone, int type) {
		String key1 = ip + "_" + type;
		String key2 = ip + "_" + phone + "_" + type;
		/*
		 * System.out.print("==========KEY1=========="+key1);
		 * System.out.print("==========KEY2=========="+key2);
		 */
		// 限制ip120秒发送
		MessageValidate messageValidate = this.validateMap.getMessageMap(key1);
		if (messageValidate != null && Utils.timeMinus(Utils.getTimestamp(), messageValidate.getCreateTime()) < 120) {
			return false;
		}

		messageValidate = this.validateMap.getMessageMap(key2);
		if (messageValidate != null && Utils.timeMinus(Utils.getTimestamp(), messageValidate.getCreateTime()) < 120) {
			return false;
		}

		MessageValidate messageValidate2 = new MessageValidate();
		messageValidate2.setAreaCode(areaCode);
		messageValidate2.setCode(Utils.randomInteger(6));
		messageValidate2.setPhone(phone);
		messageValidate2.setCreateTime(Utils.getTimestamp());
		this.validateMap.putMessageMap(key2, messageValidate2);
		this.validateMap.putMessageMap(key1, messageValidate2);

		Fvalidatemessage fvalidatemessage = new Fvalidatemessage();
		fvalidatemessage.setFcontent(this.constantMap.getString(ConstantKeys.VALIDATE_MESSAGE_CONTENT).replace("#code#",
				messageValidate2.getCode()));
		fvalidatemessage.setFcreateTime(Utils.getTimestamp());
		fvalidatemessage.setFphone(phone);
		fvalidatemessage.setFstatus(ValidateMessageStatusEnum.Not_send);
		this.frontValidateService.addFvalidateMessage(fvalidatemessage);

		this.taskList.returnMessageList(fvalidatemessage.getFid());

		return true;
	}

	public boolean validateMessageCode(String ip, String areaCode, String phone, int type, String code) {
		if(!Comm.getISVALIDATE())
		{
			return true;
		}
		if (type != MessageTypeEnum.REG_CODE && type != MessageTypeEnum.FIND_PASSWORD) {
			System.out.println("调用方法错误");
			return false;
		}

		// String key1 = ip+"message_"+type ;
		String key2 = ip + "_" + phone + "_" + type;
		/* System.out.print("==========KEY2=========="+key2); */
		boolean match = true;
		MessageValidate messageValidate = this.validateMap.getMessageMap(key2);
		if (messageValidate == null) {
			match = false;
		} else {
			if (!messageValidate.getAreaCode().equals(areaCode) || !messageValidate.getPhone().equals(phone)
					|| !messageValidate.getCode().equals(code)) {
				match = false;
			} else {
				match = true;
				// this.validateMap.removeMessageMap(key1);
				// this.validateMap.removeMessageMap(key2);
			}
		}

		return match;
	}

	// 发送邮件验证码，未登录用户
	public boolean SendMail(String ip, String mail, int type) {
		String key1 = ip + "mail_" + type;
		String key2 = ip + "_" + mail + "_" + type;
		/*
		 * System.out.print("==========KEY1=========="+key1);
		 * System.out.print("==========KEY2=========="+key2);
		 */
		// 限制ip120秒发送
		Emailvalidate mailValidate = this.validateMap.getMailMap(key1);
		if (mailValidate != null && Utils.timeMinus(Utils.getTimestamp(), mailValidate.getFcreateTime()) < 120) {
			return false;
		}

		mailValidate = this.validateMap.getMailMap(key2);
		if (mailValidate != null && Utils.timeMinus(Utils.getTimestamp(), mailValidate.getFcreateTime()) < 120) {
			return false;
		}

		Emailvalidate mailValidate2 = new Emailvalidate();
		mailValidate2.setCode(Utils.randomInteger(6));
		mailValidate2.setFcreateTime(Utils.getTimestamp());
		mailValidate2.setFmail(mail);

		this.validateMap.putMailMap(key1, mailValidate2);
		this.validateMap.putMailMap(key2, mailValidate2);

		Fvalidateemail fvalidateemail = new Fvalidateemail();
		fvalidateemail.setEmail(mail);
		fvalidateemail.setFcontent(
				this.constantMap.getString(ConstantKeys.regmailContent).replace("#code#", mailValidate2.getCode()));
		fvalidateemail.setFcreateTime(Utils.getTimestamp());
		fvalidateemail.setFstatus(ValidateMailStatusEnum.Not_send);
		fvalidateemail.setFtitle(this.constantMap.getString(ConstantKeys.WEB_NAME) + "注册验证码");
		this.frontValidateService.addFvalidateemail(fvalidateemail);

		this.taskList.returnMailList(fvalidateemail.getFid());

		return true;
	}

	public boolean validateMailCode(String ip, String mail, int type, String code) {
		if(!Comm.getISVALIDATE())
		{
			return true;
		}
		// String key1 = ip+"mail_"+type ;
		String key2 = ip + "_" + mail + "_" + type;
		/* System.out.print("==========KEY2=========="+key2); */
		boolean match = true;
		Emailvalidate emailvalidate = this.validateMap.getMailMap(key2);
		if (emailvalidate == null) {
			match = false;
		} else {
			if (!emailvalidate.getFmail().equals(mail) || !emailvalidate.getCode().equals(code)) {
				match = false;
			} else {
				match = true;
				// this.validateMap.removeMailMap(key1);
				// this.validateMap.removeMailMap(key2);
			}
		}

		return match;
	}

	// 发送邮件验证码，已登录
	public boolean SendMail(Fuser fuser, String mail, int type) {
		String key1 = fuser.getFid() + "mail_" + type;
		String key2 = fuser.getFid() + "_" + mail + "_" + type;
		/*
		 * System.out.print("==========KEY1=========="+key1);
		 * System.out.print("==========KEY2=========="+key2);
		 */
		// 限制ip120秒发送
		Emailvalidate mailValidate = this.validateMap.getMailMap(key1);
		if (mailValidate != null && Utils.timeMinus(Utils.getTimestamp(), mailValidate.getFcreateTime()) < 120) {
			return false;
		}

		mailValidate = this.validateMap.getMailMap(key2);
		if (mailValidate != null && Utils.timeMinus(Utils.getTimestamp(), mailValidate.getFcreateTime()) < 120) {
			return false;
		}

		Emailvalidate mailValidate2 = new Emailvalidate();
		mailValidate2.setCode(Utils.randomInteger(6));
		mailValidate2.setFcreateTime(Utils.getTimestamp());
		mailValidate2.setFmail(mail);

		this.validateMap.putMailMap(key1, mailValidate2);
		this.validateMap.putMailMap(key2, mailValidate2);

		Fvalidateemail fvalidateemail = new Fvalidateemail();
		fvalidateemail.setEmail(mail);
		fvalidateemail.setFcontent(
				this.constantMap.getString(ConstantKeys.regmailContent).replace("#code#", mailValidate2.getCode()));
		fvalidateemail.setFcreateTime(Utils.getTimestamp());
		fvalidateemail.setFstatus(ValidateMailStatusEnum.Not_send);
		fvalidateemail.setFtitle(this.constantMap.getString(ConstantKeys.WEB_NAME) + "注册验证码");
		this.frontValidateService.addFvalidateemail(fvalidateemail);

		this.taskList.returnMailList(fvalidateemail.getFid());

		return true;
	}

	public boolean validateMailCode(Fuser fuser, String mail, int type, String code) {
		// String key1 = ip+"mail_"+type ;
		String key2 = fuser.getFid() + "_" + mail + "_" + type;
		/* System.out.print("==========KEY2=========="+key2); */
		boolean match = true;
		Emailvalidate emailvalidate = this.validateMap.getMailMap(key2);
		if (emailvalidate == null) {
			match = false;
		} else {
			if (!emailvalidate.getFmail().equals(mail) || !emailvalidate.getCode().equals(code)) {
				match = false;
			} else {
				match = true;
				// this.validateMap.removeMailMap(key1);
				// this.validateMap.removeMailMap(key2);
			}
		}

		return match;
	}

	public int totalPage(int totalCount, int maxResults) {
		return totalCount / maxResults + (totalCount % maxResults == 0 ? 0 : 1);
	}

	public Flimittrade isLimitTrade(String vid) {
		Flimittrade flimittrade = null;
		String filter = "where fvirtualcointype.fid='" + vid + "'";
		List<Flimittrade> flimittrades = this.limittradeService.list(0, 0, filter, false);
		if (flimittrades != null && flimittrades.size() > 0) {
			flimittrade = flimittrades.get(0);
		}
		return flimittrade;
	}

	// 图片验证码
	public boolean vcodeValidate(HttpServletRequest request, String vcode) {
		if(!Comm.getISVALIDATE())
		{
			return true;
		}
		Object session_code = request.getSession().getAttribute("checkcode");
		if (session_code == null || !vcode.equalsIgnoreCase((String) session_code)) {
			return false;
		} else {
			return true;
		}
	}
	public List<Fsubscription>  getRechargeTypeList()
	{
		//可以充值的数字资产
		String filterFusb="where fisRMB=1";
		List<Fsubscription> listFsubscription=subscriptionService.findByParam(0, 0, filterFusb, false, "Fsubscription");
        return listFsubscription;
		
	}

	public static String getIpAddr(HttpServletRequest request) {
		System.out.println(request);
		String ip = request.getHeader("X-Forwarded-For");
		try {
			if (ip != null && ip.trim().length() > 0) {
				System.out.println("xxxxxxxxxxxxx" + ip.split(",")[0]);
				return ip.split(",")[0];
			}
		} catch (Exception e) {
		}

		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("http_client_ip");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		// 如果是多级代理，那么取第一个ip为客户ip
		if (ip != null && ip.indexOf(",") != -1) {
			ip = ip.substring(ip.lastIndexOf(",") + 1, ip.length()).trim();
		}
		return ip;
	}
}
