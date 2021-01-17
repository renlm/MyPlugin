package cn.renlm.plugins.MyUtil;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.CharUtil;
import cn.hutool.core.util.ClassLoaderUtil;
import cn.hutool.core.util.HashUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
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
	 * 添加ClassPath
	 * 
	 * @param dir
	 * @return
	 */
	public static final boolean addClassPath(String dir) {
		return StrUtil.isBlank(dir) ? false : CompilerUtils.addClassPath(dir);
	}

	/**
	 * 添加Library
	 * 
	 * @param dirOrJar
	 * @return
	 */
	public static final boolean addLibrary(String dirOrJar) {
		if (StrUtil.isBlank(dirOrJar) || !FileUtil.exist(dirOrJar) || !"jar".equals(FileUtil.getSuffix(dirOrJar))) {
			return false;
		}
		if (FileUtil.isFile(dirOrJar)) {
			CompilerUtils.addClassPath(dirOrJar);
		}
		AtomicInteger cnt = new AtomicInteger();
		FileUtil.loopFiles(Paths.get(dirOrJar), 1, file -> {
			return "jar".equals(FileNameUtil.getSuffix(file));
		}).forEach(file -> {
			String path;
			try {
				path = file.getCanonicalPath();
			} catch (IOException ignored) {
				path = file.getAbsolutePath();
			}
			cnt.incrementAndGet();
			CompilerUtils.addClassPath(path);
		});
		return cnt.get() > 0;
	}

	/**
	 * 编译代码
	 * 
	 * @param javaCode
	 * @return
	 */
	public static final Class<?> loadClass(String javaCode) {
		return loadClass(ClassLoaderUtil.getClassLoader(), javaCode);
	}

	/**
	 * 编译代码（Hash路径）
	 * 
	 * @param javaCode
	 * @return
	 */
	public static final Class<?> loadClassByHash(String javaCode) {
		return loadClassByHash(ClassLoaderUtil.getClassLoader(), javaCode);
	}

	/**
	 * 编译代码
	 * 
	 * @param classLoader
	 * @param javaCode
	 * @return
	 */
	@SneakyThrows
	public static final Class<?> loadClass(ClassLoader classLoader, String javaCode) {
		String cleanCode = cleanNotes(javaCode);
		String packages = fetchPackage(cleanCode);
		String className = packages + CharUtil.DOT + fetchClassName(cleanCode);
		return CompilerUtils.CACHED_COMPILER.loadFromJava(classLoader, className, javaCode);
	}

	/**
	 * 编译代码（Hash路径）
	 * 
	 * @param classLoader
	 * @param javaCode
	 * @return
	 */
	@SneakyThrows
	public static final Class<?> loadClassByHash(ClassLoader classLoader, String javaCode) {
		String cleanCode = cleanNotes(javaCode);
		String packages = hashPackage(cleanCode, javaCode);
		String className = packages + CharUtil.DOT + fetchClassName(cleanCode);
		String hashJavaCode = hashJavaCode(cleanCode, javaCode);
		return CompilerUtils.CACHED_COMPILER.loadFromJava(classLoader, className, hashJavaCode);
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