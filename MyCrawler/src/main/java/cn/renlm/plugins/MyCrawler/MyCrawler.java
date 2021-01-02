package cn.renlm.plugins.MyCrawler;

import java.util.function.Consumer;

import cn.edu.hfut.dmic.webcollector.plugin.berkeley.BreadthCrawler;

/**
 * 爬虫
 * 
 * @author 任黎明
 *
 */
public abstract class MyCrawler extends BreadthCrawler {
	private static final int threads = 2;
	private static final int topN = 100;
	private static final int maxExecuteCount = 10;
	private static final int executeInterval = 3000;

	/**
	 * 默认配置
	 * 
	 * @param crawlPath
	 * @param autoParse
	 * @param consumer
	 */
	public MyCrawler(String crawlPath, boolean autoParse, Consumer<MyCrawler> consumer) {
		super(crawlPath, autoParse);

		this.setThreads(threads);
		this.getConf().setTopN(topN);
		this.getConf().setMaxExecuteCount(maxExecuteCount);
		this.getConf().setExecuteInterval(executeInterval);
		consumer.accept(this);
	}
}