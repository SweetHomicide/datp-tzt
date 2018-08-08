package com.ruizton.main.controller.admin;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.ruizton.main.Enum.CapitalOperationInStatus;
import com.ruizton.main.Enum.CapitalOperationOutStatus;
import com.ruizton.main.Enum.CapitalOperationTypeEnum;
import com.ruizton.main.controller.BaseController;
import com.ruizton.main.model.Fadmin;
import com.ruizton.main.model.Fcapitaloperation;
import com.ruizton.main.model.Fsubscription;
import com.ruizton.main.model.Fvirtualwallet;
import com.ruizton.main.model.Fwallet;
import com.ruizton.main.service.admin.AdminService;
import com.ruizton.main.service.admin.CapitaloperationService;
import com.ruizton.main.service.admin.SubscriptionService;
import com.ruizton.main.service.admin.SystemArgsService;
import com.ruizton.main.service.admin.VirtualWalletService;
import com.ruizton.main.service.admin.WalletService;
import com.ruizton.main.service.front.FrontAccountService;
import com.ruizton.main.service.front.FrontUserService;
import com.ruizton.util.Utils;
import com.ruizton.util.XlsExport;

@Controller
public class CapitaloperationController extends BaseController {
	@Autowired
	private CapitaloperationService capitaloperationService;
	@Autowired
	private AdminService adminService;
	@Autowired
	private SystemArgsService systemArgsService;
	@Autowired
	private VirtualWalletService virtualWalletService;
	@Autowired
	private SubscriptionService subscriptionService;
	@Autowired
	private HttpServletRequest request;
	@Autowired
	private FrontUserService frontUserService ;
	@Autowired
	private FrontAccountService frontAccountService ;
	// 每页显示多少条数据
	private int numPerPage = Utils.getNumPerPage();

	@RequestMapping("/ssadmin/capitaloperationList")
	public ModelAndView Index() throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("ssadmin/capitaloperationList");
		// 当前页
		int currentPage = 1;
		// 搜索关键字
		String keyWord = request.getParameter("keywords");
		String capitalId = request.getParameter("capitalId");
		String orderField = request.getParameter("orderField");
		String orderDirection = request.getParameter("orderDirection");
		if (request.getParameter("pageNum") != null) {
			currentPage = Integer.parseInt(request.getParameter("pageNum"));
		}
		StringBuffer filterSQL = new StringBuffer();
		filterSQL.append("where 1=1 \n");
		if (keyWord != null && keyWord.trim().length() > 0) {		
				filterSQL.append("and (fBank like '%" + keyWord + "%' OR \n");
				filterSQL.append("fuser.fid like '%" + keyWord + "%' OR \n");
				filterSQL.append("fuser.floginName like '%" + keyWord + "%' OR \n");
				filterSQL.append("fuser.fnickName like '%" + keyWord + "%' OR \n");
				filterSQL.append("fuser.frealName like '%" + keyWord + "%' OR \n");
				filterSQL.append("fAccount like '%" + keyWord + "%' OR \n");
				filterSQL.append("fPhone like '%" + keyWord + "%' OR \n");
				filterSQL.append("fPayee like '%" + keyWord + "%' OR \n");
				filterSQL.append("famount like '%" + keyWord + "%') \n");
			modelAndView.addObject("keywords", keyWord);
		}
		
		String logDate = request.getParameter("logDate");
		if(logDate != null && logDate.trim().length() >0){
			filterSQL.append("and  DATE_FORMAT(fcreateTime,'%Y-%m-%d') = '"+logDate+"' \n");
			modelAndView.addObject("logDate", logDate);
		}

		if (capitalId != null && capitalId.trim().length() > 0) {
			boolean flag = Utils.isNumeric(capitalId);
			if (flag) {
				filterSQL.append("AND fid ='" + capitalId + "' \n");
				modelAndView.addObject("capitalId", capitalId);
			}
		}

		String status = request.getParameter("fstatus");
		if (status != null && status.trim().length() > 0) {
			String fstatus = status.trim();
			if (!fstatus.equals("0")) {
				if (fstatus.indexOf("充值") != -1) {
					filterSQL.append("AND ftype ="
							+ CapitalOperationTypeEnum.RMB_IN + " \n");
					filterSQL.append("AND fstatus ="
							+ fstatus.replace("充值-", "") + " \n");
				} else if (fstatus.indexOf("提现") != -1) {
					filterSQL.append("AND ftype ="
							+ CapitalOperationTypeEnum.RMB_OUT + " \n");
					filterSQL.append("AND fstatus ="
							+ fstatus.replace("提现-", "") + " \n");
				}
			}
			modelAndView.addObject("fstatus", fstatus);
		} else {
			modelAndView.addObject("fstatus", "0");
		}

		if (orderField != null && orderField.trim().length() > 0) {
			filterSQL.append("order by " + orderField + "\n");
		} else {
			filterSQL.append("order by fid \n");
		}

		if (orderDirection != null && orderDirection.trim().length() > 0) {
			filterSQL.append(orderDirection + "\n");
		} else {
			filterSQL.append("desc \n");
		}

