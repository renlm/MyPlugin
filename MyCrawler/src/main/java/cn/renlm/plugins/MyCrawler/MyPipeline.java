package cn.renlm.plugins.MyCrawler;

import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;

/**
 * 结果处理
 * 
 * @author Renlm
 *
 * @param <T>
 */
@FunctionalInterface
public interface MyPipeline<T> {

	public void process(T extra, ResultItems resultItems, Task task);

}