package com.ditp.controller.WeChat;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ditp.WeChat.comm.WxPayAPI;
import com.ditp.WeChat.comm.WxPayData;
import com.ditp.service.StationMailService;
import com.ruizton.main.Enum.CapitalOperationInStatus;
import com.ruizton.main.model.Fcapitaloperation;
import com.ruizton.main.model.Fvirtualwallet;
import com.ruizton.main.model.Fwallet;
import com.ruizton.main.service.admin.CapitaloperationService;
import com.ruizton.main.service.admin.UserService;
import com.ruizton.main.service.admin.VirtualWalletService;
import com.ruizton.main.service.admin.WalletService;
import com.ruizton.util.Utils;
@Controller
public class ResultNotifyController {
	
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

	@RequestMapping("account/resultNotify")
	public WxPayData  resultNotify(ServletRequest request, ServletResponse response)
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
            //转换数据格式并验证签名
            WxPayData data = new WxPayData();
                try {
					data.FromXml(info.toString());
				} catch (Exception e) {
					 //若签名错误，则立即返回结果给微信支付后台
	                WxPayData res = new WxPayData();
	                res.SetValue("return_code", "FAIL");
	                res.SetValue("return_msg", e.getMessage());
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                
                //检查支付结果中transaction_id是否存在
                if (!data.IsSet("transaction_id"))
                {
                    //若transaction_id不存在，则立即返回结果给微信支付后台
                    WxPayData res = new WxPayData();
                    res.SetValue("return_code", "FAIL");
                    res.SetValue("return_msg", "支付结果中微信订单号不存在");   
                    return res;
                }
               String transaction_id = data.GetValue("transaction_id").toString();

                //查询订单，判断订单真实性
                if (!QueryOrder(transaction_id))
                {
                    //若订单查询失败，则立即返回结果给微信支付后台
                    WxPayData res = new WxPayData();
                    res.SetValue("return_code", "FAIL");
                    res.SetValue("return_msg", "订单查询失败");
                    return res;
                }
                //查询订单成功
                else
                {
                    WxPayData res = new WxPayData();
                    res.SetValue("return_code", "SUCCESS");
                    res.SetValue("return_msg", "OK");
                    String out_trade_no=data.GetValue("out_trade_no").toString();//订单号
                    //
        			List<Fcapitaloperation> list=capitaloperationService.findByProperty("forderId",out_trade_no );
        			Fvirtualwallet fvirtualwallet=null;
        			for(Fcapitaloperation fcapitaloperation:list){  
        				int fstatus=fcapitaloperation.getFstatus();
        				if(fstatus!=CapitalOperationInStatus.Come) {
        					String total_fee = data.GetValue("total_fee").toString();
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
        					// 钱包解冻相对应金额
        					fwallet =userService.findById(fcapitaloperation.getFuser().getFid()).getFwallet();
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
        				fcapitaloperation.setFstatus(CapitalOperationInStatus.Come);
        				capitaloperationService.updateObj(fcapitaloperation);
              } 
                    return res;
                }
			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}  


            /* 
             * {"dataJson":"","syncType":2} 
             */         
	}
	
    //查询订单
    private boolean QueryOrder(String transaction_id)
    {
        WxPayData req = new WxPayData();
        req.SetValue("transaction_id", transaction_id);
        WxPayData res = WxPayAPI.OrderQuery(req);
        if (res.GetValue("return_code").toString().equals("SUCCESS") &&
            res.GetValue("result_code").toString().equals("SUCCESS"))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
}
