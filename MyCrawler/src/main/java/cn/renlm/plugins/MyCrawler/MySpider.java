package cn.renlm.plugins.MyCrawler;

import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

/**
 * 爬虫
 * 
 * @author Renlm
 *
 */
public class MySpider extends Spider {

	public MySpider(PageProcessor pageProcessor) {
		super(pageProcessor);
	}

	public void run(int threadNum, String... urls) {
		this.thread(threadNum).addUrl(urls).run();
	}

	public void runAsync(int threadNum, String... urls) {
		this.thread(threadNum).addUrl(urls).runAsync();
	}
}