## 简介
爬虫工具封装。

## 安装
### Maven
在项目的pom.xml的dependencies中加入以下内容:

```xml
<dependency>
    <groupId>cn.renlm.plugins</groupId>
    <artifactId>MyCrawler</artifactId>
    <version>1.9.19</version>
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
@Slf4j
public class MyCrawlerTest {
	Setting chromeSetting = new Setting("chrome.setting");

	@Test
	public void test() {
		MySite site = MySite.me();
		site.setDomain("crawler.renlm.cn");
		site.setEnableSelenuim(true);
		site.setHeadless(false);
		site.setScreenshot(true);
		site.setChromeSetting(chromeSetting);
		site.addCookie(site.getDomain(), "XSRF-TOKEN", "e3d80ea9-d47e-4dc9-8b78-9352227a3d9b");
		site.addCookie(site.getDomain(), "SESSION", "ZDVlNzZiYzAtMmM5NS00MTg1LWI1MTEtNDRkOGZjY2QxNTE5");
		MySpider spider = MyCrawlerUtil.createSpider(site, myPage -> {
			Page page = myPage.page();
			Html html = page.getHtml();
			Console.log(html);
			log.info(myPage.screenshotBASE64());
		});
		spider.addUrl("https://crawler.renlm.cn/sys/const");
		spider.run();
	}
}
```