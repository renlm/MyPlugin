package cn.renlm.plugins.MyExcel.handler;

import java.util.Map;

import cn.renlm.plugins.MyExcel.entity.CheckResult;

/**
 * 行数据读取处理
 * 
 * @author Renlm
 *
 */
@FunctionalInterface
public interface DataReadHandler {

	void handle(final Map<String, Object> data, CheckResult checkResult);

}