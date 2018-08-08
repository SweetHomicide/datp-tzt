package com.ruizton.main.controller.front;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.HtmlUtils;

import com.ruizton.main.Enum.EntrustTypeEnum;
import com.ruizton.main.Enum.SharePlanLogStatusEnum;
import com.ruizton.main.Enum.SharePlanStatusEnum;
import com.ruizton.main.Enum.SharePlanTypeEnum;
import com.ruizton.main.comm.ConstantMap;
import com.ruizton.main.comm.KeyValues;
import com.ruizton.main.controller.BaseController;
import com.ruizton.main.model.Fintrolinfo;
import com.ruizton.main.model.Fuser;
import com.ruizton.main.model.Fvirtualcointype;
import com.ruizton.main.model.Fvirtualwallet;
import com.ruizton.main.model.Fwallet;
import com.ruizton.main.service.admin.AdminService;
import com.ruizton.main.service.admin.EntrustService;
import com.ruizton.main.service.admin.IntrolinfoService;
import com.ruizton.main.service.admin.UserService;
import com.ruizton.main.service.admin.VirtualCoinService;
import com.ruizton.main.service.admin.VirtualWalletService;
import com.ruizton.main.service.front.FrontUserService;
import com.ruizton.main.service.front.FrontVirtualCoinService;
import com.ruizton.main.service.front.UtilsService;
import com.ruizton.util.Constant;
import com.ruizton.util.Utils;

@Controller
public class FrontDivideController extends BaseController {

	@Autowired
	private FrontUserService frontUserService ;
	@Autowired
	private IntrolinfoService introlinfoService;
	@Autowired
	private AdminService adminService;
	@Autowired
	private UserService userService;
	
	
	@RequestMapping("/introl/mydivide")
	public ModelAndView introl(
			HttpServletRequest request,
			@RequestParam(required=true,defaultValue="1")int type,
			@RequestParam(required=false,defaultValue="1")int currentPage
			) throws Exception {
		ModelAndView modelAndView = new ModelAndView() ;
		Fuser fuser = this.frontUserService.findById(GetSession(request).getFid()) ;
		modelAndView.addObject("spreadLink", Constant.Domain+"?r="+fuser.getFid()) ;
		if(type == 1){
			String filter = "where fIntroUser_id.fid='"+fuser.getFid()+"' order by flastUpdateTime desc";
			int total = this.adminService.getAllCount("Fuser", filter);
			int totalPage = total/Constant.RecordPerPage + ((total%Constant.RecordPerPage) ==0?0:1) ;
			List<Fuser> fusers = this.userService.list((currentPage-1)*Constant.RecordPerPage, Constant.RecordPerPage,filter,true) ;
			String pagin = super.generatePagin(totalPage, currentPage, "/introl/mydivide.html?type=1&") ;
			
			modelAndView.addObject("fusers", fusers) ;
			modelAndView.addObject("pagin", pagin) ;
			modelAndView.setViewName("front/introl/index") ;
		}else{
			String filter = "where fuser.fid='"+fuser.getFid()+"' order by fcreatetime desc";
			int total = this.adminService.getAllCount("Fintrolinfo", filter);
			int totalPage = total/Constant.RecordPerPage + ((total%Constant.RecordPerPage)  ==0?0:1) ;
			List<Fintrolinfo> fintrolinfos = this.introlinfoService.list((currentPage-1)*Constant.RecordPerPage, Constant.RecordPerPage,filter,true) ;
			String pagin = super.generatePagin(totalPage, currentPage, "/introl/mydivide.html?type=2&") ;
			
			modelAndView.addObject("fintrolinfos", fintrolinfos) ;
			modelAndView.addObject("pagin", pagin) ;
			modelAndView.setViewName("front/introl/index2") ;
		}
		modelAndView.addObject("type", type) ;
		
		return modelAndView ;
	}
}
