package cn.renlm.plugins.MyUtil;

import cn.hutool.core.util.StrUtil;
import lombok.experimental.UtilityClass;

/**
 * 自定义字符处理工具
 * 
 * @author Renlm
 *
 */
@UtilityClass
public class MyStrUtil {

	/**
	 * 批量移除后缀
	 * 
	 * @param text
	 * @param suffix
	 * @return
	 */
	public static final String removeSuffix(String text, String... suffix) {
		for (String item : suffix) {
			text = StrUtil.removeSuffix(text, item);
		}
		return text;
	}
}