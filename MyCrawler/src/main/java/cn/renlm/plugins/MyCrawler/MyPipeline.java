package cn.renlm.plugins.MyCrawler;

import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;

/**
 * 结果处理
 * 
 * @author Renlm
 *
 */
@FunctionalInterface
public interface MyPipeline {

	public void process(ResultItems resultItems, Task task);

}