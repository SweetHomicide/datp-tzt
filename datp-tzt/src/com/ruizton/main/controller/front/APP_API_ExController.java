package com.ruizton.main.controller.front;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.ruizton.main.Enum.CountLimitTypeEnum;
import com.ruizton.main.Enum.SubStatusEnum;
import com.ruizton.main.Enum.SubscriptionTypeEnum;
import com.ruizton.main.auto.RealTimeData;
import com.ruizton.main.controller.BaseController;
import com.ruizton.main.model.Fentrust;
import com.ruizton.main.model.Fsubscription;
import com.ruizton.main.model.Fsubscriptionlog;
import com.ruizton.main.model.Fuser;
import com.ruizton.main.model.Fvirtualcointype;
import com.ruizton.main.model.Fvirtualwallet;
import com.ruizton.main.service.admin.SubscriptionService;
import com.ruizton.main.service.front.FrontTradeService;
import com.ruizton.main.service.front.FrontUserService;
import com.ruizton.main.service.front.FrontValidateService;
import com.ruizton.util.Comm;
import com.ruizton.util.Constant;
import com.ruizton.util.Utils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Controller
@Scope("prototype")
public class APP_API_ExController extends BaseController {
	@Autowired
	private RealTimeData realTimeData;
	@Autowired
	private SubscriptionService subscriptionService;
	@Autowired
	private FrontTradeService frontTradeService;
	@Autowired
	private FrontValidateService frontValidateService;
	@Autowired
	private FrontUserService frontUserService;
	public final static String Result = "result";
	public final static String ErrorCode = "code";
	public final static String Value = "value";
	public final static String LoginToken = "loginToken";
	public final static String CurrentPage = "currentPage";
	public final static String TotalPage = "totalPage";
	public final static String LastUpdateTime = "lastUpdateTime";
	public final static String Fid = "Fid";

	private String curLoginToken = null;
	int maxResult = Constant.AppRecordPerPage;

	@ResponseBody
	@RequestMapping(value = "/appApi_ex", produces = JsonEncode)
	public String appApi(HttpServletRequest request, @RequestParam(required = false, defaultValue = "0") String action)
			throws Exception {
		request.setCharacterEncoding("UTF-8");
		curLoginToken = request.getParameter(LoginToken);
		Integer actionInteger = ApiConstant.getInteger(action);

		// 判断是否需要登录才能操作
		if (actionInteger / 100 == 2) {
			boolean isLogin = this.realTimeData.isAppLogin(this.curLoginToken, false);
			if (isLogin == false) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.accumulate(Result, false);
				jsonObject.accumulate(ErrorCode, -10001);// 未登录
				jsonObject.accumulate(Value, "未登录");// 未登录
				return jsonObject.toString();
			}
		}

		String ret = "";
		switch (actionInteger) {
		case 0: {
			JSONObject jsonObject = new JSONObject();
			jsonObject.accumulate(Result, false);
			jsonObject.accumulate(ErrorCode, -10000);// 非法提交
			jsonObject.accumulate(Value, "API不存在");// 未登录
			ret = jsonObject.toString();
		}
			break;

		default:
			try {
				Method method = this.getClass().getDeclaredMethod(action, HttpServletRequest.class);
				ret = (String) method.invoke(this, request);
			} catch (Exception e) {
				ret = ApiConstant.getUnknownError(e);
			}
			break;
		}

