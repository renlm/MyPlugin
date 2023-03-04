package cn.renlm.plugins.MyExcel.reader;

import java.io.InputStream;

import cn.hutool.core.io.IoUtil;
import cn.renlm.plugins.MyExcel.config.MyWorkbook;
import cn.renlm.plugins.MyExcel.handler.DataReadHandler;

/**
 * 解析器
 * 
 * @author RenLiMing(任黎明)
 *
 */
public abstract class AbstractReader {

	final MyWorkbook myExcel;

	final InputStream in;

	public abstract AbstractReader read(String sheetName, DataReadHandler dataReadHandler);

	public AbstractReader(MyWorkbook myExcel, InputStream in) {
		this.myExcel = myExcel;
		this.in = IoUtil.toMarkSupportStream(in);
	}

}
