package com.ruizton.main.controller.front;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.ruizton.main.Enum.TransportlogStatusEnum;
import com.ruizton.main.comm.ConstantMap;
import com.ruizton.main.controller.BaseController;
import com.ruizton.main.model.Ftransportlog;
import com.ruizton.main.model.Fuser;
import com.ruizton.main.model.Fvirtualcointype;
import com.ruizton.main.model.Fvirtualwallet;
import com.ruizton.main.service.admin.AdminService;
import com.ruizton.main.service.admin.TransportlogService;
import com.ruizton.main.service.admin.VirtualCoinService;
import com.ruizton.main.service.front.FrontUserService;
import com.ruizton.util.Constant;

@Controller
public class FrontTransferController extends BaseController {
//	@Autowired
//	private FrontUserService frontUserService ;
//	@Autowired
//	private AdminService adminService;
//	@Autowired
//	private ConstantMap map;
//	@Autowired
//	private TransportlogService transportlogService;
//	@Autowired
//	private ConstantMap constantMap;
//
//	@RequestMapping("/account/btcTransport")
//	public ModelAndView btcTransport(
//			HttpServletRequest request,
//			@RequestParam(required=false,defaultValue="1")int currentPage
//			) throws Exception {
//		ModelAndView modelAndView = new ModelAndView() ;
//		Fuser fuser = this.frontUserService.findById(GetSession(request).getFid()) ;
//		
//		String filter = " where (fuser.fid='"+fuser.getFid()+"' or faddress ='"+fuser.getFid()+"') order by fid desc " ;
//		int total = this.adminService.getAllCount("Ftransportlog", filter);
//		int totalPage = total/Constant.AppRecordPerPage + ((total%Constant.AppRecordPerPage==0)?0:1) ;
//		List<Ftransportlog> list = this.transportlogService.list((currentPage-1)*Constant.AppRecordPerPage, Constant.AppRecordPerPage, filter, true);
//		String pagin = super.generatePagin(totalPage, currentPage, "/account/btcTransport.html?") ;
//		
//		List<Fvirtualcointype> fvirtualcointypes = (List)this.constantMap.get("virtualCoinType");
//		modelAndView.addObject("fvirtualcointypes", fvirtualcointypes) ;
//		
//		
//		double transferRate = Double.valueOf(this.map.get("transferRate").toString());
//		modelAndView.addObject("transferRate", transferRate) ;
//		
//		boolean isBindGoogle = fuser.getFgoogleBind() ;
//		boolean isBindTelephone = fuser.isFisTelephoneBind() ;
//		modelAndView.addObject("isBindGoogle", isBindGoogle) ;
//        modelAndView.addObject("isBindTelephone", isBindTelephone) ;
//		
//		modelAndView.addObject("pagin", pagin) ;
//		modelAndView.addObject("list", list) ;
//		modelAndView.addObject("fuser", fuser) ;
//		modelAndView.setViewName("front/transfer/btctransport") ;
//		return modelAndView ;
//	}

}
