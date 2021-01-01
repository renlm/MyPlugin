package cn.renlm.plugins.MyExcel.util;

import lombok.experimental.UtilityClass;

/**
 * 常量值
 * 
 * @author Renlm
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