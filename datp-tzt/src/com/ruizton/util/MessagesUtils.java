package com.ruizton.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.httpclient.HttpClient;

import com.mysql.jdbc.Util;

public class MessagesUtils {
	
	/*
	 * 100			发送成功
		101			验证失败
		102			手机号码格式不正确
		103			会员级别不够
		104			内容未审核
		105			内容过多
		106			账户余额不足
		107			Ip受限
		108			手机号码发送太频繁
		120			系统升级
	 * 
	 * */
    public static int send(String name,String password,String key,String phone,String content){
    	try {
//			sendSMS.send(name,password,key,content,phone) ;
            String msg = URLEncoder.encode(content, "GB2312");

            String sms = "http://api.106txt.com/smsGBK.aspx?action=Send&username=" + name
                    + "&password=" + password + "&gwid=" + key + "&mobile="+phone+"&message=" + msg;

            System.out.println(sms);
            URL yahoo = new URL(sms);
            BufferedReader in = new BufferedReader(new InputStreamReader(yahoo.openStream(),"GB2312"));

            String inputLine;

            while ((inputLine = in.readLine()) != null)
                System.out.println(inputLine);

            in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return 1 ;
    	/*
    	int retCode = ReturnCode.FAIL ;
    	try {
    		String u = "http://sdk2.zucp.net:8060/z_mdsmssend.aspx?sn="+name.trim()+"&pwd="+password.trim()+"&mobile="+phone.trim()+"&content="+URLEncoder.encode(content,"GB2312")+"&ext=&rrid=&stime=";
    		URL url = new URL(u) ;
			System.out.println(url.toString());
    		BufferedReader br = new BufferedReader(new InputStreamReader( url.openStream()) ) ;
			StringBuffer sb = new StringBuffer() ;
			String tmp = null ;
			while((tmp=br.readLine())!=null){
				sb.append(tmp) ;
			}
			if(sb.toString().indexOf("100")!=-1){
				retCode = ReturnCode.SUCCESS ;
			}
			System.out.println(sb.toString());
			br.close() ;
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return retCode ;
    */}
    
//    public static void main(String args[]) throws Exception{
//    	System.out.println(MessagesUtils.send("SDK-WSS-010-08603", "204E814BC12E600A9D202119AD4A286A", "15017549972", "您的验证码是："+33333+"。请不要把验证码泄露给其他人。如非本人操作，可不用理会！"));
//    }
    
    /**
	 * hbsmservicer短信发送
	 * @param name 用户id
	 * @param password  密码
	 * @param key  业务代码
	 * @param content  短信内容
	 * @param tel  手机号码
	 * @param msgId  短信id
	 * @param ext 用户自己分配的扩展号
	 * @return String 
	 *  0#数字   提交成功的手机数
		-10       余额不足
		-11       账号关闭
		-12       短信内容超过1000字（包括1000字）或为空
		-13       手机号码超过200个或合法的手机号码为空，或者手机号码与通道代码正则不匹配
		-14       msg_id超过50个字符或没有传msg_id字段
		-16       用户名不存在
		-18       访问ip错误
		-19       密码错误 或者业务代码错误 或者通道关闭 或者业务关闭
		-20       小号错误
		 9       访问地址不存在
		 -1   本平台调用异常
	 * @throws IOException
	 */
    public static String send(String name,String password,String key,String tel,
    		String content,String msgId,String ext){
    	return sendSMS.sendHbSms(name,password,key,content,tel,msgId,ext) ;
    }
    
    /**
     * 
     *  作者：           Dylan
     *  标题：           sendM5C 
     *  时间：           2018年8月13日
     *  描述：          发送短信    
     *  平台服务账号密码可用  相关数据库表【fsystemargs】中进行配置 
     *  messageName = klzl
     *  messagePassword = ASD12345
     *  messageKey = e4104d82b6dca2329c32b9c08e741421
     *  不抛异常则正常发送 成功状态
     *  @param name 短信平台账号 : klzl
     *  @param password 短信平台密码：  ASD12345
     *  @param key  短信平台key :e4104d82b6dca2329c32b9c08e741421
     *  @param content  内容
     *  @param tel    手机
     */
    public static void sendM5C(String name, String password, String key, String content, String tel) {
    	try {
			sendSMS.send(name, password, key, content, tel);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
}
