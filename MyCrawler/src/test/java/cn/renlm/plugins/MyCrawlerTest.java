package cn.renlm.plugins;

import org.apache.fontbox.ttf.CmapLookup;
import org.jsoup.nodes.Element;
import org.junit.Test;

import cn.edu.hfut.dmic.webcollector.fetcher.Visitor;
import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.lang.Console;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.ReUtil;
import cn.hutool.http.HtmlUtil;
import cn.hutool.json.JSONUtil;
import cn.renlm.plugins.MyCrawler.CrawlerRequester;
import cn.renlm.plugins.MyCrawler.FontDecryption;
import lombok.SneakyThrows;

/**
 * 爬虫测试
 * 
 * @author Renlm
 *
 */
public class MyCrawlerTest {
	
	String regexTtf = "https://((?!(https://))[\\s\\S])*\\.ttf";

	Dict gmap = JSONUtil.toBean(ResourceUtil.readUtf8Str("GlyphCode.json"), Dict.class);

	@Test
	@SneakyThrows
	public void test() {
		CrawlerRequester requester = new CrawlerRequester();
		MyCrawlerUtil.createDefault("crawl", true, crawler -> {
			crawler.setThreads(1);
			crawler.addSeed("https://www.qidian.com");
			crawler.addRegex("https://book.qidian.com/info/.*");
			crawler.setRequester(requester);
			crawler.setVisitor(new Visitor() {
				@Override
				public void visit(Page page, CrawlDatums next) {
					if (page.matchUrl("https://book.qidian.com/info/.*")) {
						Element el = page.select("body>div.wrap>div.book-detail-wrap>div.book-information").first();
						// 封面
						String cover = el.select("div.book-img>a>img").attr("src");
						// 书名
						String name = el.select("div.book-info>h1>em").text();
						// 作者
						String author = el.select("div.book-info>h1>span>a").text();
						// 简介
						String intro = el.select("div.book-info>p.intro").text();
						// 字数
						Element wdEl = el.select("div.book-info>p>em").first();
						String ttf = ReUtil.getGroup0(regexTtf, wdEl.select("style").html());
						CmapLookup cmap = FontDecryption.getUnicodeCmapLookupFromTTF(ttf);
						String glyphCodes = HtmlUtil.unescape(wdEl.select("span").text());
						String wordNumber = FontDecryption.fetchFromGlyphCode(gmap, cmap, glyphCodes);
						Console.log(cover, name, author, intro, ttf, wordNumber);
					}
				}
			});
		}).start(4);
	}
}