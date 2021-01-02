package cn.renlm.plugins;

import java.util.Arrays;

import org.junit.Test;

import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.util.Config;
import cn.hutool.core.util.ZipUtil;
import cn.renlm.plugins.MyCrawler.CrawlerRequester;
import cn.renlm.plugins.MyCrawler.MyCrawler;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * 爬虫测试
 * 
 * @author Renlm
 *
 */
@Slf4j
public class CrawlerTest {

	@Test
	@SneakyThrows
	public void test() {
		CrawlerRequester requester = new CrawlerRequester();
		requester.setAccept(
				"text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3");
		requester.setAcceptEncoding("gzip, deflate");
		requester.setAcceptLanguage("zh-CN,zh;q=0.9,en;q=0.8");
		requester.setCacheControl("no-cache");
		requester.setConnection("keep-alive");
		requester.setCookie(
				"TRACKID=1924c23a-ea57-4e1b-bb9d-dc68c7504209; JSESSIONID=661598C5B26B6A550D24F99E73A5510A.tomcat-1; GSESSIONID=e36e499a-c19c-427d-bacb-3b93456d3db4");
		requester.setHost("pc.gcxx.com");
		requester.setPragma("no-cache");
		requester.setReferer("http://pc.gcxx.com/audit/project-v2/list");
		requester.setUpgradeInsecureRequests("1");
		requester.setUserAgent(
				"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.132 Safari/537.36");
		new MyCrawler("crawler", false, crawler -> {
			crawler.addSeed(Arrays.asList("http://pc.gcxx.com/project/newdetail/301473?isNeedShowAlert=1"));
			crawler.setRequester(requester);

			crawler.getConf().setConnectTimeout(Config.TIMEOUT_CONNECT * 5);
			crawler.getConf().setReadTimeout(Config.TIMEOUT_READ * 5);
		}) {
			@Override
			public void visit(Page page, CrawlDatums next) {
				String html = ZipUtil.unGzip(page.content(), page.charset());
				page.html(html);
				log.info(html);
			}
		}.start(1);
	}
}