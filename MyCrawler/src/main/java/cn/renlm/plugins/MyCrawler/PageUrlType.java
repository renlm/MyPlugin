package cn.renlm.plugins.MyCrawler;

import cn.renlm.plugins.Common.IntToEnum;
import lombok.Getter;

/**
 * 页面链接类型
 * 
 * @author Renlm
 *
 */
public enum PageUrlType implements IntToEnum.IntValue {

	seed(0, "种子"), data(1, "数据");

	public static final String extrakey = "PageUrlTypeExtra";

	private final int type;

	@Getter
	private final String text;

	private PageUrlType(int type, String text) {
		this.type = type;
		this.text = text;
	}

	@Override
	public int value() {
		return this.type;
	}
}