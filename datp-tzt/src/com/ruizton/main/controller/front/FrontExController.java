package com.ruizton.main.controller.front;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.ruizton.main.Enum.SubscriptionTypeEnum;
import com.ruizton.main.auto.RealTimeData;
import com.ruizton.main.controller.BaseController;
import com.ruizton.main.model.Fsubscription;
import com.ruizton.main.model.Fsubscriptionlog;
import com.ruizton.main.model.Fuser;
import com.ruizton.main.model.Fvirtualcointype;
import com.ruizton.main.service.admin.SubscriptionService;
import com.ruizton.main.service.front.FrontTradeService;
import com.ruizton.main.service.front.FrontUserService;
import com.ruizton.main.service.front.FrontVirtualCoinService;
import com.ruizton.util.Comm;
import com.ruizton.util.Utils;

import net.sf.json.JSONObject;

@Controller
public class FrontExController extends BaseController {
	@Autowired
	private FrontUserService frontUserService;
	@Autowired
	private FrontTradeService frontTradeService;
	@Autowired
	private SubscriptionService subscriptionService;

	
	@RequestMapping("/exchange/index")
	public ModelAndView exchange(HttpServletRequest request,
			@RequestParam(required = false, defaultValue = "0") String id,
			@RequestParam(required = false, defaultValue = "1") String currentPage,
			@RequestParam(required = false, defaultValue = "1") String type // 0是兑换中心
																			// 1兑换操作页面
	) throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		int pageSize = Comm.getEX_PAGE();
	
		String filter = "where fisopen=1 and ftype=" + SubscriptionTypeEnum.COIN;
		String searchName = request.getParameter("searchName");
		if (searchName != null && !"".equals(searchName)) {
			filter += " and (ftitle  like '%" + searchName + "%' or fvirtualcointype.fname like '%" + searchName
					+ "%' or fvirtualcointypeCost.fname like '%" + searchName + "%')";
			modelAndView.addObject("searchName", searchName);
		}
		filter +=" order by fcreateTime desc";
		List<Fsubscription> fsubscription1 = this.subscriptionService
				.list((Integer.valueOf(currentPage) - 1) *pageSize, pageSize, filter, true);
	
