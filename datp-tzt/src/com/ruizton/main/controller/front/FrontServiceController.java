package com.ruizton.main.controller.front;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.ruizton.main.comm.ConstantMap;
import com.ruizton.main.comm.KeyValues;
import com.ruizton.main.controller.BaseController;
import com.ruizton.main.model.Farticle;
import com.ruizton.main.model.Farticletype;
import com.ruizton.main.service.admin.ArticleService;
import com.ruizton.main.service.front.FrontOthersService;

@Controller
public class FrontServiceController extends BaseController {

	@Autowired
	private FrontOthersService frontOthersService ;
	@Autowired
	private ArticleService articleService;
	@Autowired
	private FrontOthersService frontOtherService ;
	@Autowired
	private ConstantMap constantMap;
	@RequestMapping("/service/ourService")
	public ModelAndView ourService(
			@RequestParam(required=false,defaultValue="2")String id,
			@RequestParam(required=false,defaultValue="1")int currentPage
			) throws Exception{//12,5,5
		ModelAndView modelAndView = new ModelAndView() ;
		if(!"1".equals(id) && !"2".equals(id) && !"3".equals(id)){
			id ="2";
		}
		
		int page =10;
		
		List<Farticle> farticles = new ArrayList<Farticle>();
		List<Farticle> farticles2 = this.frontOthersService.findFarticle(id, (currentPage-1)*page, page) ;
		for (Farticle farticle : farticles2) {
			if(farticle.getFurl()==null || "".equals(farticle.getFurl())){
				farticle.setFurl(this.constantMap.getString("newsImage"));
			}
			farticles.add(farticle);
		}
		int total = this.frontOthersService.findFarticleCount(id) ;
		String pagin = generatePagin(total/page+(total%page==0?0:1), currentPage, "/service/ourService.html?id="+id+"&") ;
		String filter = "where farticletype.fid='"+id+"' order by fcreateDate desc";
		List<Farticle> hotsArticles = this.articleService.list(0, 6, filter, true);
		modelAndView.addObject("hotsArticles", hotsArticles) ;
		List<KeyValues> articles = new ArrayList<KeyValues>() ;
		List<Farticletype> farticletypes = this.frontOtherService.findFarticleTypeAll() ;
		for (int i = 0; i < farticletypes.size(); i++) {
			KeyValues keyValues = new KeyValues() ;
			Farticletype farticletype = farticletypes.get(i) ;
			List<Farticle> farticles1= this.frontOtherService.findFarticle(farticletype.getFid(), 0, 6) ;
			keyValues.setKey(farticletype) ;
			keyValues.setValue(farticles1) ;
			articles.add(keyValues) ;
		}
		modelAndView.addObject("articles",articles) ;
		modelAndView.addObject("id",id) ;
		modelAndView.addObject("pagin",pagin) ;
		modelAndView.addObject("farticles", farticles) ;
		modelAndView.setViewName("front/service/index") ;
		return modelAndView ;
	}
	
	
	
	
	@RequestMapping("/service/article")
	public ModelAndView article(
			String id
			) throws Exception{
		ModelAndView modelAndView = new ModelAndView() ;
		Farticle farticle = this.frontOthersService.findFarticleById(id) ;
		if(farticle == null){
			modelAndView.setViewName("redirect:/service/ourService.html") ;
			return modelAndView;
		}
//		farticle.setFcount(farticle.getFcount()+1);
//		try {
//			this.articleService.updateObj(farticle);
//		} catch (Exception e) {}
		
		modelAndView.addObject("farticle", farticle) ;
		
		String filter = "order by fcreateDate desc";
		List<Farticle> hotsArticles = this.articleService.list(0, 6, filter, true);
		modelAndView.addObject("hotsArticles", hotsArticles) ;
		
		modelAndView.setViewName("front/service/article") ;
		return modelAndView ;
	}
}
