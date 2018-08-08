package com.ruizton.main.service.front.pay.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.ls.LSInput;

import com.ruizton.main.Enum.CapitalOperationInStatus;
import com.ruizton.main.dao.FcapitaloperationDAO;
import com.ruizton.main.model.Fcapitaloperation;
import com.ruizton.main.model.Fuser;
import com.ruizton.main.model.Fwallet;
import com.ruizton.main.service.admin.CapitaloperationService;
import com.ruizton.main.service.admin.SystembankService;
import com.ruizton.main.service.admin.UserService;
import com.ruizton.main.service.admin.WalletService;
import com.ruizton.main.service.front.pay.PayService;
import com.ruizton.util.AcpBase;
import com.unionpay.acp.sdk.AcpService;
import com.unionpay.acp.sdk.LogUtil;
import com.unionpay.acp.sdk.SDKConfig;
@Service
@Transactional
public class PayServiceImpl implements PayService {
	@Autowired 
	FcapitaloperationDAO  fcapitaloperationDAO;
	@Autowired
	UserService userService;
	@Autowired
	SystembankService  systembankService;
	@Autowired
	CapitaloperationService capitaloperationService;
	@Autowired
	private WalletService walletService;

	Fuser fuser;
	

	/**
	 * 全渠道支付开通查询交易，用于查询银行卡是否已开通银联全渠道支付。
	 * @param merId
	 * @param orderId
	 * @param txnTime
	 * @param accNo
	 * return activateStatus状态
	 */
	public String openCardState(String merId,String orderId,String txnTime,String accNo){
		Map<String, String> contentData = new HashMap<String, String>();
		/***银联全渠道系统，产品参数，除了encoding自行选择外其他不需修改***/
		contentData.put("version", AcpBase.version);                  //版本号
		contentData.put("encoding", AcpBase.encoding_UTF8);                //字符集编码 可以使用UTF-8,GBK两种方式
		contentData.put("signMethod", "01");                           //签名方法 目前只支持01-RSA方式证书加密
		contentData.put("txnType", "78");                              //交易类型 78-开通查询
		contentData.put("txnSubType", "00");                           //交易子类型 00-根据账号accNo查询(默认）
		contentData.put("bizType", "000301");                          //业务类型 认证支付2.0
		contentData.put("channelType", "07");                          //渠道类型07-PC

		/***商户接入参数***/
		contentData.put("merId", merId);                   			   //商户号码（本商户号码仅做为测试调通交易使用，该商户号配置了需要对敏感信息加密）测试时请改成自己申请的商户号，【自己注册的测试777开头的商户号不支持代收产品】
		contentData.put("orderId", orderId);             			   //商户订单号，8-40位数字字母，不能含“-”或“_”，可以自行定制规则	
		contentData.put("accessType", "0");                            //接入类型，商户接入固定填0，不需修改	
		contentData.put("txnTime", txnTime);         				   //订单发送时间，格式为YYYYMMDDhhmmss，必须取当前时间，否则会报txnTime无效
		//contentData.put("reqReserved", "透传字段");        			   //请求方保留域，透传字段（可以实现商户自定义参数的追踪）本交易的后台通知,对本交易的交易状态查询交易、对账文件中均会原样返回，商户可以按需上传，长度为1-1024个字节		

		////////////如果商户号开通了【商户对敏感信息加密】的权限那么需要对 accNo,phoneNo加密使用：
		String accNo1 = AcpService.encryptData(accNo, AcpBase.encoding_UTF8);  			//这里测试的时候使用的是测试卡号，正式环境请使用真实卡号
		contentData.put("accNo", accNo1);
		contentData.put("encryptCertId",AcpService.getEncryptCertId());   //加密证书的certId，配置在acp_sdk.properties文件 acpsdk.encryptCert.path属性下

		/////////如果商户号未开通【商户对敏感信息加密】权限那么不需对敏感信息加密使用：
		//contentData.put("accNo", accNo);            					//这里测试的时候使用的是测试卡号，正式环境请使用真实卡号

		/**对请求参数进行签名并发送http post请求，接收同步应答报文**/
		String activateStatus="";
		Map<String, String> reqData = AcpService.sign(contentData,AcpBase.encoding_UTF8);
		//报文中certId,signature的值是在signData方法中获取并自动赋值的，只要证书配置正确即可。
		String requestBackUrl = SDKConfig.getConfig().getBackRequestUrl();   						  //交易请求url从配置文件读取对应属性文件acp_sdk.properties中的 acpsdk.backTransUrl
		Map<String, String> rspData = AcpService.post(reqData,requestBackUrl,AcpBase.encoding_UTF8); //发送请求报文并接受同步应答（默认连接超时时间30秒，读取返回结果超时时间30秒）;这里调用signData之后，调用submitUrl之前不能对submitFromData中的键值对做任何修改，如果修改会导致验签不通过

		/**对应答码的处理，请根据您的业务逻辑来编写程序,以下应答码处理逻辑仅供参考------------->**/
		//应答码规范参考open.unionpay.com帮助中心 下载  产品接口规范  《平台接入接口规范-第5部分-附录》
		if(!rspData.isEmpty()){
			if(AcpService.validate(rspData, AcpBase.encoding_UTF8)){
				LogUtil.writeLog("验证签名成功");
				String respCode = rspData.get("respCode") ;//同步应答00，仅表示银联受理成功，不表示订单成功。
				if(("00").equals(respCode) || ("77").equals(respCode)){
					activateStatus = rspData.get("activateStatus") ;//开通状态(0：未开通业务 1：已开通银联全渠道支付)
					return activateStatus;
					
				}else{
					//其他应答码为失败请排查原因或做失败处理
					return rspData.get("respMsg");
				}
			}else{
				LogUtil.writeErrorLog("验证签名失败");
				// 检查验证签名失败的原因
				return rspData.get("验证签名失败");
			}
		}else{
			//未返回正确的http状态
			LogUtil.writeErrorLog("未获取到返回报文或返回http状态码非200");
			return "未获取到返回报文或返回http状态码非200";
		}

	}
	/**
	 * 银联全渠道支付开通交易用于开通银行卡的银联全渠道支付功能
	 * 1）确定交易成功机制：商户需开发后台通知接口或交易状态查询接口确定交易是否成功，建议发起查询交易的机制：可查询N次（不超过6次），每次时间间隔2N秒发起,即间隔1，2，4，8，16，32S查询（查询到03，04，05继续查询，否则终止查询）
	 * 2）报文中必送卡号，消费后卡号就开通了。
	 * @param merId
	 * @param orderId
	 * @param txnTime
	 * @return 签名后的请求参数
	 */
	public Map<String, String> openCardAndConsume(String merId,String orderId,String txnTime,String accNo,String txnAmt){

		/**
		 * 组装请求报文
		 */
		Map<String, String> contentData = new HashMap<String, String>();
		/***银联全渠道系统，产品参数，除了encoding自行选择外其他不需修改***/
		contentData.put("version", AcpBase.version);                  //版本号
		contentData.put("encoding", AcpBase.encoding_UTF8);                //字符集编码 可以使用UTF-8,GBK两种方式
		contentData.put("signMethod", "01");                           //签名方法 目前只支持01-RSA方式证书加密
		contentData.put("txnType", "01");                              //交易类型 11-代收
		contentData.put("txnSubType", "01");                           //交易子类型 00-默认开通
		contentData.put("bizType", "000301");                          //业务类型 认证支付2.0
		contentData.put("channelType", "07");                          //渠道类型07-PC

		/***商户接入参数***/
		contentData.put("merId", merId);                   			   //商户号码（本商户号码仅做为测试调通交易使用，该商户号配置了需要对敏感信息加密）测试时请改成自己申请的商户号，【自己注册的测试777开头的商户号不支持代收产品】
		contentData.put("accessType", "0");                            //接入类型，商户接入固定填0，不需修改	
		contentData.put("orderId", orderId);             			   //商户订单号，8-40位数字字母，不能含“-”或“_”，可以自行定制规则	
		contentData.put("txnTime", txnTime);         				   //订单发送时间，格式为YYYYMMDDhhmmss，必须取当前时间，否则会报txnTime无效
		contentData.put("currencyCode", "156");						   //交易币种（境内商户一般是156 人民币）
		contentData.put("txnAmt",txnAmt);							   //交易金额，单位分，不要带小数点
		contentData.put("accType", "01");                              //账号类型

		////////////【开通并付款卡号必送】如果商户号开通了【商户对敏感信息加密】的权限那么需要对 accNo加密：
		String accNo1 = AcpService.encryptData(accNo, "UTF-8");  			   //这里测试的时候使用的是测试卡号，正式环境请使用真实卡号
		contentData.put("accNo", accNo1);
		contentData.put("encryptCertId",AcpService.getEncryptCertId());       //加密证书的certId，配置在acp_sdk.properties文件 acpsdk.encryptCert.path属性下
		//////////

		/////////【开通并付款卡号必送】
		//contentData.put("accNo", accNo);            					   //这里测试的时候使用的是测试卡号，正式环境请使用真实卡号
		////////

		//contentData.put("reqReserved", "透传字段");        					//请求方保留域，透传字段（可以实现商户自定义参数的追踪）本交易的后台通知,对本交易的交易状态查询交易、对账文件中均会原样返回，商户可以按需上传，长度为1-1024个字节		
		//contentData.put("reserved", "{customPage=true}");         	//如果开通并支付页面需要使用嵌入页面的话，请上送此用法		

		//前台通知地址 （需设置为外网能访问 http https均可），支付成功后的页面 点击“返回商户”按钮的时候将异步通知报文post到该地址
		//异步通知参数详见open.unionpay.com帮助中心 下载  产品接口规范  网关支付产品接口规范 消费交易 商户通知
		//注：如果开通失败的“返回商户”按钮也是触发frontUrl地址，点击时是按照get方法返回的，没有通知数据返回商户
		contentData.put("frontUrl", AcpBase.frontUrl);

		//后台通知地址（需设置为【外网】能访问 http https均可），支付成功后银联会自动将异步通知报文post到商户上送的该地址，失败的交易银联不会发送后台通知
		//后台通知参数详见open.unionpay.com帮助中心 下载  产品接口规范  网关支付产品接口规范 消费交易 商户通知
		//注意:1.需设置为外网能访问，否则收不到通知    2.http https均可  3.收单后台通知后需要10秒内返回http200或302状态码 
		//    4.如果银联通知服务器发送通知后10秒内未收到返回状态码或者应答码非http200，那么银联会间隔一段时间再次发送。总共发送5次，每次的间隔时间为0,1,2,4分钟。
		//    5.后台通知地址如果上送了带有？的参数，例如：http://abc/web?a=b&c=d 在后台通知处理程序验证签名之前需要编写逻辑将这些字段去掉再验签，否则将会验签失败
		contentData.put("backUrl", AcpBase.backUrl);

		//contentData.put("reqReserved", "透传字段");         				//请求方保留域，透传字段（可以实现商户自定义参数的追踪）本交易的后台通知,对本交易的交易状态查询交易、对账文件中均会原样返回，商户可以按需上传，长度为1-1024个字节		
		//contentData.put("reserved", "{customPage=true}");         	//如果开通页面需要使用嵌入页面的话，请上送此用法		


		/**请求参数设置完毕，以下对请求参数进行签名**/
		Map<String, String> reqData = AcpService.sign(contentData,AcpBase.encoding_UTF8);            		 //报文中certId,signature的值是在signData方法中获取并自动赋值的，只要证书配置正确即可。
		return reqData;
	}
	/**
	 * 重要：联调测试时请仔细阅读注释！
	 * 
	 * 产品：代收产品<br>
	 * 交易：交易状态查询交易：只有同步应答 <br>
	 * 日期： 2015-09<br>
	 * 版本： 1.0.0 
	 * 版权： 中国银联<br>
	 * 交易说明：消费同步返回00，如果未收到后台通知建议发起查询交易，可查询N次（不超过6次），每次时间间隔2N秒发起,即间隔1，2，4，8，16，32S查询（查询到03 04 05继续查询，否则终止查询）。【如果最终尚未确定交易是否成功请以对账文件为准】
	 *        消费同步返03 04 05响应码及未得到银联响应（读超时）建议发起查询交易，可查询N次（不超过6次），每次时间间隔2N秒发起,即间隔1，2，4，8，16，32S查询（查询到03 04 05继续查询，否则终止查询）。【如果最终尚未确定交易是否成功请以对账文件为准】
	 */
	public String queryState(String merId,String orderId,String txnTime){
		Map<String, String> data = new HashMap<String, String>();
		String remark="";
		/***银联全渠道系统，产品参数，除了encoding自行选择外其他不需修改***/
		data.put("version", AcpBase.version);                 //版本号
		data.put("encoding", AcpBase.encoding_UTF8);               //字符集编码 可以使用UTF-8,GBK两种方式
		data.put("signMethod", "01");                          //签名方法 目前只支持01-RSA方式证书加密
		data.put("txnType", "00");                             //交易类型 00-默认
		data.put("txnSubType", "00");                          //交易子类型  默认00
		data.put("bizType", "000301");                         //业务类型

		/***商户接入参数***/
		data.put("merId", merId);                  			   //商户号码，请改成自己申请的商户号或者open上注册得来的777商户号测试
		data.put("accessType", "0");                           //接入类型，商户接入固定填0，不需修改

		/***要调通交易以下字段必须修改***/
		data.put("orderId", orderId);                 //****商户订单号，每次发交易测试需修改为被查询的交易的订单号
		data.put("txnTime", txnTime);                 //****订单发送时间，每次发交易测试需修改为被查询的交易的订单发送时间

		/**请求参数设置完毕，以下对请求参数进行签名并发送http post请求，接收同步应答报文------------->**/

		Map<String, String> reqData = AcpService.sign(data,AcpBase.encoding_UTF8);			//报文中certId,signature的值是在signData方法中获取并自动赋值的，只要证书配置正确即可。
		String url = SDKConfig.getConfig().getSingleQueryUrl();								//交易请求url从配置文件读取对应属性文件acp_sdk.properties中的 acpsdk.singleQueryUrl
		Map<String, String> rspData = AcpService.post(reqData, url,AcpBase.encoding_UTF8); //发送请求报文并接受同步应答（默认连接超时时间30秒，读取返回结果超时时间30秒）;这里调用signData之后，调用submitUrl之前不能对submitFromData中的键值对做任何修改，如果修改会导致验签不通过

		/**对应答码的处理，请根据您的业务逻辑来编写程序,以下应答码处理逻辑仅供参考------------->**/
		//应答码规范参考open.unionpay.com帮助中心 下载  产品接口规范  《平台接入接口规范-第5部分-附录》
		double rmb;
		double totlaRmb;
		double total_fee;
		List<Fcapitaloperation> list;
		if(!rspData.isEmpty()){
			if(AcpService.validate(rspData, AcpBase.encoding_UTF8)){
				LogUtil.writeLog("验证签名成功");
				if(("00").equals(rspData.get("respCode"))){//如果查询交易成功
					//交易成功，更新商户订单状态
					String txnType = rspData.get("txnType");
					switch (txnType) {
					case "01":
						//1.根据orderId更新充值标识	
						orderId=rspData.get("orderId");

						list=capitaloperationService.findByProperty("forderId", orderId);
						list.get(0).setFstatus(CapitalOperationInStatus.Come);//银行到账
						capitaloperationService.updateObj(list.get(0));
						
						total_fee=list.get(0).getFamount();
						// 钱包解冻相对应金额
						Fwallet fwallet;
						fwallet =userService.findById(list.get(0).getFuser().getFid()).getFwallet();;
						//frozenRMB = fwallet.getFfrozenRmb();
						//totalFrozenRMB = frozenRMB - total_fee;
						rmb = fwallet.getFtotalRmb();
						totlaRmb = rmb + total_fee;
						//fwallet.setFfrozenRmb(totalFrozenRMB);
						fwallet.setFtotalRmb(totlaRmb);
						walletService.updateObj(fwallet);
						break;
					case "79":
						// 开通交易（开通并支付）
						//1. 根据orderId更新充值成功标识				
						orderId=rspData.get("orderId");
						list=capitaloperationService.findByProperty("forderId", orderId);
						list.get(0).setFstatus(CapitalOperationInStatus.WaitForComing);//等待银行到账
						capitaloperationService.updateObj(list.get(0));
						break;	
					default:
						break;
					}
				}else if(("34").equals(rspData.get("respCode"))){
					//订单不存在，可认为交易状态未明，需要稍后发起交易状态查询，或依据对账结果为准
					queryState(merId,orderId,txnTime);

				}else{//查询交易本身失败，如应答码10/11检查查询报文是否正确
					//
					/*orderId=rspData.get("orderId");
					list=capitaloperationService.findByProperty("forderId", orderId);
					list.get(0).setFstatus(CapitalOperationInStatus.WaitForComing);//等待银行到账
					capitaloperationService.updateObj(list.get(0));*/
					remark="交易出错";
				}
			}else{
				// 检查验证签名失败的原因
				remark="验证签名失败";
				LogUtil.writeErrorLog(remark);
				list=capitaloperationService.findByProperty("forderId", orderId);
				list.get(0).setFstatus(CapitalOperationInStatus.Fail);//失败
				list.get(0).setFremark(remark);
				capitaloperationService.updateObj(list.get(0));
			}
		}else{
			//未返回正确的http状态
			remark="未获取到返回报文或返回http状态码非200";
			LogUtil.writeErrorLog(remark);
			list=capitaloperationService.findByProperty("forderId", orderId);
			list.get(0).setFstatus(CapitalOperationInStatus.Fail);//失败
			list.get(0).setFremark(remark);
			capitaloperationService.updateObj(list.get(0));
		}

		return remark;
	}
	/**
	 * 消费是指境内外持卡人在境内外商户网站进行购物等消费时用银行卡结算的交易，经批准的消费额将即时地反映到该持卡人的账户余额上。
	 * @param merId
	 * @param orderId
	 * @param txnTime
	 * @param txnAmt
	 */
	public String consume( String merId,String orderId,String txnTime,String txnAmt, String accNoR,String phone,Fcapitaloperation fcapitaloperation){
		Map<String, String> contentData = new HashMap<String, String>();

		/***银联全渠道系统，产品参数，除了encoding自行选择外其他不需修改***/
		contentData.put("version", AcpBase.version);                  //版本号
		contentData.put("encoding", AcpBase.encoding_UTF8);           //字符集编码 可以使用UTF-8,GBK两种方式
		contentData.put("signMethod", "01");                           //签名方法 目前只支持01-RSA方式证书加密
		contentData.put("txnType", "01");                              //交易类型 01-消费
		contentData.put("txnSubType", "01");                           //交易子类型 01-消费
		contentData.put("bizType", "000301");                          //业务类型 认证支付2.0
		contentData.put("channelType", "07");                          //渠道类型07-PC

		/***商户接入参数***/
		contentData.put("merId", merId);                   			   //商户号码（本商户号码仅做为测试调通交易使用，该商户号配置了需要对敏感信息加密）测试时请改成自己申请的商户号，【自己注册的测试777开头的商户号不支持代收产品】
		contentData.put("accessType", "0");                            //接入类型，商户接入固定填0，不需修改	
		contentData.put("orderId", orderId);             			   //商户订单号，8-40位数字字母，不能含“-”或“_”，可以自行定制规则	
		contentData.put("txnTime", txnTime);         				   //订单发送时间，格式为YYYYMMDDhhmmss，必须取当前时间，否则会报txnTime无效
		contentData.put("currencyCode", "156");						   //交易币种（境内商户一般是156 人民币）
		contentData.put("txnAmt", txnAmt);							   //交易金额，单位分，不要带小数点
		contentData.put("accType", "01");                              //账号类型

		//消费：交易要素卡号+验证码看业务配置(默认要短信验证码)。
		Map<String,String> customerInfoMap = new HashMap<String,String>();
		customerInfoMap.put("smsCode", phone);			    	//短信验证码,测试环境不会真实收到短信，固定填111111

		////////////如果商户号开通了【商户对敏感信息加密】的权限那么需要对 accNo，pin和phoneNo，cvn2，expired加密（如果这些上送的话），对敏感信息加密使用：
		String accNo = AcpService.encryptData(accNoR, AcpBase.encoding_UTF8);  //这里测试的时候使用的是测试卡号，正式环境请使用真实卡号
		contentData.put("accNo", accNo);
		contentData.put("encryptCertId",AcpService.getEncryptCertId());       //加密证书的certId，配置在acp_sdk.properties文件 acpsdk.encryptCert.path属性下
		String customerInfoStr = AcpService.getCustomerInfoWithEncrypt(customerInfoMap,accNoR,AcpBase.encoding_UTF8);
		//////////

		/////////如果商户号未开通【商户对敏感信息加密】权限那么不需对敏感信息加密使用：
		//contentData.put("accNo", "6216261000000000018");            		//这里测试的时候使用的是测试卡号，正式环境请使用真实卡号
		//String customerInfoStr = AcpService.getCustomerInfo(customerInfoMap,"6216261000000000018",AcpService.encoding_UTF8);
		////////

		contentData.put("customerInfo", customerInfoStr);
		//		contentData.put("reqReserved", "透传字段");        					//请求方保留域，透传字段（可以实现商户自定义参数的追踪）本交易的后台通知,对本交易的交易状态查询交易、对账文件中均会原样返回，商户可以按需上传，长度为1-1024个字节		

		//后台通知地址（需设置为【外网】能访问 http https均可），支付成功后银联会自动将异步通知报文post到商户上送的该地址，失败的交易银联不会发送后台通知
		//后台通知参数详见open.unionpay.com帮助中心 下载  产品接口规范  代收产品接口规范 代收交易 商户通知
		//注意:1.需设置为外网能访问，否则收不到通知    2.http https均可  3.收单后台通知后需要10秒内返回http200或302状态码 
		//    4.如果银联通知服务器发送通知后10秒内未收到返回状态码或者应答码非http200，那么银联会间隔一段时间再次发送。总共发送5次，每次的间隔时间为0,1,2,4分钟。
		//    5.后台通知地址如果上送了带有？的参数，例如：http://abc/web?a=b&c=d 在后台通知处理程序验证签名之前需要编写逻辑将这些字段去掉再验签，否则将会验签失败
		contentData.put("backUrl", AcpBase.backUrl);

		//分期付款用法（商户自行设计分期付款展示界面）：
		//修改txnSubType=03，增加instalTransInfo域
		//【测试环境】固定使用测试卡号6221558812340000，测试金额固定在100-1000元之间，分期数固定填06
		//【生产环境】支持的银行列表清单请联系银联业务运营接口人索要
		//		contentData.put("txnSubType", "03");                           //交易子类型 03-分期付款
		//		contentData.put("instalTransInfo","{numberOfInstallments=06}");//分期付款信息域，numberOfInstallments期数

		/**对请求参数进行签名并发送http post请求，接收同步应答报文**/
		Map<String, String> reqData = AcpService.sign(contentData,AcpBase.encoding_UTF8);			//报文中certId,signature的值是在signData方法中获取并自动赋值的，只要证书配置正确即可。
		String requestBackUrl = SDKConfig.getConfig().getBackRequestUrl();   			//交易请求url从配置文件读取对应属性文件acp_sdk.properties中的 acpsdk.backTransUrl
		Map<String, String> rspData = AcpService.post(reqData,requestBackUrl,AcpBase.encoding_UTF8); //发送请求报文并接受同步应答（默认连接超时时间30秒，读取返回结果超时时间30秒）;这里调用signData之后，调用submitUrl之前不能对submitFromData中的键值对做任何修改，如果修改会导致验签不通过

		/**对应答码的处理，请根据您的业务逻辑来编写程序,以下应答码处理逻辑仅供参考------------->**/
		//应答码规范参考open.unionpay.com帮助中心 下载  产品接口规范  《平台接入接口规范-第5部分-附录》
		List<Fcapitaloperation> list;
		if(!rspData.isEmpty()){
			if(AcpService.validate(rspData, AcpBase.encoding_UTF8)){
				LogUtil.writeLog("验证签名成功");
				String respCode = rspData.get("respCode") ;
				if(("00").equals(respCode)){
					list=capitaloperationService.findByProperty("forderId", orderId);
					list.get(0).setFstatus(CapitalOperationInStatus.WaitForComing);//等待银行到账
					capitaloperationService.updateObj(list.get(0));
					


					return "00";
					//交易已受理(不代表交易已成功），等待接收后台通知更新订单状态,也可以主动发起 查询交易确定交易状态。
					//TODO
					//如果是配置了敏感信息加密，如果需要获取卡号的铭文，可以按以下方法解密卡号
					//String accNo1 = rspData.get("accNo");
					//String accNo2 = AcpService.decryptData(accNo1, "UTF-8");  //解密卡号使用的证书是商户签名私钥证书acpsdk.signCert.path
					//LogUtil.writeLog("解密后的卡号："+accNo2);

				}else if(("03").equals(respCode)||
						("04").equals(respCode)||
						("05").equals(respCode)){
					//后续需发起交易状态查询交易确定交易状态
					queryState(merId,orderId,txnTime);
					return rspData.get("respMsg");
					//TODO
				}else{
					//其他应答码为失败请排查原因
					//TODO
					list=capitaloperationService.findByProperty("forderId", orderId);
					list.get(0).setFstatus(CapitalOperationInStatus.Fail);//等待银行到账
					list.get(0).setFremark(rspData.get("respMsg"));
					capitaloperationService.updateObj(list.get(0));

					return rspData.get("respMsg");
				}
			}else{
				String mark="验证签名失败";
				LogUtil.writeErrorLog(mark);
				list=capitaloperationService.findByProperty("forderId", orderId);
				list.get(0).setFstatus(CapitalOperationInStatus.Fail);//等待银行到账
				list.get(0).setFremark(mark);
				capitaloperationService.updateObj(list.get(0));
				return mark;
			}
		}else{
			//未返回正确的http状态
			String mark="未获取到返回报文或返回http状态码非200";
			LogUtil.writeErrorLog(mark);
			list=capitaloperationService.findByProperty("forderId", orderId);
			list.get(0).setFstatus(CapitalOperationInStatus.Invalidate);//等待银行到账
			list.get(0).setFremark(mark);
			capitaloperationService.updateObj(list.get(0));
			return mark;
		}


	}

