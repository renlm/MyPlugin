## 简介
爬虫工具封装。

## 安装
### Maven
在项目的pom.xml的dependencies中加入以下内容:

```xml
<dependency>
    <groupId>cn.renlm.plugins</groupId>
    <artifactId>MyCrawler</artifactId>
    <version>1.8.3</version>
</dependency>
```

### 案例

```
/**
 * 爬虫（可配置Redis进行分布式链接去重）
 * 
 * @author Renlm
 *
 */
public class MyCrawlerTest {

	private static final String ttf = "(https://((?!(https://))[\\s\\S])*\\.ttf)";

	@Test
	public void run() {
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
}
```