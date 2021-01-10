package cn.renlm.plugins;

import org.junit.Test;

import us.codecraft.webmagic.Site;

/**
 * 爬虫
 * 
 * @author Renlm
 *
 */
public class MyCrawlerTest {

	@Test
	public void run() {
		MyCrawlerUtil.createSpider(Site.me(), page -> {

		}, (resultItems, task) -> {

		}).run("");
	}
}