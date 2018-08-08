package com.ruizton.main.controller.admin;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.security.MessageDigest;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.ruizton.main.Enum.EntrustStatusEnum;
import com.ruizton.main.Enum.EntrustTypeEnum;
import com.ruizton.main.comm.MultipleValues;
import com.ruizton.main.controller.BaseController;
import com.ruizton.main.model.Fentrust;
import com.ruizton.main.model.Fvirtualcaptualoperation;
import com.ruizton.main.model.Fvirtualcointype;
import com.ruizton.main.service.admin.AdminService;
import com.ruizton.main.service.admin.EntrustService;
import com.ruizton.main.service.admin.EntrustlogService;
import com.ruizton.main.service.admin.VirtualCapitaloperationService;
import com.ruizton.main.service.admin.VirtualCoinService;
import com.ruizton.main.service.front.UtilsService;

@Controller
public class CoinCheckController extends BaseController {
	@Autowired
	private UtilsService utilsService ;
	@Autowired
	private HttpServletRequest request ;
	//每页显示多少条数据
	private int numPerPage = 500;
	@Autowired
	private VirtualCoinService virtualCoinService ;
	@Autowired
	private VirtualCapitaloperationService virtualCapitaloperationService ;
	@Autowired
	private AdminService adminService ;
	@Autowired
	private EntrustService entrustService ;
	
	//通过币地址查找用户
	@RequestMapping("/ssadmin/findFuserByAddress")
	public ModelAndView Index() throws Exception{

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("ssadmin/findFuserByAddress");
		// 当前页
		int currentPage = 1;
		// 搜索关键字
		String keyWord = request.getParameter("keywords");
		String orderField = request.getParameter("orderField");
		String orderDirection = request.getParameter("orderDirection");
		if (request.getParameter("pageNum") != null) {
			currentPage = Integer.parseInt(request.getParameter("pageNum"));
		}
		StringBuffer filterSQL = new StringBuffer();
		filterSQL.append("where 1=1 \n");
		if (keyWord != null && keyWord.trim().length() > 0) {
			filterSQL.append("and (fuser.floginName like '%" + keyWord
					+ "%' OR \n");
			filterSQL.append("fuser.fnickName like '%" + keyWord + "%' OR \n");
			filterSQL.append("fuser.frealName like '%" + keyWord + "%' OR \n");
			filterSQL.append("fuser.fid like '%" + keyWord + "%' OR \n");
			filterSQL.append("withdraw_virtual_address like '%" + keyWord
					+ "%' OR \n");
			filterSQL.append("recharge_virtual_address like '%" + keyWord
					+ "%' )\n");
			modelAndView.addObject("keywords", keyWord);
		}

		if (request.getParameter("ftype") != null) {
			String type = request.getParameter("ftype");
			if (type != null) {
				filterSQL.append("and fvirtualcointype.fid='" + type + "'\n");
			}
			modelAndView.addObject("ftype", type);
		} else {
			modelAndView.addObject("ftype", "0");
		}
		filterSQL.append("and fuser.fid is not null \n");

		if (orderField != null && orderField.trim().length() > 0) {
			filterSQL.append("order by " + orderField + "\n");
		} else {
			filterSQL.append("order by fcreateTime \n");
		}
		if (orderDirection != null && orderDirection.trim().length() > 0) {
			filterSQL.append(orderDirection + "\n");
		} else {
			filterSQL.append("desc \n");
		}

		List<Fvirtualcointype> type = this.virtualCoinService.findAll();
		HashMap<String, String> typeMap = new HashMap<String,String>();
		for (Fvirtualcointype fvirtualcointype : type) {
			typeMap.put(fvirtualcointype.getFid(), fvirtualcointype.getFname());
		}
		typeMap.put("0", "全部");
		modelAndView.addObject("typeMap", typeMap);

		List<Fvirtualcaptualoperation> list = this.virtualCapitaloperationService
				.list((currentPage - 1) * numPerPage, numPerPage, filterSQL
						+ "", true);
		modelAndView.addObject("list", list);
		modelAndView.addObject("numPerPage", numPerPage);
		modelAndView.addObject("currentPage", currentPage);
		modelAndView.addObject("rel", "findFuserByAddress");
		// 总数量
		modelAndView.addObject("totalCount", this.adminService.getAllCount(
				"Fvirtualcaptualoperation", filterSQL + ""));
		return modelAndView;
	}
	
