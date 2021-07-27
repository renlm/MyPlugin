package cn.renlm.plugins.MyCrawler;

import java.util.function.Consumer;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.Setting;
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
	 * 构造函数
	 * 
	 * @param pageProcessor
	 */
	public MySpider(PageProcessor pageProcessor) {
		super(pageProcessor);
	}

	/**
	 * 下载完成回调
	 * 
	 * @param site
	 * @param page
	 * @return
	 */
	public MySpider onDownloaded(MySite site, Consumer<Page> page) {
		if (ObjectUtil.isNotEmpty(site) && site.isEnableSelenuim() && ObjectUtil.isNotEmpty(site.getSelenuimSetting())
				&& StrUtil.isNotBlank(site.getSelenuimSetting().getStr("chromeDriverPath"))
				&& StrUtil.isNotBlank(site.getSelenuimSetting().getStr("selenuimConfig"))) {
			Setting selenuim = site.getSelenuimSetting();
			String chromeDriverPath = selenuim.getStr("chromeDriverPath");
			System.setProperty("selenuim_config", selenuim.getStr("selenuimConfig"));
			int thread = ObjectUtil.defaultIfNull(selenuim.getInt("thread"), 1);
			int sleepTime = ObjectUtil.defaultIfNull(selenuim.getInt("sleepTime"), 1000);
			SeleniumDownloader downloader = new SeleniumDownloader(chromeDriverPath) {
				@Override
				public Page download(Request request, Task task) {
					Page pager = super.download(request, task);
					page.accept(pager);
					return pager;
				}
			};
			downloader.setThread(thread);
			downloader.setSleepTime(sleepTime);
			this.setDownloader(downloader);
		} else {
			HttpClientDownloader downloader = new HttpClientDownloader() {
				@Override
				public Page download(Request request, Task task) {
					Page pager = super.download(request, task);
					page.accept(pager);
					return pager;
				}
			};
			this.setDownloader(downloader);
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