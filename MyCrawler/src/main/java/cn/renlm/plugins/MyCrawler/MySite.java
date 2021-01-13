package cn.renlm.plugins.MyCrawler;

import java.util.Set;

import cn.hutool.core.bean.BeanUtil;
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

	/**
	 * 扩展参数属性名
	 */
	private static final String extraKey = "extra";

	/**
	 * 扩展参数
	 */
	private T extra;

	/**
	 * 构造（默认）
	 * 
	 * @return
	 */
	public static final MySite<String> me() {
		return me(IdUtil.objectId());
	}

	/**
	 * 构造（自定义）
	 * 
	 * @param <T>
	 * @param extra
	 * @return
	 */
	public static final <T> MySite<T> me(T extra) {
		return new MySite<T>().setExtra(extra);
	}

	/**
	 * 构造（自定义）
	 * 
	 * @param <T>
	 * @param extra
	 * @param copyBean
	 * @return
	 */
	public static final <T> MySite<T> me(T extra, Object copyBean) {
		MySite<T> mySite = me(extra);
		BeanUtil.copyProperties(copyBean, mySite, extraKey);
		return mySite;
	}

	/**
	 * 添加默认Cookie
	 * 
	 * @return
	 */
	@Override
	public MySite<T> addCookie(String name, String value) {
		super.addCookie(name, value);
		return this;
	}

	/**
	 * 添加指定域Cookie（生效需设置域名）
	 * 
	 * @return
	 */
	@Override
	public MySite<T> addCookie(String domain, String name, String value) {
		super.addCookie(domain, name, value);
		return this;
	}

	/**
	 * 设置UserAgent
	 * 
	 * @return
	 */
	@Override
	public MySite<T> setUserAgent(String userAgent) {
		super.setUserAgent(userAgent);
		return this;
	}

	/**
	 * 设置域名
	 * 
	 * @return
	 */
	@Override
	public MySite<T> setDomain(String domain) {
		super.setDomain(domain);
		return this;
	}

	/**
	 * 设置编码
	 * 
	 * @return
	 */
	@Override
	public MySite<T> setCharset(String charset) {
		super.setCharset(charset);
		return this;
	}

	/**
	 * 设置超时时间（毫秒，默认5000）
	 * 
	 * @return
	 */
	@Override
	public MySite<T> setTimeOut(int timeOut) {
		super.setTimeOut(timeOut);
		return this;
	}

	/**
	 * 设置接受请求响应码
	 * 
	 * @return
	 */
	@Override
	public MySite<T> setAcceptStatCode(Set<Integer> acceptStatCode) {
		super.setAcceptStatCode(acceptStatCode);
		return this;
	}

	/**
	 * 设置休眠时间（毫秒，默认5000）
	 * 
	 * @return
	 */
	@Override
	public MySite<T> setSleepTime(int sleepTime) {
		super.setSleepTime(sleepTime);
		return this;
	}

	/**
	 * 添加Header
	 * 
	 * @return
	 */
	@Override
	public MySite<T> addHeader(String key, String value) {
		super.addHeader(key, value);
		return this;
	}

	/**
	 * 设置重试次数（默认0）
	 * 
	 * @return
	 */
	@Override
	public MySite<T> setRetryTimes(int retryTimes) {
		super.setRetryTimes(retryTimes);
		return this;
	}

	/**
	 * 设置循环重试次数（默认0）
	 * 
	 * @return
	 */
	@Override
	public MySite<T> setCycleRetryTimes(int cycleRetryTimes) {
		super.setCycleRetryTimes(cycleRetryTimes);
		return this;
	}

	/**
	 * 设置重试休眠时间（毫秒，默认1000）
	 * 
	 * @return
	 */
	@Override
	public MySite<T> setRetrySleepTime(int retrySleepTime) {
		super.setRetrySleepTime(retrySleepTime);
		return this;
	}

	/**
	 * 设置是否启用Gzip
	 * 
	 * @return
	 */
	@Override
	public MySite<T> setUseGzip(boolean useGzip) {
		super.setUseGzip(useGzip);
		return this;
	}

	/**
	 * 设置是否禁用Cookie
	 * 
	 * @return
	 */
	@Override
	public MySite<T> setDisableCookieManagement(boolean disableCookieManagement) {
		super.setDisableCookieManagement(disableCookieManagement);
		return this;
	}
}