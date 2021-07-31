package cn.renlm.plugins;

import java.util.List;
import java.util.stream.Collectors;

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
	public void seedTest() {
		MySite site = MySite.me();
		site.setEnableSelenuim(true);
		site.setChromeSetting(chromeSetting);
		MySpider spider = MyCrawlerUtil.createSpider(site, myPage -> {
			Page page = myPage.page();
			List<String> urls = page.getHtml().links().all();
			List<String> seedUrls = page.getHtml().links().regex(seedUrlRegex, 0).all().stream().distinct()
					.collect(Collectors.toList());
			List<String> dataUrls = page.getHtml().links().regex(dataUrlRegex, 0).all().stream().distinct()
					.collect(Collectors.toList());
			System.out.println();
			System.out.println();
			urls.forEach(url -> {
				System.out.println("++++++ AllUrl: " + url);
			});
			System.out.println();
			System.out.println();
			seedUrls.forEach(url -> {
				System.out.println("====== SeedUrl: " + url);
			});
			System.out.println();
			System.out.println();
			dataUrls.forEach(url -> {
				System.out.println("****** DataUrl: " + url);
			});
			System.out.println();
			System.out.println();
		});
		spider.addUrl("http://ggzy.guiyang.gov.cn/gcjs/zbhxrgs/jl_5372461/index.html?i=9");
		spider.run();
	}
}