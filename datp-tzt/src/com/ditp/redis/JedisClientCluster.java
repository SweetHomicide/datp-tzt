package com.ditp.redis;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import redis.clients.jedis.JedisCluster;

public class JedisClientCluster implements JedisClient {
	// 注入jedisCluster
	@Autowired
	private JedisCluster jedisCluster;

	/**
	 * 设置String数据类型
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	@Override
	public String set(String key, String value) {
		return jedisCluster.set(key, value);
	}

	public void close() {
		jedisCluster.close();
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
		return jedisCluster.setex(key, seconds, value);
	}

	/**
	 * 获取String数据类型
	 * 
	 * @param key
	 * @return
	 */
	@Override
	public String get(String key) {
		return jedisCluster.get(key);
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
		return jedisCluster.hset(key, item, value);
	}

	/**
	 * 同时将多个 field-value (域-值)对设置到哈希表 key 中
	 * 
	 * @param key
	 * @param item
	 * @param hash
	 * @return
	 */
	public String hmset(String key, Map<String, String> hash) {
		return jedisCluster.hmset(key, hash);
	}

	/**
	 * 获取所有哈希表中的字段
	 */
	public Set<String> hkeys(String key) {
		return jedisCluster.hkeys(key);
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

	/**
	 * 获取hash数据类型
	 * 
	 * @param key
	 * @param item
	 * @return
	 */
	@Override
	public String hget(String key, String item) {
		return jedisCluster.hget(key, item);
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
		return jedisCluster.incr(key);
	}

	/**
	 * 加一操作
	 * 
	 * @param key
	 * @return
	 */
	@Override
	public long decr(String key) {
		return jedisCluster.decr(key);
	}

	/**
	 * 减一操作
	 * 
	 * @param key
	 * @return
	 */
	@Override
	public long expire(String key, int second) {
		return jedisCluster.expire(key, second);
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
		return jedisCluster.ttl(key);
	}

	/**
	 * 判断key是否过期
	 * 
	 * @param key
	 * @return
	 */
	@Override
	public Long hdel(String key, String item) {
		return jedisCluster.hdel(key, item);
	}

	public Map<String, String> hgetall(String key) {
		return jedisCluster.hgetAll(key);
	}

	/**
	 * 删除key
	 */
	@Override
	public void del(String key) {
		// TODO Auto-generated method stub
		try {
			jedisCluster.del(key);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
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

		try {
			jedisCluster.lpush(key, value);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}
	}

	public void rpop(String key) {
		try {
			jedisCluster.rpop(key);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}

	}

	/**
	 * lindex [lindex key index]:通过索引index获取列表的元素、key>=0从头到尾，key<0从尾到头
	 * 
	 * @param key
	 * @param index
	 */
	public String lindex(String key, int index) {
		return jedisCluster.lindex(key, index);

	}

	@Override
	public List<String> LRANGE(String key, int beginIndex, int endIndex) {
		// TODO Auto-generated method stub
		return jedisCluster.lrange(key, beginIndex, endIndex);
	}

	/**
	 * 根据key删除list 让列表只保留指定区间内的元素，不在指定区间之内的元素都将被删除
	 */
	@Override
	public void ltrim(String key, int beginIndex, int endIndex) {
		jedisCluster.ltrim(key, beginIndex, endIndex);
	}

	@Override
	public long setnx(String key, String value) {
		// TODO Auto-generated method stub
		return jedisCluster.setnx(key, value);
	}

	/**
	 * 向集合添加一个或多个成员
	 */
	@Override
	public long sadd(String key, String... member) {
		// TODO Auto-generated method stub
		return jedisCluster.sadd(key, member);
	}

	/**
	 * 返回集合中的所有成员
	 */
	@Override
	public Set<String> smembers(String key) {
		// TODO Auto-generated method stub
		return jedisCluster.smembers(key);
	}

	/**
	 * 命令用于移除集合中的一个或多个成员元素，不存在的成员元素会被忽略。
	 */
	@Override
	public long srem(String key, String... member) {
		// TODO Auto-generated method stub
		return jedisCluster.srem(key, member);
	}

	public String getSet(String key, String value) {
		return jedisCluster.getSet(key, value);
	}

}
