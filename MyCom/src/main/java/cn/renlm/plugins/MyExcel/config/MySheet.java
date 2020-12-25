package cn.renlm.plugins.MyExcel.config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.CharUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.renlm.plugins.MyExcel.config.column.Alias;
import cn.renlm.plugins.MyExcel.config.column.Dict.DictItem;
import cn.renlm.plugins.MyExcel.config.column.Dict.DictType;
import cn.renlm.plugins.MyExcel.config.column.Title;
import cn.renlm.plugins.MyExcel.entity.CellUnit;
import cn.renlm.plugins.MyExcel.entity.CheckResult;
import cn.renlm.plugins.MyExcel.handler.DataReadHandler;
import cn.renlm.plugins.MyExcel.util.ConstVal;
import cn.renlm.plugins.MyExcel.util.StyleUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * Sheet页配置
 * 
 * @author renlm
 *
 */
@Data
@Slf4j
public class MySheet implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Sheet页名称
	 */
	@XStreamAsAttribute
	private String name;

	/**
	 * 起始行号（第一行：0）
	 */
	@XStreamAsAttribute
	private int start = 0;

	/**
	 * 冻结列数（左侧）
	 */
	@XStreamAsAttribute
	private int freezes = 0;

	/**
	 * 列配置集
	 */
	@XStreamImplicit(itemFieldName = "column")
	private List<MyColumn> columns;

	/**
	 * 标题层级
	 * 
	 * @return
	 */
	public int level() {
		return this.columns.stream().map(col -> col.getTitle()).mapToInt(title -> {
			if (StrUtil.isNotBlank(title.getSplit())) {
				return title.getText().split(title.getSplit()).length;
			}
			return 1;
		}).max().getAsInt();
	}

	/**
	 * 标题键-字段映射
	 * 
	 * @return
	 */
	public Map<String, String> keyFields() {
		Map<String, String> map = new LinkedHashMap<>();
		for (MyColumn col : columns) {
			if (col.isIgnore()) {
				continue;
			}
			Title title = col.getTitle();
			map.put(title.getText(), col.getField());
			if (StrUtil.isNotBlank(title.getSplit())) {
				map.put(title.getText().replace(title.getSplit(), ConstVal.LEVEL_TITLE_SPLIT), col.getField());
			}
			if (CollUtil.isEmpty(col.getAliasList())) {
				continue;
			}
			for (Alias alias : col.getAliasList()) {
				if (StrUtil.isEmpty(alias.getText())) {
					continue;
				}
				map.put(alias.getText(), col.getField());
				if (StrUtil.isNotBlank(title.getSplit())) {
					map.put(alias.getText().replace(title.getSplit(), ConstVal.LEVEL_TITLE_SPLIT), col.getField());
				}
			}
		}
		return map;
	}

	/**
	 * 生成[标题|字段-值索引]映射
	 * 
	 * @param titles
	 * @param dataReadHandler
	 * @return
	 */
	public List<String> generateKeys(List<List<String>> titles, DataReadHandler dataReadHandler) {
		List<String> keys = new ArrayList<>();
		Map<String, String> keyFields = keyFields();
		CheckResult checkResult = new CheckResult().setRowIndex(this.start);
		int length = titles.stream().mapToInt(it -> it.size()).max().getAsInt();
		for (int i = 0; i < length; i++) {
			List<String> appends = new ArrayList<>();
			for (List<String> it : titles) {
				String str = CollUtil.get(it, i);
				if (StrUtil.isNotBlank(str)) {
					if (appends.size() == 0 || !str.equals(appends.get(appends.size() - 1))) {
						appends.add(str);
					}
				}
			}
			String key = CollUtil.join(appends, ConstVal.LEVEL_TITLE_SPLIT);
			if (keyFields.containsKey(key)) {
				keys.add(keyFields.get(key));
			} else {
				keys.add(StrUtil.EMPTY);
			}
		}
		// 检查模板
		Map<String, MyColumn> fieldColMap = this.getFieldColMap();
		for (Map.Entry<String, MyColumn> entry : fieldColMap.entrySet()) {
			String field = entry.getKey();
			MyColumn col = entry.getValue();
			if (!col.isIgnore() && !col.isOptional() && !keys.contains(field)) {
				String message = StrUtil.format("{}，模板错误，缺失列", col.getTitle().getText());
				checkResult.getErrors().add(message);
			}
		}
		if (checkResult.isError()) {
			dataReadHandler.handle(MapUtil.empty(), checkResult);
		}
		return keys;
	}

	/**
	 * 数据读取转换
	 * 
	 * @param rowIndex
	 * @param data
	 * @return
	 */
	public CheckResult readConvert(long rowIndex, Map<String, Object> data) {
		CheckResult checkResult = new CheckResult().setRowIndex(rowIndex).setProcess(true);
		Map<String, MyColumn> fieldColMap = getFieldColMap();
		for (Map.Entry<String, MyColumn> entry : fieldColMap.entrySet()) {
			String field = entry.getKey();
			MyColumn col = entry.getValue();
			Object value = data.get(field);
			// 忽略字段
			if (col.isIgnore() || StrUtil.isBlankIfStr(value)) {
				data.put(field, null);
				continue;
			}
			// 去除前缀
			if (StrUtil.isNotBlank(col.getPrefix()) && data.get(field).toString().startsWith(col.getPrefix())) {
				String valStr = data.get(field).toString();
				data.put(field, StrUtil.removePrefix(valStr, col.getPrefix()));
			}
			// 去除后缀
			if (StrUtil.isNotBlank(col.getSuffix()) && data.get(field).toString().endsWith(col.getSuffix())) {
				String valStr = data.get(field).toString();
				data.put(field, StrUtil.removeSuffix(valStr, col.getSuffix()));
			}
			// 字典转换
			if (col.getDict() != null) {
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
			if (StrUtil.isNotBlank(col.getNumberFormat())) {
				String valStr = data.get(field).toString();
				data.put(field, NumberUtil.parseNumber(valStr));
			}
			// 日期转换
			if (StrUtil.isNotBlank(col.getDateFormat())) {
				if (!(data.get(field) instanceof Date)) {
					try {
						String valStr = data.get(field).toString();
						data.put(field, DateUtil.parse(valStr, col.getDateFormat()));
					} catch (Exception e) {
						data.put(field, null);
						String message = StrUtil.format("{}，日期格式错误，限定{}", col.getTitle().getText(),
								col.getDateFormat());
						checkResult.getErrors().add(message);
						log.error(message);
					}
				}
			}
			// 非空字段
			if (col.isNotNull() && StrUtil.isBlankIfStr(data.get(field))) {
				String message = StrUtil.format("{}，不能为空", col.getTitle().getText());
				checkResult.getErrors().add(message);
				log.error(message);
			}
		}
		return checkResult;
	}

	/**
	 * 填充标题（根据模板配置）
	 * 
	 * @param wb
	 * @param isTemplate
	 * @return [ { 字段: 单元格 } ]
	 */
	public List<Map<String, CellUnit>> fieldTitles(SXSSFWorkbook wb, boolean isTemplate) {
		int level = level();
		List<Map<String, CellUnit>> titles = new ArrayList<>();
		for (int i = 0; i < level; i++) {
			Map<String, CellUnit> data = new LinkedHashMap<>();
			titles.add(data);
			for (MyColumn col : columns) {
				// 忽略字段
				if (col.isIgnore()) {
					continue;
				}
				// 模板不显示可选字段
				if (isTemplate && col.isOptional()) {
					continue;
				}
				// 单元格属性
				CellUnit cellUnit = new CellUnit();
				cellUnit.setRowIndex(start + i);
				cellUnit.setColIndex(i);
				cellUnit.setColumn(col);
				data.put(col.getField(), cellUnit);
				// 单级表头，竖向填充
				if (StrUtil.isBlank(col.getTitle().getSplit())) {
					short fontSize = level == 1 ? ConstVal.FONT_SIZE : (ConstVal.FONT_SIZE + 1);
					CellStyle cellStyle = StyleUtil.createCellStyleWithBorder(wb, ConstVal.FONT, fontSize, true,
							col.getAlign());
					cellUnit.setText(col.getTitle().getText());
					cellUnit.setCellStyle(cellStyle);
				}
				// 多级表头，由末级竖向填充
				else {
					String[] arr = col.getTitle().getText().split(col.getTitle().getSplit());
					short fontSize = i >= (arr.length - 1) ? ConstVal.FONT_SIZE : (ConstVal.FONT_SIZE + 1);
					HorizontalAlignment align = i >= (arr.length - 1) ? col.getAlign() : HorizontalAlignment.CENTER;
					CellStyle cellStyle = StyleUtil.createCellStyleWithBorder(wb, ConstVal.FONT, fontSize, true, align);
					cellUnit.setText(i >= arr.length ? arr[arr.length - 1] : arr[i]);
					cellUnit.setCellStyle(cellStyle);
				}
			}
		}
		return titles;
	}

	/**
	 * 填充标题（根据读取数据）
	 * 
	 * @param rowList
	 * @return
	 */
	public static final List<String> fillTitle(List<Object> rowList) {
		String lastTitle = null;
		List<String> list = new ArrayList<>();
		for (Object obj : rowList) {
			if (obj == null) {
				list.add(lastTitle);
			} else {
				String str = obj.toString().chars().filter(c -> !CharUtil.isBlankChar(c))
						.mapToObj(c -> CharUtil.toString((char) c)).collect(Collectors.joining());
				if (StrUtil.isEmptyIfStr(str)) {
					list.add(StrUtil.EMPTY);
				} else {
					lastTitle = str;
					list.add(lastTitle);
				}
			}
		}
		return list;
	}

	/**
	 * 数据字段-列配置映射
	 */
	private Map<String, MyColumn> fieldColMap;

	/**
	 * 获取数据字段-列配置映射
	 * 
	 * @return
	 */
	public Map<String, MyColumn> getFieldColMap() {
		if (fieldColMap == null) {
			fieldColMap = new LinkedHashMap<>();
			for (MyColumn col : columns) {
				fieldColMap.put(col.getField(), col);
			}
		}
		return fieldColMap;
	}
}