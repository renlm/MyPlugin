package cn.renlm.plugins.MyExcel.handler;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.renlm.plugins.MyExcel.config.MyColumn;
import cn.renlm.plugins.MyExcel.config.column.Dict.DictItem;
import cn.renlm.plugins.MyExcel.config.column.Dict.DictType;
import cn.renlm.plugins.MyExcel.entity.CellUnit;
import cn.renlm.plugins.MyExcel.util.StyleUtil;

/**
 * 数据写入处理
 * 
 * @author RenLiMing(任黎明)
 *
 */
public class DataWriterHandler {

	private final SXSSFWorkbook wb;

	private final SXSSFSheet createSheet;

	private final int dataRowStart;

	private final List<Map<String, CellUnit>> fieldTitles;

	public DataWriterHandler(SXSSFWorkbook wb, SXSSFSheet createSheet, int dataRowStart,
			List<Map<String, CellUnit>> fieldTitles) {
		this.wb = wb;
		this.createSheet = createSheet;
		this.dataRowStart = dataRowStart;
		this.fieldTitles = fieldTitles;
	}

	public String getSheetName() {
		return this.createSheet.getSheetName();
	}

	/**
	 * 数据写入
	 * 
	 * @param object
	 */
	public void write(Object object) {
		List<Object> datas = new ArrayList<>();
		Map<String, CellUnit> fields = CollUtil.getFirst(fieldTitles);

		// 区分是否批量写入数据
		Class<?> clazz = object.getClass();
		if (clazz.isArray()) {
			CollUtil.addAll(datas, (Object[]) object);
		} else if (ClassUtil.isAssignable(Iterable.class, clazz)) {
			CollUtil.addAll(datas, (Iterable<?>) object);
		} else {
			datas.add(object);
		}

		// 逐行写入
		for (Object it : datas) {
			Map<String, Object> data = BeanUtil.beanToMap(it);
			int colNum = 0;
			int rowNum = createSheet.getLastRowNum() + 1;
			Row row = createSheet.createRow(rowNum);
			for (Map.Entry<String, CellUnit> entry : fields.entrySet()) {
				String field = entry.getKey();
				CellUnit cellUnit = entry.getValue();
				MyColumn col = cellUnit.getColumn();
				Cell cell = row.createCell(colNum++);
				Object value = data.get(field);
				// 空值，写入空字符串
				if (value == null) {
					data.put(field, StrUtil.EMPTY);
				}
				// 转换规则
				else {
					// 日期转换
					if (data.get(field) != null && StrUtil.isNotBlank(col.getDateFormat())) {
						if (data.get(field) instanceof Date) {
							data.put(field, DateUtil.format((Date) data.get(field), col.getDateFormat()));
						}
					}
					// 数字格式化
					if (StrUtil.isNotBlank(col.getNumberFormat())) {
						data.put(field, NumberUtil.decimalFormat(col.getNumberFormat(), data.get(field)));
					}
					// 字典转换
					if (col.getDict() != null) {
						String valStr = data.get(field).toString();
						if (col.getDict().getType() == DictType.value) {
							DictItem di = col.getDict().getKeyMap().get(valStr);
							if (di != null) {
								data.put(field, di.getValue());
							}
						}
					}
					// 添加后缀
					if (StrUtil.isNotBlank(col.getSuffix())) {
						String valStr = data.get(field).toString();
						data.put(field, StrUtil.addSuffixIfNot(valStr, col.getSuffix()));
					}
					// 添加前缀
					if (StrUtil.isNotBlank(col.getPrefix())) {
						String valStr = data.get(field).toString();
						data.put(field, StrUtil.addPrefixIfNot(valStr, col.getPrefix()));
					}
				}
				// 值|样式
				cell.setCellValue(ObjectUtil.toString(data.get(field)));
				cell.setCellStyle(cellUnit.getColumn().getCellStyle(this.wb));
			}
			if (rowNum == this.dataRowStart || (rowNum - this.dataRowStart) == 25) {
				StyleUtil.autoSizeColumn(createSheet, fieldTitles);
			}
		}
	}
}