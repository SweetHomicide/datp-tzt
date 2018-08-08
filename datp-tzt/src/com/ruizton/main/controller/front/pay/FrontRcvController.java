package com.ruizton.main.controller.front.pay;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;


import com.unionpay.acp.sdk.AcpService;
import com.unionpay.acp.sdk.LogUtil;
import com.unionpay.acp.sdk.SDKConstants;

/**
 * 重要：联调测试时请仔细阅读注释！
 * 
 * 产品：代收产品<br>
 * 功能：前台通知接收处理示例 <br>
 * 日期： 2015-09<br>
 * 版本： 1.0.0 
 * 版权： 中国银联<br>
 * 交易说明：支付成功点击“返回商户”按钮的时候出现的处理页面示例
 */
@Controller
public class FrontRcvController {
	@RequestMapping("/account/FrontRcvResponse")
	public ModelAndView FrontRcvResponse(			
			HttpServletRequest request,
			HttpServletResponse response
			) throws Exception{	
		ModelAndView modelAndView = new ModelAndView() ;

		LogUtil.writeLog("FrontRcvResponse前台接收报文返回开始");

		String encoding = request.getParameter(SDKConstants.param_encoding);
		LogUtil.writeLog("返回报文中encoding=[" + encoding + "]");
//		String pageResult = "";
		/*if (AcpBase.encoding_UTF8.equalsIgnoreCase(encoding)) {
			pageResult = "/utf8_result.jsp";
		} else {
			pageResult = "/gbk_result.jsp";
		}*/
		Map<String, String> respParam = getAllRequestParam(request);
		
		LogUtil.printRequestLog(respParam);

		Map<String, String> valideData = null;
		StringBuffer page = new StringBuffer();
		if (null != respParam && !respParam.isEmpty()) {
			Iterator<Entry<String, String>> it = respParam.entrySet()
					.iterator();
			valideData = new HashMap<String, String>(respParam.size());
			while (it.hasNext()) {
				Entry<String, String> e = it.next();
				String key = (String) e.getKey();
				String value = (String) e.getValue();
				value = new String(value.getBytes(encoding), encoding);
				/*page.append("<tr><td width=\"30%\" align=\"right\">" + key
						+ "(" + key + ")</td><td>" + value + "</td></tr>");*/
				valideData.put(key, value);
			}
		}
		if (!AcpService.validate(valideData, encoding)) {
//			page.append("<tr><td width=\"30%\" align=\"right\">验证签名结果</td><td>失败</td></tr>");
			LogUtil.writeLog("验证签名结果[失败].");
			modelAndView.addObject("data", "开卡失败，请重新开卡！");
		} else {
			
//			page.append("<tr><td width=\"30%\" align=\"right\">验证签名结果</td><td>成功</td></tr>");
			LogUtil.writeLog("验证签名结果[成功].");
			//System.out.println(valideData.get("orderId")); //其他字段也可用类似方式获取
			modelAndView.addObject("data", "开卡已成功，请重新进行支付！");
		}
		
		modelAndView.setViewName("front/payment/result");
		LogUtil.writeLog("FrontRcvResponse前台接收报文返回结束");
		
		return modelAndView;
	}
	/**
	 * 获取请求参数中所有的信息
	 * 
	 * @param request
	 * @return
	 */
	public static Map<String, String> getAllRequestParam(
			final HttpServletRequest request) {
		Map<String, String> res = new HashMap<String, String>();
		Enumeration<?> temp = request.getParameterNames();
		if (null != temp) {
			while (temp.hasMoreElements()) {
				String en = (String) temp.nextElement();
				String value = request.getParameter(en);
				res.put(en, value);
				// 在报文上送时，如果字段的值为空，则不上送<下面的处理为在获取所有参数数据时，判断若值为空，则删除这个字段>
				if (res.get(en) == null || "".equals(res.get(en))) {
					// System.out.println("======为空的字段名===="+en);
					res.remove(en);
				}
			}
		}
		return res;
	}
}
