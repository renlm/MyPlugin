package cn.renlm.plugins.MyExcel.reader;

import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.renlm.plugins.MyExcel.config.MySheet;
import cn.renlm.plugins.MyExcel.config.MyWorkbook;
import cn.renlm.plugins.MyExcel.entity.CheckResult;
import cn.renlm.plugins.MyExcel.handler.DataReadHandler;

/**
 * 解析器
 * 
 * @author RenLiMing(任黎明)
 *
 */
public abstract class AbstractReader {

	/**
	 * 读取行数（key：页签名称，value：行数）
	 */
	final Map<String, Integer> read = new LinkedHashMap<>();

	/**
	 * 配置信息
	 */
	final MyWorkbook myExcel;

	/**
	 * 文件输入流
	 */
	final byte[] bytes;

	/**
	 * 读取页签（适用小文件，内存消耗较大）
	 * <p>
	 * 默认按配置名称获取数据，找不到取文件第一个
	 * </p>
	 * 
	 * @param sheetName
	 * @param dataReadHandler
	 * @return
	 */
	public abstract AbstractReader read(String sheetName, DataReadHandler dataReadHandler);

	/**
	 * 读取页签（适用大文件，Sax模式）
	 * <p>
	 * 默认按配置名称获取数据，找不到取文件第一个
	 * </p>
	 * 
	 * @param sheetName
	 * @param dataReadHandler
	 * @return
	 */
	public abstract AbstractReader readBySax(String sheetName, DataReadHandler dataReadHandler);

	/**
	 * 获取指定页签读取行数
	 * 
	 * @param sheetName
	 * @return
	 */
	public int getRead(String sheetName) {
		return ObjectUtil.defaultIfNull(read.get(sheetName), 0);
	}

	/**
	 * 构造函数
	 * 
	 * @param myExcel
	 * @param in
	 */
	public AbstractReader(MyWorkbook myExcel, InputStream in) {
		this.myExcel = myExcel;
		this.bytes = IoUtil.readBytes(in);
	}

	/**
	 * 行数据处理
	 * 
	 * @param myExcel
	 * @param titles
	 * @param keys
	 * @param dataReadHandler
	 * @param sheet
	 * @param rowIndex
	 * @param rowList
	 */
	final void processRow(MyWorkbook myExcel, List<List<String>> titles, List<String> keys,
			DataReadHandler dataReadHandler, MySheet sheet, long rowIndex, List<Object> rowList) {
		final int sheetLevel = sheet.level();
		final long level = rowIndex - sheet.getStart() + 1;
		this.read.put(sheet.getName(), this.getRead(sheet.getName()) + 1);
		if (level >= 1) {
			if (level <= sheetLevel) { // 标题行，建立[字段-值索引]映射
				titles.add(MySheet.fillTitle(rowList));
				if (level == sheetLevel) {
					keys.addAll(sheet.generateKeys(titles, dataReadHandler));
				}
			} else { // 数据行，取出映射数据
				Map<String, Object> data = CollUtil.zip(keys, rowList);
				data.remove(StrUtil.EMPTY);
				CheckResult checkResult = dataReadHandler.readConvert(sheet, rowIndex, data);
				dataReadHandler.handle(data, checkResult);
			}
		}
	}

}
