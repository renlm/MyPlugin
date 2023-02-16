package cn.renlm.plugins.MyExcel.config.column;

import java.io.Serializable;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.converters.extended.ToAttributedValueConverter;

import lombok.Data;

/**
 * 列配置-表头
 * 
 * @author RenLiMing(任黎明)
 *
 */
@Data
@XStreamConverter(value = ToAttributedValueConverter.class, strings = { "text" })
public class Title implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 多级表头分隔符
	 */
	@XStreamAsAttribute
	private String split;

	/**
	 * 表头名称（多级表头以分隔符拼接）
	 */
	private String text;

}