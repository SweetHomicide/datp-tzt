package com.ditp.service;

import java.beans.IntrospectionException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ditp.dao.RedisDAO;
import com.ditp.redis.RedisLock;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Service
public class RedisService {
	@Autowired
	private RedisDAO redisDAO;
	@Autowired
	private RedisLock redisLock;

	// @Autowired
	private JedisPool jedisPool;
	private static String pwd = "";

	/**
	 * 保存 值到redis
	 * 
	 * @param key
	 * @param value
	 */
	public void saveKey(String key, String value) {
		try {
		
		//	redisLockl.RedisLockSet(key, 10000, 20000);
		//	if (redisLockl.lock()) {
				redisDAO.saveKey(key, value);
		//	} 
		} catch (Exception e) {
			e.printStackTrace();
			//unlock();
		}finally
		{
		//redisLockl.unlock();
		}
	}
	/**
	 * 根据key读取值
	 * 
	 * @param key
	 * @return
	 */
	public String get(String key) {
		String retStr = "";
		try {
				retStr = redisDAO.doRead(key);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return retStr;
	}

	public long hset(String key, String item, Object bean) {
		long l = 0;
		try {
			String json = ConvertJson(bean);
			l = redisDAO.hset(key, item, json);
		} catch (Exception e) {

			// e.printStackTrace();
		}
		return l;
	}

	public long hset(String key, String item, String value) {
		long l = 0;
		try {
			l = redisDAO.hset(key, item, value);
		} catch (Exception e) {

			// e.printStackTrace();
		}
		return l;
	}

	public Map<String, String> getHashAll(String hkey) {
		return redisDAO.getHash(hkey);
	}
	public String hget(String key, String item) {
		return redisDAO.hget(key, item);
	}

	public void removeHashByitem(String hkey, String item) {
		redisDAO.removeHash(hkey, item);
	}
	/**
	 * 获取所有哈希表中的字段
	 */
	public Set<String> hkeys(String key) {
		try {
			Set<String> hk= redisDAO.hkeys(key);
			return hk;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return null;
		}
	}
	/**
	 * 保存一个bean到redis
	 * 
	 * @param key
	 * @param bean
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws IntrospectionException
	 */
	public void saveBean(String key, Object bean) {
		redisLock.setKey(key);
		try {
			//if (lock()) {
				redisDAO.saveRedis(key, bean);
				unlock();
			//} else {
			//	saveBean(key, bean);
			//}
		} catch (Exception e) {

		}
	}

	

	/**
	 * 获取一个bean对象
	 * 
	 * @param key
	 * @return
	 */
	public Object getBean(String key) {
		Object obj = "";
		redisLock.setKey(key);
		try {
	//		if (lock()) {
				obj = redisDAO.getBean(key);
				//unlock();
//			} else {
//				getBean(key);
//			}
		} catch (Exception e) {

		}
		return obj;
	}

	/**
	 * 把list保存到redis list
	 * 
	 * @param key
	 * @param value
	 * @param isInit
	 *            是否初始化list
	 */
	public void saveList(String key, List<?> dataList, boolean isInit) {
		Jedis jedis = jedisPool.getResource();
		redisLock.setKey(key);
		try {
			if (lock()) {
				if (isInit) {
					// redisDAO.delList(key, 1, 0);// 根据key清空list
				}
				if (null != dataList) {
					for (Object bean : dataList) {
						try {
							String json = ConvertJson(bean);
							// redisDAO.saveList(key, json);
							jedis.sadd(key, json);
						} catch (Exception e) {

						}
					}
				}
				unlock();
			} else {
				saveList(key, dataList, isInit);
			}
		} catch (Exception e) {

		} finally {
			jedis.close();
		}
	}

	/**
	 * 把单一的值保存到redis list
	 * 
	 * @param key
	 * @param value
	 * @param isInit
	 *            是否初始化list
	 */
	public void saveSingleList(String key, Object bean, boolean isInit) {

		redisLock.setKey(key);
		if (lock()) {
			if (isInit) {
				// redisDAO.delList(key, 1, 0);// 根据key清空list
			}
		//	if (null != bean) {

				try {

					String json = ConvertJson(bean);
					// redisDAO.saveList(key, json);
					redisDAO.sadd(key, json);
				} catch (Exception e) {

				}

			}
//			unlock();
//		} else {
//			saveSingleList(key, bean, isInit);
//		}
	}

	public String ConvertJson(Object bean) {
		// 得到类对象
		Field[] fs = bean.getClass().getDeclaredFields();
		String jsonStr = "{";
		for (int i = 0; i < fs.length; i++) {
			try {
				Field f = fs[i];
				f.setAccessible(true); // 设置些属性是可以访问的

				Object val = f.get(bean);// 得到此属性的值
				// System.out.println("name:"+f.getName()+"\t value = "+val);
				String type = f.getType().toString();// 得到此属性的类型
				if (!type.equals("interface java.util.Set") && !type.contains("com.ruizton.main.model")) {
					// redisManager.hset(key, f.getName(), val.toString());
					if (i == fs.length - 1) {
						jsonStr += "\"" + f.getName() + "\":\"" + val.toString() + "\"";
					} else {
						jsonStr += "\"" + f.getName() + "\":\"" + val.toString() + "\",";
					}
				}
			} catch (Exception e) {

				// e.printStackTrace();
			}

		}
		jsonStr += "}";
		return jsonStr;
	}

	public void delList(String key, int begindex, int endindex) {
		redisLock.setKey(key);
		// if (lock()) {
		redisDAO.delList(key, 1, 0);// 根据key清空list
		unlock();
		// } else {
		// delList(key, begindex, endindex);
		// }
	}

	/**
	 * 移除list尾部
	 * 
	 * @param key
	 */
	public void delListrpop(String key) {
		redisLock.setKey(key);
		// if (lock()) {
		redisDAO.delListrpop(key);
		unlock();
		// } else {
		// delListrpop(key);
		// }

	}

	/**
	 * 通过索引index获取列表的元素、key>=0从头到尾，key<0从尾到头
	 * 
	 * @param key
	 * @param index
	 * @return
	 */
	public List<?> getList(String key, int beginIndex, int endIndex, Object beanClass) {

		// List<?> listStr = redisDAO.getList(key, beginIndex, endIndex);
		Set<String> listStr = redisDAO.smembers(key);

		List<Object> listRet = new ArrayList<Object>();
		redisLock.setKey(key);
		// if (lock()) {
		JSONArray jsonArray = JSONArray.fromObject(listStr);
		Object objBean = null;
		try {
			for (Object bean : listStr) {
				objBean = redisDAO.convertBean(beanClass.getClass(), bean.toString());
				listRet.add(objBean);
			}
		} catch (Exception e) {
		}
		// unlock();
		// } else {
		// getList(key, beginIndex, endIndex, beanClass);
		// }
		return listRet;
	}

	/**
	 * 从set里面获取
	 * 
	 * @param key
	 * @param beanClass
	 * @return
	 */
	public Set<String> getList(String key) {

		Set<String> listStr = redisDAO.smembers(key);
		return listStr;
	}

	/**
	 * 通过索引index获取列表的元素、key>=0从头到尾，key<0从尾到头
	 * 
	 * @param key
	 * @param index
	 * @return
	 */
	public Object getObject(String key, int beginIndex, int endIndex, Object beanClass) {
		// List<?> listStr = redisDAO.getList(key, beginIndex, endIndex);
		Set<String> listStr = redisDAO.smembers(key);
		List<Object> listRet = new ArrayList<Object>();
		redisLock.setKey(key);
		// if (lock()) {
		try {
			Object objBean = null;
			int i = 0;
			if (listStr != null) {
				for (Object bean : listStr) {
					objBean = redisDAO.convertBean(beanClass.getClass(), bean.toString());
					// objBean=ObjectTranscoder.deserialize(Base64.decode(bean.toString()));
					listRet.add(objBean);
					i++;
				}
				if (i == 1) {
					return objBean;
				}
			}
		} catch (Exception e) {

			// e.printStackTrace();
		}
		// unlock();
		// } else {
		// getObject(key, beginIndex, endIndex, beanClass);
		// }
		return listRet;
	}

	/**
	 * 从set里面获取
	 * 
	 * @param key
	 * @param beanClass
	 * @return
	 */
	public Object getObject(String key, Object beanClass) {
		// List<?> listStr = redisDAO.getList(key, beginIndex, endIndex);
		Set<String> listStr = redisDAO.smembers(key);
		List<Object> listRet = new ArrayList<Object>();
		redisLock.setKey(key);
		// if (lock()) {
		try {
			Object objBean = null;
			int i = 0;
			if (listStr != null) {
				for (Object bean : listStr) {
					objBean = redisDAO.convertBean(beanClass.getClass(), bean.toString());
					// objBean=ObjectTranscoder.deserialize(Base64.decode(bean.toString()));
					listRet.add(objBean);
					i++;
				}
				if (i == 1) {
					return objBean;
				}
			}
		} catch (Exception e) {

			// e.printStackTrace();
		}
		// unlock();
		// } else {
		// getObject(key, beanClass);
		// }
		return listRet;
	}

	/**
	 * 给key值加锁
	 * 
	 * @param key
	 * @return
	 */
	public boolean lock() {
		return true;
		//return redisLock.lock();
	}

	/**
	 * 给key值解锁
	 * 
	 * @param key
	 */
	public void unlock() {
		//redisLock.unlock();
	}

	/**
	 * 根据key 和值 删除 set
	 * 
	 * @param key
	 * @param bean
	 * @return
	 */
	public long srem(String key, Object bean) {
		String json = ConvertJson(bean);
		return redisDAO.srem(key, json);
	}
	/**
	 * 删除   key
	 * @param sessionid
	 */
	public void delete(String Key) {
		if (Key == null) {
			return;
		}
		redisDAO.delete(Key);

	}
	// public List<Object> getListBean(String key)
	// {
	//
	//
	// }

}
