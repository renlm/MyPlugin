package cn.renlm.plugins;

import org.junit.Test;

import cn.hutool.core.lang.Console;
import cn.hutool.setting.Setting;
import cn.renlm.plugins.MyCrawler.MySite;
import cn.renlm.plugins.MyCrawler.MySpider;
import lombok.extern.slf4j.Slf4j;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.selector.Html;

/**
 * 爬虫测试
 * 
 * @author Renlm
 *
 */
@Slf4j
public class MyCrawlerTest {
	Setting chromeSetting = new Setting("chrome.setting");

	@Test
	public void test() {
		MySite site = MySite.me();
		site.setDomain("crawler.renlm.cn");
		site.setEnableSelenuim(true);
		site.setHeadless(false);
		site.setScreenshot(true);
		site.setChromeSetting(chromeSetting);
		site.addCookie(site.getDomain(), "XSRF-TOKEN", "e3d80ea9-d47e-4dc9-8b78-9352227a3d9b");
		site.addCookie(site.getDomain(), "SESSION", "ZDVlNzZiYzAtMmM5NS00MTg1LWI1MTEtNDRkOGZjY2QxNTE5");
		MySpider spider = MyCrawlerUtil.createSpider(site, myPage -> {
			Page page = myPage.page();
			Html html = page.getHtml();
			Console.log(html);
			log.info(myPage.screenshotBASE64());
		});
		spider.addUrl("https://crawler.renlm.cn/sys/const");
		spider.run();
	}
}