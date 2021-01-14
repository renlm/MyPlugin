package cn.renlm.plugins;

import org.junit.Test;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.renlm.plugins.MyUtil.MyCompilerUtil;
import lombok.SneakyThrows;

/**
 * 动态编译
 * 
 * @author Renlm
 *
 */
public class DynamicCompilerTest {

	@Test
	@SneakyThrows
	public void test1() {
		String javaCode = ResourceUtil.readUtf8Str("CompilerCode.java");
		Class<?> clazz = MyCompilerUtil.loadFromJava(javaCode);
		Runnable runnable = (Runnable) ReflectUtil.newInstance(clazz);
		runnable.run();
	}
}