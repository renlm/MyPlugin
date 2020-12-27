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

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 检查结果
 * 
 * @author renlm
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