## 简介
针对工作过程中的一些常见场景，总结一些方法和套路，减少代码量和重复工作，让代码越写越少。

## 包含组件
| 模块                |    介绍                                                                          |
| -------------------|---------------------------------------------------------------------------------- |
| MyBotPls           |     针对MyBatis-Plus和springBoot封装的一个由数据库自动生成代码的套路                  |
| MyExcel            |     使用xml完全配置化Excel、Csv表格的模板生成，数据读取，数据导出                     |

可以根据需求对每个模块单独引入，新项目可以直接引入`MyAll`。

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