	//通过价格查找用户
	@RequestMapping("/ssadmin/findFuserByFentrustPrice")
	public ModelAndView findFuserByFentrustPrice() throws Exception{
		ModelAndView modelAndView = new ModelAndView() ;
		modelAndView.setViewName("ssadmin/entrustList") ;
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
			filter.append("and (fuser.floginName like '%"+keyWord+"%' OR \n");
			filter.append("fuser.frealName like '%"+keyWord+"%' OR \n");
			filter.append("fuser.fid like '%" + keyWord + "%' OR \n");
			filter.append("fuser.fnickName like '%"+keyWord+"%' ) \n");
			modelAndView.addObject("keywords", keyWord);
		}
		
		if(request.getParameter("ftype") != null){
			String type = request.getParameter("ftype");
			if(type != null){
				filter.append("and fvirtualcointype.fid='"+type+"'\n");
			}
			modelAndView.addObject("ftype", type);
		}else{
			modelAndView.addObject("ftype", "0");
		}
		
		String status = request.getParameter("status");
		if(status != null && status.trim().length() >0){
			if(!status.equals("0")){
				filter.append("and fstatus="+status+" \n");
			}
			modelAndView.addObject("status", status);
		}else{
			modelAndView.addObject("status", 0);
		}
		
		String entype = request.getParameter("entype");
		if(entype != null && entype.trim().length() >0){
			if(!entype.equals("-1")){
				filter.append("and fentrustType="+entype+" \n");
			}
			modelAndView.addObject("entype", entype);
		}else{
			modelAndView.addObject("entype", -1);
		}
		
		try {
			String price = request.getParameter("price");
			if(price != null && price.trim().length() >0){
				double p = Double.valueOf(price);
				filter.append("and fprize="+p+" \n");
			}
			modelAndView.addObject("price", price);
		} catch (Exception e) {
		}
		
		String logDate = request.getParameter("logDate");
		if(logDate != null && logDate.trim().length() >0){
			filter.append("and  DATE_FORMAT(fcreateTime,'%Y-%m-%d') = '"+logDate+"' \n");
			modelAndView.addObject("logDate", logDate);
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
		HashMap<String, String> typeMap = new HashMap<String,String>();
		for (Fvirtualcointype fvirtualcointype : type) {
			typeMap.put(fvirtualcointype.getFid(), fvirtualcointype.getFname());
		}
		typeMap.put("0", "全部");
		modelAndView.addObject("typeMap", typeMap);
		
		Map statusMap = new HashMap();
		statusMap.put(EntrustStatusEnum.AllDeal, EntrustStatusEnum.getEnumString(EntrustStatusEnum.AllDeal));
		statusMap.put(EntrustStatusEnum.Cancel, EntrustStatusEnum.getEnumString(EntrustStatusEnum.Cancel));
		statusMap.put(EntrustStatusEnum.Going, EntrustStatusEnum.getEnumString(EntrustStatusEnum.Going));
		statusMap.put(EntrustStatusEnum.PartDeal, EntrustStatusEnum.getEnumString(EntrustStatusEnum.PartDeal));
		statusMap.put(0,"全部");
		modelAndView.addObject("statusMap", statusMap);
		
		Map entypeMap = new HashMap();
		entypeMap.put(EntrustTypeEnum.BUY, EntrustTypeEnum.getEnumString(EntrustTypeEnum.BUY));
		entypeMap.put(EntrustTypeEnum.SELL, EntrustTypeEnum.getEnumString(EntrustTypeEnum.SELL));
		entypeMap.put(-1,"全部");
		modelAndView.addObject("entypeMap", entypeMap);
		
		
		List<Fentrust> list = this.entrustService.list((currentPage-1)*numPerPage, numPerPage, filter+"",true);
		modelAndView.addObject("entrustList", list);
		modelAndView.addObject("numPerPage", numPerPage);
		modelAndView.addObject("rel", "entrustList");
		modelAndView.addObject("currentPage", currentPage);
		//总数量
		modelAndView.addObject("totalCount",this.adminService.getAllCount("Fentrust", filter+""));
		return modelAndView ;
	}
		
		
		
}
