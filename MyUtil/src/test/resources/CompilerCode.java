package crawl.script;

import cn.hutool.core.lang.Console;
import cn.hutool.core.util.ReUtil;

/**
 * 编译代码
 * 
 * @author RenLiMing(任黎明)
 *
 */
public class CompilerCode implements Runnable {

	public void run() {
		Console.log(ReUtil.count("(登录)", "登录系统，新建工单，提交审批，退出登录"));
	}
}