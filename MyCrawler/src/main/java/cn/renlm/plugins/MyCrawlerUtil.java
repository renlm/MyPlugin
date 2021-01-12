package cn.renlm.plugins;

import cn.renlm.plugins.MyCrawler.MyPageProcessor;
import cn.renlm.plugins.MyCrawler.MyPipeline;
import cn.renlm.plugins.MyCrawler.MySpider;
import lombok.experimental.UtilityClass;
import redis.clients.jedis.JedisPool;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Site;
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
	 * @param <T>
	 * @param site
	 * @param extra
	 * @param pageProcessor
	 * @param pipeline
	 * @return
	 */
	public static final <T> MySpider createSpider(Site site, T extra, MyPageProcessor<T> pageProcessor,
			MyPipeline<T> pipeline) {
		MySpider mySpider = new MySpider(createPageProcessor(site, extra, pageProcessor));
		mySpider.addPipeline(createPipeline(extra, pipeline));
		return mySpider;
	}

	/**
	 * 爬虫实例
	 * 
	 * @param <T>
	 * @param pool
	 * @param site
	 * @param extra
	 * @param pageProcessor
	 * @param pipeline
	 * @return
	 */
	public static final <T> MySpider createSpider(JedisPool pool, Site site, T extra, MyPageProcessor<T> pageProcessor,
			MyPipeline<T> pipeline) {
		MySpider mySpider = new MySpider(createPageProcessor(site, extra, pageProcessor));
		mySpider.setScheduler(new RedisScheduler(pool));
		mySpider.addPipeline(createPipeline(extra, pipeline));
		return mySpider;
	}

	/**
	 * 页面处理器
	 * 
	 * @param <T>
	 * @param site
	 * @param extra
	 * @param pageProcessor
	 * @return
	 */
	private static final <T> PageProcessor createPageProcessor(Site site, T extra, MyPageProcessor<T> pageProcessor) {
		return new PageProcessor() {

			@Override
			public void process(Page page) {
				pageProcessor.process(extra, page);
			}

			@Override
			public Site getSite() {
				return site;
			}
		};
	}

	/**
	 * 结果处理器
	 * 
	 * @param <T>
	 * @param extra
	 * @param pipeline
	 * @return
	 */
	private static final <T> Pipeline createPipeline(T extra, MyPipeline<T> pipeline) {
		return new Pipeline() {

			@Override
			public void process(ResultItems resultItems, Task task) {
				pipeline.process(extra, resultItems, task);
			}
		};
	}
}