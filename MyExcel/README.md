## 简介
Excel、Csv表格读取，Xlsx表格导出。

## 安装
### Maven
在项目的pom.xml的dependencies中加入以下内容:

```xml
<dependency>
    <groupId>cn.renlm.plugins</groupId>
    <artifactId>MyExcel</artifactId>
    <version>1.0.1</version>
</dependency>
```

## 使用
### 配置Xml
可读取Xls、Xlsx、Csv三种表格，导出表格为Xlsx，支持大数据量表格读取导出且不会造成内存溢出，支持多级表头，别名配置，格式化读取与导出，多sheet页及标注与下拉选生成等。<br/>
详细规则可查看[MyExcel.xsd](http://www.renlm.cn/schema/MyExcel.xsd)，引入约束后可自动提示，简单易用，对代码无侵入。

```
<?xml version="1.0" encoding="UTF-8"?>
<excel name="Demo" csv-charset="GBK" ref="备注.xlsx"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:noNamespaceSchemaLocation="http://www.renlm.cn/schema/MyExcel.xsd">
	<sheet name="农村家庭情况调查表">
		<column field="year">
			<title>年度</title>
		</column>
		<column field="county">
			<title>县（区）</title>
		</column>
		<column field="town">
			<title>镇（乡）</title>
		</column>
		<column field="village">
			<title>行政村</title>
		</column>
		<column field="name">
			<title>户主姓名</title>
		</column>
		<column field="idCard" prefix="#">
			<title>户主身份证号码</title>
			<alias>身份证号码</alias>
			<annotation>添加前缀#，避免数字转化错误。</annotation>
		</column>
		<column field="familySize">
			<title>家庭人口数</title>
		</column>
		<column field="tuopinProperty">
			<title>脱贫属性</title>
		</column>
		<column field="isDangerousHouseholds">
			<title>是否危房户</title>
			<dict type="value" force-check="true">
				<item value="是" key="true"></item>
				<item value="否" key="false"></item>
			</dict>
		</column>
		<column field="perNetIncome" prefix="￥" suffix="元"
			number-format=",###.##">
			<title>人均纯收入</title>
		</column>
		<column field="annualIncome" prefix="￥" suffix="元"
			number-format=",###.##">
			<title>年收入</title>
		</column>
		<column field="recordDate" optional="true">
			<title>记录日期</title>
		</column>
		<column field="isDeleted" ignore="true">
			<title>删除标记</title>
		</column>
	</sheet>
	<sheet name="2018年财政教育经费投入情况调查表" freezes="2">
		<column field="province">
			<title>省（直辖市）</title>
		</column>
		<column field="schoolType">
			<title>学校类别</title>
			<annotation>如果下拉选中类别补全，请自行填写对应的学校类别</annotation>
			<dict type="value">
				<item value="小学" key="1"></item>
				<item value="初中" key="2"></item>
				<item value="高中" key="3"></item>
				<item value="大学" key="4"></item>
			</dict>
		</column>
		<column field="eduFinanceTotalAmount" number-format=",###.##">
			<title split="-">财政教育经费投入（万元）-总计</title>
		</column>
		<column field="eduFinanceJysyfTotalAmount"
			number-format=",###.##">
			<title split="-">财政教育经费投入（万元）-教育事业费-合计-金额</title>
		</column>
		<column field="eduFinanceJysyfTotalIncrease" suffix="%"
			number-format="#.##">
			<title split="-">财政教育经费投入（万元）-教育事业费-合计-比上年增长（%）</title>
		</column>
		<column field="eduFinanceJysyfRyjf">
			<title split="-">财政教育经费投入（万元）-教育事业费-人员经费</title>
		</column>
		<column field="eduFinanceJysyfRcgyjf">
			<title split="-">财政教育经费投入（万元）-教育事业费-日常公用经费</title>
		</column>
		<column field="eduFinanceJysyfXmjfTotal">
			<title split="-">财政教育经费投入（万元）-教育事业费-项目经费-合计</title>
		</column>
		<column field="eduFinanceJysyfXmjfOtherBzhjs">
			<title split="-">财政教育经费投入（万元）-教育事业费-项目经费-其中-标准化建设</title>
		</column>
		<column field="eduFinanceJysyfXmjfOtherXxhjs">
			<title split="-">财政教育经费投入（万元）-教育事业费-项目经费-其中-信息化建设</title>
		</column>
		<column field="eduFinanceJjbk">
			<title split="-">财政教育经费投入（万元）-基建拨款</title>
		</column>
		<column field="eduFinanceVillageTrHj">
			<title split="-">其他投入-村投入-合计</title>
		</column>
		<column field="otherVillageTrRyjf">
			<title split="-">其他投入-村投入-其中-人员经费</title>
		</column>
		<column field="otherVillageTrRcgyjf">
			<title split="-">其他投入-村投入-其中-日常公用经费</title>
		</column>
		<column field="otherVillageTrXmjf">
			<title split="-">其他投入-村投入-其中-项目经费</title>
		</column>
		<column field="otherVillageTrJjtr">
			<title split="-">其他投入-村投入-其中-基建投入</title>
		</column>
		<column field="otherShjkTotal">
			<title split="-">其他投入-社会捐款-合计</title>
		</column>
		<column field="otherShjkXmjf">
			<title split="-">其他投入-社会捐款-其中-项目经费</title>
		</column>
		<column field="otherShjkJjtr">
			<title split="-">其他投入-社会捐款-其中-基建投入</title>
		</column>
		<column field="zlbcXxhjsBntrAmount">
			<title split="-">补充资料-信息化建设-本年投入金额（万元）</title>
		</column>
		<column field="zlbcXxhjsComputerTotalNum">
			<title split="-">补充资料-信息化建设-电脑数（台）-合计</title>
		</column>
		<column field="zlbcXxhjsComputerBngzs">
			<title split="-">补充资料-信息化建设-电脑数（台）-本年购置数</title>
		</column>
		<column field="zlbcXxhjsXywTotalNum">
			<title split="-">补充资料-信息化建设-校园网数（个）-合计</title>
		</column>
		<column field="zlbcXxhjsXywBnjcs">
			<title split="-">补充资料-信息化建设-校园网数（个）-本年建成数</title>
		</column>
		<column field="remark" width256="32">
			<title>备注</title>
		</column>
	</sheet>
</excel>
```

### 生成模板

```
@Test
@SneakyThrows
public void template() {
	String path = FileUtil.getUserHomePath() + "/Desktop/Template.xlsx";
	FileUtil.del(path);
	OutputStream stream = new FileOutputStream(path);
	Workbook workbook = MyExcelUtil.createWorkbook("Demo.xml", true);
	workbook.write(stream);
}
```

### 综合案例，读取并导出

```
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
				MyExcelUtil.readBySax("Demo.xml", inCsv, sheetName, (data, checkResult) -> {
					sh1.write(CollUtil.newArrayList(data));
				});

				// 读取03格式数据，忽略模板和数据验证，写入导出表格
				InputStream inXls = FileUtil.getInputStream("测试数据.xls");
				MyExcelUtil.readBySax("Demo.xml", inXls, 0, sheetName, (data, checkResult) -> {
					sh1.write(CollUtil.newArrayList(data));
				});
			},
			// Demo.xml中第二个Sheet
			sh2 -> {
				String sheetName = sh2.getSheetName();

				// 读取07格式数据，判断模板和数据验证，写入导出表格
				InputStream inXlsx = FileUtil.getInputStream("测试数据.xlsx");
				MyExcelUtil.readBySax("Demo.xml", inXlsx, sheetName, (data, checkResult) -> {
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
```