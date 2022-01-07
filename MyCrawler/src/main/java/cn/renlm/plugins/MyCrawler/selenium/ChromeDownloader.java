package cn.renlm.plugins.MyCrawler.selenium;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.setting.Setting;
import cn.renlm.plugins.MyCrawlerUtil;
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

	private int sleepTime = 100;

	private int poolSize = 1;

	private Setting chromeSetting;

	public ChromeDownloader(Setting chromeSetting) {
		this.chromeSetting = chromeSetting;
		this.sleepTime = chromeSetting.getInt("sleepTime", 100);
		this.checkInit();
	}

	@Override
	public Page download(Request request, Task task) {
		String url = request.getUrl();
		logger.info("downloading page " + url);
		this.checkInit();
		Page page = Page.fail();
		MyChromeDriver myChromeDriver;
		ChromeDriver webDriver;
		try {
			myChromeDriver = webDriverPool.get();
			webDriver = myChromeDriver.getWebDriver();
		} catch (Exception e) {
			e.printStackTrace();
			return page;
		}
		try {
			webDriver.get(url);
			this.addCookies(webDriver, url, task.getSite());
			ThreadUtil.safeSleep(sleepTime);
			// 截屏
			if (chromeSetting.getBool("screenshot", false)) {
				JavascriptExecutor jse = (JavascriptExecutor) webDriver;
				Long width = (Long) jse.executeScript("return document.documentElement.scrollWidth");
				Long height = (Long) jse.executeScript("return document.documentElement.scrollHeight");
				webDriver.manage().window().setSize(new Dimension(width.intValue(), height.intValue()));
				String screenshotBASE64 = webDriver.getScreenshotAs(OutputType.BASE64);
				request.putExtra(MyCrawlerUtil.screenshotBASE64ExtraKey, screenshotBASE64);
			}
			// 页面
			WebElement webElement = webDriver.findElement(By.xpath("/html"));
			String content = webElement.getAttribute("outerHTML");
			page.setRawText(content);
			page.setUrl(new PlainText(request.getUrl()));
			page.setRequest(request);
			page.setDownloadSuccess(true);
			return page;
		} catch (Exception e) {
			e.printStackTrace();
			return page;
		} finally {
			webDriverPool.returnToPool(myChromeDriver);
		}
	}

	/**
	 * 驱动初始化
	 */
	private void checkInit() {
		if (webDriverPool == null) {
			synchronized (this) {
				webDriverPool = new ChromeDriverPool(chromeSetting, poolSize);
			}
		}
	}

	/**
	 * 访问页面，设置Cookie后重新访问
	 * 
	 * @param webDriver
	 * @param url
	 * @param site
	 */
	private void addCookies(WebDriver webDriver, String url, Site site) {
		Map<String, Map<String, String>> cookies = site.getAllCookies();
		Map<String, String> defaultCookies = site.getCookies();
		if (MapUtil.isNotEmpty(defaultCookies)) {
			String domain = StrUtil.blankToDefault(site.getDomain(), StrUtil.EMPTY);
			defaultCookies.forEach((name, value) -> {
				if (!cookies.containsKey(domain)) {
					cookies.put(domain, new HashMap<String, String>());
				}
				cookies.get(domain).put(name, value);
			});
		}
		if (MapUtil.isEmpty(cookies)) {
			return;
		}
		WebDriver.Options manage = webDriver.manage();
		Set<Cookie> currentCookies = manage.getCookies();
		boolean addCookie = CollUtil.isEmpty(currentCookies);
		Map<String, Cookie> map = new LinkedHashMap<>();
		cookies.forEach((_domain, cookieMap) -> {
			cookieMap.forEach((_name, _value) -> {
				String name = StrUtil.trimToNull(_name);
				String value = StrUtil.trimToNull(_value);
				String domain = StrUtil.removePrefix(StrUtil.trimToNull(_domain), StrUtil.DOT);
				if (StrUtil.isNotBlank(name) && StrUtil.isNotBlank(value)) {
					Cookie cookie = new Cookie(name, value, domain, null, null);
					map.put(JSONUtil.toJsonStr(cookie), cookie);
				}
			});
		});
		if (CollUtil.isNotEmpty(currentCookies)) {
			for (Cookie item : currentCookies) {
				String name = StrUtil.trimToNull(item.getName());
				String value = StrUtil.trimToNull(item.getValue());
				String domain = StrUtil.removePrefix(StrUtil.trimToNull(item.getDomain()), StrUtil.DOT);
				if (StrUtil.isNotBlank(name) && StrUtil.isNotBlank(value)) {
					Cookie cookie = new Cookie(name, value, domain, null, null);
					addCookie = !map.containsKey(JSONUtil.toJsonStr(cookie));
					if (addCookie) {
						break;
					}
				}
			}
		}
		if (addCookie && MapUtil.isNotEmpty(map)) {
			manage.deleteAllCookies();
			map.forEach((jsonKey, cookie) -> {
				manage.addCookie(cookie);
			});
			webDriver.navigate().to(url);
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