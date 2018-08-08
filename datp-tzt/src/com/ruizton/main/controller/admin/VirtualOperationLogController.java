package com.ruizton.main.controller.admin;

import java.io.InputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.ruizton.main.Enum.AdminLevelEnum;
import com.ruizton.main.Enum.OperationlogEnum;
import com.ruizton.main.Enum.RemittanceTypeEnum;
import com.ruizton.main.comm.ParamArray;
import com.ruizton.main.controller.BaseController;
import com.ruizton.main.model.Fadmin;
import com.ruizton.main.model.Foperationlog;
import com.ruizton.main.model.Fuser;
import com.ruizton.main.model.Fvirtualcointype;
import com.ruizton.main.model.Fvirtualoperationlog;
import com.ruizton.main.model.Fvirtualwallet;
import com.ruizton.main.service.admin.AdminService;
import com.ruizton.main.service.admin.OperationLogService;
import com.ruizton.main.service.admin.UserService;
import com.ruizton.main.service.admin.VirtualCoinService;
import com.ruizton.main.service.admin.VirtualOperationLogService;
import com.ruizton.main.service.admin.VirtualWalletService;
import com.ruizton.util.Constant;
import com.ruizton.util.Utils;

@Controller
public class VirtualOperationLogController extends BaseController {
	@Autowired
	private VirtualOperationLogService virtualOperationLogService;
	@Autowired
	private AdminService adminService ;
	@Autowired
	private UserService userService ;
	@Autowired
	private VirtualWalletService virtualWalletService;
	@Autowired
	private VirtualCoinService virtualCoinService ;
	@Autowired
	private HttpServletRequest request ;
	//每页显示多少条数据
	private int numPerPage = Utils.getNumPerPage();
	
