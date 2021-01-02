package cn.renlm.plugins.MyCrawler;

import java.util.LinkedHashMap;
import java.util.Map;

import cn.edu.hfut.dmic.webcollector.model.CrawlDatum;
import cn.edu.hfut.dmic.webcollector.plugin.net.OkHttpRequester;
import cn.hutool.core.util.StrUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * 爬虫请求信息
 * 
 * @author Renlm
 *
 */
@Data
@Slf4j
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class CrawlerRequester extends OkHttpRequester {
	private String Accept;
	private String AcceptEncoding;
	private String AcceptLanguage;
	private String CacheControl;
	private String Connection;
	private String ContentType;
	private String Cookie;
	private String Host;
	private String Origin;
	private String Pragma;
	private String Referer;
	private String UpgradeInsecureRequests;
	private String UserAgent;
	private String XRequestedWith;
	private final Map<String, String> map = new LinkedHashMap<>();
	
	/**
	 * 添加请求参数
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public CrawlerRequester build(String key, String value) {
		this.map.put(key, value);
		return this;
	}

	@Override
	@SneakyThrows 
	public Request.Builder createRequestBuilder(CrawlDatum crawlDatum) {
		MultipartBody.Builder body = new MultipartBody.Builder()
				.setType(MultipartBody.FORM);
		for(Map.Entry<String, String> entry: map.entrySet()) {
			body.addFormDataPart(entry.getKey(), entry.getValue());
		}
		
		log.debug("爬虫请求：" + crawlDatum.url());
		Request.Builder builder = super.createRequestBuilder(crawlDatum)
				.header("Cache-Control", "no-cache")
				.header("Connection", "keep-alive")
				.header("Pragma", "no-cache")
				.header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.81 Safari/537.36")
				;
		
		if(StrUtil.isNotBlank(Accept)) 					builder.header("Accept", Accept);
		if(StrUtil.isNotBlank(AcceptEncoding))  		builder.header("Accept-Encoding", AcceptEncoding);
		if(StrUtil.isNotBlank(AcceptLanguage))  		builder.header("Accept-Language", AcceptLanguage);
		if(StrUtil.isNotBlank(CacheControl))  			builder.header("Cache-Control", CacheControl);
		if(StrUtil.isNotBlank(Connection))  			builder.header("Connection", Connection);
		if(StrUtil.isNotBlank(ContentType))  			builder.header("Content-Type", ContentType);
		if(StrUtil.isNotBlank(Cookie))  				builder.header("Cookie", Cookie);
		if(StrUtil.isNotBlank(Host))  					builder.header("Host", Host);
		if(StrUtil.isNotBlank(Origin))  				builder.header("Origin", Origin);
		if(StrUtil.isNotBlank(Pragma))  				builder.header("Pragma", Pragma);
		if(StrUtil.isNotBlank(Referer))  				builder.header("Referer", Referer);
		if(StrUtil.isNotBlank(UpgradeInsecureRequests))	builder.header("Upgrade-Insecure-Requests", UpgradeInsecureRequests);
		if(StrUtil.isNotBlank(UserAgent))  				builder.header("User-Agent", UserAgent);
		if(StrUtil.isNotBlank(XRequestedWith))  		builder.header("X-Requested-With", XRequestedWith);
		
		if(map.size() == 0) {
			return builder;
		}
		RequestBody reqBody = body.build();
		builder.header("Content-Length", String.valueOf(reqBody.contentLength()));
		return builder.post(reqBody);
	}
}