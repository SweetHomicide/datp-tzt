package com.ruizton.main.controller.admin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.ruizton.main.Enum.IdentityTypeEnum;
import com.ruizton.main.Enum.RegTypeEnum;
import com.ruizton.main.Enum.UserGradeEnum;
import com.ruizton.main.Enum.UserStatusEnum;
import com.ruizton.main.comm.ConstantMap;
import com.ruizton.main.controller.BaseController;
import com.ruizton.main.dao.FpoolDAO;
import com.ruizton.main.dao.FvirtualaddressDAO;
import com.ruizton.main.dao.FvirtualwalletDAO;
import com.ruizton.main.model.Fcapitaloperation;
import com.ruizton.main.model.Fpool;
import com.ruizton.main.model.Fscore;
import com.ruizton.main.model.Fuser;
import com.ruizton.main.model.Fusersetting;
import com.ruizton.main.model.Fvirtualaddress;
import com.ruizton.main.model.Fvirtualcointype;
import com.ruizton.main.model.Fvirtualwallet;
import com.ruizton.main.service.admin.AdminService;
import com.ruizton.main.service.admin.CapitaloperationService;
import com.ruizton.main.service.admin.ScoreService;
import com.ruizton.main.service.admin.UserService;
import com.ruizton.main.service.admin.UsersettingService;
import com.ruizton.main.service.admin.WithdrawFeesService;
import com.ruizton.main.service.front.FrontUserService;
import com.ruizton.util.Utils;
import com.ruizton.util.XlsExport;

@Controller
public class UserController extends BaseController {
	@Autowired
	private UserService userService;
	@Autowired
	private AdminService adminService;
	@Autowired
	private WithdrawFeesService withdrawFeesService;
	@Autowired
	private CapitaloperationService capitaloperationService;
	@Autowired
	private UsersettingService usersettingService;
	@Autowired
	private HttpServletRequest request;
	@Autowired
	private ScoreService scoreService;
	@Autowired
	private FrontUserService frontUserService;
	@Autowired
	private ConstantMap constantMap ;
	@Autowired
	private FpoolDAO fpoolDAO ;
	@Autowired
	private FvirtualwalletDAO fvirtualwalletDAO ;
	@Autowired
	private FvirtualaddressDAO fvirtualaddressDAO ;
	
	
	// 每页显示多少条数据
	private int numPerPage = Utils.getNumPerPage();

	@RequestMapping("/ssadmin/userList")
	public ModelAndView Index() throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("ssadmin/userList");
		// 当前页
		int currentPage = 1;
		// 搜索关键字
		String keyWord = request.getParameter("keywords");
		String uid = request.getParameter("uid");
		String startDate = request.getParameter("startDate");
		String orderField = request.getParameter("orderField");
		String orderDirection = request.getParameter("orderDirection");
		if (request.getParameter("pageNum") != null) {
			currentPage = Integer.parseInt(request.getParameter("pageNum"));
		}
		StringBuffer filter = new StringBuffer();
		filter.append("where 1=1 \n");
		if (keyWord != null && keyWord.trim().length() > 0) {
				filter.append("and (floginName like '%" + keyWord + "%' or \n");
				filter.append("fid like '%"+keyWord+"%' OR \n");
				filter.append("fnickName like '%" + keyWord + "%'  or \n");
				filter.append("frealName like '%" + keyWord + "%'  or \n");
				filter.append("ftelephone like '%" + keyWord + "%'  or \n");
				filter.append("femail like '%" + keyWord + "%'  or \n");
				filter.append("fidentityNo like '%" + keyWord + "%' )\n");
			modelAndView.addObject("keywords", keyWord);
		}

		Map<Integer,String> typeMap = new HashMap<Integer,String>();
		typeMap.put(0, "全部");
		typeMap.put(UserStatusEnum.NORMAL_VALUE,
				UserStatusEnum.getEnumString(UserStatusEnum.NORMAL_VALUE));
		typeMap.put(UserStatusEnum.FORBBIN_VALUE,
				UserStatusEnum.getEnumString(UserStatusEnum.FORBBIN_VALUE));
		modelAndView.addObject("typeMap", typeMap);

		if (request.getParameter("ftype") != null
				&& request.getParameter("ftype").trim().length() > 0) {
			int type = Integer.parseInt(request.getParameter("ftype"));
			if (type != 0) {
				filter.append("and fstatus=" + request.getParameter("ftype")
						+ " \n");
			}
			modelAndView.addObject("ftype", request.getParameter("ftype"));
		}

