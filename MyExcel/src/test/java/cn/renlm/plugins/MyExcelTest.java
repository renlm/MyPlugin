package cn.renlm.plugins;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Test;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Console;
import lombok.SneakyThrows;

/**
 * 样例测试
 * 
 * @author Renlm
 *
 */
public class MyExcelTest {

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
		Workbook workbook = MyExcelUtil.createWorkbook("Demo.xml", false,
				// Demo.xml中第一个Sheet
				sh1 -> {
					String sheetName = sh1.getSheetName();

					// 读取Csv格式数据，忽略模板和数据验证，写入导出表格
					InputStream inCsv = FileUtil.getInputStream("测试数据.csv");
					MyExcelUtil.readBySax("Demo.xml", inCsv, 0, sheetName, (data, checkResult) -> {
						sh1.write(data);
					});

					// 读取03格式数据（指定文件中的第一个Sheet），忽略模板和数据验证，写入导出表格
					InputStream inXls = FileUtil.getInputStream("测试数据.xls");
					MyExcelUtil.readBySax("Demo.xml", inXls, 0, sheetName, (data, checkResult) -> {
						sh1.write(data);
					});
				},
				// Demo.xml中第二个Sheet
				sh2 -> {
					String sheetName = sh2.getSheetName();

					// 读取07格式数据，判断模板和数据验证，写入导出表格
					InputStream inXlsx = FileUtil.getInputStream("测试数据.xlsx");
					MyExcelUtil.readBySax("Demo.xml", inXlsx, 0, sheetName, (data, checkResult) -> {
						if (checkResult.isError()) { // 出错了
							if (checkResult.isProcess()) { // 表头已处理完，进入行数据读取流程中
								Console.log(checkResult);
							} else { // 模板表头校验失败
								Console.error(checkResult);
							}
						} else {
							sh2.write(data);
						}
					});
				});
		workbook.write(stream);
	}
}