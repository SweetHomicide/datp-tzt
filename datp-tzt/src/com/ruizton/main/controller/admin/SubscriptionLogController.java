package com.ruizton.main.controller.admin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.ditp.service.StationMailService;
import com.ruizton.main.Enum.SubStatusEnum;
import com.ruizton.main.controller.BaseController;
import com.ruizton.main.model.Fsubscription;
import com.ruizton.main.model.Fsubscriptionlog;
import com.ruizton.main.model.Fvirtualwallet;
import com.ruizton.main.model.Fwallet;
import com.ruizton.main.service.admin.AdminService;
import com.ruizton.main.service.admin.SubscriptionLogService;
import com.ruizton.main.service.admin.SubscriptionService;
import com.ruizton.main.service.admin.SystemArgsService;
import com.ruizton.main.service.admin.WalletService;
import com.ruizton.main.service.front.FrontUserService;
import com.ruizton.util.Utils;

@Controller
public class SubscriptionLogController extends BaseController {
	@Autowired
	private SubscriptionLogService subscriptionLogService;
	@Autowired
	private SubscriptionService subscriptionService;
	@Autowired
	private AdminService adminService ;
	@Autowired
	private HttpServletRequest request ;
	@Autowired
	private FrontUserService frontUserService ;
	@Autowired
	private WalletService walletService ;
	@Autowired
	private SystemArgsService systemArgsService;
	@Autowired
	private StationMailService stationMailService;
	//每页显示多少条数据
	private int numPerPage = 500;

	
	@RequestMapping("/ssadmin/subscriptionLogList")
	public ModelAndView subscriptionLogList() throws Exception{
		ModelAndView modelAndView = new ModelAndView() ;
		modelAndView.setViewName("ssadmin/subscriptionLogList") ;
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
			filter.append("and (fuser.floginName like '%"+ keyWord+"%' OR \n");
			filter.append("fuser.fnickName like '%"+ keyWord+"%' OR \n");
			filter.append("fuser.frealName like '%"+ keyWord+"%' )\n");
			modelAndView.addObject("keywords", keyWord);
		}
		String sparentId=request.getParameter("parentId");
		if(request.getParameter("parentId") != null){
			String parentId = request.getParameter("parentId");
			filter.append("and fsubscription.fid='"+parentId+"'\n");
			modelAndView.addObject("parentId", parentId);
		}
		
		Map<Integer,String> typeMap = new HashMap<Integer,String>();
		typeMap.put(0, "全部");
		typeMap.put(1,SubStatusEnum.getEnumString(1));
		typeMap.put(2,SubStatusEnum.getEnumString(2));
		typeMap.put(3,SubStatusEnum.getEnumString(3));
		modelAndView.addObject("typeMap", typeMap);
		
		if(request.getParameter("ftype") != null && request.getParameter("ftype").trim().length() >0){
			int type = Integer.parseInt(request.getParameter("ftype"));
			if(type != 0){
				filter.append("and fstatus="+request.getParameter("ftype")+" \n");
			}
			modelAndView.addObject("ftype", request.getParameter("ftype"));
		}else{
			modelAndView.addObject("ftype", 0);
		}
		
		if(orderField != null && orderField.trim().length() >0){
			filter.append("order by "+orderField+"\n");
		}
		if(orderDirection != null && orderDirection.trim().length() >0){
			filter.append(orderDirection+"\n");
		}
		List<Fsubscriptionlog> list = this.subscriptionLogService.list((currentPage-1)*numPerPage, numPerPage, filter+"",true);
		
		
		
