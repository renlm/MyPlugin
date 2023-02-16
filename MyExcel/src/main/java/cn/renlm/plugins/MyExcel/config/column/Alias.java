package cn.renlm.plugins.MyExcel.config.column;

import java.io.Serializable;

import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.converters.extended.ToAttributedValueConverter;

import lombok.Data;

/**
 * 列配置-表头别名
 * 
 * @author RenLiMing(任黎明)
 *
 */
@Data
@XStreamConverter(value = ToAttributedValueConverter.class, strings = { "text" })
public class Alias implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 别名名称（多级表头以表头分隔符拼接）
	 */
	private String text;

}