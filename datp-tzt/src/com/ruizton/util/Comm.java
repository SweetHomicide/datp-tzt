package com.ruizton.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
/*
 * 对comm.properties中的数据进行读取
 */
public final class Comm {
	 private static int MAX_Withdrawals_Time; 
	 private static String WX_NOTIFY_URL;//微信回调地址
	 private static String ALI_NotifyUrl;//支付宝回调地址
	 private static double ALI_PAYNUMBER;//支付宝数值 1为元 0.01为分 
	 private static int WX_PAYNUMBER;//微信和银联数值  100单位为元 1为分
	 private static int MAX_NUMBER;  //交易中心列表查询条数
	 private static int MAX_NUMBERS;
	 private static String FRONTURL;//前台回调地址
	 private static String BACKURL;//后台回调地址
	 private static String MERID;//无跳转商户号
     private static String VIR_CONFIRS;//虚拟币确认数
     private static String URLHEAD;//APP接口传参时的url前缀路径
     private static boolean ISREDIS;//是否开启Redis
     private static int REDIS_OUTTIME;//Redis过期时间  毫秒为单位
     private static int PAGE_NUM;//交易中心分页
     private static int FIRST_PAGE_NUM;
     private static int MAXRESULT;
     private static String RED_PWD;//redis密码
     private static String FTYPE;//帮助分类虚拟币类型
     private static String ISHIDDEN_DEAL;//是否隐藏首页交易行情和交易中心菜单
     private static boolean ISDEAL_OWNBYOWN;//是否可以自己和自己交易
     private static boolean ISHIDDEN_EX;//是否隐藏兑换中心
     private static int EX_PAGE;//兑换中心分页 
     private static boolean ISHIDDEN_CROWDFUNDING;//是否隐藏众筹
     private static boolean ISTRAD_OWNBYOWN;
     private static int FINANCIAL_NUM;//资金账号虚拟币地址管理列表分页
     private static int LIMIT_PAGE;//资产记录详情页面分页
     private static boolean ISVALIDATE;//是否开启短信 图片验证码 邮箱验证码 验证 （提供测试使用）
	    static {   
	        Properties prop = new Properties();   
	        InputStream in = Comm.class.getResourceAsStream("/comm.properties");   
	         try {   
	          prop.load(in);   
	          String MAX = prop.getProperty("MAX_Withdrawals_Time").trim();    
	          MAX_Withdrawals_Time = Integer.valueOf(MAX);
	          WX_NOTIFY_URL=prop.getProperty("WX_NOTIFY_URL").trim(); 
	          ALI_NotifyUrl=prop.getProperty("ALI_NotifyUrl").trim();
	          ALI_PAYNUMBER=Double.parseDouble(prop.getProperty("ALI_PAYNUMBER").trim());
	          WX_PAYNUMBER=Integer.valueOf(prop.getProperty("WX_PAYNUMBER").trim());
	          MAX_NUMBER = Integer.valueOf(prop.getProperty("MAX_NUMBER").trim());
	          MAX_NUMBERS = Integer.valueOf(prop.getProperty("MAX_NUMBERS").trim());
	          FRONTURL=prop.getProperty("FRONTURL").trim();
	          BACKURL=prop.getProperty("BACKURL").trim();
	          MERID=prop.getProperty("MERID").trim();
	          VIR_CONFIRS=prop.getProperty("VIR_CONFIRS").trim();
	          URLHEAD=prop.getProperty("URLHEAD").trim();
	          ISREDIS=Boolean.parseBoolean(prop.getProperty("ISREDIS").trim());
	          REDIS_OUTTIME=Integer.parseInt(prop.getProperty("REDIS_OUTTIME").trim());
	          PAGE_NUM=Integer.parseInt(prop.getProperty("PAGE_NUM").trim());
	          FIRST_PAGE_NUM=Integer.parseInt(prop.getProperty("FIRST_PAGE_NUM").trim());
	          MAXRESULT=Integer.parseInt(prop.getProperty("MAXRESULT").trim());
	          RED_PWD=prop.getProperty("RED_PWD").trim();
	          FTYPE=prop.getProperty("FTYPE").trim();
	          ISHIDDEN_DEAL=prop.getProperty("ISHIDDEN_DEAL").trim();
	          ISDEAL_OWNBYOWN=Boolean.parseBoolean(prop.getProperty("ISDEAL_OWNBYOWN").trim());
	          ISHIDDEN_EX=Boolean.parseBoolean(prop.getProperty("ISHIDDEN_EX").trim());
	          EX_PAGE=Integer.parseInt(prop.getProperty("EX_PAGE").trim());
	          ISHIDDEN_CROWDFUNDING=Boolean.parseBoolean(prop.getProperty("ISHIDDEN_CROWDFUNDING").trim());
	          ISTRAD_OWNBYOWN=Boolean.parseBoolean(prop.getProperty("ISTRAD_OWNBYOWN").trim());
	          FINANCIAL_NUM=Integer.parseInt(prop.getProperty("FINANCIAL_NUM").trim());
	          LIMIT_PAGE=Integer.parseInt(prop.getProperty("LIMIT_PAGE").trim());
	          ISVALIDATE=Boolean.parseBoolean(prop.getProperty("ISVALIDATE").trim());
	         } catch (IOException e) {   
	             e.printStackTrace();   
	         }   
	     }   

