package cn.renlm.plugins;

import org.junit.Test;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.json.JSONUtil;

/**
 * 爬虫
 * 
 * @author Renlm
 *
 */
public class MyCrawlerTest {

	String REGEX_TTF = "https://((?!(https://))[\\s\\S])*\\.ttf";

	Dict GMAP = JSONUtil.toBean(ResourceUtil.readUtf8Str("GlyphCode.json"), Dict.class);

	@Test
	public void test() {

	}
}