		Map<String,String> statusMap = new HashMap<String,String>();
		statusMap.put("0", "全部");
		statusMap.put(
				"充值-" + CapitalOperationInStatus.Come,
				"充值-"
						+ CapitalOperationInStatus
								.getEnumString(CapitalOperationInStatus.Come));
		statusMap
				.put("充值-" + CapitalOperationInStatus.Invalidate,
						"充值-"
								+ CapitalOperationInStatus
										.getEnumString(CapitalOperationInStatus.Invalidate));
		statusMap
				.put("充值-" + CapitalOperationInStatus.NoGiven,
						"充值-"
								+ CapitalOperationInStatus
										.getEnumString(CapitalOperationInStatus.NoGiven));
		statusMap
				.put("充值-" + CapitalOperationInStatus.WaitForComing,
						"充值-"
								+ CapitalOperationInStatus
										.getEnumString(CapitalOperationInStatus.WaitForComing));
		statusMap
				.put("提现-" + CapitalOperationOutStatus.Cancel,
						"提现-"
								+ CapitalOperationOutStatus
										.getEnumString(CapitalOperationOutStatus.Cancel));
		statusMap
				.put("提现-" + CapitalOperationOutStatus.OperationLock,
						"提现-"
								+ CapitalOperationOutStatus
										.getEnumString(CapitalOperationOutStatus.OperationLock));
		statusMap
				.put("提现-" + CapitalOperationOutStatus.OperationSuccess,
						"提现-"
								+ CapitalOperationOutStatus
										.getEnumString(CapitalOperationOutStatus.OperationSuccess));
		statusMap
				.put("提现-" + CapitalOperationOutStatus.WaitForOperation,
						"提现-"
								+ CapitalOperationOutStatus
										.getEnumString(CapitalOperationOutStatus.WaitForOperation));
		modelAndView.addObject("statusMap", statusMap);

