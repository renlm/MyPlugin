package cn.renlm.plugins.MyExcel.entity;

import org.apache.poi.ss.usermodel.CellStyle;

import cn.renlm.plugins.MyExcel.config.MyColumn;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 单元格封装
 * 
 * @author Renlm
 * 
 */
@Data
@Accessors(chain = true)
public class CellUnit {

	/**
	 * 行号（起始行：0）
	 */
	private int rowIndex;

	/**
	 * 列号（起始列：0）
	 */
	private int colIndex;

	/**
	 * 文本值
	 */
	private String text;

	/**
	 * 样式
	 */
	private CellStyle cellStyle;

	/**
	 * 列配置
	 */
	private MyColumn column;

}