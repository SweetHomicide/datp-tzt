package com.ruizton.main.controller.front;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.ruizton.main.Enum.SubscriptionTypeEnum;
import com.ruizton.main.controller.BaseController;
import com.ruizton.main.model.Fsubscription;
import com.ruizton.main.model.Fsubscriptionlog;
import com.ruizton.main.model.Fuser;
import com.ruizton.main.model.Fvirtualwallet;
import com.ruizton.main.service.admin.AdminService;
import com.ruizton.main.service.admin.SubscriptionLogService;
import com.ruizton.main.service.admin.SubscriptionService;
import com.ruizton.main.service.admin.UserService;
import com.ruizton.main.service.front.FrontTradeService;
import com.ruizton.main.service.front.FrontUserService;
import com.ruizton.util.Comm;
import com.ruizton.util.Constant;
import com.ruizton.util.Utils;

@Controller
public class FrontZhongChouController extends BaseController {
	@Autowired
	private FrontTradeService frontTradeService ;
	@Autowired
	private SubscriptionLogService subscriptionLogService;
	@Autowired
	private SubscriptionService subscriptionService;
	@Autowired
	private AdminService adminService;
	@Autowired
	private UserService userSerivce;
	@Autowired
	private FrontUserService frontUserService;

	@RequestMapping("/crowd/index")
	public ModelAndView index(
                 HttpServletRequest request,
                 @RequestParam(required=false,defaultValue="1")int currentPage,
                 @RequestParam(required=true,defaultValue="1")int type
			) throws Exception{
		ModelAndView modelAndView = new ModelAndView() ;
		Timestamp time = Utils.getTimestamp();
		int pageSize = 10;
		String status = "";
		String filter = "where ftype="+SubscriptionTypeEnum.RMB;
		if (type==0) {
			status = "未开始";
			filter+=" and FbeginTime > '"+time.toString()+"'";
			modelAndView.setViewName("front/zhongchou/index") ;
		}else if(type==1) {
			status = "众筹中";
			filter+=" and FbeginTime < '"+time.toString()+"' and FendTime > '"+time.toString()+"'" ;
			modelAndView.setViewName("front/zhongchou/index2") ;
		}else{
			status = "已结束";
			filter+=" and FendTime < '"+time.toString()+"'" ;
			modelAndView.setViewName("front/zhongchou/index3") ;
		}
		filter+="  order by fcreateTime desc";
		String countSize = "select count(*) from Fsubscription "+filter;
		int count = this.subscriptionService.selectCount(countSize);
		String pagin = this.generatePagin(count / pageSize + ((count % pageSize) == 0 ? 0 : 1),
				Integer.valueOf(currentPage),"/crowd/index.html?type="+type+"&");
		modelAndView.addObject("page", pagin);
		List<Fsubscription> fsubscriptions = this.subscriptionService.list((Integer.valueOf(currentPage) - 1) * pageSize, pageSize, filter, true);
		if(fsubscriptions == null || fsubscriptions.size() ==0){
			/*modelAndView.setViewName("redirect:/") ;*/
			modelAndView.addObject("type", type) ;
			modelAndView.addObject("msg", "未发起众筹，请等待") ;
			modelAndView.setViewName("front/blank") ;
			return modelAndView ;
		}
		for (int i = 0; i < fsubscriptions.size(); i++) {
			fsubscriptions.get(i).setFstatus(status);
		}
		modelAndView.addObject("fsubscriptions", fsubscriptions);
		return modelAndView ;
		/*if(type==0){
			modelAndView.addObject("fsubscriptions", fsubscriptions);
			modelAndView.setViewName("front/zhongchou/index") ;
		}else if(type==1){
			modelAndView.addObject("fsubscriptions", fsubscriptions);
			modelAndView.setViewName("front/zhongchou/index2") ;
		}else if(type==2){
			modelAndView.addObject("fsubscriptions", fsubscriptions);
			modelAndView.setViewName("front/zhongchou/index3") ;
		}*/
		/*List<Fsubscription> fsubscriptions1 = new ArrayList<Fsubscription>();
		List<Fsubscription> fsubscriptions2 = new ArrayList<Fsubscription>();
		List<Fsubscription> fsubscriptions3 = new ArrayList<Fsubscription>();*/
		//long now = time.getTime() ;
		/*for (Fsubscription fsubscription : fsubscriptions) {
			String status = "";
			if(fsubscription.getFbeginTime().getTime()>now){
				status = "未开始";
				fsubscription.setFstatus(status);
				fsubscriptions1.add(fsubscription);
			}
			if(fsubscription.getFbeginTime().getTime()<now && fsubscription.getFendTime().getTime()>now){
				status = "众筹中";
				fsubscription.setFstatus(status);
				fsubscriptions2.add(fsubscription);
			}
			if(fsubscription.getFendTime().getTime()<now){
				status = "已结束";
				fsubscription.setFstatus(status);
				fsubscriptions3.add(fsubscription);
			}
		}*/
	}
	
