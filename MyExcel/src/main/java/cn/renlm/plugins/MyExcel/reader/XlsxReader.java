package cn.renlm.plugins.MyExcel.reader;

import java.io.InputStream;

import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.usermodel.XSSFComment;

import cn.renlm.plugins.MyExcel.config.MyWorkbook;
import cn.renlm.plugins.MyExcel.handler.DataReadHandler;
import lombok.SneakyThrows;

/**
 * Xlsx 解析
 * 
 * @author RenLiMing(任黎明)
 *
 */
public class XlsxReader extends AbstractReader implements XSSFSheetXMLHandler.SheetContentsHandler {

	public XlsxReader(MyWorkbook myExcel, InputStream in) {
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
	public void startRow(int rowNum) {

	}

	@Override
	public void endRow(int rowNum) {

	}

	@Override
	public void cell(String cellReference, String formattedValue, XSSFComment comment) {

	}

}
