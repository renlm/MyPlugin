package cn.renlm.plugins.MyCrawler.selenium;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.Setting;
import lombok.extern.slf4j.Slf4j;

/**
 * 网页驱动
 * 
 * @author Renlm
 *
 */
@Slf4j
class ChromeDriverPool {
	private final static int STAT_RUNNING = 1;
	private final static int STAT_CLODED = 2;

	private AtomicInteger stat = new AtomicInteger(STAT_RUNNING);
	private List<MyChromeDriver> webDriverList = Collections.synchronizedList(new ArrayList<MyChromeDriver>());
	private BlockingDeque<MyChromeDriver> innerQueue = new LinkedBlockingDeque<MyChromeDriver>();

	private final Setting chromeSetting;
	private final int capacity;
	private MyChromeDriver mDriver = null;

	public ChromeDriverPool(Setting chromeSetting, int capacity) {
		this.chromeSetting = chromeSetting;
		this.capacity = NumberUtil.max(capacity, 1);
	}

	public void configure() throws IOException {
		boolean headless = chromeSetting.getBool("headless", false);
		String windowSize = chromeSetting.getStr("windowSize", "1280,720");
		String driverPath = chromeSetting.getStr("driverPath");
		if (!StrUtil.equals(driverPath, System.getProperty(ChromeDriverService.CHROME_DRIVER_EXE_PROPERTY))) {
			System.setProperty(ChromeDriverService.CHROME_DRIVER_EXE_PROPERTY, driverPath);
		}

		// https://sites.google.com/a/chromium.org/chromedriver/capabilities
		// https://peter.sh/experiments/chromium-command-line-switches
		ChromeOptions options = new ChromeOptions();
		options.setHeadless(headless);
		options.addArguments("--no-sandbox");
		options.addArguments("--disable-gpu");
		options.addArguments("--disable-dev-shm-usage");
		options.addArguments("--window-size=" + windowSize);
		options.setExperimentalOption("excludeSwitches", CollUtil.newArrayList("enable-automation"));
		ChromeDriverService service = ChromeDriverService.createDefaultService();
		ChromeDriver chromeDriver = new ChromeDriver(service, options);
		mDriver = new MyChromeDriver(chromeDriver, service);
	}

	public MyChromeDriver get() throws InterruptedException {
		checkRunning();
		MyChromeDriver poll = innerQueue.poll();
		if (poll != null) {
			return poll;
		}
		if (webDriverList.size() < capacity) {
			synchronized (webDriverList) {
				if (webDriverList.size() < capacity) {
					try {
						configure();
						innerQueue.add(mDriver);
						webDriverList.add(mDriver);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return innerQueue.take();
	}

	public void returnToPool(MyChromeDriver webDriver) {
		checkRunning();
		innerQueue.add(webDriver);
	}

	protected void checkRunning() {
		if (!stat.compareAndSet(STAT_RUNNING, STAT_RUNNING)) {
			throw new IllegalStateException("Already closed!");
		}
	}

	public void closeAll() {
		if (!stat.compareAndSet(STAT_RUNNING, STAT_CLODED)) {
			throw new IllegalStateException("Already closed!");
		}
		for (MyChromeDriver myChromeDriver : webDriverList) {
			WebDriver webDriver = myChromeDriver.getWebDriver();
			ChromeDriverService service = myChromeDriver.getService();
			log.info("Quit webDriver" + webDriver);
			webDriver.quit();
			service.stop();
		}
	}
}