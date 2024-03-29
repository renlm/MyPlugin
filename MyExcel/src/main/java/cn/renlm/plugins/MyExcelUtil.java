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
	 * 读取页签（适用小文件，自动拉平合并单元格，内存消耗较大）
	 * <p>
	 * 默认按配置名称获取数据，找不到取文件第一个
	 * </p>
	 * 
	 * @param config
	 * @param inputStream
	 * @param sheetName
	 * @param dataReadHandler
	 * @return
	 */
	public static final AbstractReader read(String config, InputStream inputStream, String sheetName,
			DataReadHandler dataReadHandler) {
		MyWorkbook myExcel = MyXStreamUtil.read(MyWorkbook.class, config);
		// Xls
		if (ExcelFileUtil.isXls(inputStream)) {
			AbstractReader reader = new XlsReader(myExcel, inputStream);
			reader.read(sheetName, dataReadHandler);
			return reader;
		}
		// Xlsx
		else if (ExcelFileUtil.isXlsx(inputStream)) {
			AbstractReader reader = new XlsxReader(myExcel, inputStream);
			reader.read(sheetName, dataReadHandler);
			return reader;
		}
		// Csv
		else {
			AbstractReader reader = new CsvReader(myExcel, inputStream);
			reader.read(sheetName, dataReadHandler);
			return reader;
		}
	}

	/**
	 * 读取页签（适用大文件，Sax模式）
	 * <p>
	 * 默认按配置名称获取数据，找不到取文件第一个
	 * 
	 * @param config
	 * @param inputStream
	 * @param sheetName
	 * @param dataReadHandler
	 * @return
	 */
	public static final AbstractReader readBySax(String config, InputStream inputStream, String sheetName,
			DataReadHandler dataReadHandler) {
		MyWorkbook myExcel = MyXStreamUtil.read(MyWorkbook.class, config);
		// Xls
		if (ExcelFileUtil.isXls(inputStream)) {
			AbstractReader reader = new XlsReader(myExcel, inputStream);
			reader.readBySax(sheetName, dataReadHandler);
			return reader;
		}
		// Xlsx
		else if (ExcelFileUtil.isXlsx(inputStream)) {
			AbstractReader reader = new XlsxReader(myExcel, inputStream);
			reader.readBySax(sheetName, dataReadHandler);
			return reader;
		}
		// Csv
		else {
			AbstractReader reader = new CsvReader(myExcel, inputStream);
			reader.readBySax(sheetName, dataReadHandler);
			return reader;
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
