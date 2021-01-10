package cn.renlm.plugins;

import java.util.function.Consumer;

import lombok.experimental.UtilityClass;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.Scheduler;

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
	 * @param scheduler
	 * @param pipeline
	 * @param thread
	 * @param urls
	 * @return
	 */
	public static final Spider createSpider(PageProcessor pageProcessor, Scheduler scheduler, Pipeline pipeline,
			int thread, String... urls) {
		return Spider.create(pageProcessor).addUrl(urls).thread(thread).setScheduler(scheduler).addPipeline(pipeline);
	}

	/**
	 * 缓存连接池
	 * 
	 * @param host
	 * @param port
	 * @param password
	 * @param timeout
	 * @param config
	 * @return
	 */
	public static final JedisPool createJedisPool(String host, int port, String password, int timeout,
			Consumer<JedisPoolConfig> config) {
		JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
		config.accept(jedisPoolConfig);
		return new JedisPool(jedisPoolConfig, host, port, timeout, password);
	}
}