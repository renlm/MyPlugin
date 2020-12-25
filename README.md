## 简介
针对工作过程中的某些特定场景，总结一些小工具，减少重复工作。

## 包含组件
| 模块                |    介绍                                                                          |
| -------------------|---------------------------------------------------------------------------------- |
| MyDb               |     纯Jdbc的数据库相关组件，目前主要用于代码生成                                      |
| MyExcel            |     使用xml完全配置化Excel、Csv表格的模板生成，数据读取，数据导出                     |

可以根据需求对每个模块单独引入，也可以通过引入`MyAll`方式引入所有模块。

## 安装

### Maven
在项目的pom.xml的dependencies中加入以下内容:

```xml
<dependency>
    <groupId>cn.renlm.plugins</groupId>
    <artifactId>MyAll</artifactId>
    <version>1.0.0</version>
</dependency>
```
