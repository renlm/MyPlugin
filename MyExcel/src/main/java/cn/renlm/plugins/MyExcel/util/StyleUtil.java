package cn.renlm.plugins.MyExcel.util;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.renlm.plugins.ConstVal;
import cn.renlm.plugins.MyExcel.config.MyColumn;
import cn.renlm.plugins.MyExcel.config.column.Annotation;
import cn.renlm.plugins.MyExcel.config.column.Dict;
import cn.renlm.plugins.MyExcel.config.column.Dict.DictType;
import cn.renlm.plugins.MyExcel.entity.CellUnit;
import lombok.experimental.UtilityClass;

/**
 * 样式工具
 * 
 * @author RenLiMing(任黎明)
 *
 */
@UtilityClass
public class StyleUtil {

	/**
	 * 生成标题批注
	 * 
	 * @param wb
	 * @param sh
	 * @param startRow
	 * @param fieldTitles
	 */
	public static final void createTitleAnnotation(final Workbook wb, final Sheet sh, int startRow,
			List<Map<String, CellUnit>> fieldTitles) {
		int i = 0;
		int level = fieldTitles.size();
		int lastTitleRow = startRow + level - 1;
		for (Map<String, CellUnit> row : fieldTitles) {
			if (++i > 1) {
				break;
			}
			AtomicInteger colIdx = new AtomicInteger(0);
			for (Map.Entry<String, CellUnit> entry : row.entrySet()) {
				int colNum = colIdx.getAndIncrement();
				CellUnit cellUnit = entry.getValue();
				MyColumn column = cellUnit.getColumn();
				Annotation annotation = column.getAnnotation();
				if (annotation != null && StrUtil.isNotBlank(annotation.getText())) {
					Cell cell = MergeUtil.findFirstCellOfUnitRegion(sh, lastTitleRow, colNum);
					int col1 = cell.getColumnIndex();
					int row1 = cell.getRowIndex();
					int col2 = col1 + 3;
					int row2 = row1 + Math.max(level, 4);

					// 单元格批注
					Drawing<?> draw = sh.createDrawingPatriarch();
					Comment comment = draw.createCellComment(new XSSFClientAnchor(0, 0, 0, 0, col1, row1, col2, row2));
					XSSFRichTextString rtf = new XSSFRichTextString(annotation.getText());
					Font commentFormatter = wb.createFont();
					commentFormatter.setFontName(ConstVal.FONT);
					commentFormatter.setFontHeightInPoints(ConstVal.FONT_SIZE);
					rtf.applyFont(commentFormatter);
					comment.setString(rtf);
					cell.setCellComment(comment);
				}
			}
		}
	}

	/**
	 * 生成下拉选
	 * 
	 * @param sh
	 * @param startRow
	 * @param fieldTitles
	 */
	public static final void createDataValidation(final Sheet sh, int startRow,
			List<Map<String, CellUnit>> fieldTitles) {
		int i = 0;
		int level = fieldTitles.size();
		int dataPos = startRow + level - 1;
		for (Map<String, CellUnit> row : fieldTitles) {
			if (++i > 1) {
				break;
			}
			AtomicInteger colIdx = new AtomicInteger(0);
			for (Map.Entry<String, CellUnit> entry : row.entrySet()) {
				int colNum = colIdx.getAndIncrement();
				CellUnit cellUnit = entry.getValue();
				MyColumn column = cellUnit.getColumn();
				Dict dict = column.getDict();
				if (dict != null && dict.getItems().size() > 0) {
					List<String> items = dict.getItems().stream().map(it -> {
						if (dict.getType() == DictType.key) {
							return it.getKey();
						} else {
							return it.getValue();
						}
					}).filter(it -> StrUtil.isNotBlank(it)).distinct().collect(Collectors.toList());
					if (items.size() > 0) {
						CellRangeAddressList cellRangeAddressList = new CellRangeAddressList(dataPos, 65535, colNum,
								colNum);
						DataValidationHelper helper = sh.getDataValidationHelper();
						DataValidationConstraint constraint = helper
								.createExplicitListConstraint(items.toArray(new String[items.size()]));
						DataValidation dataValidation = helper.createValidation(constraint, cellRangeAddressList);
						dataValidation.setEmptyCellAllowed(true);
						if (dict.isForceCheck()) {
							dataValidation.setShowPromptBox(true);
							dataValidation.createPromptBox("提示", "请从下拉框里选择数据");
							dataValidation.setSuppressDropDownArrow(true);
							dataValidation.setShowErrorBox(true);
						}
						sh.addValidationData(dataValidation);
					}
				}
			}
		}
	}

	/**
	 * 列宽自适应
	 * 
	 * @param sh
	 * @param fieldTitles
	 */
	public static final void autoSizeColumn(final SXSSFSheet sh, List<Map<String, CellUnit>> fieldTitles) {
		int i = 0;
		sh.trackAllColumnsForAutoSizing();
		for (Map<String, CellUnit> row : fieldTitles) {
			if (++i > 1) {
				break;
			}
			AtomicInteger colIdx = new AtomicInteger(0);
			row.forEach((field, cellUnit) -> {
				int colNum = colIdx.getAndIncrement();
				MyColumn column = cellUnit.getColumn();
				sh.autoSizeColumn(colNum, true);
				int width = ArrayUtil.max(ConstVal.COL_MIN_WIDTH, column.getWidth256() * 256,
						sh.getColumnWidth(colNum) + 256);
				sh.setColumnWidth(colNum, ArrayUtil.min(width, ConstVal.COL_MAX_WIDTH));
			});
		}
	}

	/**
	 * 单元格样式（带边框）
	 * 
	 * @param wb
	 * @param fontName
	 * @param fontSize
	 * @param isBold
	 * @param align
	 * @return
	 */
	public static final CellStyle createCellStyleWithBorder(final Workbook wb, final String fontName,
			final short fontSize, final Boolean isBold, final HorizontalAlignment align) {
		CellStyle cellStyle = wb.createCellStyle();

		Font font = wb.createFont();
		font.setFontName(fontName);
		font.setFontHeightInPoints(fontSize);
		font.setBold(isBold == null ? false : isBold);
		cellStyle.setFont(font);
		cellStyle.setAlignment(align == null ? HorizontalAlignment.LEFT : align);

		cellStyle.setBorderBottom(BorderStyle.THIN);
		cellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
		cellStyle.setBorderLeft(BorderStyle.THIN);
		cellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
		cellStyle.setBorderRight(BorderStyle.THIN);
		cellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
		cellStyle.setBorderTop(BorderStyle.THIN);
		cellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
		return cellStyle;
	}
}