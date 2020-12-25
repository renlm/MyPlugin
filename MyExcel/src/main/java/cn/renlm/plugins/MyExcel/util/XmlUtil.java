package cn.renlm.plugins.MyExcel.util;

import java.io.InputStream;

import com.thoughtworks.xstream.XStream;

import cn.hutool.core.io.resource.ResourceUtil;

/**
 * Xml配置转换
 * 
 * @author renlm
 *
 */
public class XmlUtil {

	/**
	 * 读取
	 * 
	 * @param type
	 * @param resource
	 * @return
	 */
	public static final <C> C read(final Class<C> type, String resource) {
		InputStream in = ResourceUtil.getStream(resource);
		return read(type, in);
	}

	/**
	 * 读取
	 * 
	 * @param type
	 * @param in
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static final <C> C read(final Class<C> type, InputStream in) {
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
		XStream.setupDefaultSecurity(xstream);
		xstream.processAnnotations(type);
		xstream.allowTypeHierarchy(type);
		xstream.ignoreUnknownElements();
		return xstream;
	}
}