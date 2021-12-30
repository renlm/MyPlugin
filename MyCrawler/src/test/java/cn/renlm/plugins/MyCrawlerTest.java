package cn.renlm.plugins;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.setting.Setting;
import cn.renlm.plugins.MyCrawler.MySite;
import cn.renlm.plugins.MyCrawler.MySpider;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.selector.Html;

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

	/**
	 * 添加Cookie
	 */
	@Test
	public void addCookie() {
		MySite site = MySite.me();
		site.setDomain("crawler.renlm.cn");
		site.setEnableSelenuim(true);
		site.setChromeSetting(chromeSetting);
		site.addCookie(site.getDomain(), "menuClickTime", Convert.toStr(DateUtil.current()));
		site.addCookie(site.getDomain(), "XSRF-TOKEN", "37e84571-8298-4afa-a029-13c801ec5567");
		site.addCookie(site.getDomain(), "SESSION", "YTkwZjYzY2QtZTdmZi00ZjViLWEzM2MtYWUyZjQ1MzFhOWVi");
		MySpider spider = MyCrawlerUtil.createSpider(site, myPage -> {
			Page page = myPage.page();
			Html html = page.getHtml();
			List<List<String>> rows = new ArrayList<>();
			html.xpath("//div[@class='datagrid-view2']/div/div/table[@class='datagrid-htable']/tbody/tr").nodes()
					.forEach(tr -> {
						List<String> cols = new ArrayList<>();
						rows.add(cols);
						tr.xpath("/tr/td").nodes().forEach(td -> {
							String text = td.xpath("/td/div/span/text()").get();
							cols.add(text);
						});
					});
			html.xpath("//div[@class='datagrid-view2']/div/table[@class='datagrid-btable']/tbody/tr").nodes()
					.forEach(tr -> {
						List<String> cols = new ArrayList<>();
						rows.add(cols);
						tr.xpath("/tr/td").nodes().forEach(td -> {
							String text = td.xpath("/td/div/text()").get();
							cols.add(text);
						});
						CollUtil.zip(rows.get(0), cols).forEach((key, value) -> {
							page.getResultItems().put(key, value);
						});
					});
		});
		spider.addUrl("https://crawler.renlm.cn/log/login");
		spider.addUrl("https://crawler.renlm.cn/log/login?pageNum=1");
		spider.addUrl("https://crawler.renlm.cn/log/login?pageNum=2");
		spider.run();
	}
}