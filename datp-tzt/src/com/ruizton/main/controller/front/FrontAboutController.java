package com.ruizton.main.controller.front;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.ruizton.main.comm.ConstantMap;
import com.ruizton.main.comm.KeyValues;
import com.ruizton.main.controller.BaseController;
import com.ruizton.main.model.Fabout;
import com.ruizton.main.model.Fvirtualcointype;
import com.ruizton.main.service.admin.AboutService;
import com.ruizton.main.service.front.FrontOthersService;
import com.ruizton.main.service.front.FrontVirtualCoinService;

@Controller
public class FrontAboutController extends BaseController {

	@Autowired
	private FrontOthersService frontOthersService ;
	@Autowired
	private ConstantMap map;
	@Autowired
	private AboutService aboutService;
	@Autowired
	private FrontVirtualCoinService frontVirtualCoinService ;
	@Autowired
	private ConstantMap constantMap ;
	
	
	@RequestMapping("/about/index")
	public ModelAndView index(
			@RequestParam(required=false,defaultValue="1")String id
			) throws Exception{
		ModelAndView modelAndView = new ModelAndView() ;
		
		
		Fabout fabout = this.frontOthersService.findFabout(id) ;
		if(fabout == null){
			modelAndView.setViewName("redirect:/") ;
			return modelAndView;
		}
		
		List<KeyValues> abouts = new ArrayList<KeyValues>() ;
		String[] args = this.map.get("helpType").toString().split("#");
		for (int i = 0; i < args.length; i++) {
			KeyValues keyValues = new KeyValues() ;
			keyValues.setKey(i) ;
			keyValues.setName(args[i]);
			String filter = "where ftype='"+args[i]+"'";
			keyValues.setValue(this.aboutService.list(0, 0, filter, false)) ;
			abouts.add(keyValues) ;
		}
		
		modelAndView.addObject("abouts", abouts) ;
		modelAndView.addObject("fabout", fabout) ;
		modelAndView.setViewName("front/about/index") ;
		return modelAndView ;
	}
	
	/*@RequestMapping("/about/index1")
	public ModelAndView index1(
			@RequestParam(required=false,defaultValue="1")String id,
			@RequestParam(required=false,defaultValue="0")String symbol
			) throws Exception{
		ModelAndView modelAndView = new ModelAndView() ;
		
		Fabout fabout = this.frontOthersService.findFabout(id) ;
		if(fabout == null){
			modelAndView.setViewName("redirect:/") ;
			return modelAndView;
		}
		
		List<KeyValues> abouts = new ArrayList<KeyValues>() ;
		String[] args = this.map.get("helpType").toString().split("#");
		for (int i = 0; i < args.length; i++) {
			KeyValues keyValues = new KeyValues() ;
			keyValues.setKey(i) ;
			keyValues.setName(args[i]);
			String filter = "where ftype='"+args[i]+"'";
			keyValues.setValue(this.aboutService.list(0, 0, filter, false)) ;
			abouts.add(keyValues) ;
		}
		
		
		Fvirtualcointype fvirtualcointype = null ;

		modelAndView.addObject("abouts", abouts) ;
		modelAndView.addObject("fabout", fabout) ;
		modelAndView.setViewName("front/trade/introduce") ;
		return modelAndView ;
	}*/
	
}
