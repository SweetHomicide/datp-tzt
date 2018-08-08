package com.ruizton.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HTMLSpirit {
    
    private static final String regEx_script = "<script[^>]*?>[\\s\\S]*?<\\/script>"; // 定义script的正则表达式
    private static final String regEx_style = "<style[^>]*?>[\\s\\S]*?<\\/style>"; // 定义style的正则表达式
    private static final String regEx_html = "<[^>]+>"; // 定义HTML标签的正则表达式
    private static final String regEx="[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]"; 
    private static final String regEx_special = "\\&[a-zA-Z]{1,10};";
    
    public static String delHTMLTag1(String htmlStr) {
    Pattern p = Pattern.compile(regEx_special); 
    Matcher m1 = p.matcher(htmlStr);
    Pattern p1 = Pattern.compile(regEx); 
    Matcher m = p1.matcher(m1.replaceAll("").trim());
    return m.replaceAll("").trim();
    }
    public static String delHTMLTag(String htmlStr) {
    	
        Pattern p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
        Matcher m_script = p_script.matcher(htmlStr);
        htmlStr = m_script.replaceAll(""); // 过滤script标签

        Pattern p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
        Matcher m_style = p_style.matcher(htmlStr);
        htmlStr = m_style.replaceAll(""); // 过滤style标签

        Pattern p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
        Matcher m_html = p_html.matcher(htmlStr);
        htmlStr = m_html.replaceAll(""); // 过滤html标签
        
        Pattern p_nbsp = Pattern.compile(regEx_special, Pattern.CASE_INSENSITIVE); 
        Matcher m_nbsp = p_nbsp.matcher(htmlStr);
        htmlStr = m_nbsp.replaceAll("");
        return htmlStr.trim(); // 返回文本字符串
    }
    public static void main(String[] args) {
		String s = "<html><a href='sssssss'>&nbsp;&nbsp;&nbsp;&nbsp; &&&1234</a>wqer</br>qwewrer</html>";
		System.out.println(s);
		String delHTMLTag = delHTMLTag(s);
		System.out.println(delHTMLTag);
		
	}
    
}