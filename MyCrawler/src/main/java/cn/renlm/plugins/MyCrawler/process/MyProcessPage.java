package cn.renlm.plugins.MyCrawler.process;

import cn.renlm.plugins.MyCrawler.MySite;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import us.codecraft.webmagic.Page;

/**
 * PageProcessor
 * 
 * @author Renlm
 *
 * @param <T>
 */
@Getter
@AllArgsConstructor
@Accessors(chain = true, fluent = true)
public class MyProcessPage<T> {

	private MySite<T> site;

	private Page page;

}