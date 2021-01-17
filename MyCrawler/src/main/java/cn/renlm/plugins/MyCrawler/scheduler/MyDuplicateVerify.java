package cn.renlm.plugins.MyCrawler.scheduler;

import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;

/**
 * 任务请求验重
 * 
 * @author Renlm
 *
 */
public interface MyDuplicateVerify {

	boolean exist(Request request, Task task);

}