package cn.renlm.plugins.MyCrawler.scheduler;

import java.util.Set;

import cn.hutool.core.util.ReflectUtil;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.scheduler.QueueScheduler;
import us.codecraft.webmagic.scheduler.component.HashSetDuplicateRemover;

/**
 * 默认Url调度
 * 
 * @author Renlm
 *
 */
public class MyQueueScheduler extends QueueScheduler implements MyDuplicateVerify {

	@Override
	@SuppressWarnings("unchecked")
	public boolean exist(Request request, Task task) {
		HashSetDuplicateRemover duplicatedRemover = (HashSetDuplicateRemover) getDuplicateRemover();
		Set<String> urls = (Set<String>) ReflectUtil.getFieldValue(duplicatedRemover, "urls");
		return urls.contains(request.getUrl());
	}
}