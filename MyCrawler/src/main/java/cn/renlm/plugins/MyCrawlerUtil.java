package cn.renlm.plugins;

import java.util.function.Consumer;

import cn.edu.hfut.dmic.webcollector.crawler.Crawler;
import cn.edu.hfut.dmic.webcollector.plugin.berkeley.BreadthCrawler;
import lombok.experimental.UtilityClass;

/**
 * 爬虫
 * 
 * @author Renlm
 *
 */
@UtilityClass
public class MyCrawlerUtil {

	private static final int threads = 5;

	private static final int topN = Integer.MAX_VALUE;

	private static final int maxExecuteCount = 3;

	private static final int executeInterval = 1000;

	/**
	 * 默认配置
	 * 
	 * @param breadthCrawler
	 * @param crawler
	 * @return
	 */
	public Crawler defaultBreadthCrawler(BreadthCrawler breadthCrawler, Consumer<BreadthCrawler> crawler) {
		breadthCrawler.setThreads(threads);
		breadthCrawler.getConf().setTopN(topN);
		breadthCrawler.getConf().setMaxExecuteCount(maxExecuteCount);
		breadthCrawler.getConf().setExecuteInterval(executeInterval);
		crawler.accept(breadthCrawler);
		return breadthCrawler;
	}
}