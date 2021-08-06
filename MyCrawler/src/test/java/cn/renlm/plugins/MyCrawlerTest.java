package cn.renlm.plugins;

import org.junit.Test;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.setting.Setting;
import cn.renlm.plugins.MyCrawler.MySite;
import cn.renlm.plugins.MyCrawler.MySpider;
import us.codecraft.webmagic.Page;

/**
 * 爬虫测试
 * 
 * @author Renlm
 *
 */
public class MyCrawlerTest {
	String seedUrlRegex = ResourceUtil.readUtf8Str("seedUrl.regex");
	String dataUrlRegex = ResourceUtil.readUtf8Str("dataUrl.regex");
	Setting chromeSetting = new Setting("config/chrome.setting");

	@Test
	public void fetchField() {
		MySite site = MySite.me();
		site.setEnableSelenuim(false);
		site.setChromeSetting(chromeSetting);
		MySpider spider = MyCrawlerUtil.createSpider(site, myPage -> {
			Page page = myPage.page();
			System.out.println(page.getHtml().xpath("//div[@class='book-info']/h1/em/text()").get());
		});
		spider.addUrl("https://book.qidian.com/info/107580");
		spider.run();
	}
}