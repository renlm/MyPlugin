package cn.renlm.plugins.MyCrawler.scheduler;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.BooleanUtil;
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
	public void cleanCache(Request request, Task task) {
		String url = request.getUrl();
		String cacheKey = Base64.encode(url);
		try (Jedis jedis = pool.getResource()) {
			jedis.srem(getSetKey(task), url);
			jedis.srem(getVerifyKey(task), url);
			jedis.del(cacheKey);
		}
	}

	@Override
	public boolean verifyDuplicate(Boolean forceUpdate, Request request, Task task) {
		String url = request.getUrl();
		Integer pageUrlType = ObjectUtil.defaultIfNull(request.getExtra(PageUrlType.extraKey),
				PageUrlType.unknown.value());
		try (Jedis jedis = pool.getResource()) {
			String cacheKey = Base64.encode(url);
			if (NumberUtil.equals(pageUrlType, PageUrlType.enterurl.value())) {
				this.cleanCache(request, task);
				return false;
			} else if (NumberUtil.equals(pageUrlType, PageUrlType.seed.value())) {
				boolean duplicate = jedis.exists(cacheKey);
				if (BooleanUtil.isTrue(forceUpdate) || !duplicate) {
					jedis.srem(getSetKey(task), url);
					jedis.setex(cacheKey, 60 * 60 * 21L, url);
				}
				if (BooleanUtil.isTrue(forceUpdate)) {
					return false;
				}
				return duplicate;
			} else {
				boolean duplicate = jedis.sadd(getVerifyKey(task), url) == 0;
				if (BooleanUtil.isTrue(forceUpdate)) {
					jedis.srem(getSetKey(task), url);
					return false;
				}
				return duplicate;
			}
		}
	}
}