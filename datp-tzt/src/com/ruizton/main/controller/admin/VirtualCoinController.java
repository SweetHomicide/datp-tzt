package com.ruizton.main.controller.admin;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.ruizton.main.Enum.CoinTypeEnum;
import com.ruizton.main.Enum.VirtualCoinTypeStatusEnum;
import com.ruizton.main.auto.AutoDealMaking;
import com.ruizton.main.auto.KlinePeriodData;
import com.ruizton.main.comm.ConstantMap;
import com.ruizton.main.comm.KeyValues;
import com.ruizton.main.controller.BaseController;
import com.ruizton.main.model.BTCMessage;
import com.ruizton.main.model.Fabout;
import com.ruizton.main.model.Ffees;
import com.ruizton.main.model.Fpool;
import com.ruizton.main.model.Fvirtualcointype;
import com.ruizton.main.model.Fwithdrawfees;
import com.ruizton.main.service.admin.AboutService;
import com.ruizton.main.service.admin.AdminService;
import com.ruizton.main.service.admin.FeeService;
import com.ruizton.main.service.admin.PoolService;
import com.ruizton.main.service.admin.VirtualCoinService;
import com.ruizton.main.service.admin.WithdrawFeesService;
import com.ruizton.main.service.front.FrontVirtualCoinService;
import com.ruizton.util.BTCUtils;
import com.ruizton.util.Comm;
import com.ruizton.util.Constant;
import com.ruizton.util.Utils;

@Controller
public class VirtualCoinController extends BaseController {
	@Autowired
	private VirtualCoinService virtualCoinService ;
	@Autowired
	private WithdrawFeesService withdrawFeesService ;
	@Autowired
	private AdminService adminService ;
	@Autowired
	private FeeService feeService;
	@Autowired
	private PoolService poolService;
	@Autowired
	private HttpServletRequest request ;
	@Autowired
	private FrontVirtualCoinService frontVirtualCoinService ;
	@Autowired
	private ConstantMap map;
	@Autowired
	private AboutService aboutService;
	@Autowired
	private KlinePeriodData klinePeriodData;
	
	@Autowired
	private AutoDealMaking autoDealMaking;
	
	//每页显示多少条数据
	private int numPerPage = Utils.getNumPerPage();
	
	@RequestMapping("/ssadmin/virtualCoinTypeList")
	public ModelAndView Index() throws Exception{
		
		ModelAndView modelAndView = new ModelAndView() ;
		modelAndView.setViewName("ssadmin/virtualCoinTypeList") ;
		//当前页
		int currentPage = 1;
		//搜索关键字
		String keyWord = request.getParameter("keywords");
		String orderField = request.getParameter("orderField");
		String orderDirection = request.getParameter("orderDirection");
		
		if(request.getParameter("pageNum") != null){
			currentPage = Integer.parseInt(request.getParameter("pageNum"));
		}
		StringBuffer filter = new StringBuffer();
		filter.append("where 1=1 \n");
		if(keyWord != null && keyWord.trim().length() >0){
			filter.append("and (fname like '%"+keyWord+"%' OR \n");
			filter.append("fShortName like '%"+keyWord+"%' OR \n");
			filter.append("fdescription like '%"+keyWord+"%' )\n");
			modelAndView.addObject("keywords", keyWord);
		}
		if(orderField != null && orderField.trim().length() >0){
			filter.append("order by "+orderField+"\n");
		}else{
			filter.append("order by faddTime \n");
		}
		if(orderDirection != null && orderDirection.trim().length() >0){
			filter.append(orderDirection+"\n");
		}else{
			filter.append("desc \n");
		}
		List<Fvirtualcointype> list = this.virtualCoinService.list((currentPage-1)*numPerPage, numPerPage, filter+"",true);
		modelAndView.addObject("virtualCoinTypeList", list);
		modelAndView.addObject("numPerPage", numPerPage);
		modelAndView.addObject("rel", "virtualCoinTypeList");
		modelAndView.addObject("currentPage", currentPage);
		//总数量
		modelAndView.addObject("totalCount",this.adminService.getAllCount("Fvirtualcointype", filter+""));
		return modelAndView ;
	}
	
