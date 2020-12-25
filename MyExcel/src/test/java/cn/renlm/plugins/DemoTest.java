package cn.renlm.plugins;

import java.io.FileOutputStream;
import java.io.OutputStream;

import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Test;

import cn.hutool.core.io.FileUtil;
import lombok.SneakyThrows;

/**
 * 样例测试
 * 
 * @author renlm
 *
 */
public class DemoTest {

	/**
	 * 生成模板
	 */
	@Test
	@SneakyThrows
	public void createTemplate() {
		String path = FileUtil.getUserHomePath() + "/Desktop/Demo.xlsx";
		FileUtil.del(path);
		OutputStream stream = new FileOutputStream(path);
		Workbook workbook = MyExcelUtil.createWorkbook("Demo.xml", true);
		workbook.write(stream);
	}
}