package com.ruizton.main.controller.admin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.ruizton.main.controller.BaseController;
import com.ruizton.main.model.FbankinfoWithdraw;
import com.ruizton.main.model.FvirtualaddressWithdraw;
import com.ruizton.main.model.Fvirtualcointype;
import com.ruizton.main.service.admin.AdminService;
import com.ruizton.main.service.admin.BankinfoWithdrawService;
import com.ruizton.main.service.admin.VirtualCoinService;
import com.ruizton.main.service.admin.VirtualaddressWithdrawService;
import com.ruizton.util.Utils;

@Controller
public class BankinfoWithdrawController extends BaseController {
	@Autowired
	private BankinfoWithdrawService bankinfoWithdrawService;
	@Autowired
	private AdminService adminService ;
	@Autowired
	private VirtualCoinService virtualCoinService ;
	@Autowired
	private HttpServletRequest request ;
	@Autowired
	private VirtualaddressWithdrawService virtualaddressWithdrawService;
	
	//每页显示多少条数据
	private int numPerPage = Utils.getNumPerPage();
	
	@RequestMapping("/ssadmin/bankinfoWithdrawList")
	public ModelAndView bankinfoWithdrawList() throws Exception{
		ModelAndView modelAndView = new ModelAndView() ;
		modelAndView.setViewName("ssadmin/bankinfoWithdrawList") ;
		//当前页
		int currentPage = 1;
		//搜索关键字
		String address = request.getParameter("address");
		String keyWord = request.getParameter("keywords");
		String orderField = request.getParameter("orderField");
		String orderDirection = request.getParameter("orderDirection");
		if(request.getParameter("pageNum") != null){
			currentPage = Integer.parseInt(request.getParameter("pageNum"));
		}
		StringBuffer filter = new StringBuffer();
		filter.append("where 1=1 \n");
		if(keyWord != null && keyWord.trim().length() >0){				
				filter.append("and (fuser.floginName like '%" + keyWord
						+ "%' OR \n");
				filter.append("fuser.fid like '%" + keyWord + "%' OR \n");
				filter.append("fuser.fnickName like '%" + keyWord + "%' OR \n");
				filter.append("fuser.frealName like '%" + keyWord + "%' ) \n");
			
			modelAndView.addObject("keywords", keyWord);
		}
		
		if(address != null && address.trim().length() >0){
			filter.append(" and fbankNumber like '%"+address+"%' \n");
			modelAndView.addObject("address", address);
		}
		
		if(orderField != null && orderField.trim().length() >0){
			filter.append("order by "+orderField+"\n");
		}else{
			filter.append("order by fid \n");
		}
		if(orderDirection != null && orderDirection.trim().length() >0){
			filter.append(orderDirection+"\n");
		}else{
			filter.append("desc \n");
		}
		
		List<FbankinfoWithdraw> list = this.bankinfoWithdrawService.list((currentPage-1)*numPerPage, numPerPage, filter+"",true);
		modelAndView.addObject("bankinfoWithdrawList", list);
		modelAndView.addObject("numPerPage", numPerPage);
		modelAndView.addObject("currentPage", currentPage);
		modelAndView.addObject("rel", "bankinfoWithdrawList");
		//总数量
		modelAndView.addObject("totalCount",this.adminService.getAllCount("FbankinfoWithdraw", filter+""));
		return modelAndView ;
	}
	
	/**
	 * 会员管理-会员虚拟币地址列表获取
	 *  作者：           Dylan
	 *  标题：           virtualaddressWithdrawList 
	 *  时间：           2018年8月15日
	 *  描述：           
	 *  
	 *  @return
	 *  @throws Exception
	 */
	@RequestMapping("/ssadmin/virtualaddressWithdrawList")
	public ModelAndView virtualaddressWithdrawList() throws Exception{
		ModelAndView modelAndView = new ModelAndView() ;
		modelAndView.setViewName("ssadmin/virtualaddressWithdrawList") ;
		//当前页
		int currentPage = 1;
		//搜索关键字
		String address = request.getParameter("address");
		String keyWord = request.getParameter("keywords");
		String orderField = request.getParameter("orderField");
		String orderDirection = request.getParameter("orderDirection");
		if(request.getParameter("pageNum") != null){
			currentPage = Integer.parseInt(request.getParameter("pageNum"));
		}
		StringBuffer filter = new StringBuffer();
		filter.append("where 1=1 \n");
		if(keyWord != null && keyWord.trim().length() >0){
				filter.append("and (fuser.floginName like '%" + keyWord
						+ "%' OR \n");
				filter.append("fuser.fid like '%" + keyWord + "%' OR \n");
				filter.append("fuser.fnickName like '%" + keyWord + "%' OR \n");
				filter.append("fuser.frealName like '%" + keyWord + "%' ) \n");
		}
		String ftype = request.getParameter("ftype");
		if (StringUtils.isNotBlank(ftype)) {
			filter.append("and fvirtualcointype.fid='" + ftype + "'\n");
		} else {
			ftype = "";
		}
		modelAndView.addObject("ftype", ftype);
		
		if(address != null && address.trim().length() >0){
			filter.append(" and fadderess like '%"+address+"%' \n");
			modelAndView.addObject("address", address);
		}
		
		if(orderField != null && orderField.trim().length() >0){
			filter.append("order by "+orderField+"\n");
		}else{
			filter.append("order by fcreateTime \n");
		}
		if(orderDirection != null && orderDirection.trim().length() >0){
			filter.append(orderDirection+"\n");
		}else{
			filter.append("desc \n");
		}
		
		List<Fvirtualcointype> type = this.virtualCoinService.findAll();
		Map<String,String> typeMap = new HashMap<String,String>();
		for (Fvirtualcointype fvirtualcointype : type) {
			typeMap.put(fvirtualcointype.getFid(), fvirtualcointype.getFname());
		}
		typeMap.put("0", "全部");
		modelAndView.addObject("typeMap", typeMap);
		
		List<FvirtualaddressWithdraw> list = this.virtualaddressWithdrawService.list((currentPage-1)*numPerPage, numPerPage, filter+"",true);
		modelAndView.addObject("virtualaddressWithdrawList", list);
		modelAndView.addObject("numPerPage", numPerPage);
		modelAndView.addObject("currentPage", currentPage);
		modelAndView.addObject("rel", "virtualaddressWithdrawList");
		//总数量
		modelAndView.addObject("totalCount",this.adminService.getAllCount("FvirtualaddressWithdraw", filter+""));
		return modelAndView ;
	}
}
