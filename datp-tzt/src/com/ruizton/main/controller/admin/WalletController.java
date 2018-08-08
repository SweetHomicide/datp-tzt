package com.ruizton.main.controller.admin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.ruizton.main.controller.BaseController;
import com.ruizton.main.model.Fcapitaloperation;
import com.ruizton.main.model.Fuser;
import com.ruizton.main.model.Fwallet;
import com.ruizton.main.service.admin.AdminService;
import com.ruizton.main.service.admin.CapitaloperationService;
import com.ruizton.main.service.admin.UserService;
import com.ruizton.main.service.admin.WalletService;
import com.ruizton.util.Utils;

@Controller
public class WalletController extends BaseController {
	@Autowired
	private WalletService walletService ;
	@Autowired
	private AdminService adminService ;
	@Autowired
	private UserService userService ;
	@Autowired
	private CapitaloperationService capitaloperationService;
	@Autowired
	private HttpServletRequest request ;
	//每页显示多少条数据
	private int numPerPage = Utils.getNumPerPage();
	
	@RequestMapping("/ssadmin/walletList")
	public ModelAndView Index() throws Exception{
		ModelAndView modelAndView = new ModelAndView() ;
		modelAndView.setViewName("ssadmin/walletList") ;
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
		if(keyWord != null && keyWord.trim().length() >0){
			filter.append("and (floginName like '%"+keyWord+"%' or \n");
			filter.append("fnickName like '%"+keyWord+"%' or \n");
			filter.append("fuser.fid like '%"+keyWord+"%' OR \n");
			filter.append("frealName like '%"+keyWord+"%' ) \n");
			modelAndView.addObject("keywords", keyWord);
		}
		if(orderField != null && orderField.trim().length() >0){
			filter.append("order by "+orderField+"\n");
		}else{
			filter.append("order by fregisterTime \n");
		}
		if(orderDirection != null && orderDirection.trim().length() >0){
			filter.append(orderDirection+"\n");
		}else{
			filter.append("desc \n");
		}
		List<Fuser> list = this.userService.list((currentPage-1)*numPerPage, numPerPage, filter+"",true);
		modelAndView.addObject("userList", list);
		modelAndView.addObject("numPerPage", numPerPage);
		modelAndView.addObject("currentPage", currentPage);
		modelAndView.addObject("rel", "walletList");
		//总数量
		modelAndView.addObject("totalCount",this.adminService.getAllCount("Fuser", filter+""));
		return modelAndView ;
	}
	
	@RequestMapping("/ssadmin/viewUserWallet")
	public ModelAndView viewUserWallet() throws Exception{
		ModelAndView modelAndView = new ModelAndView() ;
		modelAndView.setViewName("ssadmin/viewUserWallet") ;
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
		if(keyWord != null && keyWord.trim().length() >0){
			filter.append("and (floginName like '%"+keyWord+"%' or \n");
			filter.append("fnickName like '%"+keyWord+"%' or \n");
			filter.append("frealName like '%"+keyWord+"%' ) \n");
			modelAndView.addObject("keywords", keyWord);
		}
		if(request.getParameter("cid") != null){
			String cid = request.getParameter("cid");
			Fcapitaloperation c = this.capitaloperationService.findById(cid);
			filter.append("and fid ="+c.getFuser().getFid()+" \n");
			modelAndView.addObject("cid",cid);
		}
		if(orderField != null && orderField.trim().length() >0){
			filter.append("order by "+orderField+"\n");
		}else{
			filter.append("order by fregisterTime \n");
		}
		if(orderDirection != null && orderDirection.trim().length() >0){
			filter.append(orderDirection+"\n");
		}else{
			filter.append("desc \n");
		}
		List<Fuser> list = this.userService.list((currentPage-1)*numPerPage, numPerPage, filter+"",true);
		modelAndView.addObject("userList", list);
		modelAndView.addObject("numPerPage", numPerPage);
		modelAndView.addObject("currentPage", currentPage);
		modelAndView.addObject("rel", "viewUserWallet");
		//总数量
		modelAndView.addObject("totalCount",this.adminService.getAllCount("Fuser", filter+""));
		return modelAndView ;
	}

}
