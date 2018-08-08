package com.ditp.controller.AliPay;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import com.alipay.api.domain.TradeFundBill;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.model.ExtendParams;
import com.alipay.demo.trade.model.GoodsDetail;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.builder.AlipayTradeQueryRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.model.result.AlipayF2FQueryResult;
import com.alipay.demo.trade.service.AlipayMonitorService;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.service.impl.AlipayMonitorServiceImpl;
import com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl;
import com.alipay.demo.trade.service.impl.AlipayTradeWithHBServiceImpl;
import com.alipay.demo.trade.utils.Utils;
import com.ditp.WeChat.comm.WxPayAPI;
import com.ditp.service.StationMailService;
import com.ruizton.main.Enum.CapitalOperationInStatus;
import com.ruizton.main.Enum.CapitalOperationTypeEnum;
import com.ruizton.main.Enum.RemittanceTypeEnum;
import com.ruizton.main.comm.ConstantMap;
import com.ruizton.main.controller.BaseController;
import com.ruizton.main.model.Fcapitaloperation;
import com.ruizton.main.model.Fsubscription;
import com.ruizton.main.model.Fuser;
import com.ruizton.main.model.Fvirtualcointype;
import com.ruizton.main.model.Fvirtualwallet;
import com.ruizton.main.model.Fwallet;
import com.ruizton.main.model.Fwebbaseinfo;
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
import net.sf.json.JSONObject;

@Controller
public class AliPayController extends BaseController {
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
	private  ConstantMap constantMap;
	@Autowired
	private StationMailService stationMailService;
	Systembankinfo systembankinfo;
	Fcapitaloperation fcapitaloperation;
	Fwallet fwallet;
	private static Log log = LogFactory.getLog(AliPayController.class);
	// // 支付宝当面付2.0服务（集成了交易保障接口逻辑）
	private static AlipayTradeService tradeWithHBService;
	// // 支付宝交易保障接口服务，供测试接口api使用，请先阅读readme.txt
	private static AlipayMonitorService monitorService;
	private static AlipayTradeService tradeService;
	static {
		/**
		 * 一定要在创建AlipayTradeService之前调用Configs.init()设置默认参数
		 *
		 * Configs会读取classpath下的zfbinfo.properties文件配置信息，如果找不到该文件则确认该文件是否在classpath目录
		 */
		Configs.init("zfbinfo.properties");
	
		/**
		 * 使用Configs提供的默认参数 AlipayTradeService可以使用单例或者为静态成员对象，不需要反复new
		 */
		tradeService = new AlipayTradeServiceImpl.ClientBuilder().build();

		// 支付宝当面付2.0服务（集成了交易保障接口逻辑）
		tradeWithHBService = new AlipayTradeWithHBServiceImpl.ClientBuilder().build();

		/**
		 * 如果需要在程序中覆盖Configs提供的默认参数, 可以使用ClientBuilder类的setXXX方法修改默认参数
		 * 否则使用代码中的默认设置
		 */
		monitorService = new AlipayMonitorServiceImpl.ClientBuilder()
				.setGatewayUrl("http://mcloudmonitor.com/gateway.do").setCharset("GBK").setFormat("json").build();
	}

