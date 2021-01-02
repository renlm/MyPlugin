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
@Test
@SneakyThrows
public void test() {
	CrawlerRequester requester = new CrawlerRequester();
	MyCrawlerUtil.createDefault("crawl", true, crawler -> {
		crawler.addSeed("https://www.qidian.com");
		crawler.addRegex("https://book.qidian.com/info/.*");
		crawler.setRequester(requester);
		crawler.setVisitor(new Visitor() {
			@Override
			public void visit(Page page, CrawlDatums next) {
				if (page.matchUrl("https://book.qidian.com/info/.*")) {
					log.info(page.url());
				}
			}
		});
	}).start(4);
}
```