package cn.renlm.plugins.MyExcel.entity;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 检查结果
 * 
 * @author RenLiMing(任黎明)
 *
 */
@Data
@Accessors(chain = true)
public class CheckResult {

	/**
	 * 错误消息
	 */
	private final List<String> errors = new ArrayList<>();

	/**
	 * 行号（起始行：0）
	 */
	private long rowIndex;

	/**
	 * 是否进入数据流程
	 */
	private boolean process;

	/**
	 * 是否发生错误
	 */
	private boolean error;

	/**
	 * 错误消息不为空即为发生错误
	 * 
	 * @return
	 */
	public boolean isError() {
		this.error = this.errors.size() > 0;
		return this.error;
	}
}