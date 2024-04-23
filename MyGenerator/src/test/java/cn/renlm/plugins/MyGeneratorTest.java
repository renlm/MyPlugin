package cn.renlm.plugins;

import java.lang.reflect.Field;

import org.junit.jupiter.api.Test;

import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;

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
	public void javaSqlTypes() {
		Field[] fields = ReflectUtil.getFields(java.sql.Types.class);
		for (Field field : fields) {
			String str = StrUtil.format("<xsd:enumeration value=\"{}\"/>", field.getName());
			System.out.println(str);
		}
	}

}
