package com.ruizton.main.controller.admin;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.ruizton.main.Enum.CountLimitTypeEnum;
import com.ruizton.main.controller.BaseController;
import com.ruizton.main.service.admin.AdminService;
import com.ruizton.main.service.admin.CapitaloperationService;
import com.ruizton.main.service.admin.EntrustService;
import com.ruizton.main.service.admin.UserService;
import com.ruizton.main.service.admin.VirtualCapitaloperationService;
import com.ruizton.main.service.admin.VirtualCoinService;
import com.ruizton.main.service.admin.VirtualWalletService;
import com.ruizton.main.service.admin.WalletService;
import com.ruizton.main.service.front.FrontValidateService;
import com.ruizton.util.Comm;
import com.ruizton.util.Utils;

@Controller
public class IndexController extends BaseController {
	@Autowired
	private HttpServletRequest request;
	@Autowired
	private FrontValidateService frontValidateService;

	@RequestMapping("/ssadmin/index")
	public ModelAndView Index() throws Exception {
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("ssadmin/index");
		modelAndView.addObject("dateTime", sdf1.format(new Date()));
		modelAndView.addObject("login_admin", request.getSession().getAttribute("login_admin"));
		return modelAndView;
	}

	@RequestMapping("/ssadmin/login_btc")
	public ModelAndView login() throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("ssadmin/login");
		return modelAndView;
	}

	@RequestMapping("/ssadmin/submitLogin__btc")
	public ModelAndView submitLogin_btc(HttpServletRequest request, @RequestParam(required = true) String name,
			@RequestParam(required = true) String password, @RequestParam(required = true) String vcode)
			throws Exception {

		ModelAndView modelAndView = new ModelAndView();

		if (name == null || "".equals(name.trim()) || password == null || "".equals(password.trim()) || vcode == null
				|| "".equals(vcode.trim())) {
			modelAndView.setViewName("redirect:/ssadmin/login_btc.html");
			return modelAndView;
		} else {
			String ip = Utils.getIp(request);
			int admin_limit = this.frontValidateService.getLimitCount(ip, CountLimitTypeEnum.AdminLogin);
			if (admin_limit <= 0) {
				modelAndView.addObject("error", "连续登陆错误30次，为安全起见，禁止登陆2小时！");
				modelAndView.setViewName("/ssadmin/login");
				return modelAndView;
			}
			if (Comm.getISVALIDATE()) {
				if (!vcode.equalsIgnoreCase((String) getSession(request).getAttribute("checkcode"))) {
					modelAndView.addObject("error", "验证码错误！");
					modelAndView.setViewName("/ssadmin/login");
					return modelAndView;
				}
			}

			Subject admin = SecurityUtils.getSubject();
			UsernamePasswordToken token = new UsernamePasswordToken(name, Utils.MD5(password));
			token.setRememberMe(true);
			try {
				admin.login(token);
			} catch (Exception e) {
				token.clear();
				this.frontValidateService.updateLimitCount(ip, CountLimitTypeEnum.AdminLogin);
				modelAndView.addObject("error", e.getMessage());
				modelAndView.setViewName("/ssadmin/login");
				return modelAndView;
			}
		}
		modelAndView.setViewName("redirect:/ssadmin/index.html");
		return modelAndView;
	}

}
