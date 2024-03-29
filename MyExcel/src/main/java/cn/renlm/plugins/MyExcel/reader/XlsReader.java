package cn.renlm.plugins.MyExcel.reader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.poi.hssf.eventusermodel.EventWorkbookBuilder.SheetRecordCollectingListener;
import org.apache.poi.hssf.eventusermodel.FormatTrackingHSSFListener;
import org.apache.poi.hssf.eventusermodel.HSSFEventFactory;
import org.apache.poi.hssf.eventusermodel.HSSFListener;
import org.apache.poi.hssf.eventusermodel.HSSFRequest;
import org.apache.poi.hssf.eventusermodel.MissingRecordAwareHSSFListener;
import org.apache.poi.hssf.eventusermodel.dummyrecord.LastCellOfRowDummyRecord;
import org.apache.poi.hssf.model.HSSFFormulaParser;
import org.apache.poi.hssf.record.BOFRecord;
import org.apache.poi.hssf.record.BoolErrRecord;
import org.apache.poi.hssf.record.BoundSheetRecord;
import org.apache.poi.hssf.record.FormulaRecord;
import org.apache.poi.hssf.record.LabelRecord;
import org.apache.poi.hssf.record.LabelSSTRecord;
import org.apache.poi.hssf.record.NumberRecord;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.record.SSTRecord;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.cell.CellUtil;
import cn.hutool.poi.excel.sax.ExcelSaxUtil;
import cn.hutool.poi.exceptions.POIException;
import cn.renlm.plugins.MyExcel.config.MySheet;
import cn.renlm.plugins.MyExcel.config.MyWorkbook;
import cn.renlm.plugins.MyExcel.handler.DataReadHandler;
import cn.renlm.plugins.MyExcel.util.MergeUtil;
import lombok.Cleanup;
import lombok.SneakyThrows;

/**
 * Xls 解析
 * 
 * @author RenLiMing(任黎明)
 *
 */
public class XlsReader extends AbstractReader implements HSSFListener {

	private MySheet mySheet;
	private DataReadHandler dataReadHandler;

	private FormatTrackingHSSFListener formatListener;
	private SheetRecordCollectingListener workbookBuildingListener;
	private SSTRecord sstRecord;
	private HSSFWorkbook stubWorkbook;
	private final List<BoundSheetRecord> boundSheetRecords = new ArrayList<>();

	private Integer rSheetIndex = -1;
	private Integer sheetIndex = -1;
	final List<List<String>> titles = new ArrayList<>();
	final List<String> keys = new ArrayList<>();
	private List<Object> rowCells = new ArrayList<>();

	public XlsReader(MyWorkbook myExcel, InputStream in) {
		super(myExcel, in);
	}

	@Override
	@SneakyThrows
	public AbstractReader read(String sheetName, DataReadHandler dataReadHandler) {
		this.titles.clear();
		this.keys.clear();
		this.rowCells.clear();
		this.mySheet = myExcel.getSheetByName(sheetName);
		this.dataReadHandler = dataReadHandler;
		@Cleanup
		InputStream in = new ByteArrayInputStream(bytes);
		ExcelReader reader = ExcelUtil.getReader(in, 0);
		List<String> sheetNames = reader.getSheetNames();
		if (sheetNames.contains(sheetName)) {
			reader.setSheet(sheetName);
		} else {
			reader.setSheet(0);
		}
		AtomicInteger rowIndex = new AtomicInteger(0);
		reader.read((cell, value) -> {
			int rIndex = cell.getRowIndex();
			int cIndex = cell.getColumnIndex();
			if (!NumberUtil.equals(rowIndex.get(), rIndex)) {
				super.processRow(myExcel, titles, keys, dataReadHandler, mySheet, rowIndex.getAndIncrement(), rowCells);
				this.rowCells.clear();
			}
			if (value == null) {
				this.addToRowCells(rIndex, cIndex, value);
			} else {
				Sheet sh = cell.getSheet();
				Cell _cell = MergeUtil.findFirstCellOfUnitRegion(sh, rIndex, cIndex);
				Object _value = CellUtil.getCellValue(_cell);
				this.addToRowCells(rIndex, cIndex, _value);
			}
		});
		if (CollUtil.isNotEmpty(this.rowCells)) {
			super.processRow(myExcel, titles, keys, dataReadHandler, mySheet, rowIndex.getAndIncrement(), rowCells);
			this.rowCells.clear();
		}
		return this;
	}

