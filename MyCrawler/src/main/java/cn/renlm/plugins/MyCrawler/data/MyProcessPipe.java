package cn.renlm.plugins.MyCrawler.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.scheduler.component.DuplicateRemover;

/**
 * Pipeline Data
 * 
 * @author Renlm
 *
 */
@Getter
@AllArgsConstructor
@Accessors(chain = true, fluent = true)
public class MyProcessPipe {

	private final Task task;

	private final ResultItems resultItems;

	private final DuplicateRemover duplicatedRemover;

}