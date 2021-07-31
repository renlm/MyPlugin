package cn.renlm.plugins.MyCrawler.scheduler;

import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.scheduler.QueueScheduler;
import us.codecraft.webmagic.scheduler.component.DuplicateRemover;
import us.codecraft.webmagic.scheduler.component.HashSetDuplicateRemover;

/**
 * 默认Url调度
 * 
 * @author Renlm
 *
 */
public class MyQueueScheduler extends QueueScheduler implements MyDuplicateVerify {

	private DuplicateRemover verifyDuplicate = new HashSetDuplicateRemover();

	@Override
	public boolean verifyDuplicate(Boolean forceUpdate, Request request, Task task) {
		return verifyDuplicate.isDuplicate(request, task);
	}
}