	@Override
	public AbstractReader readBySax(String sheetName, DataReadHandler dataReadHandler) {
		this.mySheet = myExcel.getSheetByName(sheetName);
		this.dataReadHandler = dataReadHandler;
		this.startProcess(-1);
		if (this.rSheetIndex == -1) {
			this.startProcess(0);
		}
		return this;
	}

	@Override
	public void processRecord(Record record) {
		if (this.rSheetIndex > -1 && this.sheetIndex > this.rSheetIndex) {
			return;
		}

		Object value = null;
		final short sid = record.getSid();
		switch (sid) {
		case BoundSheetRecord.sid:
			final BoundSheetRecord boundSheetRecord = (BoundSheetRecord) record;
			this.boundSheetRecords.add(boundSheetRecord);
			String currentSheetName = boundSheetRecord.getSheetname();
			if (this.rSheetIndex < 0 && StrUtil.equals(this.mySheet.getName(), currentSheetName)) {
				this.rSheetIndex = this.boundSheetRecords.size() - 1;
			}
			break;
		case BOFRecord.sid:
			final BOFRecord bofRecord = (BOFRecord) record;
			if (bofRecord.getType() == BOFRecord.TYPE_WORKSHEET) {
				this.sheetIndex++;
				if (this.workbookBuildingListener != null && this.stubWorkbook == null) {
					this.stubWorkbook = this.workbookBuildingListener.getStubHSSFWorkbook();
				}
			}
			break;
		case SSTRecord.sid:
			this.sstRecord = (SSTRecord) record;
			break;
		default:
			break;
		}

		if (NumberUtil.equals(this.rSheetIndex, this.sheetIndex)) {
			switch (sid) {
			case BoolErrRecord.sid:
				final BoolErrRecord berec = (BoolErrRecord) record;
				value = berec.getBooleanValue();
				this.addToRowCells(berec.getRow(), berec.getColumn(), value);
				break;
			case FormulaRecord.sid:
				final FormulaRecord formulaRec = (FormulaRecord) record;
				value = HSSFFormulaParser.toFormulaString(this.stubWorkbook, formulaRec.getParsedExpression());
				this.addToRowCells(formulaRec.getRow(), formulaRec.getColumn(), value);
				break;
			case LabelRecord.sid:
				final LabelRecord lrec = (LabelRecord) record;
				this.addToRowCells(lrec.getRow(), lrec.getColumn(), lrec.getValue());
				break;
			case LabelSSTRecord.sid:
				final LabelSSTRecord lsrec = (LabelSSTRecord) record;
				value = this.sstRecord == null ? null : this.sstRecord.getString(lsrec.getSSTIndex()).toString();
				this.addToRowCells(lsrec.getRow(), lsrec.getColumn(), value);
				break;
			case NumberRecord.sid:
				final NumberRecord numrec = (NumberRecord) record;
				value = ExcelSaxUtil.getNumberOrDateValue(numrec, numrec.getValue(), this.formatListener);
				this.addToRowCells(numrec.getRow(), numrec.getColumn(), value);
				break;
			default:
				break;
			}

			if (record instanceof LastCellOfRowDummyRecord) {
				LastCellOfRowDummyRecord lastCell = (LastCellOfRowDummyRecord) record;
				super.processRow(myExcel, titles, keys, dataReadHandler, mySheet, lastCell.getRow(), rowCells);
				this.rowCells.clear();
			}
		}
	}

	@SneakyThrows
	private void startProcess(Integer rSheetIndex) {
		this.sstRecord = null;
		this.stubWorkbook = null;
		this.boundSheetRecords.clear();
		this.rSheetIndex = rSheetIndex;
		this.sheetIndex = -1;
		this.titles.clear();
		this.keys.clear();
		this.rowCells.clear();

		@Cleanup
		InputStream in = new ByteArrayInputStream(bytes);
		@Cleanup
		POIFSFileSystem fs = new POIFSFileSystem(in);

		formatListener = new FormatTrackingHSSFListener(new MissingRecordAwareHSSFListener(this));
		final HSSFRequest request = new HSSFRequest();
		workbookBuildingListener = new SheetRecordCollectingListener(formatListener);
		request.addListenerForAllRecords(workbookBuildingListener);

		final HSSFEventFactory factory = new HSSFEventFactory();
		try {
			factory.processWorkbookEvents(request, fs);
		} catch (IOException e) {
			throw new POIException(e);
		} finally {
			IoUtil.close(fs);
		}
	}

	private void addToRowCells(int row, int column, Object value) {
		while (column > this.rowCells.size()) {
			this.rowCells.add(null);
		}
		this.rowCells.add(column, value);
	}

}
