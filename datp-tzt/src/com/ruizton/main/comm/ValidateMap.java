package com.ruizton.main.comm;

import java.util.HashMap;
import java.util.Map;

import com.ruizton.main.model.Emailvalidate;

public class ValidateMap {
	private Map<String, MessageValidate> messageMap = new HashMap<String, MessageValidate>() ;
	private Map<String, Emailvalidate> mailMap = new HashMap<String, Emailvalidate>() ;
	
	//短信
	public synchronized void putMessageMap(String key,MessageValidate messageValidate){
		MessageValidate put = this.messageMap.put(key, messageValidate) ;
		//System.out.println(put.getAreaCode()+" "+put.getCode()+" "+put.getPhone());
	}
	
	public MessageValidate getMessageMap(String key){
		return this.messageMap.get(key) ;
	}
	public void removeMessageMap(String key){
		if(this.messageMap.isEmpty()) return;
		if(!this.messageMap.containsKey(key)) return;
		this.messageMap.remove(key) ;
	}
	
	//邮件
	public synchronized void putMailMap(String key,Emailvalidate messageValidate){
		this.mailMap.put(key, messageValidate) ;
	}
	
	public Emailvalidate getMailMap(String key){
		return this.mailMap.get(key) ;
	}
	public void removeMailMap(String key){
		if(this.mailMap.isEmpty()) return;
		if(!this.mailMap.containsKey(key)) return;
		this.mailMap.remove(key) ;
	}
}
