package com.ditp.WeChat.comm;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;



public class WxPayAPI {
	
	///  liuruichen
	///  2016-11-29
    /// <summary>
    /// 微信支付协议接口数据类，所有的API接口通信都依赖这个数据结构，
    /// 在调用接口之前先填充各个字段的值，然后进行接口通信，
    /// 这样设计的好处是可扩展性强，用户可随意对协议进行更改而不用重新设计数据结构，
    /// 还可以随意组合出不同的协议数据包，不用为每个协议设计一个数据包结构
    public void WxPayData()
    {    

    }
    
    //采用排序的Dictionary的好处是方便对数据包进行签名，不用再签名之前再做一次排序
   Map<String,Object>  m_values=new HashMap<String,Object>();
   
   /**
   * 设置某个字段的值
   * @param key 字段名
    * @param value 字段值
   */
   public void SetValue(String key, Object value)
   {
	   m_values.put(key, value);
   }
    
   /**
   * 根据字段名获取某个字段的值
   * @param key 字段名
    * @return key对应的字段值
   */
   public Object GetValue(String key)
   {
	   Object o = null;
       o=m_values.get(key);
       return o;
   }
   /**
    * 判断某个字段是否已设置
    * @param key 字段名
    * @return 若字段key已被设置，则返回true，否则返回false
    */
   public boolean IsSet(String key)
   {
       Object o = null;
       o=m_values.get(key);
       if (null != o)
           return true;
       else
           return false;
   }
   
   
   /**
    * @将Dictionary转成xml
    * @return 经转换得到的xml串
    * @throws WxPayException
    **/
    public String ToXml()
    {
        //数据为空时不能转化为xml格式
        if (0 == m_values.size())
        {
            //Log.Error(this.GetType().ToString(), "WxPayData数据为空!");
            //throw new WxPayException("WxPayData数据为空!");
        }

        String xml = "<xml>";
        for(Map.Entry<String, Object>  pair:m_values.entrySet())       
        {
            //字段值不能为null，会影响后续流程
            if (pair == null)
            {
               // Log.Error(this.GetType().ToString(), "WxPayData内部含有值为null的字段!");
                //throw new WxPayException("WxPayData内部含有值为null的字段!");
            	Integer.parseInt("");
            }

            if (isInteger(pair.getValue().toString()))
            {
                xml += "<" + pair.getKey() + ">" + pair.getValue() + "</" + pair.getKey() + ">";
            }
            else if (getType(pair) == "String")
            {
                xml += "<" + pair.getKey() + ">" + "<![CDATA[" + pair.getKey() + "]]></" + pair.getKey() + ">";
            }
            else//除了string和int类型不能含有其他数据类型
            {
                //Log.Error(this.GetType().ToString(), "WxPayData字段数据类型错误!");
                //throw new WxPayException("WxPayData字段数据类型错误!");
            }
        }
        xml += "</xml>";
        return xml;
    }
    
    
   
    
     /**
      * 
      * 检测签名是否正确
      * 正确返回true，错误抛异常
      */
      public boolean CheckSign()
      {
          //如果没有设置签名，则跳过检测
          if (!IsSet("sign"))
          {
             //Log.Error(this.GetType().ToString(), "WxPayData签名存在但不合法!");
             //throw new WxPayException("WxPayData签名存在但不合法!");
          }
          //如果设置了签名但是签名为空，则抛异常
          else if(GetValue("sign") == null || GetValue("sign").toString() == "")
          {
             // Log.Error(this.GetType().ToString(), "WxPayData签名存在但不合法!");
            //  throw new WxPayException("WxPayData签名存在但不合法!");
          }

          //获取接收到的签名
          String return_sign = GetValue("sign").toString();

          //在本地计算新的签名
          String cal_sign = MakeSign();

          if (cal_sign == return_sign)
          {
              return true;
         }
return true;
         //Log.Error(this.GetType().ToString(), "WxPayData签名验证错误!");
        //  throw new WxPayException("WxPayData签名验证错误!");
      }
      
      /**
      * @生成签名，详见签名生成算法
      * @return 签名, sign字段不参加签名
      */
      public String MakeSign()
      {
          //转url格式
    	  String str = ToUrl();
          //在string后加入API KEY
          str += "&key=" + WxPayConfig.KEY;
          //MD5加密
          String md5 = MD5.MD5Encode(str);

          //foreach (byte b in bs)
         // {
          //    sb.Append(b.ToString("x2"));
         // }
          //所有字符转为大写
          return md5.toString().toUpperCase();
      }
      
      /**
       * @Dictionary格式转化成url参数格式
       * @ return url格式串, 该串不包含sign字段值
       */
       public String ToUrl()
       {
    	   String buff = "";
           for (Map.Entry<String, Object> pair:m_values.entrySet())
           {
               if (pair.getValue() == null)
               {
                  // Log.Error(this.GetType().ToString(), "WxPayData内部含有值为null的字段!");
                  // throw new WxPayException("WxPayData内部含有值为null的字段!");
               }

               if (pair.getKey() != "sign" && pair.getValue().toString() != "")
               {
                   buff += pair.getKey() + "=" + pair.getValue() + "&";
               }
           }
           buff = buff.trim();
           return buff;
       }

    /**
     * 获取值类型
     * @param o
     * @return
     */
    public  String getType(Object o){
    	return o.getClass().toString();
    	}
    
