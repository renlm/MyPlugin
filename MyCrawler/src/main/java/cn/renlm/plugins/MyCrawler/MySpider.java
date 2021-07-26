package cn.renlm.plugins.MyCrawler;

import java.util.function.Consumer;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.SpiderListener;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.downloader.selenium.SeleniumDownloader;
import us.codecraft.webmagic.processor.PageProcessor;

/**
 * 爬虫
 * 
 * @author Renlm
 *
 */
public class MySpider extends Spider {

	/**
	 * 站点配置
	 */
	MySite site;

	/**
	 * 构造函数
	 * 
	 * @param site
	 * @param pageProcessor
	 */
	public MySpider(MySite site, PageProcessor pageProcessor) {
		super(pageProcessor);
		this.site = site;
	}

	/**
	 * 下载完成回调
	 * 
	 * @param page
	 * @return
	 */
	public MySpider onDownloaded(Consumer<Page> page) {
		if (StrUtil.isNotBlank(this.site.getSelenuimConfig())) {
			System.setProperty("selenuim_config", this.site.getSelenuimConfig());
			SeleniumDownloader downloader = new SeleniumDownloader() {
				@Override
				public Page download(Request request, Task task) {
					Page pager = super.download(request, task);
					page.accept(pager);
					return pager;
				}
			};
			String thread = System.getProperty("selenium.downloader.thread");
			if (StrUtil.isNotBlank(thread)) {
				downloader.setThread(NumberUtil.parseInt(thread));
			}
			String sleepTime = System.getProperty("selenium.downloader.sleepTime");
			if (StrUtil.isNotBlank(sleepTime)) {
				downloader.setSleepTime(NumberUtil.parseInt(sleepTime));
			}
			this.downloader = downloader;
		} else {
			this.downloader = new HttpClientDownloader() {
				@Override
				public Page download(Request request, Task task) {
					Page pager = super.download(request, task);
					page.accept(pager);
					return pager;
				}
			};
		}
		return this;
	}

	/**
	 * 成功回调
	 * 
	 * @param request
	 * @return
	 */
	public MySpider onSuccess(Consumer<Request> request) {
		if (CollUtil.isEmpty(this.getSpiderListeners())) {
			this.setSpiderListeners(CollUtil.newArrayList());
		}
		this.getSpiderListeners().add(new SpiderListener() {
			@Override
			public void onSuccess(Request req) {
				request.accept(req);
			}

			@Override
			public void onError(Request req) {

			}
		});
		return this;
	}

	/**
	 * 失败回调
	 * 
	 * @param request
	 * @return
	 */
	public MySpider onError(Consumer<Request> request) {
		if (CollUtil.isEmpty(this.getSpiderListeners())) {
			this.setSpiderListeners(CollUtil.newArrayList());
		}
		this.getSpiderListeners().add(new SpiderListener() {
			@Override
			public void onSuccess(Request req) {

			}

			@Override
			public void onError(Request req) {
				request.accept(req);
			}
		});
		return this;
	}
}