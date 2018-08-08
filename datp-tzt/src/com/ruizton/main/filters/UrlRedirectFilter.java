package com.ruizton.main.filters;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.ditp.dao.RedisDAO;
import com.ditp.service.wallet.WalletPlatFromService;
import com.ditp.util.cookieComm;
import com.ruizton.main.model.Fuser;
import com.ruizton.util.Comm;
import com.ruizton.util.Constant;

/**
 * 请求重定向
 **/
public class UrlRedirectFilter implements Filter {
	@Autowired
	private RedisDAO redisDAO;

	public void init(FilterConfig arg0) throws ServletException {
	}

	public void destroy() {
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;
		resp.setHeader("Access-Control-Allow-Origin", "*");
		resp.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
		resp.setHeader("Access-Control-Max-Age", "3600");
		resp.setHeader("Access-Control-Allow-Headers", "Origin, No-Cache, X-Requested-With, If-Modified-Since, Pragma, Last-Modified, Cache-Control, Expires, Content-Type");

		String uri = req.getRequestURI().toLowerCase().trim();

		/*
		 * if(!uri.endsWith(".js")&&!uri.endsWith(".png")&&!uri.endsWith(".css")
		 * ) if(uri.startsWith("/appapi.html")==true||uri.startsWith(
		 * "/kline/start.html")==true||uri.startsWith("/kline/period.html")==
		 * true ||uri.startsWith("/kline/depth.html")==true||uri.startsWith(
		 * "/kline/trades.html")==true){ chain.doFilter(request, response) ;
		 * return ; }else{ return ; }
		 */
		ServletContext servletContext = ((HttpServletRequest) request).getSession().getServletContext();
		ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(servletContext);
		// 实现类 walletPlatFromService
		RedisDAO redisDao = (RedisDAO) ctx.getBean("redisDAO");
		redisDAO = redisDao;

		// 加密
		String servletName = req.getServerName().toLowerCase();

		Cookie[] cookies = req.getCookies();
		boolean cookieFlag = false;
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("open")) {
					cookieFlag = true;
				}
			}
		}
		if (!cookieFlag) {
			resp.addCookie(new Cookie("open", "true"));
		}

		// 不接受任何jsp请求
		if (uri.endsWith(".jsp")) {
			return;
		}

		// 只拦截.html结尾的请求
		if (!uri.endsWith(".html")) {
			chain.doFilter(request, response);
			return;
		}


		// 是否隐藏交易
		if (Comm.getISHIDDEN_DEAL().equals("true")) {
			if (uri.startsWith("/trade/coin.html") || uri.startsWith("/kline/trade.html")
					|| uri.startsWith("/real/market.html") || uri.startsWith("/trade/entrustinfo.html")
					|| uri.startsWith("/real/market.html")) {
				resp.sendRedirect("/index.html");
				return;
			}
		}
		// 是否隐藏众筹
		if (Comm.getISHIDDEN_CROWDFUNDING()) {
			if (uri.startsWith("/crowd/index.html") || uri.startsWith("/crowd/view.html")
					|| uri.startsWith("/json/crowd/submit.html")) {
				resp.sendRedirect("/index.html");
				return;
			}

		}
		// 是否隐藏兑换
		if (Comm.getISHIDDEN_EX()) {
			if (uri.startsWith("/exchange/index.html") || uri.startsWith("/json/ex/submitex.html")) {
				resp.sendRedirect("/index.html");
				return;
			}
		}
		if (uri.startsWith("/exchange/index.html")) {
			String type = req.getParameter("type");
			if (type != null && type.equals("0")) {
				chain.doFilter(request, response);
				return;
			}

		}
		///////////////////////////////////////////////////////////////////////////////
		if (uri.startsWith("/index.html")// 首页
				|| uri.startsWith("/appapi_stamail.html")//站内信接口
				|| uri.startsWith("/appapi_fincing.html")//理财接口
				|| uri.startsWith("/appapi.html")// app api
				|| uri.startsWith("/appapi_comm.html")// app api 通用
				|| uri.startsWith("/appapi_ex.html")// app api 兑换
				|| uri.startsWith("/app/article")// qq
				|| uri.startsWith("/qqLogin".toLowerCase())// qq
				|| uri.startsWith("/account/rechargeOnline.html?type=2".toLowerCase())// 银联后台通知
				|| uri.startsWith("/account/backRcvResponse.html".toLowerCase())// 银联后台通知
				|| uri.startsWith("/link/wx/callback")// qq
				|| uri.startsWith("/link/qq/call")// qq
				|| uri.startsWith("/error/")// error
				|| uri.startsWith("/api/")// api
				|| uri.startsWith("/data/ticker.html") || uri.startsWith("/user/sendfindpasswordmsg")// api
				|| uri.startsWith("/user/sendregmsg")// api
				|| uri.startsWith("/json/findpassword")// api
				|| uri.startsWith("/line/period-btcdefault.html") || uri.startsWith("/data/depth-btcdefault.html")
				|| uri.startsWith("/data/stock-btcdefault.html") || uri.startsWith("/index_chart.html")
				|| uri.startsWith("/user/login")// 登陆
				|| uri.startsWith("/user/logout")// 退出
				|| uri.startsWith("/user/reg")// 注册
				|| uri.startsWith("/app/reg.html")// 注册
				|| uri.startsWith("/app/suc.html")// 注册
				|| uri.startsWith("/real/")// 行情
				|| uri.startsWith("/finacing/index")// 理财
				|| uri.startsWith("/market")// 行情
				|| uri.startsWith("/block/") || uri.startsWith("/kline/")// 行情
				|| uri.startsWith("/json.html")// 行情
				|| uri.startsWith("/json/")// 行情
				|| uri.startsWith("/validate/")// 邮件激活,找回密码
				|| uri.startsWith("/about/")// 文章管理
				|| (uri.startsWith("/crowd/index")) || uri.startsWith("/service/")// 文章管理
				|| uri.startsWith("/trade/coin.html")// 文章管理
				|| uri.startsWith("/trade/entrustlog.html")// 文章管理
				|| uri.startsWith("/trade/entrustinfo.html")// 文章管理
				// ||uri.startsWith("/appcenter/index")//文章管理
				|| uri.startsWith("/user/sendMailCode".toLowerCase())// 注册邮件
				|| uri.startsWith("/user/sendMsg".toLowerCase())// 注册短信
				|| uri.startsWith("/account/resultnotify.html")// 微信那回调
				|| uri.startsWith("/account/resultalipay.html")// 支付宝回调
				|| uri.startsWith("/test/index.html")// 测试
				|| uri.startsWith("/trade/cointype.html")// ajax分页
				|| uri.startsWith("/real/appkline.html")
		// ||uri.startsWith("/financial/accountcointype.html")//ajax分页
		// ||uri.startsWith("financial/index1")//个人资产ajax分页
		) {
			chain.doFilter(request, response);
			return;
		} else {

			if (uri.startsWith("/ssadmin/")) {
				// 后台
				if (uri.startsWith("/ssadmin/login_btc.html") || uri.startsWith("/ssadmin/submitlogin__btc.html")
						|| req.getSession().getAttribute("login_admin") != null) {
					chain.doFilter(request, response);
				} else {
					resp.sendRedirect("/ssadmin/login_btc.html");
				}
				return;
			} else {
				Object login_user = null;
				if (!Comm.getISREDIS()) {
					login_user = req.getSession().getAttribute("login_user");
				} else {
					login_user = redisDAO.getFuser(cookieComm.getCookie(req, "sessionId"));
				}
				if (login_user == null) {
					String queryStr = req.getQueryString();
					if (queryStr != null && !"".equals(queryStr)) {
						resp.sendRedirect("/?forwardUrl=" + req.getRequestURI().trim() + "?" + queryStr);
					} else {
						resp.sendRedirect("/?forwardUrl=" + req.getRequestURI().trim());
					}
					return;
				} else {
					if (((Fuser) login_user).getFpostRealValidate()) {
						// 提交身份认证信息了
						chain.doFilter(request, response);
						return;
					} else {
						if (uri.startsWith("/user/realCertification.html".toLowerCase())
								|| uri.startsWith("/user/validateidentity.html")
								|| uri.startsWith("/gamecenter/golottery.html")) {
							chain.doFilter(request, response);
						} else {
							resp.sendRedirect("/user/realCertification.html");
						}
						return;
					}
				}

			}

		}

	}

}