		try {
			if (request.getParameter("troUid") != null
					&& request.getParameter("troUid").trim().length() > 0) {
				String troUid = request.getParameter("troUid");
				filter.append("and fIntroUser_id.fid='" + troUid + "' \n");
				modelAndView.addObject("troUid", troUid);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (orderField != null && orderField.trim().length() > 0) {
			filter.append("order by " + orderField + "\n");
		} else {
			filter.append("order by fRegisterTime \n");
		}

		if (orderDirection != null && orderDirection.trim().length() > 0) {
			filter.append(orderDirection + "\n");
		} else {
			filter.append("desc \n");
		}

		List<Fuser> list = this.userService.list(
				(currentPage - 1) * numPerPage, numPerPage, filter + "", true);
		modelAndView.addObject("startDate", startDate);
		modelAndView.addObject("userList", list);
		modelAndView.addObject("numPerPage", numPerPage);
		modelAndView.addObject("currentPage", currentPage);
		modelAndView.addObject("rel", "listUser");
		// 总数量
		modelAndView.addObject("totalCount",
		 		this.adminService.getAllCount("Fuser", filter + ""));
		return modelAndView;
	}
	
	@RequestMapping("/ssadmin/viewUser1")
	public ModelAndView viewUser1() throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("ssadmin/viewUser1");
		// 当前页
		int currentPage = 1;
		// 搜索关键字
		String keyWord = request.getParameter("keywords");
		String orderField = request.getParameter("orderField");
		String orderDirection = request.getParameter("orderDirection");
		if (request.getParameter("pageNum") != null) {
			currentPage = Integer.parseInt(request.getParameter("pageNum"));
		}
		StringBuffer filter = new StringBuffer();
		filter.append("where 1=1 \n");
		if (keyWord != null && keyWord.trim().length() > 0) {
			filter.append("and (floginName like '%" + keyWord + "%' or \n");
			filter.append("fnickName like '%" + keyWord + "%'  or \n");
			filter.append("frealName like '%" + keyWord + "%'  or \n");
			filter.append("ftelephone like '%" + keyWord + "%'  or \n");
			filter.append("femail like '%" + keyWord + "%'  or \n");
			filter.append("fidentityNo like '%" + keyWord + "%' )\n");
			modelAndView.addObject("keywords", keyWord);
		}

		Map typeMap = new HashMap();
		typeMap.put(0, "全部");
		typeMap.put(UserStatusEnum.NORMAL_VALUE,
				UserStatusEnum.getEnumString(UserStatusEnum.NORMAL_VALUE));
		typeMap.put(UserStatusEnum.FORBBIN_VALUE,
				UserStatusEnum.getEnumString(UserStatusEnum.FORBBIN_VALUE));
		modelAndView.addObject("typeMap", typeMap);

		if (request.getParameter("ftype") != null
				&& request.getParameter("ftype").trim().length() > 0) {
			int type = Integer.parseInt(request.getParameter("ftype"));
			if (type != 0) {
				filter.append("and fstatus=" + request.getParameter("ftype")
						+ " \n");
			}
			modelAndView.addObject("ftype", request.getParameter("ftype"));
		}

		try {
			if (request.getParameter("troUid") != null
					&& request.getParameter("troUid").trim().length() > 0) {
				String troUid = request.getParameter("troUid");
				filter.append("and fIntroUser_id.fid='" + troUid + "' \n");
				modelAndView.addObject("troUid", troUid);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(request.getParameter("cid") != null){
			String cid = request.getParameter("cid");
			Fcapitaloperation c = this.capitaloperationService.findById(cid);
			filter.append("and fid ="+c.getFuser().getFid()+" \n");
			modelAndView.addObject("cid",cid);
		}

		if (orderField != null && orderField.trim().length() > 0) {
			filter.append("order by " + orderField + "\n");
		} else {
			filter.append("order by fregisterTime \n");
		}

		if (orderDirection != null && orderDirection.trim().length() > 0) {
			filter.append(orderDirection + "\n");
		} else {
			filter.append("desc \n");
		}

		List<Fuser> list = this.userService.list(
				(currentPage - 1) * numPerPage, numPerPage, filter + "", true);
		modelAndView.addObject("userList", list);
		modelAndView.addObject("numPerPage", numPerPage);
		modelAndView.addObject("currentPage", currentPage);
		modelAndView.addObject("rel", "viewUser1");
		// 总数量
		modelAndView.addObject("totalCount",
				this.adminService.getAllCount("Fuser", filter + ""));
		return modelAndView;
	}

	
	@RequestMapping("/ssadmin/userLookup")
	public ModelAndView userLookup() throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("ssadmin/userLookup");
		// 当前页
		int currentPage = 1;
		// 搜索关键字
		String keyWord = request.getParameter("keywords");

		if (request.getParameter("pageNum") != null) {
			currentPage = Integer.parseInt(request.getParameter("pageNum"));
		}
		StringBuffer filter = new StringBuffer();
		filter.append("where 1=1 \n");
		if (keyWord != null && keyWord.trim().length() > 0) {
				filter.append("and (floginName like '%" + keyWord + "%' or \n");
				filter.append("fuser.fid like '%"+keyWord+"%' OR \n");
				filter.append("fnickName like '%" + keyWord + "%'  or \n");
				filter.append("frealName like '%" + keyWord + "%'  or \n");
				filter.append("ftelephone like '%" + keyWord + "%'  or \n");
				filter.append("femail like '%" + keyWord + "%'  or \n");
				filter.append("fidentityNo like '%" + keyWord + "%' )\n");
			modelAndView.addObject("keywords", keyWord);
		}
		
		List<Fuser> list = this.userService.list(
				(currentPage - 1) * numPerPage, numPerPage, filter + "", true);
		modelAndView.addObject("userList1", list);
		modelAndView.addObject("numPerPage", numPerPage);
		modelAndView.addObject("rel", "operationLogList");
		modelAndView.addObject("currentPage", currentPage);
		// 总数量
		modelAndView.addObject("totalCount",
				this.adminService.getAllCount("Fuser", filter + ""));
		return modelAndView;
	}

	@RequestMapping("/ssadmin/userAuditList")
	public ModelAndView userAuditList() throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("ssadmin/userAuditList");
		// 当前页
		int currentPage = 1;
		// 搜索关键字
		String keyWord = request.getParameter("keywords");
		String orderField = request.getParameter("orderField");
		String orderDirection = request.getParameter("orderDirection");
		if (request.getParameter("pageNum") != null) {
			currentPage = Integer.parseInt(request.getParameter("pageNum"));
		}
		StringBuffer filter = new StringBuffer();
		filter.append("where 1=1 \n");
		if (keyWord != null && keyWord.trim().length() > 0) {
				filter.append("and (floginName like '%" + keyWord + "%' or \n");
				filter.append("fid like '%"+keyWord+"%' OR \n");
				filter.append("fnickName like '%" + keyWord + "%'  or \n");
				filter.append("frealName like '%" + keyWord + "%'  or \n");
				filter.append("ftelephone like '%" + keyWord + "%'  or \n");
				filter.append("femail like '%" + keyWord + "%'  or \n");
				filter.append("fidentityNo like '%" + keyWord + "%' )\n");
			modelAndView.addObject("keywords", keyWord);
		}
		filter.append("and fpostRealValidate=1 and fhasRealValidate=0 \n");

		if (orderField != null && orderField.trim().length() > 0) {
			filter.append("order by " + orderField + "\n");
		} else {
			filter.append("order by fid \n");
		}

		if (orderDirection != null && orderDirection.trim().length() > 0) {
			filter.append(orderDirection + "\n");
		} else {
			filter.append("desc \n");
		}
		List<Fuser> list = this.userService.listUserForAudit((currentPage - 1)
				* numPerPage, numPerPage, filter + "", true);
		modelAndView.addObject("userList", list);
		modelAndView.addObject("numPerPage", numPerPage);
		modelAndView.addObject("currentPage", currentPage);
		modelAndView.addObject("rel", "listUser");
		// 总数量
		modelAndView.addObject("totalCount",
				this.adminService.getAllCount("Fuser", filter + ""));
		return modelAndView;
	}

