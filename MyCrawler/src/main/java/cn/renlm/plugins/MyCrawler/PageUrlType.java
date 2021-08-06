package cn.renlm.plugins.MyCrawler;

import java.util.LinkedHashMap;
import java.util.Map;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.net.url.UrlQuery;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.renlm.plugins.Common.IntToEnum;
import lombok.Getter;
import us.codecraft.webmagic.utils.UrlUtils;

/**
 * 页面链接类型
 * 
 * @author Renlm
 *
 */
public enum PageUrlType implements IntToEnum.IntValue {

	seed(0, "种子"), data(1, "数据"), enterurl(-1, "入口链接"), unknown(-99, "未知");

	public static final String extraKey = "_PageUrlTypeExtra_";

	private final int type;

	@Getter
	private final String text;

	private PageUrlType(int type, String text) {
		this.type = type;
		this.text = text;
	}

	@Override
	public int value() {
		return this.type;
	}

	/**
	 * 标准化处理请求链接（去除无效参数，减少重复请求）
	 * 
	 * @param url               请求链接
	 * @param cleanParams       是否清除Url参数
	 * @param invalidParamNames 无效参数名（多个逗号分隔）
	 * @return
	 */
	public static final String standardUrl(String url, Boolean cleanParams, String invalidParamNames) {
		String fixedUrl = UrlUtils.fixIllegalCharacterInUrl(url);
		fixedUrl = fixedUrl.replaceAll("#.*$", StrUtil.EMPTY);
		if (StrUtil.isBlank(fixedUrl)) {
			return null;
		}

		String noQueryUrl = fixedUrl.split("\\?")[0];
		if (BooleanUtil.isTrue(cleanParams)) {
			return noQueryUrl;
		}

		String[] invalidParamNameArr = StrUtil.splitToArray(invalidParamNames, StrUtil.COMMA);
		UrlQuery urlQuery = UrlQuery.of(fixedUrl, CharsetUtil.CHARSET_UTF_8);

		Map<CharSequence, CharSequence> param = new LinkedHashMap<>();
		BeanUtil.copyProperties(urlQuery.getQueryMap(), param);
		MapUtil.removeNullValue(param);
		MapUtil.removeAny(param, invalidParamNameArr);

		if (ObjectUtil.isEmpty(param)) {
			return noQueryUrl;
		}

		return noQueryUrl + "?" + UrlQuery.of(param).build(CharsetUtil.CHARSET_UTF_8);
	}
}