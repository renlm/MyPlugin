package cn.renlm.plugins.MyCrawler.data;

import cn.renlm.plugins.MyCrawler.MySite;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import us.codecraft.webmagic.ResultItems;

/**
 * Pipeline Data
 * 
 * @author Renlm
 *
 * @param <T>
 */
@Getter
@AllArgsConstructor
@Accessors(chain = true, fluent = true)
public class MyProcessPipe<T> {

	private String uuid;

	private MySite<T> site;

	private ResultItems resultItems;

}