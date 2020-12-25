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

	@Test
	@SneakyThrows
	public void test() {
		String config = "MyExcelTest.xml";
		String path = "C:/Users/renlm-a/Desktop/导出测试.xlsx";
		FileUtil.del(path);
		OutputStream stream = new FileOutputStream(path);

		// 合并三个表格到指定模板
		Workbook workbook = MyExcelUtil.createWorkbook(config, false, sheet1 -> {
			String sheetName = sheet1.getSheetName();
			InputStream in = FileUtil.getInputStream("MyExcelTest/1.扶贫对象-贫困户.xls");
			MyExcelUtil.readBySax(config, in, sheetName, (data, checkResult) -> {
				if (checkResult.isError()) {
					if (checkResult.isProcess()) {
						Console.log(checkResult);
					} else { // 模板表头校验失败
						Console.error(checkResult);
					}
				} else {
					sheet1.write(CollUtil.newArrayList(data));
				}
			});
		}, sheet2 -> {
			String sheetName = sheet2.getSheetName();
			InputStream in = FileUtil.getInputStream("MyExcelTest/2.帮扶措施-受益项目列表.csv");
			MyExcelUtil.readBySax(config, in, sheetName, (data, checkResult) -> {
				if (checkResult.isError()) {
					if (checkResult.isProcess()) {
						Console.log(checkResult);
					} else { // 模板表头校验失败
						Console.error(checkResult);
					}
				} else {
					sheet2.write(CollUtil.newArrayList(data));
				}
			});
		}, sheet3 -> {
			String sheetName = sheet3.getSheetName();
			InputStream in = FileUtil.getInputStream("MyExcelTest/3.农业局-种养殖.xlsx");
			MyExcelUtil.readBySax(config, in, sheetName, (data, checkResult) -> {
				if (checkResult.isError()) {
					if (checkResult.isProcess()) {
						Console.log(checkResult);
					} else { // 模板表头校验失败
						Console.error(checkResult);
					}
				} else {
					sheet3.write(CollUtil.newArrayList(data));
				}
			});
		});
		workbook.write(stream);
	}
}