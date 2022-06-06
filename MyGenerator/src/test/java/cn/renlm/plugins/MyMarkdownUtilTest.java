package cn.renlm.plugins;

import org.junit.Test;

import cn.renlm.plugins.MyGeneratorUtil.GeneratorConfig;

/**
 * Markdown辅助工具
 * 
 * @author Renlm
 *
 */
public class MyMarkdownUtilTest {

	@Test
	public void classToTable() {
		String md = MyMarkdownUtil.classToTable(GeneratorConfig.class);
		System.out.println(md);
	}
}