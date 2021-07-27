package cn.renlm.plugins.MyCrawler.scheduler;

import cn.hutool.core.util.ObjectUtil;
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

	public MyRedisScheduler(JedisPool pool) {
		super(pool);
		this.pool = pool;
	}

	@Override
	public boolean verifyDuplicate(Request request, Task task) {
		return ObjectUtil.isNull(this.pool.getResource().zrank(getSetKey(task), request.getUrl()));
	}
}