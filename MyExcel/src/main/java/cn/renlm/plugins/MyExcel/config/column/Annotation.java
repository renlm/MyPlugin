package cn.renlm.plugins.MyExcel.config.column;

import java.io.Serializable;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.converters.extended.ToAttributedValueConverter;

import lombok.Data;

/**
 * 列配置-标注
 * 
 * @author Renlm
 *
 */
@Data
@XStreamAlias("annotation")
@XStreamConverter(value = ToAttributedValueConverter.class, strings = { "text" })
public class Annotation implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 标注内容
	 */
	private String text;

}