package cn.renlm.plugins;

import java.util.function.Consumer;

import cn.edu.hfut.dmic.webcollector.crawler.Crawler;
import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
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

	private static final int threads = 2;

	private static final int topN = 100;

	private static final int maxExecuteCount = 10;

	private static final int executeInterval = 3000;

	/**
	 * 默认配置
	 * 
	 * @param crawlPath
	 * @param autoParse
	 * @param crawler
	 * @return
	 */
	public Crawler createDefault(String crawlPath, boolean autoParse, Consumer<BreadthCrawler> crawler) {
		BreadthCrawler breadthCrawler = new BreadthCrawler(crawlPath, autoParse) {
			@Override
			public void visit(Page page, CrawlDatums next) {
				this.visitor.visit(page, next);
			}
		};

		breadthCrawler.setThreads(threads);
		breadthCrawler.getConf().setTopN(topN);
		breadthCrawler.getConf().setMaxExecuteCount(maxExecuteCount);
		breadthCrawler.getConf().setExecuteInterval(executeInterval);
		crawler.accept(breadthCrawler);
		return breadthCrawler;
	}
}