	@RequestMapping("/ssadmin/walletAddressList")
	public ModelAndView walletAddressList() throws Exception{
		ModelAndView modelAndView = new ModelAndView() ;
		modelAndView.setViewName("ssadmin/walletAddressList") ;
		//当前页
		int currentPage = 1;
		//搜索关键字
		String keyWord = request.getParameter("keywords");
		
		if(request.getParameter("pageNum") != null){
			currentPage = Integer.parseInt(request.getParameter("pageNum"));
		}
		StringBuffer filter = new StringBuffer();
		filter.append("where 1=1 \n");
		if(keyWord != null && keyWord.trim().length() >0){
			filter.append("and b.fname like '%"+keyWord+"%'\n");
			modelAndView.addObject("keywords", keyWord);
		}
		filter.append("and (a.fstatus=0 or a.fstatus is null)\n");
		
		List list = this.poolService.list((currentPage-1)*numPerPage, numPerPage, filter+"",true);
		modelAndView.addObject("walletAddressList", list);
		modelAndView.addObject("numPerPage", numPerPage);
		modelAndView.addObject("rel", "walletAddressList");
		modelAndView.addObject("currentPage", currentPage);
		//总数量
		modelAndView.addObject("totalCount",this.poolService.list((currentPage-1)*numPerPage, numPerPage,filter+"",false).size());
		return modelAndView ;
	}
	
	
	@RequestMapping("ssadmin/goVirtualCoinTypeJSP")
	public ModelAndView goVirtualCoinTypeJSP() throws Exception{
		String url = request.getParameter("url");
		ModelAndView modelAndView = new ModelAndView() ;
		modelAndView.setViewName(url) ;
		if(request.getParameter("uid") != null){
			String fid = request.getParameter("uid");
			Fvirtualcointype virtualCoinType = this.virtualCoinService.findById(fid);
			modelAndView.addObject("virtualCoinType", virtualCoinType);
			
			String filter = "where fvirtualcointype.fid='"+fid+"' order by flevel asc";
			List<Ffees> allFees = this.feeService.list(0, 0, filter, false);
			modelAndView.addObject("allFees", allFees);
		}
		
		Map<Integer, String> typeMap = new HashMap<Integer, String>();
		for(int i=1;i<=1;i++){
			typeMap.put(i, CoinTypeEnum.getEnumString(i));
		}
		modelAndView.addObject("typeMap", typeMap);
		
		return modelAndView;
	}
	
