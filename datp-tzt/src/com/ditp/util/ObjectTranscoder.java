package com.ditp.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;

import com.ditp.AilPayCom.Base64;

public class ObjectTranscoder {
	 public static String serializeToString(Object value) {	        
			// --sep1 序列化为byte
			byte[] data = serialize(value);
			// --sep2 byte转为base64字符串
			String byteString = new String(Base64.encode(data));
			return byteString;
		
	    }
	 public static Object deserializeByString(String value) {	        
			byte[] data = Base64.decode(value);
		     return deserialize(data);
		
	    }
	 public static byte[] serialize(Object value) {    
	        if (value == null) {    
	            throw new NullPointerException("Can't serialize null");    
	        }    
	        byte[] rv=null;    
	        ByteArrayOutputStream bos = null;    
	        ObjectOutputStream os = null;    
	        try {    
	            bos = new ByteArrayOutputStream();    
	            os = new ObjectOutputStream(bos);    
	            os.writeObject(value);    
	            os.close();    
	            bos.close();    
	            rv = bos.toByteArray();  
	        } catch (IOException e) {    
	            throw new IllegalArgumentException("Non-serializable object", e);    
	        } finally {    
	            try {  
	                 if(os!=null)os.close();  
	                 if(bos!=null)bos.close();  
	            }catch (Exception e2) {  
	             e2.printStackTrace();  
	            }    
	        }    
	        return rv;    
	    }    
	
	  
	    public static Object deserialize(byte[] in) {    
	        Object rv=null;    
	        ByteArrayInputStream bis = null;    
	        ObjectInputStream is = null;    
	        try {    
	            if(in != null) {    
	                bis=new ByteArrayInputStream(in);    
	                is=new ObjectInputStream(bis);    
	                rv=is.readObject();    
	                is.close();    
	                bis.close();    
	            }    
	        } catch (Exception e) {    
	            e.printStackTrace();  
	         }finally {    
	             try {  
	                 if(is!=null)is.close();  
	                 if(bis!=null)bis.close();  
	             } catch (Exception e2) {  
	                 e2.printStackTrace();  
	             }  
	         }  
	        return rv;    
	    }   
}
