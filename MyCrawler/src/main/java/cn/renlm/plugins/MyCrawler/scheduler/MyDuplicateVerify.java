package cn.renlm.plugins.MyCrawler.scheduler;

import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.scheduler.component.DuplicateRemover;

/**
 * Url去重管理
 * 
 * @author Renlm
 *
 */
public interface MyDuplicateVerify extends DuplicateRemover {

	boolean exist(Request request, Task task);

}