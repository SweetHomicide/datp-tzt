package com.ditp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ditp.entity.Dict;
import com.ditp.service.DictService;

@Controller
@RequestMapping("/Dict")
public class DictController {

	@Autowired
	private DictService dictService;
	
	@RequestMapping("get")
	public String getDictList(){
		try {
			List<Dict> list = dictService.get();
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}
}