	@RequestMapping("ssadmin/saveVirtualCoinType")
	public ModelAndView saveVirtualCoinType(
			@RequestParam(required=false) MultipartFile filedata,
			@RequestParam(required=false) String fdescription,
			@RequestParam(required=false) String fname,
			@RequestParam(required=false) String fShortName,
			@RequestParam(required=false) String faccess_key,
			@RequestParam(required=false) String fsecrt_key,
			@RequestParam(required=false) String fip,
			@RequestParam(required=false) String fport,
			@RequestParam(required=false) String fSymbol,
			@RequestParam(required=false) String FIsWithDraw,
			@RequestParam(required=false) String fisShare,
			@RequestParam(required=false) int fcount,
			@RequestParam(required=false) int ftype,
			@RequestParam(required=false) double fprice,
			@RequestParam(required=true) String ftradetime,
			@RequestParam(required=false) String fweburl,
			@RequestParam(required=false) String fisDefAsset
			) throws Exception{
		ModelAndView modelAndView = new ModelAndView() ;
		modelAndView.setViewName("ssadmin/comm/ajaxDone") ;
		Fvirtualcointype virtualCoinType = new Fvirtualcointype();
		virtualCoinType.setFprice(fprice);
		virtualCoinType.setFcount(fcount);
		String fpictureUrl = "";
		boolean isTrue = false;
		 if(filedata != null && !filedata.isEmpty()){
			InputStream inputStream = filedata.getInputStream() ;
			String fileRealName = filedata.getOriginalFilename() ;
			if(fileRealName != null && fileRealName.trim().length() >0){
				String[] nameSplit = fileRealName.split("\\.") ;
				String ext = nameSplit[nameSplit.length-1] ;
				if(ext!=null 
				 && !ext.trim().toLowerCase().endsWith("jpg")
				 && !ext.trim().toLowerCase().endsWith("png")){
					modelAndView.addObject("statusCode",300);
					modelAndView.addObject("message","非jpg、png文件格式");
					return modelAndView;
				}
				String realPath = request.getSession().getServletContext().getRealPath("/")+Constant.AdminSYSDirectory;
				String fileName = Utils.getRandomImageName()+"."+ext;
				boolean flag = Utils.saveFile(realPath,fileName, inputStream) ;
				if(flag){
					fpictureUrl = "/"+Constant.AdminSYSDirectory+"/"+fileName ;
					isTrue = true;
				}
			}
		}
		if(isTrue){
			virtualCoinType.setFurl(fpictureUrl);
		}
		virtualCoinType.setFtradetime(ftradetime);
		virtualCoinType.setFaddTime(Utils.getTimestamp());
		virtualCoinType.setFdescription(fdescription);
		virtualCoinType.setFname(fname);
		virtualCoinType.setfShortName(fShortName);
		virtualCoinType.setFstatus(VirtualCoinTypeStatusEnum.Abnormal);
		virtualCoinType.setFaccess_key(faccess_key);
		virtualCoinType.setFsecrt_key(fsecrt_key);
		virtualCoinType.setFip(fip);
		virtualCoinType.setFtype(ftype);
		virtualCoinType.setFport(fport);
		virtualCoinType.setfSymbol(fSymbol);
		if(FIsWithDraw != null && FIsWithDraw.trim().length() >0){
			virtualCoinType.setFIsWithDraw(true);
		}else{
			virtualCoinType.setFIsWithDraw(false);
		}
		if(fisShare != null && fisShare.trim().length() >0){
			virtualCoinType.setFisShare(true);
		}else{
			virtualCoinType.setFisShare(false);
		}
		Fabout about = new Fabout();
		about.setFcontent(".");
		about.setFtitle(virtualCoinType.getFname());
		about.setFtype(Comm.getFTYPE());
		this.aboutService.saveObj(about);
		virtualCoinType.setFweburl(String.valueOf(about.getFid()));
		this.virtualCoinService.saveObj(virtualCoinType);
		
		List<Fvirtualcointype> fvirtualcointypes= this.frontVirtualCoinService.findFvirtualCoinType(VirtualCoinTypeStatusEnum.Normal) ;
		map.put("virtualCoinType", fvirtualcointypes) ;
		
		String xx = "where fstatus=1 and FIsWithDraw=1";
		List<Fvirtualcointype> allWithdrawCoins= this.virtualCoinService.list(0, 0, xx, false);
		map.put("allWithdrawCoins", allWithdrawCoins) ;
		
//		{
//			List<KeyValues> coins = new ArrayList<KeyValues>() ;
//			for (int i=1;i<=5;i++) {
//				KeyValues keyValues = new KeyValues() ;
//				keyValues.setKey(i);
//				keyValues.setValue(this.virtualCoinService.list(0, 0, "where fstatus=1 and fisshare=1 and ftype="+i, false));
//				coins.add(keyValues);
//			}
//			map.put("indexCoins", coins) ;
//		}
		
		List<Fwithdrawfees> allWithDrawFees = withdrawFeesService.findAll();
		for (Fwithdrawfees fwithdrawfees : allWithDrawFees) {
			Ffees fees = new Ffees();
			fees.setWithdraw(0);
			fees.setFfee(0);
			fees.setFbuyfee(0);
			fees.setFlevel(fwithdrawfees.getFlevel());
			fees.setFvirtualcointype(virtualCoinType);
			feeService.saveObj(fees);
		}

		modelAndView.addObject("statusCode",200);
		modelAndView.addObject("message","新增成功");
		modelAndView.addObject("callbackType","closeCurrent");
		return modelAndView;
	}
	
