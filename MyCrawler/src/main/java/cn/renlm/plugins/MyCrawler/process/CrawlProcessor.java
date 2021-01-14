package cn.renlm.plugins.MyCrawler.process;

import cn.renlm.plugins.MyCrawler.data.MyProcessPage;

/**
 * 数据抓取
 * 
 * @author Renlm
 *
 */
public interface CrawlProcessor<T> {

	/**
	 * 处理过程
	 * 
	 * @param extra
	 * @param myPage
	 */
	void process(final T extra, final MyProcessPage myPage);

}