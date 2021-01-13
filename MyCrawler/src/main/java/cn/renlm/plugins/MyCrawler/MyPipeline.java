package cn.renlm.plugins.MyCrawler;

import cn.renlm.plugins.MyCrawler.process.MyProcessPipe;

/**
 * 结果处理
 * 
 * @author Renlm
 *
 * @param <T>
 */
@FunctionalInterface
public interface MyPipeline<T> {

	public void process(final MyProcessPipe<T> myData);

}