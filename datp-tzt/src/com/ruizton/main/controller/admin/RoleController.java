package com.ruizton.main.controller.admin;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.security.MessageDigest;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.ruizton.main.Enum.AdminLevelEnum;
import com.ruizton.main.Enum.AdminStatusEnum;
import com.ruizton.main.comm.ParamArray;
import com.ruizton.main.controller.BaseController;
import com.ruizton.main.model.Fabout;
import com.ruizton.main.model.Fadmin;
import com.ruizton.main.model.Frole;
import com.ruizton.main.model.FroleSecurity;
import com.ruizton.main.model.Fsecurity;
import com.ruizton.main.service.admin.AboutService;
import com.ruizton.main.service.admin.AdminService;
import com.ruizton.main.service.admin.RoleSecurityService;
import com.ruizton.main.service.admin.RoleService;
import com.ruizton.main.service.admin.SecurityService;
import com.ruizton.util.Constant;
import com.ruizton.util.Utils;

@Controller
public class RoleController extends BaseController {
	@Autowired
	private RoleService roleService;
	@Autowired
	private SecurityService securityService;
	@Autowired
	private RoleSecurityService roleSecurityService;
	@Autowired
	private AdminService adminService ;
	@Autowired
	private HttpServletRequest request ;
	//每页显示多少条数据
	private int numPerPage = Utils.getNumPerPage();
	
	@RequestMapping("/ssadmin/roleList")
	public ModelAndView roleList() throws Exception{
		ModelAndView modelAndView = new ModelAndView() ;
		modelAndView.setViewName("ssadmin/roleList") ;
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
			filter.append("and fname like '%"+keyWord+"%' \n");
			modelAndView.addObject("keywords", keyWord);
		}
		if(orderField != null && orderField.trim().length() >0){
			filter.append("order by "+orderField+"\n");
		}else{
			filter.append("order by fid \n");
		}
		if(orderDirection != null && orderDirection.trim().length() >0){
			filter.append(orderDirection+"\n");
		}else{
			filter.append("desc \n");
		}
		List<Frole> list = this.roleService.list((currentPage-1)*numPerPage, numPerPage, filter+"",true);
		modelAndView.addObject("roleList", list);
		modelAndView.addObject("numPerPage", numPerPage);
		modelAndView.addObject("currentPage", currentPage);
		modelAndView.addObject("rel", "roleList");
		//总数量
		modelAndView.addObject("totalCount",this.adminService.getAllCount("Frole", filter+""));
		return modelAndView ;
	}
	
	@RequestMapping("ssadmin/goRoleJSP")
	public ModelAndView goRoleJSP() throws Exception{
		String url = request.getParameter("url");
		ModelAndView modelAndView = new ModelAndView() ;
		modelAndView.setViewName(url) ;
		if(request.getParameter("uid") != null){
			String fid = request.getParameter("uid");
			Fsecurity security = this.securityService.findById(fid);
			modelAndView.addObject("security", security);
		}
		if(request.getParameter("roleId") != null){
			String roleId = request.getParameter("roleId");
			Frole role = this.roleService.findById(roleId);
			modelAndView.addObject("role", role);
		}
		return modelAndView;
	}
	
	@RequestMapping("ssadmin/saveRole")
	public ModelAndView saveRole() throws Exception{
		ModelAndView modelAndView = new ModelAndView() ;
		List<Fsecurity> all = this.securityService.findAll();
		Frole role = new Frole();
		role.setFdescription(request.getParameter("fdescription"));
		role.setFname(request.getParameter("fname"));
		this.roleService.saveObj(role);
		
		for (Fsecurity fsecurity : all) {
			String fid = fsecurity.getFid();
			String key = "role["+fid+"]";
			String value = request.getParameter(key);
			if(value != null){
				FroleSecurity roleSecurity = new FroleSecurity();
				roleSecurity.setFrole(role);
				roleSecurity.setFsecurity(fsecurity);
				this.roleSecurityService.saveObj(roleSecurity);
			}
		}

		modelAndView.setViewName("ssadmin/comm/ajaxDone") ;
		modelAndView.addObject("statusCode",200);
		modelAndView.addObject("message","保存成功");
		modelAndView.addObject("callbackType","closeCurrent");
		return modelAndView;
	}
	
	@RequestMapping("ssadmin/updateRole")
	public ModelAndView updateRole() throws Exception{
		ModelAndView modelAndView = new ModelAndView() ;
		List<Fsecurity> all = this.securityService.findAll();
		String roleId = request.getParameter("roleId");
		Frole role = this.roleService.findById(roleId);
		role.setFdescription(request.getParameter("fdescription"));
		role.setFname(request.getParameter("fname"));
		this.roleService.updateObj(role);
		
		this.roleSecurityService.deleteByRoleId(roleId);
		for (Fsecurity fsecurity : all) {
			String fid = fsecurity.getFid();
			String key = "role["+fid+"]";
			String value = request.getParameter(key);
			if(value != null){
				FroleSecurity roleSecurity = new FroleSecurity();
				roleSecurity.setFrole(role);
				roleSecurity.setFsecurity(fsecurity);
				this.roleSecurityService.saveObj(roleSecurity);
			}
		}

		modelAndView.setViewName("ssadmin/comm/ajaxDone") ;
		modelAndView.addObject("statusCode",200);
		modelAndView.addObject("message","更新成功");
		modelAndView.addObject("callbackType","closeCurrent");
		return modelAndView;
	}
	
}
