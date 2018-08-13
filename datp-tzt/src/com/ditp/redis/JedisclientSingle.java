package com.ditp.redis;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.ruizton.util.Comm;

import redis.clients.jedis.Jedis;

import redis.clients.jedis.JedisPool;

public class JedisclientSingle implements JedisClient {
	@Autowired
	private JedisPool jedisPool;
	private static String pwd =Comm.getRED_PWD();

	/**
	 * 设置String数据类型
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	@Override
	public String set(String key, String value) {
		Jedis jedis = jedisPool.getResource();
		if (!pwd.equals(""))
			jedis.auth(pwd);
		String jedisSet;
		try {
			jedisSet = jedis.set(key, value);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			return null;
		} finally {
			jedis.close();
		}
		return jedisSet;
	}

	public void close() {
		// jedis.close();
	}

	/**
	 * 设置String数据类型
	 * 
	 * @param key
	 * @param value
	 * @param seconds
	 *            过期时间
	 * @return
	 */
	@Override
	public String set(String key, String value, int seconds) {
		Jedis jedis = jedisPool.getResource();
		if (!pwd.equals(""))
			jedis.auth(pwd);
		String jedisSet;
		try {
			jedisSet = jedis.setex(key, seconds, value);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			return null;
		} finally {
			jedis.close();
		}
		return jedisSet;
	}

	/**
	 * 获取String数据类型
	 * 
	 * @param key
	 * @return
	 */
	@Override
	public String get(String key) {
		Jedis jedis = jedisPool.getResource();
		if (!pwd.equals(""))
			jedis.auth(pwd);
		String jedisGet;
		try {
			jedisGet = jedis.get(key);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			return null;
		} finally {
			jedis.close();
		}
		return jedisGet;
	}

	/**
	 * 设置hash数据类型
	 * 
	 * @param key
	 * @param item
	 * @param value
	 * @return
	 */
	@Override
	public long hset(String key, String item, String value) {
		value = isNullOrEmpty(value);
		Jedis jedis = jedisPool.getResource();
		if (!pwd.equals(""))
			jedis.auth(pwd);
		try {
			long l = jedis.hset(key, item, value);

			return l;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			return 0;
		} finally {
			jedis.close();
		}
	}

	public String isNullOrEmpty(String value) {
		try {
			if (value == null || value.equals("")) {
				return "";
			}
			return value;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return "";
		}

	}
	public String hmset(String key,Map<String,String> hash) {
		Jedis jedis = jedisPool.getResource();
		if (!pwd.equals(""))
			jedis.auth(pwd);
		try {
			String hstr= jedis.hmset(key, hash);
			return hstr;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return "";
		}finally{
			jedis.close();
		}
	}
	public Set<String> hkeys(String key)
	{
		Jedis jedis = jedisPool.getResource();
		if (!pwd.equals(""))
			jedis.auth(pwd);
		try {
			return jedis.hkeys(key);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return null;
		}finally{
			jedis.close();
		}
	}
	/**
	 * 获取hash数据类型中指定字段的值
	 * 对应的命令 ：	HGET key field
	 * @param key
	 * @param item
	 * @return
	 */
	@Override
	public String hget(String key, String item) {
		Jedis jedis = jedisPool.getResource();
		if (!pwd.equals(""))
			jedis.auth(pwd);
		try {
			String str = jedis.hget(key, item);
			return str;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			return null;
		} finally {
			jedis.close();
		}
	}

	/**
	 * 删除hash数据
	 * 
	 * @param key
	 * @param item
	 * @return
	 */
	@Override
	public long incr(String key) {
		Jedis jedis = jedisPool.getResource();
		if (!pwd.equals(""))
			jedis.auth(pwd);
		try {
			long l = jedis.incr(key);
			return l;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			return 0;
		} finally {
			jedis.close();
		}
	}

	/**
	 * 加一操作
	 * 
	 * @param key
	 * @return
	 */
	@Override
	public long decr(String key) {
		Jedis jedis = jedisPool.getResource();
		if (!pwd.equals(""))
			jedis.auth(pwd);
		try {
			long l = jedis.decr(key);
			return l;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			return 0;
		} finally {
			jedis.close();
		}
	}

	/**
	 * 减一操作
	 * 
	 * @param key
	 * @return
	 */
	@Override
	public long expire(String key, int second) {
		Jedis jedis = jedisPool.getResource();
		if (!pwd.equals(""))
			jedis.auth(pwd);
		long l;
		try {
			l = jedis.expire(key, second);
			return l;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			return 0;
		} finally {
			jedis.close();
		}

	}

	/**
	 * 设置key的过期时间
	 * 
	 * @param key
	 * @param second
	 * @return
	 */
	@Override
	public long ttl(String key) {
		Jedis jedis = jedisPool.getResource();
		if (!pwd.equals(""))
			jedis.auth(pwd);
		try {
			long l = jedis.ttl(key);
			return l;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			return 0;
		} finally {
			jedis.close();
		}

	}

