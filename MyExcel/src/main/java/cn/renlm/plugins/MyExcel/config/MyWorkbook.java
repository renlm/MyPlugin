package cn.renlm.plugins.MyExcel.config;

import java.io.Serializable;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import cn.hutool.core.util.StrUtil;
import lombok.Data;

/**
 * 工作簿配置
 * 
 * @author RenLiMing(任黎明)
 *
 */
@Data
@XStreamAlias("excel")
public class MyWorkbook implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 表格名称
	 */
	@XStreamAsAttribute
	private String name;

	/**
	 * 引用模板
	 */
	@XStreamAsAttribute
	private String ref;

	/**
	 * Csv编码（处理Csv时使用）
	 */
	@XStreamAsAttribute
	@XStreamAlias("csv-charset")
	private String csvCharset;

	/**
	 * Sheet集
	 */
	@XStreamImplicit(itemFieldName = "sheet")
	private List<MySheet> sheets;

	/**
	 * 根据 sheetName 获取配置
	 * 
	 * @param sheetName
	 * @return
	 */
	public MySheet getSheetByName(String sheetName) {
		for (MySheet item : this.sheets) {
			if (StrUtil.equals(sheetName, item.getName())) {
				return item;
			}
		}
		return null;
	}

}
