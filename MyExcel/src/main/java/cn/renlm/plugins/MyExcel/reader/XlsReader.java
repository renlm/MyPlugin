package cn.renlm.plugins.MyExcel.reader;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.eventusermodel.EventWorkbookBuilder.SheetRecordCollectingListener;
import org.apache.poi.hssf.eventusermodel.FormatTrackingHSSFListener;
import org.apache.poi.hssf.eventusermodel.HSSFEventFactory;
import org.apache.poi.hssf.eventusermodel.HSSFListener;
import org.apache.poi.hssf.eventusermodel.HSSFRequest;
import org.apache.poi.hssf.eventusermodel.MissingRecordAwareHSSFListener;
import org.apache.poi.hssf.model.HSSFFormulaParser;
import org.apache.poi.hssf.record.BOFRecord;
import org.apache.poi.hssf.record.BlankRecord;
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

import cn.hutool.core.io.IoUtil;
import cn.hutool.poi.excel.sax.ExcelSaxUtil;
import cn.hutool.poi.exceptions.POIException;
import cn.renlm.plugins.MyExcel.config.MyWorkbook;
import cn.renlm.plugins.MyExcel.handler.DataReadHandler;
import lombok.SneakyThrows;

/**
 * Xls 解析
 * 
 * @author RenLiMing(任黎明)
 *
 */
public class XlsReader extends AbstractReader implements HSSFListener {

	private FormatTrackingHSSFListener formatListener;

	private SheetRecordCollectingListener workbookBuildingListener;

	private SSTRecord sstRecord;

	private HSSFWorkbook stubWorkbook;

	private final List<BoundSheetRecord> boundSheetRecords = new ArrayList<>();

	private Integer sheetIndex = -1;

	private List<Object> rowCells = new ArrayList<>();

	public XlsReader(MyWorkbook myExcel, InputStream in) {
		super(myExcel, in);
	}

	@Override
	@SneakyThrows
	public AbstractReader read(String sheetName, DataReadHandler dataReadHandler) {
		if (in.markSupported()) {
			in.reset();
		}

		try (POIFSFileSystem fs = new POIFSFileSystem(in)) {
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
		} catch (IOException e) {
			throw new POIException(e);
		}

		return this;
	}

	@Override
	public void processRecord(Record record) {
		Object value = null;
		switch (record.getSid()) {
		case BoundSheetRecord.sid:
			boundSheetRecords.add((BoundSheetRecord) record);
			break;
		case BOFRecord.sid:
			BOFRecord bofRecord = (BOFRecord) record;
			if (bofRecord.getType() == BOFRecord.TYPE_WORKSHEET) {
				sheetIndex++;
				if (workbookBuildingListener != null && stubWorkbook == null) {
					stubWorkbook = workbookBuildingListener.getStubHSSFWorkbook();
				}
			}
			break;
		case SSTRecord.sid:
			sstRecord = (SSTRecord) record;
			break;
		case BlankRecord.sid:
			BlankRecord brec = (BlankRecord) record;
			this.addToRowCells(brec.getRow(), brec.getColumn(), null);
			break;
		case BoolErrRecord.sid:
			BoolErrRecord berec = (BoolErrRecord) record;
			value = berec.getBooleanValue();
			this.addToRowCells(berec.getRow(), berec.getColumn(), value);
			break;
		case FormulaRecord.sid:
			FormulaRecord formulaRec = (FormulaRecord) record;
			value = HSSFFormulaParser.toFormulaString(stubWorkbook, formulaRec.getParsedExpression());
			this.addToRowCells(formulaRec.getRow(), formulaRec.getColumn(), value);
			break;
		case LabelRecord.sid:
			LabelRecord lrec = (LabelRecord) record;
			this.addToRowCells(lrec.getRow(), lrec.getColumn(), lrec.getValue());
			break;
		case LabelSSTRecord.sid:
			LabelSSTRecord lsrec = (LabelSSTRecord) record;
			value = sstRecord == null ? null : sstRecord.getString(lsrec.getSSTIndex()).toString();
			this.addToRowCells(lsrec.getRow(), lsrec.getColumn(), value);
			break;
		case NumberRecord.sid:
			NumberRecord numrec = (NumberRecord) record;
			value = ExcelSaxUtil.getNumberOrDateValue(numrec, numrec.getValue(), this.formatListener);
			this.addToRowCells(numrec.getRow(), numrec.getColumn(), value);
			break;
		default:
			break;
		}
	}

	private void addToRowCells(int row, int column, Object value) {
		while (column > this.rowCells.size()) {
			this.rowCells.add(null);
		}
		this.rowCells.add(column, value);
	}

}
