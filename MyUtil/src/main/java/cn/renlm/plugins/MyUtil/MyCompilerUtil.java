package cn.renlm.plugins.MyUtil;

import cn.hutool.core.util.CharUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.ReflectUtil;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import net.openhft.compiler.CompilerUtils;

/**
 * 代码编译
 * 
 * @author Renlm
 *
 */
@UtilityClass
public class MyCompilerUtil {

	/**
	 * 从代码中实例对象
	 * 
	 * @param <T>
	 * @param className
	 * @param javaCode
	 * @return
	 */
	@SneakyThrows
	@SuppressWarnings("unchecked")
	public <T> T loadFromJava(String javaCode) {
		String packages = fetchPackage(javaCode);
		String className = packages + CharUtil.DOT + fetchClassName(javaCode);
		String hashJavaCode = addHashToPackage(javaCode);
		Class<?> clazz = CompilerUtils.CACHED_COMPILER.loadFromJava(className, hashJavaCode);
		return (T) ReflectUtil.newInstance(clazz);
	}

	/**
	 * 从代码中提取包路径
	 * 
	 * @param javaCode
	 * @return
	 */
	public static String fetchPackage(String javaCode) {
		return ReUtil.get("(?im)^\\s*package\\s+([^;]+);", javaCode, 1);
	}

	/**
	 * 从代码中提取类名
	 * 
	 * @param javaCode
	 * @return
	 */
	public static String fetchClassName(String javaCode) {
		return ReUtil.get("(?m)^\\s*public\\s+class\\s+(\\w+)\\b", javaCode, 1);
	}

	/**
	 * 追加Hash包路径
	 * 
	 * @param javaCode
	 * @return
	 */
	public String addHashToPackage(String javaCode) {
		return javaCode;
	}
}