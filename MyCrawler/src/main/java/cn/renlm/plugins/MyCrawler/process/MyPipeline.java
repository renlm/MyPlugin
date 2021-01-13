package cn.renlm.plugins.MyCrawler.process;

import cn.renlm.plugins.MyCrawler.data.MyProcessPipe;

/**
 * 结果处理
 * 
 * @author Renlm
 *
 */
@FunctionalInterface
public interface MyPipeline {

	public void process(final MyProcessPipe myData);

}