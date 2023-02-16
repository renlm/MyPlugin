package cn.renlm.plugins.MyCrawler.processor;

import cn.renlm.plugins.MyCrawler.data.MyProcessPage;

/**
 * 数据抓取
 * 
 * @author RenLiMing(任黎明)
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