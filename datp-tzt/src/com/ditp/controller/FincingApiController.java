package com.ditp.controller;

import java.lang.reflect.Method;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.ditp.entity.Detail;
import com.ditp.entity.FinacingRead;
import com.ditp.entity.ProfitLogRead;
import com.ditp.entity.TradeLog;
import com.ditp.entity.TradeLogRead;
import com.ditp.entity.Wallet;
import com.ditp.entity.WalletRead;
import com.ditp.service.DetailService;
import com.ditp.service.FinacingService;
import com.ditp.service.ProfitlogService;
import com.ditp.service.TradeService;
import com.ditp.service.WalletService;
import com.ruizton.main.auto.RealTimeData;
import com.ruizton.main.controller.BaseController;
import com.ruizton.main.controller.front.ApiConstant;
import com.ruizton.main.model.Fuser;
import com.ruizton.main.model.Fvirtualwallet;
import com.ruizton.main.service.front.FrontUserService;
import com.ruizton.util.Constant;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
@Controller
public class FincingApiController  extends BaseController{
	@Autowired
	private FinacingService finacingService;
	@Autowired
	private FrontUserService frontUserService;
	@Autowired
	private DetailService detailService;
	@Autowired
	private WalletService walletService;
	@Autowired
	private TradeService tradeService;
	@Autowired
	private ProfitlogService profitlogService;
	
	
	
	public final static String Result = "result";
	public final static String ErrorCode = "code";
	public final static String Value = "value";
	public final static String LoginToken = "loginToken";
	public final static String CurrentPage = "currentPage";
	public final static String TotalPage = "totalPage";
	public final static String LastUpdateTime = "lastUpdateTime";
	public final static String Fid = "Fid";
	@Autowired
	private RealTimeData realTimeData;

	private String curLoginToken = null;
	int maxResult = Constant.AppRecordPerPage;
	@ResponseBody
	@RequestMapping(value = "/appApi_Fincing", produces = JsonEncode)
	public String appApi(HttpServletRequest request, @RequestParam(required = false, defaultValue = "0") String action)
			throws Exception {
		request.setCharacterEncoding("UTF-8");
		curLoginToken = request.getParameter(LoginToken);
		Integer actionInteger = ApiConstant.getInteger(action);

		// 判断是否需要登录才能操作
		if (actionInteger / 100 == 2) {
			boolean isLogin = this.realTimeData.isAppLogin(this.curLoginToken, false);
			if (isLogin == false) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.accumulate(Result, false);
				jsonObject.accumulate(ErrorCode, -10001);// 未登录
				jsonObject.accumulate(Value, "未登录");// 未登录
				return jsonObject.toString();
			}
		}

		String ret = "";
		switch (actionInteger) {
		case 0: {
			JSONObject jsonObject = new JSONObject();
			jsonObject.accumulate(Result, false);
			jsonObject.accumulate(ErrorCode, -10000);// 非法提交
			jsonObject.accumulate(Value, "API不存在");// 未登录
			ret = jsonObject.toString();
		}
			break;

		default:
			try {
				Method method = this.getClass().getDeclaredMethod(action, HttpServletRequest.class);
				ret = (String) method.invoke(this, request);
			} catch (Exception e) {
				ret = ApiConstant.getUnknownError(e);
			}
			break;
		}

