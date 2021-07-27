package cn.renlm.plugins;

import java.util.List;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.renlm.plugins.MyCrawler.MySite;
import cn.renlm.plugins.MyCrawler.MySpider;
import cn.renlm.plugins.MyCrawler.data.MyProcessPage;
import cn.renlm.plugins.MyCrawler.data.MyProcessPipe;
import cn.renlm.plugins.MyCrawler.pipeline.MyPipeline;
import cn.renlm.plugins.MyCrawler.processor.MyPageProcessor;
import cn.renlm.plugins.MyCrawler.scheduler.MyDuplicateVerify;
import cn.renlm.plugins.MyCrawler.scheduler.MyQueueScheduler;
import cn.renlm.plugins.MyCrawler.scheduler.MyRedisScheduler;
import lombok.experimental.UtilityClass;
import redis.clients.jedis.JedisPool;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
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

	private static final String depthExtraKey = "_MyCrawlerDepthExtra_";

	/**
	 * 爬虫实例
	 * 
	 * @param site
	 * @param pageProcessor
	 * @param pipelines
	 * @return
	 */
	public static final MySpider createSpider(MySite site, MyPageProcessor pageProcessor, MyPipeline... pipelines) {
		MyQueueScheduler scheduler = new MyQueueScheduler();
		MySpider mySpider = new MySpider(createPageProcessor(site, pageProcessor));
		mySpider.setScheduler(scheduler);
		for (MyPipeline pipeline : pipelines) {
			mySpider.addPipeline(createPipeline(site, pipeline, scheduler));
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
				int depth = ObjectUtil.defaultIfNull(page.getRequest().getExtra(depthExtraKey), 1);
				MyProcessPage myPage = new MyProcessPage(depth, site, page);
				pageProcessor.process(myPage);
				List<Request> targetRequests = page.getTargetRequests();
				// 爬取深度控制
				if (CollUtil.isNotEmpty(targetRequests)) {
					page.getTargetRequests().forEach(it -> {
						it.putExtra(depthExtraKey, depth + 1);
					});
					if (site.getMaxDepth() > 0 && depth >= site.getMaxDepth()) {
						page.getTargetRequests().clear();
					}
				}
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
	 * @param duplicateVerify
	 * @return
	 */
	private static final Pipeline createPipeline(final MySite site, final MyPipeline pipeline,
			final MyDuplicateVerify duplicateVerify) {
		return new Pipeline() {
			@Override
			public void process(ResultItems resultItems, Task task) {
				MyProcessPipe myData = new MyProcessPipe(task, resultItems, duplicateVerify);
				pipeline.process(myData);
			}
		};
	}
}