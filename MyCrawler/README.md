## 简介
WebCollector爬虫。

## 安装
### Maven
在项目的pom.xml的dependencies中加入以下内容:

```xml
<dependency>
    <groupId>cn.renlm.plugins</groupId>
    <artifactId>MyCrawler</artifactId>
    <version>1.0.1.RELEASE</version>
</dependency>
```

## 使用

```
/**
 * 爬虫测试
 * 
 * @author Renlm
 *
 */
public class MyCrawlerTest {

	String REGEX_TTF = "https://((?!(https://))[\\s\\S])*\\.ttf";

	Dict GMAP = JSONUtil.toBean(ResourceUtil.readUtf8Str("GlyphCode.json"), Dict.class);

	@Test
	@SneakyThrows
	public void test() {
		// 启动爬虫
		CrawlerRequester requester = new CrawlerRequester();
		MyCrawlerUtil.defaultBreadthCrawler(new BreadthCrawler("crawl", true) {
			@Override
			public void visit(Page page, CrawlDatums next) {
				// 避免加密字体转义
				page.html(ReUtil.replaceAll(page.html(), FontDecryption.REGEX, matcher -> {
					return HtmlUtil.escape(matcher.group());
				}));
				// 书籍详情页
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
					Element wordNumberEl = el.select("div.book-info>p>em").first();
					String ttf = ReUtil.getGroup0(REGEX_TTF, wordNumberEl.select("style").html());
					CmapLookup cmap = FontDecryption.getUnicodeCmapLookupFromTTF(ttf);
					String glyphCodes = HtmlUtil.unescape(wordNumberEl.select("span").html());
					Double wordNumber = Double.valueOf(FontDecryption.fetchFromGlyphCode(GMAP, cmap, glyphCodes));
					String wordNumberUnit = el.select("div.book-info>p>cite").first().text();
					// 打印结果
					Console.log(cover, name, author, intro, wordNumber + wordNumberUnit);
				}
			}
		}, crawler -> {
			crawler.setThreads(1);
			crawler.addSeed("https://www.qidian.com");
			crawler.addRegex("https://book.qidian.com/info/.*");
			crawler.setRequester(requester);
		}).start(2);
	}
}
```