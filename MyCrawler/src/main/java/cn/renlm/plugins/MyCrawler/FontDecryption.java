package cn.renlm.plugins.MyCrawler;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.binary.Base64;
import org.apache.fontbox.ttf.CmapLookup;
import org.apache.fontbox.ttf.TTFParser;
import org.apache.fontbox.ttf.TrueTypeFont;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import lombok.SneakyThrows;

/**
 * 字体工具
 * 
 * @author 任黎明
 *
 */
public class FontDecryption {
	public final static String split_GlyphCode = "@^|^@";
	public final static String regex_split_GlyphCode = "@\\^\\|\\^@";
	public final static String glyph_prefix = "&#";
	public final static String glyph_suffix = ";";
	public final static String regex_GlyphCode = glyph_prefix + "\\d{6}" + glyph_suffix;

	/**
	 * 获取字典
	 * 
	 * @param fonturl
	 * @return
	 */
	@SneakyThrows
	public static CmapLookup getUnicodeCmapLookupFromTTF(String fonturl) {
		InputStream in = new ByteArrayInputStream(HttpUtil.downloadBytes(fonturl));
		TrueTypeFont font = new TTFParser().parse(in);
		return font.getUnicodeCmapLookup();
	}

	/**
	 * 加密网页字体编码
	 * 
	 * @param html
	 */
	public static String safeGlyphCode(String html) {
		Pattern pattern = Pattern.compile(regex_GlyphCode);
		Matcher matcher = pattern.matcher(html);
		while (matcher.find()) {
			String base64 = Base64.encodeBase64URLSafeString(matcher.group().getBytes());
			html = html.replaceAll(matcher.group(), split_GlyphCode + base64 + split_GlyphCode);
		}
		return html;
	}

	/**
	 * 从加密网页字体编码中获取字体解码
	 * 
	 * @param _gmap         编号字符映射
	 * @param cmap          编码映射
	 * @param safeGlyphCode 加密网页字体编码
	 * @return
	 */
	public static String fetchDataFromSafeGlyphCode(Map<Integer, String> _gmap, CmapLookup cmap, String safeGlyphCode) {
		if (StrUtil.isNotBlank(safeGlyphCode)) {
			String glyphCode = "";
			String[] arrs = safeGlyphCode.split(regex_split_GlyphCode);
			for (String base64 : arrs) {
				if (StrUtil.isNotBlank(base64)) {
					String character = new String(Base64.decodeBase64(base64));
					Integer glyphId = cmap.getGlyphId(Integer.valueOf(character.substring(2, character.length() - 1)));
					glyphCode += _gmap.get(glyphId);
				}
			}
			return glyphCode;
		}
		return null;
	}

	/**
	 * 转换明文为字体编码
	 * 
	 * @param gmap  字符编号映射
	 * @param cmap  编码映射
	 * @param plain 明文文本
	 * @return
	 */
	public static String toGlyphCode(Map<String, Integer> gmap, CmapLookup cmap, String plain) {
		String glyphCode = "";
		if (StrUtil.isNotBlank(plain)) {
			for (int i = 0; i < plain.length(); i++) {
				int glyphId = gmap.get(String.valueOf(plain.charAt(i)));
				List<Integer> charCodes = cmap.getCharCodes(glyphId);
				for (Integer charCode : charCodes) {
					glyphCode += glyph_prefix + charCode + glyph_suffix;
				}
			}
		}
		return glyphCode;
	}
}