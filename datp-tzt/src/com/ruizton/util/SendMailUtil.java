package com.ruizton.util;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

public class SendMailUtil {
	public static boolean send(String smtp, String name, String password,
			String toAddress, String title, String content) {
		boolean flag = false ;
		try {
			Properties props = new Properties();
			props.setProperty("mail.transport.protocol", "smtp");
			props.setProperty("mail.smtp.auth", "true");
			props.setProperty("mail.smtp.localhost", "localHostAdress");
			Session session = Session.getInstance(props);
			session.setDebug(true);
			Transport transport = session.getTransport();
			transport.connect(smtp.trim(), name.trim(), password.trim());

			// 设置邮件内容
			MimeMultipart contents = new MimeMultipart();
			
			Message message = new MimeMessage(session);
			message.setSentDate(new Date());
			 String nick="";  
		        try {  
		            nick=javax.mail.internet.MimeUtility.encodeText(Constant.MailName);  
		        } catch (UnsupportedEncodingException e) {  
		            e.printStackTrace();  
		        }   
	        message.setFrom(new InternetAddress(nick+" <"+name+">")); 
	        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toAddress));  
			message.setSubject(MimeUtility.encodeText(title, "gb2312", "b"));

			//正文HTML
			MimeBodyPart body = new MimeBodyPart() ;
			body.setContent(content, "text/html;charset=gb2312") ;
			contents.addBodyPart(body) ;
			
			message.setContent(contents);

			
			List<String> emails = new ArrayList<String>() ;
			emails.add(toAddress) ;
			transport.sendMessage(message, InternetAddress.parse(emails.toString().replace("[", "").replace("]", ""))) ;
			transport.close();
			
			flag = true ;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag ;
	}
	
	public static void main(String[] args) {
		//send("mx7.dns.com.cn", "admin@xiehee.com", "liguangcs22", "1456019685@qq.com", "test", "test...");
		send("email-smtp.us-west-2.amazonaws.com", "AKIAI32XDGGGA4EB4E2A", "AqSXSc8nWjp/QnZ0glL2IXk7oY8TR0cw9RNbyQab7cg8", "support@iexchg.com", "", "liu_qing_2010@foxmail.com", "test", "test...");
	}

	public static boolean send(String smtp, String smtpUser, String smtpPassword, String fromAccount,
			String fromPassword, String toAccount, String emailTitle, String emailContent) {
		boolean flag = false;
		try {
			Properties props = new Properties();
			props.setProperty("mail.transport.protocol", "smtp");
			props.setProperty("mail.smtp.auth", "true");
			props.setProperty("mail.smtp.localhost", "localHostAdress");
			Session session = Session.getInstance(props);
			session.setDebug(true);
			Transport transport = session.getTransport();
			transport.connect(smtp.trim(), smtpUser.trim(), smtpPassword.trim());

			// 设置邮件内容
			MimeMultipart contents = new MimeMultipart();

			Message message = new MimeMessage(session);
			message.setSentDate(new Date());
			String nick = "";
			try {
				nick = javax.mail.internet.MimeUtility.encodeText(Constant.MailName);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			message.setFrom(new InternetAddress(nick + " <" + fromAccount + ">"));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toAccount));
			message.setSubject(MimeUtility.encodeText(emailTitle, "gb2312", "b"));

			// 正文HTML
			MimeBodyPart body = new MimeBodyPart();
			body.setContent(emailContent, "text/html;charset=gb2312");
			contents.addBodyPart(body);

			message.setContent(contents);

			List<String> emails = new ArrayList<String>();
			emails.add(toAccount);
			transport.sendMessage(message, InternetAddress.parse(emails.toString().replace("[", "").replace("]", "")));
			transport.close();

			flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}
}
