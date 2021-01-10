package cn.renlm.plugins;

import org.junit.Test;

import cn.hutool.core.lang.Console;
import cn.hutool.core.util.ReUtil;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.selector.Html;

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
			String url = page.getUrl().get();
			Html html = page.getHtml();
			String regex = "(https://book.qidian.com/info/\\d+)";
			page.addTargetRequests(html.links().regex(regex).all());
			if (ReUtil.isMatch(regex, url)) {
				page.putField("name", html.xpath("//div[@class='book-info']/h1/em/text()").get());
				page.putField("author", html.xpath("//div[@class='book-info']/h1/span/a/text()").get());
				page.putField("intro", html.xpath("//div[@class='book-info']/p[@class='intro']/text()").get());
			} else {
				page.setSkip(true);
			}
		}, (resultItems, task) -> {
			if (!resultItems.isSkip()) {
				Console.log(resultItems);
			}
		}).run("https://book.qidian.com");
	}
}