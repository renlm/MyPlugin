package cn.renlm.plugins.MyExcel.reader;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.text.csv.CsvUtil;
import cn.hutool.core.util.StrUtil;
import cn.renlm.plugins.MyExcel.config.MySheet;
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
		final List<List<String>> titles = new ArrayList<>();
		final List<String> keys = new ArrayList<>();

		final MySheet sheet = myExcel.getSheetByName(sheetName);
		Charset charset = StandardCharsets.UTF_8;
		if (StrUtil.isNotBlank(myExcel.getCsvCharset())) {
			charset = Charset.forName(myExcel.getCsvCharset());
		}

		cn.hutool.core.text.csv.CsvReader reader = CsvUtil.getReader();
		reader.read(IoUtil.getReader(in, charset), csvRow -> {
			Long rowIndex = csvRow.getOriginalLineNumber();
			List<Object> rowList = new ArrayList<>(csvRow.getRawList());
			super.processRow(myExcel, titles, keys, dataReadHandler, sheet, rowIndex, rowList);
		});
		return this;
	}

}
