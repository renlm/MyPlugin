package cn.renlm.plugins.MyCrawler.data;

import cn.renlm.plugins.MyCrawler.scheduler.MyDuplicateVerify;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;

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

	private final MyDuplicateVerify duplicateVerify;

}