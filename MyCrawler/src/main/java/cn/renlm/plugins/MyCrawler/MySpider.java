package cn.renlm.plugins.MyCrawler;

import java.io.IOException;
import java.util.function.Consumer;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.Setting;
import cn.renlm.plugins.MyCrawler.scheduler.MyDuplicateVerify;
import cn.renlm.plugins.MyCrawler.selenium.ChromeDownloader;
import lombok.Getter;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.SpiderListener;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.processor.PageProcessor;

/**
 * 爬虫
 * 
 * @author Renlm
 *
 */
public class MySpider extends Spider {

	@Getter
	private final MySite mySite;

	@Getter
	private final MyDuplicateVerify myDuplicateVerify;

	/**
	 * 构造函数
	 * 
	 * @param pageProcessor
	 * @param mySite
	 * @param myDuplicateVerify
	 */
	public MySpider(PageProcessor pageProcessor, MySite mySite, MyDuplicateVerify myDuplicateVerify) {
		super(pageProcessor);
		this.mySite = mySite;
		this.myDuplicateVerify = myDuplicateVerify;
	}

	/**
	 * 处理缓存问题
	 */
	@Override
	public Spider addUrl(String... urls) {
		if (ObjectUtil.isNotEmpty(this.mySite) && ObjectUtil.isNotEmpty(this.myDuplicateVerify)
				&& BooleanUtil.isTrue(this.mySite.isForceUpdate())) {
			for (String url : urls) {
				this.myDuplicateVerify.cleanCache(new Request(url), this);
			}
		}
		super.addUrl(urls);
		return this;
	}

	/**
	 * 处理缓存问题
	 */
	@Override
	public MySpider addRequest(Request... requests) {
		if (ObjectUtil.isNotEmpty(this.mySite) && ObjectUtil.isNotEmpty(this.myDuplicateVerify)
				&& BooleanUtil.isTrue(this.mySite.isForceUpdate())) {
			for (Request request : requests) {
				this.myDuplicateVerify.cleanCache(request, this);
			}
		}
		super.addRequest(requests);
		return this;
	}

	/**
	 * 下载完成回调
	 * 
	 * @param site
	 * @param page
	 * @return
	 */
	public MySpider onDownloaded(MySite site, Consumer<Page> page) {
		if (ObjectUtil.isNotEmpty(site) && site.isEnableSelenuim()) {
			Setting chromeSetting = site.getChromeSetting();
			if (BooleanUtil.isTrue(site.getHeadless())) {
				chromeSetting.set("headless", "true");
			}
			if (StrUtil.isNotBlank(site.getUserAgent())) {
				chromeSetting.set("userAgent", site.getUserAgent());
			}
			if (BooleanUtil.isTrue(site.getScreenshot())) {
				chromeSetting.set("screenshot", "true");
			}
			ChromeDownloader downloader = new ChromeDownloader(chromeSetting) {
				@Override
				public Page download(Request request, Task task) {
					Page pager = super.download(request, task);
					page.accept(pager);
					return pager;
				}

				@Override
				public void close() {
					try {
						super.close();
					} catch (IOException e) {
					}
				}
			};
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