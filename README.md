## 简介
让代码越写越少，积累工具，起步。

## 包含组件
| module             |    introduce
| -------------------|----------------------------------------------------------------------------------
| MyCrawler          |     爬虫工具封装
| MyExcel            |     使用xml完全配置化Excel、Csv表格的模板生成，数据读取，数据导出
| MyGenerator        |     MyBatis-Plus代码生成
| MyUtil             |     工具集

可以根据需求对每个模块单独引入，也可以通过引入`My-Plugin`方式引入所有模块。

## 安装
### Maven
在项目的pom.xml的dependencies中加入以下内容:

```xml
<dependency>
    <groupId>cn.renlm.plugins</groupId>
    <artifactId>My-Plugin</artifactId>
    <version>1.9.13</version>
</dependency>
```