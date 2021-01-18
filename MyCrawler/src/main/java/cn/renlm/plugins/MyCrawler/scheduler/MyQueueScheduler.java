package cn.renlm.plugins.MyCrawler.scheduler;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.scheduler.QueueScheduler;

/**
 * 默认Url调度
 * 
 * @author Renlm
 *
 */
public class MyQueueScheduler extends QueueScheduler implements MyDuplicateVerify {

	private Set<String> urls = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());

	@Override
	public boolean verifyDuplicate(Request request, Task task) {
		return !urls.add(request.getUrl());
	}
}