	/**
	 * 判断key是否过期
	 * 
	 * @param key
	 * @return
	 */
	@Override
	public Long hdel(String key, String item) {
		Jedis jedis = jedisPool.getResource();
		if (!pwd.equals(""))
			jedis.auth(pwd);
		try {
			long l = jedis.hdel(key, item);
			return l;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			return (long) 0;
		} finally {
			jedis.close();
		}
	}

	public Map<String, String> hgetall(String key) {
		Jedis jedis = jedisPool.getResource();
		if (!pwd.equals(""))
			jedis.auth(pwd);
		try {
			Map<String, String> map = jedis.hgetAll(key);
			return map;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			return null;
		} finally {
			jedis.close();
		}
	}

	@Override
	public void del(String key) {
		// TODO Auto-generated method stub

		Jedis jedis = jedisPool.getResource();
		if (!pwd.equals(""))
			jedis.auth(pwd);
		try {
			jedis.del(key);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		} finally {
			jedis.close();
		}
	}

	/**
	 * lpush [lpush key valus...] 类似于压栈操作，将元素放入头部
	 * 
	 * @param key
	 * @param value
	 */
	@Override
	public void lpush(String key, String... value) {

		Jedis jedis = jedisPool.getResource();
		if (!pwd.equals(""))
			jedis.auth(pwd);
		try {
			jedis.lpush(key, value);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// //e.printStackTrace();
		} finally {
			jedis.close();
		}
	}

	public void rpop(String key) {

		Jedis jedis = jedisPool.getResource();
		if (!pwd.equals(""))
			jedis.auth(pwd);
		try {
			jedis.rpop(key);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		} finally {

		}
		jedis.close();

	}

	/**
	 * lindex [lindex key index]:通过索引index获取列表的元素、key>=0从头到尾，key<0从尾到头
	 * 
	 * @param key
	 * @param index
	 */
	public String lindex(String key, int index) {
		Jedis jedis = jedisPool.getResource();
		if (!pwd.equals(""))
			jedis.auth(pwd);
		String str = "";
		try {
			str = jedis.lindex(key, index);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		} finally {
			jedis.close();
		}
		return str;

	}

	@Override
	public List<String> LRANGE(String key, int beginIndex, int endIndex) {
		// TODO Auto-generated method stub
		Jedis jedis = jedisPool.getResource();
		if (!pwd.equals(""))
			jedis.auth(pwd);
		try {
			List<String> LRANGE = jedis.lrange(key, beginIndex, endIndex);
			return LRANGE;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			return null;
		} finally {
			jedis.close();
		}

	}

	/**
	 * 根据key删除list 让列表只保留指定区间内的元素，不在指定区间之内的元素都将被删除
	 */
	@Override
	public void ltrim(String key, int beginIndex, int endIndex) {
		Jedis jedis = jedisPool.getResource();
		if (!pwd.equals(""))
			jedis.auth(pwd);
		try {
			jedis.ltrim(key, beginIndex, endIndex);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// //e.printStackTrace();
		} finally {
			jedis.close();
		}

	}

	@Override
	public long setnx(String key, String value) {
		// TODO Auto-generated method stub
		Jedis jedis = jedisPool.getResource();
		if (!pwd.equals(""))
			jedis.auth(pwd);
		try {
			long l = jedis.setnx(key, value);
			return l;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			return 0;
		} finally {
			jedis.close();
		}
	}

	/**
	 * 向集合添加一个或多个成员
	 */
	@Override
	public long sadd(String key, String... member) {
		// TODO Auto-generated method stub
		Jedis jedis = jedisPool.getResource();
		if (!pwd.equals(""))
			jedis.auth(pwd);
		try {
			long l = jedis.sadd(key, member);
			return l;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			return 0;
		} finally {
			jedis.close();
		}
	}

	/**
	 * 返回集合中的所有成员
	 */
	@Override
	public Set<String> smembers(String key) {
		// TODO Auto-generated method stub
		Jedis jedis = jedisPool.getResource();
		if (!pwd.equals(""))
			jedis.auth(pwd);
		try {
			Set<String> smembers = jedis.smembers(key);
			return smembers;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			return null;
		} finally {
			jedis.close();
		}
	}

	/**
	 * 命令用于移除集合中的一个或多个成员元素，不存在的成员元素会被忽略。
	 */
	@Override
	public long srem(String key, String... member) {
		// TODO Auto-generated method stub
		Jedis jedis = jedisPool.getResource();
		if (!pwd.equals(""))
			jedis.auth(pwd);
		try {
			long l = jedis.srem(key, member);
			return l;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			return 0;
		} finally {
			jedis.close();
		}
	}
	public String getSet(String key, String value) {
		// TODO Auto-generated method stub
		Jedis jedis = jedisPool.getResource();
		if (!pwd.equals(""))
			jedis.auth(pwd);
		try {
		return jedis.getSet(key, value);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			return "";
		} finally {
			jedis.close();
		}
	}

}
