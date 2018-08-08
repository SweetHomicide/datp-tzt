package com.ruizton.main.controller.front;

import java.text.SimpleDateFormat;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ruizton.main.Enum.EntrustPlanStatusEnum;
import com.ruizton.main.Enum.EntrustStatusEnum;
import com.ruizton.main.Enum.EntrustTypeEnum;
import com.ruizton.main.Enum.SubStatusEnum;
import com.ruizton.main.Enum.SubscriptionTypeEnum;
import com.ruizton.main.Enum.VirtualCoinTypeStatusEnum;
import com.ruizton.main.auto.RealTimeData;
import com.ruizton.main.comm.ConstantMap;
import com.ruizton.main.controller.BaseController;
import com.ruizton.main.model.Fentrust;
import com.ruizton.main.model.Fentrustlog;
import com.ruizton.main.model.Fentrustplan;
import com.ruizton.main.model.Fintrolinfo;
import com.ruizton.main.model.Flimittrade;
import com.ruizton.main.model.Fsubscription;
import com.ruizton.main.model.Fsubscriptionlog;
import com.ruizton.main.model.Fuser;
import com.ruizton.main.model.Fvirtualcointype;
import com.ruizton.main.model.Fvirtualwallet;
import com.ruizton.main.model.Fwallet;
import com.ruizton.main.service.admin.SubscriptionLogService;
import com.ruizton.main.service.admin.SystemArgsService;
import com.ruizton.main.service.front.FrontTradeService;
import com.ruizton.main.service.front.FrontUserService;
import com.ruizton.main.service.front.FrontValidateService;
import com.ruizton.main.service.front.FrontVirtualCoinService;
import com.ruizton.main.service.front.UtilsService;
import com.ruizton.util.Comm;
import com.ruizton.util.Utils;

@Controller
public class FrontTradeJsonController extends BaseController {

	@Autowired
	private FrontValidateService frontValidateService;
	@Autowired
	private FrontUserService frontUserService;
	@Autowired
	private RealTimeData realTimeData;
	@Autowired
	private FrontVirtualCoinService frontVirtualCoinService;
	@Autowired
	private FrontTradeService frontTradeService;
	@Autowired
	private SystemArgsService systemArgsService;
	@Autowired
	private ConstantMap constantMap;
	@Autowired
	private SubscriptionLogService subscriptionLogService;
	@Autowired
	private UtilsService utilsService;

