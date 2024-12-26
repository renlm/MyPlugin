package cn.renlm.plugins.MyUtil;

import java.io.InputStream;

import com.thoughtworks.xstream.XStream;

import cn.hutool.core.io.FileUtil;
import lombok.experimental.UtilityClass;

/**
 * Xml配置读取工具
 * 
 * @author RenLiMing(任黎明)
 *
 */
@UtilityClass
public final class MyXStreamUtil {
	
	/**
	 * 读取
	 * 
	 * @param <C>
	 * @param type
	 * @param filePath
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static final <C> C readFromFile(final Class<C> type, String filePath) {
		InputStream in = FileUtil.getInputStream(filePath);
		XStream xstream = create(type);
		return (C) xstream.fromXML(in);
	}

	/**
	 * 读取
	 * 
	 * @param <C>
	 * @param type
	 * @param resource
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static final <C> C read(final Class<C> type, String resource) {
		InputStream in = MyXStreamUtil.class.getClassLoader().getResourceAsStream(resource);
		XStream xstream = create(type);
		return (C) xstream.fromXML(in);
	}

	/**
	 * 新建
	 * 
	 * @param type
	 * @return
	 */
	public static final XStream create(final Class<?> type) {
		XStream xstream = new XStream();
		xstream.processAnnotations(type);
		xstream.allowTypeHierarchy(type);
		xstream.ignoreUnknownElements();
		return xstream;
	}

}
