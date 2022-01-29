package cn.renlm.plugins.MyExcel.handler;

import java.util.Date;
import java.util.Map;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.renlm.plugins.MyExcel.config.MyColumn;
import cn.renlm.plugins.MyExcel.config.MySheet;
import cn.renlm.plugins.MyExcel.config.column.Dict.DictItem;
import cn.renlm.plugins.MyExcel.config.column.Dict.DictType;
import cn.renlm.plugins.MyExcel.entity.CheckResult;

/**
 * 行数据读取处理
 * 
 * @author Renlm
 *
 */
@FunctionalInterface
public interface DataReadHandler {

	/**
	 * 单行数据处理
	 * 
	 * @param data
	 * @param checkResult
	 */
	void handle(final Map<String, Object> data, CheckResult checkResult);

	/**
	 * 单行数据读取转换
	 * 
	 * @param sheet
	 * @param rowIndex
	 * @param data
	 * @return
	 */
	default CheckResult readConvert(MySheet sheet, long rowIndex, Map<String, Object> data) {
		CheckResult checkResult = new CheckResult().setRowIndex(rowIndex).setProcess(true);
		Map<String, MyColumn> fieldColMap = sheet.getFieldColMap();
		for (Map.Entry<String, MyColumn> entry : fieldColMap.entrySet()) {
			String field = entry.getKey();
			MyColumn col = entry.getValue();
			// 空值
			if (StrUtil.isBlankIfStr(data.get(field))) {
				data.put(field, null);
			}
			// 去除前缀
			if (data.get(field) != null && StrUtil.isNotBlank(col.getPrefix())
					&& data.get(field).toString().startsWith(col.getPrefix())) {
				String valStr = data.get(field).toString();
				data.put(field, StrUtil.removePrefix(valStr, col.getPrefix()));
			}
			// 去除后缀
			if (data.get(field) != null && StrUtil.isNotBlank(col.getSuffix())
					&& data.get(field).toString().endsWith(col.getSuffix())) {
				String valStr = data.get(field).toString();
				data.put(field, StrUtil.removeSuffix(valStr, col.getSuffix()));
			}
			// 字典转换
			if (data.get(field) != null && col.getDict() != null) {
				String valStr = data.get(field).toString();
				if (col.getDict().getType() == DictType.key) {
					DictItem di = col.getDict().getKeyMap().get(valStr);
					if (di != null && StrUtil.isNotBlank(col.getDict().getConvertToField())) {
						data.put(col.getDict().getConvertToField(), di.getValue());
					}
				} else if (col.getDict().getType() == DictType.value) {
					DictItem di = col.getDict().getValMap().get(valStr);
					if (di != null) {
						valStr = di.getKey();
						data.put(field, valStr);
					}
				}
			}
			// 数字格式化
			if (data.get(field) != null && StrUtil.isNotBlank(col.getNumberFormat())) {
				String valStr = data.get(field).toString();
				data.put(field, NumberUtil.parseNumber(valStr));
			}
			// 日期转换
			if (data.get(field) != null && StrUtil.isNotBlank(col.getDateFormat())) {
				if (!(data.get(field) instanceof Date)) {
					try {
						String valStr = data.get(field).toString();
						data.put(field, DateUtil.parse(valStr, col.getDateFormat()));
					} catch (Exception e) {
						data.put(field, null);
						String message = StrUtil.format("{}，日期格式错误，限定{}", col.getTitle().getText(),
								col.getDateFormat());
						checkResult.getErrors().add(message);
						System.err.println(message);
					}
				}
			}
			// 非空字段
			if (col.isNotNull() && (data.get(field) == null || StrUtil.isBlankIfStr(data.get(field)))) {
				String message = StrUtil.format("{}，不能为空", col.getTitle().getText());
				checkResult.getErrors().add(message);
				System.err.println(message);
			}
		}
		return checkResult;
	}
}