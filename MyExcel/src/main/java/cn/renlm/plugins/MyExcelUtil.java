package cn.renlm.plugins;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.read.builder.ExcelReaderBuilder;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.csv.CsvReader;
import cn.hutool.core.text.csv.CsvUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelFileUtil;
import cn.renlm.plugins.MyExcel.config.MySheet;
import cn.renlm.plugins.MyExcel.config.MyWorkbook;
import cn.renlm.plugins.MyExcel.entity.CellUnit;
import cn.renlm.plugins.MyExcel.entity.CheckResult;
import cn.renlm.plugins.MyExcel.handler.DataReadHandler;
import cn.renlm.plugins.MyExcel.handler.DataWriterHandler;
import cn.renlm.plugins.MyExcel.util.MergeUtil;
import cn.renlm.plugins.MyExcel.util.StyleUtil;
import cn.renlm.plugins.MyUtil.MyXStreamUtil;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

/**
 * 表格工具类
 * 
 * @author Renlm
 *
 */
@UtilityClass
public class MyExcelUtil {

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

	/**
	 * 读取（兼容xls、xlsx、csv）
	 * 
	 * @param config
	 * @param in
	 * @param sheetNo
	 * @param dataReadHandler
	 * @return
	 */
	public static final int readBySax(String config, InputStream in, Integer sheetNo, DataReadHandler dataReadHandler) {
		return readBySax(config, in, sheetNo, null, dataReadHandler);
	}

	/**
	 * 读取（兼容xls、xlsx、csv）
	 * 
	 * @param config
	 * @param in
	 * @param sheetName
	 * @param dataReadHandler
	 * @return
	 */
	public static final int readBySax(String config, InputStream in, String sheetName,
			DataReadHandler dataReadHandler) {
		return readBySax(config, in, null, sheetName, dataReadHandler);
	}

	/**
	 * 读取（兼容xls、xlsx、csv）
	 * 
	 * @param config
	 * @param in
	 * @param sheetNo
	 * @param sheetName
	 * @param dataReadHandler
	 * @return
	 */
	public static final int readBySax(String config, InputStream in, Integer sheetNo, String sheetName,
			DataReadHandler dataReadHandler) {
		final MyWorkbook myExcel = MyXStreamUtil.read(MyWorkbook.class, config);
		final MySheet sheet = myExcel.getSheetByName(sheetName);

		final List<List<String>> titles = new ArrayList<>();
		final List<String> keys = new ArrayList<>();
		final AtomicInteger rows = new AtomicInteger(0);

		// Excel
		if (ExcelFileUtil.isXls(in) || ExcelFileUtil.isXlsx(in)) {
			ExcelReaderBuilder builder = EasyExcel.read(in, new AnalysisEventListener<Map<Integer, Object>>() {
				@Override
				public void invoke(Map<Integer, Object> data, AnalysisContext context) {
					rows.incrementAndGet();
					int rowIndex = context.readRowHolder().getRowIndex();
					data = MapUtil.sort(data, (rowIndex1, rowIndex2) -> {
						return rowIndex1 - rowIndex2;
					});
					processRow(myExcel, titles, keys, dataReadHandler, sheet, rowIndex,
							CollUtil.newArrayList(data.values()));
				}

				@Override
				public void doAfterAllAnalysed(AnalysisContext context) {

				}
			});
			builder.sheet(sheetNo, sheetName).headRowNumber(0).doRead();
		}
		// Csv
		else {
			CsvReader reader = CsvUtil.getReader();
			reader.read(IoUtil.getReader(in, Charset.forName(myExcel.getCsvCharset())), csvRow -> {
				rows.incrementAndGet();
				long rowIndex = csvRow.getOriginalLineNumber();
				List<Object> rowList = new ArrayList<>(csvRow.getRawList());
				processRow(myExcel, titles, keys, dataReadHandler, sheet, rowIndex, rowList);
			});
		}
		return rows.get();
	}

	/**
	 * 行数据处理
	 * 
	 * @param myExcel
	 * @param titles
	 * @param keys
	 * @param dataReadHandler
	 * @param sheet
	 * @param rowIndex
	 * @param rowList
	 */
	private static final void processRow(MyWorkbook myExcel, List<List<String>> titles, List<String> keys,
			DataReadHandler dataReadHandler, MySheet sheet, long rowIndex, List<Object> rowList) {
		final int sheetLevel = sheet.level();
		final long level = rowIndex - sheet.getStart() + 1;
		if (level >= 1) {
			if (level <= sheetLevel) { // 标题行，建立[字段-值索引]映射
				titles.add(MySheet.fillTitle(rowList));
				if (level == sheetLevel) {
					keys.addAll(sheet.generateKeys(titles, dataReadHandler));
				}
			} else { // 数据行，取出映射数据
				Map<String, Object> data = CollUtil.zip(keys, rowList);
				data.remove(StrUtil.EMPTY);
				CheckResult checkResult = dataReadHandler.readConvert(sheet, rowIndex, data);
				dataReadHandler.handle(data, checkResult);
			}
		}
	}

}