	@RequestMapping("ssadmin/updateVirtualCoinType")
	public ModelAndView updateVirtualCoinType(
			@RequestParam(required=false) MultipartFile filedata,
			@RequestParam(required=false) String fdescription,
			@RequestParam(required=false) String fname,
			@RequestParam(required=false) String fShortName,
			@RequestParam(required=false) String faccess_key,
			@RequestParam(required=false) String fsecrt_key,
			@RequestParam(required=false) String fip,
			@RequestParam(required=false) String fport,
			@RequestParam(required=false) String fSymbol,
			@RequestParam(required=false) String FIsWithDraw,
			@RequestParam(required=false) String fisShare,
			@RequestParam(required=false) String fid,
			@RequestParam(required=false) int ftype,
			@RequestParam(required=false) int fcount,
			@RequestParam(required=false) double fprice,
			@RequestParam(required=true) String ftradetime,
			@RequestParam(required=false) String fweburl,
			@RequestParam(required=false) String fisDefAsset
			) throws Exception{
		ModelAndView modelAndView = new ModelAndView() ;
		modelAndView.setViewName("ssadmin/comm/ajaxDone") ;
		Fvirtualcointype virtualCoinType = this.virtualCoinService.findById(fid);
		virtualCoinType.setFprice(fprice);
		virtualCoinType.setFtradetime(ftradetime);
		virtualCoinType.setFcount(fcount);
		
		String fpictureUrl = "";
		boolean isTrue = false;
		 if(filedata != null && !filedata.isEmpty()){
			InputStream inputStream = filedata.getInputStream() ;
			String fileRealName = filedata.getOriginalFilename() ;
			if(fileRealName != null && fileRealName.trim().length() >0){
				String[] nameSplit = fileRealName.split("\\.") ;
				String ext = nameSplit[nameSplit.length-1] ;
				if(ext!=null 
				 && !ext.trim().toLowerCase().endsWith("jpg")
				 && !ext.trim().toLowerCase().endsWith("png")){
					modelAndView.addObject("statusCode",300);
					modelAndView.addObject("message","非jpg、png文件格式");
					return modelAndView;
				}
				String realPath = request.getSession().getServletContext().getRealPath("/")+Constant.AdminSYSDirectory;
				String fileName = Utils.getRandomImageName()+"."+ext;
				boolean flag = Utils.saveFile(realPath,fileName, inputStream) ;
				if(flag){
					fpictureUrl = "/"+Constant.AdminSYSDirectory+"/"+fileName ;
					isTrue = true;
				}
			}
		}
		if(isTrue){
			virtualCoinType.setFurl(fpictureUrl);
		}
//		virtualCoinType.setFweburl(fweburl);
		virtualCoinType.setFaddTime(Utils.getTimestamp());
		virtualCoinType.setFdescription(fdescription);
		virtualCoinType.setFname(fname);
		virtualCoinType.setfShortName(fShortName);
		virtualCoinType.setFaccess_key(faccess_key);
		virtualCoinType.setFsecrt_key(fsecrt_key);
		virtualCoinType.setFip(fip);
		virtualCoinType.setFport(fport);
		virtualCoinType.setFtype(ftype);
		virtualCoinType.setfSymbol(fSymbol);
		if(FIsWithDraw != null && FIsWithDraw.trim().length() >0){
			virtualCoinType.setFIsWithDraw(true);
		}else{
			virtualCoinType.setFIsWithDraw(false);
		}
		if(fisShare != null && fisShare.trim().length() >0){
			virtualCoinType.setFisShare(true);
		}else{
			virtualCoinType.setFisShare(false);
		}
		if(fisDefAsset != null && fisDefAsset.trim().length() >0){
			virtualCoinType.setFisDefAsset(true);
		}else{
			virtualCoinType.setFisDefAsset(false);
		}
		
		this.virtualCoinService.updateObj(virtualCoinType);
		
		List<Fvirtualcointype> fvirtualcointypes= this.frontVirtualCoinService.findFvirtualCoinType(VirtualCoinTypeStatusEnum.Normal) ;
		map.put("virtualCoinType", fvirtualcointypes) ;
		
		String xx = "where fstatus=1 and FIsWithDraw=1";
		List<Fvirtualcointype> allWithdrawCoins= this.virtualCoinService.list(0, 0, xx, false);
		map.put("allWithdrawCoins", allWithdrawCoins) ;
		
//		{
//			List<KeyValues> coins = new ArrayList<KeyValues>() ;
//			for (int i=1;i<=5;i++) {
//				KeyValues keyValues = new KeyValues() ;
//				keyValues.setKey(i);
//				keyValues.setValue(this.virtualCoinService.list(0, 0, "where fstatus=1 and fisshare=1 and ftype="+i, false));
//				coins.add(keyValues);
//			}
//			map.put("indexCoins", coins) ;
//		}
		
		List<Fwithdrawfees> allWithDrawFees = withdrawFeesService.findAll();
		for (Fwithdrawfees fwithdrawfees : allWithDrawFees) {
		    String filter = "where flevel="+fwithdrawfees.getFlevel()+" and fvirtualcointype.fid='"+fid+"'";
			List<Ffees> feesList = this.feeService.list(0, 0, filter, false);
			if(feesList == null || feesList.size() == 0){
				Ffees fees = new Ffees();
				fees.setWithdraw(0);
				fees.setFfee(0);
				fees.setFbuyfee(0);
				fees.setFlevel(fwithdrawfees.getFlevel());
				fees.setFvirtualcointype(virtualCoinType);
				feeService.saveObj(fees);
			}
		}
		
		modelAndView.addObject("statusCode",200);
		modelAndView.addObject("message","更新成功");
		modelAndView.addObject("callbackType","closeCurrent");
		return modelAndView;
	}
	
