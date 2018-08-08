package com.ruizton.main.controller.front.pay;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ditp.service.StationMailService;
import com.ruizton.main.Enum.CapitalOperationInStatus;
import com.ruizton.main.dao.FcapitaloperationDAO;
import com.ruizton.main.model.Fcapitaloperation;
import com.ruizton.main.model.Fsubscription;
import com.ruizton.main.model.Fvirtualwallet;
import com.ruizton.main.model.Fwallet;
import com.ruizton.main.service.admin.CapitaloperationService;
import com.ruizton.main.service.admin.SubscriptionService;
import com.ruizton.main.service.admin.UserService;
import com.ruizton.main.service.admin.VirtualWalletService;
import com.ruizton.main.service.admin.WalletService;
import com.ruizton.main.service.front.pay.PayService;
import com.ruizton.util.Utils;
import com.unionpay.acp.sdk.AcpService;
import com.unionpay.acp.sdk.LogUtil;
import com.unionpay.acp.sdk.SDKConstants;
/**
 * 重要：联调测试时请仔细阅读注释！
 * 
 * 产品：代收产品<br>
 * 功能：后台通知接收处理示例 <br>
 * 日期： 2016-12-08<br>
 * 版本： 1.0.0 
 * 版权： 中国银联<br>
 * 交易说明：成功的交易才会发送后台通知，建议此交易与交易状态查询交易结合使用确定交易是否成功
 */
@Controller
public class BackRcvController {
	@Autowired
	FcapitaloperationDAO fcapitaloperationDAO;
	@Autowired
	CapitaloperationService capitaloperationService;
	@Autowired
	private VirtualWalletService virtualWalletService;
	@Autowired
	private SubscriptionService subscriptionService;
	@Autowired
	private PayService payService ;
	@Autowired
	private WalletService walletService;
	@Autowired
	private UserService userService;
	@Autowired
	private StationMailService stationMailService;
	
