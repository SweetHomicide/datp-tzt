package com.ruizton.main.controller.front;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.ruizton.main.Enum.BankTypeEnum;
import com.ruizton.main.Enum.CapitalOperationInStatus;
import com.ruizton.main.Enum.CapitalOperationTypeEnum;
import com.ruizton.main.Enum.RemittanceTypeEnum;
import com.ruizton.main.Enum.SystemBankInfoEnum;
import com.ruizton.main.comm.ConstantMap;
import com.ruizton.main.controller.BaseController;
import com.ruizton.main.model.FbankinfoWithdraw;
import com.ruizton.main.model.Fcapitaloperation;
import com.ruizton.main.model.Fsubscription;
import com.ruizton.main.model.Fuser;
import com.ruizton.main.model.Systembankinfo;
import com.ruizton.main.service.admin.AdminService;
import com.ruizton.main.service.admin.BankinfoWithdrawService;
import com.ruizton.main.service.admin.CapitaloperationService;
import com.ruizton.main.service.front.FrontAccountService;
import com.ruizton.main.service.front.FrontBankInfoService;
import com.ruizton.main.service.front.FrontUserService;
import com.ruizton.util.Constant;
import com.ruizton.util.Utils;

@Controller
public class FrontAccountOnlineController extends BaseController{
	
	@Autowired
	private FrontBankInfoService frontBankInfoService ;
	@Autowired
	private AdminService adminService;
	@Autowired
	private CapitaloperationService capitaloperationService;
	@Autowired
	private ConstantMap constantMap;
	@Autowired
	BankinfoWithdrawService bankinfoWithdrawService;
	@Autowired
	private FrontUserService frontUserService ;
	@Autowired
	private FrontAccountService frontAccountService ;
	
