package cn.renlm.plugins.MyUtil;

import cn.hutool.core.util.CharUtil;
import cn.hutool.core.util.HashUtil;
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

	static final String PackageRegex = "(?im)^\\s*package\\s+([^;]+);";

	static final String ClassNameRegex = "(?m)^\\s*public\\s+class\\s+(\\w+)\\b";

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
	public static final <T> T loadFromJava(String javaCode) {
		String packages = hashPackage(javaCode);
		String className = packages + CharUtil.DOT + fetchClassName(javaCode);
		String hashJavaCode = hashJavaCode(javaCode);
		Class<?> clazz = CompilerUtils.CACHED_COMPILER.loadFromJava(className, hashJavaCode);
		return (T) ReflectUtil.newInstance(clazz);
	}

	/**
	 * 获取代码包路径
	 * 
	 * @param javaCode
	 * @return
	 */
	private static final String hashPackage(String javaCode) {
		return fetchPackage(javaCode) + HashUtil.fnvHash(javaCode);
	}

	/**
	 * 获取代码包路径
	 * 
	 * @param javaCode
	 * @return
	 */
	private static final String fetchPackage(String javaCode) {
		return ReUtil.get(PackageRegex, javaCode, 1);
	}

	/**
	 * 获取代码类名
	 * 
	 * @param javaCode
	 * @return
	 */
	private static final String fetchClassName(String javaCode) {
		return ReUtil.get(ClassNameRegex, javaCode, 1);
	}

	/**
	 * 代码Hash
	 * 
	 * @param javaCode
	 * @return
	 */
	private static final String hashJavaCode(String javaCode) {
		String newPkg = hashPackage(javaCode);
		return ReUtil.replaceAll(javaCode, PackageRegex, "package " + newPkg + ";");
	}
}