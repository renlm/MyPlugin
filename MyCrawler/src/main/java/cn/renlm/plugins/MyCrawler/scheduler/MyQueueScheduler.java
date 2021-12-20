package cn.renlm.plugins.MyCrawler.scheduler;

import java.util.Set;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.ReflectUtil;
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
	public void cleanCache(Request request, Task task) {
		this.verifyDuplicate(true, request, task);
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean verifyDuplicate(Boolean forceUpdate, Request request, Task task) {
		boolean duplicate = verifyDuplicate.isDuplicate(request, task);
		if (BooleanUtil.isTrue(forceUpdate)) {
			Set<String> urls = (Set<String>) ReflectUtil.getFieldValue(verifyDuplicate, "urls");
			urls.remove(request.getUrl());
			return false;
		}
		return duplicate;
	}
}