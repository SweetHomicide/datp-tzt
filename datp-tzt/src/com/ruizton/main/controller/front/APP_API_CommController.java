package com.ruizton.main.controller.front;

import java.lang.reflect.Method;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ruizton.main.auto.RealTimeData;
import com.ruizton.main.controller.BaseController;
import com.ruizton.main.model.Fabout;
import com.ruizton.main.service.admin.AboutService;
import com.ruizton.util.Comm;
import com.ruizton.util.Constant;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Controller
@Scope("prototype")
public class APP_API_CommController extends BaseController {
	@Autowired
	private RealTimeData realTimeData;
	@Autowired
	private AboutService aboutService;

	public final static String Result = "result";
	public final static String ErrorCode = "code";
	public final static String Value = "value";
	public final static String LoginToken = "loginToken";
	public final static String CurrentPage = "currentPage";
	public final static String TotalPage = "totalPage";
	public final static String LastUpdateTime = "lastUpdateTime";
	public final static String Fid = "Fid";

	private String curLoginToken = null;
	int maxResult = Constant.AppRecordPerPage;

	@ResponseBody
	@RequestMapping(value = "/appApi_comm", produces = JsonEncode)
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

	public String isOpen(HttpServletRequest request) {
		JSONObject jsonObject = new JSONObject();
		try {

			String type = request.getParameter("type");
			String result = "";
			JSONArray jsonArray = new JSONArray();
			if (type != null && !"".equals(type)) {
				switch (type) {
				case "ISDEAL_OWNBYOWN":
					result = Boolean.toString(Comm.getISDEAL_OWNBYOWN());
					break;
				case "ISHIDDEN_DEAL":
					result = Comm.getISHIDDEN_DEAL();
					break;
				case "ISHIDDEN_EX":
					result = Boolean.toString(Comm.getISHIDDEN_EX());
					break;
				}

			} else if ("".equals(type)) {

				JSONObject item = new JSONObject();
				item.accumulate("ISDEAL_OWNBYOWN", Comm.getISDEAL_OWNBYOWN());
				item.accumulate("ISHIDDEN_DEAL", Boolean.parseBoolean(Comm.getISHIDDEN_DEAL()));
				item.accumulate("ISHIDDEN_EX", Comm.getISHIDDEN_EX());
				item.accumulate("ISHIDDEN_CROWDFUNDING", Comm.getISHIDDEN_CROWDFUNDING());
				jsonArray.add(item);

			}
			jsonObject.accumulate(Result, true);
			jsonObject.accumulate(ErrorCode, 0);
			jsonObject.accumulate(Value, "");
			jsonObject.accumulate("resultVal", result);
			jsonObject.accumulate("list", jsonArray);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			jsonObject.accumulate(Result, false);
			jsonObject.accumulate(ErrorCode, -1);
			jsonObject.accumulate(Value, "网络错误");
			// e.printStackTrace();
		}
		return jsonObject.toString();
	}

	/**
	 * 查询产品信息
	 * @return
	 */
	public String productIntroList(HttpServletRequest request) {
		JSONObject jsonObject = new JSONObject();
		JSONArray jsonArray=new JSONArray();
		try {

			List<Fabout> findByProperty = aboutService.findByProperty("ftype", "产品介绍");
			for(Fabout Fabout:findByProperty)
			{
				JSONObject item = new JSONObject();
				item.accumulate("fid", Fabout.getFid());
				item.accumulate("ftitle",Fabout.getFtitle());
				item.accumulate("ftype",Fabout.getFtype());
				String url="";
				switch(Fabout.getFtitle())
				{
				case "产品信息":url="/static/front/images/index/cp.jpg";
				case "数字资产信息":url="/static/front/images/index/zc.jpg";
				case "交易所IT运营指导信息":url="/static/front/images/index/jys.jpg";
				}
				item.accumulate("url",url);
				jsonArray.add(item);
			}
			jsonObject.accumulate(Result, true);
			jsonObject.accumulate(ErrorCode, 0);
			jsonObject.accumulate(Value, "");
			jsonObject.accumulate("list", jsonArray);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			jsonObject.accumulate(Result, false);
			jsonObject.accumulate(ErrorCode, -1);
			jsonObject.accumulate(Value, "网络错误");
		}
		return jsonObject.toString();

	}
	/**
	 * 根据id 获取内容
	 * @param id
	 * @return
	 */
	public String getProduct(HttpServletRequest request)
	{
		String id=request.getParameter("id");
		JSONObject jsonObject = new JSONObject();
		JSONObject jsFab = new JSONObject();
		try {
			Fabout fabout=aboutService.findById(id);
			jsFab=net.sf.json.JSONObject.fromObject(fabout);
			jsonObject.accumulate(Result, true);
			jsonObject.accumulate(ErrorCode, 0);
			jsonObject.accumulate(Value, "");
			jsonObject.accumulate("fabout", jsFab);
		} catch (Exception e) {
			jsonObject.accumulate(Result, false);
			jsonObject.accumulate(ErrorCode, -1);
			jsonObject.accumulate(Value, "网络错误");
		}
		return jsonObject.toString();
	}

}
