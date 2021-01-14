package cn.renlm.plugins;

import org.junit.Test;

import cn.hutool.core.io.resource.ResourceUtil;
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
		Runnable runnable = MyCompilerUtil.loadFromCache(javaCode);
		runnable.run();
	}

	@Test
	@SneakyThrows
	public void test2() {
		String javaCode = ResourceUtil.readUtf8Str("CompilerCode.java");
		Runnable runnable = MyCompilerUtil.loadFromCacheByHash(javaCode);
		runnable.run();
	}
}