package cn.renlm.plugins;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.fontbox.ttf.CmapLookup;
import org.apache.poi.ss.usermodel.Workbook;
import org.jsoup.nodes.Element;
import org.junit.Test;

import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.plugin.berkeley.BreadthCrawler;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.ReUtil;
import cn.hutool.http.HtmlUtil;
import cn.hutool.json.JSONUtil;
import cn.renlm.plugins.MyCrawler.CrawlerRequester;
import cn.renlm.plugins.MyCrawler.FontDecryption;
import cn.renlm.plugins.MyExcel.handler.DataWriterHandler;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;

/**
 * 爬虫测试
 * 
 * @author Renlm
 *
 */
public class MyCrawlerTest {

	String REGEX_TTF = "https://((?!(https://))[\\s\\S])*\\.ttf";

	Dict GMAP = JSONUtil.toBean(ResourceUtil.readUtf8Str("GlyphCode.json"), Dict.class);

	@Test
	@SneakyThrows
	public void test() {
		// 生成导出表格模板
		final AtomicReference<DataWriterHandler> sheet = new AtomicReference<DataWriterHandler>();
		final Workbook workbook = MyExcelUtil.createWorkbook("Book.xml", false, sh -> {
			sheet.set(sh);
		});
		// 启动爬虫
		CrawlerRequester requester = new CrawlerRequester();
		MyCrawlerUtil.defaultBreadthCrawler(new BreadthCrawler("crawl", true) {
			@Override
			public void visit(Page page, CrawlDatums next) {
				// 避免加密字体转义
				page.html(ReUtil.replaceAll(page.html(), FontDecryption.REGEX, matcher -> {
					return HtmlUtil.escape(matcher.group());
				}));
				// 书籍详情页
				if (page.matchUrl("https://book.qidian.com/info/.*")) {
					Element el = page.select("body>div.wrap>div.book-detail-wrap>div.book-information").first();
					// 封面
					String cover = el.select("div.book-img>a>img").attr("src");
					// 书名
					String name = el.select("div.book-info>h1>em").text();
					// 作者
					String author = el.select("div.book-info>h1>span>a").text();
					// 简介
					String intro = el.select("div.book-info>p.intro").text();
					// 字数
					Element wordNumberEl = el.select("div.book-info>p>em").first();
					String ttf = ReUtil.getGroup0(REGEX_TTF, wordNumberEl.select("style").html());
					CmapLookup cmap = FontDecryption.getUnicodeCmapLookupFromTTF(ttf);
					String glyphCodes = HtmlUtil.unescape(wordNumberEl.select("span").html());
					Double wordNumber = Double.valueOf(FontDecryption.fetchFromGlyphCode(GMAP, cmap, glyphCodes));
					String wordNumberUnit = el.select("div.book-info>p>cite").first().text();
					// 封装结果
					Book book = new Book().setCover(cover).setName(name).setAuthor(author).setIntro(intro)
							.setWordNumber(wordNumber).setWordNumberUnit(wordNumberUnit).setUrl(page.url());
					// 写入表格
					sheet.get().write(book);
				}
			}

			@Override
			public void afterStop() {
				super.afterStop();
				// 导出结果
				String path = FileUtil.getUserHomePath() + "/Desktop/起点小说网.xlsx";
				FileUtil.del(path);
				try (OutputStream stream = new FileOutputStream(path)) {
					workbook.write(stream);
				} catch (Exception e) {
					e.printStackTrace();
					IoUtil.close(workbook);
				}
			}
		}, crawler -> {
			crawler.setThreads(1);
			crawler.addSeed("https://www.qidian.com");
			crawler.addRegex("https://book.qidian.com/info/.*");
			crawler.setRequester(requester);
		}).start(2);
	}

	@Data
	@Accessors(chain = true)
	public class Book implements Serializable {

		private static final long serialVersionUID = 1L;

		/**
		 * 封面链接
		 */
		private String cover;

		/**
		 * 书名
		 */
		private String name;

		/**
		 * 作者
		 */
		private String author;

		/**
		 * 字数
		 */
		private Double wordNumber;

		/**
		 * 字数单位
		 */
		private String wordNumberUnit;

		/**
		 * 简介
		 */
		private String intro;

		/**
		 * 访问链接
		 */
		private String url;

	}
}