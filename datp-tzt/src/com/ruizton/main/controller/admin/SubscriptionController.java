package com.ruizton.main.controller.admin;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.ditp.entity.StationMail;
import com.ditp.service.StationMailService;
import com.ruizton.main.Enum.SubscriptionTypeEnum;
import com.ruizton.main.controller.BaseController;
import com.ruizton.main.dao.FuserDAO;
import com.ruizton.main.dao.FvirtualcointypeDAO;
import com.ruizton.main.model.Fadmin;
import com.ruizton.main.model.Fsubscription;
import com.ruizton.main.model.Fuser;
import com.ruizton.main.model.Fvirtualcointype;
import com.ruizton.main.service.admin.AdminService;
import com.ruizton.main.service.admin.SubscriptionService;
import com.ruizton.main.service.admin.VirtualCoinService;
import com.ruizton.util.Utils;

@Controller
public class SubscriptionController extends BaseController {
	@Autowired
	private SubscriptionService subscriptionService;
	@Autowired
	private AdminService adminService;
	@Autowired
	private HttpServletRequest request;
	@Autowired
	private VirtualCoinService virtualCoinService;
	@Autowired
	private StationMailService stationMailService;
	@Autowired
	private FvirtualcointypeDAO fvirtualcointypeDAO;
	@Autowired
	private FuserDAO fuserDAO;

	// 每页显示多少条数据
	private int numPerPage = 20;

	@RequestMapping("/ssadmin/subscriptionList")
	public ModelAndView subscriptionList() throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("ssadmin/subscriptionList");
		// 当前页
		int currentPage = 1;
		String orderField = request.getParameter("orderField");
		String orderDirection = request.getParameter("orderDirection");
		if (request.getParameter("pageNum") != null) {
			currentPage = Integer.parseInt(request.getParameter("pageNum"));
		}
		StringBuffer filter = new StringBuffer();
		filter.append("where 1=1 \n");

		if (request.getParameter("ftype") != null && !request.getParameter("ftype").equals("0")) {

			filter.append("and fvirtualcointype.fid='" + request.getParameter("ftype") + "'\n");
			modelAndView.addObject("ftype", request.getParameter("ftype"));

		} else {
			modelAndView.addObject("ftype", "0");
		}

		if (request.getParameter("ftype1") != null && !request.getParameter("ftype1").equals("0")) {

			filter.append("and fvirtualcointypeCost.fid='" + request.getParameter("ftype1") + "'\n");
			modelAndView.addObject("ftype1", request.getParameter("ftype1"));

		} else {
			modelAndView.addObject("ftype1", "0");
		}

		filter.append("and ftype=" + SubscriptionTypeEnum.COIN + "\n");

		if (orderField != null && orderField.trim().length() > 0) {
			filter.append("order by " + orderField + "\n");
		}
		if (orderDirection != null && orderDirection.trim().length() > 0) {
			filter.append(orderDirection + "\n");
		}

		List<Fvirtualcointype> type = this.virtualCoinService.findAll();
		HashMap<String, String> typeMap = new HashMap<String, String>();
		for (Fvirtualcointype fvirtualcointype : type) {
			typeMap.put(fvirtualcointype.getFid(), fvirtualcointype.getFname());
		}
		typeMap.put("0", "全部");
		modelAndView.addObject("typeMap", typeMap);