	/**
	 * 银联在线支付
	 * @param request
	 * @param currentPage
	 * @param type
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("account/rechargeOnline")
	public ModelAndView rechargeOnline(
			HttpServletRequest request,
			@RequestParam(required=false,defaultValue="")String currentPage,
			@RequestParam(required=false,defaultValue="2")int type,
			@RequestParam(required=false,defaultValue="")String selectType,
			@RequestParam(required=false,defaultValue="")String fviId
			) throws Exception{
		int cur = 1 ;
		if(currentPage==null||"".equals(currentPage)){
			cur = 1; 
		}else{
			cur = Integer.parseInt(currentPage) ;
		}
		
		ModelAndView modelAndView = new ModelAndView() ;
		modelAndView.addObject("selectType",selectType) ;
		//人民币随机尾数
		int randomMoney = (new Random().nextInt(80)+11) ;
		//系统银行账号
		List<Systembankinfo> systembankinfos = this.frontBankInfoService.findAllSystemBankInfo() ;
		for (Systembankinfo systembankinfo : systembankinfos) {
			int l = systembankinfo.getFbankNumber().length();
			systembankinfo.setFbankNumber(systembankinfo.getFbankNumber().substring(l-4, l));
		}
		
		modelAndView.addObject("randomMoney",randomMoney) ;
		modelAndView.addObject("bankInfo",systembankinfos) ;
		
		Fuser fuser = this.frontUserService.findById4WithDraw(GetSession(request).getFid()) ;
		boolean isBindGoogle = fuser.getFgoogleBind() ;
		boolean isBindTelephone = fuser.isFisTelephoneBind() ;
        modelAndView.addObject("isBindTelephone", isBindTelephone) ;
        modelAndView.addObject("isBindGoogle", isBindGoogle) ;
		//record
//		Fuser fuser = this.GetSession(request) ;
		StringBuffer filter = new StringBuffer();
		filter.append("where fuser.fid='"+fuser.getFid()+"' \n");
		filter.append("and ftype ="+CapitalOperationTypeEnum.RMB_IN+"\n");
		if(type ==2){
			filter.append("and fremittanceType='"+RemittanceTypeEnum.getEnumString(type)+"' \n");
		}else{
			filter.append("and systembankinfo is not null \n");
		}
		
		if ("".equals(fviId)) {
			filter.append(" and fviType is null  order by fcreateTime desc \n");
		}else{
			filter.append(" and fviType = '"+fviId+"'  order by fcreateTime desc \n");
		}
		List<Fcapitaloperation> list = this.capitaloperationService.list((cur-1)*Constant.AppRecordPerPage, Constant.RecordPerPage, filter.toString(), true);
		
		int totalCount = this.adminService.getAllCount("Fcapitaloperation", filter.toString());
		String url = "/account/rechargeOnline.html?type="+type+"&";
		String pagin = this.generatePagin(totalCount/Constant.RecordPerPage+( (totalCount%Constant.RecordPerPage)==0?0:1), cur,url) ;
		
		//最小充值金额
		double minRecharge = Double.parseDouble(this.constantMap.get("minrechargecny").toString().trim()) ;
		modelAndView.addObject("minRecharge", minRecharge) ;
		
		Map<Integer,String> bankTypes = new HashMap<Integer,String>() ;
		for(int i=1;i<=BankTypeEnum.QT;i++){
			if(BankTypeEnum.getEnumString(i) != null && BankTypeEnum.getEnumString(i).trim().length() >0){
				bankTypes.put(i,BankTypeEnum.getEnumString(i)) ;
			}			
		}
		//TODO 
		String filter1 = "where fuser.fid='"+fuser.getFid()+"' and fbankType >0";
		List<FbankinfoWithdraw> fbankinfoWithdraws =this.frontUserService.findFbankinfoWithdrawByFuser(0, 0, filter1, false);
		for (FbankinfoWithdraw fbankinfoWithdraw : fbankinfoWithdraws) {
			int l = fbankinfoWithdraw.getFbankNumber().length();
			fbankinfoWithdraw.setFbankNumber(fbankinfoWithdraw.getFbankNumber().substring(l-4, l));
		}
		//可以充值的数字资产
		List<Fsubscription> listFsubscription=getRechargeTypeList();
		modelAndView.addObject("listRechargeType", listFsubscription);	//可以充值的数字资产
		modelAndView.addObject("bankTypes", bankTypes) ;
		modelAndView.addObject("orderId", UUID.randomUUID().toString().replaceAll("\\-", ""));
		modelAndView.addObject("list", list) ;
		modelAndView.addObject("pagin", pagin) ;
		modelAndView.addObject("cur_page", cur) ;
		modelAndView.addObject("fuser",GetSession(request)) ;
		modelAndView.addObject("type", type) ;
		modelAndView.addObject("selectType", fviId) ;
		modelAndView.addObject("fbankinfoWithdraws", fbankinfoWithdraws) ;
		modelAndView.setViewName("front/account/account_rechargeOnline") ;
		return modelAndView ;
	}
	
	/**
	 * 银联在线提现
	 * @param currentPage
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("account/withdrawOnline")
	public ModelAndView withdrawCny(
			@RequestParam(required=false,defaultValue="")String currentPage,
			HttpServletRequest request
			) throws Exception{
		ModelAndView modelAndView = new ModelAndView() ;
		int cur = 1 ;
		if(currentPage==null||"".equals(currentPage)){
			cur = 1; 
		}else{
			cur = Integer.parseInt(currentPage) ;
		}
		
		Fuser fuser = this.frontUserService.findById4WithDraw(GetSession(request).getFid()) ;
		String filter = "where fuser.fid='"+fuser.getFid()+"' and fbankType >0";
		List<FbankinfoWithdraw> fbankinfoWithdraws =this.frontUserService.findFbankinfoWithdrawByFuser(0, 0, filter, false);
		for (FbankinfoWithdraw fbankinfoWithdraw : fbankinfoWithdraws) {
			int l = fbankinfoWithdraw.getFbankNumber().length();
			fbankinfoWithdraw.setFbankNumber(fbankinfoWithdraw.getFbankNumber().substring(l-4, l));
		}
		
		Map<Integer,String> bankTypes = new HashMap<Integer,String>() ;
		for(int i=1;i<=BankTypeEnum.QT;i++){
			if(BankTypeEnum.getEnumString(i) != null && BankTypeEnum.getEnumString(i).trim().length() >0){
				bankTypes.put(i,BankTypeEnum.getEnumString(i)) ;
			}			
		}
		modelAndView.addObject("bankTypes", bankTypes) ;
		
		double fee = this.frontAccountService.findWithdrawFeesByLevel(fuser.getFscore().getFlevel());
		modelAndView.addObject("fee", fee) ;
		
		//獲得千12條交易記錄
		String param = "where fuser.fid='"+fuser.getFid()+"' and ftype="+CapitalOperationTypeEnum.RMB_OUT+" order by fCreateTime desc";
		List<Fcapitaloperation> fcapitaloperations = this.frontAccountService.findCapitalList((cur-1)*Constant.RecordPerPage, Constant.RecordPerPage, param, true) ;
		int totalCount = this.adminService.getAllCount("Fcapitaloperation", param.toString());
		String url = "/account/withdrawOnline.html?";
		String pagin = this.generatePagin(totalCount/Constant.RecordPerPage+( (totalCount%Constant.RecordPerPage)==0?0:1), cur,url) ;
		
		boolean isBindGoogle = fuser.getFgoogleBind() ;
		boolean isBindTelephone = fuser.isFisTelephoneBind() ;
		modelAndView.addObject("isBindGoogle", isBindGoogle) ;
        modelAndView.addObject("isBindTelephone", isBindTelephone) ;
		
		modelAndView.addObject("pagin", pagin) ;
		modelAndView.addObject("fcapitaloperations", fcapitaloperations) ;
		modelAndView.addObject("fuser",fuser) ;
		modelAndView.addObject("fbankinfoWithdraws",fbankinfoWithdraws) ;
		modelAndView.setViewName("front/account/account_withdrawOnline");
		return modelAndView ;
	}
	
	
	
	
	
	
}
