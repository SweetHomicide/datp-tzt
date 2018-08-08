package com.ditp.controller.WeChat;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.ditp.WeChat.comm.WxPayAPI;
import com.ditp.WeChat.comm.WxPayData;
import com.ditp.service.StationMailService;
import com.ruizton.main.Enum.CapitalOperationInStatus;
import com.ruizton.main.Enum.CapitalOperationTypeEnum;
import com.ruizton.main.Enum.RemittanceTypeEnum;
import com.ruizton.main.controller.BaseController;
import com.ruizton.main.model.Fcapitaloperation;
import com.ruizton.main.model.Fsubscription;
import com.ruizton.main.model.Fuser;
import com.ruizton.main.model.Fvirtualcointype;
import com.ruizton.main.model.Fvirtualwallet;
import com.ruizton.main.model.Fwallet;
import com.ruizton.main.model.Systembankinfo;
import com.ruizton.main.service.admin.AdminService;
import com.ruizton.main.service.admin.CapitaloperationService;
import com.ruizton.main.service.admin.SubscriptionService;
import com.ruizton.main.service.admin.SystembankService;
import com.ruizton.main.service.admin.UserService;
import com.ruizton.main.service.admin.VirtualWalletService;
import com.ruizton.main.service.admin.WalletService;
import com.ruizton.util.Comm;
import com.ruizton.util.Constant;
import com.ruizton.util.Utils;

import net.sf.json.JSONObject;

@Controller
public class WeChatController extends BaseController {
	@Autowired
	CapitaloperationService capitaloperationService;
	@Autowired
	SystembankService systembankService;
	@Autowired
	private AdminService adminService;
	@Autowired
	private WalletService walletService;
	@Autowired
	private UserService userService;
	@Autowired
	private SubscriptionService subscriptionService;
	@Autowired
	private VirtualWalletService virtualWalletService;
	@Autowired
	private StationMailService stationMailService;

	Systembankinfo systembankinfo;
	Fcapitaloperation fcapitaloperation;
	Fwallet fwallet;