	@RequestMapping("/crowd/view")
	public ModelAndView view(
                 HttpServletRequest request,
                 @RequestParam(required=true,defaultValue="0")String fid
			) throws Exception{
        ModelAndView modelAndView = new ModelAndView() ;
		
		Fsubscription fsubscription = this.frontTradeService.findFsubscriptionById(fid) ;
		if(fsubscription==null || fsubscription.getFtype() !=SubscriptionTypeEnum.RMB){
			modelAndView.setViewName("redirect:/crowd/index.html") ;
			return modelAndView ;
		}
		
		String status = "";
		long now = Utils.getTimestamp().getTime() ;
		if(fsubscription.getFbeginTime().getTime()>now){
			status = "未开始";
		}
		if(fsubscription.getFbeginTime().getTime()<now && fsubscription.getFendTime().getTime()>now){
			status = "众筹中";
		}
		if(fsubscription.getFendTime().getTime()<now){
			status = "已结束";
		}
		fsubscription.setFstatus(status);
		
		long s = fsubscription.getFbeginTime().getTime()-now;
		long e = fsubscription.getFendTime().getTime()-now;

		modelAndView.addObject("s", s/1000L) ;
		modelAndView.addObject("e", e/1000L) ;
		
		String url = null;
		double totalAmt =0d;
		Fuser fuser = this.userSerivce.findById(GetSession(request).getFid());
		if(fsubscription.getFvirtualcointypeCost() == null){
			url = "/account/rechargeCny.html";
			totalAmt = fuser.getFwallet().getFtotalRmb();
		}else{
			url = "/account/rechargeBtc.html?symbol="+fsubscription.getFvirtualcointypeCost().getFid();
			Fvirtualwallet fvirtualwallet = this.frontUserService.findVirtualWalletByUser(fuser.getFid(), fsubscription.getFvirtualcointypeCost().getFid());
			totalAmt = fvirtualwallet.getFtotal();
		}
		
		modelAndView.addObject("totalAmt", totalAmt) ;
		modelAndView.addObject("rechargeUrl", url) ;
		modelAndView.addObject("fsubscription", fsubscription) ;
		modelAndView.setViewName("front/zhongchou/detail") ;
		return modelAndView ;
	}
	
	@RequestMapping("/crowd/logs")
	public ModelAndView logs(
			@RequestParam(required=false,defaultValue="1")int currentPage,
            HttpServletRequest request
			) throws Exception{
		ModelAndView modelAndView = new ModelAndView() ;
		
		String filter = "where fuser.fid='"+GetSession(request).getFid()+"' and fsubscription.ftype !='"+SubscriptionTypeEnum.COIN+"' order by fcreatetime desc";
		List<Fsubscriptionlog> subscriptionlogs = this.subscriptionLogService.list((currentPage-1)*Constant.RecordPerPage, Constant.RecordPerPage, filter, true);
		int total = this.adminService.getAllCount("Fsubscriptionlog", filter);
		String pagin = generatePagin(total/Constant.RecordPerPage+(total%Constant.RecordPerPage==0?0:1), currentPage, "/crowd/logs.html?") ;
		modelAndView.addObject("pagin", pagin) ;
		modelAndView.addObject("subscriptionlogs", subscriptionlogs) ;

		modelAndView.setViewName("front/zhongchou/logs") ;
		return modelAndView ;
	}

}
