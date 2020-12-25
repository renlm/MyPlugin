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
	 * 模板
	 */
	@Test
	@SneakyThrows
	public void template() {
		String path = FileUtil.getUserHomePath() + "/Desktop/Template.xlsx";
		FileUtil.del(path);
		OutputStream stream = new FileOutputStream(path);
		Workbook workbook = MyExcelUtil.createWorkbook("Demo.xml", true);
		workbook.write(stream);
	}

	/**
	 * 导出
	 */
	@Test
	@SneakyThrows
	public void export() {
		String path = FileUtil.getUserHomePath() + "/Desktop/Export.xlsx";
		FileUtil.del(path);
		OutputStream stream = new FileOutputStream(path);
		Workbook workbook = MyExcelUtil.createWorkbook("Demo.xml", false, sh1 -> {

		}, sh2 -> {

		});
		workbook.write(stream);
	}
}