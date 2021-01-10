package cn.renlm.plugins;

import org.junit.Test;

import cn.hutool.core.lang.Console;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.processor.example.GithubRepoPageProcessor;

/**
 * 爬虫
 * 
 * @author Renlm
 *
 */
public class MyCrawlerTest {

	@Test
	public void run() {
		MyCrawlerUtil.createSpider(new GithubRepoPageProcessor(), new Pipeline() {
			@Override
			public void process(ResultItems resultItems, Task task) {
				Console.log(resultItems);
			}
		}, 5, "https://github.com/code4craft").run();
	}
}