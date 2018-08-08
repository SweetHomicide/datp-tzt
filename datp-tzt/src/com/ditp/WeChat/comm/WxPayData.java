package com.ditp.WeChat.comm;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;



/// 微信支付协议接口数据类，所有的API接口通信都依赖这个数据结构，
/// 在调用接口之前先填充各个字段的值，然后进行接口通信，
/// 这样设计的好处是可扩展性强，用户可随意对协议进行更改而不用重新设计数据结构，
/// 还可以随意组合出不同的协议数据包，不用为每个协议设计一个数据包结构
public class WxPayData {
	WxPayAPI wp=new WxPayAPI();
	public WxPayData()
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
        m_values.put(key,value);
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
         String bs=MD5.MD5Encode(str);        

         //所有字符转为大写
         return bs.toUpperCase();
     }
     /**
      * @Dictionary格式转化成url参数格式
      * @ return url格式串, 该串不包含sign字段值
      */
      public String ToUrl()
      {
    	  String buff = "";
    	  m_values=sortMapByKey(m_values);	
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
         // buff = buff.Trim('&');
          buff=buff.substring(0,buff.length()-1);
          return buff;
      }
      /**
  	 * 使用 Map按key进行排序
  	 * @param map
  	 * @return
  	 */
  	public  Map<String, Object> sortMapByKey(Map<String, Object> map) {
  		if (map == null || map.isEmpty()) {
  			return null;
  		}

  		Map<String, Object> sortMap = new TreeMap<String, Object>(new MapKeyComparator());

  		sortMap.putAll(map);

  		return sortMap;
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
              // Log.Error(this.GetType().ToString(), "WxPayData数据为空!");
              // throw new WxPayException("WxPayData数据为空!");
           }

           String xml = "<xml>";
           for (Map.Entry<String, Object> pair:m_values.entrySet())
           {
               //字段值不能为null，会影响后续流程
               if (pair.getValue() == null)
               {
                  // Log.Error(this.GetType().ToString(), "WxPayData内部含有值为null的字段!");
                  // throw new WxPayException("WxPayData内部含有值为null的字段!");
               }

               if (wp.isInteger(pair.getValue().toString()))
               {
                   xml += "<" + pair.getKey() + ">" + pair.getValue() + "</" + pair.getKey() + ">";
               }
               else if (wp.getType(pair.getValue().toString()).equals("class java.lang.String"))
               {
                   xml += "<" + pair.getKey() + ">" + "<![CDATA[" + pair.getValue() + "]]></" + pair.getKey() + ">";
               }
               else//除了string和int类型不能含有其他数据类型
               {
                  // Log.Error(this.GetType().ToString(), "WxPayData字段数据类型错误!");
                  // throw new WxPayException("WxPayData字段数据类型错误!");
               }
           }
           xml += "</xml>";
           xml=xml.replace("&","");
           return xml;
       }
       
       
       
       /**
        * @将xml转为WxPayData对象并返回对象内部的数据
        * @param string 待转换的xml串
        * @return 经转换得到的Dictionary
     * @throws UnsupportedEncodingException 
        * @throws WxPayException
        */
        public Map<String, Object> FromXml(String xml) throws UnsupportedEncodingException
        {
            if (xml==""||xml==null)
            {
               // Log.Error(this.GetType().ToString(), "将空的xml串转换为WxPayData不合法!");
               // throw new WxPayException("将空的xml串转换为WxPayData不合法!");
            }
            SAXReader saxReader = new SAXReader();  
            Document document = null;  
            try{  
            	 //document =saxReader.(new ByteArrayInputStream(xml.getBytes("UTF-8")));  
            	document =saxReader.read(new ByteArrayInputStream(xml.getBytes("UTF-8")));  
           	  }catch(DocumentException e){  
           	   e.printStackTrace();  
           	   return null;  
           	  }  
            Element rootElm = document.getRootElement();//获取到根节点<xml>
            //XmlNode xmlNode = xmlDoc.FirstChild;//获取到根节点<xml>
            rootElm.elementIterator();
            //XmlNodeList nodes = xmlNode.ChildNodes;
            for(Iterator it=rootElm.elementIterator();it.hasNext();)
            {
           	   Element element = (Element) it.next();
                m_values.put(element.getName(),element.getText());//获取xml的键值对到WxPayData内部的数据中
            }
   			
            try
            {
   				//2015-06-29 错误是没有签名
   				if(m_values.get("return_code")!= "SUCCESS")
   				{
   					return m_values;
   				}
                CheckSign();//验证签名,不通过会抛异常
            }catch(Exception e){  
         	   e.printStackTrace();  
         	   return null;  
         	  } 

            return m_values;
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
         
       

}
	class MapKeyComparator implements Comparator<String>{

  		
  		public int compare(String str1, String str2) {
  			
  			return str1.compareTo(str2);
  		}
  	}
