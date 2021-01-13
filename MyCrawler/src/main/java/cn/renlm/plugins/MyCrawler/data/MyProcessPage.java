package cn.renlm.plugins.MyCrawler.data;

import cn.renlm.plugins.MyCrawler.MySite;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import us.codecraft.webmagic.Page;

/**
 * Processor Page
 * 
 * @author Renlm
 *
 */
@Getter
@AllArgsConstructor
@Accessors(chain = true, fluent = true)
public class MyProcessPage {

	private MySite site;

	private Page page;

}