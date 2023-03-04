package cn.renlm.plugins.MyExcel.reader;

import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cn.hutool.core.io.IoUtil;
import cn.renlm.plugins.MyExcel.config.MyWorkbook;
import cn.renlm.plugins.MyExcel.handler.DataReadHandler;
import lombok.Getter;

/**
 * 解析器
 * 
 * @author RenLiMing(任黎明)
 *
 */
public abstract class AbstractReader {

	@Getter
	final Map<String, List<String>> sheetErrors = new LinkedHashMap<>();

	final MyWorkbook myExcel;

	final InputStream in;

	public abstract AbstractReader read(String sheetName, DataReadHandler dataReadHandler);

	public AbstractReader(MyWorkbook myExcel, InputStream in) {
		this.myExcel = myExcel;
		this.in = IoUtil.toMarkSupportStream(in);
	}

}
