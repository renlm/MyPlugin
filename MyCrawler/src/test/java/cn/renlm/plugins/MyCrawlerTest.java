package cn.renlm.plugins;

import java.util.List;

import org.junit.Test;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.setting.Setting;
import cn.renlm.plugins.MyCrawler.MySite;
import cn.renlm.plugins.MyCrawler.MySpider;
import cn.renlm.plugins.MyCrawler.PageUrlType;
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
	public void urlRegex() {
		MySite site = MySite.me();
		site.setEnableSelenuim(true);
		site.setChromeSetting(chromeSetting);
		MySpider spider = MyCrawlerUtil.createSpider(site, myPage -> {
			Page page = myPage.page();
			System.out.println();
			System.out.println();
			List<String> allUrls = CollUtil.removeBlank(CollUtil.distinct(page.getHtml().links().all()));
			allUrls.forEach(url -> {
				System.out.println("++++++ AllUrl: " + url);
			});
			System.out.println();
			System.out.println();
			List<String> seedUrls = CollUtil
					.removeBlank(CollUtil.distinct(page.getHtml().links().regex(seedUrlRegex, 0).all()));
			seedUrls.forEach(url -> {
				System.out.println("====== SeedStandardUrl: " + PageUrlType.standardUrl(url, "v"));
			});
			System.out.println();
			System.out.println();
			List<String> dataUrls = CollUtil
					.removeBlank(CollUtil.distinct(page.getHtml().links().regex(dataUrlRegex, 0).all()));
			dataUrls.forEach(url -> {
				System.out.println("****** DataStandardUrl: " + PageUrlType.standardUrl(url, "v"));
			});
			System.out.println();
			System.out.println();
		});
		spider.addUrl("http://ggzy.guiyang.gov.cn/gcjs/zbhxrgs/jl_5372461/index.html?i=9");
		spider.run();
	}
}