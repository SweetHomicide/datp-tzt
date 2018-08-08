package com.ruizton.main.controller.front;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.ruizton.main.controller.BaseController;

@Controller
public class FrontErrorController extends BaseController {
	
	@RequestMapping("/error/error")
	public ModelAndView error404() throws Exception{
		ModelAndView modelAndView = new ModelAndView() ;
		modelAndView.setViewName("front/error/error") ;
		return modelAndView ;
	}
}
