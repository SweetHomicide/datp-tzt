package com.ruizton.main.controller.admin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.ruizton.main.comm.ConstantMap;
import com.ruizton.main.comm.ParamArray;
import com.ruizton.main.controller.BaseController;
import com.ruizton.main.model.Fwebbaseinfo;
import com.ruizton.main.service.admin.WebBaseInfoService;

@Controller
public class WebBaseInfoController extends BaseController {
	@Autowired
	private WebBaseInfoService webBaseInfoService ;
	@Autowired
	private ConstantMap constantMap ;
	@Autowired
	private HttpServletRequest request ;
	
	@RequestMapping("/ssadmin/webBaseInfoList")
	public ModelAndView index() throws Exception{
		ModelAndView modelAndView = new ModelAndView() ;
		modelAndView.setViewName("ssadmin/webBaseInfo") ;
		List<Fwebbaseinfo> webBaseList = this.webBaseInfoService.findAll();
		if(webBaseList.size() >0){
			modelAndView.addObject("webBaseInfo",(Fwebbaseinfo)webBaseList.get(0));
		}
		return modelAndView ;
	}
	
	@RequestMapping("/ssadmin/saveOrUpdateWebInfo")
	public ModelAndView saveOrUpdateWebInfo(ParamArray param) throws Exception{
		Fwebbaseinfo fwebbaseinfo = param.getFwebbaseinfo();
		Fwebbaseinfo newInfo = null;
		if(request.getParameter("fwebbaseinfo.fid") != null){
			String fid = request.getParameter("fwebbaseinfo.fid");
			newInfo = this.webBaseInfoService.findById(fid);
			newInfo.setFbeianInfo(fwebbaseinfo.getFbeianInfo());
			newInfo.setFcopyRights(fwebbaseinfo.getFcopyRights());
			newInfo.setFdescription(fwebbaseinfo.getFdescription());
			newInfo.setFkeywords(fwebbaseinfo.getFkeywords());
			newInfo.setFsystemMail(fwebbaseinfo.getFsystemMail());
			newInfo.setFtitle(fwebbaseinfo.getFtitle());
			this.webBaseInfoService.updateObj(newInfo);
		}else{
			newInfo = new Fwebbaseinfo();
			newInfo.setFbeianInfo(fwebbaseinfo.getFbeianInfo());
			newInfo.setFcopyRights(fwebbaseinfo.getFcopyRights());
			newInfo.setFdescription(fwebbaseinfo.getFdescription());
			newInfo.setFkeywords(fwebbaseinfo.getFkeywords());
			newInfo.setFsystemMail(fwebbaseinfo.getFsystemMail());
			newInfo.setFtitle(fwebbaseinfo.getFtitle());
			this.webBaseInfoService.saveObj(newInfo);
		}
		
		constantMap.put("webinfo", fwebbaseinfo);
		
		ModelAndView modelAndView = new ModelAndView() ;
		modelAndView.setViewName("ssadmin/comm/ajaxDone") ;
		modelAndView.addObject("statusCode",200);
		modelAndView.addObject("message","保存成功");
		modelAndView.addObject("callbackType","closeCurrent");
		
		return modelAndView ;
	}

}
