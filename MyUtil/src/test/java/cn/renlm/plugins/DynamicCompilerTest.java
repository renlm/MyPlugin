package cn.renlm.plugins;

import org.junit.Test;

import cn.hutool.core.io.resource.ResourceUtil;
import lombok.SneakyThrows;
import net.openhft.compiler.CompilerUtils;

/**
 * 动态编译
 * 
 * @author Renlm
 *
 */
public class DynamicCompilerTest {

	String className = "crawl.script.CompilerCode";

	String javaCode = ResourceUtil.readUtf8Str("CompilerCode.java");

	@Test
	@SneakyThrows
	public void test() {
		Class<?> aClass = CompilerUtils.CACHED_COMPILER.loadFromJava(className, javaCode);
		Runnable runner = (Runnable) aClass.newInstance();
		runner.run();
	}
}