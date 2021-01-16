package cn.renlm.plugins.MyCrawler.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.scheduler.component.DuplicateRemover;

/**
 * Pipeline Data
 * 
 * @author Renlm
 *
 */
@AllArgsConstructor
@Accessors(chain = true, fluent = true)
public class MyProcessPipe {

	@Getter
	private final Task task;

	@Getter
	private final ResultItems resultItems;

	private final DuplicateRemover duplicatedRemover;

	/**
	 * 请求是否重复
	 * 
	 * @param request
	 * @param task
	 * @return
	 */
	public final boolean isDuplicate(Request request, Task task) {
		return this.duplicatedRemover.isDuplicate(request, task);
	}
}