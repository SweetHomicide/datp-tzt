package com.ruizton.main.controller.front;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.ruizton.main.Enum.CountLimitTypeEnum;
import com.ruizton.main.Enum.EntrustStatusEnum;
import com.ruizton.main.Enum.VirtualCoinTypeStatusEnum;
import com.ruizton.main.comm.ApiParam;
import com.ruizton.main.controller.BaseController;
import com.ruizton.main.model.Fapi;
import com.ruizton.main.model.Fentrust;
import com.ruizton.main.model.Fuser;
import com.ruizton.main.model.Fvirtualcointype;
import com.ruizton.main.model.Fvirtualwallet;
import com.ruizton.main.model.Fwallet;
import com.ruizton.main.service.front.FrontTradeService;
import com.ruizton.main.service.front.FrontUserService;
import com.ruizton.main.service.front.FrontVirtualCoinService;
import com.ruizton.util.ApiCoder;
import com.ruizton.util.Constant;
import com.ruizton.util.Utils;

@Controller
public class FrontApiJsonController extends BaseController{/*

	@Autowired
	private FrontUserService frontUserService ;
	@Autowired
	private FrontVirtualCoinService frontVirtualCoinService ;
	@Autowired
	private FrontTradeService frontTradeService ;
	private Fuser fuser = null ;
	
	 * "partner=2088101568338364"，
	"secretKey=xxxxxxxxxx"（MD5加密后发送） 
	"symbol=1",（对应数据库虚拟币的id，）
	"type=1",（交易类型，买1，卖2） 
	"count=1.0"（交易数量） 
	"prize=1.0"（交易单价）
	 * 
	
	*//**
	 * @param apiParam
	 * @return
	 *//*
	public boolean validate(ApiParam apiParam){
		this.fuser = this.frontUserService.findById(ApiCoder.decode(apiParam.getPartner())) ;
		if(fuser!=null){
			Fapi api = fuser.getFapi() ;
			if(api!=null){
				try {
					if(Utils.MD5(api.getFsecret()).equalsIgnoreCase(apiParam.getSecretKey())){
						return true ;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return false ;
	}
	
	@ResponseBody
	@RequestMapping("/api/userinfo")
	public String userinfo(ApiParam apiParam) throws Exception{
		JSONObject jsonObject= new JSONObject() ;
		if(!validate(apiParam)){
			jsonObject.accumulate("result", false) ;
			jsonObject.accumulate("errorCode", 10003) ;
			return jsonObject.toString() ;
		}
		
		jsonObject.accumulate("result", true) ;
		
		JSONObject free = new JSONObject() ;
		JSONObject frozen = new JSONObject() ;
		
		Fwallet fwallet = this.frontUserService.findFwalletById(fuser.getFwallet().getFid()) ;
		free.accumulate("cny", fwallet.getFtotalRmb()) ;
		frozen.accumulate("cny", fwallet.getFfrozenRmb()) ;
		
		Map<Integer,Fvirtualwallet> fvirtualwallets = this.frontUserService.findVirtualWallet(fuser.getFid()) ;
		for (Map.Entry<Integer, Fvirtualwallet> entry : fvirtualwallets.entrySet()) {
			free.accumulate(entry.getValue().getFvirtualcointype().getfShortName(), entry.getValue().getFtotal()) ;
			frozen.accumulate(entry.getValue().getFvirtualcointype().getfShortName(), entry.getValue().getFfrozen()) ;
		}
		
		JSONObject funds = new JSONObject() ;
		JSONObject info = new JSONObject() ;
		funds.accumulate("free", free) ;
		funds.accumulate("freezed", frozen) ;
		info.accumulate("funds", funds) ;
		
		jsonObject.accumulate("info", info) ;
		
		return jsonObject.toString() ;
	}
	
	@ResponseBody
	@RequestMapping("/api/trade")
	public String trade(ApiParam apiParam) throws Exception{
		JSONObject jsonObject= new JSONObject() ;
		if(!validate(apiParam)){
			jsonObject.accumulate("result", false) ;
			jsonObject.accumulate("errorCode", 10003) ;
			return jsonObject.toString() ;
		}

		Fvirtualcointype fvirtualcointype = this.frontVirtualCoinService.findFvirtualCoinById(apiParam.getSymbol()) ;
		if(fvirtualcointype==null || fvirtualcointype.getFstatus()==VirtualCoinTypeStatusEnum.Abnormal){
			jsonObject.accumulate("result", false) ;
			jsonObject.accumulate("resultCode", 10000) ;
			return jsonObject.toString() ;
		}
		
		
		if(apiParam.getType()==1){
			//買
			if(apiParam.getCount()<0.01F){
				jsonObject.accumulate("result", false) ;
				jsonObject.accumulate("resultCode", 10006) ;
				return jsonObject.toString() ;
			}
			
			if(apiParam.getPrize()<0.000001F){
				jsonObject.accumulate("result", false) ;
				jsonObject.accumulate("resultCode", 10006) ;
				return jsonObject.toString() ;
			}
			
			double totalTradePrice = apiParam.getCount()*apiParam.getPrize() ;
		
			Fwallet fwallet = fuser.getFwallet() ;
			if(fwallet.getFtotalRmb()<totalTradePrice){
				jsonObject.accumulate("result", false) ;
				jsonObject.accumulate("resultCode", 10005) ;
				return jsonObject.toString() ;
			}
			
			boolean flag = false ;
			try {
				flag = this.frontTradeService.updateEntrustBuy(apiParam.getSymbol(), apiParam.getCount(), apiParam.getPrize(), fuser, false,request) ;
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(flag){
				jsonObject.accumulate("result", true) ;
				jsonObject.accumulate("resultCode", 0) ;
			}else{
				jsonObject.accumulate("result", false) ;
				jsonObject.accumulate("resultCode", 10002) ;
			}
			
			return jsonObject.toString() ;
		}else if(apiParam.getType()==2){
			//賣
			if(apiParam.getCount()<0.01F){
				jsonObject.accumulate("result", false) ;
				jsonObject.accumulate("resultCode", 10006) ;
				return jsonObject.toString() ;
			}
			
			if(apiParam.getPrize()<0.000001F){
				jsonObject.accumulate("result", false) ;
				jsonObject.accumulate("resultCode", 10006) ;
				return jsonObject.toString() ;
			}
			
			Fvirtualwallet fvirtualwallet = this.frontUserService.findVirtualWalletByUser(fuser.getFid(), apiParam.getSymbol()) ;
			if(fvirtualwallet==null){
				jsonObject.accumulate("result", false) ;
				jsonObject.accumulate("resultCode", 10000) ;
				return jsonObject.toString() ;
			}
			if(fvirtualwallet.getFtotal()<apiParam.getCount()){
				jsonObject.accumulate("result", false) ;
				jsonObject.accumulate("resultCode", 10005) ;
				return jsonObject.toString() ;
			}
			
			
			boolean flag = false ;
			try {
				flag = this.frontTradeService.updateEntrustSell(apiParam.getSymbol(), apiParam.getCount(), apiParam.getPrize(), fuser, false,request) ;
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(flag){
				jsonObject.accumulate("result", true) ;
				return jsonObject.toString() ;
			}else{
				jsonObject.accumulate("resultCode", 10002) ;
				return jsonObject.toString() ;
			}
			
		
		}else{
			jsonObject.accumulate("result", false) ;
			jsonObject.accumulate("resultCode", 10000) ;
			return jsonObject.toString() ;
		}
		
	}
	@ResponseBody
	@RequestMapping("/api/cancelorder")
	public String cancelorder(ApiParam apiParam) throws Exception{
		JSONObject jsonObject= new JSONObject() ;
		if(!validate(apiParam)){
			jsonObject.accumulate("result", false) ;
			jsonObject.accumulate("errorCode", 10003) ;
			return jsonObject.toString() ;
		}
		
		int id = apiParam.getId() ;
		
		Fentrust fentrust = this.frontTradeService.findFentrustById(id) ;
		if(fentrust!=null
				&&(fentrust.getFstatus()==EntrustStatusEnum.Going || fentrust.getFstatus()==EntrustStatusEnum.PartDeal )
				&&fentrust.getFuser().getFid() == fuser.getFid() ){
			try {
				this.frontTradeService.updateCancelFentrust(fentrust, fuser) ;
				jsonObject.accumulate("result", true) ;
				return jsonObject.toString() ;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else{
			jsonObject.accumulate("result", false) ;
			jsonObject.accumulate("errorCode", 10004) ;
			return jsonObject.toString() ;
		}
		
		jsonObject.accumulate("result", false) ;
		jsonObject.accumulate("errorCode", 10002) ;
		return jsonObject.toString() ;
	}
	
	@ResponseBody
	@RequestMapping("/api/getorder")
	public String getorder(ApiParam apiParam) throws Exception{
		JSONObject jsonObject= new JSONObject() ;
		if(!validate(apiParam)){
			jsonObject.accumulate("result", false) ;
			jsonObject.accumulate("errorCode", 10003) ;
			return jsonObject.toString() ;
		}
		
		Fvirtualcointype fvirtualcointype = this.frontVirtualCoinService.findFvirtualCoinById(apiParam.getSymbol()) ;
		if(fvirtualcointype==null ||fvirtualcointype.getFstatus()==VirtualCoinTypeStatusEnum.Abnormal){
			jsonObject.accumulate("result", false) ;
			jsonObject.accumulate("errorCode", 10000) ;
			return jsonObject.toString() ;
		}
		
		//委托记录
		List<Fentrust> fentrusts = this.frontTradeService.findFentrustHistory(
				fuser.getFid(), apiParam.getSymbol(),null, 0, Integer.MAX_VALUE, " flastUpdatTime desc ", new int[]{EntrustStatusEnum.Going,EntrustStatusEnum.PartDeal}) ;
		
		List<JSONObject> list = new ArrayList<JSONObject>() ;
		for (Fentrust fentrust : fentrusts) {
			double avg = 0;
			if((fentrust.getFcount()-fentrust.getFleftCount())!=0){
				avg = (fentrust.getFsuccessAmount()/(fentrust.getFcount()-fentrust.getFleftCount())) ;
			}
			
			double type = fentrust.getFentrustType() ;
			int id = fentrust.getFid() ;
			int status = fentrust.getFstatus() ;
			double amount = fentrust.getFamount() ;
			double success_amount = fentrust.getFsuccessAmount() ;
			
			JSONObject item = new JSONObject() ;
			item.accumulate("orders_id", id) ;
			item.accumulate("status", status) ;
			item.accumulate("type", type) ;
			item.accumulate("rate", avg) ;
			item.accumulate("amount", amount) ;
			item.accumulate("deal_amount", success_amount) ;
			list.add(item) ;
		}
		jsonObject.accumulate("result", list) ;
		
		return jsonObject.toString() ;
	}
*/}