	public boolean consumeSMS(String merId,String orderId,String txnTime,String txnAmt,String accNoR,String phoneNo){
		Map<String, String> contentData = new HashMap<String, String>();
		/***银联全渠道系统，产品参数，除了encoding自行选择外其他不需修改***/
		contentData.put("version", AcpBase.version);                  //版本号
		contentData.put("encoding", AcpBase.encoding_UTF8);                //字符集编码 可以使用UTF-8,GBK两种方式
		contentData.put("signMethod", "01");                           //签名方法 目前只支持01-RSA方式证书加密
		contentData.put("txnType", "77");                              //交易类型 11-代收
		contentData.put("txnSubType", "02");                           //交易子类型 02-消费短信
		contentData.put("bizType", "000301");                          //业务类型 认证支付2.0
		contentData.put("channelType", "07");                          //渠道类型07-PC

		/***商户接入参数***/
		contentData.put("merId", merId);                   			   //商户号码（本商户号码仅做为测试调通交易使用，该商户号配置了需要对敏感信息加密）测试时请改成自己申请的商户号，【自己注册的测试777开头的商户号不支持代收产品】
		contentData.put("accessType", "0");                            //接入类型，商户接入固定填0，不需修改	
		contentData.put("orderId", orderId);             			   //商户订单号，8-40位数字字母，不能含“-”或“_”，可以自行定制规则	
		contentData.put("txnTime", txnTime);         				   //订单发送时间，格式为YYYYMMDDhhmmss，必须取当前时间，否则会报txnTime无效
		contentData.put("currencyCode", "156");						   //交易币种（境内商户一般是156 人民币）
		contentData.put("txnAmt", txnAmt);							   //交易金额，单位分，不要带小数点
		contentData.put("accType", "01");                              //账号类型

		//必送手机号
		Map<String,String> customerInfoMap = new HashMap<String,String>();
		customerInfoMap.put("phoneNo", phoneNo);			        

		////////////如果商户号开通了【商户对敏感信息加密】的权限那么需要对 accNo,phoneNo加密使用：
		String accNo = AcpService.encryptData(accNoR, "UTF-8");  //这里测试的时候使用的是测试卡号，正式环境请使用真实卡号
		contentData.put("accNo", accNo);
		contentData.put("encryptCertId",AcpService.getEncryptCertId());       //加密证书的certId，配置在acp_sdk.properties文件 acpsdk.encryptCert.path属性下
		String customerInfoStr = AcpService.getCustomerInfoWithEncrypt(customerInfoMap,null,AcpBase.encoding_UTF8);

		/////////如果商户号未开通【商户对敏感信息加密】权限那么不需对敏感信息加密使用：
		//contentData.put("accNo", "6216261000000000018");            		//这里测试的时候使用的是测试卡号，正式环境请使用真实卡号
		//String customerInfoStr = DemoBase.getCustomerInfo(customerInfoMap,"6216261000000000018",DemoBase.encoding_UTF8);
		////////

		contentData.put("customerInfo", customerInfoStr);					
		//contentData.put("reqReserved", "透传字段");        					//请求方保留域，透传字段（可以实现商户自定义参数的追踪）本交易的后台通知,对本交易的交易状态查询交易、对账文件中均会原样返回，商户可以按需上传，长度为1-1024个字节		

		/**对请求参数进行签名并发送http post请求，接收同步应答报文**/
		Map<String, String> reqData = AcpService.sign(contentData,AcpBase.encoding_UTF8);			 //报文中certId,signature的值是在signData方法中获取并自动赋值的，只要证书配置正确即可。
		String requestBackUrl = SDKConfig.getConfig().getBackRequestUrl();   								 //交易请求url从配置文件读取对应属性文件acp_sdk.properties中的 acpsdk.backTransUrl
		Map<String, String> rspData = AcpService.post(reqData,requestBackUrl,AcpBase.encoding_UTF8); //发送请求报文并接受同步应答（默认连接超时时间30秒，读取返回结果超时时间30秒）;这里调用signData之后，调用submitUrl之前不能对submitFromData中的键值对做任何修改，如果修改会导致验签不通过

		/**对应答码的处理，请根据您的业务逻辑来编写程序,以下应答码处理逻辑仅供参考------------->**/
		//应答码规范参考open.unionpay.com帮助中心 下载  产品接口规范  《平台接入接口规范-第5部分-附录》

		if(!rspData.isEmpty()){
			if(AcpService.validate(rspData, AcpBase.encoding_UTF8)){
				LogUtil.writeLog("验证签名成功");
				String respCode = rspData.get("respCode") ;
				if(("00").equals(respCode)){
					//成功
					//TODO
					return true;
				}else{
					//其他应答码为失败请排查原因或做失败处理
					//TODO
					return false;
				}
			}else{
				LogUtil.writeErrorLog("验证签名失败");
				//TODO 检查验证签名失败的原因
				return false;
			}
		}else{
			//未返回正确的http状态
			LogUtil.writeErrorLog("未获取到返回报文或返回http状态码非200");
			return false;
		}
	}
	