		List<Fsubscription> list = this.subscriptionService.list((currentPage - 1) * numPerPage, numPerPage,
				filter + "", true);
		modelAndView.addObject("subscriptionList", list);
		modelAndView.addObject("numPerPage", numPerPage);
		modelAndView.addObject("rel", "subscriptionList");
		modelAndView.addObject("currentPage", currentPage);
		// 总数量
		modelAndView.addObject("totalCount", this.adminService.getAllCount("Fsubscription", filter + ""));
		return modelAndView;
	}

	@RequestMapping("ssadmin/goSubscriptionJSP")
	public ModelAndView goSubscriptionJSP() throws Exception {
		String url = request.getParameter("url");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName(url);
		if (request.getParameter("uid") != null) {
			String fid = request.getParameter("uid");
			Fsubscription subscription = this.subscriptionService.findById(fid);
			modelAndView.addObject("subscription", subscription);
			if (subscription.getFendTime() != null) {
				String e = sdf.format(subscription.getFendTime());
				request.setAttribute("e", e);
			}
			if (subscription.getFbeginTime() != null) {
				String s = sdf.format(subscription.getFbeginTime());
				request.setAttribute("s", s);
			}
		}

		List<Fvirtualcointype> allType = this.virtualCoinService.findAll();
		modelAndView.addObject("allType", allType);

		Map<String, String> coins = new HashMap<String, String>();
		coins.put("0", "人民币");
		for (Fvirtualcointype fvirtualcointype : allType) {
			coins.put(fvirtualcointype.getFid(), fvirtualcointype.getFname());
		}
		modelAndView.addObject("coins", coins);

		return modelAndView;
	}

	@RequestMapping("ssadmin/saveSubscription")
	public ModelAndView saveSubscription() throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("ssadmin/comm/ajaxDone");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String vid = request.getParameter("vid");
		String vid1 = request.getParameter("vid1");
		Fvirtualcointype vt = this.virtualCoinService.findById(vid);
		Fvirtualcointype vt1 = this.virtualCoinService.findById(vid1);
		String fisopen = request.getParameter("fisopen");
		String fisRMB = request.getParameter("fisRMB");
		double ftotal = Double.valueOf(request.getParameter("ftotal"));
		double fprice = Double.valueOf(request.getParameter("fprice"));
		int fbuyCount = Integer.parseInt(request.getParameter("fbuyCount"));
		int fbuyTimes = Integer.parseInt(request.getParameter("fbuyTimes"));
		String fbeginTimes = request.getParameter("fbeginTime");
		String fendTimes = request.getParameter("fendTime");
		Date beginDate = sdf.parse(fbeginTimes);
		Date endDate = sdf.parse(fendTimes);
		if (beginDate.compareTo(endDate) > 0) {
			modelAndView.addObject("statusCode", 300);
			modelAndView.addObject("message", "开始时间不能大于结束时间");
			return modelAndView;
		}
		Fsubscription subscription = new Fsubscription();
		subscription.setFbeginTime(Timestamp.valueOf(fbeginTimes));
		subscription.setFendTime(Timestamp.valueOf(fendTimes));
		if (fisopen != null && fisopen.trim().length() > 0) {
			subscription.setFisopen(true);
		} else {
			subscription.setFisopen(false);
		}
		if (fisRMB != null && fisRMB.trim().length() > 0) {
			subscription.setFisRMB(true);
		} else {
			subscription.setFisRMB(false);
		}
		subscription.setFvirtualcointype(vt);
		subscription.setFvirtualcointypeCost(vt1);
		subscription.setFbuyCount(fbuyCount);
		subscription.setFbuyTimes(fbuyTimes);
		subscription.setFprice(fprice);
		subscription.setFtotal(ftotal);
		subscription.setFcreateTime(Utils.getTimestamp());
		subscription.setFtype(SubscriptionTypeEnum.COIN);
		subscription.setFqty(0d);
		subscription.setFtitle(request.getParameter("ftitle"));
		this.subscriptionService.saveObj(subscription);
		// 消息提醒 如果开启就进行提醒
		if (subscription.getFisopen()) {
			Fadmin fadmin = getAdminSession(request);
			stationMailService.sendStationMail(subscription, fadmin);
		}

		modelAndView.addObject("statusCode", 200);
		modelAndView.addObject("message", "新增成功");
		modelAndView.addObject("callbackType", "closeCurrent");
		return modelAndView;
	}

	@RequestMapping("ssadmin/deleteSubscription")
	public ModelAndView deleteSubscription() throws Exception {
		String fid = request.getParameter("uid");
		this.subscriptionService.deleteObj(fid);

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("ssadmin/comm/ajaxDone");
		modelAndView.addObject("statusCode", 200);
		modelAndView.addObject("message", "删除成功");
		return modelAndView;
	}

	@RequestMapping("ssadmin/updateSubscription")
	public ModelAndView updateSubscription() throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("ssadmin/comm/ajaxDone");
		String fid = request.getParameter("fid");
		Fsubscription subscription = this.subscriptionService.findById(fid);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String vid = request.getParameter("vid");
		String vid1 = request.getParameter("vid1");
		Fvirtualcointype vt = this.virtualCoinService.findById(vid);
		Fvirtualcointype vt1 = this.virtualCoinService.findById(vid1);
		String fisopen = request.getParameter("fisopen");
		String fisRMB = request.getParameter("fisRMB");
		double ftotal = Double.valueOf(request.getParameter("ftotal"));
		double fprice = Double.valueOf(request.getParameter("fprice"));
		int fbuyCount = Integer.parseInt(request.getParameter("fbuyCount"));
		int fbuyTimes = Integer.parseInt(request.getParameter("fbuyTimes"));
		String fbeginTimes = request.getParameter("fbeginTime");
		String fendTimes = request.getParameter("fendTime");
		Date beginDate = sdf.parse(fbeginTimes);
		Date endDate = sdf.parse(fendTimes);
		if (beginDate.compareTo(endDate) > 0) {
			modelAndView.addObject("statusCode", 300);
			modelAndView.addObject("message", "开始时间不能大于结束时间");
			return modelAndView;
		}

		subscription.setFbeginTime(Timestamp.valueOf(fbeginTimes));
		subscription.setFendTime(Timestamp.valueOf(fendTimes));
		if (fisopen != null && fisopen.trim().length() > 0) {
			subscription.setFisopen(true);
		} else {
			subscription.setFisopen(false);
		}
		if (fisRMB != null && fisRMB.trim().length() > 0) {
			subscription.setFisRMB(true);
		} else {
			subscription.setFisRMB(false);
		}
		subscription.setFvirtualcointypeCost(vt1);
		subscription.setFvirtualcointype(vt);
		subscription.setFbuyCount(fbuyCount);
		subscription.setFbuyTimes(fbuyTimes);
		subscription.setFprice(fprice);
		subscription.setFtotal(ftotal);
		subscription.setFtype(SubscriptionTypeEnum.COIN);
		subscription.setFtitle(request.getParameter("ftitle"));
		this.subscriptionService.updateObj(subscription);

		// 消息提醒 如果开启就进行提醒
		if (subscription.getFisopen()) {
			Fadmin fadmin = getAdminSession(request);
			stationMailService.sendStationMail(subscription, fadmin);
			// sendStationMail(subscription);
		}

		modelAndView.addObject("statusCode", 200);
		modelAndView.addObject("message", "修改成功");
		modelAndView.addObject("callbackType", "closeCurrent");
		return modelAndView;
	}

	@RequestMapping("/ssadmin/subscriptionList1")
	public ModelAndView subscriptionList1() throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("ssadmin/subscriptionList1");
		// 当前页
		int currentPage = 1;
		String orderField = request.getParameter("orderField");
		String orderDirection = request.getParameter("orderDirection");
		if (request.getParameter("pageNum") != null) {
			currentPage = Integer.parseInt(request.getParameter("pageNum"));
		}
		StringBuffer filter = new StringBuffer();
		filter.append("where 1=1 \n");

		if (request.getParameter("ftype") != null) {
			String type = request.getParameter("ftype");
			if (type != null) {
				filter.append("and fvirtualcointype.fid='" + type + "'\n");
			}
			modelAndView.addObject("ftype", type);
		} else {
			modelAndView.addObject("ftype", "0");
		}

		filter.append("and ftype=" + SubscriptionTypeEnum.RMB + "\n");

		if (orderField != null && orderField.trim().length() > 0) {
			filter.append("order by " + orderField + "\n");
		}
		if (orderDirection != null && orderDirection.trim().length() > 0) {
			filter.append(orderDirection + "\n");
		}

		List<Fvirtualcointype> type = this.virtualCoinService.findAll();
		HashMap<String, String> typeMap = new HashMap<String, String>();
		for (Fvirtualcointype fvirtualcointype : type) {
			typeMap.put(fvirtualcointype.getFid(), fvirtualcointype.getFname());
		}
		typeMap.put("0", "全部");
		modelAndView.addObject("typeMap", typeMap);

		List<Fsubscription> list = this.subscriptionService.list((currentPage - 1) * numPerPage, numPerPage,
				filter + "", true);
		modelAndView.addObject("subscriptionList1", list);
		modelAndView.addObject("numPerPage", numPerPage);
		modelAndView.addObject("rel", "subscriptionList1");
		modelAndView.addObject("currentPage", currentPage);
		// 总数量
		modelAndView.addObject("totalCount", this.adminService.getAllCount("Fsubscription", filter + ""));
		return modelAndView;
	}

	@RequestMapping("ssadmin/saveSubscription1")
	public ModelAndView saveSubscription1() throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("ssadmin/comm/ajaxDone");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String vid = request.getParameter("vid");
		String vid1 = request.getParameter("vid1");
		Fvirtualcointype vt = this.virtualCoinService.findById(vid);
		Fvirtualcointype vt1 = this.virtualCoinService.findById(vid1);
		String fisopen = request.getParameter("fisopen");
		// String fisICO = request.getParameter("fisICO");
		double ftotal = Double.valueOf(request.getParameter("ftotal"));
		double fprice = Double.valueOf(request.getParameter("fprice"));
		int fbuyCount = Integer.parseInt(request.getParameter("fbuyCount"));
		int fbuyTimes = Integer.parseInt(request.getParameter("fbuyTimes"));
		String fbeginTimes = request.getParameter("fbeginTime");
		String fendTimes = request.getParameter("fendTime");
		Date beginDate = sdf.parse(fbeginTimes);
		Date endDate = sdf.parse(fendTimes);
		if (beginDate.compareTo(endDate) > 0) {
			modelAndView.addObject("statusCode", 300);
			modelAndView.addObject("message", "开始时间不能大于结束时间");
			return modelAndView;
		}
		Fsubscription subscription = new Fsubscription();
		subscription.setFbeginTime(Timestamp.valueOf(fbeginTimes));
		subscription.setFendTime(Timestamp.valueOf(fendTimes));
		if (fisopen != null && fisopen.trim().length() > 0) {
			subscription.setFisopen(true);
		} else {
			subscription.setFisopen(false);
		}
		subscription.setFisICO(false);
		subscription.setFprice(fprice);

		subscription.setFvirtualcointype(vt);
		subscription.setFvirtualcointypeCost(vt1);
		subscription.setFbuyCount(fbuyCount);
		subscription.setFbuyTimes(fbuyTimes);

		subscription.setFtotal(ftotal);
		subscription.setFnumber(0);
		subscription.setFcreateTime(Utils.getTimestamp());
		subscription.setFtype(SubscriptionTypeEnum.RMB);
		subscription.setFtitle(request.getParameter("ftitle"));
		subscription.setFcontent(request.getParameter("fcontent"));
		subscription.setFminbuyCount(Integer.parseInt(request.getParameter("fminbuyCount")));
		subscription.setFtotalqty(Double.valueOf(request.getParameter("ftotalqty")));
		subscription.setFqty(0d);
		this.subscriptionService.saveObj(subscription);

		// 消息提醒 如果开启就进行提醒
		if (subscription.getFisopen()) {
			Fadmin fadmin = getAdminSession(request);
			stationMailService.sendStationMail(subscription, fadmin);
			// sendStationMail(subscription);
		}

		modelAndView.addObject("statusCode", 200);
		modelAndView.addObject("message", "新增成功");
		modelAndView.addObject("callbackType", "closeCurrent");
		return modelAndView;
	}

	@RequestMapping("ssadmin/updateSubscription1")
	public ModelAndView updateSubscription1() throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("ssadmin/comm/ajaxDone");
		String fid = request.getParameter("fid");
		String vid1 = request.getParameter("vid1");
		Fsubscription subscription = this.subscriptionService.findById(fid);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String vid = request.getParameter("vid");
		Fvirtualcointype vt = this.virtualCoinService.findById(vid);
		Fvirtualcointype vt1 = this.virtualCoinService.findById(vid1);
		String fisopen = request.getParameter("fisopen");
		double ftotal = Double.valueOf(request.getParameter("ftotal"));
		double fprice = Double.valueOf(request.getParameter("fprice"));
		int fbuyCount = Integer.parseInt(request.getParameter("fbuyCount"));
		int fbuyTimes = Integer.parseInt(request.getParameter("fbuyTimes"));
		String fbeginTimes = request.getParameter("fbeginTime");
		String fendTimes = request.getParameter("fendTime");
		Date beginDate = sdf.parse(fbeginTimes);
		Date endDate = sdf.parse(fendTimes);
		if (beginDate.compareTo(endDate) > 0) {
			modelAndView.addObject("statusCode", 300);
			modelAndView.addObject("message", "开始时间不能大于结束时间");
			return modelAndView;
		}

		subscription.setFbeginTime(Timestamp.valueOf(fbeginTimes));
		subscription.setFendTime(Timestamp.valueOf(fendTimes));
		if (fisopen != null && fisopen.trim().length() > 0) {
			subscription.setFisopen(true);
		} else {
			subscription.setFisopen(false);
		}
		if (subscription.isFisICO()) {
			subscription.setFprice(0d);
		} else {
			subscription.setFprice(fprice);
		}
		subscription.setFvirtualcointype(vt);
		subscription.setFvirtualcointypeCost(vt1);
		subscription.setFbuyCount(fbuyCount);
		subscription.setFbuyTimes(fbuyTimes);

		subscription.setFtotal(ftotal);
		subscription.setFnumber(0);
		subscription.setFcontent(request.getParameter("fcontent"));
		subscription.setFtitle(request.getParameter("ftitle"));
		subscription.setFtype(SubscriptionTypeEnum.RMB);
		subscription.setFminbuyCount(Integer.parseInt(request.getParameter("fminbuyCount")));
		subscription.setFtotalqty(Double.valueOf(request.getParameter("ftotalqty")));
		this.subscriptionService.updateObj(subscription);

		// 消息提醒 如果开启就进行提醒
		if (subscription.getFisopen()) {
			Fadmin fadmin = getAdminSession(request);
			stationMailService.sendStationMail(subscription, fadmin);
			// sendStationMail(subscription);
		}
		modelAndView.addObject("statusCode", 200);
		modelAndView.addObject("message", "修改成功");
		modelAndView.addObject("callbackType", "closeCurrent");
		return modelAndView;
	}
}
