package cn.renlm.plugins;

import java.io.Serializable;

import org.junit.Test;

import cn.edu.hfut.dmic.webcollector.fetcher.Visitor;
import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.renlm.plugins.MyCrawler.CrawlerRequester;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
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
		MyCrawlerUtil.createDefault("crawl", true, crawler -> {
			crawler.addSeed("https://www.qidian.com");
			crawler.addRegex("https://book.qidian.com/info/.*");
			crawler.setRequester(requester);
			crawler.setVisitor(new Visitor() {
				@Override
				public void visit(Page page, CrawlDatums next) {
					if (page.matchUrl("https://book.qidian.com/info/.*")) {
						log.info(page.url());
					}
				}
			});
		}).start(4);
	}

	@Data
	@Accessors(chain = true)
	public class Book implements Serializable {

		private static final long serialVersionUID = 1L;

		/**
		 * 书名
		 */
		private String name;

		/**
		 * 作者
		 */
		private String author;

		/**
		 * 封面
		 */
		private String coverPhoto;

		/**
		 * 字数（万字）
		 */
		private Integer wordNumber;

		/**
		 * 简介
		 */
		private String briefIntroduction;

	}
}