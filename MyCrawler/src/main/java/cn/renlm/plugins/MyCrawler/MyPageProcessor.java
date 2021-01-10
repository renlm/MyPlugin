package cn.renlm.plugins.MyCrawler;

import us.codecraft.webmagic.Page;

/**
 * 页面处理
 * 
 * @author Renlm
 *
 */
@FunctionalInterface
public interface MyPageProcessor {

	public void process(Page page);

}