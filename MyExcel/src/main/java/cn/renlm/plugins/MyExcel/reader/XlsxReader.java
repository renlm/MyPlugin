package cn.renlm.plugins.MyExcel.reader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.util.XMLHelper;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.eventusermodel.XSSFReader.SheetIterator;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.model.Comments;
import org.apache.poi.xssf.model.SharedStrings;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFComment;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.exceptions.POIException;
import cn.renlm.plugins.MyExcel.config.MyColumn;
import cn.renlm.plugins.MyExcel.config.MySheet;
import cn.renlm.plugins.MyExcel.config.MyWorkbook;
import cn.renlm.plugins.MyExcel.handler.DataReadHandler;
import lombok.Cleanup;
import lombok.SneakyThrows;

/**
 * Xlsx 解析
 * 
 * @author RenLiMing(任黎明)
 *
 */
public class XlsxReader extends AbstractReader implements XSSFSheetXMLHandler.SheetContentsHandler {

	private MySheet mySheet;
	private DataReadHandler dataReadHandler;

	protected StylesTable styles;
	private SharedStrings strings;
	private String firstSheetName;
	private Map<String, byte[]> sheetMap = new LinkedHashMap<>();
	private Map<String, Comments> sheetCommentsMap = new LinkedHashMap<>();

	private int rowNum;
	private int colNum;
	final List<List<String>> titles = new ArrayList<>();
	final List<String> keys = new ArrayList<>();
	private List<Object> rowCells = new ArrayList<>();

	@SneakyThrows
	public XlsxReader(MyWorkbook myExcel, InputStream in) {
		super(myExcel, in);
		@Cleanup
		InputStream inputStream = new ByteArrayInputStream(bytes);
		try (final OPCPackage opcPackage = OPCPackage.open(inputStream)) {
			XSSFReader xssfReader = new XSSFReader(opcPackage);
			this.styles = xssfReader.getStylesTable();
			this.strings = xssfReader.getSharedStringsTable();
			SheetIterator iter = (SheetIterator) xssfReader.getSheetsData();
			while (iter.hasNext()) {
				@Cleanup
				InputStream stream = iter.next();
				String sheetName = iter.getSheetName();
				this.sheetCommentsMap.put(sheetName, iter.getSheetComments());
				this.sheetMap.put(sheetName, IoUtil.readBytes(stream));
				this.firstSheetName = ObjectUtil.defaultIfBlank(this.firstSheetName, sheetName);
			}
		} catch (IOException e) {
			throw new IORuntimeException(e);
		} catch (InvalidFormatException e) {
			throw new POIException(e);
		}
	}

	@Override
	public AbstractReader read(String sheetName, DataReadHandler dataReadHandler) {
		return this;
	}

	@Override
	@SneakyThrows
	public AbstractReader readBySax(String sheetName, DataReadHandler dataReadHandler) {
		this.mySheet = myExcel.getSheetByName(sheetName);
		this.dataReadHandler = dataReadHandler;
		this.startProcess(sheetMap.containsKey(sheetName) ? sheetName : this.firstSheetName);
		return this;
	}

	@Override
	public void startRow(int rowNum) {
		this.rowNum = rowNum;
	}

	@Override
	public void endRow(int rowNum) {
		super.processRow(myExcel, titles, keys, dataReadHandler, mySheet, rowNum, rowCells);
		this.rowCells = new ArrayList<>(this.rowCells.size());
	}

	@Override
	public void cell(String cellReference, String formattedValue, XSSFComment comment) {
		if (cellReference == null) {
			return;
		} else {
			this.colNum = (int) (new CellReference(cellReference)).getCol();
			this.addToRowCells(this.rowNum, this.colNum, formattedValue);
		}
	}

	@SneakyThrows
	private void startProcess(String sheetName) {
		InputStream stream = new ByteArrayInputStream(this.sheetMap.get(sheetName));
		InputSource sheetSource = new InputSource(stream);
		XMLReader sheetParser = XMLHelper.newXMLReader();
		Comments comments = this.sheetCommentsMap.get(sheetName);
		DataFormatter df = new DataFormatter();
		ContentHandler handler = new XSSFSheetXMLHandler(this.styles, comments, this.strings, this, df, false);
		sheetParser.setContentHandler(handler);
		sheetParser.parse(sheetSource);
	}

	private void addToRowCells(int row, int column, Object value) {
		while (column > this.rowCells.size()) {
			this.rowCells.add(null);
		}
		this.rowCells.add(column, value);
	}

	public class DataFormatter extends org.apache.poi.ss.usermodel.DataFormatter {
		@Override
		public String formatRawCellContents(double value, int formatIndex, String formatString,
				boolean use1904Windowing) {
			if (DateUtil.isADateFormat(formatIndex, formatString)) {
				if (DateUtil.isValidExcelDate(value)) {
					String key = keys.get(colNum + 1);
					if (StrUtil.isNotBlank(key)) {
						MyColumn myColumn = mySheet.getFieldColMap().get(key);
						if (myColumn != null && StrUtil.isNotBlank(myColumn.getDateFormat())) {
							Date date = DateUtil.getJavaDate(value, use1904Windowing);
							return cn.hutool.core.date.DateUtil.format(date, myColumn.getDateFormat());
						}
					}
				}
			}
			return super.formatRawCellContents(value, formatIndex, formatString, use1904Windowing);
		}
	}

}
