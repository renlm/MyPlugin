package us.codecraft.webmagic.downloader.selenium;

/**
 * 浏览器模拟下载器
 * 
 * @author Renlm
 *
 */
public class MySeleniumDownloader extends SeleniumDownloader {

	public MySeleniumDownloader(int sleepTime) {
		this.setSleepTime(sleepTime);
	}
}