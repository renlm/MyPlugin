package cn.renlm.plugins;

import org.apache.fontbox.ttf.CmapLookup;
import org.junit.Test;

import cn.hutool.core.lang.Console;
import cn.hutool.core.util.ReUtil;
import cn.hutool.http.HtmlUtil;
import cn.hutool.setting.Setting;
import cn.renlm.plugins.MyCrawler.MySite;
import cn.renlm.plugins.MyCrawler.MySpider;
import cn.renlm.plugins.MyUtil.MyFontDecryptUtil;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.selector.Html;

/**
 * 爬虫（可配置Redis进行分布式链接去重）
 * 
 * @author Renlm
 *
 */
public class MyCrawlerTest {

	private static final String ttf = "(https://((?!(https://))[\\s\\S])*\\.ttf)";

	@Test
	public void httpClient() {
		MySpider spider = MyCrawlerUtil.createSpider(MySite.me(), myPage -> {
			Page page = myPage.page();
			// 避免加密字体转义
			page.setRawText(ReUtil.replaceAll(page.getRawText(), MyFontDecryptUtil.Regex, matcher -> {
				return HtmlUtil.escape(matcher.group());
			}));

			// 发现详情页链接，添加到下层任务
			String url = page.getUrl().get();
			Html html = page.getHtml();
			String regex = "(https://book.qidian.com/info/\\d+)";
			page.addTargetRequests(html.links().regex(regex).all());

			// 书籍详情页，抓取字段
			if (ReUtil.isMatch(regex, url)) {
				page.putField("cover", html.xpath("//div[@class='book-img']/a/img/@src").get());
				page.putField("name", html.xpath("//div[@class='book-info']/h1/em/text()").get());
				page.putField("author", html.xpath("//div[@class='book-info']/h1/span/a/text()").get());
				page.putField("intro", html.xpath("//div[@class='book-info']/p[@class='intro']/text()").get());
				page.putField("fonturl", html.xpath("//div[@class='book-info']/p[3]/em/style").regex(ttf).get());
				page.putField("wordNumber", html.xpath("//div[@class='book-info']/p[3]/em/span/text()").get());
				page.putField("wordNumberUnit", html.xpath("//div[@class='book-info']/p[3]/cite/text()").get());
			} else {
				page.setSkip(true);
			}
		}, myData -> {
			ResultItems resultItems = myData.resultItems();
			// 获取书籍详情，解密字数
			if (!resultItems.isSkip()) {
				String wordNumber = resultItems.get("wordNumber");
				CmapLookup cmap = MyFontDecryptUtil.getUnicodeCmapLookupFromTTF((String) resultItems.get("fonturl"));
				resultItems.put("wordNumber", MyFontDecryptUtil.fetchFromGlyphs(cmap, wordNumber));
				Console.log(resultItems);
			}
		});
		spider.addUrl("https://book.qidian.com");
		spider.run();
	}

	@Test
	public void selenuimByRedis() {
		MySite site = MySite.me();
		site.setEnableSelenuim(true);
		site.setChromeSetting(new Setting("config/chrome.setting"));
		MySpider spider = MyCrawlerUtil.createSpider(site, myPage -> {
			Page page = myPage.page();
			Html html = page.getHtml();
			System.out.println("公告名称：" + html.xpath("//div[@class='wzy_title']/text()").get());
			System.out.println("发布日期：" + html.xpath("//div[@class='time']/[contains(text(),发布日期)]/text()").get());
			System.out.println("交易中心：" + html.xpath("//div[@class='sourse']/[contains(text(),来源)]/text()").get().substring(4));
			System.out.println("交易中心-联 系 人："
					+ html.xpath("//div[@class='gg']/font/table[2]/tbody/tr[1]/td[2]/div/u/text()").get());
			System.out.println(
					"交易中心-电  话：" + html.xpath("//div[@class='gg']/font/table[2]/tbody/tr[2]/td[2]/u/text()").get());
			System.out.println(
					"项目概况：" + html.xpath("//div[@class='gg']/font/p[2]/u/[contains(text(),项目地址)]/text()").get());
			System.out.println("项目名称：" + html.xpath("//div[@class='gg']/font/p[1]/u[1]/text()").get());
			System.out.println(
					"招 标 人：" + html.xpath("//div[@class='gg']/font/table[1]/tbody/tr[1]/td[2]/u/text()").get());
			System.out.println(
					"招 标 人-联 系 人：" + html.xpath("//div[@class='gg']/font/table[1]/tbody/tr[2]/td[2]/u/text()").get());
			System.out.println(
					"招 标 人-电    话：" + html.xpath("//div[@class='gg']/font/table[1]/tbody/tr[3]/td[2]/u/text()").get());
			System.out.println(
					"招标代理机构：" + html.xpath("//div[@class='gg']/font/table[1]/tbody/tr[1]/td[5]/u/text()").get());
			System.out.println(
					"招标代理机构-联 系 人：" + html.xpath("//div[@class='gg']/font/table[1]/tbody/tr[2]/td[5]/u/text()").get());
			System.out.println(
					"招标代理机构-电    话：" + html.xpath("//div[@class='gg']/font/table[1]/tbody/tr[3]/td[5]/u/text()").get());
		});
		spider.addUrl("http://ggzy.guiyang.gov.cn/gcjs/zbgg_5372453/jl/202106/t20210630_68875084.html");
		spider.run();
	}
}