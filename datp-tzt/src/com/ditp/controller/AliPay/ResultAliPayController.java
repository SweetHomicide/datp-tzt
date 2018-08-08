package com.ditp.controller.AliPay;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ditp.AilPayCom.RsaSign;
import com.ditp.service.StationMailService;
import com.ruizton.main.Enum.CapitalOperationInStatus;
import com.ruizton.main.controller.BaseController;
import com.ruizton.main.model.Fcapitaloperation;
import com.ruizton.main.model.Fuser;
import com.ruizton.main.model.Fvirtualwallet;
import com.ruizton.main.model.Fwallet;
import com.ruizton.main.service.admin.CapitaloperationService;
import com.ruizton.main.service.admin.UserService;
import com.ruizton.main.service.admin.VirtualWalletService;
import com.ruizton.main.service.admin.WalletService;
import com.ruizton.util.Comm;
@Controller
public class ResultAliPayController extends BaseController {
	@Autowired
	CapitaloperationService capitaloperationService;
	@Autowired
	private WalletService walletService;
	@Autowired
	private UserService userService;
	@Autowired
	private VirtualWalletService virtualWalletService;
	@Autowired
	private StationMailService stationMailService;
	Fwallet fwallet;

	public String resultAliPay(ServletRequest request, ServletResponse response)
	{
		try {
			StringBuffer info=new StringBuffer();  
			InputStream  in = request.getInputStream();  
      BufferedInputStream  buf = new BufferedInputStream(in);  
			byte[] buffer=new byte[1024];   
			int iRead;                            
			while((iRead=buf.read(buffer))!=-1){  
			    info.append(new String(buffer,0,iRead,"gbk"));  
			}
			return null;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}	
	}
	
	/**
	 * 异步返回数据
	 * @param dctx
	 * @param context
	 */
	@RequestMapping("account/resultAliPay")
	public  void alipay_notify_Sign(HttpServletRequest request,HttpServletResponse response) {
		 Map<String, String> params = new HashMap<String, String>();
		    Map requestParams = request.getParameterMap();
		    for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
		        String name = (String) iter.next();
		        String[] values = (String[]) requestParams.get(name);
		        String valueStr = "";
		        for (int i = 0; i < values.length; i++) {
		            valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
		        }
		       // System.out.println(">>>>>参数" + name + ":" + valueStr);
		        params.put(name, valueStr);
		    }
		    requestParams.get("trade_status");

		    String tradeNo = request.getParameter("out_trade_no");//商品id
		    String tradeStatus = request.getParameter("trade_status");
		    String total_fee=request.getParameter("total_amount");//金额
		    total_fee=Double.toString((Double.parseDouble(total_fee)/Comm.getALI_PAYNUMBER()));
		    if (RsaSign.verify(params)) {//验证成功	    	
		        if (tradeStatus.equals("TRADE_FINISHED") || tradeStatus.equals("TRADE_SUCCESS")) {
		         //   System.out.println(">>>>>状态要更新了");
		            //商品交易成功之后的业务逻辑代码
		        	List<Fcapitaloperation> list=capitaloperationService.findByProperty("forderId",tradeNo );
		        	Fvirtualwallet fvirtualwallet=null;
        			for(Fcapitaloperation fcapitaloperation:list){  
        				int fstatus=fcapitaloperation.getFstatus();
        				if(fstatus!=CapitalOperationInStatus.Come) {
        				//	 total_fee = data.GetValue("total_fee").toString();
        					if (null!=fcapitaloperation.getFviType()) {
        						String fivwFilter="where fvirtualcointype.fid='"+fcapitaloperation.getFviType().getFid()+"' and fuser.fid='"+fcapitaloperation.getFuser().getFid()+"'";
        						//获取平台钱包
        						fvirtualwallet= virtualWalletService.list(0, 0, fivwFilter, false).get(0);
        						//Fsubscription fsubscription = subscriptionService.findByFviId(fcapitaloperation.getFviType().getFid());
        						double amountBTC = fcapitaloperation.getFtotalBTC();
        						fvirtualwallet.setFlastUpdateTime(com.ruizton.util.Utils.getTimestamp());
        						fvirtualwallet.setFtotal(fvirtualwallet.getFtotal()+amountBTC);
        						virtualWalletService.updateObj(fvirtualwallet);
        					}else{
        						// 钱包更新金额
            					fwallet =userService.findById(fcapitaloperation.getFuser().getFid()).getFwallet();
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
        				fcapitaloperation.setFstatus(CapitalOperationInStatus.Come);
        				capitaloperationService.updateObj(fcapitaloperation);
              }  

		         // System.out.println(">>>>>下单成功" + tradeNo);
		        }  } else {//验证失败
		           // System.out.println(">>>>>验签失败" + tradeNo);
		           // System.out.println(">>>>>交易被关闭了");
		         
		        }
		    
	}
	
	
	
}
