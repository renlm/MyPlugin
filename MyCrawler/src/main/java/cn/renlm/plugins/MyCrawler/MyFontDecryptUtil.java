package cn.renlm.plugins.MyCrawler;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import org.apache.fontbox.ttf.CmapLookup;
import org.apache.fontbox.ttf.TTFParser;
import org.apache.fontbox.ttf.TrueTypeFont;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.ReUtil;
import cn.hutool.http.HttpUtil;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

/**
 * 字体解密
 * 
 * @author Renlm
 *
 */
@UtilityClass
public class MyFontDecryptUtil {

	public static final String REGEX = "&#(\\d{6});";

	/**
	 * 获取字典
	 * 
	 * @param url
	 * @return
	 */
	public static final CmapLookup getUnicodeCmapLookupFromTTF(String url) {
		return getUnicodeCmapLookupFromTTF(HttpUtil.downloadBytes(url));
	}

	/**
	 * 获取字典
	 * 
	 * @param bytes
	 * @return
	 */
	@SneakyThrows
	public static final CmapLookup getUnicodeCmapLookupFromTTF(byte[] bytes) {
		@Cleanup
		InputStream in = new ByteArrayInputStream(bytes);
		return getUnicodeCmapLookupFromTTF(in);
	}

	/**
	 * 获取字典
	 * 
	 * @param in
	 * @return
	 */
	@SneakyThrows
	public static final CmapLookup getUnicodeCmapLookupFromTTF(InputStream in) {
		TrueTypeFont font = new TTFParser().parse(in);
		return font.getUnicodeCmapLookup();
	}

	/**
	 * 字体解密
	 * 
	 * @param gmap       编号字符映射
	 * @param cmap       编码字典
	 * @param glyphCodes 加密字符串
	 * @return
	 */
	public static final String fetchFromGlyphCode(Dict gmap, CmapLookup cmap, String glyphCodes) {
		StringBuffer buff = new StringBuffer();
		List<String> codes = ReUtil.findAll(REGEX, glyphCodes, 1);
		for (String code : codes) {
			int glyphId = cmap.getGlyphId(Integer.valueOf(code));
			buff.append(gmap.get(String.valueOf(glyphId)));
		}
		return buff.toString();
	}
}