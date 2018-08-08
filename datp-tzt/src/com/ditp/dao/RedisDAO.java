package com.ditp.dao;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.ditp.redis.JedisclientSingle;
import com.qq.connect.utils.json.JSONException;
import com.qq.connect.utils.json.JSONObject;
import com.ruizton.main.model.Fuser;
import com.ruizton.main.model.Fwallet;
import com.ruizton.util.Comm;

@Repository
public class RedisDAO {

	private static Logger logger = LoggerFactory.getLogger(RedisDAO.class);
	/**
	 * shiro-redis的session对象前缀
	 */
	private final String REDIS_SESSION_PRE = "redis_session:";
    //@Autowired
	private JedisclientSingle redisManager;
	private int timeOut = Comm.getREDIS_OUTTIME();//

	public void update(String sessionid, String userName, HttpServletResponse response) {
		// 把sessionid 存到coolie
		Cookie cookie = new Cookie("sessionId", sessionid);
		cookie.setPath("/");
		response.addCookie(cookie);

		this.save(sessionid, userName);
	}

	// 更新用户信息
	public void updateFuser(Fuser fuser) {
		try {
			String key = "fuser"+fuser.getFid();
			redisManager.hset(key, "fid", fuser.getFid());
			// redisManager.hset(fuser.getFid(), "fwallet", fuser.getFwallet());
			saveRedis("fwallet"+fuser.getFid(), fuser.getFwallet());
			redisManager.hset(key, "floginName", fuser.getFloginName());
			//redisManager.hset(key, "floginPassword", fuser.getFloginPassword());
			//redisManager.hset(key, "ftradePassword", fuser.getFtradePassword());
			redisManager.hset(key, "fnickName", fuser.getFnickName());
			redisManager.hset(key, "frealName", fuser.getFrealName());
			//redisManager.hset(key, "fareaCode", fuser.getFareaCode());
			redisManager.hset(key, "ftelephone", fuser.getFtelephone());
			redisManager.hset(key, "fisTelephoneBind", String.valueOf(fuser.isFisTelephoneBind()));
			redisManager.hset(key, "femail", fuser.getFemail());
			redisManager.hset(key, "fidentityType", String.valueOf(fuser.getFidentityType()));
			// redisManager.hset(key, "fidentityType_s",
			// fuser.getFidentityType_s());
			redisManager.hset(key, "fidentityNo", fuser.getFidentityNo());
			//redisManager.hset(key, "fidentityNo_s", fuser.getFidentityNo_s());
			redisManager.hset(key, "fregisterTime", fuser.getFregisterTime().toString());
			redisManager.hset(key, "flastLoginTime", fuser.getFlastLoginTime().toString());
			redisManager.hset(key, "flastUpdateTime", fuser.getFlastUpdateTime().toString());
			redisManager.hset(key, "fgoogleAuthenticator", fuser.getFgoogleAuthenticator());
			redisManager.hset(key, "fgoogleurl", fuser.getFgoogleurl());
			redisManager.hset(key, "fgoogleBind", String.valueOf(fuser.getFgoogleBind()));
			redisManager.hset(key, "fstatus", String.valueOf(fuser.getFstatus()));
			redisManager.hset(key, "fstatus_s", fuser.getFstatus_s());
			redisManager.hset(key, "fisTelValidate", String.valueOf(fuser.getFisTelValidate()));
			redisManager.hset(key, "fisMailValidate", String.valueOf(fuser.getFisMailValidate()));
			redisManager.hset(key, "fgoogleValidate", String.valueOf(fuser.getFgoogleValidate()));
			redisManager.hset(key, "fpostRealValidate", String.valueOf(fuser.getFpostRealValidate()));
			redisManager.hset(key, "fhasRealValidate", String.valueOf(fuser.getFhasRealValidate()));
			redisManager.hset(key, "fIdentityPath", fuser.getfIdentityPath());
			redisManager.hset(key, "fIdentityPath2", fuser.getfIdentityPath2());
			redisManager.hset(key, "fpostRealValidateTime", fuser.getFpostRealValidateTime().toString());
			redisManager.hset(key, "fhasRealValidateTime",
					fuser.getFhasRealValidateTime() == null ? "" : fuser.getFhasRealValidateTime().toString());
			redisManager.hset(key, "fopenTelValidate", String.valueOf(fuser.getFopenTelValidate()));
			redisManager.hset(key, "fopenGoogleValidate", String.valueOf(fuser.getFopenGoogleValidate()));
			redisManager.hset(key, "fopenSecondValidate", String.valueOf(fuser.getFopenSecondValidate()));
			// redisManager.hset(key, "fscore", fuser.getFscore());
			// redisManager.hset(key, "fapi", fuser.getFapi());
			// redisManager.hset(key, "fIntroUser_id",
			// fuser.getFIntroUser_id());
			redisManager.hset(key, "qqlogin", fuser.getQqlogin());
			redisManager.hset(key, "fInvalidateIntroCount", String.valueOf(fuser.getfInvalidateIntroCount()));
			// redisManager.hset(key, "fusersetting", fuser.getFusersetting());

			// JSONArray array = JSONArray.fromObject(fuser);
			// String jsonstr = array.toString();
			// redisManager.set(fuser.getFid(), jsonstr);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 获取用户
	 * 
	 * @param sessionID
	 * @return
	 */
	public Fuser getFuser(String sessionID) {
		Fuser fuser = null;
		Fwallet fwallet = null;
		if (sessionID != null && !sessionID.equals("")) {
			// 获取用户前判断 是否登录
			if (isLogin(sessionID)) {
				String key =  "fuser"+sessionID;
				try {
					Map<String, String> fuserMap = new HashMap<String, String>();
					Map<String, String> fwalletMap = new HashMap<String, String>();
					fuserMap = redisManager.hgetall(key);
					fwalletMap = redisManager.hgetall( "fwallet"+sessionID);
					fuser = (Fuser) convertMap(Fuser.class, fuserMap);
					fwallet = (Fwallet) convertMap(Fwallet.class, fwalletMap);
					fuser.setFwallet(fwallet);
					return fuser;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					return fuser;
				}

			}
			return fuser;
		} else {
			return fuser;
		}
	}
	/**
	 * 获取用户
	 * 
	 * @param sessionID
	 * @return
	 */
	public Map<String, String> getFuserMap(String sessionID) {
		Map<String, String> fuserMap=null;
		if (sessionID != null && !sessionID.equals("")) {
			// 获取用户前判断 是否登录
			if (isLogin(sessionID)) {
				String key = "fuser"+sessionID;
				try {
					 fuserMap = new HashMap<String, String>();
					Map<String, String> fwalletMap = new HashMap<String, String>();
					fuserMap = redisManager.hgetall(key);
					//fwalletMap = redisManager.hgetall(sessionID + "fwallet");
					return fuserMap;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					return fuserMap;
				}

			}
			return fuserMap;
		} else {
			return fuserMap;
		}
	}

	/**
	 * 给对象赋值
	 * 
	 * @param type
	 * @param map
	 * @return
	 * @throws IntrospectionException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws InvocationTargetException
	 */
	public static Object convertMap(Class type, Map map)
			throws IntrospectionException, IllegalAccessException, InstantiationException, InvocationTargetException {
		BeanInfo beanInfo = Introspector.getBeanInfo(type); // 获取类属性
		Object obj = type.newInstance(); // 创建 JavaBean 对象

		// 给 JavaBean 对象的属性赋值
		PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
		// System.out.println("----------------begin----------");
		for (int i = 0; i < propertyDescriptors.length; i++) {
			PropertyDescriptor descriptor = propertyDescriptors[i];
			String propertyName = descriptor.getName();

			if (map.containsKey(propertyName)) {
				// 下面一句可以 try 起来，这样当一个属性赋值失败的时候就不会影响其他属性赋值。
				try {

					Object value = map.get(propertyName);
					if (descriptor.getPropertyType() == Timestamp.class) {
						if (!value.equals("")) {
							value = Timestamp.valueOf(value.toString());
						} else {
							value = null;
						}
					}
					if (descriptor.getPropertyType() == int.class) {
						if (value != null && !value.equals("")) {
							value = Integer.parseInt(value.toString());
						} else {
							value = 0;
						}
					}
					if (descriptor.getPropertyType() == boolean.class) {
						if (!value.equals("")) {
							value = Boolean.parseBoolean(value.toString());
						} else {
							value = true;
						}
					}
					if (descriptor.getPropertyType() == double.class) {
						if (!value.equals("")) {
							value = Double.parseDouble(value.toString());
						} else {
							value = 0.00;
						}
					}
					Object[] args = new Object[1];
					args[0] = value;
					descriptor.getWriteMethod().invoke(obj, args);
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					// e.printStackTrace();
					System.out.println("异常：" + descriptor.getPropertyType());
					System.out.println(propertyName);
				}
			}
		}
		// System.out.println("------------------end----------");
		return obj;
	}

	/**
	 * 给对象赋值
	 * 
	 * @param type
	 * @param map
	 * @return
	 * @throws IntrospectionException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws InvocationTargetException
	 * @throws JSONException
	 */
	public static Object convertBean(Class type, String jsonString)
			throws IntrospectionException, InstantiationException, IllegalAccessException {
		BeanInfo beanInfo = Introspector.getBeanInfo(type); // 获取类属性
		Object obj = type.newInstance(); // 创建 JavaBean 对象

		try {
			// 给 JavaBean 对象的属性赋值
			PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
			// System.out.println("----------------begin----------");
			JSONObject jsonObject = new JSONObject(jsonString);
			Iterator iterator = jsonObject.keys();
			while (iterator.hasNext()) {
				String key = (String) iterator.next();
				for (int i = 0; i < propertyDescriptors.length; i++) {
					PropertyDescriptor descriptor = propertyDescriptors[i];
					String propertyName = descriptor.getName();
					Object value = jsonObject.getString(key);
					if (key.equals(propertyName)) {
						// 下面一句可以 try 起来，这样当一个属性赋值失败的时候就不会影响其他属性赋值。
						try {

							// Object value = map.get(propertyName);
							if (descriptor.getPropertyType() == Timestamp.class) {
								if (!value.equals("")) {
									value = Timestamp.valueOf(value.toString());
								} else {
									value = null;
								}
							}
							if (descriptor.getPropertyType() == int.class) {
								if (value != null && !value.equals("")) {
									value = Integer.parseInt(value.toString());
								} else {
									value = 0;
								}
							}
							if (descriptor.getPropertyType() == boolean.class) {
								if (!value.equals("")) {
									value = Boolean.parseBoolean(value.toString());
								} else {
									value = true;
								}
							}
							if (descriptor.getPropertyType() == double.class) {
								if (!value.equals("")) {
									value = Double.parseDouble(value.toString());
								} else {
									value = 0.00;
								}
							}
							if (descriptor.getPropertyType() == Double.class) {
								if (!value.equals("")) {
									value = Double.valueOf(value.toString());
								} else {
									value = 0.00;
								}
							}
							//
							// System.out.println(descriptor.getPropertyType());
							Object[] args = new Object[1];
							args[0] = value;
							descriptor.getWriteMethod().invoke(obj, args);
						} catch (IllegalArgumentException e) {
							// TODO Auto-generated catch block
							// e.printStackTrace();
							System.out.println("异常：" + descriptor.getPropertyType());
							System.out.println(propertyName);
						}
					}
				}

			}
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block

			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// System.out.println("------------------end----------");
		return obj;
	}

	/**
	 * 保存到redis
	 * 
	 * @param key
	 * @param bean
	 * @throws IntrospectionException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public void saveRedis(String key, Object bean)
			throws IntrospectionException, InstantiationException, IllegalAccessException, InvocationTargetException {
		// 得到类对象
		Field[] fs = bean.getClass().getDeclaredFields();
		for (int i = 0; i < fs.length; i++) {
			Field f = fs[i];
			f.setAccessible(true); // 设置些属性是可以访问的
			Object val = f.get(bean);// 得到此属性的值
			// System.out.println("name:"+f.getName()+"\t value = "+val);
			String type = f.getType().toString();// 得到此属性的类型
			// System.out.println("####"+f.getType().toString()+"\t");
			try {
				if (!type.equals("interface java.util.Set")) {
					redisManager.hset(key, f.getName(), val.toString());
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
				// System.out.println("异常name:" + f.getName() + "\t value = " +
				// val);
				// System.out.println("####" + f.getType().toString() + "\t");
			}

		}

	}

	public void saveListRedis(String key, List<Object> Listbean)
			throws IntrospectionException, InstantiationException, IllegalAccessException, InvocationTargetException {
		// 得到类对象
		for (Object bean : Listbean) {

			Field[] fs = bean.getClass().getDeclaredFields();
			for (int i = 0; i < fs.length; i++) {
				Field f = fs[i];
				f.setAccessible(true); // 设置些属性是可以访问的
				Object val = f.get(bean);// 得到此属性的值
				// System.out.println("name:"+f.getName()+"\t value = "+val);
				String type = f.getType().toString();// 得到此属性的类型
				// System.out.println("####"+f.getType().toString()+"\t");
				try {
					if (!type.equals("interface java.util.Set")) {
						redisManager.hset(key, f.getName(), val.toString());
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					// e.printStackTrace();
					// System.out.println("异常name:" + f.getName() + "\t value =
					// " + val);
					// System.out.println("####" + f.getType().toString() +
					// "\t");
				}

			}
		}

	}

	public boolean isLogin(String key) {
		try {
			String keyValue = redisManager.get(key);
			if (keyValue == null || keyValue.equals("")) {
				return false;
			}
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return false;
		}
	}

	/**
	 * save session
	 *
	 * @param session
	 * @throws UnknownHttpSessionException
	 */
	private void save(String sessionid, String userName) {
		if (userName == null) {
			logger.error("userName or userName id is null");
			return;
		}
		int expire = timeOut / 1000;

		this.redisManager.set(sessionid, userName, expire);
	}

	public void delete(String sessionid) {
		if (sessionid == null) {
			logger.error("userName or userName id is null");
			return;
		}
		redisManager.del(sessionid);

	}

	/**
	 * 
	 * @param sessionId
	 * @return
	 */
	public String getFuserSession(String sessionId) {
		try {
			String fuserJson = redisManager.get(sessionId);
			return fuserJson;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			return "";
		}
	}

	/**
	 * 
	 * @param key
	 * @param value
	 */
	public void saveKey(String key, String value) {
		if (key == null || value == null) {
			logger.error("key or value is null");
			return;
		}

		try {
			this.redisManager.set(key, value);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}
	}

	/**
	 * 根据Key读取redis
	 * 
	 * @param sessionId
	 * @return
	 */
	public String doRead(String sessionId) {
		if (sessionId == null) {
			logger.error("userName id is null");
			return null;
		}

		String s = redisManager.get(sessionId);

		return s;
	}

	/**
	 * 获取Bean对象
	 * 
	 * @param sessionID
	 * @return
	 */
	public Object getBean(String key) {
		Map<String, String> map = null;
		if (key != null && !key.equals("")) {

			try {
				Map<String, String> fuserMap = new HashMap<String, String>();
				Map<String, String> fwalletMap = new HashMap<String, String>();
				map = redisManager.hgetall(key);
				return map;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				return map;
			}
		}
		return map;
	}

	/**
	 * 获取Bean对象
	 * 
	 * @param sessionID
	 * @return
	 */
	public List<Object> getListBean(String key) {
		List<Map<String, String>> listmap = null;
		if (key != null && !key.equals("")) {

			return null;
		}
		return null;
	}

	/**
	 * 增加list
	 * 
	 * @param key
	 * @param value
	 */
	public void saveList(String key, String... value) {
		redisManager.lpush(key, value);
	}

	/**
	 * 移除list尾部
	 * 
	 * @param key
	 */
	public void delListrpop(String key) {
		redisManager.rpop(key);
	}

	/**
	 * 通过索引index获取列表的元素、key>=0从头到尾，key<0从尾到头
	 * 
	 * @param key
	 * @param index
	 * @return
	 */
	public List<String> getList(String key, int beginIndex, int endIndex) {
		try {
			return redisManager.LRANGE(key, beginIndex, endIndex);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			return null;
		}
	}

	/**
	 * 根据key删除list 让列表只保留指定区间内的元素，不在指定区间之内的元素都将被删除
	 * 
	 * @param key
	 * @param beginIndex
	 * @param endIndex
	 */
	public void delList(String key, int beginIndex, int endIndex) {
		try {
			redisManager.ltrim(key, beginIndex, endIndex);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}
	}

	public int getTimeOut() {
		return timeOut;
	}

	public void setTimeOut(int timeOut) {
		this.timeOut = timeOut;
	}

	public void updateKeyTime(String sessionId) {

		if (isLogin(sessionId)) {
			redisManager.expire(sessionId, timeOut / 1000);
		}

	}

	/**
	 * 给key设置值，如果key存在则不修改，并返回0
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public long setnx(String key, String value) {
		return redisManager.setnx(key, value);
	}

	/**
	 * 更新ky的过期时间
	 * 
	 * @param key
	 * @param time
	 */
	public void expire(String key, int time) {
		redisManager.expire(key, time);
	}

	/**
	 * 获取key的过期时间
	 * 
	 * @param key
	 * @return
	 */
	public long getexpireTime(String key) {
		return redisManager.ttl(key);
	}

	public void saveHash(String hkey, String key, String value) {
		redisManager.hset(hkey, key, value);
	}

	public void removeHash(String hkey, String item) {
		redisManager.hdel(hkey, item);
	}

	public Map<String, String> getHash(String hkey) {
		return redisManager.hgetall(hkey);
	}
	public String hget(String key, String item) {
		return redisManager.hget(key, item);
	}
	public long sadd(String key, String... member) {
		return redisManager.sadd(key, member);
	}
	public Set<String> smembers(String key) {
		return redisManager.smembers(key);
	}
	public long srem(String key, String... member) {
		return redisManager.srem(key, member);
	}
	public long hset(String key,String item,String value)
	{
		return redisManager.hset(key, item, value);
	}
	/**
	 * 同时将多个 field-value (域-值)对设置到哈希表 key 中
	 * 
	 * @param key
	 * @param item
	 * @param hash
	 * @return
	 */
	public String hmset(String key,Map<String, String> hash) {
		return redisManager.hmset(key, hash);
	}

	/**
	 * 获取所有哈希表中的字段
	 */
	public Set<String> hkeys(String key) {
		return redisManager.hkeys(key);
	}
	public void close()
	{
		redisManager.close();
	}
	
	public String getSet(String key, String value) {
		return redisManager.getSet(key, value);
	}

}
