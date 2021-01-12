package cn.renlm.plugins.MyCrawler;

import java.util.Set;

import cn.hutool.core.util.IdUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import us.codecraft.webmagic.Site;

/**
 * 配置
 * 
 * @author Renlm
 *
 * @param <T>
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class MySite<T> extends Site {

	private T extra;

	public static final MySite<String> me() {
		return me(IdUtil.objectId());
	}

	public static final <T> MySite<T> me(T extra) {
		return new MySite<T>().setExtra(extra);
	}

	@Override
	public MySite<T> addCookie(String name, String value) {
		super.addCookie(name, value);
		return this;
	}

	@Override
	public MySite<T> addCookie(String domain, String name, String value) {
		super.addCookie(domain, name, value);
		return this;
	}

	@Override
	public MySite<T> setUserAgent(String userAgent) {
		super.setUserAgent(userAgent);
		return this;
	}

	@Override
	public MySite<T> setDomain(String domain) {
		super.setDomain(domain);
		return this;
	}

	@Override
	public MySite<T> setCharset(String charset) {
		super.setCharset(charset);
		return this;
	}

	@Override
	public MySite<T> setTimeOut(int timeOut) {
		super.setTimeOut(timeOut);
		return this;
	}

	@Override
	public MySite<T> setAcceptStatCode(Set<Integer> acceptStatCode) {
		super.setAcceptStatCode(acceptStatCode);
		return this;
	}

	@Override
	public MySite<T> setSleepTime(int sleepTime) {
		super.setSleepTime(sleepTime);
		return this;
	}

	@Override
	public MySite<T> addHeader(String key, String value) {
		super.addHeader(key, value);
		return this;
	}

	@Override
	public MySite<T> setRetryTimes(int retryTimes) {
		super.setRetryTimes(retryTimes);
		return this;
	}

	@Override
	public MySite<T> setCycleRetryTimes(int cycleRetryTimes) {
		super.setCycleRetryTimes(cycleRetryTimes);
		return this;
	}

	@Override
	public MySite<T> setRetrySleepTime(int retrySleepTime) {
		super.setRetrySleepTime(retrySleepTime);
		return this;
	}

	@Override
	public MySite<T> setUseGzip(boolean useGzip) {
		super.setUseGzip(useGzip);
		return this;
	}

	@Override
	public MySite<T> setDisableCookieManagement(boolean disableCookieManagement) {
		super.setDisableCookieManagement(disableCookieManagement);
		return this;
	}
}