package cn.renlm.plugins;

import static cn.hutool.core.text.CharSequenceUtil.EMPTY;
import static cn.hutool.core.text.StrPool.BRACKET_END;
import static cn.hutool.core.text.StrPool.BRACKET_START;
import static cn.hutool.core.text.StrPool.CRLF;
import static cn.hutool.core.text.StrPool.DASHED;
import static cn.hutool.core.text.StrPool.DOT;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import lombok.experimental.UtilityClass;

/**
 * Markdown辅助工具
 * 
 * @author Renlm
 *
 */
@UtilityClass
public class MyMarkdownUtil {

	private static final String ARR_TAG = BRACKET_START + StrUtil.SPACE
			+ BRACKET_END;

	/**
	 * Java类转markdown表格
	 * 
	 * @param clazz
	 * @return
	 */
	public static final String classToTable(Class<?> clazz) {
		Assert.notNull(clazz, "clazz为空");
		Assert.isFalse(ClassUtil.isSimpleTypeOrArray(clazz),
				"clazz为简单值类型或简单值类型的数组");
		StringBuffer sb = new StringBuffer();
		Set<Class<?>> set = new HashSet<>();
		Map<String, Field> map = new LinkedHashMap<>();

		// 遍历结构
		classToTableRecursion(EMPTY, set, clazz, map);

		// 拼装文档
		sb.append("|字段").append("|类型").append("|注释|");
		sb.append(CRLF);
		sb.append("|:-").append("|:-").append("|:-|");
		sb.append(CRLF);
		map.forEach((name, field) -> {
			sb.append("|").append(name);
			sb.append("|").append(field.getType().getSimpleName());
			sb.append("|").append(DASHED);
			sb.append("|");
			sb.append(CRLF);
		});
		return sb.toString();
	}

	/**
	 * Java类转markdown表格（遍历结构）
	 * 
	 * @param prefix
	 * @param set
	 * @param clazz
	 * @param map
	 */
	private static final void classToTableRecursion(String prefix,
			Set<Class<?>> set, Class<?> clazz, Map<String, Field> map) {
		if (set.contains(clazz)) {
			return;
		}
		set.add(clazz);
		Field[] fields = ReflectUtil.getFields(clazz);
		for (Field field : fields) {
			String fieldName = StrUtil.isNotBlank(prefix)
					? (prefix + DOT + field.getName())
					: field.getName();
			Class<?> fieldType = field.getType();
			if (Modifier.isStatic(field.getModifiers())) {
				continue;
			}
			if (Modifier.isFinal(field.getModifiers())) {
				continue;
			}
			if (map.containsKey(fieldName)) {
				continue;
			}
			// Map
			if (ClassUtil.isAssignable(fieldType, Map.class)) {
				map.put(fieldName, field);
			}
			// 简单值类型
			else if (ClassUtil.isSimpleValueType(fieldType)) {
				map.put(fieldName, field);
			}
			// 数组
			else if (clazz.isArray()) {
				map.put(fieldName, field);
				if (ClassUtil.isSimpleTypeOrArray(clazz.getComponentType())) {
					continue;
				}
				classToTableRecursion(fieldName + ARR_TAG, set,
						fieldType.getComponentType(), map);
			}
			// 集合
			else if (ClassUtil.isAssignable(fieldType, Collection.class)) {
				map.put(fieldName, field);
				Type fgt = field.getGenericType();
				if (fgt == null) {
					continue;
				}
				if (fgt instanceof ParameterizedType) {
					ParameterizedType pt = (ParameterizedType) fgt;
					if (ArrayUtil.isEmpty(pt.getActualTypeArguments())) {
						continue;
					}
					Class<?> clz = (Class<?>) pt.getActualTypeArguments()[0];
					classToTableRecursion(fieldName + ARR_TAG, set, clz, map);
				}
			}
			// 其它
			else {
				map.put(fieldName, field);
				classToTableRecursion(fieldName, set, fieldType, map);
			}
		}
	}
}