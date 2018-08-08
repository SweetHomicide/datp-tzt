package com.ruizton.main.controller.front;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ruizton.main.Enum.CountLimitTypeEnum;
import com.ruizton.main.Enum.MessageTypeEnum;
import com.ruizton.main.Enum.TransportlogStatusEnum;
import com.ruizton.main.Enum.VirtualCoinTypeStatusEnum;
import com.ruizton.main.comm.ConstantMap;
import com.ruizton.main.controller.BaseController;
import com.ruizton.main.model.Ftransportlog;
import com.ruizton.main.model.Fuser;
import com.ruizton.main.model.Fvirtualcointype;
import com.ruizton.main.model.Fvirtualwallet;
import com.ruizton.main.service.admin.TransportlogService;
import com.ruizton.main.service.admin.VirtualCoinService;
import com.ruizton.main.service.front.FrontUserService;
import com.ruizton.main.service.front.FrontValidateService;
import com.ruizton.util.Constant;
import com.ruizton.util.GoogleAuth;
import com.ruizton.util.Utils;

@Controller
public class FrontTransferJsonController extends BaseController{
//	@Autowired
//	private FrontUserService frontUserService;
//	@Autowired
//	private ConstantMap map;
//	@Autowired
//	private VirtualCoinService virtualCoinService;
//	@Autowired
//	private TransportlogService transportlogService;
//	@Autowired
//	private FrontValidateService frontValidateService;
//	
//	
//	
//	@ResponseBody
//	@RequestMapping(value="/account/cancelTransport",produces={JsonEncode})
//	public String cancelTransport(
//			HttpServletRequest request,
//			@RequestParam(required=true)int fid
//			) throws Exception {
//		JSONObject js = new JSONObject();
//		Ftransportlog log = this.transportlogService.findById(fid);
//		Fuser fuser = this.frontUserService.findById(GetSession(request).getFid()) ;
//		if(fuser.getFid() != log.getFuser().getFid()){
//			js.accumulate("code", -1);
//			js.accumulate("msg", "操作异常");
//			return js.toString();
//		}
//		if(log.getFstatus() != TransportlogStatusEnum.NORMAL_VALUE){
//			js.accumulate("code", -1);
//			js.accumulate("msg", "当前状态不允许取消");
//			return js.toString();
//		}
//		log.setFstatus(TransportlogStatusEnum.CANCEL_VALUE);
//		Fvirtualwallet fvirtualwallet = this.frontUserService.findVirtualWalletByUser(log.getFuser().getFid(), log.getFvirtualcointype().getFid()) ;
//		fvirtualwallet.setFtotal(fvirtualwallet.getFtotal()+log.getFamount()+log.getFfees());
//		fvirtualwallet.setFfrozen(fvirtualwallet.getFfrozen()-log.getFamount()-log.getFfees());
//		try {
//			this.frontUserService.updateCancelTransport(log, fvirtualwallet) ;
//		} catch (Exception e) {
//			js.accumulate("code", -1);
//			js.accumulate("msg", "网络异常");
//			return js.toString();
//		}
//
//		js.accumulate("code", 0);
//		js.accumulate("msg", "取消成功");
//		return js.toString();
//	}
//	
//	
//	@ResponseBody
//	@RequestMapping(value="/json/transfer/getType",produces={JsonEncode})
//	public String getType(
//			HttpServletRequest request,
//			@RequestParam(required=true)int typeid
//			) throws Exception {
//		JSONObject js = new JSONObject();
//		
//		Fvirtualcointype coin = this.virtualCoinService.findById(typeid);
//        if(coin == null || coin.getFstatus() != VirtualCoinTypeStatusEnum.Normal){
//        	js.accumulate("code", -1);
//    		js.accumulate("msg", "非法操作");
//    		return js.toString();
//        }
//		
//        try {
//			Fvirtualwallet v = this.frontUserService.findVirtualWalletByUser(GetSession(request).getFid(), typeid);
//			js.accumulate("amt", v.getFtotal());
//			js.accumulate("code", 0);
//			js.accumulate("coin", coin.getFname());
//			return js.toString();
//		} catch (Exception e) {
//			js.accumulate("code", -1);
//			js.accumulate("msg", "网络异常");
//			return js.toString();
//		}
//	}
//	
//	@ResponseBody
//	@RequestMapping(value="/account/json/btcTransport",produces={JsonEncode})
//	public String btcTransport(
//			HttpServletRequest request,
//			@RequestParam(required=true)int address,
//			@RequestParam(required=true)int fid,
//			@RequestParam(required=true)double amount,
//			@RequestParam(required=true)String passwd,
//			@RequestParam(required=false,defaultValue="0")String totpCode,
//			@RequestParam(required=false,defaultValue="0")String phoneCode
//			) throws Exception {
//		JSONObject js = new JSONObject();
//		amount = Utils.getDouble(amount, 4) ;
//		Fvirtualcointype type = this.virtualCoinService.findById(fid);
//		String openTransport = this.map.get("openTransport").toString();
//		if(!openTransport.equals("1")){
//			js.accumulate("code", -1);
//			js.accumulate("msg", "会员转账功能暂未开放");
//			return js.toString();
//		}
//		
//		if(type == null || type.getFstatus() != 1){
//			js.accumulate("code", -1);
//			js.accumulate("msg", "虚拟币不存在");
//			return js.toString();
//		}
//
//		Fuser fuser = this.frontUserService.findById(address);
//		if(fuser == null){
//			js.accumulate("code", -1);
//			js.accumulate("msg", "会员UID不存在,请查正再输入");
//			return js.toString();
//		}
//		
//		if(amount <0.1D){
//			js.accumulate("code", -1);
//			js.accumulate("msg", "最小转账数量为0.1个");
//			return js.toString();
//		}
//		
//		Fuser preUser = this.frontUserService.findById(GetSession(request).getFid()) ;
//		
//		if(preUser.getFtradePassword() == null){
//			js.accumulate("code", -1);
//			js.accumulate("msg", "请先设置交易密码");
//			return js.toString();
//		}
//		
//		if(!preUser.getFgoogleBind() && !preUser.isFisTelephoneBind()){
//			//没有绑定谷歌或者手机
//			js.accumulate("code", -1) ;
//			js.accumulate("msg","请先绑定GOOGLE验证或者绑定手机号码") ;
//			return js.toString() ;
//		}
//		
//		String ip = Utils.getIp(request) ;
//		if(preUser.getFtradePassword()!=null){
//			int trade_limit = this.frontValidateService.getLimitCount(ip, CountLimitTypeEnum.TRADE_PASSWORD) ;
//			if(trade_limit<=0){
//				js.accumulate("code", -1) ;
//				js.accumulate("msg","交易密码有误，请稍候再试") ;
//				return js.toString() ;
//			}else{
//				boolean flag = preUser.getFtradePassword().equals(Utils.MD5(passwd)) ;
//				if(!flag){
//					js.accumulate("code", -1) ;
//					js.accumulate("msg","交易密码有误，您还有"+(trade_limit-1)+"次机会") ;
//					this.frontValidateService.updateLimitCount(ip, CountLimitTypeEnum.TRADE_PASSWORD) ;
//					return js.toString() ;
//				}else if(trade_limit<Constant.ErrorCountLimit){
//					this.frontValidateService.deleteCountLimite(ip, CountLimitTypeEnum.TRADE_PASSWORD) ;
//				}
//			}
//		}
//		
//		Fvirtualwallet fvirtualwallet = this.frontUserService.findVirtualWalletByUser(GetSession(request).getFid(), fid) ;
//		if(fvirtualwallet.getFtotal()<amount){
//			js.accumulate("code", -1) ;
//			js.accumulate("msg","余额不足") ;
//			return js.toString() ;
//		}
//		
//		int userId = fuser.getFid();
//		if(userId == GetSession(request).getFid()){
//			js.accumulate("code", -1) ;
//			js.accumulate("msg","不允许转给自己") ;
//			return js.toString() ;
//		}
//		
//		if(preUser.getFgoogleBind()){
//			int google_limit = this.frontValidateService.getLimitCount(ip, CountLimitTypeEnum.GOOGLE) ;
//			if(google_limit<=0){
//				js.accumulate("code", -1) ;
//				js.accumulate("msg","谷歌验证码有误，请稍候再试") ;
//				return js.toString() ;
//			}else{
//				boolean flag = GoogleAuth.auth(Long.parseLong(totpCode.trim()), preUser.getFgoogleAuthenticator()) ;
//				if(!flag){
//					js.accumulate("code", -1) ;
//					js.accumulate("msg","谷歌验证码有误，您还有"+(google_limit-1)+"次机会") ;
//					this.frontValidateService.updateLimitCount(ip, CountLimitTypeEnum.GOOGLE) ;
//					return js.toString() ;
//				}else if(google_limit<Constant.ErrorCountLimit){
//					this.frontValidateService.deleteCountLimite(ip, CountLimitTypeEnum.GOOGLE) ;
//				}
//			}
//		}
//		
//		if(preUser.isFisTelephoneBind()){
//			int tel_limit = this.frontValidateService.getLimitCount(ip, CountLimitTypeEnum.TELEPHONE) ;
//			if(tel_limit<=0){
//				js.accumulate("code", -1) ;
//				js.accumulate("msg","手机验证码有误，请稍候再试") ;
//				return js.toString() ;
//			}else{
//				boolean flag = validateMessageCode(preUser, preUser.getFareaCode(), preUser.getFtelephone(), MessageTypeEnum.TRANSFER_CODE, phoneCode) ;
//				if(!flag){
//					js.accumulate("code", -1) ;
//					js.accumulate("msg","手机验证码有误，您还有"+(tel_limit-1)+"机会") ;
//					this.frontValidateService.updateLimitCount(ip, CountLimitTypeEnum.TELEPHONE) ;
//					return js.toString() ;
//				}else if(tel_limit<Constant.ErrorCountLimit){
//					this.frontValidateService.deleteCountLimite(ip, CountLimitTypeEnum.TELEPHONE) ;
//				}
//			}
//		}
//		
//		double transferRate = Double.valueOf(this.map.get("transferRate").toString());
//		double ffee = Utils.getDouble(amount*transferRate, 4);
//		double last = Utils.getDouble(amount-ffee, 4);
//		Ftransportlog ftransportlog = new Ftransportlog() ;
//		ftransportlog.setFaddress(String.valueOf(address)) ;
//		ftransportlog.setFamount(last) ;
//		ftransportlog.setFfees(ffee);
//		ftransportlog.setFcreatetime(Utils.getTimestamp()) ;
//		ftransportlog.setFvirtualcointype(type);
//		ftransportlog.setFuser(preUser) ;
//		ftransportlog.setFstatus(TransportlogStatusEnum.NORMAL_VALUE);
//		fvirtualwallet.setFtotal(fvirtualwallet.getFtotal()-amount) ;
//		fvirtualwallet.setFfrozen(fvirtualwallet.getFfrozen()+amount);
//		
//		try {
//			this.frontUserService.updateTransport(ftransportlog, fvirtualwallet) ;
//		} catch (Exception e) {
//			js.accumulate("code", -1) ;
//			js.accumulate("msg","网络异常") ;
//			return js.toString() ;
//		}
//		
//		js.accumulate("code", 0) ;
//		js.accumulate("msg","操作成功") ;
//		return js.toString();
//	}
	
}
