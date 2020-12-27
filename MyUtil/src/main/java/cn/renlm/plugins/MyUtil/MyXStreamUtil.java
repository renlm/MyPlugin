/*
 * Copyright (c) [Year] [name of copyright holder]
 * [Software Name] is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * 	http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package cn.renlm.plugins.MyUtil;

import java.io.InputStream;

import com.thoughtworks.xstream.XStream;

import lombok.experimental.UtilityClass;

/**
 * Xml配置读取工具
 * 
 * @author renlm
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
		XStream.setupDefaultSecurity(xstream);
		xstream.processAnnotations(type);
		xstream.allowTypeHierarchy(type);
		xstream.ignoreUnknownElements();
		return xstream;
	}
}