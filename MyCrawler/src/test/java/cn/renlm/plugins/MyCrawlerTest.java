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
import lombok.extern.slf4j.Slf4j;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.selector.Html;

/**
 * 爬虫（可配置Redis进行分布式链接去重）
 * 
 * @author Renlm
 *
 */
@Slf4j
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
		}).onDownloaded(page -> {
			Console.log(page.getStatusCode());
		});
		spider.addUrl("https://book.qidian.com");
		spider.run();
	}

	@Test
	public void selenuim() {
		MySite site = MySite.me();
		site.setEnableSelenuim(true);
		site.setSelenuimSetting(new Setting());
		site.getSelenuimSetting().put("selenuimConfig",
				ConstVal.userDir + "/src/test/resources/chrome/selenuimConfig.ini");
		site.getSelenuimSetting().put("chromeDriverPath",
				ConstVal.userDir + "/src/test/resources/chrome/chromedriver.exe");
		site.getSelenuimSetting().put("thread", "1");
		site.getSelenuimSetting().put("sleepTime", "5000");
		MySpider spider = MyCrawlerUtil.createSpider(site, myPage -> {
			System.out.println(myPage.page().getHtml());
		}).onDownloaded(page -> {
			if (page.isDownloadSuccess()) {
				log.debug("{} download success.", page.getUrl());
			} else {
				log.error("{} download fail.", page.getUrl());
			}
		});
		spider.addUrl("http://ggzy.guiyang.gov.cn/gcjs/zbgg_5372453/jl/index.html?i=1&v=1627317850851");
		spider.run();
	}
}