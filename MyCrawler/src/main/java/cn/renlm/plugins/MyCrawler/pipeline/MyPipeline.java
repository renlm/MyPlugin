package cn.renlm.plugins.MyCrawler.pipeline;

import cn.renlm.plugins.MyCrawler.data.MyProcessPipe;

/**
 * 结果处理
 * 
 * @author Renlm
 *
 */
@FunctionalInterface
public interface MyPipeline {

	void process(final MyProcessPipe myData);

}