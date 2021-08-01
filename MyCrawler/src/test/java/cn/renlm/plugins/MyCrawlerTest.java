package cn.renlm.plugins;

import java.util.List;

import org.junit.Test;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HtmlUtil;
import cn.hutool.setting.Setting;
import cn.renlm.plugins.MyCrawler.MySite;
import cn.renlm.plugins.MyCrawler.MySpider;
import cn.renlm.plugins.MyCrawler.PageUrlType;
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
				System.out.println("====== SeedStandardUrl: " + PageUrlType.standardUrl(url, false, "v"));
			});
			System.out.println();
			System.out.println();
			List<String> dataUrls = CollUtil
					.removeBlank(CollUtil.distinct(page.getHtml().links().regex(dataUrlRegex, 0).all()));
			dataUrls.forEach(url -> {
				System.out.println("****** DataStandardUrl: " + PageUrlType.standardUrl(url, false, "v"));
			});
			System.out.println();
			System.out.println();
		});
		spider.addUrl("http://ggzy.guiyang.gov.cn/gcjs/zbhxrgs/jl_5372461/index.html?i=9");
		spider.run();
	}

	@Test
	public void fetchPrice() {
		MySite site = MySite.me();
		site.setSleepTime(0);
		MySpider spider = MyCrawlerUtil.createSpider(site, myPage -> {
			Page page = myPage.page();
			Html html = page.getHtml();
			String field = html.xpath(
					"//font[@id='Zoom']/p//regex('<font[^>]*?>[^>]*?([\\u4e00-\\u9fa5]*投资：(\\w|[\\u4e00-\\u9fa5])+元)[^<]*?</font>',1)")
					.get();
			String field1 = html.xpath(
					"//font[@id='Zoom']/p//regex('<p[^>]*?><span[^>]*?><font[^>]*?>([^>]*?投标价[^<]*?)</font></span><b[^>]*?><span[^>]*?>(<font[^>]*?>(.*?)</font>)+</span></b></p>',1)")
					.get();
			String field2 = html.xpath(
					"//font[@id='Zoom']/p//regex('<p[^>]*?><span[^>]*?><font[^>]*?>([^>]*?投标价[^<]*?)</font></span><b[^>]*?><span[^>]*?>((<font[^>]*?>(.*?)</font>)+)</span></b></p>',2)")
					.get();
			String fieldValue = ObjectUtil.defaultIfBlank(field, field1 + HtmlUtil.unwrapHtmlTag(field2, "font"));
			System.out.println("====== " + fieldValue);
		});
		spider.addUrl("http://ggzyjy.zunyi.gov.cn/jyxx/gcjs/zbhxrgs/202107/t20210730_69356681.html");
		spider.addUrl("http://ggzyjy.zunyi.gov.cn/jyxx/gcjs/zbgg/202107/t20210729_69341605.html");
		spider.run();
	}

	@Test
	public void fetchDuration() {
		MySite site = MySite.me();
		site.setSleepTime(0);
		MySpider spider = MyCrawlerUtil.createSpider(site, myPage -> {
			Page page = myPage.page();
			Html html = page.getHtml();
			String field = html.xpath(
					"//font[@id='Zoom']/p//regex('<font[^>]*?>[^>]*?([\\u4e00-\\u9fa5]*工期：\\d+日历天)[^<]*?</font>',1)")
					.get();
			String field1 = html.xpath(
					"//font[@id='Zoom']/p//regex('<p[^>]*?><span[^>]*?><font[^>]*?>([^>]*?工期[^<]*?)</font></span><span[^>]*?><font[^>]*?>.*?</font></span><b[^>]*?><span[^>]*?>(<font[^>]*?>(.*?)</font>)+</span></b></p>',1)")
					.get();
			String field2 = html.xpath(
					"//font[@id='Zoom']/p//regex('<p[^>]*?><span[^>]*?><font[^>]*?>([^>]*?工期[^<]*?)</font></span><span[^>]*?><font[^>]*?>.*?</font></span><b[^>]*?><span[^>]*?>(<font[^>]*?>(.*?)</font>)+</span></b></p>',2)")
					.get();
			String fieldValue = ObjectUtil.defaultIfBlank(field, field1 + "：" + HtmlUtil.unwrapHtmlTag(field2, "font"));
			System.out.println("====== " + fieldValue);
		});
		spider.addUrl("http://ggzyjy.zunyi.gov.cn/jyxx/gcjs/zbhxrgs/202107/t20210730_69356681.html");
		spider.addUrl("http://ggzyjy.zunyi.gov.cn/jyxx/gcjs/zbgg/202107/t20210729_69341605.html");
		spider.run();
	}
}