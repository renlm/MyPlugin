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
	Setting chromeSetting = new Setting("chrome.setting");

	@Test
	public void test() {
		MySite site = MySite.me();
		site.setDomain("crawler.renlm.cn");
		site.setEnableSelenuim(true);
		site.setChromeSetting(chromeSetting);
		site.addCookie(site.getDomain(), "menuClickTime", Convert.toStr(DateUtil.current()));
		site.addCookie(site.getDomain(), "XSRF-TOKEN", "e5b0c11d-12fe-476b-968f-78b438a6e1f4");
		site.addCookie(site.getDomain(), "SESSION", "Y2ZkYmFjNzktNzQ5ZC00ODcyLWE4MzYtNTU1NWQyMmExNDM4");
		MySpider spider = MyCrawlerUtil.createSpider(site, myPage -> {
			Page page = myPage.page();
			Html html = page.getHtml();
			List<List<String>> rows = new ArrayList<>();
			html.xpath("//div[@class='datagrid-view2']/div/div/table[@class='datagrid-htable']/tbody/tr").nodes()
					.forEach(tr -> {
						List<String> cols = new ArrayList<>();
						rows.add(cols);
						tr.xpath("/tr/td").nodes().forEach(td -> {
							String text = td.xpath("/td/div/span/text()").get();
							cols.add(text);
						});
					});
			html.xpath("//div[@class='datagrid-view2']/div/table[@class='datagrid-btable']/tbody/tr").nodes()
					.forEach(tr -> {
						List<String> cols = new ArrayList<>();
						rows.add(cols);
						tr.xpath("/tr/td").nodes().forEach(td -> {
							String text = td.xpath("/td/div/text()").get();
							cols.add(text);
						});
						Map<String, String> data = CollUtil.zip(rows.get(0), cols);
						Console.log(JSONUtil.toJsonPrettyStr(data));
					});
		});
		spider.addUrl("https://crawler.renlm.cn/sys/const?time=" + DateUtil.current());
		spider.addUrl("https://crawler.renlm.cn/sys/const?time=" + DateUtil.current());
		spider.run();
	}
}
```