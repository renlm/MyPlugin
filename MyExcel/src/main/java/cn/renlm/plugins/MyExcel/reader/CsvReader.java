package cn.renlm.plugins.MyExcel.reader;

import java.io.InputStream;

import cn.renlm.plugins.MyExcel.config.MyWorkbook;
import cn.renlm.plugins.MyExcel.handler.DataReadHandler;

/**
 * Csv 解析
 * 
 * @author RenLiMing(任黎明)
 *
 */
public class CsvReader extends AbstractReader {

	public CsvReader(MyWorkbook myExcel, InputStream in) {
		super(myExcel, in);
	}

	@Override
	public AbstractReader read(String sheetName, DataReadHandler dataReadHandler) {
		return this;
	}

}