	@ResponseBody
	@RequestMapping(value = "/trade/buyBtcSubmit", produces = { JsonEncode })
	public String buyBtcSubmit(HttpServletRequest request,
			@RequestParam(required = false, defaultValue = "0") int limited, // 是否按照市场价买入
			@RequestParam(required = true) String symbol, // 币种
			@RequestParam(required = true) double tradeAmount, // 数量
			@RequestParam(required = true) double tradeCnyPrice, // 单价
			@RequestParam(required = false, defaultValue = "") String tradePwd) throws Exception {
		// limited=0;//禁用市价单

		JSONObject jsonObject = new JSONObject();

		Fvirtualcointype fvirtualcointype = this.frontVirtualCoinService.findFvirtualCoinById(symbol);
		if (fvirtualcointype == null || !fvirtualcointype.isFisShare()
				|| fvirtualcointype.getFstatus() == VirtualCoinTypeStatusEnum.Abnormal) {
			jsonObject.accumulate("resultCode", -100);
			jsonObject.accumulate("msg", "该币暂时不能交易");
			return jsonObject.toString();
		}

		// 是否开放交易
		if (Utils.openTrade(fvirtualcointype) == false) {
			jsonObject.accumulate("resultCode", -400);
			jsonObject.accumulate("msg", "现在不是交易时间");
			return jsonObject.toString();
		}

		tradeAmount = Utils.getDouble(tradeAmount, 4);
		tradeCnyPrice = Utils.getDouble(tradeCnyPrice, fvirtualcointype.getFcount());

		if (limited == 0) {

			if (tradeAmount < 0.0001D) {
				jsonObject.accumulate("resultCode", -1);
				jsonObject.accumulate("msg", "请填写正确的交易数量");
				return jsonObject.toString();
			}

			if (tradeCnyPrice <= 0D) {
				jsonObject.accumulate("resultCode", -3);
				jsonObject.accumulate("msg", "请填写正确的交易价格");
				return jsonObject.toString();
			}
			double total = Utils.getDouble(tradeAmount * tradeCnyPrice, 4);
			if (total < 0.01d) {
				jsonObject.accumulate("resultCode", -33);
				jsonObject.accumulate("msg", "最小购买金额：￥0.01");
				return jsonObject.toString();
			}

			Flimittrade limittrade = this.isLimitTrade(fvirtualcointype.getFid());
			double upPrice = 0d;
			double downPrice = 0d;
			if (limittrade != null) {
				upPrice = Utils.getDouble(
						limittrade.getFupprice() + limittrade.getFupprice() * limittrade.getFpercent(),
						fvirtualcointype.getFcount());
				downPrice = Utils.getDouble(
						limittrade.getFdownprice() - limittrade.getFdownprice() * limittrade.getFpercent(),
						fvirtualcointype.getFcount());
				if (downPrice < 0)
					downPrice = 0;
				if (tradeCnyPrice > upPrice) {
					jsonObject.accumulate("resultCode", -500);
					jsonObject.accumulate("msg", "挂单价格不能高于涨停价￥" + upPrice);
					return jsonObject.toString();
				}
				if (tradeCnyPrice < downPrice) {
					jsonObject.accumulate("resultCode", -600);
					jsonObject.accumulate("msg", "挂单价格不能低于跌停价￥" + downPrice);
					return jsonObject.toString();
				}
			}

		} else {
			if (tradeCnyPrice < 0.01d) {
				jsonObject.accumulate("resultCode", -33);
				jsonObject.accumulate("msg", "最小购买金额：￥0.01");
				return jsonObject.toString();
			}
		}

		Fuser fuser = this.frontUserService.findById(GetSession(request).getFid());
		double totalTradePrice = 0F;
		if (limited == 0) {
			totalTradePrice = tradeAmount * tradeCnyPrice;
		} else {
			totalTradePrice = tradeAmount;
		}
		Fwallet fwallet = fuser.getFwallet();
		if (fwallet.getFtotalRmb() < totalTradePrice) {
			jsonObject.accumulate("resultCode", -4);
			jsonObject.accumulate("msg", "人民币余额不足");
			return jsonObject.toString();
		}
		if (!Comm.getISTRAD_OWNBYOWN()) {
			double price=0;
			String filter=" where fuser.fid='"+fuser.getFid()+"' AND fEntrustType=1 "
					+ "and fStatus!=3 and fStatus!=4 "
					+ "ORDER BY fPrize";
			List<Fentrust> fenList=frontTradeService.findFentrustByParam(0, 1, filter, true);
			
			if(fenList!=null&&fenList.size()>0)
			{
				price=fenList.get(0).getFprize();
				if(tradeCnyPrice>=price)
				{
					jsonObject.accumulate("resultCode", -700);
					jsonObject.accumulate("msg", "您不能购买自己的订单");
					return jsonObject.toString();
				}
			}
		}
		if (isNeedTradePassword(request)) {
			if (tradePwd == null || tradePwd.trim().length() == 0) {
				jsonObject.accumulate("resultCode", -50);
				jsonObject.accumulate("msg", "交易密码错误");
				return jsonObject.toString();
			}

			if (fuser.getFtradePassword() == null) {
				jsonObject.accumulate("resultCode", -5);
				jsonObject.accumulate("msg",
						"您还没有设置交易密码，请到安全中心设置<a class='text-danger' href='/user/security.html'>安全中心&gt;&gt;</a>");
				return jsonObject.toString();
			}
			if (!Utils.MD5(tradePwd).equals(fuser.getFtradePassword())) {
				jsonObject.accumulate("resultCode", -2);
				jsonObject.accumulate("msg", "交易密码错误");
				return jsonObject.toString();
			}
		}
	

		/*
		 * if(tradePwd.equals("0")){ jsonObject.accumulate("resultCode", -8) ;
		 * return jsonObject.toString() ; }
		 */

		// double lastDealPrize = this.realTimeData.getLatestDealPrize(symbol) ;
		// if(lastDealPrize-tradeCnyPrice/lastDealPrize>0.3f){
		// jsonObject.accumulate("resultCode", -6) ;
		// return jsonObject.toString() ;
		// }

		String ip = Utils.getIp(request);
		/*
		 * int trade_limit = this.frontValidateService.getLimitCount(ip,
		 * CountLimitTypeEnum.TRADE_PASSWORD) ; if(trade_limit<=0){
		 * jsonObject.accumulate("resultCode", -2) ;
		 * jsonObject.accumulate("errorNum", 0) ; return jsonObject.toString() ;
		 * }
		 * 
		 * if(!Utils.MD5(tradePwd).equals(fuser.getFtradePassword())){
		 * jsonObject.accumulate("resultCode", -2) ;
		 * jsonObject.accumulate("errorNum", trade_limit-1) ;
		 * this.frontValidateService.updateLimitCount(ip,
		 * CountLimitTypeEnum.TRADE_PASSWORD) ; return jsonObject.toString() ;
		 * }else if(trade_limit<Constant.ErrorCountLimit){
		 * this.frontValidateService.deleteCountLimite(ip,
		 * CountLimitTypeEnum.TRADE_PASSWORD) ; }
		 */

		boolean flag = false;
		Fentrust fentrust = null;
		try {
			fentrust = this.frontTradeService.updateEntrustBuy(symbol, tradeAmount, tradeCnyPrice, fuser, limited == 1);
			flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (flag && fentrust != null) {
			fentrust = this.frontTradeService.findFentrustById(fentrust.getFid());
			if (limited == 1) {
				this.realTimeData.addEntrustLimitBuyMap(symbol, fentrust);
			} else {
				this.realTimeData.addEntrustBuyMap(symbol, fentrust);
			}

			jsonObject.accumulate("resultCode", 0);
			jsonObject.accumulate("msg", "下单成功");
			setNoNeedPassword(request);
		} else {
			jsonObject.accumulate("resultCode", -200);
			jsonObject.accumulate("msg", "网络错误，请稍后再试");
		}

		return jsonObject.toString();
	}

	/*
	 * http://localhost:8899/trade/sellBtcSubmit.html?random=11
	 * &limited=0&symbol=1&tradeAmount=3&tradeCnyPrice=1.0&tradePwd=sbicrgw
	 */
	@ResponseBody
	@RequestMapping(value = "/trade/sellBtcSubmit", produces = { JsonEncode })
	public String sellBtcSubmit(HttpServletRequest request,
			@RequestParam(required = false, defaultValue = "0") int limited, // 是否按照市场价买入
			@RequestParam(required = true) String symbol, // 币种
			@RequestParam(required = true) double tradeAmount, // 数量
			@RequestParam(required = true) double tradeCnyPrice, // 单价
			@RequestParam(required = false, defaultValue = "") String tradePwd) throws Exception {
		// limited=0;

		JSONObject jsonObject = new JSONObject();

		Fvirtualcointype fvirtualcointype = this.frontVirtualCoinService.findFvirtualCoinById(symbol);
		if (fvirtualcointype == null || !fvirtualcointype.isFisShare()
				|| fvirtualcointype.getFstatus() != VirtualCoinTypeStatusEnum.Normal) {
			jsonObject.accumulate("resultCode", -100);
			jsonObject.accumulate("msg", "该币暂时不能交易");
			return jsonObject.toString();
		}

		// 是否开放交易
		if (Utils.openTrade(fvirtualcointype) == false) {
			jsonObject.accumulate("resultCode", -400);
			jsonObject.accumulate("msg", "现在不是交易时间");
			return jsonObject.toString();
		}

		tradeAmount = Utils.getDouble(tradeAmount, 4);
		tradeCnyPrice = Utils.getDouble(tradeCnyPrice, fvirtualcointype.getFcount());

		if (limited == 0) {

			if (tradeAmount < 0.0001D) {
				jsonObject.accumulate("resultCode", -1);
				jsonObject.accumulate("msg", "请输入正确的交易数量");
				return jsonObject.toString();
			}

			if (tradeCnyPrice <= 0D) {
				jsonObject.accumulate("resultCode", -3);
				jsonObject.accumulate("msg", "请输入正确的交易价格");
				return jsonObject.toString();
			}
			double total = Utils.getDouble(tradeAmount * tradeCnyPrice, 4);
			if (total < 0.01d) {
				jsonObject.accumulate("resultCode", -33);
				jsonObject.accumulate("msg", "最小购买金额：￥0.01");
				return jsonObject.toString();
			}

			Flimittrade limittrade = this.isLimitTrade(fvirtualcointype.getFid());
			double upPrice = 0d;
			double downPrice = 0d;
			if (limittrade != null) {
				upPrice = Utils.getDouble(
						limittrade.getFupprice() + limittrade.getFupprice() * limittrade.getFpercent(),
						fvirtualcointype.getFcount());
				downPrice = Utils.getDouble(
						limittrade.getFdownprice() - limittrade.getFdownprice() * limittrade.getFpercent(),
						fvirtualcointype.getFcount());
				if (downPrice < 0)
					downPrice = 0;
				if (tradeCnyPrice > upPrice) {
					jsonObject.accumulate("resultCode", -500);
					jsonObject.accumulate("msg", "挂单价格不能高于涨停价￥" + upPrice);
					return jsonObject.toString();
				}
				if (tradeCnyPrice < downPrice) {
					jsonObject.accumulate("resultCode", -600);
					jsonObject.accumulate("msg", "挂单价格不能低于跌停价￥" + downPrice);
					return jsonObject.toString();
				}
			}

		} else {
			if (tradeAmount < 0.01d) {
				jsonObject.accumulate("resultCode", -33);
				jsonObject.accumulate("msg", "最小购买数量：0.01");
				return jsonObject.toString();
			}
		}

		Fuser fuser = this.frontUserService.findById(GetSession(request).getFid());
		Fvirtualwallet fvirtualwallet = this.frontUserService.findVirtualWalletByUser(fuser.getFid(), symbol);
		if (fvirtualwallet == null) {
			jsonObject.accumulate("resultCode", -200);
			jsonObject.accumulate("msg", "系统错误，请联系管理员");
			return jsonObject.toString();
		}
		if (fvirtualwallet.getFtotal() < tradeAmount) {
			jsonObject.accumulate("resultCode", -4);
			jsonObject.accumulate("msg", fvirtualcointype.getFname() + "余额不足");
			return jsonObject.toString();
		}
		if (!Comm.getISTRAD_OWNBYOWN()) {
			double price=0;
			String filter=" where fuser.fid='"+fuser.getFid()+"' AND fEntrustType=0 "
					+ "and fStatus!=3 and fStatus!=4 "
					+ "ORDER BY fPrize DESC";
			List<Fentrust> fenList=frontTradeService.findFentrustByParam(0, 1, filter, true);
			
			if(fenList!=null&&fenList.size()>0)
			{
				price=fenList.get(0).getFprize();
				if(tradeCnyPrice<=price)
				{
					jsonObject.accumulate("resultCode", -700);
					jsonObject.accumulate("msg", "您不能和自己的订单成交");
					return jsonObject.toString();
				}
			}
		}
		if (isNeedTradePassword(request)) {
			if (tradePwd == null || tradePwd.trim().length() == 0) {
				jsonObject.accumulate("resultCode", -50);
				jsonObject.accumulate("msg", "交易密码错误");
				return jsonObject.toString();
			}

			if (fuser.getFtradePassword() == null) {
				jsonObject.accumulate("resultCode", -5);
				jsonObject.accumulate("msg",
						"您还没有设置交易密码，请到安全中心设置<a class='text-danger' href='/user/security.html'>安全中心&gt;&gt;</a>");
				return jsonObject.toString();
			}
			if (!Utils.MD5(tradePwd).equals(fuser.getFtradePassword())) {
				jsonObject.accumulate("resultCode", -2);
				jsonObject.accumulate("msg", "交易密码错误");
				return jsonObject.toString();
			}
		}
	

		/*
		 * if(fuser.getFtradePassword()==null){
		 * jsonObject.accumulate("resultCode", -5) ; return
		 * jsonObject.toString() ; }
		 */

		/*
		 * if(tradePwd.equals("0")){ jsonObject.accumulate("resultCode", -8) ;
		 * return jsonObject.toString() ; }
		 */

		// double lastDealPrize = this.realTimeData.getLatestDealPrize(symbol) ;
		// if(tradeCnyPrice-lastDealPrize/lastDealPrize>0.3f){
		// jsonObject.accumulate("resultCode", -6) ;
		// return jsonObject.toString() ;
		// }

		String ip = Utils.getIp(request);
		/*
		 * int trade_limit = this.frontValidateService.getLimitCount(ip,
		 * CountLimitTypeEnum.TRADE_PASSWORD) ; if(trade_limit<=0){
		 * jsonObject.accumulate("resultCode", -2) ;
		 * jsonObject.accumulate("errorNum", 0) ; return jsonObject.toString() ;
		 * }
		 * 
		 * if(!Utils.MD5(tradePwd).equals(fuser.getFtradePassword())){
		 * jsonObject.accumulate("resultCode", -2) ;
		 * jsonObject.accumulate("errorNum", trade_limit-1) ;
		 * this.frontValidateService.updateLimitCount(ip,
		 * CountLimitTypeEnum.TRADE_PASSWORD) ; return jsonObject.toString() ;
		 * }else if(trade_limit<Constant.ErrorCountLimit){
		 * this.frontValidateService.deleteCountLimite(ip,
		 * CountLimitTypeEnum.TRADE_PASSWORD) ; }
		 */

		boolean flag = false;
		Fentrust fentrust = null;
		try {
			fentrust = this.frontTradeService.updateEntrustSell(symbol, tradeAmount, tradeCnyPrice, fuser,
					limited == 1);
			flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (flag && fentrust != null) {
			fentrust = this.frontTradeService.findFentrustById(fentrust.getFid());
			if (limited == 1) {
				this.realTimeData.addEntrustLimitSellMap(symbol, fentrust);
			} else {
				this.realTimeData.addEntrustSellMap(symbol, fentrust);
			}

			jsonObject.accumulate("resultCode", 0);
			jsonObject.accumulate("msg", "下单成功");
			setNoNeedPassword(request);
		} else {
			jsonObject.accumulate("resultCode", -200);
			jsonObject.accumulate("msg", "网络错误，请稍后再试");
		}

		return jsonObject.toString();
	}

	@ResponseBody
	@RequestMapping(value = "/trade/cancelEntrust", produces = JsonEncode)
	public String cancelEntrust(HttpServletRequest request, @RequestParam(required = true) String id) throws Exception {

		JSONObject jsonObject = new JSONObject();

		Fuser fuser = this.frontUserService.findById(GetSession(request).getFid());
		Fentrust fentrust = this.frontTradeService.findFentrustById(id);
		if (fentrust != null
				&& (fentrust.getFstatus() == EntrustStatusEnum.Going
						|| fentrust.getFstatus() == EntrustStatusEnum.PartDeal)
				&& fentrust.getFuser().getFid().equals(fuser.getFid())) {
			boolean flag = false;
			try {
				this.frontTradeService.updateCancelFentrust(fentrust, fuser);
				flag = true;
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (flag == true) {
				if (fentrust.getFentrustType() == EntrustTypeEnum.BUY) {

					// 买
					if (fentrust.isFisLimit()) {
						this.realTimeData.removeEntrustLimitBuyMap(fentrust.getFvirtualcointype().getFid(), fentrust);
					} else {
						this.realTimeData.removeEntrustBuyMap(fentrust.getFvirtualcointype().getFid(), fentrust);
					}
				} else {

					// 卖
					if (fentrust.isFisLimit()) {
						this.realTimeData.removeEntrustLimitSellMap(fentrust.getFvirtualcointype().getFid(), fentrust);
					} else {
						this.realTimeData.removeEntrustSellMap(fentrust.getFvirtualcointype().getFid(), fentrust);
					}

				}
			}
		}

		jsonObject.accumulate("code", 0);
		jsonObject.accumulate("msg", "取消成功");
		return jsonObject.toString();
	}

	@ResponseBody
	@RequestMapping("/trade/cancelPlanEntrust")
	public String cancelPlanEntrust(HttpServletRequest request, @RequestParam(required = true) int id)
			throws Exception {

		Fuser fuser = this.frontUserService.findById(GetSession(request).getFid());
		Fentrustplan fentrustplan = this.frontTradeService.findFentrustplanById(id);
		if (fentrustplan != null && fentrustplan.getFuser().getFid().equals(fuser.getFid())
				&& fentrustplan.getFstatus() == EntrustPlanStatusEnum.Not_entrust) {
			fentrustplan.setFstatus(EntrustPlanStatusEnum.Cancel);
			try {
				this.frontTradeService.updateCancelEntrustPlan(fentrustplan, fuser);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return String.valueOf(0);
	}

	@ResponseBody
	@RequestMapping(value = "/trade/entrustLog", produces = JsonEncode)
	public String entrustLog(HttpServletRequest request, @RequestParam(required = true) String id) throws Exception {

		JSONObject jsonObject = new JSONObject();

		if (GetSession(request) == null) {
			jsonObject.accumulate("result", false);
		}

		Fentrust fentrust = this.frontTradeService.findFentrustById(id);
		if (fentrust == null) {
			jsonObject.accumulate("result", false);
		} else {
			List<Fentrustlog> fentrustlogs = this.frontTradeService.findFentrustLogByFentrust(fentrust);

			jsonObject.accumulate("result", true);
			jsonObject.accumulate("title", "详细成交情况");

			StringBuffer content = new StringBuffer();
			/* content.append("<div><span style>委托ID:"+id+"</span></div>"); */
			content.append("<div> <table class=\"table\" > " + "<tr> " + "<td style='width:200px'>成交时间</td> " +
			/* "<td>委托类别</td> " + */
					"<td>委托价格</td> " + "<td>成交价格</td> " + "<td>成交数量</td> " + "<td>成交金额</td> " + "</tr>");

			if (fentrustlogs.size() == 0) {
				content.append("<tr><td colspan='6' class='no-data-tips'><span>暂无委托</span></td></tr>");
			}

			Fvirtualcointype fvirtualcointype = this.frontVirtualCoinService
					.findFvirtualCoinById(fentrust.getFvirtualcointype().getFid());

			for (Fentrustlog fentrustlog : fentrustlogs) {
				content.append("<tr> " + "<td class='gray'>"
						+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(fentrustlog.getFcreateTime()) + "</td> " +
						/*
						 * "<td class='"+(fentrust.getFentrustType()==
						 * EntrustTypeEnum.BUY?"text-success":"text-danger")+
						 * "'>"+fentrust.getFentrustType_s()+"</td>" +
						 */
						"<td>￥" + Utils.number2String(fentrust.getFprize()) + "</td>" + "<td>￥"
						+ Utils.number2String(fentrustlog.getFprize()) + "</td>" + "<td>"
						+ fvirtualcointype.getfSymbol() + Utils.number2String(fentrustlog.getFcount()) + "</td>"
						+ "<td>￥" + Utils.number2String(fentrustlog.getFamount()) + "</td>" + "</tr>");
			}
			content.append("</table> </div>");
			jsonObject.accumulate("content", content.toString());
		}
		return jsonObject.toString();
	}
}
