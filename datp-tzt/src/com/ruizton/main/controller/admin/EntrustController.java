package com.ruizton.main.controller.admin;

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
import com.ruizton.main.auto.RealTimeData;
import com.ruizton.main.controller.BaseController;
import com.ruizton.main.model.Fentrust;
import com.ruizton.main.model.Fvirtualcointype;
import com.ruizton.main.service.admin.AdminService;
import com.ruizton.main.service.admin.EntrustService;
import com.ruizton.main.service.admin.VirtualCoinService;
import com.ruizton.main.service.front.FrontTradeService;
import com.ruizton.util.Utils;

@Controller
public class EntrustController extends BaseController {
	@Autowired
	private EntrustService entrustService;
	@Autowired
	private AdminService adminService ;
	@Autowired
	private VirtualCoinService virtualCoinService;
	@Autowired
	private HttpServletRequest request ;
	@Autowired
	private RealTimeData realTimeData ;
	@Autowired
	private FrontTradeService frontTradeService ;
	//每页显示多少条数据
	private int numPerPage = Utils.getNumPerPage();
	
	@RequestMapping("/ssadmin/entrustList")
	public ModelAndView Index() throws Exception{
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
				filter.append("fuser.fid like '%"+keyWord+"%' OR \n");
				filter.append("fuser.frealName like '%"+keyWord+"%' OR \n");
				filter.append("fuser.fnickName like '%"+keyWord+"%' ) \n");
			modelAndView.addObject("keywords", keyWord);
		}
		
		if(request.getParameter("ftype") != null && !("0").equals(request.getParameter("ftype"))){
			
			filter.append("and fvirtualcointype.fid='"+request.getParameter("ftype")+"'\n");
			modelAndView.addObject("ftype", request.getParameter("ftype"));

		}else{
			modelAndView.addObject("ftype", "0");
		}
		
		String status = request.getParameter("status");
		if(status != null && status.trim().length() >0 && !status.equals("0")){
			
			filter.append("and fstatus="+status+" \n");
			modelAndView.addObject("status", status);
			
		}else{
			modelAndView.addObject("status", 0);
		}
		
		String entype = request.getParameter("entype");
		if(entype != null && entype.trim().length() >0 && !("-1").equals(entype)){
			
			filter.append("and fentrustType="+entype+" \n");
			modelAndView.addObject("entype", entype);
			
		}else{
			modelAndView.addObject("entype", -1);
		}
		
		try {
			String price = request.getParameter("price");
			if(price != null && price.trim().length() >0){
				double p = Double.valueOf(price);
				filter.append("and fprize >="+p+" \n");
			}
			modelAndView.addObject("price", price);
		} catch (Exception e) {
		}
		
		try {
			String price = request.getParameter("price1");
			if(price != null && price.trim().length() >0){
				double p = Double.valueOf(price);
				filter.append("and fprize <="+p+" \n");
			}
			modelAndView.addObject("price1", price);
		} catch (Exception e) {
		}
		
		String logDate = request.getParameter("logDate");
		if(logDate != null && logDate.trim().length() >0){
			filter.append("and  DATE_FORMAT(fcreateTime,'%Y-%m-%d HH:mm:ss') >= '"+logDate+"' \n");
			modelAndView.addObject("logDate", logDate);
		}
		
