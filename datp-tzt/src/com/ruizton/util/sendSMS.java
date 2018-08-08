package com.ruizton.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import com.ditp.WeChat.comm.MD5;

/**
 * 文件名称：sendSMS_demo.java
 * 
 * 文件作用：美联软通 http接口使用实例
 * 
 * 创建时间：2012-05-18
 * 
 * 
返回值											说明
success:msgid								提交成功，发送状态请见4.1
error:msgid									提交失败
error:Missing username						用户名为空
error:Missing password						密码为空
error:Missing apikey						APIKEY为空
error:Missing recipient						手机号码为空
error:Missing message content				短信内容为空
error:Account is blocked					帐号被禁用
error:Unrecognized encoding					编码未能识别
error:APIKEY or password error				APIKEY 或密码错误
error:Unauthorized IP address				未授权 IP 地址
error:Account balance is insufficient		余额不足
error:Black keywords is:党中央				屏蔽词
 */

public class sendSMS {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void send(String name,String password,String key,String content,String tel) throws IOException {
		try {
			//发送内容
			
			// 创建StringBuffer对象用来操作字符串
			StringBuffer sb = new StringBuffer("http://m.5c.com.cn/api/send/?");
			
			// APIKEY
			sb.append("apikey="+key);

			//用户名
			sb.append("&username="+name);

			// 向StringBuffer追加密码
			sb.append("&password="+password);

			// 向StringBuffer追加手机号码
			sb.append("&mobile="+tel);

			// 向StringBuffer追加消息内容转URL标准码
			sb.append("&content="+URLEncoder.encode(content,"GBK"));

			// 创建url对象
			URL url = new URL(sb.toString());

			// 打开url连接
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();

			// 设置url请求方式 ‘get’ 或者 ‘post’
			connection.setRequestMethod("POST");

			// 发送
			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

			// 返回发送结果
			String inputline = in.readLine();

			// 输出结果
			System.out.println(inputline);
			in.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	
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
	 */
	public static String sendHbSms(String name,String password,String key,
			String content,String tel,String msgId,String ext ){
		String result = "";
		try {
			// 创建StringBuffer对象用来操作字符串
			StringBuffer sb = new StringBuffer("http://sms.cloud.hbsmservice.com:8080/post_sms.do?");
			
			// 用户id
			sb.append("id="+name);
			//（密码+业务代码）MD5加密码
			/*sb.append("&MD5_td_code="+MD5.MD5Encode(password+key));*/
			sb.append("&MD5_td_code="+key);
//			sb.append("&MD5_td_code="+MD5.MD5Encode(password+key));
			// 向StringBuffer追加密码
			sb.append("&mobile="+tel);
			// 向StringBuffer追加消息内容转URL标准码
			sb.append("&msg_content="+URLEncoder.encode(content,"GBK"));
			// 用户发送短信自己定义的短信id，用于处理群发状态报告，参数名必须填写，参数值可为空
			sb.append("&msg_id="+msgId);
			// 用户自己分配的扩展号。参数名必须填写，参数值可为空。。该参数是显示在接收手机上的主叫尾号，可用于上行信息匹配
			sb.append("&ext="+ext);

			// 创建url对象
			URL url = new URL(sb.toString());

			// 打开url连接
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();

			// 设置url请求方式 ‘get’ 或者 ‘post’
			connection.setRequestMethod("POST");

			// 发送
			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

			// 返回发送结果
			result = in.readLine();
			in.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result ="-1";
		}
		return result;
	}
	public static void test(String name,String pwd){
		String result = "";
		try {
			// 创建StringBuffer对象用来操作字符串
			StringBuffer sb = new StringBuffer("http://sms.cloud.hbsmservice.com:8080/get_balance.do?");
			
			
			// 用户id
			sb.append("id="+name);
			//（密码+业务代码）MD5加密码
			/*sb.append("&MD5_td_code="+MD5.MD5Encode(password+key));*/
			sb.append("&pwd="+pwd);

			// 创建url对象
			URL url = new URL(sb.toString());

			// 打开url连接
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();

			// 设置url请求方式 ‘get’ 或者 ‘post’
			connection.setRequestMethod("POST");

			// 发送
			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

			// 返回发送结果
			result = in.readLine();
			in.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result ="-1";
		}
	}
	public static void main(String[] args){

		test("zj6522", "2566cx");

	}
}
