package cn.renlm.plugins;

import org.junit.Test;

import com.baomidou.mybatisplus.generator.config.DataSourceConfig;

/**
 * Markdown辅助工具
 * 
 * @author Renlm
 *
 */
public class MyMarkdownUtilTest {

	@Test
	public void classToTable() {
		String md = MyMarkdownUtil.classToTable(DataSourceConfig.class);
		System.out.println(md);
	}
}