		String logDate1 = request.getParameter("logDate1");
		if(logDate1 != null && logDate1.trim().length() >0){
			filter.append("and  DATE_FORMAT(fcreateTime,'%Y-%m-%d HH:mm:ss') <= '"+logDate1+"' \n");
			modelAndView.addObject("logDate1", logDate);
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
		
		List<Fvirtualcointype> type = this.virtualCoinService.findAll();
		Map<String,String> typeMap = new HashMap<String,String>();
		for (Fvirtualcointype fvirtualcointype : type) {
			typeMap.put(fvirtualcointype.getFid(), fvirtualcointype.getFname());
		}
		typeMap.put("0", "全部");
		modelAndView.addObject("typeMap", typeMap);
		
		Map<Integer,String> statusMap = new HashMap<Integer,String>();
		statusMap.put(EntrustStatusEnum.AllDeal, EntrustStatusEnum.getEnumString(EntrustStatusEnum.AllDeal));
		statusMap.put(EntrustStatusEnum.Cancel, EntrustStatusEnum.getEnumString(EntrustStatusEnum.Cancel));
		statusMap.put(EntrustStatusEnum.Going, EntrustStatusEnum.getEnumString(EntrustStatusEnum.Going));
		statusMap.put(EntrustStatusEnum.PartDeal, EntrustStatusEnum.getEnumString(EntrustStatusEnum.PartDeal));
		statusMap.put(0,"全部");
		modelAndView.addObject("statusMap", statusMap);
		
		Map<Integer,String> entypeMap = new HashMap<Integer,String>();
		entypeMap.put(EntrustTypeEnum.BUY, EntrustTypeEnum.getEnumString(EntrustTypeEnum.BUY));
		entypeMap.put(EntrustTypeEnum.SELL, EntrustTypeEnum.getEnumString(EntrustTypeEnum.SELL));
		entypeMap.put(-1,"全部");
		modelAndView.addObject("entypeMap", entypeMap);
		
		double fees = this.adminService.getSQLValue2("select sum(ffees-fleftfees) from Fentrust "+filter.toString());
		double totalAmt = this.adminService.getSQLValue2("select sum(fcount-fleftCount) from Fentrust "+filter.toString());
		double totalQty = this.adminService.getSQLValue2("select sum(fsuccessAmount) from Fentrust "+filter.toString());
		
		
		modelAndView.addObject("fees", Utils.getDouble(fees, 2));
		modelAndView.addObject("totalAmt", Utils.getDouble(totalAmt, 2));
		modelAndView.addObject("totalQty", Utils.getDouble(totalQty, 2));
		
		
		List<Fentrust> list = this.entrustService.list((currentPage-1)*numPerPage, numPerPage, filter+"",true);
		modelAndView.addObject("entrustList", list);
		modelAndView.addObject("numPerPage", numPerPage);
		modelAndView.addObject("rel", "entrustList");
		modelAndView.addObject("currentPage", currentPage);
		//总数量
		modelAndView.addObject("totalCount",this.adminService.getAllCount("Fentrust", filter+""));
		return modelAndView ;
	}
	
	
	@RequestMapping("/ssadmin/cancelEntrust")
	public ModelAndView cancelEntrust() throws Exception{
		String ids = request.getParameter("ids");
		String[] idString = ids.split(",");
		for(int i=0;i<idString.length;i++){
			try {
				int id = Integer.parseInt(idString[i]);
				String filter = "where fid="+id;
				List<Fentrust> fentrust = this.entrustService.list(0, 0, filter, false);
				for (Fentrust fentrust2 : fentrust) {
					if(fentrust2!=null
							&&(fentrust2.getFstatus()==EntrustStatusEnum.Going || fentrust2.getFstatus()==EntrustStatusEnum.PartDeal )){
						boolean flag = false ;
						try {
							this.frontTradeService.updateCancelFentrust(fentrust2, fentrust2.getFuser()) ;
							flag = true ;
						} catch (Exception e) {
							e.printStackTrace();
						}
						if(flag==true){
							if(fentrust2.getFentrustType()==EntrustTypeEnum.BUY){
								if(fentrust2.isFisLimit()){
									this.realTimeData.removeEntrustLimitBuyMap(fentrust2.getFvirtualcointype().getFid(), fentrust2) ;
								}else{
									this.realTimeData.removeEntrustBuyMap(fentrust2.getFvirtualcointype().getFid(), fentrust2) ;
								}
							}else{
								if(fentrust2.isFisLimit()){
									this.realTimeData.removeEntrustLimitSellMap(fentrust2.getFvirtualcointype().getFid(), fentrust2) ;
								}else{
									this.realTimeData.removeEntrustSellMap(fentrust2.getFvirtualcointype().getFid(), fentrust2) ;
								}
								
							}
						}
					}
				}
			} catch (Exception e) {}
		}

		ModelAndView modelAndView = new ModelAndView() ;
		modelAndView.setViewName("ssadmin/comm/ajaxDone") ;
		modelAndView.addObject("statusCode",200);
		modelAndView.addObject("message","取消成功");
		return modelAndView;
	}
	
}
