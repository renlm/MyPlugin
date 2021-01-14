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
		MySpider mySpider = new MySpider(createPageProcessor(site, pageProcessor));
		for (MyPipeline pipeline : pipelines) {
			mySpider.addPipeline(createPipeline(site, pipeline));
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
		MySpider mySpider = new MySpider(createPageProcessor(site, pageProcessor));
		mySpider.setScheduler(new MyRedisScheduler(pool));
		for (MyPipeline pipeline : pipelines) {
			mySpider.addPipeline(createPipeline(site, pipeline));
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
	private static final PageProcessor createPageProcessor(MySite site, MyPageProcessor pageProcessor) {
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
	 * @return
	 */
	private static final Pipeline createPipeline(MySite site, MyPipeline pipeline) {
		return new Pipeline() {
			@Override
			public void process(ResultItems resultItems, Task task) {
				MyProcessPipe myData = new MyProcessPipe(task.getUUID(), site, resultItems);
				pipeline.process(myData);
			}
		};
	}
}