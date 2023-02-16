package cn.renlm.plugins;

import org.junit.jupiter.api.Test;

import cn.hutool.json.JSONUtil;
import cn.renlm.plugins.MyGeneratorUtil.GeneratorConfig;
import cn.renlm.plugins.MyUtil.MyXStreamUtil;

/**
 * 代码生成封装
 * 
 * @author RenLiMing(任黎明)
 *
 */
public class MyGeneratorTest {

	@Test
	public void run() {
		MyGeneratorUtil.run("MyGenerator.xml");
	}

	@Test
	public void read() {
		GeneratorConfig conf = MyXStreamUtil.read(GeneratorConfig.class, "MyGenerator.xml");
		System.out.println(JSONUtil.toJsonPrettyStr(conf));
	}

}
