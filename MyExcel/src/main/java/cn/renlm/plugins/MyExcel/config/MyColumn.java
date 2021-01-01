package cn.renlm.plugins.MyExcel.config;

import java.io.Serializable;
import java.util.List;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Workbook;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.converters.SingleValueConverter;

import cn.hutool.core.util.StrUtil;
import cn.renlm.plugins.MyExcel.config.column.Alias;
import cn.renlm.plugins.MyExcel.config.column.Annotation;
import cn.renlm.plugins.MyExcel.config.column.Dict;
import cn.renlm.plugins.MyExcel.config.column.Title;
import cn.renlm.plugins.MyExcel.util.ConstVal;
import cn.renlm.plugins.MyExcel.util.StyleUtil;
import lombok.Data;

/**
 * 列配置
 * 
 * @author Renlm
 *
 */
@Data
public class MyColumn implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 表头（必须）
	 */
	@XStreamAlias("title")
	private Title title;

	/**
	 * 表头别名（可选）
	 */
	@XStreamImplicit(itemFieldName = "alias")
	private List<Alias> aliasList;

	/**
	 * 标注（可选，显示在表头的最末级单元格上）
	 */
	@XStreamAlias("annotation")
	private Annotation annotation;

	/**
	 * 字典（类型为编码时，配置字段取枚举值；类型为枚举值时，配置字段保持原值并将转换后的编码存在转换值存储字段中）
	 */
	@XStreamAlias("dict")
	private Dict dict;

	/**
	 * 数据字段名（必须）
	 */
	@XStreamAsAttribute
	private String field;

	/**
	 * 前缀（可选，读取时去除前缀，导出时添加前缀）
	 */
	@XStreamAsAttribute
	private String prefix;

	/**
	 * 后缀（可选，读取时去除后缀，导出时添加后缀）
	 */
	@XStreamAsAttribute
	private String suffix;

	/**
	 * 数字格式（可选，默认#）
	 */
	@XStreamAsAttribute
	@XStreamAlias("number-format")
	private String numberFormat;

	/**
	 * 日期格式（可选，默认yyyy/MM/dd）
	 */
	@XStreamAsAttribute
	@XStreamAlias("date-format")
	private String dateFormat;

	/**
	 * 不允许为空（可选，默认否）
	 */
	@XStreamAsAttribute
	@XStreamAlias("not-null")
	private boolean notNull = false;

	/**
	 * 是否忽略（可选，默认否，忽略时导入导出均不处理）
	 */
	@XStreamAsAttribute
	private boolean ignore = false;

	/**
	 * 是否可选列（可选，默认否，可选列不出现在模板但导出）
	 */
	@XStreamAsAttribute
	private boolean optional = false;

	/**
	 * 列宽（可选，256一个单位，默认最小8*256）
	 */
	@XStreamAsAttribute
	private int width256;

	/**
	 * 列对齐方式（可选，默认左对齐，数字及日期右对齐）
	 */
	@XStreamAsAttribute
	@XStreamConverter(HorizontalAlignmentConverter.class)
	private HorizontalAlignment align;

	public static final class HorizontalAlignmentConverter implements SingleValueConverter {

		@Override
		@SuppressWarnings("rawtypes")
		public boolean canConvert(Class type) {
			return type == HorizontalAlignment.class;
		}

		@Override
		public String toString(Object obj) {
			HorizontalAlignment align = (HorizontalAlignment) obj;
			return align.name().toLowerCase();
		}

		@Override
		public Object fromString(String str) {
			for (HorizontalAlignment align : HorizontalAlignment.values()) {
				if (align.name().toLowerCase().equals(str))
					return align;
			}
			return null;
		}
	}

	/**
	 * 数字及日期右对齐
	 * 
	 * @return
	 */
	public HorizontalAlignment getAlign() {
		if (StrUtil.isNotBlank(this.numberFormat) || StrUtil.isNotBlank(this.getDateFormat())) {
			this.align = HorizontalAlignment.RIGHT;
		}
		return align;
	}

	/**
	 * 数据样式（复用型通用样式）
	 */
	private CellStyle cellStyle;

	/**
	 * 数据样式（复用型通用样式）
	 * 
	 * @param wb
	 * @return
	 */
	public CellStyle getCellStyle(Workbook wb) {
		if (this.cellStyle == null) {
			this.cellStyle = StyleUtil.createCellStyleWithBorder(wb, ConstVal.FONT, ConstVal.FONT_SIZE, false,
					this.getAlign());
		}
		return this.cellStyle;
	}
}