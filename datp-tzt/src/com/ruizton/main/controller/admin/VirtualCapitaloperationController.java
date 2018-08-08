package com.ruizton.main.controller.admin;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.ruizton.main.Enum.CapitalOperationInStatus;
import com.ruizton.main.Enum.CapitalOperationOutStatus;
import com.ruizton.main.Enum.CapitalOperationTypeEnum;
import com.ruizton.main.Enum.EntrustTypeEnum;
import com.ruizton.main.Enum.VirtualCapitalOperationInStatusEnum;
import com.ruizton.main.Enum.VirtualCapitalOperationOutStatusEnum;
import com.ruizton.main.Enum.VirtualCapitalOperationTypeEnum;
import com.ruizton.main.controller.BaseController;
import com.ruizton.main.model.BTCInfo;
import com.ruizton.main.model.BTCMessage;
import com.ruizton.main.model.Fvirtualcaptualoperation;
import com.ruizton.main.model.Fvirtualcointype;
import com.ruizton.main.model.Fvirtualwallet;
import com.ruizton.main.service.admin.AdminService;
import com.ruizton.main.service.admin.CapitaloperationService;
import com.ruizton.main.service.admin.EntrustService;
import com.ruizton.main.service.admin.UserService;
import com.ruizton.main.service.admin.VirtualCapitaloperationService;
import com.ruizton.main.service.admin.VirtualCoinService;
import com.ruizton.main.service.admin.VirtualWalletService;
import com.ruizton.main.service.admin.WalletService;
import com.ruizton.main.service.front.FrontUserService;
import com.ruizton.util.BTCUtils;
import com.ruizton.util.Utils;

@Controller
public class VirtualCapitaloperationController extends BaseController {
	@Autowired
	private FrontUserService frontUserService;
	@Autowired
	private AdminService adminService;
	@Autowired
	private WalletService walletService;
	@Autowired
	private CapitaloperationService capitaloperationService;
	@Autowired
	private VirtualWalletService virtualWalletService;
	@Autowired
	private VirtualCapitaloperationService virtualCapitaloperationService;
	@Autowired
	private EntrustService entrustService;
	@Autowired
	private VirtualCoinService virtualCoinService;
	@Autowired
	private HttpServletRequest request;
	// 每页显示多少条数据
	private int numPerPage = Utils.getNumPerPage();

