package com.ruizton.main.controller.admin;

import java.security.MessageDigest;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.ruizton.main.Enum.AdminLevelEnum;
import com.ruizton.main.Enum.LinkTypeEnum;
import com.ruizton.main.comm.ParamArray;
import com.ruizton.main.controller.BaseController;
import com.ruizton.main.model.Ffriendlink;
import com.ruizton.main.model.Flimittrade;
import com.ruizton.main.model.Fvirtualcointype;
import com.ruizton.main.service.admin.AdminService;
import com.ruizton.main.service.admin.LimittradeService;
import com.ruizton.main.service.admin.VirtualCoinService;
import com.ruizton.util.Utils;

@Controller
public class LimittradeController extends BaseController {
	@Autowired
	private LimittradeService limittradeService;
	@Autowired
	private AdminService adminService ;
	@Autowired
	private VirtualCoinService virtualCoinService;
	@Autowired
	private HttpServletRequest request ;
	//每页显示多少条数据
	private int numPerPage = Utils.getNumPerPage();
	
	@RequestMapping("/ssadmin/limittradeList")
	public ModelAndView limittradeList() throws Exception{
		ModelAndView modelAndView = new ModelAndView() ;
		modelAndView.setViewName("ssadmin/limittradeList") ;
		//当前页
		int currentPage = 1;
		//搜索关键字
		String keyWord = request.getParameter("keywords");
		String orderField = request.getParameter("orderField");
		String orderDirection = request.getParameter("orderDirection");
		if(request.getParameter("pageNum") != null){
			currentPage = Integer.parseInt(request.getParameter("pageNum"));
		}

        StringBuffer filter = new StringBuffer();
        filter.append("where 1=1 \n");
//		if(keyWord != null && keyWord.trim().length() >0){
//			filter.append("and (fname like '%"+keyWord+"%' or \n");
//			filter.append("furl like '%"+keyWord+"%' ) \n");
//			modelAndView.addObject("keywords", keyWord);
//		}
//		if(orderField != null && orderField.trim().length() >0){
//			filter.append("order by "+orderField+"\n");
//		}else{
//			filter.append("order by fcreateTime \n");
//		}
//		
//		if(orderDirection != null && orderDirection.trim().length() >0){
//			filter.append(orderDirection+"\n");
//		}else{
//			filter.append("desc \n");
//		}
		
		List<Flimittrade> list = this.limittradeService.list((currentPage-1)*numPerPage, numPerPage, filter+"",true);
		modelAndView.addObject("limittradeList", list);
		modelAndView.addObject("numPerPage", numPerPage);
		modelAndView.addObject("currentPage", currentPage);
		modelAndView.addObject("rel", "limittradeList");
		//总数量
		modelAndView.addObject("totalCount",this.adminService.getAllCount("Flimittrade", filter+""));
		return modelAndView ;
	}
	
	@RequestMapping("ssadmin/goLimittradeJSP")
	public ModelAndView goLimittradeJSP() throws Exception{
		String url = request.getParameter("url");
		ModelAndView modelAndView = new ModelAndView() ;
		modelAndView.setViewName(url) ;
		if(request.getParameter("uid") != null){
			String fid = request.getParameter("uid");
			Flimittrade flimittrade = this.limittradeService.findById(fid);
			modelAndView.addObject("flimittrade", flimittrade);
		}
		
		List<Fvirtualcointype> fvirtualcointypes = this.virtualCoinService.findAll();
		modelAndView.addObject("fvirtualcointypes", fvirtualcointypes);
		return modelAndView;
	}
	
	@RequestMapping("ssadmin/saveLimittrade")
	public ModelAndView saveLimittrade() throws Exception{
		ModelAndView modelAndView = new ModelAndView() ;
		modelAndView.setViewName("ssadmin/comm/ajaxDone") ;
		
		String vid = request.getParameter("vid");
		Fvirtualcointype virtualcointype = this.virtualCoinService.findById(vid);
		String filter = "where fvirtualcointype.fid='"+vid+"'";
		int count = this.adminService.getAllCount("Flimittrade", filter);
		if(count >0){
			modelAndView.addObject("statusCode",300);
			modelAndView.addObject("message","此币种已存在记录");
			return modelAndView;
		}
		Flimittrade limittrade = new Flimittrade();
		limittrade.setFvirtualcointype(virtualcointype);
		limittrade.setFpercent(Double.valueOf(request.getParameter("fpercent")));
		limittrade.setFdownprice(Double.valueOf(request.getParameter("fdownprice")));
		limittrade.setFupprice(Double.valueOf(request.getParameter("fupprice")));
		this.limittradeService.saveObj(limittrade);
		
		modelAndView.addObject("statusCode",200);
		modelAndView.addObject("message","新增成功");
		modelAndView.addObject("callbackType","closeCurrent");
		return modelAndView;
	}
	
	@RequestMapping("ssadmin/deleteLimittrade")
	public ModelAndView deleteLimittrade() throws Exception{
		String fid = request.getParameter("uid");
		this.limittradeService.deleteObj(fid);
		
		ModelAndView modelAndView = new ModelAndView() ;
		modelAndView.setViewName("ssadmin/comm/ajaxDone") ;
		modelAndView.addObject("statusCode",200);
		modelAndView.addObject("message","删除成功");
		return modelAndView;
	}
	
	
	@RequestMapping("ssadmin/updateLimittrade")
	public ModelAndView updateLimittrade() throws Exception{
		ModelAndView modelAndView = new ModelAndView() ;
		modelAndView.setViewName("ssadmin/comm/ajaxDone") ;
		String fid = request.getParameter("fid");
		Flimittrade limittrade = this.limittradeService.findById(fid);
		String vid = request.getParameter("vid");
		Fvirtualcointype virtualcointype = this.virtualCoinService.findById(vid);
		String filter = "where fvirtualcointype.fid='"+vid+"' and fid <> '"+fid+"'";
		int count = this.adminService.getAllCount("Flimittrade", filter);
		if(count >0){
			modelAndView.addObject("statusCode",300);
			modelAndView.addObject("message","此币种已存在记录");
			return modelAndView;
		}
		limittrade.setFvirtualcointype(virtualcointype);
		limittrade.setFpercent(Double.valueOf(request.getParameter("fpercent")));
		limittrade.setFdownprice(Double.valueOf(request.getParameter("fdownprice")));
		limittrade.setFupprice(Double.valueOf(request.getParameter("fupprice")));
		this.limittradeService.updateObj(limittrade);
		
		modelAndView.addObject("statusCode",200);
		modelAndView.addObject("message","修改成功");
		modelAndView.addObject("callbackType","closeCurrent");
		return modelAndView;
	}
	
}
