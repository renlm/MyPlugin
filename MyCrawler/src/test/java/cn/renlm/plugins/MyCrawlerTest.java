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
	String regex = ResourceUtil.readUtf8Str("regex.txt");
	Setting chromeSetting = new Setting("config/chrome.setting");

	@Test
	public void seedTest() {
		MySite site = MySite.me();
		site.setEnableSelenuim(true);
		site.setChromeSetting(chromeSetting);
		MySpider spider = MyCrawlerUtil.createSpider(site, myPage -> {
			Page page = myPage.page();
			List<String> urls = page.getHtml().links().all();
			List<String> urls2 = page.getHtml().links().regex(regex, 0).all().stream().distinct()
					.collect(Collectors.toList());
			urls.forEach(url -> {
				System.err.println(url);
			});
			urls2.forEach(url -> {
				System.out.println(url);
			});
		});
		spider.addUrl("http://ggzy.guiyang.gov.cn/gcjs/zbgg_5372453/jl/index.html");
		spider.run();
	}
}