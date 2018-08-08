package com.ditp.controller;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.ditp.domain.JsonObject;
import com.ditp.domain.Page;
import com.ditp.domain.Pager;
import com.ditp.entity.StationMail;
import com.ditp.entity.StationMailRead;
import com.ditp.service.StationMailService;
import com.ruizton.main.auto.RealTimeData;
import com.ruizton.main.controller.BaseController;
import com.ruizton.main.controller.front.ApiConstant;
import com.ruizton.main.model.Fuser;
import com.ruizton.util.Constant;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
@Controller
public class APIStationMailController extends BaseController{

	@Autowired
	private StationMailService stationMailService;
	@Autowired
	private HttpServletRequest request;
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
	@RequestMapping(value = "/appApi_StaMail", produces = JsonEncode)
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
	 * 查询未读取站内信的数量
	 * @return
	 */
	public String getUnread(HttpServletRequest request)
	{
		JSONObject jsonObject = new JSONObject();
		Fuser fuser =this.realTimeData.getAppFuser(this.curLoginToken);
		try {
			String count=stationMailService.getUnread(fuser.getFid());
			jsonObject.accumulate("count", count);
			jsonObject.accumulate(Result, true);
			jsonObject.accumulate(ErrorCode, 0);
			jsonObject.accumulate(Value, "");
		} catch (Exception e) {
			return ApiConstant.getUnknownError(e);
		}
		return jsonObject.toString();
	}
	/**
	 * 分页查询
	 * @param stationMailRead
	 * @param page
	 * @return
	 */
	public String staMailSearch(HttpServletRequest request) {
		JSONObject jsonObject = new JSONObject();
		JSONArray jsonArray=new JSONArray();
		StationMailRead stationMailRead=new StationMailRead();
		Pager page=new Pager();
		try {
			String currentPage=request.getParameter("currentPage");
			String title=request.getParameter("title");
			String type=request.getParameter("type");
			if(StringUtils.isNotEmpty(currentPage))
			{
				page.setPageIndex(Integer.parseInt(currentPage));
			}
			if(StringUtils.isNotEmpty(title))
			{
				stationMailRead.setFtitle(title);
			}
			if(StringUtils.isNotEmpty(type))
			{
				stationMailRead.setFtype(type);
			}
			Fuser fuser = this.realTimeData.getAppFuser(this.curLoginToken);
			stationMailRead.setFuserid(fuser.getFid());
			Page<StationMailRead> list = stationMailService.get(page.getPageIndex(), page.getPageSize(), stationMailRead);
			jsonArray=net.sf.json.JSONArray.fromObject(list);
			jsonObject.accumulate("list", jsonArray);
			jsonObject.accumulate(Result, true);
			jsonObject.accumulate(ErrorCode, 0);
			jsonObject.accumulate(Value, "");
			jsonObject.accumulate("totalPage", (list.getTotalElements()+page.getPageSize()-1)/page.getPageSize());
			jsonObject.accumulate("currentPage", currentPage);
		} catch (Exception e) {
			return ApiConstant.getUnknownError(e);
		}
		return jsonObject.toString();
	}
	
	/**
	 * 查看详情
	 * @return
	 */
	public String staMailget(HttpServletRequest request)
	{
		JSONObject jsonObject = new JSONObject();
		StationMail stationMail=new StationMail();
		try {
			String fid=request.getParameter("fid");
			stationMail.setFid(fid);
			Fuser fuser =this.realTimeData.getAppFuser(this.curLoginToken);
			stationMail.setFuserid(fuser.getFid());
			String type=request.getParameter("type");
			if(StringUtils.isNotEmpty(type))
			{
				stationMail.setFtype(type);
			}
			StationMailRead stationMailRead=stationMailService.getAndUpdate(stationMail);
			jsonObject.accumulate("stationMailRead", stationMailRead);
			jsonObject.accumulate(Result, true);
			jsonObject.accumulate(ErrorCode, 0);
			jsonObject.accumulate(Value, "");
		} catch (Exception e) {
			return ApiConstant.getUnknownError(e);
		}

		return jsonObject.toString();
	}
	/**
	 * 删除
	 * @param ids
	 * @return
	 */
	public String staMaildel(HttpServletRequest request)
	{
		JSONObject jsonObject = new JSONObject();
		String result="0";
		Fuser fuser = this.realTimeData.getAppFuser(this.curLoginToken);
		String [] ids=request.getParameter("ids").split(",");
		try {
			    result= stationMailService.del(ids, fuser.getFid());
				jsonObject.accumulate(Result, true);
				jsonObject.accumulate(ErrorCode, 0);
				jsonObject.accumulate(Value, "");
		} catch (Exception e) {
			return ApiConstant.getUnknownError(e);
		}
		return jsonObject.toString();
	}
}
