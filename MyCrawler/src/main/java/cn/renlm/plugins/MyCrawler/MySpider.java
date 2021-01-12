package cn.renlm.plugins.MyCrawler;

import java.util.function.Consumer;

import cn.hutool.core.collection.CollUtil;
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
	 * @param page
	 */
	public void onDownloaded(Consumer<Page> page) {
		this.downloader = new HttpClientDownloader() {

			@Override
			public Page download(Request request, Task task) {
				Page pager = super.download(request, task);
				page.accept(pager);
				return pager;
			}
		};
	}

	/**
	 * 成功回调
	 * 
	 * @param req
	 */
	public void onSuccess(Consumer<Request> request) {
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
	}

	/**
	 * 失败回调
	 * 
	 * @param req
	 */
	public void onError(Consumer<Request> request) {
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
	}
}