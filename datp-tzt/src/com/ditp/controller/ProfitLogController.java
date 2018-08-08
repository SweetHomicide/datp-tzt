package com.ditp.controller;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import com.ditp.domain.JsonObject;
import com.ditp.domain.Pager;
import com.ditp.entity.ProfitLogRead;
import com.ditp.service.ProfitlogService;
import com.ruizton.main.controller.BaseController;
import com.ruizton.main.model.Fuser;

/**
 * 收益记录控制器
 * 
 * @author Thinkpad
 *
 */
@Controller
@RequestMapping("/profitLog")
public class ProfitLogController extends BaseController {
	@Autowired
	private ProfitlogService profitlogService;
	@Autowired
	private HttpServletRequest request;

	/**
	 * 查询 收益页面
	 * 
	 * @param profitLog
	 * @return
	 */
	@RequestMapping("/websearch")
	public ModelAndView search(ProfitLogRead tradeLog, String currentPage) {
		ModelAndView model = new ModelAndView();
		Fuser fuser = GetSession(request);
		tradeLog.setFuserid(fuser.getFid());
		if (null==currentPage||"".equals(currentPage)) {
			currentPage = "1";
		}
		int pageSize = 5;//每页显示多少条
		int tountCount = profitlogService.findCount(tradeLog);
		List<ProfitLogRead> listProfitLog = profitlogService.search(tradeLog, currentPage, pageSize);
		if (null==tradeLog.getBeginTime()) {
			tradeLog.setBeginTime("");
		}
		if (null==tradeLog.getEndTime()) {
			tradeLog.setEndTime("");
		}
		if (null==tradeLog.getFinaName()) {
			tradeLog.setFinaName("");
		}
		if (null==tradeLog.getFfinaId()) {
			tradeLog.setFfinaId("");
		}
		String pagin = generatePagin(tountCount/pageSize+(tountCount%pageSize==0?0:1),
				Integer.valueOf(currentPage), "/profitLog/websearch.html?ffinaId="+tradeLog.getFfinaId()+"&beginTime="+tradeLog.getBeginTime()+
				"&endTime="+tradeLog.getEndTime()+"&finaName="+tradeLog.getFinaName()+"&");
		model.addObject("list", listProfitLog);
		model.addObject("pagin", pagin);
		model.addObject("tradeLog",tradeLog);
		model.setViewName("/front/finacing/profitLog");
		return model;
	}
	
	/**
	 * 财务中心里面产品收益明细
	 * @return
	 */
	@RequestMapping("web/earningsDetail")
	public ModelAndView earningsDetail() {
		ModelAndView model = new ModelAndView();
		model.setViewName("");
		return model;
	}

	/**
	 * 查看收益数据
	 * @param tradeLog
	 * @param pager
	 * @return
	 */
	@RequestMapping("/search")
	public JsonObject searchJson(ProfitLogRead tradeLog, Pager pager) {
		JsonObject jsonObject = new JsonObject();
		try {
			//List<ProfitLogRead> listProfitLog = profitlogService.search(tradeLog, pager, true);
			jsonObject.setSuccess(true);
			//jsonObject.setData(listProfitLog);
			jsonObject.setMsg("查看成功");
		} catch (Exception e) {
			jsonObject.setSuccess(false);
			jsonObject.setData("");
			jsonObject.setMsg("查看失败");
		}
		return jsonObject;
	}
}
