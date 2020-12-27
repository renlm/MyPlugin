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

import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.converters.extended.ToAttributedValueConverter;

import lombok.Data;

/**
 * 列配置-表头别名
 * 
 * @author renlm
 *
 */
@Data
@XStreamConverter(value = ToAttributedValueConverter.class, strings = { "text" })
public class Alias implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 别名名称（多级表头以表头分隔符拼接）
	 */
	private String text;

}