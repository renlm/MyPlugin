package cn.renlm.plugins.MyUtil;

import java.util.regex.Pattern;

import cn.hutool.core.util.CharUtil;
import cn.hutool.core.util.HashUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import net.openhft.compiler.CachedCompiler;
import net.openhft.compiler.CompilerUtils;

/**
 * 代码编译（代码请手动格式化）
 * 
 * @author Renlm
 *
 */
@UtilityClass
public class MyCompilerUtil {

	/**
	 * 包路径-正则
	 */
	static final String PackageRegex = "(?im)^\\s*package\\s+([^;]+);";

	/**
	 * 类名-正则
	 */
	static final String ClassNameRegex = "(?m)^\\s*public\\s+class\\s+(\\w+)\\b";

	/**
	 * 单行注释-正则
	 */
	static final String NoteSingleLineRegex = "//.+\\r\\n";

	/**
	 * 多行注释-正则
	 */
	static final String NoteMultiLineRegex = "/\\*.+?\\*/";

	/**
	 * 实例对象
	 * 
	 * @param <T>
	 * @param className
	 * @param javaCode
	 * @param compiler
	 * @param params
	 * @return
	 */
	@SneakyThrows
	@SuppressWarnings("unchecked")
	public static final <T> T loadFromJava(String className, String javaCode, CachedCompiler compiler,
			Object... params) {
		Class<?> clazz = compiler.loadFromJava(className, javaCode);
		return (T) ReflectUtil.newInstance(clazz, params);
	}

	/**
	 * 实例对象
	 * 
	 * @param <T>
	 * @param javaCode
	 * @param params
	 * @return
	 */
	public static final <T> T loadCacheFromJava(String javaCode, Object... params) {
		String cleanCode = cleanNotes(javaCode);
		String packages = fetchPackage(cleanCode);
		String className = packages + CharUtil.DOT + fetchClassName(cleanCode);
		return loadFromJava(className, javaCode, CompilerUtils.CACHED_COMPILER, params);
	}

	/**
	 * 实例对象
	 * 
	 * @param <T>
	 * @param javaCode
	 * @param params
	 * @return
	 */
	public static final <T> T loadCacheFromJavaByHash(String javaCode, Object... params) {
		String cleanCode = cleanNotes(javaCode);
		String packages = hashPackage(cleanCode, javaCode);
		String className = packages + CharUtil.DOT + fetchClassName(cleanCode);
		String hashJavaCode = hashJavaCode(cleanCode, javaCode);
		return loadFromJava(className, hashJavaCode, CompilerUtils.CACHED_COMPILER, params);
	}

	/**
	 * 获取代码包路径
	 * 
	 * @param cleanCode
	 * @param javaCode
	 * @return
	 */
	private static final String hashPackage(String cleanCode, String javaCode) {
		return fetchPackage(cleanCode) + HashUtil.fnvHash(javaCode);
	}

	/**
	 * 获取代码包路径
	 * 
	 * @param cleanCode
	 * @return
	 */
	private static final String fetchPackage(String cleanCode) {
		return ReUtil.get(PackageRegex, cleanCode, 1);
	}

	/**
	 * 获取代码类名
	 * 
	 * @param cleanCode
	 * @return
	 */
	private static final String fetchClassName(String cleanCode) {
		return ReUtil.get(ClassNameRegex, cleanCode, 1);
	}

	/**
	 * 代码Hash
	 * 
	 * @param cleanCode
	 * @param javaCode
	 * @return
	 */
	private static final String hashJavaCode(String cleanCode, String javaCode) {
		String newPkg = hashPackage(cleanCode, javaCode);
		return ReUtil.replaceAll(javaCode, PackageRegex, "package " + newPkg + ";");
	}

	/**
	 * 清除代码注释
	 * 
	 * @param javaCode
	 * @return
	 */
	private static final String cleanNotes(String javaCode) {
		javaCode.replaceAll(NoteSingleLineRegex, StrUtil.EMPTY);
		Pattern pattern = Pattern.compile(NoteMultiLineRegex, Pattern.DOTALL);
		return pattern.matcher(javaCode).replaceAll(StrUtil.EMPTY);
	}
}