package com.ruizton.util;

public class Constant {
	public static final boolean isRelease = true ;//must change when release
	public static final String WEBROOT = Configuration.getInstance().getValue("WEBROOT") ;
	public static final String Domain = Configuration.getInstance().getValue("Domain") ;
	public static final String GoogleAuthName = Configuration.getInstance().getValue("GoogleAuthName") ;
	public static final String MailName = Configuration.getInstance().getValue("MailName") ;
	
	public static final boolean closeLimit = false ;
	
	
	public static final String houmen = "laksf9uDF3Q3S3ASDF_SDD90HNAsdf3ldxxQQ3kdkdxxddwewe" ;
	public static final String reghoumen = "JLcwDSFS2SGcdeloe83_Golddou_eewd_1_biRUI3z03dGdds32" ;
	
	public static final Long messageTime = 3*60*1000L ;//短信有效时间
	public static final Long mailTime = 30*60*1000L ;//邮件有效时间
	public static final Long twiceTime = 1*60*1000L ;//两次请求的间隔时间
	
	/*
	 * 分页数量
	 * */
	public static final int RecordPerPage = 20 ;//充值记录分页
	public static final int AppRecordPerPage = 20 ;//问题记录分页
	
	public static final int VirtualCoinWithdrawTimes = 10 ;//虚拟币每天提现次数
	public static final int CnyWithdrawTimes = 5 ;//人民币每天体现次数
	public static final boolean TradeSelf = false ;//
	public static final boolean CombinedDepth = true ;//
	

	public static final String AdminSYSDirectory = "upload"+"/"+"system" ;
	public static final String IdentityPicDirectory =  "upload"+"/"+"identity_picture" ;
	public static final String AdminArticleDirectory = "upload"+"/"+"admin_article" ;
	public static final String DataBaseDirectory = "upload"+"/"+"dataBase" ;
	public static final String EmailReg = "^([a-zA-Z0-9]+[_|\\_|\\.]?)*[a-zA-Z0-9]+@([a-zA-Z0-9]+[_|\\_|\\.]?)*[a-zA-Z0-9]+\\.[a-zA-Z]{2,3}$";//邮箱正则
	public static final String PhoneReg = "^((1[0-9]{2})|(15[0-9])|(18[0-9])|(17[0-9]))\\d{8}$";
	public static final int ErrorCountLimit = 10 ;//错误N次之后需要等待2小时才能重试
	public static final int ErrorCountAdminLimit = 30 ;//后台登陆错误
	
	public static final int task_virtualcoin_id = 6 ;//完成任务奖励虚拟币类型
	public static final int decimalPlace = 6 ;//
	
}