		List<Fcapitaloperation> list = this.capitaloperationService.list(
				(currentPage - 1) * numPerPage, numPerPage, filterSQL + "",
				true);
		modelAndView.addObject("capitaloperationList", list);
		modelAndView.addObject("numPerPage", numPerPage);
		modelAndView.addObject("currentPage", currentPage);
		modelAndView.addObject("rel", "capitaloperationList");
		// 总数量
		modelAndView.addObject("totalCount",this.adminService.getAllCount("Fcapitaloperation", filterSQL+ ""));
		return modelAndView;
	}

	@RequestMapping("/ssadmin/capitalInList")
	public ModelAndView capitalInList() throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		String type = "(" + CapitalOperationTypeEnum.RMB_IN + ")";
		String status = "(" + CapitalOperationInStatus.WaitForComing + ")";
		modelAndView.setViewName("ssadmin/capitalInList");
		// 当前页
		int currentPage = 1;
		// 搜索关键字
		String keyWord = request.getParameter("keywords");
		String capitalId = request.getParameter("capitalId");
		String orderField = request.getParameter("orderField");
		String orderDirection = request.getParameter("orderDirection");

		if (request.getParameter("pageNum") != null) {
			currentPage = Integer.parseInt(request.getParameter("pageNum"));
		}
		StringBuffer filterSQL = new StringBuffer();
		filterSQL.append("where 1=1 \n");
		filterSQL.append("and ftype IN " + type + "\n");
		filterSQL.append("AND fstatus IN " + status + "\n");

		if (keyWord != null && keyWord.trim().length() > 0) {
				filterSQL.append("and (fBank like '%" + keyWord + "%' OR \n");
				filterSQL.append("fuser.fid like '%" + keyWord + "%' OR \n");
				filterSQL.append("fAccount like '%" + keyWord + "%' OR \n");
				filterSQL.append("fPhone like '%" + keyWord + "%' OR \n");
				filterSQL.append("fPayee like '%" + keyWord + "%' OR \n");
				filterSQL.append("famount like '%" + keyWord + "%') \n");
			modelAndView.addObject("keywords", keyWord);
		}
		
		String logDate = request.getParameter("logDate");
		if(logDate != null && logDate.trim().length() >0){
			filterSQL.append("and  DATE_FORMAT(fcreateTime,'%Y-%m-%d') = '"+logDate+"' \n");
			modelAndView.addObject("logDate", logDate);
		}

		if (capitalId != null && capitalId.trim().length() > 0) {
			boolean flag = Utils.isNumeric(capitalId);
			if (flag) {
				filterSQL.append(" AND fid ='" + capitalId + "'\n");
				modelAndView.addObject("capitalId", capitalId);
			}
		}
		if (orderField != null && orderField.trim().length() > 0) {
			filterSQL.append("order by " + orderField + "\n");
		} else {
			filterSQL.append("order by fCreateTime \n");
		}

		if (orderDirection != null && orderDirection.trim().length() > 0) {
			filterSQL.append(orderDirection + "\n");
		} else {
			filterSQL.append("desc \n");
		}
		
		List<Fcapitaloperation> list = this.capitaloperationService.list(
				(currentPage - 1) * numPerPage, numPerPage, filterSQL + "",
				true);
		modelAndView.addObject("capitaloperationList", list);
		modelAndView.addObject("numPerPage", numPerPage);
		modelAndView.addObject("currentPage", currentPage);
		modelAndView.addObject("rel", "capitalInList");
		// 总数量
		modelAndView.addObject(
				"totalCount",
				this.adminService.getAllCount("Fcapitaloperation", filterSQL
						+ ""));
		return modelAndView;
	}
	
	@RequestMapping("/ssadmin/capitalInSucList")
	public ModelAndView capitalInSucList() throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		String type = "(" + CapitalOperationTypeEnum.RMB_IN + ")";
		String status = "(" + CapitalOperationInStatus.Come + ")";
		modelAndView.setViewName("ssadmin/capitalInSucList");
		// 当前页
		int currentPage = 1;
		// 搜索关键字
		String keyWord = request.getParameter("keywords");
		String capitalId = request.getParameter("capitalId");
		String orderField = request.getParameter("orderField");
		String orderDirection = request.getParameter("orderDirection");

		if (request.getParameter("pageNum") != null) {
			currentPage = Integer.parseInt(request.getParameter("pageNum"));
		}
		StringBuffer filterSQL = new StringBuffer();
		filterSQL.append("where 1=1 \n");
		filterSQL.append("and ftype IN " + type + "\n");
		filterSQL.append("AND fstatus IN " + status + "\n");

		if (keyWord != null && keyWord.trim().length() > 0) {
				filterSQL.append("and (fBank like '%" + keyWord + "%' OR \n");
				filterSQL.append("fuser.fid like '%" + keyWord + "%' OR \n");
				filterSQL.append("fAccount like '%" + keyWord + "%' OR \n");
				filterSQL.append("fPhone like '%" + keyWord + "%' OR \n");
				filterSQL.append("fPayee like '%" + keyWord + "%' OR \n");
				filterSQL.append("famount like '%" + keyWord + "%') \n");
			modelAndView.addObject("keywords", keyWord);
		}
		
		String logDate = request.getParameter("logDate");
		if(logDate != null && logDate.trim().length() >0){
			filterSQL.append("and  DATE_FORMAT(fcreateTime,'%Y-%m-%d') = '"+logDate+"' \n");
			modelAndView.addObject("logDate", logDate);
		}

		if (capitalId != null && capitalId.trim().length() > 0) {
			boolean flag = Utils.isNumeric(capitalId);
			if (flag) {
				filterSQL.append(" AND fid =" + capitalId + "\n");
				modelAndView.addObject("capitalId", capitalId);
			}
		}
		if (orderField != null && orderField.trim().length() > 0) {
			filterSQL.append("order by " + orderField + "\n");
		} else {
			filterSQL.append("order by fid \n");
		}

		if (orderDirection != null && orderDirection.trim().length() > 0) {
			filterSQL.append(orderDirection + "\n");
		} else {
			filterSQL.append("desc \n");
		}
		
		List<Fcapitaloperation> list = this.capitaloperationService.list(
				(currentPage - 1) * numPerPage, numPerPage, filterSQL + "",
				true);
		modelAndView.addObject("capitaloperationList", list);
		modelAndView.addObject("numPerPage", numPerPage);
		modelAndView.addObject("currentPage", currentPage);
		modelAndView.addObject("rel", "capitalInSucList");
		// 总数量
		modelAndView.addObject(
				"totalCount",
				this.adminService.getAllCount("Fcapitaloperation", filterSQL
						+ ""));
		return modelAndView;
	}

	@RequestMapping("/ssadmin/capitalOutList")
	public ModelAndView capitalOutList() throws Exception {
		String type = "(" + CapitalOperationTypeEnum.RMB_OUT + ",4)";
		String status = "(" + CapitalOperationOutStatus.WaitForOperation + ","
				+ CapitalOperationOutStatus.OperationLock + ")";
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("ssadmin/capitalOutList");
		// 当前页
		int currentPage = 1;
		// 搜索关键字
		String keyWord = request.getParameter("keywords");
		String capitalId = request.getParameter("capitalId");
		String orderField = request.getParameter("orderField");
		String orderDirection = request.getParameter("orderDirection");
		if (request.getParameter("pageNum") != null) {
			currentPage = Integer.parseInt(request.getParameter("pageNum"));
		}

		StringBuffer filterSQL = new StringBuffer();
		filterSQL.append("where 1=1 \n");
		filterSQL.append("and ftype IN " + type + "\n");
		filterSQL.append("AND fstatus IN " + status + "\n");
		if (keyWord != null && keyWord.trim().length() > 0) {
				filterSQL.append(" AND (fBank like '%" + keyWord + "%' OR \n");
				filterSQL.append("fuser.fid like '%" + keyWord + "%' OR \n");
				filterSQL.append(" fAccount like '%" + keyWord + "%' OR \n");
				filterSQL.append(" fPhone like '%" + keyWord + "%' OR \n");
				filterSQL.append(" fPayee like '%" + keyWord + "%' OR \n");
				filterSQL.append(" famount like '%" + keyWord + "%') \n");
			modelAndView.addObject("keywords", keyWord);
		}
		
		String logDate = request.getParameter("logDate");
		if(logDate != null && logDate.trim().length() >0){
			filterSQL.append("and  DATE_FORMAT(fcreateTime,'%Y-%m-%d') = '"+logDate+"' \n");
			modelAndView.addObject("logDate", logDate);
		}

		if (capitalId != null && capitalId.trim().length() > 0) {
			boolean flag = Utils.isNumeric(capitalId);
			if (flag) {
				filterSQL.append(" AND fid =" + capitalId + "\n");
				modelAndView.addObject("capitalId", capitalId);
			}
		}
		if (orderField != null && orderField.trim().length() > 0) {
			filterSQL.append("order by " + orderField + "\n");
		} else {
			filterSQL.append("order by fid \n");
		}

		if (orderDirection != null && orderDirection.trim().length() > 0) {
			filterSQL.append(orderDirection + "\n");
		} else {
			filterSQL.append("desc \n");
		}
		
		List<Fcapitaloperation> list = this.capitaloperationService.list(
				(currentPage - 1) * numPerPage, numPerPage, filterSQL + "",
				true);
		modelAndView.addObject("capitaloperationList", list);
		modelAndView.addObject("numPerPage", numPerPage);
		modelAndView.addObject("currentPage", currentPage);
		modelAndView.addObject("rel", "capitalOutList");
		// 总数量
		modelAndView.addObject(
				"totalCount",
				this.adminService.getAllCount("Fcapitaloperation", filterSQL
						+ ""));
		return modelAndView;
	}
	
	@RequestMapping("/ssadmin/capitalOutSucList")
	public ModelAndView capitalOutSucList() throws Exception {
		String type = "(" + CapitalOperationTypeEnum.RMB_OUT + ")";
		String status = "(" + CapitalOperationOutStatus.OperationSuccess + ","
				+ CapitalOperationOutStatus.OperationLock + ")";
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("ssadmin/capitalOutSucList");
		// 当前页
		int currentPage = 1;
		// 搜索关键字
		String keyWord = request.getParameter("keywords");
		String capitalId = request.getParameter("capitalId");
		String orderField = request.getParameter("orderField");
		String orderDirection = request.getParameter("orderDirection");
		if (request.getParameter("pageNum") != null) {
			currentPage = Integer.parseInt(request.getParameter("pageNum"));
		}

		StringBuffer filterSQL = new StringBuffer();
		filterSQL.append("where 1=1 \n");
		filterSQL.append("and ftype IN " + type + "\n");
		filterSQL.append("AND fstatus IN " + status + "\n");
		if (keyWord != null && keyWord.trim().length() > 0) {
				filterSQL.append(" AND (fBank like '%" + keyWord + "%' OR \n");
				filterSQL.append("fuser.fid like '%" + keyWord + "%' OR \n");
				filterSQL.append(" fAccount like '%" + keyWord + "%' OR \n");
				filterSQL.append(" fPhone like '%" + keyWord + "%' OR \n");
				filterSQL.append(" fPayee like '%" + keyWord + "%' OR \n");
				filterSQL.append(" famount like '%" + keyWord + "%') \n");
			modelAndView.addObject("keywords", keyWord);
		}
		
		String logDate = request.getParameter("logDate");
		if(logDate != null && logDate.trim().length() >0){
			filterSQL.append("and  DATE_FORMAT(fcreateTime,'%Y-%m-%d') = '"+logDate+"' \n");
			modelAndView.addObject("logDate", logDate);
		}

		if (capitalId != null && capitalId.trim().length() > 0) {
			boolean flag = Utils.isNumeric(capitalId);
			if (flag) {
				filterSQL.append(" AND fid =" + capitalId + "\n");
				modelAndView.addObject("capitalId", capitalId);
			}
		}
		if (orderField != null && orderField.trim().length() > 0) {
			filterSQL.append("order by " + orderField + "\n");
		} else {
			filterSQL.append("order by fid \n");
		}

		if (orderDirection != null && orderDirection.trim().length() > 0) {
			filterSQL.append(orderDirection + "\n");
		} else {
			filterSQL.append("desc \n");
		}
		
		List<Fcapitaloperation> list = this.capitaloperationService.list(
				(currentPage - 1) * numPerPage, numPerPage, filterSQL + "",
				true);
		modelAndView.addObject("capitaloperationList", list);
		modelAndView.addObject("numPerPage", numPerPage);
		modelAndView.addObject("currentPage", currentPage);
		modelAndView.addObject("rel", "capitalOutSucList");
		// 总数量
		modelAndView.addObject(
				"totalCount",
				this.adminService.getAllCount("Fcapitaloperation", filterSQL
						+ ""));
		return modelAndView;
	}

	@RequestMapping("ssadmin/goCapitaloperationJSP")
	public ModelAndView goCapitaloperationJSP() throws Exception {
		String url = request.getParameter("url");
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName(url);
		if (request.getParameter("uid") != null) {
			String fid = request.getParameter("uid");
			Fcapitaloperation capitaloperation = this.capitaloperationService
					.findById(fid);
			modelAndView.addObject("capitaloperation", capitaloperation);
		}
		return modelAndView;
	}

	@RequestMapping("/ssadmin/capitalInCancel")
	public ModelAndView capitalInCancel() throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("ssadmin/comm/ajaxDone");
		String fid = request.getParameter("uid");
		Fcapitaloperation capitalOperation = this.capitaloperationService
				.findById(fid);
		int status = capitalOperation.getFstatus();
		if (status == CapitalOperationInStatus.Come || status == CapitalOperationInStatus.Invalidate) {
			modelAndView.setViewName("ssadmin/comm/ajaxDone");
			modelAndView.addObject("statusCode", 300);
			modelAndView.addObject("message", "取消失败");
			return modelAndView;
		}
		capitalOperation.setFstatus(CapitalOperationInStatus.Invalidate);
		this.capitaloperationService.updateObj(capitalOperation);
		modelAndView.addObject("statusCode", 200);
		modelAndView.addObject("message", "取消成功");
		return modelAndView;
	}

	@RequestMapping("ssadmin/capitalInAudit")
	public ModelAndView capitalInAudit() throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		String fid = request.getParameter("uid");
		Fcapitaloperation capitalOperation = this.capitaloperationService
				.findById(fid);
		int status = capitalOperation.getFstatus();
		if (status != CapitalOperationInStatus.WaitForComing) {
			modelAndView.setViewName("ssadmin/comm/ajaxDone");
			modelAndView.addObject("statusCode", 300);
			String status_s = CapitalOperationInStatus
					.getEnumString(CapitalOperationInStatus.WaitForComing);
			modelAndView.addObject("message", "审核失败,只有状态为:" + status_s
					+ "的充值记录才允许审核!");
			return modelAndView;
		}
		// 根据用户查钱包最后修改时间
		Fwallet walletInfo = capitalOperation.getFuser().getFwallet();
		if (walletInfo == null) {
			modelAndView.setViewName("ssadmin/comm/ajaxDone");
			modelAndView.addObject("statusCode", 300);
			modelAndView.addObject("message", "审核失败，会员钱包信息异常!");
			return modelAndView;
		}
		double rate = Double.valueOf(this.systemArgsService.getValue("rechargeRate"));
		double amount = Utils.getDouble(capitalOperation.getFamount()+capitalOperation.getFamount()*rate, 4);

		Fadmin admin = (Fadmin) request.getSession()
				.getAttribute("login_admin");
		// 充值操作
		capitalOperation.setFstatus(CapitalOperationInStatus.Come);
		capitalOperation.setfLastUpdateTime(Utils.getTimestamp());
		capitalOperation.setfAuditee_id(admin);
		Fvirtualwallet fvirtualwallet=null;
		if(capitalOperation.getFviType()!=null){
			 String fivwFilter="where fvirtualcointype.fid='"+capitalOperation.getFviType().getFid()+"' and fuser.fid='"+capitalOperation.getFuser().getFid()+"'";
			//获取平台钱包
			 fvirtualwallet= virtualWalletService.list(0, 0, fivwFilter, false).get(0);
			Fsubscription fsubscription = subscriptionService.findByFviId(capitalOperation.getFviType().getFid());
			double amountBTC = Utils.getDouble(capitalOperation.getFtotalBTC()+capitalOperation.getFamount()*rate*fsubscription.getFprice(), 4);
			
			fvirtualwallet.setFlastUpdateTime(Utils.getTimestamp());
			fvirtualwallet.setFtotal(fvirtualwallet.getFtotal()+amountBTC);
		}else{
			// 钱包操作
			walletInfo.setFlastUpdateTime(Utils.getTimestamp());
			walletInfo.setFtotalRmb(walletInfo.getFtotalRmb() + amount);
		}

		boolean flag = false;
		try {
			this.capitaloperationService.updateCapital(capitalOperation,
					walletInfo,true, fvirtualwallet);
			flag = true;
		} catch (Exception e) {
			flag = false;
		}
		if (!flag) {
			modelAndView.setViewName("ssadmin/comm/ajaxDone");
			modelAndView.addObject("statusCode", 300);
			modelAndView.addObject("message", "审核失败");
			return modelAndView;
		}
		modelAndView.setViewName("ssadmin/comm/ajaxDone");
		modelAndView.addObject("statusCode", 200);
		modelAndView.addObject("message", "审核成功");
		return modelAndView;
	}

	@RequestMapping("ssadmin/capitalOutAudit")
	public ModelAndView capitalOutAudit(@RequestParam(required = true) int type)
			throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		String fid = request.getParameter("uid");
		Fcapitaloperation capitalOperation = this.capitaloperationService
				.findById(fid);
		int status = capitalOperation.getFstatus();
		switch (type) {
		case 1:
			if (status != CapitalOperationOutStatus.OperationLock) {
				modelAndView.setViewName("ssadmin/comm/ajaxDone");
				modelAndView.addObject("statusCode", 300);
				String status_s = CapitalOperationOutStatus
						.getEnumString(CapitalOperationOutStatus.OperationLock);
				modelAndView.addObject("message", "审核失败,只有状态为:‘" + status_s
						+ "’的提现记录才允许审核!");
				return modelAndView;
			}
			break;
		case 2:
			if (status != CapitalOperationOutStatus.WaitForOperation) {
				modelAndView.setViewName("ssadmin/comm/ajaxDone");
				modelAndView.addObject("statusCode", 300);
				String status_s = CapitalOperationOutStatus
						.getEnumString(CapitalOperationOutStatus.WaitForOperation);
				modelAndView.addObject("message", "锁定失败,只有状态为:‘" + status_s
						+ "’的提现记录才允许锁定!");
				return modelAndView;
			}
			break;
		case 3:
			if (status != CapitalOperationOutStatus.OperationLock) {
				modelAndView.setViewName("ssadmin/comm/ajaxDone");
				modelAndView.addObject("statusCode", 300);
				String status_s = CapitalOperationOutStatus
						.getEnumString(CapitalOperationOutStatus.OperationLock);
				modelAndView.addObject("message", "取消锁定失败,只有状态为:‘" + status_s
						+ "’的提现记录才允许取消锁定!");
				return modelAndView;
			}
			break;
		case 4:
			if (status == CapitalOperationOutStatus.Cancel || status == CapitalOperationOutStatus.OperationSuccess) {
				modelAndView.setViewName("ssadmin/comm/ajaxDone");
				modelAndView.addObject("statusCode", 300);
				modelAndView.addObject("message", "取消失败!");
				return modelAndView;
			}
			break;
		default:
			modelAndView.setViewName("ssadmin/comm/ajaxDone");
			modelAndView.addObject("statusCode", 300);
			modelAndView.addObject("message", "非法提交！");
			return modelAndView;
		}

		// 根据用户查钱包最后修改时间
		Fwallet walletInfo = capitalOperation.getFuser().getFwallet();
		
		if (walletInfo == null) {
			modelAndView.setViewName("ssadmin/comm/ajaxDone");
			modelAndView.addObject("statusCode", 300);
			modelAndView.addObject("message", "审核失败，会员钱包信息异常!");
			return modelAndView;
		}

		double amount = capitalOperation.getFamount();
		double frees = capitalOperation.getFfees();
		double totalAmt = Utils.getDouble(amount + frees,2);

		// 充值操作
		Fadmin admin = (Fadmin) request.getSession()
				.getAttribute("login_admin");
		capitalOperation.setfLastUpdateTime(Utils.getTimestamp());
		capitalOperation.setfAuditee_id(admin);

		boolean flag = false;
		// 钱包操作//1审核,2锁定,3取消锁定,4取消提现
		String tips = "";
		if(capitalOperation.getFviType()!=null){
			Fvirtualwallet fvirtualwallet = this.frontUserService.findVirtualWalletByUser(capitalOperation.getFuser().getFid(), capitalOperation.getFviType().getFid()) ;
			switch (type) {
			case 1:
				capitalOperation
						.setFstatus(CapitalOperationOutStatus.OperationSuccess);

				fvirtualwallet.setFlastUpdateTime(Utils.getTimestamp());
				fvirtualwallet.setFfrozen(fvirtualwallet.getFfrozen() - capitalOperation.getFtotalBTC());
				tips = "审核";
				break;
			case 2:
				capitalOperation
						.setFstatus(CapitalOperationOutStatus.OperationLock);
				tips = "锁定";
				break;
			case 3:
				capitalOperation
						.setFstatus(CapitalOperationOutStatus.WaitForOperation);
				tips = "取消锁定";
				break;
			case 4:
				capitalOperation.setFstatus(CapitalOperationOutStatus.Cancel);
				fvirtualwallet.setFlastUpdateTime(Utils.getTimestamp());
				fvirtualwallet.setFfrozen(fvirtualwallet.getFfrozen() - capitalOperation.getFtotalBTC());
				fvirtualwallet.setFtotal(fvirtualwallet.getFtotal() + capitalOperation.getFtotalBTC());
				tips = "取消";
				break;
			}
			try {
				this.frontAccountService.savefvirtualwallet(capitalOperation, fvirtualwallet);
				flag = true;
			} catch (Exception e) {
				flag = false;
			}
		}else{
		switch (type) {
		case 1:
			capitalOperation
					.setFstatus(CapitalOperationOutStatus.OperationSuccess);

			walletInfo.setFlastUpdateTime(Utils.getTimestamp());
			walletInfo.setFfrozenRmb(walletInfo.getFfrozenRmb() - totalAmt);
			tips = "审核";
			break;
		case 2:
			capitalOperation
					.setFstatus(CapitalOperationOutStatus.OperationLock);
			tips = "锁定";
			break;
		case 3:
			capitalOperation
					.setFstatus(CapitalOperationOutStatus.WaitForOperation);
			tips = "取消锁定";
			break;
		case 4:
			capitalOperation.setFstatus(CapitalOperationOutStatus.Cancel);
			walletInfo.setFlastUpdateTime(Utils.getTimestamp());
			walletInfo.setFfrozenRmb(walletInfo.getFfrozenRmb() - totalAmt);
			walletInfo.setFtotalRmb(walletInfo.getFtotalRmb() + totalAmt);
			tips = "取消";
			break;
		}
		try {
			this.capitaloperationService.updateCapital(capitalOperation,
					walletInfo,false,null);
			flag = true;
		} catch (Exception e) {
			flag = false;
		}
		}
		if (!flag) {
			modelAndView.setViewName("ssadmin/comm/ajaxDone");
			modelAndView.addObject("statusCode", 300);
			modelAndView.addObject("message", tips + "失败");
			return modelAndView;
		}

		modelAndView.setViewName("ssadmin/comm/ajaxDone");
		modelAndView.addObject("statusCode", 200);
		modelAndView.addObject("message", tips + "成功");
		return modelAndView;
	}

	private static enum ExportFiled {
		订单ID,会员UID,会员登陆名, 会员昵称, 会员真实姓名, 状态, 金额, 手续费, 备注, 创建时间, 最后修改时间, 审核人;
	}

	private List<Fcapitaloperation> getCapitalOperationList(String type,
			String status) {
		String keyWord = request.getParameter("keywords");
		String capitalId = request.getParameter("capitalId");
		String orderField = request.getParameter("orderField");
		String orderDirection = request.getParameter("orderDirection");
		StringBuffer filterSQL = new StringBuffer();
		filterSQL.append("where 1=1 \n");
		if (type != null && type.trim().length() > 0) {
			filterSQL.append("and ftype IN " + type + "\n");
		}
		if (status != null && status.trim().length() > 0) {
			filterSQL.append("AND fstatus IN " + status + "\n");
		}
		if (keyWord != null && keyWord.trim().length() > 0) {
				filterSQL.append(" AND (fBank like '%" + keyWord + "%' OR \n");
				filterSQL.append("fuser.fid like '%" + keyWord + "%' OR \n");
				filterSQL.append(" fAccount like '%" + keyWord + "%' OR \n");
				filterSQL.append(" fPhone like '%" + keyWord + "%' OR \n");
				filterSQL.append(" fPayee like '%" + keyWord + "%' OR \n");
				filterSQL.append(" famount like '%" + keyWord + "%') \n");
		}
		
		String logDate = request.getParameter("logDate");
		if(logDate != null && logDate.trim().length() >0){
			filterSQL.append("and  DATE_FORMAT(fcreateTime,'%Y-%m-%d') = '"+logDate+"' \n");
		}

		if (capitalId != null && capitalId.trim().length() > 0) {
			boolean flag = Utils.isNumeric(capitalId);
			if (flag) {
				filterSQL.append(" AND fid =" + capitalId + "\n");
			}
		}

		try {
			if (request.getParameter("fstatus") != null
					&& request.getParameter("fstatus").trim().length() > 0) {
				String fstatus = new String(request.getParameter("fstatus")
						.getBytes("iso8859-1"), "utf-8");
				if (!fstatus.equals("0")) {
					if (fstatus.indexOf("充值") != -1) {
						filterSQL.append("AND ftype ="
								+ CapitalOperationTypeEnum.RMB_IN + " \n");
						filterSQL.append("AND fstatus ="
								+ fstatus.replace("充值-", "") + " \n");
					} else if (fstatus.indexOf("提现") != -1) {
						filterSQL.append("AND ftype ="
								+ CapitalOperationTypeEnum.RMB_OUT + " \n");
						filterSQL.append("AND fstatus ="
								+ fstatus.replace("提现-", "") + " \n");
					}
				}
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		if (orderField != null && orderField.trim().length() > 0) {
			filterSQL.append("order by " + orderField + "\n");
		} else {
			filterSQL.append("order by fid \n");
		}

		if (orderDirection != null && orderDirection.trim().length() > 0) {
			filterSQL.append(orderDirection + "\n");
		} else {
			filterSQL.append("desc \n");
		}
		List<Fcapitaloperation> list = this.capitaloperationService.list(0, 0,
				filterSQL + "", false);
		return list;
	}

	@RequestMapping("ssadmin/capitaloperationExport")
	public ModelAndView capitaloperationExport(HttpServletResponse response)
			throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		response.setContentType("Application/excel");
		response.addHeader("Content-Disposition",
				"attachment;filename=capitaloperationList.xls");
		XlsExport e = new XlsExport();
		int rowIndex = 0;

		// header
		e.createRow(rowIndex++);
		for (ExportFiled filed : ExportFiled.values()) {
			e.setCell(filed.ordinal(), filed.toString());
		}
		String type = null;
		String status = null;
		List<Fcapitaloperation> capitalOperationList = getCapitalOperationList(
				type, status);
		for (Fcapitaloperation capitalOperation : capitalOperationList) {
			e.createRow(rowIndex++);
			for (ExportFiled filed : ExportFiled.values()) {
				switch (filed) {
				case 订单ID:
					if (capitalOperation.getFuser() != null)
						e.setCell(filed.ordinal(), capitalOperation.getFid());
					break;
				case 会员UID:
					if (capitalOperation.getFuser() != null)
						e.setCell(filed.ordinal(), capitalOperation.getFuser().getFid());
					break;
				case 会员登陆名:
					if (capitalOperation.getFuser() != null)
						e.setCell(filed.ordinal(), capitalOperation.getFuser()
								.getFloginName());
					break;
				case 会员昵称:
					if (capitalOperation.getFuser() != null)
						e.setCell(filed.ordinal(), capitalOperation.getFuser()
								.getFnickName());
					break;
				case 会员真实姓名:
					if (capitalOperation.getFuser() != null)
						e.setCell(filed.ordinal(), capitalOperation.getFuser()
								.getFrealName());
					break;
				case 状态:
					e.setCell(filed.ordinal(), capitalOperation.getFstatus_s());
					break;
				case 金额:
					e.setCell(filed.ordinal(), capitalOperation.getFamount());
					break;
				case 手续费:
					e.setCell(filed.ordinal(), capitalOperation.getFfees());
					break;
				case 创建时间:
					e.setCell(filed.ordinal(),
							capitalOperation.getFcreateTime());
					break;
				case 最后修改时间:
					e.setCell(filed.ordinal(),
							capitalOperation.getfLastUpdateTime());
					break;
				case 审核人:
					if (capitalOperation.getfAuditee_id() != null)
						e.setCell(filed.ordinal(), capitalOperation
								.getfAuditee_id().getFname());
					break;
				default:
					break;
				}
			}
		}

		e.exportXls(response);
		response.getOutputStream().close();

		modelAndView.setViewName("ssadmin/comm/ajaxDone");
		modelAndView.addObject("statusCode", 200);
		modelAndView.addObject("message", "导出成功");
		return modelAndView;
	}

	private static enum Export1Filed {
		订单ID,会员UID,会员登陆名, 会员昵称, 会员真实姓名, 类型, 状态, 金额, 手续费, 银行, 收款帐号, 手机号码, 创建时间, 最后修改时间;
	}

	@RequestMapping("ssadmin/capitalOutExport")
	public ModelAndView capitalOutExport(HttpServletResponse response)
			throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		response.setContentType("Application/excel");
		response.addHeader("Content-Disposition",
				"attachment;filename=capitalOutList.xls");
		XlsExport e = new XlsExport();
		int rowIndex = 0;

		// header
		e.createRow(rowIndex++);
		for (Export1Filed filed : Export1Filed.values()) {
			e.setCell(filed.ordinal(), filed.toString());
		}
		String type = "(" + CapitalOperationTypeEnum.RMB_OUT + ")";
		String status = "(" + CapitalOperationOutStatus.WaitForOperation + ","
				+ CapitalOperationOutStatus.OperationLock + ")";
		List<Fcapitaloperation> capitalOperationList = getCapitalOperationList(
				type, status);
		for (Fcapitaloperation capitalOperation : capitalOperationList) {
			e.createRow(rowIndex++);
			for (Export1Filed filed : Export1Filed.values()) {
				switch (filed) {
				case 订单ID:
					if (capitalOperation.getFuser() != null)
						e.setCell(filed.ordinal(), capitalOperation.getFid());
					break;
				case 会员UID:
					if (capitalOperation.getFuser() != null)
						e.setCell(filed.ordinal(), capitalOperation.getFuser().getFid());
					break;
				case 会员登陆名:
					if (capitalOperation.getFuser() != null)
						e.setCell(filed.ordinal(), capitalOperation.getFuser()
								.getFloginName());
					break;
				case 会员昵称:
					if (capitalOperation.getFuser() != null)
						e.setCell(filed.ordinal(), capitalOperation.getFuser()
								.getFnickName());
					break;
				case 会员真实姓名:
					if (capitalOperation.getFuser() != null)
						e.setCell(filed.ordinal(), capitalOperation.getFuser()
								.getFrealName());
					break;
				case 类型:
					e.setCell(filed.ordinal(), capitalOperation.getFtype_s());
					break;
				case 状态:
					e.setCell(filed.ordinal(), capitalOperation.getFstatus_s());
					break;
				case 金额:
					e.setCell(filed.ordinal(), capitalOperation.getFamount());
					break;
				case 手续费:
					e.setCell(filed.ordinal(), capitalOperation.getFfees());
					break;
				case 银行:
					e.setCell(filed.ordinal(), capitalOperation.getfBank());
					break;
				case 收款帐号:
					e.setCell(filed.ordinal(), capitalOperation.getFaccount_s());
					break;
				case 手机号码:
					e.setCell(filed.ordinal(), capitalOperation.getfPhone());
					break;
				case 创建时间:
					e.setCell(filed.ordinal(),
							capitalOperation.getFcreateTime());
					break;
				case 最后修改时间:
					e.setCell(filed.ordinal(),
							capitalOperation.getfLastUpdateTime());
					break;
				default:
					break;
				}
			}
		}

		e.exportXls(response);
		response.getOutputStream().close();

		modelAndView.setViewName("ssadmin/comm/ajaxDone");
		modelAndView.addObject("statusCode", 200);
		modelAndView.addObject("message", "导出成功");
		return modelAndView;
	}

	@RequestMapping("ssadmin/capitalOutAuditAll")
	public ModelAndView capitalOutAuditAll() throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		String ids = request.getParameter("ids");
		String[] idString = ids.split(",");
		// 充值操作
		Fadmin admin = (Fadmin) request.getSession()
				.getAttribute("login_admin");
		for(int i=0;i<idString.length;i++){
			String id = idString[i];
			Fcapitaloperation capitalOperation = this.capitaloperationService.findById(id);
			int status = capitalOperation.getFstatus();
			if (status != CapitalOperationOutStatus.WaitForOperation) {
				modelAndView.setViewName("ssadmin/comm/ajaxDone");
				modelAndView.addObject("statusCode", 300);
				String status_s = CapitalOperationOutStatus
						.getEnumString(CapitalOperationOutStatus.WaitForOperation);
				modelAndView.addObject("message", "锁定失败,只有状态为:‘" + status_s
						+ "’的提现记录才允许锁定!");
				return modelAndView;
			}
			capitalOperation.setfLastUpdateTime(Utils.getTimestamp());
			capitalOperation.setfAuditee_id(admin);
			capitalOperation.setFstatus(CapitalOperationOutStatus.OperationLock);
			this.capitaloperationService.updateObj(capitalOperation);
		}

		modelAndView.setViewName("ssadmin/comm/ajaxDone");
		modelAndView.addObject("statusCode", 200);
		modelAndView.addObject("message", "批量锁定成功");
		return modelAndView;
	}

}
