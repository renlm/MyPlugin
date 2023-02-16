package cn.renlm.plugins.MyCrawler.data;

import cn.renlm.plugins.MyCrawler.MySite;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import us.codecraft.webmagic.Page;

/**
 * Processor Page
 * 
 * @author RenLiMing(任黎明)
 *
 */
@Getter
@AllArgsConstructor
@Accessors(chain = true, fluent = true)
public class MyProcessPage {

	private int depth;

	private MySite site;

	private Page page;

	/**
	 * 截屏图片BASE64
	 */
	private String screenshotBASE64;

}