    /**
     * 判断字符串是否是整数
     */
    public  boolean isInteger(String value) {
     try {
      Integer.parseInt(value);
      return true;
     } catch (NumberFormatException e) {
      return false;
     }
    }

    /**
    * 根据当前系统时间加随机序列来生成订单号
     * @return 订单号
    */
    public static String GenerateOutTradeNo()
    {
    	String ran=WxPayConfig.MCHID+getNowDate()+((int)(Math.random()*999)+100);

    	return ran;
    }
    
    /**
     * 获取当前时间字符串
     * @return
     */
    public static String getNowDate()
    {
    	Date now = new Date();
    	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");//可以方便地修改日期格式
    	String dateNow = dateFormat.format(now); 
    	return dateNow;
    }
    /**
     *模拟交易结束时间
     * @return
     */
    public static String getNowDateAfter()
    {
    	Date now = new Date();
    	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");//可以方便地修改日期格式
    	String dateNow = dateFormat.format(now.getTime() + 300000); 
    	return dateNow;
    }
    /**
     * 
     * 统一下单
     * @param WxPaydata inputObj 提交给统一下单API的参数
     * @param int timeOut 超时时间
     * @throws WxPayException
     * @return 成功时返回，其他抛异常
     * @throws UnsupportedEncodingException 
     */
     public static WxPayData UnifiedOrder (WxPayData inputObj, int timeOut) throws UnsupportedEncodingException
     {
         String url = "https://api.mch.weixin.qq.com/pay/unifiedorder";
         //检测必填参数
         if (!inputObj.IsSet("out_trade_no"))
         {
             //throw new WxPayException("缺少统一支付接口必填参数out_trade_no！");
         }
         else if (!inputObj.IsSet("body"))
         {
             //throw new WxPayException("缺少统一支付接口必填参数body！");
         }
         else if (!inputObj.IsSet("total_fee"))
         {
             //throw new WxPayException("缺少统一支付接口必填参数total_fee！");
         }
         else if (!inputObj.IsSet("trade_type"))
         {
             //throw new WxPayException("缺少统一支付接口必填参数trade_type！");
         }

         //关联参数
         if (inputObj.GetValue("trade_type").toString() == "JSAPI" && !inputObj.IsSet("openid"))
         {
           //  throw new WxPayException("统一支付接口中，缺少必填参数openid！trade_type为JSAPI时，openid为必填参数！");
         }
         if (inputObj.GetValue("trade_type").toString() == "NATIVE" && !inputObj.IsSet("product_id"))
         {
             //throw new WxPayException("统一支付接口中，缺少必填参数product_id！trade_type为JSAPI时，product_id为必填参数！");
         }

         //异步通知url未设置，则使用配置文件中的url
         if (!inputObj.IsSet("notify_url"))
         {
             inputObj.SetValue("notify_url", WxPayConfig.NOTIFY_URL);//异步通知url
         }

         inputObj.SetValue("appid", WxPayConfig.APPID);//公众账号ID
         inputObj.SetValue("mch_id", WxPayConfig.MCHID);//商户号
         inputObj.SetValue("spbill_create_ip", WxPayConfig.IP);//终端ip	  	    
         inputObj.SetValue("nonce_str", GenerateNonceStr());//随机字符串
         //liuruichen

         //签名
         inputObj.SetValue("sign", inputObj.MakeSign());
       String xml = inputObj.ToXml();
         try{
         String response=HttpWXPost.sendPost(url, xml);

         WxPayData result = new WxPayData();
         result.FromXml(response);        
         return result;
       	}catch(Exception e){
       		WxPayData result = new WxPayData();
       		return result;
       	}
     }
     

     /**
      *    
      * 查询订单
      * @param WxPayData inputObj 提交给查询订单API的参数
      * @param int timeOut 超时时间
      * @throws WxPayException
      * @return 成功时返回订单查询结果，其他抛异常
      */
      public static WxPayData OrderQuery(WxPayData inputObj)
      {
          try {
			String url = "https://api.mch.weixin.qq.com/pay/orderquery";
			  //检测必填参数
			  if (!inputObj.IsSet("out_trade_no") && !inputObj.IsSet("transaction_id"))
			  {
			    //  throw new WxPayException("订单查询接口中，out_trade_no、transaction_id至少填一个！");
			  }

			  inputObj.SetValue("appid", WxPayConfig.APPID);//公众账号ID
			  inputObj.SetValue("mch_id", WxPayConfig.MCHID);//商户号
			  inputObj.SetValue("nonce_str", GenerateNonceStr());//随机字符串
			  inputObj.SetValue("sign", inputObj.MakeSign());//签名

			  String xml = inputObj.ToXml();			
			  String start = getNowDate();

			  String response = HttpWXPost.sendPost(url, xml);//调用HTTP通信接口提交数据


			  String end =  getNowDate();
			 // int timeCost = (int)((end - start).TotalMilliseconds);//获得接口耗时

			  //将xml格式的数据转化为对象以返回
			  WxPayData result = new WxPayData();
			  result.FromXml(response);

			  return result;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
      }

     
     
     /**
     * 生成随机串，随机串包含字母或数字
     * @return 随机串
     */
     public static String GenerateNonceStr()
     {   	 
    	 String uuid = UUID.randomUUID().toString();
    	  uuid=uuid.replace("-","");
    	  return uuid;  
     }
     
     



}
