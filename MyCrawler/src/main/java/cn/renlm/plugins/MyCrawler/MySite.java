package cn.renlm.plugins.MyCrawler;

import java.util.Set;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.setting.Setting;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import us.codecraft.webmagic.Site;

/**
 * 站点配置
 * 
 * @author RenLiMing(任黎明)
 *
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class MySite extends Site {

	/**
	 * 最大深度（默认0，无限制）
	 */
	private int maxDepth;

	/**
	 * 附加设置，是否强制更新数据
	 */
	private boolean forceUpdate;

	/**
	 * 是否启用浏览器模式
	 */
	private boolean enableSelenuim;

	/**
	 * 无头模式（浏览器模式下，默认否）
	 */
	private Boolean headless;

	/**
	 * 保存页面截图（浏览器模式下，默认否）
	 */
	private Boolean screenshot;

	/**
	 * 浏览器模式配置
	 *     [ driverPath=浏览器驱动路径 ] 
	 *     [ sleepTime=页面渲染等待时间（毫秒，默认100） ]
	 *     [ windowSize=窗口尺寸（默认1280,720） ]
	 */
	private Setting chromeSetting;

	/**
	 * 默认
	 * 
	 * @return
	 */
	public static final MySite me() {
		return new MySite();
	}

	/**
	 * 填充属性
	 * 
	 * @param properties
	 * @return
	 */
	public static final MySite me(Object properties) {
		MySite site = me();
		BeanUtil.copyProperties(properties, site);
		return site;
	}

	/**
	 * 添加默认Cookie
	 */
	@Override
	public MySite addCookie(String name, String value) {
		super.addCookie(name, value);
		return this;
	}

	/**
	 * 添加指定域Cookie（生效需设置域名）
	 */
	@Override
	public MySite addCookie(String domain, String name, String value) {
		super.addCookie(domain, name, value);
		return this;
	}

	/**
	 * 设置UserAgent
	 */
	@Override
	public MySite setUserAgent(String userAgent) {
		super.setUserAgent(userAgent);
		return this;
	}

	/**
	 * 设置域名
	 */
	@Override
	public MySite setDomain(String domain) {
		super.setDomain(domain);
		return this;
	}

	/**
	 * 设置编码
	 */
	@Override
	public MySite setCharset(String charset) {
		super.setCharset(charset);
		return this;
	}

	/**
	 * 设置超时时间（毫秒，默认5000）
	 */
	@Override
	public MySite setTimeOut(int timeOut) {
		super.setTimeOut(timeOut);
		return this;
	}

	/**
	 * 设置接受请求响应码
	 */
	@Override
	public MySite setAcceptStatCode(Set<Integer> acceptStatCode) {
		super.setAcceptStatCode(acceptStatCode);
		return this;
	}

	/**
	 * 设置休眠时间（毫秒，默认5000）
	 */
	@Override
	public MySite setSleepTime(int sleepTime) {
		super.setSleepTime(sleepTime);
		return this;
	}

	/**
	 * 添加Header
	 */
	@Override
	public MySite addHeader(String key, String value) {
		super.addHeader(key, value);
		return this;
	}

	/**
	 * 设置重试次数（默认0）
	 */
	@Override
	public MySite setRetryTimes(int retryTimes) {
		super.setRetryTimes(retryTimes);
		return this;
	}

	/**
	 * 设置循环重试次数（默认0）
	 */
	@Override
	public MySite setCycleRetryTimes(int cycleRetryTimes) {
		super.setCycleRetryTimes(cycleRetryTimes);
		return this;
	}

	/**
	 * 设置重试休眠时间（毫秒，默认1000）
	 */
	@Override
	public MySite setRetrySleepTime(int retrySleepTime) {
		super.setRetrySleepTime(retrySleepTime);
		return this;
	}

	/**
	 * 设置是否启用Gzip
	 */
	@Override
	public MySite setUseGzip(boolean useGzip) {
		super.setUseGzip(useGzip);
		return this;
	}

	/**
	 * 设置是否禁用Cookie
	 */
	@Override
	public MySite setDisableCookieManagement(boolean disableCookieManagement) {
		super.setDisableCookieManagement(disableCookieManagement);
		return this;
	}
}