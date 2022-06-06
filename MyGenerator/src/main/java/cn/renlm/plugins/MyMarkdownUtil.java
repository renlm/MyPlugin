package cn.renlm.plugins;

import static cn.hutool.core.text.CharSequenceUtil.SPACE;
import static cn.hutool.core.text.StrPool.BRACKET_END;
import static cn.hutool.core.text.StrPool.BRACKET_START;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ReflectUtil;
import lombok.experimental.UtilityClass;

/**
 * Markdown辅助工具
 * 
 * @author Renlm
 *
 */
@UtilityClass
public class MyMarkdownUtil {

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
		Map<String, Field> map = new LinkedHashMap<>();

		// 遍历结构
		Field[] fields = ReflectUtil.getFields(clazz);
		for (Field field : fields) {
			Class<?> fieldType = field.getType();
			String fieldName = fieldType.getName();
			if (map.containsKey(fieldName)) {
				continue;
			}
			// 简单值类型
			if (ClassUtil.isSimpleValueType(fieldType)) {
				map.put(fieldName, field);
			}
			// 简单值类型的数组
			else if (ClassUtil.isSimpleTypeOrArray(fieldType)) {
				map.put(fieldName + BRACKET_START + BRACKET_END, field);
			}
		}

		// 拼装文档
		sb.append("|字段").append("|类型").append("|注释|");
		sb.append("|:-").append("|:-").append("|:-|");
		map.forEach((name, field) -> {
			sb.append("|").append(name);
			sb.append("|").append(field.getType());
			sb.append("|").append(SPACE);
			sb.append("|");
		});
		return sb.toString();
	}
}