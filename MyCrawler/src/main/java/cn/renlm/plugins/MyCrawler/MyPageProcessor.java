package cn.renlm.plugins.MyCrawler;

import us.codecraft.webmagic.Page;

/**
 * 页面处理
 * 
 * @author renlm-a
 *
 * @param <T>
 */
@FunctionalInterface
public interface MyPageProcessor<T> {

	public void process(T extra, Page page);

}