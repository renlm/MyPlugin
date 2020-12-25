package cn.renlm.plugins;

import org.junit.Test;

/**
 * 代码生成
 * 
 * @author renlm
 *
 */
public class MyGeneratorUtilTest {

	@Test
	public void run() {
		MyGeneratorUtil.run("Generator.MySQL.xml");
		MyGeneratorUtil.run("Generator.PostgreSQL.xml");
	}
}