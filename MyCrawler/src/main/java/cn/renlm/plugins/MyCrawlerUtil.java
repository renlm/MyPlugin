package cn.renlm.plugins;

import cn.hutool.core.util.StrUtil;
import cn.hutool.db.nosql.redis.RedisDS;
import cn.renlm.plugins.MyCrawler.MyPageProcessor;
import cn.renlm.plugins.MyCrawler.MyPipeline;
import cn.renlm.plugins.MyCrawler.MyRedisSetting;
import cn.renlm.plugins.MyCrawler.MySpider;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.RedisScheduler;
import us.codecraft.webmagic.scheduler.Scheduler;

/**
 * 爬虫工具
 * 
 * @author Renlm
 *
 */
@Slf4j
@UtilityClass
public class MyCrawlerUtil {

	/**
	 * 爬虫实例
	 * 
	 * @param site
	 * @param pageProcessor
	 * @param pipeline
	 * @return
	 */
	public static final MySpider createSpider(Site site, MyPageProcessor pageProcessor, MyPipeline pipeline) {
		MySpider mySpider = new MySpider(createPageProcessor(site, pageProcessor));
		mySpider.setScheduler(createRedisScheduler(null, mySpider.getScheduler()));
		mySpider.addPipeline(createPipeline(pipeline));
		return mySpider;
	}

	/**
	 * 爬虫实例
	 * 
	 * @param redisGroup
	 * @param site
	 * @param pageProcessor
	 * @param pipeline
	 * @return
	 */
	public static final MySpider createSpider(String redisGroup, Site site, MyPageProcessor pageProcessor,
			MyPipeline pipeline) {
		MySpider mySpider = new MySpider(createPageProcessor(site, pageProcessor));
		mySpider.setScheduler(createRedisScheduler(redisGroup, mySpider.getScheduler()));
		mySpider.addPipeline(createPipeline(pipeline));
		return mySpider;
	}

	/**
	 * 页面
	 * 
	 * @param site
	 * @param pageProcessor
	 * @return
	 */
	private static final PageProcessor createPageProcessor(Site site, MyPageProcessor pageProcessor) {
		return new PageProcessor() {
			@Override
			public void process(Page page) {
				pageProcessor.process(page);
			}

			@Override
			public Site getSite() {
				return site;
			}
		};
	}

	/**
	 * 分布式链接去重
	 * 
	 * @param group
	 * @param defaultScheduler
	 * @return
	 */
	private static final Scheduler createRedisScheduler(String group, Scheduler defaultScheduler) {
		try {
			final MyRedisSetting setting = new MyRedisSetting(RedisDS.REDIS_CONFIG_PATH, true);
			final JedisPoolConfig config = new JedisPoolConfig();
			setting.toBean(config);
			if (StrUtil.isNotBlank(group)) {
				setting.toBean(group, config);
			}
			JedisPool pool = new JedisPool(config, setting.getHost(group), setting.getPort(group),
					setting.getTimeout(group), setting.getPassword(group), setting.getDatabase(group),
					setting.getClientName(group), false);
			pool.getResource().connect();
			log.info("Redis加载成功 [ {} ]", setting.getDatabase(group));
			return new RedisScheduler(pool);
		} catch (Exception e) {
			log.error("Redis加载失败[ config/redis.setting ]", e);
		}
		return defaultScheduler;
	}

	/**
	 * 结果
	 * 
	 * @param pipeline
	 * @return
	 */
	private static final Pipeline createPipeline(MyPipeline pipeline) {
		return new Pipeline() {
			@Override
			public void process(ResultItems resultItems, Task task) {
				pipeline.process(resultItems, task);
			}
		};
	}
}