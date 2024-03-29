package cn.renlm.plugins.Common;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 枚举对象
 * 
 * @author RenLiMing(任黎明)
 *
 * @param <T>
 */
public class IntToEnum<T extends Enum<T> & IntToEnum.IntValue> {

	public static interface IntValue {
		int value();
	}

	private final Map<Integer, T> map;

	public IntToEnum(final T[] enumValues) {
		map = new LinkedHashMap<>();
		for (T enumValue : enumValues) {
			map.put(enumValue.value(), enumValue);
		}
	}

	public T valueToEnum(final int i) {
		return this.map.get(i);
	}

}
