package com.ruizton.main.service.front.pay;

import java.util.Map;

import com.ruizton.main.model.FbankinfoWithdraw;
import com.ruizton.main.model.Fcapitaloperation;

public interface PayService {
	/**
	 * 支付
	 * @param merId 商户号
	 * @param txnTime 订单发送时间
	 * @param txnAmt 支付金额 
	 * @param orderId 商户订单号
	 * @param  accNo 卡号 
	 * return 返回信息
	 */
//	public Map<String, String> pay(String merId,String txnTime,String txnAmt,String orderId,String accNo);
	
	
	/**
	 * 检查开卡状态
	 * @param merId 商户号
	 * @param txnTime 订单发送时间
	 * @param accNo 卡号
	 * @return  开卡状态
	 */
	public String openCardState(String merId,String orderId,String txnTime,String accNo);
	
	/**
	 * 开卡
	 * @param merId 商户号
	 * @param orderId 商户订单号
	 * @param txnTime 订单发送时间
	 * @return 签名后的数据
	 */
	public  Map<String, String>openCardAndConsume(String merId,String orderId,String txnTime,String accNo,String txnAmt);
	/**
	 * 支付
	 * @param merId 商户号
	 * @param orderId 商户订单号
	 * @param txnTime 订单发送时间
	 * @param txnAmt 交易金额
	 * @return 
	 */
	public String consume(String merId,String orderId,String txnTime,String txnAmt,String accNoR,String phone,Fcapitaloperation fcapitaloperation);
	/**
	 * 交易状态查询交易：只有同步应答
	* @param merId 商户号
	 * @param orderId 商户订单号
	 * @param txnTime 订单发送时间
	 */
	public String queryState(String merId,String orderId,String txnTime);
	/**
	 * 消费短信：后台交易，无通知
	 * @param merId 商户号
	 * @param orderId 商户订单号
	 * @param txnTime 订单发送时间
	 * @param txnAmt 交易金额
	 * @param accNoR 用户账号
	 * @param phoneNo 用户手机号码
	 * @return 是否成功
	 */
	public boolean consumeSMS(String merId,String orderId,String txnTime,String txnAmt,String accNoR,String phoneNo);

	/**
	 * 代付业务指商户从自身单位结算账户向持卡人指定银行卡账户进行款项划付的业务。
	 * @param merId 商户号
	 * @param txnAmt
	 * @param orderId
	 * @param txnTime
	 * @param fbankinfoWithdraw
	 * @param fcapitaloperation
	 * @return
	 */
	public String df(String merId,String txnAmt,String orderId,String txnTime,String accNo, String identity,Fcapitaloperation fcapitaloperation);

}
