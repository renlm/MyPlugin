package cn.renlm.plugins.MyExcel.reader;

import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.hssf.eventusermodel.EventWorkbookBuilder.SheetRecordCollectingListener;
import org.apache.poi.hssf.eventusermodel.FormatTrackingHSSFListener;
import org.apache.poi.hssf.eventusermodel.HSSFEventFactory;
import org.apache.poi.hssf.eventusermodel.HSSFListener;
import org.apache.poi.hssf.eventusermodel.HSSFRequest;
import org.apache.poi.hssf.eventusermodel.MissingRecordAwareHSSFListener;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import cn.hutool.core.io.IoUtil;
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

	}

}