	// 微信支付
	@RequestMapping("account/weChatPay")
	public ModelAndView WeChatPayIndex(HttpServletRequest request,
			@RequestParam(required = false, defaultValue = "") String currentPage,
			@RequestParam(required = false, defaultValue = "4") int type,
			@RequestParam(required=false,defaultValue="")String fviId
			) {
		try {
			int cur = 1;
			if (currentPage == null || "".equals(currentPage)) {
				cur = 1;
			} else {
				cur = Integer.parseInt(currentPage);
			}
			ModelAndView modelAndView = new ModelAndView();
			
			Fuser fuser = this.GetSession(request);
			StringBuffer filter = new StringBuffer();
			filter.append("where fuser.fid='" + fuser.getFid() + "' \n");
			filter.append("and ftype =" + CapitalOperationTypeEnum.RMB_IN + "\n");
			if (type == 4) {
				filter.append("and fremittanceType='" + RemittanceTypeEnum.getEnumString(type) + "' \n");
			} else {
				filter.append("and systembankinfo is not null \n");
			}

			if ("".equals(fviId)) {
				filter.append(" and fviType is null  order by fcreateTime desc \n");
			}else{
				filter.append(" and fviType = '"+fviId+"'  order by fcreateTime desc \n");
			}
			List<Fcapitaloperation> list = this.capitaloperationService.list(
					(cur - 1) * Constant.AppRecordPerPage, Constant.RecordPerPage, filter.toString(),
					true);
			int totalCount = this.adminService.getAllCount("Fcapitaloperation", filter.toString());
			String url = "/account/weChatPay.html?type=" + type + "&";
			String pagin = this.generatePagin(totalCount / Constant.RecordPerPage
					+ ((totalCount % Constant.RecordPerPage) == 0 ? 0 : 1), cur, url);
			//可以充值的数字资产
			List<Fsubscription> listFsubscription=getRechargeTypeList();
			modelAndView.addObject("listRechargeType", listFsubscription);	//可以充值的数字资产
			modelAndView.addObject("list", list);
			modelAndView.addObject("pagin", pagin);
			modelAndView.addObject("cur_page", cur);
			modelAndView.addObject("type", type);
			modelAndView.addObject("abouts", "");
			modelAndView.addObject("fabout", "");
			modelAndView.addObject("selectType",fviId) ;
			modelAndView.setViewName("front/account/account_weChatPay");
			return modelAndView;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
/**
 * 请求微信 返回二维码
 * @param currentPage
 * @param txnAmt 金额
 * @param request 请求
 * @return 
 * @throws Exception
 */
	@ResponseBody
	@RequestMapping(value = "/account/weChatPayGetQR", produces = { JsonEncode })
	public String WeChatPayGetQR(@RequestParam(required = false, defaultValue = "") String currentPage,
			@RequestParam(required = false, defaultValue = "") String txnAmt, HttpServletRequest request,
			@RequestParam(required=true,defaultValue = "") String fviId)
			throws Exception {
		try {
			String productId = WxPayAPI.getNowDate() + getFixLenthString(6);
			WxPayData data = new WxPayData();
			// 注：微信金额为分，平台为元，所以提交微信时要乘以100 测试阶段还是已分为单位
			// String
			String	 wxPaytxnAmt=Integer.toString((Integer.valueOf(txnAmt)*Comm.getWX_PAYNUMBER()));
			//String wxPaytxnAmt = txnAmt;
			String bodyName = "数字积分交易平台-充值";// 商品描述
			String out_trade_no = WxPayAPI.GenerateOutTradeNo();
			data.SetValue("body", bodyName);// 商品描述
			data.SetValue("attach", "ditp");// 附加数据
			data.SetValue("out_trade_no", out_trade_no);// 随机字符串
			data.SetValue("total_fee", wxPaytxnAmt);// 总金额[分为单位]
			data.SetValue("time_start", WxPayAPI.getNowDate());// 交易起始时间
			data.SetValue("time_expire", WxPayAPI.getNowDateAfter());// 交易结束时间
			data.SetValue("goods_tag", "empty");// 商品标记，使用代金券或立减优惠功能时需要的参数
			data.SetValue("trade_type", "NATIVE");// 交易类型
			data.SetValue("product_id", productId);// 商品ID
			WxPayData result = WxPayAPI.UnifiedOrder(data, 6);// 调用统一下单接口
			JSONObject jsonObject = new JSONObject();
			// 获得统一下单接口返回的二维码链接--start
			jsonObject.accumulate("url", result.GetValue("code_url").toString());
			jsonObject.accumulate("out_trade_no", out_trade_no);
			//---------钱包增加操作记录  钱包更新钱
			systembankinfo = new Systembankinfo();
			fcapitaloperation = new Fcapitaloperation();
			Fvirtualcointype f = new Fvirtualcointype();
//			String fviId = "";
			if (!"".equals(fviId)) {
				f.setFid(fviId);
				fcapitaloperation.setFviType(f);//虚拟币类型ID
				Fsubscription fsubscription = subscriptionService.findByFviId(fviId);//人民币兑换  fisRM是1
				fcapitaloperation.setFtotalBTC(Double.parseDouble(txnAmt)*fsubscription.getFprice());//充值金额*兑换比例
			}
			fcapitaloperation.setFuser(this.GetSession(request));
			fcapitaloperation.setFamount(Double.parseDouble(txnAmt));
			fcapitaloperation.setFremittanceType(RemittanceTypeEnum.getEnumString(4));//// 汇款方式
			fcapitaloperation.setFremark(RemittanceTypeEnum.getEnumString(4));
			fcapitaloperation.setForderId(out_trade_no);// 交易id
			fcapitaloperation.setFtype(CapitalOperationTypeEnum.RMB_IN);
			fcapitaloperation.setFstatus(CapitalOperationInStatus.NoGiven);// 尚未付款
			systembankinfo = systembankService.findById("1");
			fcapitaloperation.setSystembankinfo(systembankinfo);
			//fcapitaloperation.setFamount(fcapitaloperation.getFamount());
			fcapitaloperation.setFcreateTime(Utils.getTimestamp());
			//fcapitaloperation.setFamount(fcapitaloperation.getFamount());
			fcapitaloperation.setfLastUpdateTime(Utils.getTimestamp());
			fcapitaloperation.setFfees(0);
			fcapitaloperation.setVersion(0);
			fcapitaloperation.setFischarge(true);
			capitaloperationService.saveObj(fcapitaloperation);
			// 钱包里面冻结币
//			fwallet =walletService.findById(this.GetSession(request).getFwallet().getFid());
//			double frozenRMB = fwallet.getFfrozenRmb();
//			double totalFrozenRMB = frozenRMB + Double.parseDouble(wxPaytxnAmt);
//			fwallet.setFfrozenRmb(totalFrozenRMB);
//			walletService.updateObj(fwallet);
			//---------钱包增加操作记录  钱包更新钱--end
			return jsonObject.toString();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
		return null;
	}

	/**
	 * 
	 * 查询订单情况
	 * 
	 * @param string
	 *            out_trade_no 商户订单号
	 * @param int
	 *            succCode 查询订单结果：0表示订单不成功，1表示订单成功，2表示继续查询
	 * @return 订单查询接口返回的数据，参见协议接口
	 */
	@ResponseBody
	@RequestMapping(value = "/account/weChatPayQuery", produces = { JsonEncode })
	public String Query(@RequestParam(required = false, defaultValue = "") String out_trade_no,
			HttpServletRequest request) {
		JSONObject jsonObject = new JSONObject();
		int status = CapitalOperationInStatus.NoGiven;
		try {
			int succCode = 2;
			WxPayData queryOrderInput = new WxPayData();
			queryOrderInput.SetValue("out_trade_no", out_trade_no);
			WxPayData result = WxPayAPI.OrderQuery(queryOrderInput);

			if (result.GetValue("return_code").toString().equals("SUCCESS")
					&& result.GetValue("result_code").toString().equals("SUCCESS")) {
				// 支付成功
				if (result.GetValue("trade_state").toString().equals("SUCCESS")) {
					succCode = 1;
				}
				// 用户支付中，需要继续查询
				else if (result.GetValue("trade_state").toString().equals("USERPAYING")) {
					succCode = 2;
				}
			}
			if (result.IsSet("err_code")) {
				// 如果返回错误码为“此交易订单号不存在”则直接认定失败
				if (result.GetValue("err_code").toString().equals("ORDERNOTEXIST")) {
					succCode = 0;
					status = CapitalOperationInStatus.Invalidate;
				} else {
					// 如果是系统错误，则后续继续
					succCode = 2;
				}
			}
			// 修改数据库中记录状态
			// 等待银行到账银行

			if (succCode == 1) {
				// 已经到账
				status = CapitalOperationInStatus.Come;

			}
			//--更新钱包操作，更新钱包--start
			List<Fcapitaloperation> list = capitaloperationService.findByProperty("forderId", out_trade_no);
			Fvirtualwallet fvirtualwallet=null;
			for (Fcapitaloperation fcapitaloperation : list) {
				int fstatus=fcapitaloperation.getFstatus();
				if (succCode == 1&&fstatus!=CapitalOperationInStatus.Come) {
					String total_fee = result.GetValue("total_fee").toString();
					if(fcapitaloperation.getFviType()!=null){
						 String fivwFilter="where fvirtualcointype.fid='"+fcapitaloperation.getFviType().getFid()+"' and fuser.fid='"+fcapitaloperation.getFuser().getFid()+"'";
						//获取平台钱包
						fvirtualwallet= virtualWalletService.list(0, 0, fivwFilter, false).get(0);
						//Fsubscription fsubscription = subscriptionService.findByFviId(fcapitaloperation.getFviType().getFid());
						double amountBTC = fcapitaloperation.getFtotalBTC();
						fvirtualwallet.setFlastUpdateTime(Utils.getTimestamp());
						fvirtualwallet.setFtotal(fvirtualwallet.getFtotal()+amountBTC);
						virtualWalletService.updateObj(fvirtualwallet);
					}else{
						// 钱包更新金额
						fwallet =userService.findById(fcapitaloperation.getFuser().getFid()).getFwallet();;
						//double frozenRMB = fwallet.getFfrozenRmb();
						//double totalFrozenRMB = frozenRMB - Double.parseDouble(total_fee);
						double rmb = fwallet.getFtotalRmb();
						double totlaRmb = rmb + Double.parseDouble(total_fee);
						//fwallet.setFfrozenRmb(totalFrozenRMB);
						fwallet.setFtotalRmb(totlaRmb);
						walletService.updateObj(fwallet);
					}
					stationMailService.sendStationMail(fcapitaloperation);
				}
				fcapitaloperation.setFstatus(status);
				capitaloperationService.updateObj(fcapitaloperation);

			}
			//--更新钱包操作，更新钱包--end


			jsonObject.accumulate("success", succCode);
			return jsonObject.toString();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			jsonObject.accumulate("success", "2");
			return jsonObject.toString();
		}
	}

	/*
	 * 返回长度为【strLength】的随机数，在前面补0
	 */
	private static String getFixLenthString(int strLength) {

		Random rm = new Random();

		// 获得随机数
		double pross = (1 + rm.nextDouble()) * Math.pow(10, strLength);

		// 将获得的获得随机数转化为字符串
		String fixLenthString = String.valueOf(pross);

		// 返回固定的长度的随机数
		return fixLenthString.substring(1, strLength + 1);
	}
}
