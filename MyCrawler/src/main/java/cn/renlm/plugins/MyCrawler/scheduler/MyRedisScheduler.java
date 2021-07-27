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

	JedisPool pool;

	private static final String VERIFY_PREFIX = "verify_";

	public MyRedisScheduler(JedisPool pool) {
		super(pool);
		this.pool = pool;
	}

	private String getVerifyKey(Task task) {
		return VERIFY_PREFIX + task.getUUID();
	}

	@Override
	public boolean verifyDuplicate(Request request, Task task) {
		try (Jedis jedis = pool.getResource()) {
			return jedis.sadd(getVerifyKey(task), request.getUrl()) == 0;
		}
	}
}