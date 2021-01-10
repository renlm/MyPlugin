package cn.renlm.plugins;

import cn.hutool.core.util.ReflectUtil;
import cn.hutool.db.nosql.redis.RedisDS;
import cn.renlm.plugins.MyCrawler.MyPageProcessor;
import cn.renlm.plugins.MyCrawler.MyPipeline;
import lombok.experimental.UtilityClass;
import redis.clients.jedis.JedisPool;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.Task;
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
	 * @param thread
	 * @param site
	 * @param pageProcessor
	 * @param pipeline
	 * @param urls
	 * @return
	 */
	public static final Spider createSpider(int thread, Site site, MyPageProcessor pageProcessor, MyPipeline pipeline,
			String... urls) {
		RedisScheduler scheduler = createRedisScheduler();
		PageProcessor pp = createPageProcessor(site, pageProcessor);
		Pipeline pl = createPipeline(pipeline);
		return Spider.create(pp).addUrl(urls).thread(thread).setScheduler(scheduler).addPipeline(pl);
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
	 * 链接去重
	 * 
	 * @return
	 */
	private static final RedisScheduler createRedisScheduler() {
		RedisDS redisDS = RedisDS.create();
		JedisPool pool = (JedisPool) ReflectUtil.getFieldValue(redisDS, "pool");
		return new RedisScheduler(pool);
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