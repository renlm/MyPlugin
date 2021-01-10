package cn.renlm.plugins;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.db.nosql.redis.RedisDS;
import lombok.experimental.UtilityClass;
import redis.clients.jedis.JedisPool;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.RedisScheduler;

/**
 * 爬虫工具
 * 
 * @author Renlm
 *
 */
@UtilityClass
public class MyCrawlerUtil {

	/**
	 * 爬虫实例
	 * 
	 * @param pageProcessor
	 * @param pipeline
	 * @param thread
	 * @param urls
	 * @return
	 */
	public static final Spider createSpider(PageProcessor pageProcessor, Pipeline pipeline, int thread,
			String... urls) {
		RedisScheduler scheduler = createRedisScheduler();
		return Spider.create(pageProcessor)
				.addUrl(urls)
				.thread(thread)
				.setUUID(IdUtil.objectId())
				.setScheduler(scheduler)
				.addPipeline(pipeline);
	}

	/**
	 * 集成Redis
	 * 
	 * @return
	 */
	private static final RedisScheduler createRedisScheduler() {
		RedisDS redisDS = RedisDS.create();
		JedisPool pool = (JedisPool) ReflectUtil.getFieldValue(redisDS, "pool");
		return new RedisScheduler(pool);
	}
}