	public String df(String merId,String txnAmt,String orderId,String txnTime,String accNoR,String identity,Fcapitaloperation fcapitaloperation){
		
		Map<String, String> contentData = new HashMap<String, String>();
		
		/***银联全渠道系统，产品参数，除了encoding自行选择外其他不需修改***/
		contentData.put("version", AcpBase.version);            //版本号 全渠道默认值
		contentData.put("encoding", AcpBase.encoding_UTF8);     //字符集编码 可以使用UTF-8,GBK两种方式
		contentData.put("signMethod", "01");           		 	//签名方法 目前只支持01：RSA方式证书加密
		contentData.put("txnType", "12");              		 	//交易类型 12：代付
		contentData.put("txnSubType", "00");           		 	//默认填写00
		contentData.put("bizType", "000401");          		 	//000401：代付
		contentData.put("channelType", "07");          		 	//渠道类型

		/***商户接入参数***/
		contentData.put("merId", merId);   		 				//商户号码，请改成自己申请的商户号或者open上注册得来的777商户号测试
		contentData.put("accessType", "0");            		 	//接入类型，商户接入填0 ，不需修改（0：直连商户， 1： 收单机构 2：平台商户）
		contentData.put("orderId", orderId);        	 	    //商户订单号，8-40位数字字母，不能含“-”或“_”，可以自行定制规则	
		contentData.put("txnTime", txnTime);		 		    //订单发送时间，取系统时间，格式为YYYYMMDDhhmmss，必须取当前时间，否则会报txnTime无效
		contentData.put("accType", "01");					 	//账号类型 01：银行卡02：存折03：IC卡帐号类型(卡介质)
		
		//////////如果商户号开通了  商户对敏感信息加密的权限那么，需要对 卡号accNo加密使用：
		contentData.put("encryptCertId",AcpService.getEncryptCertId());      //上送敏感信息加密域的加密证书序列号
		String accNo = AcpService.encryptData(accNoR, AcpBase.encoding_UTF8); //这里测试的时候使用的是测试卡号，正式环境请使用真实卡号
		contentData.put("accNo", accNo);
		//////////
		
		/////////商户未开通敏感信息加密的权限那么不对敏感信息加密使用：
		//contentData.put("accNo", "6216261000000000018");                  //这里测试的时候使用的是测试卡号，正式环境请使用真实卡号
		////////

		//代收交易的上送的卡验证要素为：姓名或者证件类型+证件号码
		Map<String,String> customerInfoMap = new HashMap<String,String>();
		customerInfoMap.put("certifTp", "01");						    //证件类型
		customerInfoMap.put("certifId", "341126197709218366");		    //证件号码
		//customerInfoMap.put("customerNm", "全渠道");					//姓名
		String customerInfoStr = AcpService.getCustomerInfo(customerInfoMap,accNoR,AcpBase.encoding_UTF8);				
		
		contentData.put("customerInfo", customerInfoStr);
		contentData.put("txnAmt", txnAmt);						 		//交易金额 单位为分，不能带小数点
		contentData.put("currencyCode", "156");                    	    //境内商户固定 156 人民币
		//contentData.put("reqReserved", "透传字段");                      //商户自定义保留域，如需使用请启用即可；交易应答时会原样返回
		
		//后台通知地址（需设置为外网能访问 http https均可），支付成功后银联会自动将异步通知报文post到商户上送的该地址，【支付失败的交易银联不会发送后台通知】
		//后台通知参数详见open.unionpay.com帮助中心 下载  产品接口规范  网关支付产品接口规范 消费交易 商户通知
		//注意:1.需设置为外网能访问，否则收不到通知    2.http https均可  3.收单后台通知后需要10秒内返回http200或302状态码 
		//    4.如果银联通知服务器发送通知后10秒内未收到返回状态码或者应答码非http200或302，那么银联会间隔一段时间再次发送。总共发送5次，银联后续间隔1、2、4、5 分钟后会再次通知。
		//    5.后台通知地址如果上送了带有？的参数，例如：http://abc/web?a=b&c=d 在后台通知处理程序验证签名之前需要编写逻辑将这些字段去掉再验签，否则将会验签失败
		contentData.put("backUrl", AcpBase.backUrl);
		
		/**对请求参数进行签名并发送http post请求，接收同步应答报文**/
		Map<String, String> reqData = AcpService.sign(contentData,AcpBase.encoding_UTF8);			 		 //报文中certId,signature的值是在signData方法中获取并自动赋值的，只要证书配置正确即可。
		String requestBackUrl = SDKConfig.getConfig().getBackRequestUrl();									 //交易请求url从配置文件读取对应属性文件acp_sdk.properties中的 acpsdk.backTransUrl
		
		Map<String, String> rspData = AcpService.post(reqData,requestBackUrl,AcpBase.encoding_UTF8);        //发送请求报文并接受同步应答（默认连接超时时间30秒，读取返回结果超时时间30秒）;这里调用signData之后，调用submitUrl之前不能对submitFromData中的键值对做任何修改，如果修改会导致验签不通过
		/**对应答码的处理，请根据您的业务逻辑来编写程序,以下应答码处理逻辑仅供参考------------->**/
		//应答码规范参考open.unionpay.com帮助中心 下载  产品接口规范  《平台接入接口规范-第5部分-附录》
		List<Fcapitaloperation> list;
		if(!rspData.isEmpty()){
			if(AcpService.validate(rspData, AcpBase.encoding_UTF8)){
				LogUtil.writeLog("验证签名成功");
				String respCode = rspData.get("respCode");
				if(("00").equals(respCode)){
					//交易已受理(不代表交易已成功），等待接收后台通知确定交易成功，也可以主动发起 查询交易确定交易状态。
					//TODO
					return "成功";
					//如果返回卡号且配置了敏感信息加密，解密卡号方法：
					//String accNo1 = resmap.get("accNo");
					//String accNo2 = AcpService.decryptPan(accNo1, "UTF-8");	//解密卡号使用的证书是商户签名私钥证书acpsdk.signCert.path
					//LogUtil.writeLog("解密后的卡号："+accNo2);
				}else if(("03").equals(respCode) ||
						 ("04").equals(respCode) ||
						 ("05").equals(respCode) ||
						 ("01").equals(respCode) ||
						 ("12").equals(respCode) ||
						 ("34").equals(respCode) ||
						 ("60").equals(respCode) ){
					//后续需发起交易状态查询交易确定交易状态。
					 queryState(merId, orderId, txnTime);
					 return rspData.get("respMsg");
					//TODO
				}else{
					//其他应答码为失败请排查原因
					String reString=rspData.get("respMsg");
					fcapitaloperation.setFremark(reString);
					list=capitaloperationService.findByProperty("forderId", orderId);
					list.get(0).setFstatus(CapitalOperationInStatus.Fail);//失败
					list.get(0).setFremark(rspData.get("respMsg"));
					capitaloperationService.updateObj(list.get(0));
					return rspData.get("respMsg");
				}
			}else{
				String result="验证签名失败";
				LogUtil.writeErrorLog(result);
				fcapitaloperation.setFremark(rspData.get("respMsg"));
				list=capitaloperationService.findByProperty("forderId", orderId);
				list.get(0).setFstatus(CapitalOperationInStatus.Fail);//失败
				list.get(0).setFremark(result);
				capitaloperationService.updateObj(list.get(0));
				return result;
				//TODO 检查验证签名失败的原因
			}	
		}else{
			//未返回正确的http状态
			String result="未获取到返回报文或返回http状态码非200";

			LogUtil.writeErrorLog(result);
			return result;
		}
	}

}
