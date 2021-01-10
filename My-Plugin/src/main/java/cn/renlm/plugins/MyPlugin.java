package cn.renlm.plugins;

import java.util.Set;

import cn.hutool.core.lang.ConsoleTable;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.StrUtil;
import lombok.experimental.UtilityClass;

/**
 * 插件集
 * 
 * @author Renlm
 *
 */
@UtilityClass
public class MyPlugin {

	public static final String AUTHOR = "Renlm";

	public static final String VERSION = "1.0.3-SNAPSHOT";

	/**
	 * 显示所有插件类
	 * 
	 * @return 插件类名集合
	 */
	public static final Set<Class<?>> getAllUtils() {
		return ClassUtil.scanPackage("cn.renlm.plugins", (clazz) -> (false == clazz.isInterface())
				&& StrUtil.startWith(clazz.getSimpleName(), "My") && StrUtil.endWith(clazz.getSimpleName(), "Util"));
	}

	/**
	 * 控制台打印所有插件类
	 */
	public static final void printAllUtils() {
		final Set<Class<?>> allUtils = getAllUtils();
		final ConsoleTable consoleTable = ConsoleTable.create().addHeader("版本", "工具类名", "所在包");
		for (Class<?> clazz : allUtils) {
			consoleTable.addBody(VERSION, clazz.getSimpleName(), clazz.getPackage().getName());
		}
		consoleTable.print();
	}
}