package cn.renlm.plugins;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Console;
import cn.hutool.json.JSONUtil;
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
	Setting chromeSetting = new Setting("chrome.setting");

	@Test
	public void test() {
		MySite site = MySite.me();
		site.setDomain("crawler.renlm.cn");
		site.setEnableSelenuim(true);
		site.setHeadless(false);
		site.setScreenshot(true);
		site.setChromeSetting(chromeSetting);
		site.addCookie(site.getDomain(), "menuClickTime", Convert.toStr(DateUtil.current()));
		site.addCookie(site.getDomain(), "XSRF-TOKEN", "e5b0c11d-12fe-476b-968f-78b438a6e1f4");
		site.addCookie(site.getDomain(), "SESSION", "Y2ZkYmFjNzktNzQ5ZC00ODcyLWE4MzYtNTU1NWQyMmExNDM4");
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
						if (rows.get(0).size() == cols.size()) {
							Map<String, String> data = CollUtil.zip(rows.get(0), cols);
							Console.log(JSONUtil.toJsonPrettyStr(data));
						}
					});
		});
		spider.addUrl("https://crawler.renlm.cn/sys/const?time=" + DateUtil.current());
		spider.addUrl("https://crawler.renlm.cn/sys/const?time=" + DateUtil.current());
		spider.run();
	}
}