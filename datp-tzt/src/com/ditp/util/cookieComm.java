package com.ditp.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public  class cookieComm {
	//根据KEY获取cookie
	public static String getCookie(HttpServletRequest request,String key)
	{
		String cookieValue="";
		Cookie[] cookies = request.getCookies();
	    for(Cookie cookie : cookies){
	        if(cookie.getName().equals(key)){
	        	cookieValue = cookie.getValue();
	        }
	     }
	    return cookieValue;
	}
}
