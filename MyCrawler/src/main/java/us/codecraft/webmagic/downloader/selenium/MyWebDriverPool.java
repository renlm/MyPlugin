package us.codecraft.webmagic.downloader.selenium;

import java.io.IOException;

/**
 * 浏览器驱动池
 * 
 * @author Renlm
 *
 */
class MyWebDriverPool extends WebDriverPool {

	public MyWebDriverPool(int capacity) {
		super(capacity);
	}

	@Override
	public void configure() throws IOException {
		super.configure();
	}
}