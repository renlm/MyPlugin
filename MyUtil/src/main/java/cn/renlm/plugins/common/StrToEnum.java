package cn.renlm.plugins.common;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 枚举对象
 * 
 * @author Renlm
 *
 * @param <T>
 */
public class StrToEnum<T extends Enum<T> & StrToEnum.StrValue> {

	public static interface StrValue {
		String value();
	}

	private final Map<String, T> map;

	public StrToEnum(final T[] enumValues) {
		map = new LinkedHashMap<>();
		for (T enumValue : enumValues) {
			map.put(enumValue.value(), enumValue);
		}
	}

	public T valueToEnum(final String str) {
		return this.map.get(str);
	}
}