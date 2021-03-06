package cn.renlm.plugins.MyUtil;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import org.apache.fontbox.ttf.CmapLookup;
import org.apache.fontbox.ttf.TTFParser;
import org.apache.fontbox.ttf.TrueTypeFont;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.ReUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
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

	public static final String Regex = "(&#(\\d{6});)";

	public static final String GlyphPath = "config/Glyph.map.json";

	private static final Dict Gmap = JSONUtil.toBean(ResourceUtil.readUtf8Str(GlyphPath), Dict.class);

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
		@Cleanup
		TrueTypeFont font = new TTFParser().parse(in);
		return font.getUnicodeCmapLookup();
	}

	/**
	 * 字体解密（默认，未打乱编码映射）
	 * 
	 * @param cmap
	 * @param glyphs
	 * @return
	 */
	public static final String fetchFromGlyphs(CmapLookup cmap, String glyphs) {
		StringBuffer buff = new StringBuffer();
		List<String> codes = ReUtil.findAll(Regex, glyphs, 2);
		for (String code : codes) {
			int glyphId = cmap.getGlyphId(Integer.valueOf(code));
			buff.append(Gmap.get(String.valueOf(glyphId)));
		}
		return buff.toString();
	}

	/**
	 * 字体解密（默认，自定义编码映射）
	 * 
	 * @param gmap   编号字符映射
	 * @param cmap   编码字典
	 * @param glyphs 加密字符串
	 * @return
	 */
	public static final String fetchFromGlyphs(Dict gmap, CmapLookup cmap, String glyphs) {
		StringBuffer buff = new StringBuffer();
		List<String> codes = ReUtil.findAll(Regex, glyphs, 2);
		for (String code : codes) {
			int glyphId = cmap.getGlyphId(Integer.valueOf(code));
			buff.append(gmap.get(String.valueOf(glyphId)));
		}
		return buff.toString();
	}
}