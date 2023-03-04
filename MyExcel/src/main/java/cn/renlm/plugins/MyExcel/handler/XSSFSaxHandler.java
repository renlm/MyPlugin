package cn.renlm.plugins.MyExcel.handler;

import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.usermodel.XSSFComment;

/**
 * Xlsx 解析
 * 
 * @author RenLiMing(任黎明)
 *
 */
public class XSSFSaxHandler implements XSSFSheetXMLHandler.SheetContentsHandler {

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
