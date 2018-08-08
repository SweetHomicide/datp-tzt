package com.ditp.redis;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.ditp.dao.RedisDAO;

@Repository
public class RedisLock {
	// 加锁标志
	public static final String LOCKED = "TRUE";
	public static final long ONE_MILLI_NANOS = 1000000L;
	// 默认超时时间（毫秒）
	public static final long DEFAULT_TIME_OUT = 3000;
	@Autowired
	private RedisDAO redisDAO;
	public static final Random r = new Random();
	// 锁的超时时间（秒），过期删除
	public static final int EXPIRE = 20;
	private String key;
	// 锁状态标志
	private boolean locked = false;

	public void setKey(String key) {
		this.key = "lock" + key;

	}

	public boolean lock(long timeout) {
		long nano = System.nanoTime();
		timeout *= ONE_MILLI_NANOS;
		try {
			while ((System.nanoTime() - nano) < timeout) {
				long Set = redisDAO.setnx(key, LOCKED);
				if (Set == 1) {
					// System.out.println(key);
					redisDAO.expire(key, EXPIRE);
					locked = true;
					return locked;
				} else {
					// 当 key 存在但没有设置剩余生存时间时，返回 -1
					// if(redisDAO.getexpireTime(key)==-1&&(System.nanoTime() -
					// nano)>500*ONE_MILLI_NANOS)
					if (redisDAO.getexpireTime(key) == -1 || redisDAO.getexpireTime(key) > EXPIRE) {
						redisDAO.delete(key);
					}
				}
				// 短暂休眠，nano避免出现活锁
				// Thread.sleep(3, r.nextInt(200));
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return false;
	}

	public boolean lock() {
		return lock(DEFAULT_TIME_OUT);
	}

	// 无论是否加锁成功，必须调用
	public void unlock() {

		redisDAO.delete(key);

	}
}
