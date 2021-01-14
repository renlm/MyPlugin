package cn.renlm.plugins.MyCrawler.processor;

import cn.renlm.plugins.MyCrawler.data.MyProcessPage;

/**
 * 页面处理
 * 
 * @author Renlm
 *
 */
@FunctionalInterface
public interface MyPageProcessor {

	void process(final MyProcessPage myPage);

}