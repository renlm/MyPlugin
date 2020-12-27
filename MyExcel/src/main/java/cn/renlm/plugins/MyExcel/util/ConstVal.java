/*
 * Copyright (c) [Year] [name of copyright holder]
 * [Software Name] is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * 	http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package cn.renlm.plugins.MyExcel.util;

import lombok.experimental.UtilityClass;

/**
 * 常量值
 * 
 * @author renlm
 *
 */
@UtilityClass
public class ConstVal {

	/**
	 * 默认字体，宋体
	 */
	public static final String FONT = "SimSun";

	/**
	 * 默认字体大小，10号
	 */
	public static final short FONT_SIZE = 10;

	/**
	 * 最小列宽
	 */
	public static final int COL_MIN_WIDTH = 256 * 8;

	/**
	 * 最大列宽
	 */
	public static final int COL_MAX_WIDTH = 256 * 64;

	/**
	 * 多级表头分割符
	 */
	public static final String LEVEL_TITLE_SPLIT = ConstVal.class.getPackage().toString();

}