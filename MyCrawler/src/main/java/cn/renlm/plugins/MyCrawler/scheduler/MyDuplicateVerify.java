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

	/**
	 * 预确认是否重复
	 * 
	 * @param forceUpdate
	 * @param request
	 * @param task
	 * @return
	 */
	boolean verifyDuplicate(Boolean forceUpdate, Request request, Task task);

}