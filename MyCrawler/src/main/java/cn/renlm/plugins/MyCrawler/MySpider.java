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

	public void run(String... urls) {
		this.addUrl(urls);
		super.run();
	}

	public void runAsync(String... urls) {
		this.addUrl(urls);
		super.runAsync();
	}
}