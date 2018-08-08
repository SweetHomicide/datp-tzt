package com.ditp.controller;

import java.util.List;
import javax.servlet.http.HttpServletRequest;

import org.jboss.cache.factories.context.MVCCContextFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import com.ditp.domain.JsonObject;
import com.ditp.domain.Pager;
import com.ditp.entity.Detail;
import com.ditp.entity.Dict;
import com.ditp.entity.Finacing;
import com.ditp.entity.TradeLog;
import com.ditp.entity.TradeLogRead;
import com.ditp.service.DetailService;
import com.ditp.service.DictService;
import com.ditp.service.FinacingService;
import com.ditp.service.TradeService;
import com.ruizton.main.controller.BaseController;
import com.ruizton.main.model.Fuser;
import net.sf.json.JSONObject;

@Controller
public class TradeController extends BaseController {

	@Autowired
	private TradeService tradeService;
	@Autowired
	private FinacingService finacingService;
	@Autowired
	private DetailService detailService;
	@Autowired
	private HttpServletRequest request;
	@Autowired
	private DictService dictService;
	
	@RequestMapping("ssadmin/tradelog/getOne")
	public ModelAndView getOne(String fid){
		ModelAndView mv = new ModelAndView();
		try {
			Finacing finacingList = finacingService.getById(fid);
			List<Detail> detailList = detailService.getByfinaId(fid);
			mv.addObject("finacingList", finacingList);
			mv.addObject("detailList", detailList);
		} catch (Exception e) {
		}
		mv.setViewName("");
		return mv;
	}
	/**
	 * 购买理财产品
	 * @param tradeLog
	 * @param pwd
	 * @return
	 */
	@RequestMapping("/web/trade/save")
	@ResponseBody
	public String save(TradeLog tradeLog,String pwd) {
		JSONObject js = new JSONObject();
		try {
			Fuser fuser = GetSession(request);
			String result = tradeService.save(tradeLog, fuser,pwd);
			if (!"1".equals(result)) {
				js.accumulate("success", false);
				js.accumulate("message", result);
			} else {
				js.accumulate("success", true);
				js.accumulate("message", "买入理财产品成功！");
			}
		} catch (Exception e) {
			js.accumulate("success", false);
			js.accumulate("message", e.getMessage());
		}

		return js.toString();
	}
	
	

	/**
	 * 网站管理平台
	 * 查询 交易记录
	 * @param profitLog
	 * @return
	 */
	@RequestMapping("ssadmin/tradelog/web/search")
	public ModelAndView search(TradeLogRead tradeLog, String currentPage) {
		ModelAndView model = new ModelAndView();
		try {
			List<Dict> typeList = dictService.getByPid("7");
			model.addObject("typeList", typeList);
			model.setViewName("ssadmin/finacing/detailList");
			List<TradeLogRead> listTradeLogRead = tradeService.search(tradeLog, currentPage, 10);
			model.addObject("fuserName", tradeLog.getFuserName());
			model.addObject("ftype", tradeLog.getFtype());
			model.addObject("list", listTradeLogRead);
			model.addObject("pager", currentPage);
			model.addObject("rel","finacing/detailList");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return model;
	}
	
	/**
	 * 网站平台
	 * 查看财务中心中产品购买记录
	 * @return
	 */
	@RequestMapping("/web/buyRecord")
	public ModelAndView buyRecord(TradeLogRead tradeLog, String currentPage) {
		ModelAndView model = new ModelAndView();
		Fuser fuser = GetSession(request);
		tradeLog.setFuserId(fuser.getFid());
		if (null==currentPage||"".equals(currentPage)) {
			currentPage = "1";
		}
		int pageSize = 5;//每页显示多少条
		int tountCount = tradeService.findCount(tradeLog);
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
		List<TradeLogRead> listTradeLogRead = tradeService.search(tradeLog, currentPage, pageSize);
		String pagin = generatePagin(tountCount / pageSize + (tountCount % pageSize == 0 ? 0 : 1),
				Integer.valueOf(currentPage), "/web/buyRecord.html?ffinaId="+tradeLog.getFfinaId()+"&&beginTime=" + tradeLog.getBeginTime() + "&endTime="
						+ tradeLog.getEndTime() + "&finaName=" + tradeLog.getFinaName() + "&");
		model.setViewName("front/finacing/record");
		model.addObject("tradeLog",tradeLog);
		model.addObject("list",listTradeLogRead);
		model.addObject("pagin", pagin);
		return model;
	}

	/**
	 * 查看交易记录
	 * @param tradeLog
	 * @param pager
	 * @return
	 */
	@RequestMapping("/search")
	public JsonObject searchJson(TradeLog tradeLog, Pager pager) {
		JsonObject jsonObject = new JsonObject();
		try {
			//List<TradeLogRead> listTradeLogRead = tradeService.search(tradeLog, pager, true);
			jsonObject.setSuccess(true);
			//jsonObject.setData(listTradeLogRead);
			jsonObject.setMsg("查看成功");
		} catch (Exception e) {
			jsonObject.setSuccess(false);
			jsonObject.setData("");
			jsonObject.setMsg("查看失败");
		}
		return jsonObject;
	}
}
