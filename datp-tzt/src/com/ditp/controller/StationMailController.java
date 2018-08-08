package com.ditp.controller;


import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import org.springframework.web.servlet.ModelAndView;

import com.ditp.domain.JsonObject;
import com.ditp.domain.Page;
import com.ditp.domain.Pager;
import com.ditp.entity.StationMail;
import com.ditp.entity.StationMailRead;
import com.ditp.service.StationMailService;
import com.ruizton.main.controller.BaseController;
import com.ruizton.main.model.Fuser;
/**
 * 站内信控制器
 * @author liuruichen
 *
 */
@Controller
@RequestMapping("/stationMail")
public class StationMailController extends BaseController {
	@Autowired
	private StationMailService stationMailService;
	@Autowired
	private HttpServletRequest request;

	@RequestMapping("/view")
	public ModelAndView view(StationMailRead stationMailRead, Pager page) {
		ModelAndView model = new ModelAndView();
		model.setViewName("/front/stationMessage/messageList");
		return model;
	}
	
	@RequestMapping("/viewSys")
	public ModelAndView viewSys(StationMailRead stationMailRead, Pager page) {
		ModelAndView model = new ModelAndView();
		model.setViewName("/front/stationMessage/messageListSys");
		return model;
	}
	/**
	 * 站内信首页
	 * 
	 * @param stationMailRead
	 * @param page
	 * @return
	 */
	@RequestMapping("/searchIndex")
	@ResponseBody
	public ModelAndView searchIndex(StationMailRead stationMailRead, Pager page) {
		ModelAndView model=new ModelAndView();
		JsonObject js = new JsonObject();
		Fuser fuser = GetSession(request);
		stationMailRead.setFuserid(fuser.getFid());
		Page<StationMailRead> listMy = stationMailService.get(page.getPageIndex(), page.getPageSize(), stationMailRead);
		stationMailRead.setFtype("00607");
		Page<StationMailRead> listSys = stationMailService.get(page.getPageIndex(), page.getPageSize(), stationMailRead);
		model.addObject("listMy", listMy);
		model.addObject("listSys", listSys);
		model.setViewName("/front/stationMessage/index");
		return model;
	}
	/**
	 * 个人消息
	 * 
	 * @param stationMailRead
	 * @param page
	 * @return
	 */
	@RequestMapping("/search")
	@ResponseBody
	public JsonObject search(StationMailRead stationMailRead, Pager page) {
		JsonObject js = new JsonObject();
		Fuser fuser = GetSession(request);
		stationMailRead.setFuserid(fuser.getFid());
		Page<StationMailRead> list = stationMailService.get(page.getPageIndex(), page.getPageSize(), stationMailRead);
		js.setData(list);
		js.setSuccess(true);
		return js;
	}
	/**
	 * 系统消息消息
	 * 
	 * @param stationMailRead
	 * @param page
	 * @return
	 */
	@RequestMapping("/searchSys")
	@ResponseBody
	public JsonObject searchSys(StationMailRead stationMailRead, Pager page) {
		JsonObject js = new JsonObject();
		Fuser fuser = GetSession(request);
		stationMailRead.setFuserid(fuser.getFid());
		stationMailRead.setFtype("00607");
		Page<StationMailRead> list = stationMailService.get(page.getPageIndex(), page.getPageSize(), stationMailRead);
		js.setData(list);
		js.setSuccess(true);
		return js;
	}
	/**
	 * 查询未读取站内信的数量
	 * 
	 * @return
	 */
	@RequestMapping("/unread")
	@ResponseBody
	public JsonObject getUnread() {
		JsonObject js = new JsonObject();
		Fuser fuser = GetSession(request);
		String count = stationMailService.getUnread(fuser.getFid());
		js.setData(count);
		js.setSuccess(true);
		return js;
	}

	/**
	 * 查看详情
	 * 
	 * @return
	 */
	@RequestMapping("/get")
	public ModelAndView get(StationMail stationMail) {
		ModelAndView model = new ModelAndView();
		Fuser fuser = GetSession(request);
		if (null != stationMail) {
			stationMail.setFuserid(fuser.getFid());
			StationMailRead stationMailRead = stationMailService.getAndUpdate(stationMail);
			model.addObject("stationMailRead", stationMailRead);
		}
		model.setViewName("/front/stationMessage/messageDetail");
		return model;
	}
	/**
	 * 查看详情
	 * 
	 * @return
	 */
	@RequestMapping("/getSys")
	public ModelAndView getSys(StationMail stationMail) {
		ModelAndView model = new ModelAndView();
		Fuser fuser = GetSession(request);
		if (null != stationMail) {
			stationMail.setFuserid(fuser.getFid());
			stationMail.setFtype("00607");
			StationMailRead stationMailRead = stationMailService.getAndUpdate(stationMail);
			model.addObject("stationMailRead", stationMailRead);
		}
		model.setViewName("/front/stationMessage/messageDetail");
		return model;
	}
	/**
	 * 删除
	 * 
	 * @param ids
	 * @return
	 */
	@RequestMapping("/del")
	@ResponseBody
	public JsonObject del(String[] ids) {
		JsonObject js = new JsonObject();
		String result = "0";
		Fuser fuser = GetSession(request);
		try {
			result = stationMailService.del(ids, fuser.getFid());
			js.setData(result);
			js.setSuccess(true);
		} catch (Exception e) {
			js.setData(result);
			js.setSuccess(false);
		}
		return js;
	}
}