	@RequestMapping("ssadmin/goWallet")
	public ModelAndView goWallet() throws Exception{
		ModelAndView modelAndView = new ModelAndView() ;
		String fid = request.getParameter("uid");
		String password = request.getParameter("passWord");
		Fvirtualcointype virtualcointype = this.virtualCoinService.findById(fid);
		modelAndView.setViewName("ssadmin/comm/ajaxDone") ;
		boolean flag = false;
		virtualcointype.setFstatus(VirtualCoinTypeStatusEnum.Normal);
		String msg = "";
		try {
			flag = this.virtualCoinService.updateCoinType(virtualcointype,password);
			flag = true;
		} catch (Exception e) {
			 msg =e.getMessage();
		}
		
		List<Fvirtualcointype> fvirtualcointypes= this.frontVirtualCoinService.findFvirtualCoinType(VirtualCoinTypeStatusEnum.Normal) ;
		map.put("virtualCoinType", fvirtualcointypes) ;
		
		String xx = "where fstatus=1 and FIsWithDraw=1";
		List<Fvirtualcointype> allWithdrawCoins= this.virtualCoinService.list(0, 0, xx, false);
		map.put("allWithdrawCoins", allWithdrawCoins) ;
		
//		{
//			List<KeyValues> coins = new ArrayList<KeyValues>() ;
//			for (int i=1;i<=5;i++) {
//				KeyValues keyValues = new KeyValues() ;
//				keyValues.setKey(i);
//				keyValues.setValue(this.virtualCoinService.list(0, 0, "where fstatus=1 and fisshare=1 and ftype="+i, false));
//				coins.add(keyValues);
//			}
//			map.put("indexCoins", coins) ;
//		}
		
		if(!flag){
			modelAndView.addObject("message",msg);
			modelAndView.addObject("statusCode",300);
		}else{
			modelAndView.addObject("message","启用成功");
			modelAndView.addObject("statusCode",200);
			modelAndView.addObject("callbackType","closeCurrent");
			this.klinePeriodData.init();
			this.autoDealMaking.autoDealMakingThread(virtualcointype);
		}
		return modelAndView;
	}
	
