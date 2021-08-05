package cn.renlm.plugins;

import java.util.List;

import org.junit.Test;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
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
	public void fetchField() {
		MySite site = MySite.me();
		site.setEnableSelenuim(true);
		site.setChromeSetting(chromeSetting);
		site.setSleepTime(0);
		MySpider spider = MyCrawlerUtil.createSpider(site, myPage -> {
			Page page = myPage.page();
			String text = HtmlUtil.cleanHtmlTag(page.getRawText());
			text = ReUtil.replaceAll(text, "&nbsp;", StrUtil.EMPTY);

			// 模板1：https://www.bijie.gov.cn/bm/bjsggzyjyzx/jy/jsgc/zbgs/202107/t20210730_69356218.html
			String projectName = ReUtil.get("根据法律、法规、规章和招标文件的规定,(.*?)（入场登记号：(\\w+)）已于(\\d{4}年\\d{1,2}月\\d{1,2}日)在",
					text, 1);
			String company = null;
			String registrationNumber = ReUtil
					.get("根据法律、法规、规章和招标文件的规定,(.*?)（入场登记号：(\\w+)）已于(\\d{4}年\\d{1,2}月\\d{1,2}日)在", text, 2);
			String openDate = ReUtil.get("根据法律、法规、规章和招标文件的规定,(.*?)（入场登记号：(\\w+)）已于(\\d{4}年\\d{1,2}月\\d{1,2}日)在", text,
					3);
			String bid1th = ReUtil.get("第一中标候选人[^：:]*(：|:)[^\\S\\r\\n]*([^\\s\\p{P}]*)", text, 2);
			String bid1thPriceTitle = ReUtil.get("(([^\\s]*)(中标价|投标价|投标报价|工程报价|下浮率)+[^：:]*)(：|:)[^\\S\\r\\n]*([^\\s]*)",
					text, 1);
			String bid1thPriceValue = ReUtil.get("(([^\\s]*)(中标价|投标价|投标报价|工程报价|下浮率)+[^：:]*)(：|:)[^\\S\\r\\n]*([^\\s]*)",
					text, 5);
			String bid1thDurationTitle = ReUtil
					.get("(([^\\s]*)(工[^\\S\\r\\n]*期|监理服务期)[^：:]*)(：|:)[^\\S\\r\\n]*([^\\s]*)", text, 1);
			String bid1thDurationValue = ReUtil
					.get("(([^\\s]*)(工[^\\S\\r\\n]*期|监理服务期)[^：:]*)(：|:)[^\\S\\r\\n]*([^\\s]*)", text, 5);
			// 模板2：https://www.bijie.gov.cn/bm/bjsggzyjyzx/jy/jsgc/zbgs/202107/t20210730_69347845.html
			if (StrUtil.isBlank(projectName)) {
				projectName = ReUtil.get(
						"根据法律、法规、规章和招标文件的规定，(.*?)（招标人名称）的(.*?)（项目名称），\\(入场登记号：(\\w+)\\)已于(\\d{4}年\\d{1,2}月\\d{1,2}日)在",
						text, 1);
				company = ReUtil.get(
						"根据法律、法规、规章和招标文件的规定，(.*?)（招标人名称）的(.*?)（项目名称），\\(入场登记号：(\\w+)\\)已于(\\d{4}年\\d{1,2}月\\d{1,2}日)在",
						text, 2);
				registrationNumber = ReUtil.get(
						"根据法律、法规、规章和招标文件的规定，(.*?)（招标人名称）的(.*?)（项目名称），\\(入场登记号：(\\w+)\\)已于(\\d{4}年\\d{1,2}月\\d{1,2}日)在",
						text, 3);
				openDate = ReUtil.get(
						"根据法律、法规、规章和招标文件的规定，(.*?)（招标人名称）的(.*?)（项目名称），\\(入场登记号：(\\w+)\\)已于(\\d{4}年\\d{1,2}月\\d{1,2}日)在",
						text, 4);
			}
			// 模板3：https://www.bijie.gov.cn/bm/bjsggzyjyzx/jy/jsgc/zbgs/202107/t20210730_69353450.html
			if (StrUtil.isBlank(projectName)) {
				projectName = ReUtil.get(
						"根据法律、法规、规章和招标文件的规定, (.*?)（招标人名称）的(.*?)（项目名称）(\\w+) \\(入场登记号：\\)已于(\\d{4}年\\d{1,2}月\\d{1,2}日)在",
						text, 1);
				company = ReUtil.get(
						"根据法律、法规、规章和招标文件的规定, (.*?)（招标人名称）的(.*?)（项目名称）(\\w+) \\(入场登记号：\\)已于(\\d{4}年\\d{1,2}月\\d{1,2}日)在",
						text, 2);
				registrationNumber = ReUtil.get(
						"根据法律、法规、规章和招标文件的规定, (.*?)（招标人名称）的(.*?)（项目名称）(\\w+) \\(入场登记号：\\)已于(\\d{4}年\\d{1,2}月\\d{1,2}日)在",
						text, 3);
				openDate = ReUtil.get(
						"根据法律、法规、规章和招标文件的规定, (.*?)（招标人名称）的(.*?)（项目名称）(\\w+) \\(入场登记号：\\)已于(\\d{4}年\\d{1,2}月\\d{1,2}日)在",
						text, 4);
			}
			// 模板4：https://www.bijie.gov.cn/bm/bjsggzyjyzx/jy/jsgc/zbgs/202107/t20210727_69299539.html
			if (StrUtil.isBlank(projectName)) {
				projectName = ReUtil.get(
						"根据法律、法规、规章和招标文件的规定，(.*?)（项目编号为：(\\w+)），已于(\\d{4}年\\d{1,2}月\\d{1,2}日\\d{1,2}时\\d{1,2}分)在", text,
						1);
				registrationNumber = ReUtil.get(
						"根据法律、法规、规章和招标文件的规定，(.*?)（项目编号为：(\\w+)），已于(\\d{4}年\\d{1,2}月\\d{1,2}日\\d{1,2}时\\d{1,2}分)在", text,
						2);
				openDate = ReUtil.get(
						"根据法律、法规、规章和招标文件的规定，(.*?)（项目编号为：(\\w+)），已于(\\d{4}年\\d{1,2}月\\d{1,2}日\\d{1,2}时\\d{1,2}分)在", text,
						3);
			}
			System.out.println("====== " + projectName);
			System.out.println("====== " + company);
			System.out.println("====== " + registrationNumber);
			System.out.println("====== " + openDate);
			System.out.println("====== " + bid1th);
			System.out.println("====== " + bid1thPriceTitle);
			System.out.println("====== " + bid1thPriceValue);
			System.out.println("====== " + bid1thDurationTitle);
			System.out.println("====== " + bid1thDurationValue);
		});
		spider.addUrl("https://www.bijie.gov.cn/bm/bjsggzyjyzx/jy/jsgc/zbgs/202107/t20210730_69356218.html");
		spider.addUrl("https://www.bijie.gov.cn/bm/bjsggzyjyzx/jy/jsgc/zbgs/202107/t20210730_69347845.html");
		spider.addUrl("https://www.bijie.gov.cn/bm/bjsggzyjyzx/jy/jsgc/zbgs/202107/t20210730_69353450.html");
		spider.addUrl("https://www.bijie.gov.cn/bm/bjsggzyjyzx/jy/jsgc/zbgs/202107/t20210727_69299539.html");
		spider.addUrl("https://www.bijie.gov.cn/bm/bjsggzyjyzx/jy/jsgc/zbgs/202107/t20210715_69021375.html");
		spider.addUrl("https://www.bijie.gov.cn/bm/bjsggzyjyzx/jy/jsgc/zbgs/202108/t20210804_69404776.html");
		spider.addUrl("https://www.bijie.gov.cn/bm/bjsggzyjyzx/jy/jsgc/zbgs/202107/t20210715_69021376.html");
		spider.addUrl("https://www.bijie.gov.cn/bm/bjsggzyjyzx/jy/jsgc/zbgs/202005/t20200513_67402374.html");
		spider.run();
	}

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