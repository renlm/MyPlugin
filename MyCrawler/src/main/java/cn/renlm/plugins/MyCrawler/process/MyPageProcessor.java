package cn.renlm.plugins.MyCrawler.process;

import cn.renlm.plugins.MyCrawler.data.MyProcessPage;

/**
 * 页面处理
 * 
 * @author Renlm
 *
 */
@FunctionalInterface
public interface MyPageProcessor {

	public void process(final MyProcessPage myPage);

}