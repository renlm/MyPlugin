package cn.renlm.plugins;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Test;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Console;
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
		Workbook workbook = MyExcelUtil.createWorkbook("Demo.xml", false,
				// Demo.xml中第一个Sheet
				sh1 -> {
					String sheetName = sh1.getSheetName();
					// 读取数据并写入导出表格
					InputStream in = FileUtil.getInputStream("测试数据.xlsx");
					MyExcelUtil.readBySax("Demo.xml", in, sheetName, (data, checkResult) -> {
						if (checkResult.isError()) { // 出错了
							if (checkResult.isProcess()) { // 表头已处理完，进入行数据读取流程中
								Console.log(checkResult);
							} else { // 模板表头校验失败
								Console.error(checkResult);
							}
						} else {
							sh1.write(CollUtil.newArrayList(data));
						}
					});
				},
				// Demo.xml中第二个Sheet
				sh2 -> {
					String sheetName = sh2.getSheetName();
					// 读取数据并写入导出表格
					InputStream in = FileUtil.getInputStream("测试数据.xlsx");
					MyExcelUtil.readBySax("Demo.xml", in, sheetName, (data, checkResult) -> {
						if (checkResult.isError()) { // 出错了
							if (checkResult.isProcess()) { // 表头已处理完，进入行数据读取流程中
								Console.log(checkResult);
							} else { // 模板表头校验失败
								Console.error(checkResult);
							}
						} else {
							sh2.write(CollUtil.newArrayList(data));
						}
					});
				});
		workbook.write(stream);
	}
}