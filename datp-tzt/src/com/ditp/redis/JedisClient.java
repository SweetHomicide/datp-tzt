package com.ditp.redis;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface  JedisClient {
	String get(String key);
	String set(String key, String value);
	String set(String key, String value,int seconds);
	String hget(String hkey, String key);
	long hset(String hkey, String key, String value);
	String hmset(String key,Map<String,String> hash);
	Set<String> hkeys(String key);
    long incr(String key);
    long expire(String key, int second);
    long ttl(String key);
	long decr(String key);
	Long hdel(String key, String item);
	void del(String key);
	public Map<String, String> hgetall(String key);
	void lpush(String key,String... value);//将元素放入头部（list操作）
    void rpop(String key);//删除list尾部
    String lindex(String key,int index);//通过索引index获取列表的元素
    List<String> LRANGE(String key,int beginIndex,int endIndex);//获取列表指定范围内的元素
    void ltrim(String key,int beginIndex, int endIndex);//删除
    long setnx(String key,String value);
    long sadd(String key,String... member);
    Set<String> smembers(String key);
    long srem(String key,String... member);
    void close();
    String getSet(String key, String value);
}