	@RequestMapping("/ssadmin/ajaxDone")
	public ModelAndView ajaxDone() throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("ssadmin/comm/ajaxDone");
		return modelAndView;
	}

	@RequestMapping("/ssadmin/userForbbin")
	public ModelAndView userForbbin() throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("ssadmin/comm/ajaxDone");
		// modelAndView.setViewName("redirect:/pages/ssadmin/comm/ajaxDone.jsp")
		// ;
		String fid = request.getParameter("uid");
		int status = Integer.parseInt(request.getParameter("status"));
		Fuser user = this.userService.findById(fid);
		if (status == 1) {
			if (user.getFstatus() == UserStatusEnum.FORBBIN_VALUE) {
				modelAndView.addObject("statusCode", 300);
				modelAndView.addObject("message", "会员已禁用，无需做此操作");
				return modelAndView;
			}
			modelAndView.addObject("statusCode", 200);
			modelAndView.addObject("message", "禁用成功");
			user.setFstatus(UserStatusEnum.FORBBIN_VALUE);
		} else if (status == 2) {
			if (user.getFstatus() == UserStatusEnum.NORMAL_VALUE) {
				modelAndView.addObject("statusCode", 300);
				modelAndView.addObject("message", "会员状态为正常，无需做此操作");
				return modelAndView;
			}
			modelAndView.addObject("statusCode", 200);
			modelAndView.addObject("message", "解除禁用成功");
			user.setFstatus(UserStatusEnum.NORMAL_VALUE);
		} else if (status == 3) {// 重设登陆密码
			modelAndView.addObject("statusCode", 200);
			modelAndView.addObject("message", "重设登陆密码成功，密码为:ABC123");
			user.setFloginPassword(Utils.MD5("ABC123"));
		} else if (status == 4) {// 重设交易密码
			modelAndView.addObject("statusCode", 200);
			modelAndView.addObject("message", "重设登陆密码成功，密码为:ABC123");
			user.setFtradePassword(Utils.MD5("ABC123"));
		}

		this.userService.updateObj(user);
		return modelAndView;
	}

	@RequestMapping("/ssadmin/auditUser")
	public ModelAndView auditUser() throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("ssadmin/comm/ajaxDone");
		int status = Integer.parseInt(request.getParameter("status"));
		String fid = request.getParameter("uid");

		Fuser user = this.userService.findById(fid);
		Fscore fscore = user.getFscore();
		Fuser fintrolUser = null;
		if (status == 1) {
			user.setFhasRealValidateTime(Utils.getTimestamp());
			user.setFhasRealValidate(true);
			user.setFisValid(true);
			if(!fscore.isFissend() && user.getfIntroUser_id() != null){
				fintrolUser = this.userService.findById(user.getfIntroUser_id().getFid());
				fintrolUser.setfInvalidateIntroCount(fintrolUser.getfInvalidateIntroCount()+1);
				fscore.setFissend(true);
			}
		} else {
			user.setFhasRealValidate(false);
			user.setFpostRealValidate(false);
			user.setFidentityNo(null);
			user.setFrealName(null);
		}
		try {
			this.userService.updateObj(user,fscore,fintrolUser);
		} catch (Exception e) {
			modelAndView.addObject("statusCode", 300);
			modelAndView.addObject("message", "网络异常");
			return modelAndView;
		}
		modelAndView.addObject("statusCode", 200);
		modelAndView.addObject("callbackType", "closeCurrent");
		modelAndView.addObject("message", "审核成功");

		return modelAndView;
	}

	@RequestMapping("ssadmin/goUserJSP")
	public ModelAndView goUserJSP() throws Exception {
		String url = request.getParameter("url");
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName(url);
		if (request.getParameter("uid") != null) {
			String fid = request.getParameter("uid");
			Fuser user = this.userService.findById(fid);
			modelAndView.addObject("fuser", user);
			
			List<Fusersetting> alls = this.usersettingService.list(0, 0, "where fuser.fid='"+fid+"'", false);
			Fusersetting usersetting = alls.get(0);
			modelAndView.addObject("usersetting", usersetting);

			Map<Integer,String> map = new HashMap<Integer,String>();
			map.put(IdentityTypeEnum.SHENFENZHENG, IdentityTypeEnum
					.getEnumString(IdentityTypeEnum.SHENFENZHENG));
			map.put(IdentityTypeEnum.JUNGUANGZHEN, IdentityTypeEnum
					.getEnumString(IdentityTypeEnum.JUNGUANGZHEN));
			map.put(IdentityTypeEnum.HUZHAO,
					IdentityTypeEnum.getEnumString(IdentityTypeEnum.HUZHAO));
			map.put(IdentityTypeEnum.TAIWAN,
					IdentityTypeEnum.getEnumString(IdentityTypeEnum.TAIWAN));
			map.put(IdentityTypeEnum.GANGAO,
					IdentityTypeEnum.getEnumString(IdentityTypeEnum.GANGAO));
			modelAndView.addObject("identityTypeMap", map);

			List allFees = withdrawFeesService.findAll();
			modelAndView.addObject("allFees", allFees);
		}
		
		Map<Integer,String> map = new HashMap<Integer,String>();
		map.put(UserGradeEnum.LEVEL0, UserGradeEnum.getEnumString(UserGradeEnum.LEVEL0));
		map.put(UserGradeEnum.LEVEL1, UserGradeEnum.getEnumString(UserGradeEnum.LEVEL1));
		map.put(UserGradeEnum.LEVEL2, UserGradeEnum.getEnumString(UserGradeEnum.LEVEL2));
		map.put(UserGradeEnum.LEVEL3, UserGradeEnum.getEnumString(UserGradeEnum.LEVEL3));
		map.put(UserGradeEnum.LEVEL4, UserGradeEnum.getEnumString(UserGradeEnum.LEVEL4));
		map.put(UserGradeEnum.LEVEL5, UserGradeEnum.getEnumString(UserGradeEnum.LEVEL5));
		modelAndView.addObject("typeMap", map);
		
		return modelAndView;
	}

	@RequestMapping("ssadmin/updateUserLevel")
	public ModelAndView updateUserLevel() throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		String fid = request.getParameter("fid");
		Fuser user = this.userService.findById(fid);
		Fscore score = user.getFscore();
		int newLevel = Integer.parseInt(request.getParameter("newLevel"));
		score.setFlevel(newLevel);
		this.scoreService.updateObj(score);

		modelAndView.setViewName("ssadmin/comm/ajaxDone");
		modelAndView.addObject("statusCode", 200);
		modelAndView.addObject("callbackType", "closeCurrent");
		modelAndView.addObject("message", "修改成功");
		return modelAndView;
	}

	@RequestMapping("ssadmin/updateIntroCount")
	public ModelAndView updateIntroCount(
			@RequestParam(required = true) int fInvalidateIntroCount)
			throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		String fid = request.getParameter("uid");
		Fuser user = this.userService.findById(fid);
		user.setfInvalidateIntroCount(fInvalidateIntroCount);
		this.userService.updateObj(user);

		modelAndView.setViewName("ssadmin/comm/ajaxDone");
		modelAndView.addObject("statusCode", 200);
		modelAndView.addObject("callbackType", "closeCurrent");
		modelAndView.addObject("message", "修改成功");
		return modelAndView;
	}
	
	@RequestMapping("ssadmin/updateUserGrade")
	public ModelAndView updateUserGrade()
			throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		String fid = request.getParameter("uid");
		Fuser user = this.userService.findById(fid);
		Fscore fscore = user.getFscore();
		fscore.setFlevel(Integer.parseInt(request.getParameter("fuserGrade")));
		this.scoreService.updateObj(fscore);

		modelAndView.setViewName("ssadmin/comm/ajaxDone");
		modelAndView.addObject("statusCode", 200);
		modelAndView.addObject("callbackType", "closeCurrent");
		modelAndView.addObject("message", "修改成功");
		return modelAndView;
	}
	
	@RequestMapping("ssadmin/updateIntroPerson")
	public ModelAndView updateIntroPerson()
			throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("ssadmin/comm/ajaxDone");
		String fid = request.getParameter("uid");
		Fuser user = this.userService.findById(fid);
		String fintrolId = request.getParameter("fintrolId");
		Fuser fintrolUser = this.userService.findById(fintrolId);
		if(fintrolUser == null){
			modelAndView.addObject("statusCode", 300);
			modelAndView.addObject("message", "用户不存在");
			return modelAndView;
		}
		user.setfIntroUser_id(fintrolUser);
		this.userService.updateObj(user);
		
		modelAndView.addObject("statusCode", 200);
		modelAndView.addObject("callbackType", "closeCurrent");
		modelAndView.addObject("message", "修改成功");
		return modelAndView;
	}
	
	@RequestMapping("ssadmin/updateUserScore")
	public ModelAndView updateUserScore()
			throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		int fid = Integer.parseInt(request.getParameter("uid"));
		Fusersetting usersetting = this.usersettingService.findById(fid);
		usersetting.setFscore(Double.valueOf(request.getParameter("fscore")));
		this.usersettingService.updateObj(usersetting);

		modelAndView.setViewName("ssadmin/comm/ajaxDone");
		modelAndView.addObject("statusCode", 200);
		modelAndView.addObject("callbackType", "closeCurrent");
		modelAndView.addObject("message", "修改成功");
		return modelAndView;
	}

	@RequestMapping("/ssadmin/cancelGoogleCode")
	public ModelAndView cancelGoogleCode() throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("ssadmin/comm/ajaxDone");
		String fid = request.getParameter("uid");
		Fuser user = this.userService.findById(fid);
		user.setFgoogleAuthenticator(null);
		user.setFgoogleBind(false);
		user.setFgoogleurl(null);
		user.setFgoogleValidate(false);
		user.setFopenSecondValidate(false);
		this.userService.updateObj(user);

		modelAndView.addObject("statusCode", 200);
		modelAndView.addObject("message", "重置谷歌认证成功");
		return modelAndView;
	}
	
	
	@RequestMapping("/ssadmin/cancelTel")
	public ModelAndView cancelTel() throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("ssadmin/comm/ajaxDone");
		String fid = request.getParameter("uid");
		Fuser user = this.userService.findById(fid);
		user.setFtelephone(null);
		user.setFisTelephoneBind(false);
		user.setFareaCode(null);
		this.userService.updateObj(user);

		modelAndView.addObject("statusCode", 200);
		modelAndView.addObject("message", "重置手机号码成功");
		return modelAndView;
	}


	 
	private static enum ExportFiled {
		会员UID,推荐人UID,会员登陆名,会员状态,昵称,真实姓名,会员等级,累计推荐注册数,电话号码,
		邮箱地址,证件类型,证件号码,注册时间,上次登陆时间;
	}

	private List<Fuser> getUserList() {
		String keyWord = request.getParameter("keywords");
		String orderField = request.getParameter("orderField");
		String orderDirection = request.getParameter("orderDirection");
		String uid = request.getParameter("uid");
		StringBuffer filter = new StringBuffer();
		filter.append("where 1=1 \n");
		if (keyWord != null && keyWord.trim().length() > 0) {
				filter.append("and (floginName like '%" + keyWord + "%' or \n");
				filter.append("fuser.fid like '%"+keyWord+"%' OR \n");
				filter.append("fnickName like '%" + keyWord + "%'  or \n");
				filter.append("frealName like '%" + keyWord + "%'  or \n");
				filter.append("ftelephone like '%" + keyWord + "%'  or \n");
				filter.append("femail like '%" + keyWord + "%'  or \n");
				filter.append("fidentityNo like '%" + keyWord + "%' )\n");
		}
		if (uid != null && uid.trim().length() > 0) {
			try {
				int fid = Integer.parseInt(uid);
				filter.append("and fid =" + fid + " \n");
			} catch (Exception e) {}
		}

		if (request.getParameter("ftype") != null
				&& request.getParameter("ftype").trim().length() > 0) {
			int type = Integer.parseInt(request.getParameter("ftype"));
			if (type != 0) {
				filter.append("and fstatus=" + request.getParameter("ftype")
						+ " \n");
			}
		}

		try {
			if (request.getParameter("troUid") != null
					&& request.getParameter("troUid").trim().length() > 0) {
				String troUid = request.getParameter("troUid");
				filter.append("and fIntroUser_id.fid='" + troUid + "' \n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (orderField != null && orderField.trim().length() > 0) {
			filter.append("order by " + orderField + "\n");
		} else {
			filter.append("order by fid \n");
		}

		if (orderDirection != null && orderDirection.trim().length() > 0) {
			filter.append(orderDirection + "\n");
		} else {
			filter.append("desc \n");
		}

		List<Fuser> list = this.userService.list(0, 0, filter + "", false);
		return list;
	}

	@RequestMapping("ssadmin/userExport")
	public ModelAndView userExport(HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		response.setContentType("Application/excel");
		response.addHeader("Content-Disposition",
				"attachment;filename=userList.xls");
		XlsExport e = new XlsExport();
		int rowIndex = 0;

		// header
		e.createRow(rowIndex++);
		for (ExportFiled filed : ExportFiled.values()) {
			e.setCell(filed.ordinal(), filed.toString());
		}

		List<Fuser> userList = getUserList();
		for (Fuser user : userList) {
			e.createRow(rowIndex++);
			for (ExportFiled filed : ExportFiled.values()) {
				switch (filed) {
				case 会员UID:
					e.setCell(filed.ordinal(), user.getFid());
					break;
				case 推荐人UID:
					if(user.getfIntroUser_id() != null)
					e.setCell(filed.ordinal(), user.getfIntroUser_id().getFid());
					break;
				case 会员登陆名:
					e.setCell(filed.ordinal(), user.getFloginName());
					break;
				case 会员状态:
					e.setCell(filed.ordinal(), user.getFstatus_s());
					break;
				case 昵称:
					e.setCell(filed.ordinal(), user.getFnickName());
					break;
				case 真实姓名:
					e.setCell(filed.ordinal(), user.getFrealName());
					break;
				case 会员等级:
					if(user.getFscore() != null)
					e.setCell(filed.ordinal(), "VIP"
							+ user.getFscore().getFlevel());
					break;
				case 累计推荐注册数:
					e.setCell(filed.ordinal(), user.getfInvalidateIntroCount());
					break;
				case 电话号码:
					e.setCell(filed.ordinal(), user.getFtelephone());
					break;
				case 邮箱地址:
					e.setCell(filed.ordinal(), user.getFemail());
					break;
				case 证件类型:
					e.setCell(filed.ordinal(), user.getFidentityType_s());
					break;
				case 证件号码:
					e.setCell(filed.ordinal(), user.getFidentityNo());
					break;
				case 注册时间:
					e.setCell(filed.ordinal(), user.getFregisterTime());
					break;
				case 上次登陆时间:
					e.setCell(filed.ordinal(), user.getFlastLoginTime());
					break;
				default:
					break;
				}
			}
		}

		e.exportXls(response);
		response.getOutputStream().close();

		modelAndView.setViewName("ssadmin/comm/ajaxDone");
		modelAndView.addObject("statusCode", 200);
		modelAndView.addObject("message", "导出成功");
		return modelAndView;
	}

	@RequestMapping("/ssadmin/userinfoList1")
	public ModelAndView userinfoList1() throws Exception{
		ModelAndView modelAndView = new ModelAndView() ;
		modelAndView.setViewName("ssadmin/userinfoList1") ;
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
			filter.append("and (t.floginName like '%"+keyWord+"%' \n");
			filter.append("or t.frealName like '%"+keyWord+"%' )\n");
			modelAndView.addObject("keywords", keyWord);
		}
		//总数量
		modelAndView.addObject("totalCount",this.adminService.getAllCount("Fuser t", filter+""));
				
		if (orderField != null && orderField.trim().length() > 0) {
			filter.append("order by " + orderField + "\n");
		}

		if (orderDirection != null && orderDirection.trim().length() > 0) {
			filter.append(orderDirection + "\n");
		}
		
		String id = request.getParameter("id");
		if(id != null && id.trim().length() >0){
			Fcapitaloperation capitaloperation = this.capitaloperationService.findById(id.trim());
			filter.append("and t.fid ='"+capitaloperation.getFuser().getFid()+"' \n");
			modelAndView.addObject("id", id);
		}
		
		List list = this.userService.getAllUserInfo((currentPage-1)*numPerPage, numPerPage, filter+"",true);
		modelAndView.addObject("userinfoList", list);
		modelAndView.addObject("numPerPage", numPerPage);
		modelAndView.addObject("currentPage", currentPage);
		modelAndView.addObject("rel", "userinfoList1");
		
		return modelAndView ;
	}
	
	@RequestMapping("/ssadmin/setUserNo")
	public ModelAndView setUserNo() throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("ssadmin/comm/ajaxDone");
		String fid = request.getParameter("fid");
		Fuser user = this.userService.findById(fid);
		String userNo = request.getParameter("fuserNo");
		if(userNo == null || userNo.trim().length() ==0){
			modelAndView.addObject("statusCode", 300);
			modelAndView.addObject("message", "商家号不能为空");
			return modelAndView;
		}else if(userNo.trim().length() >100){
			modelAndView.addObject("statusCode", 300);
			modelAndView.addObject("message", "商家号长度不能大于100个字符");
			return modelAndView;
		}
		
		if(user.getFuserNo() != null && user.getFuserNo().trim().length() > 0){
			modelAndView.addObject("statusCode", 300);
			modelAndView.addObject("message", "该用户已存在商家号，不允许修改！");
			return modelAndView;
		}
		
		String filter = "where fuserNo='"+userNo+"'";
		List<Fuser> fusers = this.userService.list(0, 0, filter, false);
		if(fusers.size() >0){
			modelAndView.addObject("statusCode", 300);
			modelAndView.addObject("message", "该商家号已存在！");
			return modelAndView;
		}

		user.setFuserNo(userNo);
		this.userService.updateObj(user);

		modelAndView.addObject("statusCode", 200);
		modelAndView.addObject("callbackType","closeCurrent");
		modelAndView.addObject("message", "商家号设置成功");
		return modelAndView;
	}
	
	@RequestMapping("/ssadmin/cancelPhone")
	public ModelAndView cancelPhone() throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("ssadmin/comm/ajaxDone");
		String fid = request.getParameter("uid");
		Fuser user = this.userService.findById(fid);
		user.setFtelephone(null);
		user.setFisTelephoneBind(false);
		user.setFisTelValidate(false);
		this.userService.updateObj(user);

		modelAndView.addObject("statusCode", 200);
		modelAndView.addObject("message", "重置手机绑定成功");
		return modelAndView;
	}
	
	@RequestMapping("/ssadmin/addUsers")
	public ModelAndView addUsers() throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("ssadmin/comm/ajaxDone");
		
		for (int i=0;i<10;i++) {
			Fuser fuser = new Fuser() ;
			
			String regName = Utils.getRandomString(10)+"@163.com";
			fuser.setFrealName("系统生成");
			fuser.setfIntroUser_id(null) ;
			fuser.setFregType(RegTypeEnum.EMAIL_VALUE);
			fuser.setFemail(regName) ;
			fuser.setFisMailValidate(true) ;
			fuser.setFnickName(regName.split("@")[0]) ;
			fuser.setFloginName(regName) ;
			
			
			fuser.setFregisterTime(Utils.getTimestamp()) ;
			fuser.setFloginPassword(Utils.MD5("123456abc")) ;
			fuser.setFtradePassword(null) ;
			String ip = Utils.getIp(request) ;
			fuser.setFregIp(ip);
			fuser.setFlastLoginIp(ip);
			fuser.setFstatus(UserStatusEnum.NORMAL_VALUE) ;
			fuser.setFlastLoginTime(Utils.getTimestamp()) ;
			fuser.setFlastUpdateTime(Utils.getTimestamp()) ;
			boolean saveFlag = false ;
			try {
				saveFlag = this.frontUserService.saveRegister(fuser) ;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		modelAndView.addObject("statusCode", 200);
		modelAndView.addObject("message", "操作成功");
		return modelAndView;
	}
	
	@RequestMapping("/ssadmin/setTiger")
	public ModelAndView setTiger() throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("ssadmin/comm/ajaxDone");
		String fid = request.getParameter("uid");
		Fuser user = this.userService.findById(fid);
		if(user.isFistiger()){
			user.setFistiger(false);
		}else{
			user.setFistiger(true);
		}
		this.userService.updateObj(user);

		modelAndView.addObject("statusCode", 200);
		modelAndView.addObject("message", "设置成功");
		return modelAndView;
	}
	
	
	@RequestMapping("/ssadmin/auditUserALL")
	public ModelAndView auditUserALL() throws Exception{
		ModelAndView modelAndView = new ModelAndView() ;
		modelAndView.setViewName("ssadmin/comm/ajaxDone") ;
		String ids = request.getParameter("ids");
		String[] idString = ids.split(",");
		int type = Integer.parseInt(request.getParameter("type"));
		for(int i=0;i<idString.length;i++){
			String id = idString[i];
			Fuser user = this.userService.findById(id);
			Fscore fscore = user.getFscore();
			Fuser fintrolUser = null;
			if (type == 1) {
				user.setFhasRealValidateTime(Utils.getTimestamp());
				user.setFhasRealValidate(true);
				user.setFisValid(true);
				if(!fscore.isFissend() && user.getfIntroUser_id() != null){
					fintrolUser = this.userService.findById(user.getfIntroUser_id().getFid());
					fintrolUser.setfInvalidateIntroCount(fintrolUser.getfInvalidateIntroCount()+1);
					fscore.setFissend(true);
				}
			} else {
				user.setFhasRealValidate(false);
				user.setFpostRealValidate(false);
				user.setFidentityNo(null);
				user.setFrealName(null);
			}
			try {
				this.userService.updateObj(user,fscore,fintrolUser);
			} catch (Exception e) {
				continue;
			}
		}
		modelAndView.addObject("statusCode", 200);
		modelAndView.addObject("message", "审核成功");
		return modelAndView;
	}
}
