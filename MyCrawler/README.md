## 简介
爬虫工具封装。

## 安装
### Maven
在项目的pom.xml的dependencies中加入以下内容:

```xml
<dependency>
    <groupId>cn.renlm.plugins</groupId>
    <artifactId>MyCrawler</artifactId>
    <version>2.0.1</version>
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
	static final String driverPath = FileUtil.normalize(ConstVal.resourcesTestDir + "/chromedriver.exe");
	static final String settingPath = FileUtil.normalize(ConstVal.resourcesTestDir + "/chrome.setting");
	static final Setting chromeSetting = new Setting(settingPath);

	static {
		chromeSetting.set("driverPath", driverPath);
		chromeSetting.store(settingPath);
	}

	@Test
	public void test() {
		MySite site = MySite.me();
		site.setDomain("crawler.renlm.cn");
		site.setEnableSelenuim(true);
		site.setHeadless(false);
		site.setScreenshot(true);
		site.setChromeSetting(chromeSetting);
		site.addCookie(site.getDomain(), "XSRF-TOKEN", "e8e91151-5de6-4244-89fe-a46d0c5996a5");
		site.addCookie(site.getDomain(), "SESSION", "ODQ5MTRjZGEtNTdjOS00MDJmLWIzNjUtOWU4MjVlZGI3ZGFh");
		MySpider spider = MyCrawlerUtil.createSpider(site, myPage -> {
			Page page = myPage.page();
			Html html = page.getHtml();
			Console.log(html);
			if (StrUtil.isNotBlank(myPage.screenshotBASE64())) {
				BufferedImage screenshot = ImgUtil.toImage(myPage.screenshotBASE64());
				File file = FileUtil.file(FileUtil.getUserHomePath() + "/Desktop/爬虫截屏.png");
				if (file.exists()) {
					file.delete();
				}
				OutputStream out = FileUtil.getOutputStream(file);
				ImgUtil.writePng(screenshot, out);
			}
		});
		spider.addUrl("https://crawler.renlm.cn/sys/const");
		spider.run();
	}
}
```