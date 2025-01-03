package cn.renlm.plugins;

import lombok.experimental.UtilityClass;

/**
 * 常量池
 * 
 * @author RenLiMing(任黎明)
 *
 */
@UtilityClass
public class ConstVal {

	public static final String AUTHOR 			= "RenLiMing(任黎明)";
	public static final String VERSION 			= "2.8.11";

	public static final String NAME 			= ConstVal.class.getName();

	public static final String userDir 			= System.getProperty("user.dir");
	public static final String javaDir 			= userDir + "/src/main/java";
	public static final String resourcesDir 	= userDir + "/src/main/resources";
	public static final String javaTestDir 		= userDir + "/src/test/java";
	public static final String resourcesTestDir = userDir + "/src/test/resources";

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

}