	@RequestMapping("/account/backRcvResponse")
	public void BackRcvResponse(			
			HttpServletRequest request,
			HttpServletResponse response
			) throws Exception{	
		
		LogUtil.writeLog("BackRcvResponse接收后台通知开始");
		String encoding = request.getParameter(SDKConstants.param_encoding);
		// 获取银联通知服务器发送的后台通知参数
		Map<String, String> reqParam = getAllRequestParam(request);

		LogUtil.printRequestLog(reqParam);

		Map<String, String> valideData = null;
		if (null != reqParam && !reqParam.isEmpty()) {
			Iterator<Entry<String, String>> it = reqParam.entrySet().iterator();
			valideData = new HashMap<String, String>(reqParam.size());
			while (it.hasNext()) {
				Entry<String, String> e = it.next();
				String key = (String) e.getKey();
				String value = (String) e.getValue();
				value = new String(value.getBytes(encoding), encoding);
				valideData.put(key, value);
			}
		}
		
		//重要！验证签名前不要修改reqParam中的键值对的内容，否则会验签不过
		String orderId=valideData.get("orderId");
		List<Fcapitaloperation> list;
		double frozenRMB;
		double totalFrozenRMB;
		double rmb;
		double totlaRmb;
		double total_fee;
		if (!AcpService.validate(valideData, encoding)) {
			LogUtil.writeLog("验证签名结果[失败].");
			//验签失败，需解决验签问题
			list=capitaloperationService.findByProperty("forderId", orderId);
			for(Fcapitaloperation fcapitaloperation:list)  
	        {  
				fcapitaloperation.setFstatus(CapitalOperationInStatus.Fail);
				capitaloperationService.updateObj(fcapitaloperation);
	        }
			//TODO 调取查询交易
			payService.queryState(valideData.get("merId"), orderId, valideData.get("txnTime"));
		} else {
			LogUtil.writeLog("验证签名结果[成功].");
			//交易成功，更新商户订单状态
			
			String txnType=valideData.get("txnType");			
			switch (txnType) {
			case "00":
				//TODO 查询交易
				break;
			case "01":
				//TODO 消费
				//1.根据orderId更新充值标识	
				orderId=valideData.get("orderId");
				Fcapitaloperation fca=null;
				list=capitaloperationService.findByProperty("forderId", orderId);
				for(Fcapitaloperation fcapitaloperation:list)  
		        {  fca=fcapitaloperation;
					fcapitaloperation.setFstatus(CapitalOperationInStatus.Come);
					capitaloperationService.updateObj(fcapitaloperation);
		        }
				Fvirtualwallet fvirtualwallet = null;
				Fwallet fwallet;
				if(list.get(0).getFviType()!=null){
					String fivwFilter="where fvirtualcointype.fid='"+list.get(0).getFviType().getFid()+"' and fuser.fid='"+list.get(0).getFuser().getFid()+"'";
					//获取平台钱包
					fvirtualwallet= virtualWalletService.list(0, 0, fivwFilter, false).get(0);
					//获取比例  人民币与数字资产的
					//Fsubscription fsubscription = subscriptionService.findByFviId(list.get(0).getFviType().getFid());
					double amountBTC = list.get(0).getFtotalBTC();
					fvirtualwallet.setFlastUpdateTime(Utils.getTimestamp());
					fvirtualwallet.setFtotal(fvirtualwallet.getFtotal()+amountBTC);
					virtualWalletService.updateObj(fvirtualwallet);
				}else{
					total_fee=list.get(0).getFamount();
					// 钱包解冻相对应金额 liuruichen
					fwallet =userService.findById(list.get(0).getFuser().getFid()).getFwallet();;
					//frozenRMB = fwallet.getFfrozenRmb();
					//totalFrozenRMB = frozenRMB - total_fee;
					rmb = fwallet.getFtotalRmb();
					totlaRmb = rmb + total_fee;
					//fwallet.setFfrozenRmb(totalFrozenRMB);
					fwallet.setFtotalRmb(totlaRmb);
					walletService.updateObj(fwallet);
				}
				//发送站内信
				stationMailService.sendStationMail(fca);

				break;
			case "04":
				//TODO 退货
				break;
			case "31":
				//TODO 消费撤销
				break;
			case "79":
				//TODO 开通交易（开通并支付）
				//1. 根据orderId更新充值成功标识				
				orderId=valideData.get("orderId");
				 list=capitaloperationService.findByProperty("forderId", orderId);
				for(Fcapitaloperation fcapitaloperation:list)  
		        {  
					fcapitaloperation.setFstatus(3);
					capitaloperationService.updateObj(fcapitaloperation);
		        } 
				
				if(list.get(0).getFviType()!=null){
					String fivwFilter="where fvirtualcointype.fid='"+list.get(0).getFviType().getFid()+"' and fuser.fid='"+list.get(0).getFuser().getFid()+"'";
					//获取平台钱包
					fvirtualwallet= virtualWalletService.list(0, 0, fivwFilter, false).get(0);
					//获取比例  人民币与数字资产的
					//Fsubscription fsubscription = subscriptionService.findByFviId(list.get(0).getFviType().getFid());
					double amountBTC = list.get(0).getFtotalBTC();
					fvirtualwallet.setFlastUpdateTime(Utils.getTimestamp());
					fvirtualwallet.setFtotal(fvirtualwallet.getFtotal()+amountBTC);
					virtualWalletService.updateObj(fvirtualwallet);
				}else{
					total_fee=list.get(0).getFamount();
					// 钱包解冻相对应金额
					fwallet =userService.findById(list.get(0).getFuser().getFid()).getFwallet();;
					//frozenRMB = fwallet.getFfrozenRmb();
					//totalFrozenRMB = frozenRMB - total_fee;
					
					rmb = fwallet.getFtotalRmb();
					totlaRmb = rmb + total_fee;
					//fwallet.setFfrozenRmb(totalFrozenRMB);
					fwallet.setFtotalRmb(totlaRmb);
					walletService.updateObj(fwallet);
				}
				
				break;	
			case "12":
				//1. 根据orderId更新提现成功标识				
				orderId=valideData.get("orderId");
				 list=capitaloperationService.findByProperty("forderId", orderId);
				 Fcapitaloperation fcapitaloperation2 = list.get(0);
				 //冻结金额
				 total_fee = fcapitaloperation2.getFamount();
				 fwallet =userService.findById(fcapitaloperation2.getFuser().getFid()).getFwallet();;
//				 frozenRMB = fwallet.getFfrozenRmb();
				 //冻结账号中将提现的金额解冻
//				 totalFrozenRMB = frozenRMB - total_fee;
//				 fwallet.setFfrozenRmb(totalFrozenRMB);
				 //修改冻结账户金额
				 walletService.updateObj(fwallet);
				break;
			default:
				break;
			}
			
		}
	}
	
	/**
	 * 获取请求参数中所有的信息
	 * 
	 * @param request
	 * @return
	 */
	public static Map<String, String> getAllRequestParam(final HttpServletRequest request) {
		Map<String, String> res = new HashMap<String, String>();
		Enumeration<?> temp = request.getParameterNames();
		if (null != temp) {
			while (temp.hasMoreElements()) {
				String en = (String) temp.nextElement();
				String value = request.getParameter(en);
				res.put(en, value);
				//在报文上送时，如果字段的值为空，则不上送<下面的处理为在获取所有参数数据时，判断若值为空，则删除这个字段>
				//System.out.println("ServletUtil类247行  temp数据的键=="+en+"     值==="+value);
				if (null == res.get(en) || "".equals(res.get(en))) {
					res.remove(en);
				}
			}
		}
		return res;
	}
}
