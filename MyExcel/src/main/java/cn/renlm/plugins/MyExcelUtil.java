package cn.renlm.plugins;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelFileUtil;
import cn.renlm.plugins.MyExcel.config.MySheet;
import cn.renlm.plugins.MyExcel.config.MyWorkbook;
import cn.renlm.plugins.MyExcel.entity.CellUnit;
import cn.renlm.plugins.MyExcel.handler.DataReadHandler;
import cn.renlm.plugins.MyExcel.handler.DataWriterHandler;
import cn.renlm.plugins.MyExcel.reader.AbstractReader;
import cn.renlm.plugins.MyExcel.reader.CsvReader;
import cn.renlm.plugins.MyExcel.reader.XlsReader;
import cn.renlm.plugins.MyExcel.reader.XlsxReader;
import cn.renlm.plugins.MyExcel.util.MergeUtil;
import cn.renlm.plugins.MyExcel.util.StyleUtil;
import cn.renlm.plugins.MyUtil.MyXStreamUtil;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

/**
 * 表格工具类
 * 
 * @author RenLiMing(任黎明)
 *
 */
@UtilityClass
public class MyExcelUtil {

	/**
	 * 读取表格
	 * 
	 * @param config
	 * @param inputStream
	 * @param sheetName
	 * @param handler
	 * @return
	 */
	public static final AbstractReader read(String config, InputStream inputStream, String sheetName,
			DataReadHandler handler) {
		MyWorkbook myExcel = MyXStreamUtil.read(MyWorkbook.class, config);
		// Xls
		if (ExcelFileUtil.isXls(inputStream)) {
			return new XlsReader(myExcel, inputStream);
		}
		// Xlsx
		else if (ExcelFileUtil.isXlsx(inputStream)) {
			return new XlsxReader(myExcel, inputStream);
		}
		// Csv
		else {
			return new CsvReader(myExcel, inputStream);
		}
	}

	/**
	 * 创建工作簿
	 * 
	 * @param config
	 * @param isTemplate
	 * @param sheet
	 * @return
	 */
	@SafeVarargs
	@SneakyThrows
	public static final Workbook createWorkbook(String config, boolean isTemplate,
			Consumer<DataWriterHandler>... sheet) {
		SXSSFWorkbook workbook = null;
		final MyWorkbook myExcel = MyXStreamUtil.read(MyWorkbook.class, config);
		// 直接根据配置生成
		if (StrUtil.isBlankIfStr(myExcel.getRef())) {
			workbook = new SXSSFWorkbook();
		}
		// 引用预置模板
		else {
			@Cleanup
			InputStream in = ResourceUtil.getStream(myExcel.getRef());
			workbook = new SXSSFWorkbook(new XSSFWorkbook(in));
		}
		// 添加模板页签
		for (int i = 0; i < myExcel.getSheets().size(); i++) {
			MySheet mySheet = myExcel.getSheets().get(i);
			int level = mySheet.level();
			SXSSFSheet createSheet = workbook.createSheet(mySheet.getName());
			workbook.setSheetOrder(mySheet.getName(), i);
			List<Map<String, CellUnit>> fieldTitles = mySheet.fieldTitles(workbook, isTemplate);
			Map<String, List<Integer[]>> rowColMap = mySheet.writeSheetTitle(mySheet.getStart(), createSheet,
					fieldTitles);
			MergeUtil.mergeComplexTitle(createSheet,
					MergeUtil.findCellRangeAddress(mySheet.getStart(), level, rowColMap));
			createSheet.createFreezePane(mySheet.getFreezes(), mySheet.getStart() + level);
			StyleUtil.createTitleAnnotation(workbook, createSheet, mySheet.getStart(), fieldTitles);
			StyleUtil.createDataValidation(createSheet, mySheet.getStart(), fieldTitles);
			StyleUtil.autoSizeColumn(createSheet, fieldTitles);
			if (i < sheet.length) {
				sheet[i].accept(new DataWriterHandler(workbook, createSheet, mySheet.getStart() + level, fieldTitles));
			}
		}
		// 后续处理
		workbook.setActiveSheet(0);
		workbook.setSelectedTab(0);
		return workbook;
	}

}
