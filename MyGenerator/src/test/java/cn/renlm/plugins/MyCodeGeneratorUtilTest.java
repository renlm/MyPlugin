package cn.renlm.plugins;

import org.junit.Test;

/**
 * 代码生成
 * 
 * @author renlm
 *
 */
public class MyCodeGeneratorUtilTest {

	@Test
	public void run() {
		MyCodeGeneratorUtil.run("Generator.MySQL.xml");
		MyCodeGeneratorUtil.run("Generator.PostgreSQL.xml");
	}
}