	@RequestMapping("/ssadmin/virtualCaptualoperationList")
	public ModelAndView Index() throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("ssadmin/virtualCaptualoperationList1");
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
				filterSQL.append("fuser.fid like '%"+keyWord+"%' OR \n");
				filterSQL.append("fuser.fnickName like '%" + keyWord + "%' OR \n");
				filterSQL.append("fuser.frealName like '%" + keyWord + "%' OR \n");
				filterSQL.append("withdraw_virtual_address like '%" + keyWord
						+ "%' OR \n");
				filterSQL.append("recharge_virtual_address like '%" + keyWord
						+ "%' )\n");
			modelAndView.addObject("keywords", keyWord);
		}

		if (request.getParameter("ftype") != null && !("0").equals(request.getParameter("ftype"))) {
				
			filterSQL.append("and fvirtualcointype.fid='" + request.getParameter("ftype") + "'\n");
			modelAndView.addObject("ftype", request.getParameter("ftype"));
			
		} else {
			modelAndView.addObject("ftype", "0");
		}
		filterSQL.append("and fuser.fid is not null \n");

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

		List<Fvirtualcointype> type = this.virtualCoinService.findAll();
		 HashMap<String, String> typeMap = new HashMap<String, String>();
		for (Fvirtualcointype fvirtualcointype : type) {
			typeMap.put(fvirtualcointype.getFid(), fvirtualcointype.getFname());
		}
		typeMap.put("0", "全部");
		modelAndView.addObject("typeMap", typeMap);

		List<Fvirtualcaptualoperation> list = this.virtualCapitaloperationService
				.list((currentPage - 1) * numPerPage, numPerPage, filterSQL
						+ "", true);
		modelAndView.addObject("virtualCapitaloperationList", list);
		modelAndView.addObject("numPerPage", numPerPage);
		modelAndView.addObject("currentPage", currentPage);
		modelAndView.addObject("rel", "virtualCapitaloperationList");
		// 总数量
		modelAndView.addObject("totalCount", this.adminService.getAllCount(
				"Fvirtualcaptualoperation", filterSQL + ""));
		return modelAndView;
	}

	@RequestMapping("/ssadmin/virtualCapitalInList")
	public ModelAndView virtualCapitalInList() throws Exception {


		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("ssadmin/virtualCapitalInList");
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
		filterSQL.append("where 1=1 and ftype="
				+ VirtualCapitalOperationTypeEnum.COIN_IN + "\n");
		if (keyWord != null && keyWord.trim().length() > 0) {
				filterSQL.append("and (fuser.floginName like '%" + keyWord
						+ "%' OR \n");
				filterSQL.append("fuser.fid like '%"+keyWord+"%' OR \n");
				filterSQL.append("fuser.fnickName like '%" + keyWord + "%' OR \n");
				filterSQL.append("fuser.frealName like '%" + keyWord + "%' OR \n");
				filterSQL.append("withdraw_virtual_address like '%" + keyWord
						+ "%' OR \n");
				filterSQL.append("recharge_virtual_address like '%" + keyWord
						+ "%' )\n");
			modelAndView.addObject("keywords", keyWord);
		}

		if (request.getParameter("ftype") != null && !("0").equals(request.getParameter("ftype"))) {
			
			filterSQL.append("and fvirtualcointype.fid='" + request.getParameter("ftype") + "'\n");
			modelAndView.addObject("ftype", request.getParameter("ftype"));
		} else {
			modelAndView.addObject("ftype", "0");
		}

		filterSQL.append("and fuser.fid is not null \n");

		if (orderField != null && orderField.trim().length() > 0) {
			filterSQL.append("order by " + orderField + "\n");
		}
		if (orderDirection != null && orderDirection.trim().length() > 0) {
			filterSQL.append(orderDirection + "\n");
		}

		List<Fvirtualcointype> type = this.virtualCoinService.findAll();
		HashMap<String, String> typeMap = new HashMap<String, String>();
		for (Fvirtualcointype fvirtualcointype : type) {
			typeMap.put(fvirtualcointype.getFid(), fvirtualcointype.getFname());
		}
		typeMap.put("0", "全部");
		modelAndView.addObject("typeMap", typeMap);

		List<Fvirtualcaptualoperation> list = this.virtualCapitaloperationService
				.list((currentPage - 1) * numPerPage, numPerPage, filterSQL
						+ "", true);
		modelAndView.addObject("virtualCapitaloperationList", list);
		modelAndView.addObject("numPerPage", numPerPage);
		modelAndView.addObject("currentPage", currentPage);
		modelAndView.addObject("rel", "virtualCapitalInList");
		// 总数量
		modelAndView.addObject("totalCount", this.adminService.getAllCount(
				"Fvirtualcaptualoperation", filterSQL + ""));
		return modelAndView;
	}

	@RequestMapping("/ssadmin/virtualCapitalOutList")
	public ModelAndView virtualCapitalOutList() throws Exception {

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("ssadmin/virtualCapitalOutList");
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
		filterSQL.append("where 1=1 and ftype="
				+ VirtualCapitalOperationTypeEnum.COIN_OUT + "\n");
		filterSQL.append("and fstatus IN("
				+ VirtualCapitalOperationOutStatusEnum.WaitForOperation + ","
				+ VirtualCapitalOperationOutStatusEnum.OperationLock + ")\n");
		if (keyWord != null && keyWord.trim().length() > 0) {
				filterSQL.append("and (fuser.floginName like '%" + keyWord
						+ "%' OR \n");
				filterSQL.append("fuser.fid like '%"+keyWord+"%' OR \n");
				filterSQL.append("fuser.fnickName like '%" + keyWord + "%' OR \n");
				filterSQL.append("fuser.frealName like '%" + keyWord + "%' OR \n");
				filterSQL.append("withdraw_virtual_address like '%" + keyWord
						+ "%' OR \n");
				filterSQL.append("recharge_virtual_address like '%" + keyWord
						+ "%' )\n");
			modelAndView.addObject("keywords", keyWord);
		}

		if (request.getParameter("ftype") != null && !("0").equals(request.getParameter("ftype"))) {
			
			filterSQL.append("and fvirtualcointype.fid='" + request.getParameter("ftype") + "'\n");
			modelAndView.addObject("ftype", request.getParameter("ftype"));
		} else {
			modelAndView.addObject("ftype", "0");
		}
		if (orderField != null && orderField.trim().length() > 0) {
			filterSQL.append("order by " + orderField + "\n");
		}
		if (orderDirection != null && orderDirection.trim().length() > 0) {
			filterSQL.append(orderDirection + "\n");
		}

		List<Fvirtualcointype> type = this.virtualCoinService.findAll();
		HashMap<String, String> typeMap = new HashMap<String, String>();
		for (Fvirtualcointype fvirtualcointype : type) {
			typeMap.put(fvirtualcointype.getFid(), fvirtualcointype.getFname());
		}
		typeMap.put("0", "全部");
		modelAndView.addObject("typeMap", typeMap);

		List<Fvirtualcaptualoperation> list = this.virtualCapitaloperationService
				.list((currentPage - 1) * numPerPage, numPerPage, filterSQL
						+ "", true);
		modelAndView.addObject("virtualCapitaloperationList", list);
		modelAndView.addObject("numPerPage", numPerPage);
		modelAndView.addObject("currentPage", currentPage);
		modelAndView.addObject("rel", "virtualCapitalOutList");
		// 总数量
		modelAndView.addObject("totalCount", this.adminService.getAllCount(
				"Fvirtualcaptualoperation", filterSQL + ""));
		return modelAndView;
	}
	
	@RequestMapping("/ssadmin/virtualCapitalOutSucList")
	public ModelAndView virtualCapitalOutSucList() throws Exception {


		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("ssadmin/virtualCapitalOutSucList");
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
		filterSQL.append("where 1=1 and ftype="
				+ VirtualCapitalOperationTypeEnum.COIN_OUT + "\n");
		filterSQL.append("and fstatus IN("
				+ VirtualCapitalOperationOutStatusEnum.OperationSuccess +")\n");
		if (keyWord != null && keyWord.trim().length() > 0) {
				filterSQL.append("and (fuser.floginName like '%" + keyWord
						+ "%' OR \n");
				filterSQL.append("fuser.fid like '%"+keyWord+"%' OR \n");
				filterSQL.append("fuser.fnickName like '%" + keyWord + "%' OR \n");
				filterSQL.append("fuser.frealName like '%" + keyWord + "%' OR \n");
				filterSQL.append("withdraw_virtual_address like '%" + keyWord
						+ "%' OR \n");
				filterSQL.append("recharge_virtual_address like '%" + keyWord
						+ "%' )\n");
			modelAndView.addObject("keywords", keyWord);
		}

		if (request.getParameter("ftype") != null && !"0".equals(request.getParameter("ftype"))) {
			
			filterSQL.append("and fvirtualcointype.fid='" + request.getParameter("ftype") + "'\n");
			modelAndView.addObject("ftype", request.getParameter("ftype"));
			
		} else {
			modelAndView.addObject("ftype", "0");
		}
		if (orderField != null && orderField.trim().length() > 0) {
			filterSQL.append("order by " + orderField + "\n");
		}
		if (orderDirection != null && orderDirection.trim().length() > 0) {
			filterSQL.append(orderDirection + "\n");
		}

		List<Fvirtualcointype> type = this.virtualCoinService.findAll();
		HashMap<String, String> typeMap = new HashMap<String, String>();
		for (Fvirtualcointype fvirtualcointype : type) {
			typeMap.put(fvirtualcointype.getFid(), fvirtualcointype.getFname());
		}
		typeMap.put("0", "全部");
		modelAndView.addObject("typeMap", typeMap);

		List<Fvirtualcaptualoperation> list = this.virtualCapitaloperationService
				.list((currentPage - 1) * numPerPage, numPerPage, filterSQL
						+ "", true);
		modelAndView.addObject("virtualCapitaloperationList", list);
		modelAndView.addObject("numPerPage", numPerPage);
		modelAndView.addObject("currentPage", currentPage);
		modelAndView.addObject("rel", "virtualCapitalOutSucList");
		// 总数量
		modelAndView.addObject("totalCount", this.adminService.getAllCount(
				"Fvirtualcaptualoperation", filterSQL + ""));
		return modelAndView;
	}

	@RequestMapping("ssadmin/goVirtualCapitaloperationChangeStatus")
	public ModelAndView goVirtualCapitaloperationChangeStatus(
			@RequestParam(required = true) int type,
			@RequestParam(required = true) String uid) throws Exception {

		ModelAndView modelAndView = new ModelAndView();
		Fvirtualcaptualoperation fvirtualcaptualoperation = this.virtualCapitaloperationService
				.findById(uid);
		fvirtualcaptualoperation.setFlastUpdateTime(Utils.getTimestamp());

		String userId = fvirtualcaptualoperation.getFuser().getFid();
		Fvirtualcointype fvirtualcointype = fvirtualcaptualoperation
				.getFvirtualcointype();
		String coinTypeId = fvirtualcointype.getFid();
		List<Fvirtualwallet> virtualWallet = this.virtualWalletService
				.findByTwoProperty("fuser.fid", userId, "fvirtualcointype.fid",
						coinTypeId);
		if (virtualWallet == null || virtualWallet.size() == 0) {
			modelAndView.setViewName("ssadmin/comm/ajaxDone");
			modelAndView.addObject("statusCode", 300);
			modelAndView.addObject("message", "审核失败，会员虚拟币钱包信息异常!");
			return modelAndView;
		}
		Fvirtualwallet virtualWalletInfo = virtualWallet.get(0);

		int status = fvirtualcaptualoperation.getFstatus();
		String tips = "";
		switch (type) {
		case 1:
			tips = "锁定";
			if (status != CapitalOperationOutStatus.WaitForOperation) {
				modelAndView.setViewName("ssadmin/comm/ajaxDone");
				modelAndView.addObject("statusCode", 300);
				String status_s = CapitalOperationOutStatus
						.getEnumString(CapitalOperationOutStatus.WaitForOperation);
				modelAndView.addObject("message", "锁定失败,只有状态为:‘" + status_s
						+ "’的充值记录才允许锁定!");
				return modelAndView;
			}
			fvirtualcaptualoperation
					.setFstatus(VirtualCapitalOperationOutStatusEnum.OperationLock);
			break;
		case 2:
			tips = "取消锁定";
			if (status != CapitalOperationOutStatus.OperationLock) {
				modelAndView.setViewName("ssadmin/comm/ajaxDone");
				modelAndView.addObject("statusCode", 300);
				String status_s = CapitalOperationOutStatus
						.getEnumString(CapitalOperationOutStatus.OperationLock);
				modelAndView.addObject("message", "取消锁定失败,只有状态为:‘" + status_s
						+ "’的充值记录才允许取消锁定!");
				return modelAndView;
			}
			fvirtualcaptualoperation
					.setFstatus(VirtualCapitalOperationOutStatusEnum.WaitForOperation);
			break;
		case 3:
			tips = "取消提现";
			if (status == CapitalOperationOutStatus.Cancel) {
				modelAndView.setViewName("ssadmin/comm/ajaxDone");
				modelAndView.addObject("statusCode", 300);
				modelAndView.addObject("message", "取消提现失败,该记录已处于取消状态!");
				return modelAndView;
			}
			double fee = fvirtualcaptualoperation.getFfees();
			double famount = fvirtualcaptualoperation.getFamount();
			fvirtualcaptualoperation
					.setFstatus(VirtualCapitalOperationOutStatusEnum.Cancel);
			virtualWalletInfo.setFfrozen(virtualWalletInfo.getFfrozen() - fee
					- famount);
			virtualWalletInfo.setFtotal(virtualWalletInfo.getFtotal() + fee
					+ famount);
			virtualWalletInfo.setFlastUpdateTime(Utils.getTimestamp());
			break;
		}

		boolean flag = false;
		try {
			this.virtualCapitaloperationService
					.updateObj(fvirtualcaptualoperation);
			this.virtualWalletService.updateObj(virtualWalletInfo);
			flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (flag) {
			modelAndView.setViewName("ssadmin/comm/ajaxDone");
			modelAndView.addObject("statusCode", 200);
			modelAndView.addObject("message", tips + "成功！");
		} else {
			modelAndView.setViewName("ssadmin/comm/ajaxDone");
			modelAndView.addObject("statusCode", 300);
			modelAndView.addObject("message", "未知错误，请刷新列表后重试！");
		}

		return modelAndView;
	}

	@RequestMapping("ssadmin/goVirtualCapitaloperationJSP")
	public ModelAndView goVirtualCapitaloperationJSP() throws Exception {

		String url = request.getParameter("url");
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName(url);
		String xid = "";
		BTCMessage msg = new BTCMessage();
		if (request.getParameter("uid") != null && request.getParameter("uid") !="") {
			String fid = request.getParameter("uid");
			Fvirtualcaptualoperation virtualcaptualoperation = this.virtualCapitaloperationService
					.findById(fid);
			xid = virtualcaptualoperation.getFtradeUniqueNumber();
			msg.setACCESS_KEY(virtualcaptualoperation.getFvirtualcointype()
					.getFaccess_key());
			msg.setIP(virtualcaptualoperation.getFvirtualcointype().getFip());
			msg.setPORT(virtualcaptualoperation.getFvirtualcointype()
					.getFport());
			msg.setSECRET_KEY(virtualcaptualoperation.getFvirtualcointype()
					.getFsecrt_key());
			modelAndView.addObject("virtualCapitaloperation",
					virtualcaptualoperation);
		}
		if (request.getParameter("type") != null
				&& request.getParameter("type").equals("ViewStatus")) {
			BTCUtils btc = new BTCUtils(msg);
			BTCInfo btcInfo = btc.gettransactionValue(xid, "send");
			modelAndView.addObject("confirmations", btcInfo.getConfirmations());
		}
		return modelAndView;
	}

	@RequestMapping("ssadmin/virtualCapitalOutAudit")
	public ModelAndView virtualCapitalOutAudit() throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("ssadmin/comm/ajaxDone");
		String fid = request.getParameter("uid");
		Fvirtualcaptualoperation virtualcaptualoperation = this.virtualCapitaloperationService.findById(fid);
		int status = virtualcaptualoperation.getFstatus();
		if (status != VirtualCapitalOperationOutStatusEnum.OperationLock) {
			modelAndView.setViewName("ssadmin/comm/ajaxDone");
			modelAndView.addObject("statusCode", 300);
			String status_s = VirtualCapitalOperationOutStatusEnum
					.getEnumString(VirtualCapitalOperationOutStatusEnum.OperationLock);
			modelAndView.addObject("message", "审核失败,只有状态为:" + status_s
					+ "的提现记录才允许审核!");
			return modelAndView;
		}
		// 根据用户查钱包最后修改时间
		String userId = virtualcaptualoperation.getFuser().getFid();
		Fvirtualcointype fvirtualcointype = virtualcaptualoperation
				.getFvirtualcointype();
		String coinTypeId = fvirtualcointype.getFid();

		Fvirtualwallet virtualWalletInfo = frontUserService.findVirtualWalletByUser(userId, coinTypeId);
		double amount = Utils.getDouble(virtualcaptualoperation.getFamount()+virtualcaptualoperation.getFfees(), 4);
		double frozenRmb = Utils.getDouble(virtualWalletInfo.getFfrozen(), 4);
		if (frozenRmb-amount < -0.0001) {
			modelAndView.addObject("statusCode", 300);
			modelAndView.addObject("message", "审核失败,冻结数量:" + frozenRmb
					+ "小于提现数量:" + amount + "，操作异常!");
			return modelAndView;
		}
		String password = request.getParameter("fpassword");
		if(password == null || password.trim().length() ==0){
			modelAndView.addObject("statusCode", 300);
			modelAndView.addObject("message", "请输入审核密码!");
			return modelAndView;
		}
		password = password.trim();
		BTCMessage btcMsg = new BTCMessage();
		btcMsg.setACCESS_KEY(fvirtualcointype.getFaccess_key());
		btcMsg.setSECRET_KEY(fvirtualcointype.getFsecrt_key());
		btcMsg.setIP(fvirtualcointype.getFip());
		btcMsg.setPASSWORD(password);
		btcMsg.setPORT(fvirtualcointype.getFport());
		BTCUtils btcUtils = new BTCUtils(btcMsg);

		try {
			double balance = btcUtils.getbalanceValue();
			if (balance < amount+0.1d) {
				modelAndView.addObject("statusCode", 300);
				modelAndView.addObject("message", "审核失败，钱包余额：" + balance + "小于"
						+ amount);
				return modelAndView;
			}
			
			boolean isTrue = btcUtils.validateaddress(virtualcaptualoperation.getWithdraw_virtual_address());
			if(!isTrue){
				modelAndView.addObject("statusCode", 300);
				modelAndView.addObject("message", "提现地址无效");
				return modelAndView;
			}
		} catch (Exception e1) {
			modelAndView.addObject("statusCode", 300);
			modelAndView.addObject("message", "审核失败，钱包连接失败");
			return modelAndView;
		}
        if(virtualcaptualoperation.getFtradeUniqueNumber() != null &&
        		virtualcaptualoperation.getFtradeUniqueNumber().trim().length() >0){
			modelAndView.addObject("statusCode", 300);
			modelAndView.addObject("message", "非法操作！请检查钱包！");
			return modelAndView;
        }
		
		// 充值操作
		virtualcaptualoperation
				.setFstatus(VirtualCapitalOperationOutStatusEnum.OperationSuccess);
		virtualcaptualoperation.setFlastUpdateTime(Utils.getTimestamp());

		// 钱包操作
		virtualWalletInfo.setFlastUpdateTime(Utils.getTimestamp());
		virtualWalletInfo.setFfrozen(virtualWalletInfo.getFfrozen() - amount);
		try {
			this.virtualCapitaloperationService.updateCapital(
					virtualcaptualoperation, virtualWalletInfo, btcUtils);
		} catch (Exception e) {
			modelAndView.addObject("statusCode", 300);
			modelAndView.addObject("message", e.getMessage());
			return modelAndView;
		}
		modelAndView.addObject("statusCode", 200);
		modelAndView.addObject("message", "审核成功");
		modelAndView.addObject("callbackType", "closeCurrent");
		return modelAndView;
	}
}
