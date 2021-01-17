package cn.renlm.plugins.MyCrawler.scheduler;

import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.scheduler.QueueScheduler;

/**
 * 默认Url调度程序
 * 
 * @author Renlm
 *
 */
public class MyQueueScheduler extends QueueScheduler implements MyDuplicateVerify {

	@Override
	public boolean exist(Request request, Task task) {
		return false;
	}
}