	@RequestMapping("ssadmin/deleteVirtualCoinType")
	public ModelAndView deleteVirtualCoinType() throws Exception{
		ModelAndView modelAndView = new ModelAndView() ;
		String fid = request.getParameter("uid");
		Fvirtualcointype virtualcointype = this.virtualCoinService.findById(fid);
		int status = Integer.parseInt(request.getParameter("status"));
		modelAndView.setViewName("ssadmin/comm/ajaxDone") ;
		boolean flag = false;
		
		if(status == 1){//禁用
			virtualcointype.setFstatus(VirtualCoinTypeStatusEnum.Abnormal);
			this.virtualCoinService.updateObj(virtualcointype);
			flag = true;
		}
		
		List<Fvirtualcointype> fvirtualcointypes= this.frontVirtualCoinService.findFvirtualCoinType(VirtualCoinTypeStatusEnum.Normal) ;
		map.put("virtualCoinType", fvirtualcointypes) ;
		
		String xx = "where fstatus=1 and FIsWithDraw=1";
		List<Fvirtualcointype> allWithdrawCoins= this.virtualCoinService.list(0, 0, xx, false);
		map.put("allWithdrawCoins", allWithdrawCoins) ;
		
//		{
//			List<KeyValues> coins = new ArrayList<KeyValues>() ;
//			for (int i=1;i<=5;i++) {
//				KeyValues keyValues = new KeyValues() ;
//				keyValues.setKey(i);
//				keyValues.setValue(this.virtualCoinService.list(0, 0, "where fstatus=1 and fisshare=1 and ftype="+i, false));
//				coins.add(keyValues);
//			}
//			map.put("indexCoins", coins) ;
//		}
		
		if(!flag){
			modelAndView.addObject("message","操作失败");
			modelAndView.addObject("statusCode",300);
		}else if(flag && status == 1){
			modelAndView.addObject("message","禁用成功");
			modelAndView.addObject("statusCode",200);
		}else{
			modelAndView.addObject("message","启用成功");
			modelAndView.addObject("statusCode",200);
		}
		return modelAndView;
	}
	
	@RequestMapping("ssadmin/testWallet")
	public ModelAndView testWallet() throws Exception{
		ModelAndView modelAndView = new ModelAndView() ;
		modelAndView.setViewName("ssadmin/comm/ajaxDone") ;
		String fid = request.getParameter("uid");
		Fvirtualcointype type = this.virtualCoinService.findById(fid);
		BTCMessage btcMessage = new BTCMessage() ;
		btcMessage.setACCESS_KEY(type.getFaccess_key()) ;
		btcMessage.setIP(type.getFip()) ;
		btcMessage.setPORT(type.getFport()) ;
		btcMessage.setSECRET_KEY(type.getFsecrt_key()) ;
		if(btcMessage.getACCESS_KEY()==null
				||btcMessage.getIP()==null
				||btcMessage.getPORT()==null
				||btcMessage.getSECRET_KEY()==null){
			modelAndView.addObject("message","钱包连接失败，请检查配置信息");
			modelAndView.addObject("statusCode",300);
		}
		try {
			BTCUtils btcUtils = new BTCUtils(btcMessage) ;
			double balance = btcUtils.getbalanceValue();
			modelAndView.addObject("message","测试成功，钱包余额:"+balance);
			modelAndView.addObject("statusCode",200);
		} catch (Exception e) {
			modelAndView.addObject("message","钱包连接失败，请检查配置信息");
			modelAndView.addObject("statusCode",300);
		}
		return modelAndView;
	}
	
