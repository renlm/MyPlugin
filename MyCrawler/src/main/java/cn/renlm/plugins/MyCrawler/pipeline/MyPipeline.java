package cn.renlm.plugins.MyCrawler.pipeline;

import cn.renlm.plugins.MyCrawler.data.MyProcessPipe;

/**
 * 结果处理
 * 
 * @author RenLiMing(任黎明)
 *
 */
@FunctionalInterface
public interface MyPipeline {

	void process(final MyProcessPipe myData);

}