	/**
	 * 支付宝首页
	 * @param request
	 * @param currentPage
	 * @param type
	 * @return
	 */
	@RequestMapping("account/AliPay")
	public ModelAndView AliPayIndex(HttpServletRequest request,
			@RequestParam(required = false, defaultValue = "") String currentPage,
			@RequestParam(required = false, defaultValue = "3") int type,
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
			if (type == 3) {
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
			modelAndView.setViewName("front/account/account_AliPay");
			return modelAndView;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
/**
 * 请求支付宝 返回二维码链接
 * @param currentPage 分页
 * @param txnAmt 金额
 * @param request
 * @return
 */
	@ResponseBody
	@RequestMapping(value = "/account/AliPayQR", produces = { JsonEncode })
	public String trade_precreate(@RequestParam(required = false, defaultValue = "") String currentPage,
			@RequestParam(required = false, defaultValue = "") String txnAmt, HttpServletRequest request
			,@RequestParam(required=true,defaultValue = "") String fviId) {
		JSONObject jsonObject = new JSONObject();

		// 注：支付宝金额单位为元 与平台相同
		String wxPaytxnAmt =Double.toString((Double.parseDouble(txnAmt)*Comm.getALI_PAYNUMBER()));
		// (必填) 商户网站订单系统中唯一订单号，64个字符以内，只能包含字母、数字、下划线，
		// 需保证商户系统端不能重复，建议通过数据库sequence生成，
		// String outTradeNo = "tradeprecreate" + System.currentTimeMillis() +
		// (long) (Math.random() * 10000000L);
		String outTradeNo = WxPayAPI.GenerateOutTradeNo();
		// (必填) 订单标题，粗略描述用户的支付目的。如“xxx品牌xxx门店当面付扫码消费”
		String subject = ((Fwebbaseinfo)constantMap.get("webinfo")).getFtitle();

		// (必填) 订单总金额，单位为元，不能超过1亿元
		// 如果同时传入了【打折金额】,【不可打折金额】,【订单总金额】三者,则必须满足如下条件:【订单总金额】=【打折金额】+【不可打折金额】
		String totalAmount =wxPaytxnAmt;

		// (可选) 订单不可打折金额，可以配合商家平台配置折扣活动，如果酒水不参与打折，则将对应金额填写至此字段
		// 如果该值未传入,但传入了【订单总金额】,【打折金额】,则该值默认为【订单总金额】-【打折金额】
		String undiscountableAmount = "0";

		// 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号)
		// 如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
		String sellerId = "";

		// 订单描述，可以对交易或商品进行一个详细地描述，比如填写"购买商品2件共15.00元"
		String body = "积分交易";

		// 商户操作员编号，添加此参数可以为商户操作员做销售统计
		String operatorId = "test_operator_id";

		// (必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
		String storeId = "test_store_id";

		// 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)，详情请咨询支付宝技术支持
		ExtendParams extendParams = new ExtendParams();
		extendParams.setSysServiceProviderId("2088100200300400500");

		// 支付超时，定义为120分钟
		String timeoutExpress = "120m";

		// 商品明细列表，需填写购买商品详细信息，
		List<GoodsDetail> goodsDetailList = new ArrayList<GoodsDetail>();
		// 创建一个商品信息，参数含义分别为商品id（使用国标）、名称、单价（单位为分）、数量，如果需要添加商品类别，详见GoodsDetail
		//GoodsDetail goods1 = GoodsDetail.newInstance("goods_id001", "xxx小面包", 1000, 1);
		// 创建好一个商品后添加至商品明细列表
		//goodsDetailList.add(goods1);

		// 继续创建并添加第一条商品信息，用户购买的产品为“黑人牙刷”，单价为5.00元，购买了两件
		//GoodsDetail goods2 = GoodsDetail.newInstance("goods_id002", "xxx牙刷", 500, 2);
		//goodsDetailList.add(goods2);

		// 创建扫码支付请求builder，设置请求参数
		AlipayTradePrecreateRequestBuilder builder = new AlipayTradePrecreateRequestBuilder().setSubject(subject)
				.setTotalAmount(totalAmount).setOutTradeNo(outTradeNo).setUndiscountableAmount(undiscountableAmount)
				.setSellerId(sellerId).setBody(body).setOperatorId(operatorId).setStoreId(storeId)
				.setExtendParams(extendParams).setTimeoutExpress(timeoutExpress)
				// account/resultAliPay 
				.setNotifyUrl(Comm.getALI_NotifyUrl())
				//.setNotifyUrl("http://www.test-notify-url.com")// 支付宝服务器主动通知商户服务器里指定的页面http路径,根据需要设置
				.setGoodsDetailList(goodsDetailList);
		AlipayF2FPrecreateResult result = tradeService.tradePrecreate(builder);
		switch (result.getTradeStatus()) {
		case SUCCESS:
			log.info("支付宝预下单成功: )");

			AlipayTradePrecreateResponse response = result.getResponse();
			String qrCode = response.getQrCode();
			// 获得统一下单接口返回的二维码链接
			jsonObject.accumulate("url", qrCode);
			jsonObject.accumulate("out_trade_no", outTradeNo);
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
			fcapitaloperation.setFremittanceType(RemittanceTypeEnum.getEnumString(3));//// 汇款方式
			fcapitaloperation.setFremark(RemittanceTypeEnum.getEnumString(3));
			fcapitaloperation.setForderId(outTradeNo);// 交易id
			fcapitaloperation.setFtype(CapitalOperationTypeEnum.RMB_IN);
			fcapitaloperation.setFstatus(CapitalOperationInStatus.NoGiven);// 尚未付款
			systembankinfo = systembankService.findById("1");//平台银行卡 暂时默认1 期望单独增加支付宝
			fcapitaloperation.setSystembankinfo(systembankinfo);
			//fcapitaloperation.setFamount(fcapitaloperation.getFamount());
			fcapitaloperation.setFcreateTime(com.ruizton.util.Utils.getTimestamp());
			//fcapitaloperation.setFamount(fcapitaloperation.getFamount());
			fcapitaloperation.setfLastUpdateTime(com.ruizton.util.Utils.getTimestamp());
			fcapitaloperation.setFfees(0);
			fcapitaloperation.setVersion(0);
			fcapitaloperation.setFischarge(true);
			capitaloperationService.saveObj(fcapitaloperation);
			// 钱包里面冻结币
//			fwallet =walletService.findById(this.GetSession(request).getFwallet().getFid());
//			double frozenRMB = fwallet.getFfrozenRmb();
//			double totalFrozenRMB = frozenRMB + Double.parseDouble(txnAmt);
//			fwallet.setFfrozenRmb(totalFrozenRMB);
//			walletService.updateObj(fwallet);
			//---------钱包增加操作记录  钱包更新钱--end
			break;

		case FAILED:
			log.error("支付宝预下单失败!!!");
			break;
		case UNKNOWN:
			log.error("系统异常，预下单状态未知!!!");
			break;
		default:
			log.error("不支持的交易状态，交易返回异常!!!");
			break;
		}

		return jsonObject.toString();
	}
	/**
	 * 查询订单
	 * @param out_trade_no 订单号
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/account/AliPayQuery", produces = { JsonEncode })
	public String Query(@RequestParam(required = false, defaultValue = "") String out_trade_no,
			HttpServletRequest request)
	{
		int succCode = 2;//充值状态
		JSONObject jsonObject = new JSONObject();
		int status = CapitalOperationInStatus.NoGiven;//尚未付款
		String total_fee="0";//充值金额
		try {
			   // (必填) 商户订单号，通过此商户订单号查询当面付的交易状态
	        //String outTradeNo = "tradepay14569786655152108371";
	      //  String outTradeNo="141484460220161216155058735";
	        // 创建查询请求builder，设置请求参数
	        AlipayTradeQueryRequestBuilder builder = new AlipayTradeQueryRequestBuilder()
	                                                    .setOutTradeNo(out_trade_no);
	       
	        AlipayF2FQueryResult result = tradeService.queryTradeResult(builder);
	        switch (result.getTradeStatus()) {
	            case SUCCESS:
	            	succCode=1;
	                log.info("查询返回该订单支付成功: )");
	                AlipayTradeQueryResponse response = result.getResponse();
	                total_fee=Double.toString((Double.parseDouble(response.getTotalAmount())/Comm.getALI_PAYNUMBER()));
	                log.info(response.getTradeStatus());
	                if (Utils.isListNotEmpty(response.getFundBillList())) {
	                    for (TradeFundBill bill : response.getFundBillList()) {
	                        log.info(bill.getFundChannel() + ":" + bill.getAmount());
	                    }
	                }
	                break;

	            case FAILED:
	                log.error("查询返回该订单支付失败或被关闭!!!");
	                succCode=2;
	                break;

	            case UNKNOWN:
	                log.error("系统异常，订单支付状态未知!!!");
	                succCode=2;
	                break;

	            default:
	                log.error("不支持的交易状态，交易返回异常!!!");
	                succCode=2;
	                break;
	        }
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
					
					if (null!=fcapitaloperation.getFviType()) {
						String fivwFilter="where fvirtualcointype.fid='"+fcapitaloperation.getFviType().getFid()+"' and fuser.fid='"+fcapitaloperation.getFuser().getFid()+"'";
						//获取平台钱包
						fvirtualwallet= virtualWalletService.list(0, 0, fivwFilter, false).get(0);
						//Fsubscription fsubscription = subscriptionService.findByFviId(fcapitaloperation.getFviType().getFid());
						double amountBTC = fcapitaloperation.getFtotalBTC();
						fvirtualwallet.setFlastUpdateTime(com.ruizton.util.Utils.getTimestamp());
						fvirtualwallet.setFtotal(fvirtualwallet.getFtotal()+amountBTC);
						virtualWalletService.updateObj(fvirtualwallet);
					} else {
						// 更新金额
						fwallet =userService.findById(fcapitaloperation.getFuser().getFid()).getFwallet();;
						//double frozenRMB = fwallet.getFfrozenRmb();
						//double totalFrozenRMB = frozenRMB - Double.parseDouble(total_fee);
						double rmb = fwallet.getFtotalRmb();
						double totlaRmb = rmb + Double.parseDouble(total_fee);
						//fwallet.setFfrozenRmb(totalFrozenRMB);
						fwallet.setFtotalRmb(totlaRmb);
						walletService.updateObj(fwallet);
					}
					//站内信
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
			jsonObject.accumulate("success", 2);
			return jsonObject.toString();
		}
	}
	
	

}