	@RequestMapping("/ssadmin/virtualoperationlogList")
	public ModelAndView Index() throws Exception{
		
		ModelAndView modelAndView = new ModelAndView() ;
		modelAndView.setViewName("ssadmin/virtualoperationlogList") ;
		//当前页
		int currentPage = 1;
		//搜索关键字
		String keyWord = request.getParameter("keywords");
		String orderField = request.getParameter("orderField");
		String orderDirection = request.getParameter("orderDirection");
		String logDate = request.getParameter("logDate");
		if(request.getParameter("pageNum") != null){
			currentPage = Integer.parseInt(request.getParameter("pageNum"));
		}
		StringBuffer filter = new StringBuffer();
		filter.append("where 1=1 \n");
		if(keyWord != null && keyWord.trim().length() >0){
				filter.append("and (fuser.floginName like '%"+keyWord+"%' or \n");
				filter.append("fuser.fid like '%"+keyWord+"%' OR \n");
				filter.append("fuser.fnickName like '%"+keyWord+"%' or \n");
				filter.append("fuser.frealName like '%"+keyWord+"%' )\n");
			modelAndView.addObject("keywords", keyWord);
		}
		
		if(logDate != null && logDate.trim().length() >0){
			filter.append("and  DATE_FORMAT(fcreateTime,'%Y-%m-%d') = '"+logDate+"' \n");
			modelAndView.addObject("logDate", logDate);
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
		List<Fvirtualoperationlog> list = this.virtualOperationLogService.list((currentPage-1)*numPerPage, numPerPage, filter+"",true);
		modelAndView.addObject("virtualoperationlogList", list);
		modelAndView.addObject("numPerPage", numPerPage);
		modelAndView.addObject("rel", "operationLogList");
		modelAndView.addObject("currentPage", currentPage);
		//总数量
		modelAndView.addObject("totalCount",this.adminService.getAllCount("Fvirtualoperationlog", filter+""));
		return modelAndView ;
	}
	
	@RequestMapping("ssadmin/goVirtualOperationLogJSP")
	public ModelAndView goVirtualOperationLogJSP() throws Exception{
		
		String url = request.getParameter("url");
		ModelAndView modelAndView = new ModelAndView() ;
		if(request.getParameter("uid") != null){
			String fid = request.getParameter("uid");
			Fvirtualoperationlog virtualoperationlog = this.virtualOperationLogService.findById(fid);
			modelAndView.addObject("virtualoperationlog", virtualoperationlog);
		}
		List<Fvirtualcointype> allType = this.virtualCoinService.findAll();
		modelAndView.addObject("allType", allType);
		modelAndView.setViewName(url);
		return modelAndView;
	}
	
	@RequestMapping("ssadmin/saveVirtualOperationLog")
	public ModelAndView saveVirtualOperationLog() throws Exception{
		Fvirtualoperationlog virtualoperationlog = new Fvirtualoperationlog();
		String userId = request.getParameter("userLookup.id");
		Fuser user = this.userService.findById(userId);
		String vid = request.getParameter("vid");
		Fvirtualcointype coinType = this.virtualCoinService.findById(vid);
		Double fqty = Double.valueOf(request.getParameter("fqty"));
		virtualoperationlog.setFqty(fqty);
		virtualoperationlog.setFvirtualcointype(coinType);
		virtualoperationlog.setFuser(user);
		virtualoperationlog.setFstatus(OperationlogEnum.SAVE);
		if(request.getParameter("fisSendMsg") != null){
			virtualoperationlog.setFisSendMsg(1);
		}else{
			virtualoperationlog.setFisSendMsg(0);
		}
		this.virtualOperationLogService.saveObj(virtualoperationlog);
		
		ModelAndView modelAndView = new ModelAndView() ;
		modelAndView.setViewName("ssadmin/comm/ajaxDone") ;
		modelAndView.addObject("statusCode",200);
		modelAndView.addObject("message","新增成功");
		modelAndView.addObject("callbackType","closeCurrent");
		return modelAndView;
	}
	
	@RequestMapping("ssadmin/deleteVirtualOperationLog")
	public ModelAndView deleteVirtualOperationLog() throws Exception{
		ModelAndView modelAndView = new ModelAndView() ;
		String fid = request.getParameter("uid");
		Fvirtualoperationlog virtualoperationlog = this.virtualOperationLogService.findById(fid);
		if(virtualoperationlog.getFstatus() != OperationlogEnum.SAVE){
			modelAndView.setViewName("ssadmin/comm/ajaxDone") ;
			modelAndView.addObject("statusCode",300);
			modelAndView.addObject("message","删除失败，记录已审核");
			return modelAndView;
		}
		
		this.virtualOperationLogService.deleteObj(fid);
		modelAndView.setViewName("ssadmin/comm/ajaxDone") ;
		modelAndView.addObject("statusCode",200);
		modelAndView.addObject("message","删除成功");
		return modelAndView;
	}
	
	@RequestMapping("ssadmin/auditVirtualOperationLog")
	public ModelAndView auditVirtualOperationLog() throws Exception{
		ModelAndView modelAndView = new ModelAndView() ;
		String fid = request.getParameter("uid");
		boolean flag = false;
		Fvirtualoperationlog virtualoperationlog = this.virtualOperationLogService.findById(fid);
		
		if(virtualoperationlog.getFstatus() != OperationlogEnum.SAVE){
			modelAndView.setViewName("ssadmin/comm/ajaxDone") ;
			modelAndView.addObject("statusCode",300);
			modelAndView.addObject("message","已审核，不允许重复审核");
			return modelAndView;
		}

		try {
			double qty = virtualoperationlog.getFqty();
			String coinTypeId = virtualoperationlog.getFvirtualcointype().getFid();
			String userId = virtualoperationlog.getFuser().getFid();
			String sql =  "where fvirtualcointype.fid='"+coinTypeId+"'and fuser.fid='"+userId+"'";
			List<Fvirtualwallet> all = this.virtualWalletService.list(0, 0,sql, false);
			if(all != null && all.size() == 1){
				Fvirtualwallet virtualwallet = all.get(0);
				virtualwallet.setFfrozen(virtualwallet.getFfrozen()+qty);
				
				Fadmin sessionAdmin = (Fadmin)request.getSession().getAttribute("login_admin");
				virtualoperationlog.setFstatus(OperationlogEnum.FFROZEN);
				virtualoperationlog.setFcreator(sessionAdmin);
				virtualoperationlog.setFcreateTime(Utils.getTimestamp());
				this.virtualOperationLogService.updateVirtualOperationLog(virtualwallet,virtualoperationlog);
			}else{
				modelAndView.setViewName("ssadmin/comm/ajaxDone") ;
				modelAndView.addObject("statusCode",300);
				modelAndView.addObject("message","会员钱包有误");
				return modelAndView;
			}
			flag = true;
		} catch (Exception e) {
			flag = false;
		}
		
		if(!flag){
			modelAndView.setViewName("ssadmin/comm/ajaxDone") ;
			modelAndView.addObject("statusCode",300);
			modelAndView.addObject("message","审核失败");
			return modelAndView;
		}
		modelAndView.setViewName("ssadmin/comm/ajaxDone") ;
		modelAndView.addObject("statusCode",200);
		modelAndView.addObject("message","审核成功");
		return modelAndView;
	}
	
	
	@RequestMapping("ssadmin/sendVirtualOperationLog")
	public ModelAndView sendVirtualOperationLog() throws Exception{
		ModelAndView modelAndView = new ModelAndView() ;
		String fid = request.getParameter("uid");
		boolean flag = false;
		Fvirtualoperationlog virtualoperationlog = this.virtualOperationLogService.findById(fid);
		
		if(virtualoperationlog.getFstatus() != OperationlogEnum.FFROZEN){
			modelAndView.setViewName("ssadmin/comm/ajaxDone") ;
			modelAndView.addObject("statusCode",300);
			modelAndView.addObject("message","只有状态为冻结，才允许发放！");
			return modelAndView;
		}

		try {
			double qty = virtualoperationlog.getFqty();
			String coinTypeId = virtualoperationlog.getFvirtualcointype().getFid();
			String userId = virtualoperationlog.getFuser().getFid();
			String sql =  "where fvirtualcointype.fid='"+coinTypeId+"'and fuser.fid='"+userId+"'";
			List<Fvirtualwallet> all = this.virtualWalletService.list(0, 0,sql, false);
			if(all != null && all.size() == 1){
				Fvirtualwallet virtualwallet = all.get(0);
				virtualwallet.setFfrozen(virtualwallet.getFfrozen()-qty);
				virtualwallet.setFtotal(virtualwallet.getFtotal()+qty);
				
				Fadmin sessionAdmin = (Fadmin)request.getSession().getAttribute("login_admin");
				virtualoperationlog.setFstatus(OperationlogEnum.AUDIT);
				virtualoperationlog.setFcreator(sessionAdmin);
				virtualoperationlog.setFcreateTime(Utils.getTimestamp());
				this.virtualOperationLogService.updateVirtualOperationLog(virtualwallet,virtualoperationlog);
			}else{
				modelAndView.setViewName("ssadmin/comm/ajaxDone") ;
				modelAndView.addObject("statusCode",300);
				modelAndView.addObject("message","会员钱包有误");
				return modelAndView;
			}
			flag = true;
		} catch (Exception e) {
			flag = false;
		}
		
		if(!flag){
			modelAndView.setViewName("ssadmin/comm/ajaxDone") ;
			modelAndView.addObject("statusCode",300);
			modelAndView.addObject("message","发放失败");
			return modelAndView;
		}
		modelAndView.setViewName("ssadmin/comm/ajaxDone") ;
		modelAndView.addObject("statusCode",200);
		modelAndView.addObject("message","发放成功");
		return modelAndView;
	}
}
