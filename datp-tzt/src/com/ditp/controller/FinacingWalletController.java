package com.ditp.controller;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import com.ditp.domain.JsonObject;
import com.ditp.entity.Detail;
import com.ditp.entity.FinacingRead;
import com.ditp.entity.TradeLog;
import com.ditp.entity.Wallet;
import com.ditp.entity.WalletRead;
import com.ditp.service.DetailService;
import com.ditp.service.DictService;
import com.ditp.service.FinacingService;
import com.ditp.service.TradeService;
import com.ditp.service.WalletService;
import com.ruizton.main.controller.BaseController;
import com.ruizton.main.model.Fuser;

import net.sf.json.JSONObject;

@Controller
@RequestMapping("/finaWallet")
public class FinacingWalletController extends BaseController{

	@Autowired
	private WalletService walletService;
	
	@Autowired
	HttpServletRequest request;
	
	@Autowired
	private TradeService tradeService;
	
	@Autowired
	private FinacingService finacingService;
	@Autowired
	private DetailService detailService;
	/**
	 * 我的理财
	 * @param walletRead
	 * @param currentPage
	 * @return
	 */
	@RequestMapping("/view")
	public ModelAndView View(String currentPage) {
		ModelAndView mv = new ModelAndView();
		try {
			if (null==currentPage||"".equals(currentPage)) {
				currentPage = "1";
			}
			int pageSize = 2;//每页显示多少条
			Fuser fuser = GetSession(request);
			int tountCount = walletService.findTount(fuser.getFid());
			List<WalletRead> listWalletRead = walletService.get(fuser.getFid(),currentPage,pageSize);
			String pagin = generatePagin(tountCount/pageSize+(tountCount%pageSize==0?0:1), Integer.valueOf(currentPage), "/finaWallet/view.html?");
			mv.addObject("pagin", pagin);
			mv.addObject("listWalletRead", listWalletRead);
		} catch (Exception e) {
			e.printStackTrace();
		}
		mv.setViewName("/front/finacing/myFinacing");
		return mv;
	}
	
	
	@RequestMapping("/search")
	public JsonObject search(WalletRead walletRead)
	{
		JsonObject jsonObject=new JsonObject();
		try {
			//List<WalletRead> listWalletRead=walletService.get(walletRead);
			jsonObject.setSuccess(true);
			//jsonObject.setData(listWalletRead);
			jsonObject.setMsg("查看成功");
		} catch (Exception e) {
			jsonObject.setSuccess(false);
			jsonObject.setData("");
			jsonObject.setMsg("查看失败");
		}
		return jsonObject;
	}
	
	
	/**
	 * 查看理财产品详情
	 * @param walletRead
	 * @return
	 */
	@RequestMapping("/getDetailsFinacing")
	public ModelAndView getDetailsFinacing(String ffinaId,String currentPage) {
		ModelAndView mv=new ModelAndView();
		try {
			if (null==currentPage||"".equals(currentPage)) {
				currentPage = "1";
			}
			int pageSize = 2;
			Fuser fuser = GetSession(request);
			int tountCount = walletService.findTount(fuser.getFid(),ffinaId);
			List<WalletRead> listWalletRead = walletService.getDetails(ffinaId,fuser.getFid(),currentPage,pageSize);
			String pagin = generatePagin(tountCount/pageSize+(tountCount%pageSize==0?0:1), Integer.valueOf(currentPage), "/finaWallet/getDetailsFinacing.html?ffinaId="+ffinaId+"&");
			mv.addObject("listWalletRead", listWalletRead);
			mv.addObject("pagin", pagin);
		} catch (Exception e) {
			e.printStackTrace();
		}
		mv.setViewName("/front/finacing/finacingDetailList");
		return mv;
	}
	
	/**
	 * 跳转到提取页面 -- 活期理财产品才能提取
	 * @return
	 */
	@RequestMapping("/kitingView")
	public ModelAndView kitingView(String fid) {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("/front/finacing/withdraw");
		try {
			Fuser fuser = GetSession(request);
			FinacingRead finacing =	finacingService.getByFid(fid);
			List<Detail> list = detailService.getByfinaId(fid);
			Wallet w = walletService.findById(fid,fuser.getFid());
			mv.addObject("wallet", w);
			mv.addObject("finacing", finacing);
			mv.addObject("list", list);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mv;
	}
	
	/**
	 * 提取金额到平台
	 * @return
	 */
	@RequestMapping("/kitingMoney")
	@ResponseBody
	public String kitingMoney(TradeLog tradeLog,String password) {
		JSONObject js = new JSONObject();
		try {
			Fuser fuser = GetSession(request);
			tradeLog.setFuserId(fuser.getFid());
			String flag = tradeService.saveKitLog(fuser,tradeLog,password);
			if(flag.equals("提现成功")){
				js.accumulate("success", true);
				js.accumulate("message", flag);
			}else{
				js.accumulate("success", false);
				js.accumulate("message", flag);
			}
			
		} catch (Exception e) {
			js.accumulate("success", false);
			js.accumulate("message", e.getMessage());
		}
		return js.toString();
	}
	
}
