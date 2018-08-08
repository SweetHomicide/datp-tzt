package com.ruizton.main.service.front;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruizton.main.dao.UtilsDAO;
import com.ruizton.main.model.Fuser;
import com.ruizton.main.model.Fvirtualcaptualoperation;
import com.ruizton.main.model.Fvirtualcointype;

@Service
public class UtilsService {

	@Autowired
	private UtilsDAO utilsDAO ;
	
	public List list(int firstResult, int maxResults, String filter, boolean isFY,Class c,Object... param){
		return this.utilsDAO.findByParam(firstResult, maxResults, filter, isFY, c.getName(),param) ;
	}
	public List list(int firstResult, int maxResults, String filter, boolean isFY,Class c){
		return this.utilsDAO.findByParam(firstResult, maxResults, filter, isFY, c.getName()) ;
	}
	public int count(String filter, Class c){
		return this.utilsDAO.findByParamCount(filter, c.getName()) ;
	}
	public double sum(String filter, String field, Class c){
		return this.utilsDAO.sum(filter, field, c) ;
	}
	
	public List<Fvirtualcaptualoperation> list3(int firstResult, int maxResults, String filter, boolean isFY,Class c){
		List<Fvirtualcaptualoperation> list= this.utilsDAO.findByParam(firstResult, maxResults, filter, isFY, c.getName()) ;
		for (Fvirtualcaptualoperation fvirtualcaptualoperation : list) {
			Fuser fuser = fvirtualcaptualoperation.getFuser() ;
			if(fuser!=null ){
				fuser.getFnickName();
			}
			
			Fvirtualcointype fvirtualcointype = fvirtualcaptualoperation.getFvirtualcointype() ;
			if(fvirtualcointype!=null ){
				fvirtualcointype.getFname() ;
			}
		}
		return list ;
	}
}