		int limitNum = (Integer.valueOf(currentPage) - 1) * pageSize;
		List<Fsubscription> fsubscriptions = this.subscriptionService.list(0, 0, filter, false);
		String pagin = "";
		if (fsubscriptions.size() == 0&&"".equals(searchName)) {
			/* modelAndView.setViewName("redirect:/") ; */
			modelAndView.addObject("msg", "未开通兑换，请等待");
			modelAndView.setViewName("front/menuBlank");
			return modelAndView;
		}
		int count = fsubscriptions.size();
		modelAndView.addObject("fsubscription1", fsubscription1);
		if (type.equals("1")) {
			Fuser fuser = this.frontUserService.findById(GetSession(request).getFid());
			Fsubscription fsubscription = this.frontTradeService.findFsubscriptionById(id);
			if (fsubscription == null || fsubscription.getFtype() != SubscriptionTypeEnum.COIN) {
				fsubscription = this.frontTradeService.findFirstSubscription(SubscriptionTypeEnum.COIN);
				if (fsubscription == null) {
					modelAndView.setViewName("redirect:/");
					return modelAndView;
				}
			}
			// Fsubscription fsubscription =
			// this.frontTradeService.findFsubscriptionById(id);
			Fvirtualcointype fvirtualcointype = fsubscription.getFvirtualcointype();

			String coinName = fsubscription.getFvirtualcointypeCost().getFname();

			double coinAmt = this.frontUserService
					.findVirtualWalletByUser(fuser.getFid(), fsubscription.getFvirtualcointypeCost().getFid())
					.getFtotal();

			int begin = 0;
			long now = Utils.getTimestamp().getTime();
			if (fsubscription.getFbeginTime().getTime() > now) {
				// 没开始
				begin = 0;
			}

			if (fsubscription.getFbeginTime().getTime() < now && fsubscription.getFendTime().getTime() > now) {
				// 进行中
				begin = 1;
			}

			if (fsubscription.getFendTime().getTime() < now) {
				// 结束
				begin = 2;
			}

			// 认购记录
			List<Fsubscriptionlog> fsubscriptionlogs = this.frontTradeService.findFsubScriptionLog(limitNum,pageSize,fuser,
					fsubscription.getFid());
			//总条数
			int totalCount = this.frontTradeService.findFsubScriptionLogCount(fuser,fsubscription.getFid());
			String generatePagin = this.generatePagin(totalCount/pageSize+(totalCount%pageSize==0?0:1),
					Integer.valueOf(currentPage), "/exchange/index.html?id="+id+"&type="+type+"&");
			// 可购买数量
			int buyCount = fsubscription.getFbuyCount();
			for (int i = 0; i < fsubscriptionlogs.size(); i++) {
				buyCount -= fsubscriptionlogs.get(i).getFcount();
			}
			buyCount = buyCount < 0 ? 0 : buyCount;

			// 可购买次数
			int buyTimes = fsubscription.getFbuyTimes() - fsubscriptionlogs.size();
			buyTimes = buyTimes < 0 ? 0 : buyTimes;
			
			modelAndView.addObject("totalCount", totalCount);
			modelAndView.addObject("count", count);
			modelAndView.addObject("page1", generatePagin);
			modelAndView.addObject("fsubscriptionlogs", fsubscriptionlogs);
			modelAndView.addObject("fsubscription", fsubscription);
			modelAndView.addObject("fsubscriptions", fsubscriptions);
			pagin = this.generatePagin5(count / pageSize + ((count % pageSize) == 0 ? 0 : 1),
					Integer.valueOf(currentPage));
			modelAndView.addObject("coinName", coinName);
			modelAndView.addObject("coinAmt", coinAmt);
			modelAndView.addObject("buyCount", buyCount);
			modelAndView.addObject("buyTimes", buyTimes);
			modelAndView.addObject("begin", begin);
			modelAndView.addObject("fid", fsubscription.getFid());
			modelAndView.addObject("fvirtualcointype", fvirtualcointype);
			modelAndView.setViewName("front/exchange/index");
		}
		if (type.equals("0")) {
			pagin = this.generatePaginForEx(count / pageSize + ((count % pageSize) == 0 ? 0 : 1),
					Integer.valueOf(currentPage));
			modelAndView.setViewName("front/exchange/panelindex");
		}
		modelAndView.addObject("page", pagin);
		return modelAndView;
	}

	@ResponseBody
	@RequestMapping("/exchange/index1")
	public String exchange1(HttpServletRequest request,
			@RequestParam(required = false, defaultValue = "") String hidlog,
			@RequestParam(required = false, defaultValue = "1") String currentPage,
			@RequestParam(required = false, defaultValue = "0") int count,
			@RequestParam(required = false, defaultValue = "0") String type // 0是兑换中心
																			// 1兑换操作页面
	) throws Exception {
		JSONObject js = new JSONObject();
		String result = "";
		int pageSize = Comm.getEX_PAGE();
		List<Fsubscription> fList;
		String sql = "";
		int limitNum = (Integer.valueOf(currentPage) - 1) * pageSize;
		if ("".equals(hidlog) || hidlog.equals(null)) {
			String filter = " where fisopen=1 and ftype=2";
			String className = "Fsubscription";
			fList = this.subscriptionService.findByParam(limitNum, pageSize, filter, true, className);
			//sql = "select count(1) from Fsubscription where fisopen=1 and ftype=2";
		} else {
			String filter = " where fisopen=1 and ftype=2 and ftitle like '%" + hidlog + "%' order by fcreateTime desc";
			String className = "Fsubscription";
			fList = this.subscriptionService.findByParam(limitNum, pageSize, filter, true, className);
			sql = " select count(1) from Fsubscription where fisopen=1 and ftype=2 and ftitle like '%" + hidlog + "%'";

			count = this.subscriptionService.findCountBysql(sql);
		}
	
		/* if (fList.size()!=0) { */
		for (Fsubscription fsubscription : fList) {

			/*String fa = "<div class='col-xs-4' style='line-height:40px;'><a href='/exchange/index.html?id="
					+ fsubscription.getFid() + "'"
					+ "><i class='lefticon col-xs-2' style='margin-right:5px;top:5px;width:20px;height:30px;background-size:100%;background-image: url("
					+ fsubscription.getFvirtualcointype().getFurl() + ")'></i><span>&nbsp;" + fsubscription.getFtitle()
					+ "</span></a></div>";*/
			String fa = "<div class='col-xs-4' style='line-height:40px;'><a href='/exchange/index.html?id="
					+ fsubscription.getFid() + "'"
					+ "><span>&nbsp;" 
					+ fsubscription.getFtitle()
					+ "</span></a></div>";

			result = result + fa;
		}

		/*
		 * } else { result =
		 * "<div class='col-xs-4' style='line-height:40px;'><span>未查询到相应币种</span></div>"
		 * ;
		 * 
		 * }
		 */

		String pagin = this.generatePagin5(count / pageSize + ((count % pageSize) == 0 ? 0 : 1),
				Integer.valueOf(currentPage));
		js.accumulate("count", count);
		js.accumulate("page", pagin);
		if (!"".equals(result)) {
			js.accumulate("result", result);
		}
		return js.toString();
	}

}
