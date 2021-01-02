package cn.renlm.plugins.MyUtil;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

import lombok.experimental.UtilityClass;

/**
 * 主机工具类
 * 
 * @author Renlm
 *
 */
@UtilityClass
public class MyHostUtil {

	/**
	 * 机器IP
	 * 
	 * @return
	 */
	public static final String ip() {
		String sIP = "";
		InetAddress ip = null;
		try {
			boolean bFindIP = false;
			Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();
			while (netInterfaces.hasMoreElements()) {
				if (bFindIP)
					break;
				NetworkInterface ni = netInterfaces.nextElement();
				Enumeration<InetAddress> ips = ni.getInetAddresses();
				while (ips.hasMoreElements()) {
					ip = ips.nextElement();
					if (!ip.isLoopbackAddress() && ip.getHostAddress().matches("(\\d{1,3}\\.){3}\\d{1,3}")) {
						bFindIP = true;
						break;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (null != ip)
			sIP = ip.getHostAddress();
		return sIP;
	}
}