	@RequestMapping("ssadmin/updateCoinFee")
	public ModelAndView updateCoinFee() throws Exception{
		ModelAndView modelAndView = new ModelAndView() ;
		String fid = request.getParameter("fid");
		List<Ffees> all = this.feeService.findByProperty("fvirtualcointype.fid", fid);
		
		//add by hank
		for (Ffees ffees : all) {
			String feeKey = "fee"+ffees.getFid();
			String buyfeeKey = "fbuyfee"+ffees.getFid();
			String withdrawKey = "withdraw"+ffees.getFid();
			double fee = Double.valueOf(request.getParameter(feeKey));
			double buyfee = Double.valueOf(request.getParameter(buyfeeKey));
			double withdraw = Double.valueOf(request.getParameter(withdrawKey));
			
			if(fee>=1 || fee<0 || withdraw>=1 || withdraw<0 || buyfee>=1 || buyfee<0){
				modelAndView.setViewName("ssadmin/comm/ajaxDone") ;
				modelAndView.addObject("statusCode",300);
				modelAndView.addObject("message","手续费率只能是小于1的小数！");
				return modelAndView;
			}
		}
		
		
		
		for (Ffees ffees : all) {
			String feeKey = "fee"+ffees.getFid();
			String withdrawKey = "withdraw"+ffees.getFid();
			String buyfeeKey = "fbuyfee"+ffees.getFid();
			double fee = Double.valueOf(request.getParameter(feeKey));
			double withdraw = Double.valueOf(request.getParameter(withdrawKey));
			double buyfee = Double.valueOf(request.getParameter(buyfeeKey));
			ffees.setFfee(fee);
			ffees.setWithdraw(withdraw);
			ffees.setFbuyfee(buyfee);
			this.feeService.updateObj(ffees);
		}
		
		modelAndView.setViewName("ssadmin/comm/ajaxDone") ;
		modelAndView.addObject("statusCode",200);
		modelAndView.addObject("message","更新成功");
		modelAndView.addObject("callbackType","closeCurrent");
		return modelAndView;
	}
	
	@RequestMapping("ssadmin/createWalletAddress")
	public ModelAndView createWalletAddress() throws Exception{
		ModelAndView modelAndView = new ModelAndView() ;
		modelAndView.setViewName("ssadmin/comm/ajaxDone") ;
		String fid = request.getParameter("uid");
		type = this.virtualCoinService.findById(fid);
		if(!type.isFIsWithDraw()){
			modelAndView.addObject("message","不允许充值和提现的虚拟币类型不能生成虚拟地址!");
			modelAndView.addObject("statusCode",300);
			return modelAndView;
		}
		btcMessage = new BTCMessage() ;
		btcMessage.setACCESS_KEY(type.getFaccess_key()) ;
		btcMessage.setIP(type.getFip()) ;
		btcMessage.setPORT(type.getFport()) ;
		btcMessage.setSECRET_KEY(type.getFsecrt_key()) ;
		btcMessage.setPASSWORD(request.getParameter("passWord"));
		if(btcMessage.getACCESS_KEY()==null
				||btcMessage.getIP()==null
				||btcMessage.getPORT()==null
				||btcMessage.getSECRET_KEY()==null){
			modelAndView.addObject("message","钱包连接失败，请检查配置信息");
			modelAndView.addObject("statusCode",300);
			return modelAndView;
		}
		
		try {
			new Thread(new Work()).start() ;
		} catch (Exception e) {
			modelAndView.addObject("message","钱包异常!");
			modelAndView.addObject("statusCode",300);
			return modelAndView;
		}
		
		
		modelAndView.addObject("message","后台执行中!");
		modelAndView.addObject("statusCode",200);
		modelAndView.addObject("rel", "createWalletAddress");
		modelAndView.addObject("callbackType","closeCurrent");
		return modelAndView;
	}
	
	private BTCMessage btcMessage;
	private Fvirtualcointype type;
	class Work implements Runnable{
		public void run() {
			createAddress(btcMessage, type);
		}
	}
	
	private void createAddress(BTCMessage btcMessage,Fvirtualcointype type) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:sss");
		BTCUtils btcUtils = null;
		try {
			btcUtils = new BTCUtils(btcMessage) ;
			for(int i=0;i<10000;i++){
				String address = btcUtils.getNewaddressValueForAdmin(sdf.format(Utils.getTimestamp()));
				if(address == null || address.trim().length() ==0){
                    break;
				}
				Fpool poolInfo = new Fpool();
				poolInfo.setFaddress(address);
				poolInfo.setFvirtualcointype(type);
				poolService.saveObj(poolInfo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				btcUtils.walletlock();
			} catch (Exception e) {}
		}

	}

}
