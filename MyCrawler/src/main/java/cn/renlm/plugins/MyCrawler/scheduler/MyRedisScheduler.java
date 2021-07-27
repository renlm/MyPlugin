package cn.renlm.plugins.MyCrawler.scheduler;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.scheduler.RedisPriorityScheduler;

/**
 * 分布式Url调度
 * 
 * @author Renlm
 *
 */
public class MyRedisScheduler extends RedisPriorityScheduler implements MyDuplicateVerify {

	private static final String VERIFY_PREFIX = "verify_";

	public MyRedisScheduler(JedisPool jedisPool) {
		super(jedisPool);
	}

	private String getVerifyKey(Task task) {
		return VERIFY_PREFIX + task.getUUID();
	}

	public boolean removeFromSetKey(Request request, Task task) {
		Jedis jedis = pool.getResource();
		try {
			return jedis.srem(getSetKey(task), request.getUrl()) > 0;
		} finally {
			jedis.close();
		}
	}

	@Override
	public boolean verifyDuplicate(Request request, Task task) {
		try (Jedis jedis = pool.getResource()) {
			return jedis.sadd(getVerifyKey(task), request.getUrl()) == 0;
		}
	}
}