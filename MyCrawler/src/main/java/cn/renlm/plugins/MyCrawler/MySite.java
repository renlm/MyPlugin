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
		return this.addCookie(name, value);
	}

	@Override
	public MySite<T> addCookie(String domain, String name, String value) {
		return this.addCookie(domain, name, value);
	}

	@Override
	public MySite<T> setUserAgent(String userAgent) {
		return this.setUserAgent(userAgent);
	}

	@Override
	public MySite<T> setDomain(String domain) {
		return this.setDomain(domain);
	}

	@Override
	public MySite<T> setCharset(String charset) {
		return this.setCharset(charset);
	}

	@Override
	public MySite<T> setTimeOut(int timeOut) {
		return this.setTimeOut(timeOut);
	}

	@Override
	public MySite<T> setAcceptStatCode(Set<Integer> acceptStatCode) {
		return this.setAcceptStatCode(acceptStatCode);
	}

	@Override
	public MySite<T> setSleepTime(int sleepTime) {
		return this.setSleepTime(sleepTime);
	}

	@Override
	public MySite<T> addHeader(String key, String value) {
		return this.addHeader(key, value);
	}

	@Override
	public MySite<T> setRetryTimes(int retryTimes) {
		return this.setRetryTimes(retryTimes);
	}

	@Override
	public MySite<T> setCycleRetryTimes(int cycleRetryTimes) {
		return this.setCycleRetryTimes(cycleRetryTimes);
	}

	@Override
	public MySite<T> setRetrySleepTime(int retrySleepTime) {
		return this.setRetrySleepTime(retrySleepTime);
	}

	@Override
	public MySite<T> setUseGzip(boolean useGzip) {
		return this.setUseGzip(useGzip);
	}

	@Override
	public MySite<T> setDisableCookieManagement(boolean disableCookieManagement) {
		return this.setDisableCookieManagement(disableCookieManagement);
	}
}