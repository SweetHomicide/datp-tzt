package com.ruizton.main.controller.front;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.omg.CORBA.COMM_FAILURE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.ruizton.main.Enum.BankTypeEnum;
import com.ruizton.main.Enum.VirtualCoinTypeStatusEnum;
import com.ruizton.main.controller.BaseController;
import com.ruizton.main.model.FbankinfoWithdraw;
import com.ruizton.main.model.Fuser;
import com.ruizton.main.model.FvirtualaddressWithdraw;
import com.ruizton.main.model.Fvirtualcointype;
import com.ruizton.main.model.Fvirtualwallet;
import com.ruizton.main.service.admin.VirtualCoinService;
import com.ruizton.main.service.front.FrontUserService;
import com.ruizton.main.service.front.FrontVirtualCoinService;
import com.ruizton.util.Comm;
import com.ruizton.util.Utils;

import net.sf.json.JSONObject;

@Controller
public class FrontFinancialController extends BaseController {

	@Autowired
	private FrontUserService frontUserService ;
	@Autowired
	private FrontVirtualCoinService frontVirtualCoinService ;
	@Autowired
	private VirtualCoinService virtualCoinService;
	
	@RequestMapping("/financial/index")
	public ModelAndView index(
			HttpServletRequest request,
			@RequestParam(required=false,defaultValue="1")int currentPage
			) throws Exception{
		int firstResult = (currentPage-1)*Comm.getMAXRESULT();
		ModelAndView modelAndView = new ModelAndView() ;
		Fuser fuser = this.frontUserService.findById(GetSession(request).getFid()) ;
		List<Fvirtualwallet> fvirtualwallet = this.frontUserService.findVirtualWallet(GetSession(request).getFid(), firstResult, Comm.getMAXRESULT(), true) ;
		String sql = "select count(*) from Fvirtualwallet where fuser.fid='"+GetSession(request).getFid()+"' and fvirtualcointype.fstatus=1";
		int count = this.frontVirtualCoinService.findCount(sql);
		String methods = "ajaxPersonalAssets";
		String pagin = this.generatePaginX(count/Comm.getMAXRESULT()+( (count%Comm.getMAXRESULT())==0?0:1), Integer.valueOf(currentPage), methods) ;
		modelAndView.addObject("count", count);
		modelAndView.addObject("pagin", pagin);
		modelAndView.addObject("fvirtualwallet", fvirtualwallet);
		modelAndView.addObject("fuser", fuser) ;
		modelAndView.setViewName("front/financial/index") ;
		return modelAndView ;
	}
	/*
	 * author :liu wenjie
	 * date :2017年3月22日14:51:01
	 * function : 财务中心 个人资产分页查询
	 */
	@ResponseBody
	@RequestMapping(value="/financial/index1",produces={JsonEncode})
	public String index1(
			HttpServletRequest request,
			@RequestParam(required=false,defaultValue="1")int currentPage,
			@RequestParam(required=false,defaultValue="0")int count,
			@RequestParam(required=false,defaultValue="")String keyWord
			){
		List<Fvirtualwallet> fvirtualwallet;
		String pagin = "";
		JSONObject js = new JSONObject();
		int firstResult = (currentPage-1)*Comm.getMAXRESULT();
		
		//通过session 查询个人用户信息
		//Fuser fuser = this.frontUserService.findById(GetSession(request).getFid()) ;
		//根据keyword查询条件是否为空去执行查询
		if(keyWord!=null && !"".equals(keyWord)){
			fvirtualwallet = this.frontUserService.findVirtualWallet(GetSession(request).getFid(),keyWord, firstResult, Comm.getMAXRESULT(), true) ;
		    String sql = "select count(*) from Fvirtualwallet "
		    		    + "where fuser.fid='"+GetSession(request).getFid()+"' and "
		    			+ "fvirtualcointype.fstatus="+ VirtualCoinTypeStatusEnum.Normal+" and"
		    			+ " fvirtualcointype.fname  like '%"+keyWord+"%'";
		    //根据模糊查询条件去查询符合条件的总数
		    count = this.frontUserService.findVirtualWalletCount(sql);
		}else{
			fvirtualwallet = this.frontUserService.findVirtualWallet(GetSession(request).getFid(), firstResult, Comm.getMAXRESULT(), true) ;
		}
		//调用的ajax方法
		String methods = "ajaxPersonalAssets";
		//执行分页标签
		pagin = this.generatePaginX(count/Comm.getMAXRESULT()+( (count%Comm.getMAXRESULT())==0?0:1), Integer.valueOf(currentPage), methods) ;
		for (Fvirtualwallet fvirtualwallet2 : fvirtualwallet) {
			//创建一个json实体类将查询的的fvirtualwallet对象遍历放入json实体类
			JSONObject js1 = new JSONObject();
			js1.accumulate("fcoinname", fvirtualwallet2.getFvirtualcointype().getFname());
			js1.accumulate("ftotal", Utils.getDouble(fvirtualwallet2.getFtotal(), 4));
			js1.accumulate("frozen", Utils.getDouble(fvirtualwallet2.getFfrozen(), 4));
			js1.accumulate("ftotalandfrozen", Utils.getDouble(fvirtualwallet2.getFtotal()+fvirtualwallet2.getFfrozen(), 4));
			//对fvirtualwallet的长度作判断用于拼接json
			if(fvirtualwallet.size()==1){
				js.accumulate("result", "["+js1+"]");
			}else{
				js.accumulate("result", js1);
			}
		}
		
		//将查询到的数据放入json实体类
		js.accumulate("count", count);
		js.accumulate("pagin", pagin);
		//js.accumulate("fvirtualwallet", fvirtualwallet);
		//js.accumulate("fuser", fuser);
		return js.toString();
	}
	
