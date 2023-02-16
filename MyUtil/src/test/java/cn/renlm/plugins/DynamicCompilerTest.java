package cn.renlm.plugins;

import org.junit.jupiter.api.Test;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.renlm.plugins.MyUtil.MyCompilerUtil;
import lombok.SneakyThrows;

/**
 * 动态编译
 * 
 * @author RenLiMing(任黎明)
 *
 */
public class DynamicCompilerTest {

	@Test
	@SneakyThrows
	public void test() {
		String javaCode = ResourceUtil.readUtf8Str("CompilerCode.java");
		Class<?> clazz = MyCompilerUtil.loadClassByHash(javaCode);
		Runnable runnable = (Runnable) ReflectUtil.newInstance(clazz);
		runnable.run();
	}
}