		if (Constant.isRelease == false) {
			System.out.println(ret);
		}
		return ret;
	}

	/**
	 * 获取兑换列表
	 * 
	 * @param request
	 * @return
	 */
	public String exchangeAppAPI(HttpServletRequest request) {
		try {
			JSONObject jsonObject = new JSONObject();
			if(Comm.getISHIDDEN_EX())
			{
				jsonObject.accumulate(Result, false);
				jsonObject.accumulate(ErrorCode, -1);
				jsonObject.accumulate(Value, "禁止兑换");
				return jsonObject.toString();
			}
			// Fuser fuser = this.realTimeData.getAppFuser(this.curLoginToken);
			// int type = Integer.parseInt(request.getParameter("type"));
			int currentPage = 1;
			int totalPage = 0;
			int pageSize = Comm.getEX_PAGE();
			// 查询内容
			String searchName = request.getParameter("Content");
			currentPage = Integer.parseInt(request.getParameter("currentPage"));
			currentPage = currentPage < 1 ? 1 : currentPage;

			String filter = "where fisopen=1 and ftype=" + SubscriptionTypeEnum.COIN;
			if (searchName != null && !"".equals(searchName)) {
				filter += " and (ftitle  like '%" + searchName + "%' or fvirtualcointype.fname like '%" + searchName
						+ "%' or fvirtualcointypeCost.fname like '%" + searchName + "%')";
			}
			filter += " order by fcreateTime desc";
			List<Fsubscription> fsubscriptionList = this.subscriptionService
					.list((Integer.valueOf(currentPage) - 1) *pageSize, pageSize, filter, true);
			int count = this.subscriptionService.list(0, 0, filter, false).size();
			totalPage=count / pageSize + ((count % pageSize) == 0 ? 0 : 1);

			jsonObject.accumulate(Result, true);
			jsonObject.accumulate(ErrorCode, 0);
			jsonObject.accumulate(Value, "");
			jsonObject.accumulate("totalPage", totalPage);
			jsonObject.accumulate("currentPage", currentPage);

			JSONArray jsonArray = new JSONArray();
			for (int i = 0; i < fsubscriptionList.size(); i++) {
				JSONObject item = new JSONObject();
				Fsubscription fsubscription = fsubscriptionList.get(i);
				item.accumulate("fid", fsubscription.getFid());
				item.accumulate("ftitle", fsubscription.getFtitle());
				item.accumulate("fvirtualcointypeCostFurl", fsubscription.getFvirtualcointypeCost().getFurl());
				item.accumulate("fvirtualcointypeFurl", fsubscription.getFvirtualcointype().getFurl());
				item.accumulate("fbeginTime",
						new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(fsubscription.getFbeginTime()));
				item.accumulate("fendTime",
						new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(fsubscription.getFendTime()));
				item.accumulate("fprice", fsubscription.getFprice());
				item.accumulate("fviCostfShortName", fsubscription.getFvirtualcointypeCost().getfShortName());
				item.accumulate("fvirtypefShortName", fsubscription.getFvirtualcointype().getfShortName());
				item.accumulate("ftotal", fsubscription.getFtotal());
				item.accumulate("fAlreadyByCount", fsubscription.getfAlreadyByCount());
				jsonArray.add(item);

			}
			jsonObject.accumulate("list", jsonArray);
			return jsonObject.toString();
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			return ApiConstant.getUnknownError(e);
		}
	}

	public String exchange(HttpServletRequest request) throws Exception {
		String id = "";
		JSONObject jsonObject = new JSONObject();
		try {
			if(Comm.getISHIDDEN_EX())
			{
				jsonObject.accumulate(Result, false);
				jsonObject.accumulate(ErrorCode, -1);
				jsonObject.accumulate(Value, "禁止兑换");
				return jsonObject.toString();
			}
			if (request.getParameter("fid") == null || "".equals(request.getParameter("fid"))) {
				jsonObject.accumulate(Result, false);
				jsonObject.accumulate(ErrorCode, -1);
				jsonObject.accumulate(Value, "兑换id不能为空");
				return jsonObject.toString();
			}
			jsonObject.accumulate(Result, true);
			jsonObject.accumulate(ErrorCode, 0);
			jsonObject.accumulate(Value, "");
			id = request.getParameter("fid");
			// Fuser fuser =
			// frontUserService.findById("4028d0815af3e0a1015af40ff33600cc");
			Fuser fuser = this.realTimeData.getAppFuser(this.curLoginToken);
			Fsubscription fsubscription = this.frontTradeService.findFsubscriptionById(id);
			if (fsubscription == null || fsubscription.getFtype() != SubscriptionTypeEnum.COIN) {
				fsubscription = this.frontTradeService.findFirstSubscription(SubscriptionTypeEnum.COIN);
				if (fsubscription == null) {
				}
			}
			Fvirtualcointype fvirtualcointype = fsubscription.getFvirtualcointype();

			String coinName = fsubscription.getFvirtualcointypeCost().getFname();

			double coinAmt = this.frontUserService
					.findVirtualWalletByUser(fuser.getFid(), fsubscription.getFvirtualcointypeCost().getFid())
					.getFtotal();

			int begin = 0;
			long now = Utils.getTimestamp().getTime();
			if (fsubscription.getFbeginTime().getTime() > now) {
				// 没开始
				begin = 0;
			}

			if (fsubscription.getFbeginTime().getTime() < now && fsubscription.getFendTime().getTime() > now) {
				// 进行中
				begin = 1;
			}

			if (fsubscription.getFendTime().getTime() < now) {
				// 结束
				begin = 2;
			}

			// 认购记录
			List<Fsubscriptionlog> fsubscriptionlogs = this.frontTradeService.findFsubScriptionLog(fuser,
					fsubscription.getFid());

			// 可购买数量
			int buyCount = fsubscription.getFbuyCount();
			for (int i = 0; i < fsubscriptionlogs.size(); i++) {
				buyCount -= fsubscriptionlogs.get(i).getFcount();
			}
			buyCount = buyCount < 0 ? 0 : buyCount;

			// 可购买次数
			int buyTimes = fsubscription.getFbuyTimes() - fsubscriptionlogs.size();
			buyTimes = buyTimes < 0 ? 0 : buyTimes;
			jsonObject.accumulate("coinName", coinName);// 支付名称
			jsonObject.accumulate("coinAmt", coinAmt);// 账户余额
			// jsonObject.accumulate("buyTimes", buyTimes);//剩余兑换次数
			JSONArray jsonArray = new JSONArray();
			JSONObject item = new JSONObject();
			item.accumulate("fid", fsubscription.getFid());
			item.accumulate("ftitle", fsubscription.getFtitle());
			item.accumulate("fvirtualcointypeCostFurl", fsubscription.getFvirtualcointypeCost().getFurl());
			item.accumulate("fvirtualcointypeFurl", fsubscription.getFvirtualcointype().getFurl());
			item.accumulate("fbeginTime",
					new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(fsubscription.getFbeginTime()));
			item.accumulate("fendTime",
					new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(fsubscription.getFendTime()));
			item.accumulate("fprice", fsubscription.getFprice());
			item.accumulate("fviCostfShortName", fsubscription.getFvirtualcointypeCost().getfShortName());
			item.accumulate("fvirtypefShortName", fsubscription.getFvirtualcointype().getfShortName());
			item.accumulate("ftotal", fsubscription.getFtotal());
			// item.accumulate("fbuyCount", fsubscription.getFbuyCount());
			item.accumulate("fAlreadyByCount", fsubscription.getfAlreadyByCount());
			jsonArray.add(item);
			jsonObject.accumulate("list", jsonArray);
		} catch (Exception e) {
			jsonObject.accumulate(Result, false);
			jsonObject.accumulate(ErrorCode, -1);
			jsonObject.accumulate(Value, "网络错误");
			// e.printStackTrace();
		}

		return jsonObject.toString();
	}

	/**
	 * 提交兑换操作
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public String submitEx(HttpServletRequest request) throws Exception {
		// 4028d0815af3e0a1015af40ff33600cc
		Fuser fuser = this.realTimeData.getAppFuser(this.curLoginToken);
		// Fuser fuser =
		// frontUserService.findById("4028d0815af3e0a1015af40ff33600cc");
		String fid = request.getParameter("fid");
		double buyAmount = Double.parseDouble(request.getParameter("buyAmount"));
		String tradePwd = request.getParameter("tradePwd");
		JSONObject js = new JSONObject();
		Fsubscription fsubscription = this.frontTradeService.findFsubscriptionById(fid);
		if (fsubscription == null) {
			fsubscription = this.frontTradeService.findFirstSubscription(SubscriptionTypeEnum.COIN);
			if (fsubscription == null) {
				js.accumulate(Result, false);
				js.accumulate(ErrorCode, -1);
				js.accumulate(Value, "系统异常");
				return js.toString();
			}
		}
		if (buyAmount <= 0) {
			js.accumulate(Result, false);
			js.accumulate(ErrorCode, -1);
			js.accumulate(Value, "兑换数量异常");
			return js.toString();
		}

		if (fsubscription.getFtotal() - fsubscription.getfAlreadyByCount() - buyAmount < 0d) {
			js.accumulate(Result, false);
			js.accumulate(ErrorCode, -1);
			js.accumulate(Value, "兑换池余额不足");
			return js.toString();
		}

		if (fuser.getFtradePassword() == null) {
			js.accumulate(Result, false);
			js.accumulate(ErrorCode, -1);
			js.accumulate(Value, "请先设置交易密码");
			return js.toString();
		}

		String ip = Utils.getIp(request);
		if (fuser.getFtradePassword() != null) {
			int trade_limit = this.frontValidateService.getLimitCount(ip, CountLimitTypeEnum.TRADE_PASSWORD);
			if (trade_limit <= 0) {
				js.accumulate(Result, false);
				js.accumulate(ErrorCode, -1);
				js.accumulate(Value, "交易密码有误，请稍候再试");
				return js.toString();
			} else {
				boolean flag = fuser.getFtradePassword().equals(Utils.MD5(tradePwd));
				if (!flag) {
					js.accumulate(Result, false);
					js.accumulate(ErrorCode, -1);
					js.accumulate(Value, "交易密码有误，您还有" + (trade_limit - 1) + "次机会");
					this.frontValidateService.updateLimitCount(ip, CountLimitTypeEnum.TRADE_PASSWORD);
					return js.toString();
				} else if (trade_limit < Constant.ErrorCountLimit) {
					this.frontValidateService.deleteCountLimite(ip, CountLimitTypeEnum.TRADE_PASSWORD);
				}
			}
		}

		int begin = 0;
		long now = Utils.getTimestamp().getTime();
		if (fsubscription.getFbeginTime().getTime() > now) {
			// 没开始
			begin = 0;
		}

		if (fsubscription.getFbeginTime().getTime() < now && fsubscription.getFendTime().getTime() > now) {
			// 进行中
			begin = 1;
		}

		if (fsubscription.getFendTime().getTime() < now) {
			// 结束
			begin = 2;
		}

		if (begin == 0) {
			js.accumulate(Result, false);
			js.accumulate(ErrorCode, -1);
			js.accumulate(Value, "兑换未开始");
			return js.toString();
		} else if (begin == 2) {
			js.accumulate(Result, false);
			js.accumulate(ErrorCode, -1);
			js.accumulate(Value, "兑换已结束");
			return js.toString();
		}

		// 认购记录
		List<Fsubscriptionlog> fsubscriptionlogs = this.frontTradeService.findFsubScriptionLog(fuser,
				fsubscription.getFid());

		// 可购买数量
		int buyCount = fsubscription.getFbuyCount();
		if (fsubscriptionlogs.size() > 0) {
			for (int i = 0; i < fsubscriptionlogs.size(); i++) {
				buyCount -= fsubscriptionlogs.get(i).getFcount();
			}
		}

		buyCount = buyCount < 0 ? 0 : buyCount;
		// 可购买次数
		int buyTimes = fsubscription.getFbuyTimes() - fsubscriptionlogs.size();
		buyTimes = buyTimes < 0 ? 0 : buyTimes;

		if (fsubscription.getFbuyCount() != 0 && buyAmount > buyCount) {
			js.accumulate(Result, false);
			js.accumulate(ErrorCode, -1);
			js.accumulate(Value, "您超出可兑换的最大数量");
			return js.toString();
		}

		if (fsubscription.getFbuyTimes() != 0 && buyTimes == 0) {
			js.accumulate(Result, false);
			js.accumulate(ErrorCode, -1);
			js.accumulate(Value, "您超出可兑换的最大次数");
			return js.toString();
		}

		Double cost = buyAmount * fsubscription.getFprice();
		// 可以购买了
		Fvirtualwallet fvirtualwalletCost = this.frontUserService.findVirtualWalletByUser(fuser.getFid(),
				fsubscription.getFvirtualcointypeCost().getFid());
		Fvirtualwallet fvirtualwallet = this.frontUserService.findVirtualWalletByUser(fuser.getFid(),
				fsubscription.getFvirtualcointype().getFid());
		if (fvirtualwalletCost.getFtotal() < cost) {
			js.accumulate(Result, false);
			js.accumulate(ErrorCode, -1);
			js.accumulate(Value, "余额不足");
			return js.toString();
		}

		fvirtualwalletCost.setFtotal(fvirtualwalletCost.getFtotal() - cost);
		fvirtualwallet.setFtotal(fvirtualwallet.getFtotal() + buyAmount);
		Fsubscriptionlog fsubscriptionlog = new Fsubscriptionlog();
		fsubscriptionlog.setFcount(buyAmount + 0.0);
		fsubscriptionlog.setFcreatetime(Utils.getTimestamp());
		fsubscriptionlog.setFprice(fsubscription.getFprice());
		fsubscriptionlog.setFsubscription(fsubscription);
		fsubscriptionlog.setFtotalCost(cost);
		fsubscriptionlog.setFischarge("true");
		fsubscriptionlog.setFuser(fuser);
		fsubscriptionlog.setFissend(true);
		fsubscriptionlog.setFstatus(SubStatusEnum.YES);
		fsubscription.setfAlreadyByCount(fsubscription.getfAlreadyByCount() + buyAmount);
		try {
			this.frontTradeService.updateSubscription(fvirtualwalletCost, fvirtualwallet, fsubscriptionlog,
					fsubscription);
		} catch (Exception e) {
			js.accumulate(Result, false);
			js.accumulate(ErrorCode, -1);
			js.accumulate(Value, "网络异常");
			return js.toString();
		}
		js.accumulate(Result, true);
		js.accumulate(ErrorCode, 0);
		js.accumulate(Value, "兑换成功");
		return js.toString();
	}

	/**
	 * 过去兑换历史
	 * 
	 * @param request
	 * @return
	 */
	public String getExLogs(HttpServletRequest request) {
		// 4028d0815af3e0a1015af40ff33600cc
		// Fuser fuser =
		// frontUserService.findById("4028d0815af3e0a1015af40ff33600cc");
		JSONObject jsonObject = new JSONObject();
		try {
			if(Comm.getISHIDDEN_EX())
			{
				jsonObject.accumulate(Result, false);
				jsonObject.accumulate(ErrorCode, -1);
				jsonObject.accumulate(Value, "禁止兑换");
				return jsonObject.toString();
			}
			int pageSize = 6;
			Fuser fuser = this.realTimeData.getAppFuser(this.curLoginToken);
			jsonObject = new JSONObject();
			if (request.getParameter("fid") == null || "".equals(request.getParameter("fid"))) {
				jsonObject.accumulate(Result, false);
				jsonObject.accumulate(ErrorCode, -1);
				jsonObject.accumulate(Value, "兑换id为空");
				return jsonObject.toString();
			}
			String fsubid = request.getParameter("fid");
			int currentPage = 1;
			currentPage = Integer.parseInt(request.getParameter("currentPage"));
			int totalPage = 0;
			String filter = " where fuser.fid='" + fuser.getFid() + "'" + " and fsubscription.fid='" + fsubid + "'"
					+ " order by fcreatetime desc";
			// 认购记录
			List<Fsubscriptionlog> fsubscriptionlogs = this.frontTradeService.findFsubScriptionLog(
					(Integer.valueOf(currentPage) - 1) *pageSize, pageSize, fuser, fsubid);
			// this.frontTradeService.findFsubScriptionLog(fuser,fsubid);
			int count = this.frontTradeService.findFsubscriptionlogByParamCount(filter);
			totalPage=count / pageSize + ((count % pageSize) == 0 ? 0 : 1);
			jsonObject.accumulate("totalPage", totalPage);
			jsonObject.accumulate("currentPage", currentPage);

			JSONArray jsonArray = new JSONArray();
			for (int i = 0; i < fsubscriptionlogs.size(); i++) {
				JSONObject item = new JSONObject();
				Fsubscriptionlog fsubscriptionlog = fsubscriptionlogs.get(i);
				item.accumulate("fid", fsubscriptionlog.getFid());
				item.accumulate("fvitypefSymbol",
						fsubscriptionlog.getFsubscription().getFvirtualcointype().getfSymbol());
				item.accumulate("fvitypeCostfSymbol",
						fsubscriptionlog.getFsubscription().getFvirtualcointypeCost().getfSymbol());
				item.accumulate("fcount", fsubscriptionlog.getFcount());
				item.accumulate("fprice", fsubscriptionlog.getFprice());
				item.accumulate("ftotalCost", fsubscriptionlog.getFtotalCost());
				item.accumulate("fcreatetime",
						new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(fsubscriptionlog.getFcreatetime()));
				jsonArray.add(item);

			}

			jsonObject.accumulate("list", jsonArray);
			jsonObject.accumulate(Result, true);
			jsonObject.accumulate(ErrorCode, 0);
			jsonObject.accumulate(Value, "");
		} catch (NumberFormatException e) {
			jsonObject.accumulate(Result, false);
			jsonObject.accumulate(ErrorCode, -1);
			jsonObject.accumulate(Value, "网络错误 ");
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}
		return jsonObject.toString();

	}
}