	@RequestMapping("/financial/accountbank")
	public ModelAndView accountbank(
			HttpServletRequest request
			) throws Exception{
		ModelAndView modelAndView = new ModelAndView() ;
		Fuser fuser = this.frontUserService.findById(GetSession(request).getFid()) ;
		modelAndView.addObject("fuser", fuser) ;
		
		Map<Integer,String> bankTypes = new HashMap<Integer,String>() ;
		for(int i=1;i<=BankTypeEnum.QT;i++){
			if(BankTypeEnum.getEnumString(i) != null && BankTypeEnum.getEnumString(i).trim().length() >0){
				bankTypes.put(i,BankTypeEnum.getEnumString(i)) ;
			}			
		}
		modelAndView.addObject("bankTypes", bankTypes) ;
		
		String filter = "where fuser.fid='"+fuser.getFid()+"' and fbankType >0";
		List<FbankinfoWithdraw> bankinfos = this.frontUserService.findFbankinfoWithdrawByFuser(0, 0, filter, false);
		for (FbankinfoWithdraw fbankinfoWithdraw : bankinfos) {
			try {
				int length = fbankinfoWithdraw.getFbankNumber().length();
				String number = "**** **** **** "+fbankinfoWithdraw.getFbankNumber().substring(length-4,length);
				fbankinfoWithdraw.setFbankNumber(number);
			} catch (Exception e) {}
		}
		modelAndView.addObject("bankinfos", bankinfos) ;
		
		boolean isBindGoogle = fuser.getFgoogleBind() ;
		boolean isBindTelephone = fuser.isFisTelephoneBind() ;
		modelAndView.addObject("isBindGoogle", isBindGoogle) ;
        modelAndView.addObject("isBindTelephone", isBindTelephone) ;
		
		modelAndView.setViewName("front/financial/accountbank") ;
		return modelAndView ;
	}
/*	
	@RequestMapping("/financial/accountalipay")
	public ModelAndView accountalipay(
			HttpServletRequest request
			) throws Exception{
		ModelAndView modelAndView = new ModelAndView() ;
		Fuser fuser = this.frontUserService.findById(GetSession(request).getFid()) ;
		modelAndView.addObject("fuser", fuser) ;
		
		String filter = "where fuser.fid='"+fuser.getFid()+"' and fbankType =0";
		List<FbankinfoWithdraw> bankinfos = this.frontUserService.findFbankinfoWithdrawByFuser(0, 0, filter, false);
		for (FbankinfoWithdraw fbankinfoWithdraw : bankinfos) {
			try {
				int length = fbankinfoWithdraw.getFbankNumber().length();
				String number = fbankinfoWithdraw.getFbankNumber().substring(0,3)+"****"+fbankinfoWithdraw.getFbankNumber().substring(length-4,length);
				fbankinfoWithdraw.setFbankNumber(number);
			} catch (Exception e) {}
		}
		modelAndView.addObject("bankinfos", bankinfos) ;
		
		boolean isBindGoogle = fuser.getFgoogleBind() ;
		boolean isBindTelephone = fuser.isFisTelephoneBind() ;
		modelAndView.addObject("isBindGoogle", isBindGoogle) ;
        modelAndView.addObject("isBindTelephone", isBindTelephone) ;
		
		
		modelAndView.setViewName("front/financial/accountalipay") ;
		return modelAndView ;
	}*/
	@RequestMapping("/financial/accountcointype")
	public ModelAndView accountcointype(
			HttpServletRequest request,
			@RequestParam(required=false,defaultValue="1")String currentPage,
			@RequestParam(required=false,defaultValue="1")String currentPage1,
			@RequestParam(required=false,defaultValue="0")String symbol,
			@RequestParam(required=false,defaultValue="")String hidlog,
			@RequestParam(required=false,defaultValue="")String searchname
			){
		String fid = GetSession(request).getFid();
		ModelAndView modelAndView = new ModelAndView() ;
		Fvirtualcointype fvirtualcointype = this.frontVirtualCoinService.findFvirtualCoinById(symbol) ;
		if(fvirtualcointype==null ||fvirtualcointype.getFstatus()==VirtualCoinTypeStatusEnum.Abnormal
				 || !fvirtualcointype.isFIsWithDraw()){
			String filter = "where fstatus=1 order by fid asc";
			List<Fvirtualcointype> alls = this.virtualCoinService.list(0, 1, filter, true);
			if(alls==null || alls.size() ==0){
				modelAndView.setViewName("redirect:/") ;
				return modelAndView ;
			}
			fvirtualcointype = alls.get(0);
			symbol = fvirtualcointype.getFid();
		}
		int pageSize = Comm.getPAGE_NUM();
		int count = 0;
		String sql;
		List<Fvirtualcointype> fList;
		int limitNum = (Integer.valueOf(currentPage1)-1)*pageSize;
		if ("".equals(hidlog) || hidlog.equals(null)) {

			fList = this.frontVirtualCoinService.findByParam(limitNum, pageSize);

			sql = "select count(*) from Fvirtualcointype where fstatus=1";
		} else {
			String filter = " where fstatus=1 and fname like '%" + hidlog + "%'";
			String className = "Fvirtualcointype";
			fList = this.frontVirtualCoinService.findByParam1(limitNum, pageSize, filter, true, className);
			sql = "select count(*) from Fvirtualcointype where fname like '%" + hidlog + "%'";

		}
		
		fList = this.frontVirtualCoinService.findByParam(limitNum, pageSize);
		count = this.frontVirtualCoinService.findCount(sql);
		
		String pagin1 = this.generatePagin4(count/pageSize+( (count%pageSize)==0?0:1), Integer.valueOf(currentPage1)) ;
		//String filter = "where fuser.fid='"+fid+"' and fvirtualcointype.fid='"+symbol+"'";
		StringBuffer filter1 =new StringBuffer();
		filter1.append("where fuser.fid='"+fid+"' and fvirtualcointype.fid='"+symbol+"' \n");
		if(searchname!=null){
			filter1.append(" and fremark like '%"+searchname+"%' \n");
		}
		List<FvirtualaddressWithdraw> allsByfilter = this.frontVirtualCoinService.findFvirtualaddressWithdraws(0, 0, filter1.toString(), false);
		int addCount = allsByfilter.size();
		String methods = "searchAccount";
		String pagin = this.generatePaginX(addCount/Comm.getFINANCIAL_NUM()+((addCount%Comm.getFINANCIAL_NUM())==0?0:1), Integer.valueOf(currentPage), methods );
		
		int beginNum = (Integer.valueOf(currentPage)-1)*Comm.getFINANCIAL_NUM();
		List<FvirtualaddressWithdraw> alls = this.frontVirtualCoinService.findFvirtualaddressWithdraws(beginNum, Comm.getFINANCIAL_NUM(), filter1.toString(), true);
		modelAndView.addObject("searchname", searchname);
		modelAndView.addObject("currentPage", currentPage);
		modelAndView.addObject("pagin", pagin);
		modelAndView.addObject("fvirtualcointype", fvirtualcointype) ;
		modelAndView.addObject("alls", alls) ;
		modelAndView.addObject("fList", fList);
		modelAndView.addObject("page", pagin1);
		modelAndView.setViewName("front/financial/accountcoin");
		return modelAndView;
	}
	@ResponseBody
	@RequestMapping("/financial/accountcointype1")
	public String accountcointype1(
			HttpServletRequest request,
			@RequestParam(required=false,defaultValue="1")String currentPage1,
			@RequestParam(required=false,defaultValue="0")String symbol,
			@RequestParam(required=false,defaultValue="")String hidlog
			){
		JSONObject js = new JSONObject();
		String result ="";
		String fid = GetSession(request).getFid();	
		int pageSize = Comm.getPAGE_NUM();
		int count = 0;
		String sql;
		List<Fvirtualcointype> fList;
		int limitNum = (Integer.valueOf(currentPage1)-1)*pageSize;
		if ("".equals(hidlog) || hidlog.equals(null)) {

			fList = this.frontVirtualCoinService.findByParam(limitNum, pageSize);

			sql = "select count(*) from Fvirtualcointype where fstatus=1";
		} else {
			String filter = " where fstatus=1 and fname like '%" + hidlog + "%'";
			String className = "Fvirtualcointype";
			fList = this.frontVirtualCoinService.findByParam1(limitNum, pageSize, filter, true, className);
			sql = "select count(*) from Fvirtualcointype where fstatus=1 and fname like '%" + hidlog + "%'";

		}
		
		count = this.frontVirtualCoinService.findCount(sql);
		
		String pagin1 = this.generatePagin4(count/pageSize+( (count%pageSize)==0?0:1), Integer.valueOf(currentPage1)) ;
		String filter = "where fuser.fid='"+fid+"' and fvirtualcointype.fid='"+symbol+"'";
		
		
		for (Fvirtualcointype fvirtualcointype : fList) {

			String fa = "<div class='col-xs-4' style='line-height:40px;'><a href='/financial/accountcointype.html?symbol="
					+ fvirtualcointype.getFid() + "'"
					+ "><i class='lefticon col-xs-2' style='top:5px;margin-right:5px;width:20px;height:30px;background-size:100%;background-image: url("
					+ fvirtualcointype.getFurl() + ")'></i><span>&nbsp;" + fvirtualcointype.getFname()
					+ "</span></a></div>";

			result = result + fa;
		}
		String pagin = this.generatePagin4(count / pageSize + ((count % pageSize) == 0 ? 0 : 1),Integer.valueOf(currentPage1));
		js.accumulate("page", pagin);
		js.accumulate("result", result);
		return js.toString();
	}
	/*@RequestMapping("/financial/accountcoin")
	public ModelAndView accountcoin(
			HttpServletRequest request,
			@RequestParam(required=false,defaultValue="1")String symbol
			) throws Exception{
		ModelAndView modelAndView = new ModelAndView() ;
		Fuser fuser = this.frontUserService.findById(GetSession(request).getFid()) ;
		
		Fvirtualcointype fvirtualcointype = this.frontVirtualCoinService.findFvirtualCoinById(symbol) ;
		if(fvirtualcointype==null ||fvirtualcointype.getFstatus()==VirtualCoinTypeStatusEnum.Abnormal
				 || !fvirtualcointype.isFIsWithDraw()){
			String filter = "where fstatus=1 and FIsWithDraw=1 order by fid asc";
			List<Fvirtualcointype> alls = this.virtualCoinService.list(0, 1, filter, true);
			if(alls==null || alls.size() ==0){
				modelAndView.setViewName("redirect:/") ;
				return modelAndView ;
			}
			fvirtualcointype = alls.get(0);
		}
		symbol = fvirtualcointype.getFid();
		String coinName = fvirtualcointype.getfShortName();
		
		String filter = "where fuser.fid='"+fuser.getFid()+"' and fvirtualcointype.fid='"+symbol+"'";
		List<FvirtualaddressWithdraw> alls = this.frontVirtualCoinService.findFvirtualaddressWithdraws(0, 0, filter, false);
		modelAndView.addObject("alls", alls) ;
		
		boolean isBindGoogle = fuser.getFgoogleBind() ;
		boolean isBindTelephone = fuser.isFisTelephoneBind() ;
		modelAndView.addObject("isBindGoogle", isBindGoogle) ;
        modelAndView.addObject("isBindTelephone", isBindTelephone) ;
		
		modelAndView.addObject("fuser", fuser) ;
		modelAndView.addObject("symbol", symbol) ;
		modelAndView.addObject("coinName", coinName) ;
		modelAndView.setViewName("front/financial/accountcoin") ;
		return modelAndView ;
	}*/
}