		double a = this.adminService.getSQLValue2("SELECT SUM(frate) from Fsubscriptionlog "+filter);
		double b = this.adminService.getSQLValue2("SELECT SUM(frate*fprice) from Fsubscriptionlog "+filter);
		double c = this.adminService.getSQLValue2("SELECT SUM(frate*foneqty) from Fsubscriptionlog "+filter);
		modelAndView.addObject("a", a);
		modelAndView.addObject("b", b);
		modelAndView.addObject("c", c);
		
		
		modelAndView.addObject("subscriptionLogList", list);
		modelAndView.addObject("numPerPage", numPerPage);
		modelAndView.addObject("currentPage", currentPage);
		modelAndView.addObject("rel", "subscriptionLogList");
		//总数量
		modelAndView.addObject("totalCount",this.adminService.getAllCount("Fsubscriptionlog", filter+""));
		return modelAndView ;
	}
	
	@RequestMapping("/ssadmin/subscriptionLogList1")
	public ModelAndView subscriptionLogList1() throws Exception{
		ModelAndView modelAndView = new ModelAndView() ;
		modelAndView.setViewName("ssadmin/subscriptionLogList1") ;
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
			filter.append("and (fuser.floginName like '%"+ keyWord+"%' OR \n");
			filter.append("fuser.fnickName like '%"+ keyWord+"%' OR \n");
			filter.append("fuser.frealName like '%"+ keyWord+"%' )\n");
			modelAndView.addObject("keywords", keyWord);
		}
		
		if(request.getParameter("parentId") != null){
			String parentId = request.getParameter("parentId");
			filter.append("and fsubscription.fid='"+parentId+"'\n");
			modelAndView.addObject("parentId", parentId);
		}
		
		if(orderField != null && orderField.trim().length() >0){
			filter.append("order by "+orderField+"\n");
		}
		if(orderDirection != null && orderDirection.trim().length() >0){
			filter.append(orderDirection+"\n");
		}
		List<Fsubscriptionlog> list = this.subscriptionLogService.list((currentPage-1)*numPerPage, numPerPage, filter+"",true);
		modelAndView.addObject("subscriptionLogList1", list);
		modelAndView.addObject("numPerPage", numPerPage);
		modelAndView.addObject("currentPage", currentPage);
		modelAndView.addObject("rel", "subscriptionLogList1");
		//总数量
		modelAndView.addObject("totalCount",this.adminService.getAllCount("Fsubscriptionlog", filter+""));
		return modelAndView ;
	}

	@RequestMapping("ssadmin/subSuccess")
	public ModelAndView subSuccess() throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		String ids = request.getParameter("ids");
		String[] idString = ids.split(",");
		
		for(int i=0;i<idString.length;i++){
			String id = idString[i];
            Fsubscriptionlog log = this.subscriptionLogService.findById(id);
            Fsubscription sub = this.subscriptionService.findById(log.getFsubscription().getFid());
            if(log.getFstatus() != SubStatusEnum.INIT) {
            	continue;
            }
            if(log.getFrate() >0){
            	log.setFstatus(SubStatusEnum.YES);
                double qty = log.getFrate();//预设值中签份数
                log.setFlastcount(qty);//中签份数
                sub.setFqty(sub.getFqty()+qty);//中签总份数
                log.setFlastqty(qty*log.getFoneqty());//中签总数量=中签份数*每份数量
            }else{
            	log.setFstatus(SubStatusEnum.NO);
            }
            
            try {
				this.subscriptionLogService.updateChargeLog(log,sub);
			} catch (Exception e) {
				continue;
			}
		}

		modelAndView.setViewName("ssadmin/comm/ajaxDone");
		modelAndView.addObject("statusCode", 200);
		modelAndView.addObject("message", "批量操作成功");
		return modelAndView;
	}
	

	@RequestMapping("ssadmin/subFail")
	public ModelAndView subFail() throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("ssadmin/comm/ajaxDone");
		String ids = request.getParameter("ids");
		String[] idString = ids.split(",");
		double subSuccessRate = Double.valueOf(this.systemArgsService.getValue("subSuccessRate"));
		for(int i=0;i<idString.length;i++){
			String id = idString[i];
            Fsubscriptionlog log = this.subscriptionLogService.findById(id);
            if(log.getFsubscription().isFisICO()){
            	modelAndView.addObject("statusCode", 300);
        		modelAndView.addObject("message", "ICO模式，直接审核，按众筹金额平均分！");
        		return modelAndView;
            }
            //getFcount众筹份数
            if(log.getFcount() < subSuccessRate){
            	continue;
            } 
            	
            log.setFrate(subSuccessRate);
            
            
            if(log.getFstatus() != SubStatusEnum.INIT) continue;
            
            String fuserid = log.getFuser().getFid();
            //虚拟币钱包
            Fvirtualwallet v = this.frontUserService.findVirtualWalletByUser(fuserid, log.getFsubscription().getFvirtualcointype().getFid());
            //钱包
            Fwallet w = this.walletService.findById(log.getFuser().getFwallet().getFid());
            
            try {
				this.subscriptionLogService.updateChargeLog(log, w, v);
			} catch (Exception e) {
				continue;
			}

		}

		
		modelAndView.addObject("statusCode", 200);
		modelAndView.addObject("message", "批量操作成功");
		return modelAndView;
	}
	
	@RequestMapping("ssadmin/subAudit")
	public ModelAndView subAudit() throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		String uid = request.getParameter("uid");
		Fsubscription sub = this.subscriptionService.findById(uid);
		double rate = Utils.getDouble(sub.getFtotal()/sub.getfAlreadyByCount(), 6);
		String filter = "where fsubscription.fid='"+uid+"'";
        List<Fsubscriptionlog> logs = this.subscriptionLogService.list(0, 0, filter, false);
        for (Fsubscriptionlog fsubscriptionlog : logs) {
        	double count = Utils.getDouble(fsubscriptionlog.getFcount()*rate, 4);
        	double qty = Utils.getDouble(count*fsubscriptionlog.getFoneqty(), 4);
        	double price = Utils.getDouble(fsubscriptionlog.getFcount()/count, 6);
        	fsubscriptionlog.setFrate(count);
        	fsubscriptionlog.setFprice(price);
        	fsubscriptionlog.setFlastcount(count);
        	fsubscriptionlog.setFlastqty(qty);
        	
            
            String fuserid = fsubscriptionlog.getFuser().getFid();
            Fvirtualwallet v = this.frontUserService.findVirtualWalletByUser(fuserid, fsubscriptionlog.getFsubscription().getFvirtualcointype().getFid());
            
            Fwallet w = this.walletService.findById(fsubscriptionlog.getFuser().getFwallet().getFid());
            
            try {
				this.subscriptionLogService.updateChargeLog(fsubscriptionlog, w, v);
			} catch (Exception e) {
				continue;
			}

		}

		modelAndView.setViewName("ssadmin/comm/ajaxDone");
		modelAndView.addObject("statusCode", 200);
		modelAndView.addObject("message", "批量操作成功");
		return modelAndView;
	}
	

	@RequestMapping("ssadmin/subSend")
	public ModelAndView subSend() throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		String ids = request.getParameter("ids");
		String[] idString = ids.split(",");
		for(int i=0;i<idString.length;i++){
			String id = idString[i];
            Fsubscriptionlog log = this.subscriptionLogService.findById(id);
            if(log.isFissend() || log.getFstatus() == SubStatusEnum.INIT) continue;
            log.setFissend(true);
            String fuserid = log.getFuser().getFid();
            Fvirtualwallet v = this.frontUserService.findVirtualWalletByUser(fuserid, log.getFsubscription().getFvirtualcointype().getFid());
            
            Fvirtualwallet v1 = null;
            Fwallet w1 = null;
            double lastValue=0;//需要返回的交易额
            if(log.getFsubscription().getFvirtualcointypeCost() == null){
            	w1 = this.walletService.findById(log.getFuser().getFwallet().getFid());
                if(log.getFstatus() == SubStatusEnum.YES){
                	v.setFtotal(v.getFtotal()+log.getFlastqty());
                }
                //last需要返回的交易额=交易额-中签数量*购买价
                double last = log.getFtotalCost()-log.getFlastcount()*log.getFprice();
                lastValue=last;
                //钱包冻结总额=冻结总额-交易额
            	w1.setFfrozenRmb(w1.getFfrozenRmb()-log.getFtotalCost());//getFtotalCost交易额
            	//钱包总额=钱包总额+返回的交易额（未中签的交易额）
            	w1.setFtotalRmb(w1.getFtotalRmb()+last);
            }else{
            	v1 = this.frontUserService.findVirtualWalletByUser(fuserid, log.getFsubscription().getFvirtualcointypeCost().getFid());
            	if(log.getFstatus() == SubStatusEnum.YES){
                	v.setFtotal(v.getFtotal()+log.getFlastqty());
                }
            	double last = log.getFtotalCost()-log.getFlastcount()*log.getFprice();
            	lastValue=last;
            	v1.setFfrozen(v1.getFfrozen()-log.getFtotalCost());
            	v1.setFtotal(v1.getFtotal()+last);
            }
            
            try {
				this.subscriptionLogService.updateChargeLog(log,w1,v1,v);
				//发送站内信
				stationMailService.sendStationMail(lastValue, log);
			} catch (Exception e) {
				continue;
			}
		}

		modelAndView.setViewName("ssadmin/comm/ajaxDone");
		modelAndView.addObject("statusCode", 200);
		modelAndView.addObject("message", "批量操作成功");
		return modelAndView;
	}
}
