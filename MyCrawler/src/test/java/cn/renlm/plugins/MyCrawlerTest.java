package cn.renlm.plugins;

import org.junit.Test;

import cn.edu.hfut.dmic.webcollector.fetcher.Visitor;
import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.renlm.plugins.MyCrawler.CrawlerRequester;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * 爬虫测试
 * 
 * @author Renlm
 *
 */
@Slf4j
public class MyCrawlerTest {

	@Test
	@SneakyThrows
	public void test() {
		CrawlerRequester requester = new CrawlerRequester();
		MyCrawlerUtil.createDefault("crawlerQidian", true, crawler -> {
			crawler.addSeed("https://www.qidian.com");
			crawler.addRegex("https://book.qidian.com/info/.*");
			crawler.setRequester(requester);
			crawler.setVisitor(new Visitor() {
				@Override
				public void visit(Page page, CrawlDatums next) {
					log.info(page.url());
				}
			});
		}).start(6);
	}
}