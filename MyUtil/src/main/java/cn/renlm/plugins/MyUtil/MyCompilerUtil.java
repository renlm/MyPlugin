package cn.renlm.plugins.MyUtil;

import java.util.regex.Pattern;

import cn.hutool.core.util.CharUtil;
import cn.hutool.core.util.HashUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import net.openhft.compiler.CachedCompiler;
import net.openhft.compiler.CompilerUtils;

/**
 * 代码编译（自动识别）
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
	 * 编译代码（缓存优先）
	 * 
	 * @param javaCode
	 * @return
	 */
	public static final Class<?> loadFromJava(String javaCode) {
		return loadFromJava(CompilerUtils.CACHED_COMPILER, javaCode);
	}

	/**
	 * 编译代码（重新编译）
	 * 
	 * @param javaCode
	 * @return
	 */
	public static final Class<?> reloadFromJava(String javaCode) {
		CachedCompiler compiler = new CachedCompiler(null, null);
		return loadFromJava(compiler, javaCode);
	}

	/**
	 * 编译代码（Hash路径，缓存优先）
	 * 
	 * @param javaCode
	 * @return
	 */
	public static final Class<?> loadFromJavaByHash(String javaCode) {
		return loadFromJavaByHash(CompilerUtils.CACHED_COMPILER, javaCode);
	}

	/**
	 * 编译代码
	 * 
	 * @param className
	 * @param javaCode
	 * @return
	 */
	@SneakyThrows
	private static final Class<?> loadFromJava(CachedCompiler compiler, String javaCode) {
		String cleanCode = cleanNotes(javaCode);
		String packages = fetchPackage(cleanCode);
		String className = packages + CharUtil.DOT + fetchClassName(cleanCode);
		return compiler.loadFromJava(className, javaCode);
	}

	/**
	 * 编译代码（Hash路径）
	 * 
	 * @param className
	 * @param javaCode
	 * @return
	 */
	@SneakyThrows
	private static final Class<?> loadFromJavaByHash(CachedCompiler compiler, String javaCode) {
		String cleanCode = cleanNotes(javaCode);
		String packages = hashPackage(cleanCode, javaCode);
		String className = packages + CharUtil.DOT + fetchClassName(cleanCode);
		String hashJavaCode = hashJavaCode(cleanCode, javaCode);
		return compiler.loadFromJava(className, hashJavaCode);
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