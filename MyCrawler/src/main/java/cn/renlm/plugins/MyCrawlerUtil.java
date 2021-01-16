package cn.renlm.plugins;

import cn.renlm.plugins.MyCrawler.MySite;
import cn.renlm.plugins.MyCrawler.MySpider;
import cn.renlm.plugins.MyCrawler.data.MyProcessPage;
import cn.renlm.plugins.MyCrawler.data.MyProcessPipe;
import cn.renlm.plugins.MyCrawler.pipeline.MyPipeline;
import cn.renlm.plugins.MyCrawler.processor.MyPageProcessor;
import cn.renlm.plugins.MyCrawler.scheduler.MyRedisScheduler;
import lombok.experimental.UtilityClass;
import redis.clients.jedis.JedisPool;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.QueueScheduler;
import us.codecraft.webmagic.scheduler.component.DuplicateRemover;

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
	 * @param site
	 * @param pageProcessor
	 * @param pipelines
	 * @return
	 */
	public static final MySpider createSpider(MySite site, MyPageProcessor pageProcessor, MyPipeline... pipelines) {
		QueueScheduler scheduler = new QueueScheduler();
		MySpider mySpider = new MySpider(createPageProcessor(site, pageProcessor));
		mySpider.setScheduler(scheduler);
		for (MyPipeline pipeline : pipelines) {
			mySpider.addPipeline(createPipeline(site, pipeline, scheduler.getDuplicateRemover()));
		}
		return mySpider;
	}

	/**
	 * 爬虫实例
	 * 
	 * @param pool
	 * @param site
	 * @param pageProcessor
	 * @param pipelines
	 * @return
	 */
	public static final MySpider createSpider(JedisPool pool, MySite site, MyPageProcessor pageProcessor,
			MyPipeline... pipelines) {
		MyRedisScheduler scheduler = new MyRedisScheduler(pool);
		MySpider mySpider = new MySpider(createPageProcessor(site, pageProcessor));
		mySpider.setScheduler(scheduler);
		for (MyPipeline pipeline : pipelines) {
			mySpider.addPipeline(createPipeline(site, pipeline, scheduler));
		}
		return mySpider;
	}

	/**
	 * 页面处理器
	 * 
	 * @param site
	 * @param pageProcessor
	 * @return
	 */
	private static final PageProcessor createPageProcessor(final MySite site, final MyPageProcessor pageProcessor) {
		return new PageProcessor() {
			@Override
			public void process(Page page) {
				MyProcessPage myPage = new MyProcessPage(site, page);
				pageProcessor.process(myPage);
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
	 * @param site
	 * @param pipeline
	 * @param duplicatedRemover
	 * @return
	 */
	private static final Pipeline createPipeline(final MySite site, final MyPipeline pipeline,
			final DuplicateRemover duplicatedRemover) {
		return new Pipeline() {
			@Override
			public void process(ResultItems resultItems, Task task) {
				MyProcessPipe myData = new MyProcessPipe(task, resultItems, duplicatedRemover);
				pipeline.process(myData);
			}
		};
	}
}