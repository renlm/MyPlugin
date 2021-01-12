package cn.renlm.plugins.MyCrawler;

import java.util.function.Consumer;

import cn.hutool.core.collection.CollUtil;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.SpiderListener;
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
	 * 成功回调
	 * 
	 * @param req
	 */
	public void onSuccess(Consumer<Request> req) {
		SpiderListener listener = new SpiderListener() {
			@Override
			public void onSuccess(Request request) {
				req.accept(request);
			}

			@Override
			public void onError(Request request) {

			}
		};
		if (CollUtil.isEmpty(this.getSpiderListeners())) {
			this.setSpiderListeners(CollUtil.newArrayList());
		}
		this.getSpiderListeners().add(listener);
	}

	/**
	 * 失败回调
	 * 
	 * @param req
	 */
	public void onError(Consumer<Request> req) {
		SpiderListener listener = new SpiderListener() {
			@Override
			public void onSuccess(Request request) {

			}

			@Override
			public void onError(Request request) {
				req.accept(request);
			}
		};
		if (CollUtil.isEmpty(this.getSpiderListeners())) {
			this.setSpiderListeners(CollUtil.newArrayList());
		}
		this.getSpiderListeners().add(listener);
	}
}