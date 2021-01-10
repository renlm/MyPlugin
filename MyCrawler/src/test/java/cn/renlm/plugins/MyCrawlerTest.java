package cn.renlm.plugins;

import org.apache.fontbox.ttf.CmapLookup;
import org.junit.Test;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.lang.Console;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.ReUtil;
import cn.hutool.http.HtmlUtil;
import cn.hutool.json.JSONUtil;
import cn.renlm.plugins.MyUtil.MyFontDecryptUtil;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.selector.Html;

/**
 * 爬虫
 * 
 * @author Renlm
 *
 */
public class MyCrawlerTest {

	private static final String ttf = "(https://((?!(https://))[\\s\\S])*\\.ttf)";

	@Test
	public void run() {
		MyCrawlerUtil.createSpider(Site.me(), page -> {
			// 避免加密字体转义
			page.setRawText(ReUtil.replaceAll(page.getRawText(), MyFontDecryptUtil.REGEX, matcher -> {
				return HtmlUtil.escape(matcher.group());
			}));
			String url = page.getUrl().get();
			Html html = page.getHtml();
			String regex = "(https://book.qidian.com/info/\\d+)";
			page.addTargetRequests(html.links().regex(regex).all());
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
		}, (resultItems, task) -> {
			if (!resultItems.isSkip()) {
				String fonturl = resultItems.get("fonturl");
				String wordNumber = resultItems.get("wordNumber");
				Dict gmap = JSONUtil.toBean(ResourceUtil.readUtf8Str("config/glyph.map"), Dict.class);
				CmapLookup cmap = MyFontDecryptUtil.getUnicodeCmapLookupFromTTF(fonturl);
				resultItems.put("wordNumber", MyFontDecryptUtil.fetchFromGlyphs(gmap, cmap, wordNumber));
				Console.log(resultItems);
			}
		}).run("https://book.qidian.com");
	}
}