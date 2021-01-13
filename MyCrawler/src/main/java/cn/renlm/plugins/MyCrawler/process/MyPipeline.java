package cn.renlm.plugins.MyCrawler.process;

import cn.renlm.plugins.MyCrawler.data.MyProcessPipe;

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