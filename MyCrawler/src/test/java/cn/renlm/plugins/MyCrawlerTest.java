package cn.renlm.plugins;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.OutputStream;

import org.junit.jupiter.api.Test;

import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Console;
import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.Setting;
import cn.renlm.plugins.MyCrawler.MySite;
import cn.renlm.plugins.MyCrawler.MySpider;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.selector.Html;

/**
 * 爬虫测试
 * 
 * @author RenLiMing(任黎明)
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
		site.setDomain("mygraph.renlm.cn");
		site.setEnableSelenuim(true);
		site.setHeadless(false);
		site.setScreenshot(true);
		site.setChromeSetting(chromeSetting);
		site.addCookie(site.getDomain(), "XSRF-TOKEN", "a8fa2805-d01c-4373-a1e2-9d34426d2d76");
		site.addCookie(site.getDomain(), "SESSION", "MGEzMDg4YzAtNzc5OS00MjhlLTgxM2EtMjE4ZTEyNzY0Yzcz");
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
		spider.addUrl("https://mygraph.renlm.cn/graph/lib");
		spider.run();
	}
}