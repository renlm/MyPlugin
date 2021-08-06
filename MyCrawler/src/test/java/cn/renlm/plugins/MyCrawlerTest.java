package cn.renlm.plugins;

import java.util.List;

import org.apache.fontbox.ttf.CmapLookup;
import org.junit.Test;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.http.HtmlUtil;
import cn.hutool.setting.Setting;
import cn.renlm.plugins.MyCrawler.MySite;
import cn.renlm.plugins.MyCrawler.MySpider;
import cn.renlm.plugins.MyUtil.MyFontDecryptUtil;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.ResultItems;
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
		MySpider spider = MyCrawlerUtil.createSpider(site, myPage -> {
			Page page = myPage.page();

			// 避免加密字体转义
			page.setRawText(ReUtil.replaceAll(page.getRawText(), MyFontDecryptUtil.Regex, matcher -> {
				return HtmlUtil.escape(matcher.group());
			}));

			// 重置网页对象
			ReflectUtil.setFieldValue(page, "html", null);
			Html html = page.getHtml();
			ResultItems resultItems = page.getResultItems();

			// 获取详情页链接
			List<String> dataUrls = CollUtil
					.removeBlank(CollUtil.distinct(page.getHtml().links().regex(dataUrlRegex, 0).all()));
			myPage.page().addTargetRequests(dataUrls);

			// 详情页提取字段
			if (ReUtil.isMatch(dataUrlRegex, page.getRequest().getUrl())) {
				// 书名
				String name = html.xpath("//div[@class='book-info']/h1/em/text()").get();
				resultItems.put("name", name);

				// 作者
				String author = html.xpath("//div[@class='book-info']/h1/span/a/text()").get();
				resultItems.put("author", author);

				// 字数
				String fontUrl = html.xpath("//div[@class='book-info']/p[3]/em/style")
						.regex("(https://((?!(https://))[\\s\\S])*\\.ttf)").get();
				String wordNumberEncrypt = html.xpath("//div[@class='book-info']/p[3]/em/span/text()").get();
				CmapLookup cmap = MyFontDecryptUtil.getUnicodeCmapLookupFromTTF(fontUrl);
				String wordNumber = MyFontDecryptUtil.fetchFromGlyphs(cmap, wordNumberEncrypt);
				resultItems.put("wordNumber", NumberUtil.toBigDecimal(wordNumber));

				// 字数单位
				String wordNumberUnit = html.xpath("//div[@class='book-info']/p[3]/cite/text()").get();
				resultItems.put("wordNumberUnit", wordNumberUnit);
			}
		});
		spider.addUrl("https://www.qidian.com/wuxia");
		spider.addUrl("https://www.qidian.com/xianxia");
		spider.addUrl("https://www.qidian.com/kehuan");
		spider.run();
	}
}