	    /**  
	         * 私有构造方法，不需要创建对象  
	      */   
	        private Comm() {   
	        	
	       }

	        public static int times() {
	        	return MAX_Withdrawals_Time;
	}
	        public static String getWX_NOTIFY_URL()
	        {
	        	return WX_NOTIFY_URL;
	        }
	        public static String getALI_NotifyUrl()
	        {
	        	return ALI_NotifyUrl;
	        }
	        public static double getALI_PAYNUMBER()
	        {
	        	return ALI_PAYNUMBER;
	        }
	        public static int getWX_PAYNUMBER()
	        {
	        	return WX_PAYNUMBER;
	        }
	        public static int getMAX_NUMBER()
	        {
	        	return MAX_NUMBER;
	        }
	        public static int getMAX_NUMBERS()
	        {
	        	return MAX_NUMBERS;
	        }
	        public static String getFRONTURL() {
	    		return FRONTURL;
	    	}
	        public static String getFTYPE() {
	    		return  FTYPE;
	    	}
	        public static String getISHIDDEN_DEAL() {
	    		return  ISHIDDEN_DEAL;
	    	}
	    	public static void setFRONTURL(String fRONTURL) {
	    		FRONTURL = fRONTURL;
	    	}

	    	public static String getBACKURL() {
	    		return BACKURL;
	    	}
	    	
	    	/**
	    	 * 
	    	 *  作者：           Dylan
	    	 *  标题：           getISREDIS 
	    	 *  时间：           2018年8月14日
	    	 *  描述：           是否开启redis缓存
	    	 *  
	    	 *  @return true 开启  false 关闭 
	    	 */
	    	public static boolean getISREDIS() {
	    		return ISREDIS;
	    	}
	    	public static String getRED_PWD() {
	    		return RED_PWD;
	    	}
	    	public static int getREDIS_OUTTIME() {
	    		return REDIS_OUTTIME;
	    	}
	    	public static boolean getISDEAL_OWNBYOWN() {
	    		return ISDEAL_OWNBYOWN;
	    	}
	    	public static boolean getISHIDDEN_EX() {
	    		return ISHIDDEN_EX;
	    	}
	    	public static int getEX_PAGE() {
	    		return EX_PAGE;
	    	}	    	
	    	public static boolean getISTRAD_OWNBYOWN() {
	    		return ISTRAD_OWNBYOWN;
	    	}
  
	    	public static boolean getISVALIDATE() {
	    		return ISVALIDATE;
	    	}
  
	    	public static void setBACKURL(String bACKURL) {
	    		BACKURL = bACKURL;
	    	}

	    	public static String getMERID() {
	    		return MERID;
	    	}
	    	public static String getVIR_CONFIRS() {
	    		return VIR_CONFIRS;
	    	}

	    	public static void setMERID(String mERID) {
	    		MERID = mERID;
	    	}

			public static String getURLHEAD() {
				return URLHEAD;
			}

			public static void setURLHEAD(String uRLHEAD) {
				URLHEAD = uRLHEAD;
			}
				
			/**
			 * 
			 *  作者：           Dylan
			 *  标题：           getPAGE_NUM 
			 *  时间：           2018年8月14日
			 *  描述：           交易中心分页 配置在comm.properties文件中  默认为10页
			 *  
			 *  @return int
			 */
			public static int getPAGE_NUM() {
				return PAGE_NUM;
			}
			public static boolean getISHIDDEN_CROWDFUNDING() {
				return ISHIDDEN_CROWDFUNDING;
			}

			public static void setPAGE_NUM(int pAGE_NUM) {
				PAGE_NUM = pAGE_NUM;
			}

			public static int getFIRST_PAGE_NUM() {
				return FIRST_PAGE_NUM;
			}

			public static void setFIRST_PAGE_NUM(int fIRST_PAGE_NUM) {
				FIRST_PAGE_NUM = fIRST_PAGE_NUM;
			}

			public static int getMAXRESULT() {
				return MAXRESULT;
			}

			public static void setMAXRESULT(int mAXRESULT) {
				MAXRESULT = mAXRESULT;
			}

			public static int getFINANCIAL_NUM() {
				return FINANCIAL_NUM;
			}

			public static void setFINANCIAL_NUM(int fINANCIAL_NUM) {
				FINANCIAL_NUM = fINANCIAL_NUM;
			}

			public static int getLIMIT_PAGE() {
				return LIMIT_PAGE;
			}

			public static void setLIMIT_PAGE(int lIMIT_PAGE) {
				LIMIT_PAGE = lIMIT_PAGE;
			}
			
			
	    	

}
