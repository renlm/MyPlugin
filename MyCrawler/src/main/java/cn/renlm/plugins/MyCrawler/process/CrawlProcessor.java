package cn.renlm.plugins.MyCrawler.process;

import cn.renlm.plugins.MyCrawler.data.MyProcessPage;

/**
 * 数据抓取
 * 
 * @author Renlm
 *
 */
public interface CrawlProcessor<T> {

	void process(final T extra, final MyProcessPage myPage);

}