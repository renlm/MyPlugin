package cn.renlm.plugins.MyExcel.reader;

import java.io.InputStream;

import org.apache.poi.hssf.eventusermodel.HSSFListener;
import org.apache.poi.hssf.record.Record;

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

	public XlsReader(MyWorkbook myExcel, InputStream in) {
		super(myExcel, in);
	}

	@Override
	@SneakyThrows
	public AbstractReader read(String sheetName, DataReadHandler dataReadHandler) {
		if (in.markSupported()) {
			in.reset();
		}

		return this;
	}

	@Override
	public void processRecord(Record record) {

	}

}