		if (Constant.isRelease == false) {
			System.out.println(ret);
		}
		return ret;
	}
	
	/**
	 * 理财产品列表展示
	 * @return
	 */
	public String FinacingView(HttpServletRequest request) {
		JSONObject jsonObject = new JSONObject();
		JSONArray jsonArray=new JSONArray();
		String currentPage=request.getParameter("currentPage");
		if (null==currentPage||"".equals(currentPage)) {
			currentPage = "1";
		}
		try {
			int pageSize = 10;//每页显示多少条
			int tountCount = finacingService.findTount();
			List<FinacingRead> finacingList = finacingService.toList(Integer.valueOf(currentPage),pageSize);
			jsonArray=net.sf.json.JSONArray.fromObject(finacingList);
			jsonObject.accumulate("list", jsonArray);
			jsonObject.accumulate(Result, true);
			jsonObject.accumulate(ErrorCode, 0);
			jsonObject.accumulate(Value, "");
			jsonObject.accumulate("totalPage", (tountCount+pageSize-1)/pageSize);
			jsonObject.accumulate("currentPage", currentPage);
		} catch (Exception e) {
			return ApiConstant.getUnknownError(e);
		}
		return jsonObject.toString();
	}
	/**
	 * 根据理财id查找理财信息 
	 * @param fid
	 * @return
	 */
	public String GetFacingById(HttpServletRequest request){
		JSONObject jsonObject = new JSONObject();
		JSONArray jsonArray=new JSONArray();
		String fid=request.getParameter("fid");
		try {
			//根据id查找finacing实体类
			FinacingRead finacing =	finacingService.getByFid(fid);
			List<Detail> list = detailService.getByfinaId(fid);
			Fuser fuser =this.realTimeData.getAppFuser(this.curLoginToken);
			Fvirtualwallet fvirtualwallet = this.frontUserService.findVirtualWalletByUser(fuser.getFid(), finacing.getFvitypeId()) ;
			jsonObject.accumulate("totalBTC", fvirtualwallet.getFtotal()+fvirtualwallet.getFvirtualcointype().getfSymbol());
			jsonArray=JSONArray.fromObject(list);
			jsonObject.accumulate("finacing", jsonObject.fromObject(finacing));
			jsonObject.accumulate("list", jsonArray);
			jsonObject.accumulate(Result, true);
			jsonObject.accumulate(ErrorCode, 0);
			jsonObject.accumulate(Value, "");
		} catch (Exception e) {
			return ApiConstant.getUnknownError(e);
		}
		return jsonObject.toString();
	}
	
	/**
	 * 我的理财 【需要登录】
	 * @param walletRead
	 * @param currentPage
	 * @return
	 */
	public String MyFincingView(HttpServletRequest request) {
		JSONObject jsonObject = new JSONObject();
		JSONArray jsonArray=new JSONArray();
		String currentPage=request.getParameter("currentPage");
		try {
			if (null==currentPage||"".equals(currentPage)) {
				currentPage = "1";
			}
			int pageSize = 2;//每页显示多少条
			Fuser fuser =this.realTimeData.getAppFuser(this.curLoginToken);
/*		    Fuser fuser=new Fuser();
		    fuser.setFid("402880c95a25e54c015a260e14ad00bd");*/
			int tountCount = walletService.findTount(fuser.getFid());
			List<WalletRead> listWalletRead = walletService.get(fuser.getFid(),currentPage,pageSize);
			jsonArray=JSONArray.fromObject(listWalletRead);
			jsonObject.accumulate("list", jsonArray);
			jsonObject.accumulate("totalPage", (tountCount+pageSize-1)/pageSize);
			jsonObject.accumulate(Result, true);
			jsonObject.accumulate(ErrorCode, 0);
			jsonObject.accumulate(CurrentPage, currentPage);
			jsonObject.accumulate(Value, "");
		} catch (Exception e) {
			return ApiConstant.getUnknownError(e);
		}
		return jsonObject.toString();
	}
	
	/**
	 * 查看理财产品详情 【需要登录】
	 * @param walletRead
	 * @return
	 */
	public String getDetailsFinacing(HttpServletRequest request) {
		String currentPage=request.getParameter("currentPage");
		String ffinaId=request.getParameter("ffinaId");
		JSONObject jsonObject = new JSONObject();
		JSONArray jsonArray=new JSONArray();
		try {
			if (null==currentPage||"".equals(currentPage)) {
				currentPage = "1";
			}
			int pageSize = 2;
			Fuser fuser=this.realTimeData.getAppFuser(this.curLoginToken);
			int tountCount = walletService.findTount(fuser.getFid(),ffinaId);
			List<WalletRead> listWalletRead = walletService.getDetails(ffinaId,fuser.getFid(),currentPage,pageSize);
			jsonArray=JSONArray.fromObject(listWalletRead);
			jsonObject.accumulate("list", jsonArray);
			jsonObject.accumulate("totalPage", (tountCount+pageSize-1)/pageSize);
			jsonObject.accumulate(Result, true);
			jsonObject.accumulate(ErrorCode, 0);
			jsonObject.accumulate(Value, "");
		} catch (Exception e) {
			return ApiConstant.getUnknownError(e);
		}
		return jsonObject.toString();
	}
	/**
	 * 网站平台 【需要登录】
	 * 查看财务中心中产品购买记录
	 * @return
	 */
	public String buyRecord(HttpServletRequest request) {
		String currentPage=request.getParameter("currentPage");
		JSONObject jsonObject = new JSONObject();
		JSONArray jsonArray=new JSONArray();
		TradeLogRead tradeLog = new TradeLogRead();
		tradeLog.setBeginTime(request.getParameter("beginTime"));
		tradeLog.setEndTime(request.getParameter("endTime"));
		tradeLog.setFinaName(request.getParameter("finaName"));
		tradeLog.setFfinaId(request.getParameter("ffinaId"));
		try {
			Fuser fuser =this.realTimeData.getAppFuser(this.curLoginToken);
			tradeLog.setFuserId(fuser.getFid());
			if (null==currentPage||"".equals(currentPage)) {
				currentPage = "1";
			}
			int pageSize = 10;//每页显示多少条
			int tountCount = tradeService.findCount(tradeLog);
			List<TradeLogRead> listTradeLogRead = tradeService.search(tradeLog, currentPage, pageSize);
			jsonArray=JSONArray.fromObject(listTradeLogRead);
			jsonObject.accumulate("list", jsonArray);
			jsonObject.accumulate("totalPage", (tountCount+pageSize-1)/pageSize);
			jsonObject.accumulate(Result, true);
			jsonObject.accumulate(ErrorCode, 0);
			jsonObject.accumulate(Value, "");
		} catch (Exception e) {
			return ApiConstant.getUnknownError(e);
		}
		return jsonObject.toString();
	}
	/**
	 * 查询 收益页面 【需要登录】
	 * 
	 * @param profitLog
	 * @return
	 */
	public String ProfitLogView(HttpServletRequest request) {
		JSONObject jsonObject = new JSONObject();
		JSONArray jsonArray=new JSONArray();
		String currentPage=request.getParameter("currentPage");
		String beginTime = request.getParameter("beginTime");
		String endTime = request.getParameter("endTime");
		String finaName = request.getParameter("finaName");
		String ffinaId = request.getParameter("ffinaId");
		ProfitLogRead tradeLog = new ProfitLogRead();
		if (null==beginTime) {
			tradeLog.setBeginTime("");
		}
		if (null==endTime) {
			tradeLog.setEndTime("");
		}
		if (null==finaName) {
			tradeLog.setFinaName("");
		}
		if (null==ffinaId) {
			tradeLog.setFfinaId("");
		}
		tradeLog.setBeginTime(beginTime);
		tradeLog.setEndTime(endTime);
		tradeLog.setFinaName(finaName);
		tradeLog.setFfinaId(ffinaId);
		try {
			Fuser fuser = this.realTimeData.getAppFuser(this.curLoginToken);
			tradeLog.setFuserid(fuser.getFid());
			if (null==currentPage||"".equals(currentPage)) {
				currentPage = "1";
			}
			int pageSize = 10;//每页显示多少条
			int tountCount = profitlogService.findCount(tradeLog);
			List<ProfitLogRead> listProfitLog = profitlogService.search(tradeLog, currentPage, pageSize);
			jsonArray=JSONArray.fromObject(listProfitLog);
			jsonObject.accumulate("tradeLog", tradeLog);
			jsonObject.accumulate("list", jsonArray);
			jsonObject.accumulate("totalPage", (tountCount+pageSize-1)/pageSize);
			jsonObject.accumulate(Result, true);
			jsonObject.accumulate(ErrorCode, 0);
			jsonObject.accumulate(Value, "");
		} catch (Exception e) {
			return ApiConstant.getUnknownError(e);
		}
		return jsonObject.toString();
	}
	/**
	 * 跳转购买页面【需要登录】
	 * @param fid
	 * @return
	 */
	public String getFacingById(HttpServletRequest request){
		String fid = request.getParameter("fid");
		JSONObject jsonObject = new JSONObject();
		JSONArray jsonArray=new JSONArray();
		try {
			//根据id查找finacing实体类
			Fuser fuser = this.realTimeData.getAppFuser(this.curLoginToken);
			FinacingRead finacing =	finacingService.getByFid(fid);
			List<Detail> list = detailService.getByfinaId(fid);
			Fvirtualwallet fvirtualwallet = null;
			fvirtualwallet = this.finacingService.findVirtualWallet(fuser.getFid(), finacing.getFvitypeId());
			jsonArray=JSONArray.fromObject(list);
			fvirtualwallet.getFuser();
			jsonObject.accumulate("list", jsonArray);
			jsonObject.accumulate("fsymbol", fvirtualwallet.getFvirtualcointype().getfSymbol());//币种符号
			jsonObject.accumulate("ftotal", fvirtualwallet.getFtotal());
			jsonObject.accumulate("finacing", JSONObject.fromObject(finacing));
			jsonObject.accumulate(Result, true);
			jsonObject.accumulate(ErrorCode, 0);
			jsonObject.accumulate(Value, "");
		} catch (Exception e) {
			return ApiConstant.getUnknownError(e);
		}
		return jsonObject.toString();
	}
	
	/**
	 * 购买理财产品
	 * @param tradeLog
	 * @param pwd
	 * @return
	 */
	public String buyFinacing(HttpServletRequest request) {
		String pwd = request.getParameter("pwd");
		String ffinaId = request.getParameter("ffinaId");
		String famount = request.getParameter("famount");
		JSONObject js = new JSONObject();
		try {
			TradeLog tradeLog = new TradeLog();
			tradeLog.setFfinaId(ffinaId);
			tradeLog.setFamount(Double.parseDouble(famount));
			Fuser fuser = this.realTimeData.getAppFuser(this.curLoginToken);
			String result = tradeService.save(tradeLog, fuser,pwd);
			if (!"1".equals(result)) {
				js.accumulate(Result, false);
				js.accumulate(ErrorCode, -1);
				js.accumulate(Value, result);;
			} else {
				js.accumulate(Result, true);
				js.accumulate(ErrorCode, 0);
				js.accumulate(Value, "买入理财产品成功！");
			}
		} catch (Exception e) {
			return ApiConstant.getUnknownError(e);
		}
		return js.toString();
	}
	
	/**
	 * 跳转到提取页面 -- 活期理财产品才能提取
	 * @return
	 */
	@RequestMapping("/kitingView")
	public String kitingView(HttpServletRequest request) {
		String fid = request.getParameter("fid");
		JSONObject jsonObject = new JSONObject();
		JSONArray jsonArray=new JSONArray();
		try {
			Fuser fuser = this.realTimeData.getAppFuser(this.curLoginToken);
			FinacingRead finacing =	finacingService.getByFid(fid);
			List<Detail> list = detailService.getByfinaId(fid);
			Wallet w = walletService.findById(fid,fuser.getFid());
			jsonArray=JSONArray.fromObject(list);
			jsonObject.accumulate("list", jsonArray);
			jsonObject.accumulate("wallet", w);
			jsonObject.accumulate("finacing", finacing);
			jsonObject.accumulate(Result, true);
			jsonObject.accumulate(ErrorCode, 0);
			jsonObject.accumulate(Value, "");
		} catch (Exception e) {
			return ApiConstant.getUnknownError(e);
		}
		return jsonObject.toString();
	}
	/**
	 * 提取金额到平台
	 * @return
	 */
	@RequestMapping("/kitingMoney")
	@ResponseBody
	public String kitingMoney(HttpServletRequest request) {
		String password = request.getParameter("password");
		String ffinaId = request.getParameter("ffinaId");
		String famount = request.getParameter("famount");
		JSONObject js = new JSONObject();
		try {
			TradeLog tradeLog = new TradeLog();
			tradeLog.setFfinaId(ffinaId);
			tradeLog.setFamount(Double.parseDouble(famount));
			Fuser fuser = this.realTimeData.getAppFuser(this.curLoginToken);
			String flag = tradeService.saveKitLog(fuser,tradeLog,password);
			if(flag.equals("提现成功")){
				js.accumulate(Result, true);
				js.accumulate(ErrorCode, 0);
				js.accumulate(Value, flag);;
			}else{
				js.accumulate(Result, false);
				js.accumulate(ErrorCode, -1);
				js.accumulate(Value, flag);;
			}
			
		} catch (Exception e) {
			return ApiConstant.getUnknownError(e);
		}
		return js.toString();
	}
	
}
