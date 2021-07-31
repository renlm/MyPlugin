package cn.renlm.plugins.MyCrawler.scheduler;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.renlm.plugins.MyCrawler.PageUrlType;
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

	@Override
	public boolean verifyDuplicate(Boolean forceUpdate, Request request, Task task) {
		String url = request.getUrl();
		Integer pageUrlType = ObjectUtil.defaultIfNull(request.getExtra(PageUrlType.extraKey),
				PageUrlType.unknown.value());
		try (Jedis jedis = pool.getResource()) {
			String cacheKey = Base64.encode(url);
			if (NumberUtil.equals(pageUrlType, PageUrlType.seed.value())) {
				boolean duplicate = jedis.exists(cacheKey);
				if (!duplicate) {
					jedis.srem(getSetKey(task), url);
					jedis.setex(cacheKey, 60 * 60 * 21, url);
				}
				return duplicate;
			} else {
				return jedis.sadd(getVerifyKey(task), url) == 0;
			}
		}
	}
}