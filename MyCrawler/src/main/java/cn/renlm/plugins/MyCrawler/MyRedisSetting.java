package cn.renlm.plugins.MyCrawler;

import cn.hutool.setting.Setting;
import redis.clients.jedis.Protocol;

/**
 * 缓存配置
 * 
 * @author Renlm
 *
 */
public class MyRedisSetting extends Setting {

	private static final long serialVersionUID = 1L;

	private static final String DefaultClientName = "MyCrawler";

	public static final String ConfigHost = "host";

	public static final String ConfigPort = "port";

	public static final String ConfigTimeout = "timeout";

	public static final String ConfigPassword = "password";

	public static final String ConfigDatabase = "database";

	public static final String ConfigClientName = "clientName";

	public MyRedisSetting(String path, boolean isUseVariable) {
		super(path, isUseVariable);
	}

	public String getHost(String group) {
		return this.getStr(ConfigHost, group, Protocol.DEFAULT_HOST);
	}

	public int getPort(String group) {
		return this.getInt(ConfigPort, group, Protocol.DEFAULT_PORT);
	}

	public int getTimeout(String group) {
		return this.getInt(ConfigTimeout, group, Protocol.DEFAULT_TIMEOUT);
	}

	public String getPassword(String group) {
		return this.getStr(ConfigPassword, group, null);
	}

	public int getDatabase(String group) {
		return this.getInt(ConfigDatabase, group, Protocol.DEFAULT_DATABASE);
	}

	public String getClientName(String group) {
		return this.getStr(ConfigClientName, group, DefaultClientName);
	}
}