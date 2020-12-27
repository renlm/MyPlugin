/*
 * Copyright (c) [Year] [name of copyright holder]
 * [Software Name] is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * 	http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package cn.renlm.plugins.MyExcel.config;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import cn.hutool.core.collection.CollUtil;
import lombok.Data;

/**
 * 工作簿配置
 * 
 * @author renlm
 *
 */
@Data
@XStreamAlias("excel")
public class MyWorkbook implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 表格名称
	 */
	@XStreamAsAttribute
	private String name;

	/**
	 * 引用模板
	 */
	@XStreamAsAttribute
	private String ref;

	/**
	 * Csv编码（处理Csv时使用）
	 */
	@XStreamAsAttribute
	@XStreamAlias("csv-charset")
	private String csvCharset;

	/**
	 * Sheet集
	 */
	@XStreamImplicit(itemFieldName = "sheet")
	private List<MySheet> sheets;

	/**
	 * 根据Sheet名称获取配置
	 * 
	 * @param sheetName
	 * @return
	 */
	public MySheet getSheetByName(String sheetName) {
		return CollUtil
				.getFirst(sheets.stream().filter(it -> it.getName().equals(sheetName)).collect(Collectors.toList()));
	}
}