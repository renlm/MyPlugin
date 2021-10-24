package cn.renlm.plugins.MyUtil;

import java.io.InputStream;

import com.thoughtworks.xstream.XStream;

import lombok.experimental.UtilityClass;

/**
 * Xml配置读取工具
 * 
 * @author Renlm
 *
 */
@UtilityClass
public final class MyXStreamUtil {

	/**
	 * 读取
	 * 
	 * @param type
	 * @param resource
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static final <C> C read(final Class<C> type, String resource) {
		InputStream in = MyXStreamUtil.class.getClassLoader().getResourceAsStream(resource);
		XStream xstream = create(type, in);
		return (C) xstream.fromXML(in);
	}

	/**
	 * 新建
	 * 
	 * @param type
	 * @param in
	 * @return
	 */
	private static final XStream create(final Class<?> type, InputStream in) {
		XStream xstream = new XStream();
		xstream.processAnnotations(type);
		xstream.allowTypeHierarchy(type);
		xstream.ignoreUnknownElements();
		return xstream;
	}
}