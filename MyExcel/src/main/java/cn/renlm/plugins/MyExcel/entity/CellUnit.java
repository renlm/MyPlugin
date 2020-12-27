/*
 * Copyright (c) [Year] [name of copyright holder]
 * [Software Name] is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * 	http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package cn.renlm.plugins.MyExcel.entity;

import org.apache.poi.ss.usermodel.CellStyle;

import cn.renlm.plugins.MyExcel.config.MyColumn;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 单元格封装
 */
@Data
@Accessors(chain = true)
public class CellUnit {

	/**
	 * 行号（起始行：0）
	 */
	private int rowIndex;

	/**
	 * 列号（起始列：0）
	 */
	private int colIndex;

	/**
	 * 文本值
	 */
	private String text;

	/**
	 * 样式
	 */
	private CellStyle cellStyle;

	/**
	 * 列配置
	 */
	private MyColumn column;

}