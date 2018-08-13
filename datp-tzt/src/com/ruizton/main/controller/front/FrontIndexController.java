package com.ruizton.main.controller.front;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.ditp.service.FinacingService;
import com.ruizton.main.Enum.UserStatusEnum;
import com.ruizton.main.comm.ConstantMap;
import com.ruizton.main.comm.KeyValues;
import com.ruizton.main.controller.BaseController;
import com.ruizton.main.model.Farticle;
import com.ruizton.main.model.Farticletype;
import com.ruizton.main.model.Fuser;
import com.ruizton.main.service.front.FrontOthersService;
import com.ruizton.main.service.front.FrontUserService;
import com.ruizton.util.Comm;

/**
 * @author   Dylan
 * @data     2018年8月13日
 * @typeName FrontIndexController
 * 说明 ：	程序首页入口 
 * 		跳转到index首页面
 *
 */
@Controller
public class FrontIndexController extends BaseController {

	@Autowired
	private ConstantMap constantMap;
	@Autowired
	private FrontUserService frontUserService;
	@Autowired
	private FrontOthersService frontOtherService;
	@Autowired
	private FinacingService finacingService;

	@RequestMapping("/index")
	public ModelAndView index(HttpServletRequest request, @RequestParam(required = false, defaultValue = "0") int index,
			@RequestParam(required = false, defaultValue = "") String forwardUrl,//forwardUrl &后面的值无法取到
			@RequestParam(required = false, defaultValue = "0") int symbol, HttpServletResponse resp) {

		// 推广注册
		try {
			//测试测试测试
			//finacingService.autoClear();

			String id = request.getParameter("r");
			if (id != null) {
				Fuser intro = this.frontUserService.findById(id);
				if (intro != null) {
					resp.addCookie(new Cookie("r", String.valueOf(id)));
				}
			}
		} catch (Exception e) {
		}

		ModelAndView modelAndView = new ModelAndView();
		if (GetSession(request) == null) {
			modelAndView.addObject("forwardUrl", forwardUrl);
		} else {
			Fuser fuser = this.frontUserService.findById(GetSession(request).getFid());
			if (fuser.getFstatus() == UserStatusEnum.FORBBIN_VALUE) {
				CleanSession(request);
			}
			// modelAndView.addObject("times",fuser.getFscore().getFkillQty()) ;
		}


		if (index == 1) {
			RemoveSecondLoginSession(request);
		}

		List<KeyValues> articles = new ArrayList<KeyValues>();
		List<Farticletype> farticletypes = this.frontOtherService.findFarticleTypeAll();
		for (int i = 0; i < farticletypes.size(); i++) {
			KeyValues keyValues = new KeyValues();
			Farticletype farticletype = farticletypes.get(i);
			List<Farticle> farticles = this.frontOtherService.findFarticle(farticletype.getFid(), 0, 6);
			keyValues.setKey(farticletype);
			keyValues.setValue(farticles);
			articles.add(keyValues);
		}

		modelAndView.addObject("articles", articles);

		/*
		 * List<Fvirtualcointype> fvirtualcointypes = (List)
		 * this.constantMap.get("virtualCoinType");
		 * modelAndView.addObject("fvirtualcointypes", fvirtualcointypes);
		 */

		int isIndex = 1;
		modelAndView.addObject("isIndex", isIndex);
		if (!forwardUrl.equals("") & GetSession(request) != null) {
			modelAndView.addObject("forwardUrl", forwardUrl);
		}
		//是否隐藏首页交易行情和交易中心菜单
		modelAndView.addObject("isHiddenDeal", Comm.getISHIDDEN_DEAL());
		modelAndView.setViewName("front/index");
		return modelAndView;
	}

}
