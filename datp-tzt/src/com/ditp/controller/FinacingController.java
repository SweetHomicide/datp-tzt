package com.ditp.controller;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import com.ditp.entity.Detail;
import com.ditp.entity.Dict;
import com.ditp.entity.Finacing;
import com.ditp.entity.FinacingRead;
import com.ditp.service.DetailService;
import com.ditp.service.DictService;
import com.ditp.service.FinacingService;
import com.ruizton.main.controller.BaseController;
import com.ruizton.main.model.Fuser;
import com.ruizton.main.model.Fvirtualcointype;
import com.ruizton.main.model.Fvirtualwallet;
import com.ruizton.main.service.admin.VirtualCoinService;

import net.sf.json.JSONObject;

@Controller
public class FinacingController extends BaseController{
	@Autowired
	private FinacingService finacingService;
	@Autowired
	private DictService dictService;
	@Autowired
	private HttpServletRequest request;
	@Autowired
	private VirtualCoinService virtualCoinService;
	@Autowired
	private DetailService detailService;
	
	@RequestMapping("/ssadmin/fina/goAddModify")
	public ModelAndView getDictList(){
		String url = request.getParameter("url");
		String fid = request.getParameter("fid");
		ModelAndView mv = new ModelAndView();
		try {
			List<Dict> typeList = dictService.getByPid("1");
			List<Dict> assetsTypeList = dictService.getByPid("4");
			List<Dict> list = dictService.getByPid("10");
			List<Fvirtualcointype> virtualcointypeList = virtualCoinService.list(0, 0, "where fstatus = 1", false);
			mv.addObject("list", list);
			mv.addObject("typeList", typeList);
			mv.addObject("assetsTypeList", assetsTypeList);
			mv.addObject("virtualcointypeList", virtualcointypeList);
			if(!"".equals(fid)&&fid!=null){
				Finacing finacing =	finacingService.getById(fid);
				List<Detail> finaIdList = detailService.getByfinaId(fid);
				mv.addObject("finacing", finacing);
				mv.addObject("finaIdList", finaIdList);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		mv.setViewName(url);
		return mv;
	}

	
	/**
	 * 推荐理财首页跳转
	 * @return
	 */
	@RequestMapping("/fina/finacingIndex")
	public ModelAndView finacingIndex() {
		ModelAndView model = new ModelAndView();
		model.setViewName("");
		return model;
	}
	
	/**
	 * 首页推荐理财产品展示
	 * @return
	 */
	@RequestMapping("/fina/getRecommendProduct")
	public String getRecommendProduct () {
		int flag = 6;
		JSONObject json = new JSONObject();
		try {
			List<Finacing> finacingList = finacingService.getRecommendProduct(flag);
			json.accumulate("finacingList", finacingList);
		} catch (Exception e) {
			json.accumulate("message", e.getMessage());
			json.accumulate("success", false);
		}
		return json.toString();
	}
	
	/**
	 * 理财产品列表展示
	 * @return
	 */
	@RequestMapping("/finacing/index")
	public ModelAndView toList(String currentPage) {
		ModelAndView mv = new ModelAndView();
		if (null==currentPage||"".equals(currentPage)) {
			currentPage = "1";
		}
		try {
			int pageSize = 2;//每页显示多少条
			int tountCount = finacingService.findTount();
			List<FinacingRead> finacingList = finacingService.toList(Integer.valueOf(currentPage),pageSize);
			String pagin = generatePagin(tountCount/pageSize+(tountCount%pageSize==0?0:1), Integer.valueOf(currentPage), "/finacing/index.html?");
			mv.addObject("pagin", pagin);
			mv.addObject("list", finacingList);
			mv.addObject("success", true);
		} catch (Exception e) {
		}
		mv.setViewName("/front/finacing/index");
		return mv;
	}
	
	/*@RequestMapping("/finacing/index")
	public ModelAndView index(String currentPage){
		ModelAndView mv = new ModelAndView();
		if(currentPage==null){
			currentPage="1";
		}
		List<FinacingRead> list = finacingService.get(Integer.valueOf(currentPage));
		String pagin = generatePagin(list.size()/20, Integer.valueOf(currentPage), "/finacing/index.html?");
		//mv.addObject("currentPage", currentPage);
		mv.addObject("pagin", pagin);
		mv.addObject("list", list);
		mv.setViewName("/front/finacing/index");
		return mv;
	}*/
	/**
	 * 跳转购买页面
	 * @param fid
	 * @return
	 */
	@RequestMapping("/finacing/getFacingById")
	public ModelAndView getFacingById(String fid){
		ModelAndView mv = new ModelAndView();
		try {
			//根据id查找finacing实体类
			Fuser fuser = GetSession(request);
			FinacingRead finacing =	finacingService.getByFid(fid);
			List<Detail> list = detailService.getByfinaId(fid);
			Fvirtualwallet fvirtualwallet = null;
			fvirtualwallet = this.finacingService.findVirtualWallet(fuser.getFid(), finacing.getFvitypeId());
			mv.addObject("fvirtualwallet", fvirtualwallet);
			mv.addObject("finacing", finacing);
			mv.addObject("list", list);
		} catch (Exception e) {
			e.printStackTrace();
		}
		mv.setViewName("/front/finacing/pay");
		return mv;
	}
	
	@RequestMapping("/ssadmin/fina/finaList")
	public ModelAndView finaList(String page,String fname,String ftype,String fstatus){
		ModelAndView mv = new ModelAndView();
		List<FinacingRead> list = null;
		try {
			StringBuffer bufferfilter = new StringBuffer();
			bufferfilter.append("where 1=1 ");
			if("".equals(page)||page==null){
				page="0";
			}
			if(!"".equals(fname)&&fname!=null){
				bufferfilter.append("and a.fname like '%"+fname+"%' ");
			}
			if(!"".equals(ftype)&&ftype!=null){
				bufferfilter.append(" and a.ftype='"+ftype+"'");
			}
			if(!"".equals(fstatus)&&fstatus!=null){
				bufferfilter.append(" and a.fstatus='"+fstatus+"'");
			}
			List<Dict> typeList = dictService.getByPid("1");
			List<Dict> fstatusList = dictService.getByPid("14");
			list = finacingService.get(Integer.valueOf(page), bufferfilter.toString());
			mv.addObject("fname", fname);
			mv.addObject("ftype", ftype);
			mv.addObject("fstatus", fstatus);
			mv.addObject("typeList", typeList);
			mv.addObject("fstatusList", fstatusList);
			mv.addObject("list", list);
		} catch (Exception e) {
			e.printStackTrace();
		}
		mv.setViewName("/ssadmin/finacing/list");
		return mv;
	}
	
	
	
	@RequestMapping("/fina/get")
	public String getList(String page){
		JSONObject js = new JSONObject();
		try {
			List<FinacingRead> list = finacingService.get(Integer.valueOf(page));
			js.accumulate("data", list);
			js.accumulate("success", true);
			js.accumulate("message", "查询理财列表成功！");
		} catch (Exception e) {
			js.accumulate("message", e.getMessage());
			js.accumulate("success", false);
		}
		return js.toString();
	}
	
	@RequestMapping("/ssadmin/fina/save")
	public ModelAndView save(Finacing finacing){
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("ssadmin/comm/ajaxDone");
		JSONObject js = new JSONObject();
		try {
			finacingService.save(finacing);
		} catch (Exception e) {
			e.printStackTrace();
		}
		modelAndView.addObject("statusCode", 200);
		modelAndView.addObject("message", "保存成功");
		modelAndView.addObject("callbackType", "closeCurrent");
		return modelAndView;
	}
	
	@RequestMapping("/fina/getById")
	public String update(String fid){
		JSONObject js = new JSONObject();
		try {
			//根据id查找finacing实体类
			if("".equals(fid)){
				js.accumulate("success", false);
				js.accumulate("message", "ID不能为空！");
			}else{
				Finacing finacing =	finacingService.getById(fid);
				js.accumulate("finacing", finacing);
				js.accumulate("success", true);
				js.accumulate("message", "查询成功");
			}
		} catch (Exception e) {
			js.accumulate("message", e.getMessage());
			js.accumulate("success", false);
		}
		return js.toString();
	}
	@RequestMapping("/ssadmin/fina/del")
	public ModelAndView del(String fids){
		try {
			finacingService.del(fids);
		} catch (Exception e) {
			e.printStackTrace();
		}
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("ssadmin/comm/ajaxDone");
		modelAndView.addObject("statusCode", 200);
		modelAndView.addObject("message", "删除成功");
		return modelAndView;
	}

	
}
