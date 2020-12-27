/*
 * Copyright (c) [Year] [name of copyright holder]
 * [Software Name] is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * 	http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package cn.renlm.plugins.MyExcel.config.column;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.converters.SingleValueConverter;

import lombok.Data;
import lombok.Getter;

/**
 * 列配置-字典
 * 
 * @author renlm
 *
 */
@Data
public class Dict implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 字典类型
	 */
	@XStreamAsAttribute
	@XStreamConverter(DictTypeConverter.class)
	private DictType type;

	/**
	 * 转换字段
	 */
	@XStreamAsAttribute
	@XStreamAlias("convert-to-field")
	private String convertToField;

	/**
	 * 是否强制检查
	 */
	@XStreamAsAttribute
	@XStreamAlias("force-check")
	private boolean forceCheck = false;

	/**
	 * 字典项列表
	 */
	@XStreamImplicit(itemFieldName = "item")
	private List<DictItem> items;

	/**
	 * 字典类型转换
	 */
	public static final class DictTypeConverter implements SingleValueConverter {

		@Override
		@SuppressWarnings("rawtypes")
		public boolean canConvert(Class type) {
			return type == DictType.class;
		}

		@Override
		public String toString(Object obj) {
			DictType type = (DictType) obj;
			return type.name();
		}

		@Override
		public Object fromString(String str) {
			for (DictType type : DictType.values()) {
				if (type.name().equals(str))
					return type;
			}
			return null;
		}
	}

	/**
	 * 字典项
	 */
	@Data
	public class DictItem implements Serializable {
		private static final long serialVersionUID = 1L;

		/**
		 * 键
		 */
		@XStreamAsAttribute
		private String key;

		/**
		 * 值
		 */
		@XStreamAsAttribute
		private String value;

	}

	/**
	 * 字典类型
	 */
	public enum DictType {
		key("编码"), value("枚举值");

		@Getter
		private String text;

		private DictType(String text) {
			this.text = text;
		}
	}

	/**
	 * 键-字典映射
	 */
	private Map<String, DictItem> keyMap;

	/**
	 * 值-字典映射
	 */
	private Map<String, DictItem> valMap;

	/**
	 * 获取键-字典映射
	 * 
	 * @return
	 */
	public Map<String, DictItem> getKeyMap() {
		if (keyMap == null) {
			keyMap = new LinkedHashMap<>();
			for (DictItem item : items) {
				if (item.key != null) {
					keyMap.put(item.key, item);
				}
			}
		}
		return keyMap;
	}

	/**
	 * 获取值-字典映射
	 * 
	 * @return
	 */
	public Map<String, DictItem> getValMap() {
		if (valMap == null) {
			valMap = new LinkedHashMap<>();
			for (DictItem item : items) {
				if (item.value != null) {
					valMap.put(item.value, item);
				}
			}
		}
		return valMap;
	}
}