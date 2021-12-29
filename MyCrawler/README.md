## 简介
爬虫工具封装。

## 安装
### Maven
在项目的pom.xml的dependencies中加入以下内容:

```xml
<dependency>
    <groupId>cn.renlm.plugins</groupId>
    <artifactId>MyCrawler</artifactId>
    <version>1.9.15</version>
</dependency>
```

### 案例

```
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

	/**
	 * 提取字段
	 */
	@Test
	public void fetchField() {
		MySite site = MySite.me();
		site.setSleepTime(20);
		MySpider spider = MyCrawlerUtil.createSpider(site, myPage -> {
			Page page = myPage.page();
			Html html = page.getHtml();

			List<List<String>> rows = new ArrayList<>();
			html.xpath("//font[@id='Zoom']/div/table/tbody/tr").nodes().forEach(tr -> {
				List<String> cols = new ArrayList<>();
				rows.add(cols);
				tr.xpath("/tr/td").nodes().forEach(td -> {
					String text = StrUtil.trim(HtmlUtil.cleanHtmlTag(td.get()));
					cols.add(ReUtil.delAll("\\s", ReUtil.delAll("\\p{Z}", text)));
				});
				if (rows.size() == 2) {
					Map<String, String> data = CollUtil.zip(rows.get(0), cols);
					data.forEach((k, v) -> {
						if (StrUtil.containsAny(k, "成交价", "成交总价")) {
							System.out.println(k + "：" + v);
						}
						if (StrUtil.containsAny(k, "受让人名称", "竞得单位名称", "竞得人")) {
							System.out.println(k + "：" + v);
						}
					});
				}
			});
		});
		spider.addUrl("http://ggzy.guiyang.gov.cn/tdcr/cjjggs/xfx_5372564/201302/t20130219_61321885.html");
		spider.addUrl("http://ggzy.guiyang.gov.cn/tdcr/cjjggs/nmq_5372559/201304/t20130403_61321892.html");
		spider.addUrl("http://ggzy.guiyang.gov.cn/tdcr/cjjggs/nmq_5372559/202009/t20200903_62859936.html");
		spider.run();
	}

	/**
	 * 查找链接
	 */
	@Test
	public void findUrls() {
		MySite site = MySite.me();
		site.setEnableSelenuim(true);
		site.setChromeSetting(chromeSetting);
		MySpider spider = MyCrawlerUtil.createSpider(site, myPage -> {
			Page page = myPage.page();
			System.out.println();
			System.out.println();
			List<String> seedUrls = CollUtil
					.removeBlank(CollUtil.distinct(page.getHtml().links().regex(seedUrlRegex, 0).all()));
			seedUrls.forEach(url -> {
				System.out.println("=== SEED === " + PageUrlType.standardUrl(url, false, "v"));
			});
			System.out.println();
			System.out.println();
			List<String> dataUrls = CollUtil
					.removeBlank(CollUtil.distinct(page.getHtml().links().regex(dataUrlRegex, 0).all()));
			dataUrls.forEach(url -> {
				System.out.println("=== DATA === " + PageUrlType.standardUrl(url, false, "v"));
			});
			System.out.println();
			System.out.println();
		});
		spider.addUrl("http://ggzy.guiyang.gov.cn/gcjs/zbhxrgs/jl_5372461/index.html?i=9");
		spider.run();
	}
}
```