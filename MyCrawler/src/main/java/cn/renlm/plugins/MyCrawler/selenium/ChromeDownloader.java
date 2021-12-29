package cn.renlm.plugins.MyCrawler.selenium;

import java.io.Closeable;
import java.io.IOException;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.setting.Setting;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.downloader.Downloader;
import us.codecraft.webmagic.selector.PlainText;

/**
 * 使用Selenium调用浏览器进行渲染
 * 
 * @author Renlm
 *
 */
public class ChromeDownloader implements Downloader, Closeable {

	private volatile ChromeDriverPool webDriverPool;

	private Logger logger = LoggerFactory.getLogger(getClass());

	private int sleepTime = 0;

	private int poolSize = 1;

	private Setting chromeSetting;

	public ChromeDownloader(Setting chromeSetting) {
		this.chromeSetting = chromeSetting;
		this.sleepTime = ObjectUtil.defaultIfNull(chromeSetting.getInt("sleepTime"), 0);
		this.checkInit();
	}

	@Override
	public Page download(Request request, Task task) {
		logger.info("downloading page " + request.getUrl());
		this.checkInit();
		WebDriver webDriver;
		try {
			webDriver = webDriverPool.get();
		} catch (InterruptedException e) {
			e.printStackTrace();
			return null;
		}

		try {
			// 设置自定义Cookies
			Site site = task.getSite();
			WebDriver.Options manage = webDriver.manage();
			if (site != null && site.getCookies() != null) {
				for (Map.Entry<String, String> cookieEntry : site.getCookies().entrySet()) {
					Cookie cookie = new Cookie(cookieEntry.getKey(), cookieEntry.getValue());
					manage.addCookie(cookie);
				}
			}
			// 请求并获取网页
			webDriver.get(request.getUrl());
			ThreadUtil.safeSleep(sleepTime);
			WebElement webElement = webDriver.findElement(By.xpath("/html"));
			String content = webElement.getAttribute("outerHTML");
			Page page = new Page();
			page.setRawText(content);
			page.setUrl(new PlainText(request.getUrl()));
			page.setRequest(request);
			return page;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			webDriverPool.returnToPool(webDriver);
		}
	}

	private void checkInit() {
		if (webDriverPool == null) {
			synchronized (this) {
				webDriverPool = new ChromeDriverPool(chromeSetting, poolSize);
			}
		}
	}

	@Override
	public void setThread(int thread) {
		this.poolSize = thread;
	}

	@Override
	public void close() throws IOException {
		webDriverPool.closeAll();
	}
}