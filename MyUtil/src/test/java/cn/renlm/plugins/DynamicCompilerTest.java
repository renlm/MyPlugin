package cn.renlm.plugins;

import org.junit.Test;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.ClassLoaderUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.renlm.plugins.MyUtil.MyCompilerUtil;
import lombok.SneakyThrows;
import net.openhft.compiler.CachedCompiler;
import net.openhft.compiler.CompilerUtils;

/**
 * 动态编译
 * 
 * @author Renlm
 *
 */
public class DynamicCompilerTest {

	@Test
	@SneakyThrows
	public void test() {
		String javaCode = ResourceUtil.readUtf8Str("CompilerCode.java");
		ClassLoader classLoader = ClassLoaderUtil.getClassLoader();
		CachedCompiler compiler = CompilerUtils.CACHED_COMPILER;
		Class<?> clazz = MyCompilerUtil.loadClass(classLoader, compiler, javaCode);
		Runnable runnable = (Runnable) ReflectUtil.newInstance(clazz);
		runnable.run();
	}
}