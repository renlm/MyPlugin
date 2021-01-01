## 简介
代码生成封装类
```
	spring-boot
	mybatis-plus-boot-starter
	mybatis-plus-generator
	dynamic-datasource-spring-boot-starter
	freemarker
```

## 安装
### Maven
在项目的pom.xml的dependencies中加入以下内容:

```xml
<dependency>
    <groupId>cn.renlm.plugins</groupId>
    <artifactId>MyGenerator</artifactId>
    <version>1.0.1</version>
</dependency>
```

## 使用
### 配置Xml
针对SpringBoot、Mybatis-Plus及多数据源的一套代码生成。<br/>
详细规则可查看[MyGenerator.xsd](http://www.renlm.cn/schema/MyGenerator.xsd)，引入约束后可自动提示，简单易用，对代码无侵入。

```
<?xml version="1.0" encoding="UTF-8"?>
<generator dsName="pg"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:noNamespaceSchemaLocation="http://www.renlm.cn/schema/MyGenerator.xsd">
	<url>
		jdbc:mysql://renlm.cn:3306/testdb?serverTimezone=Asia/Shanghai
	</url>
	<username>username</username>
	<password>password</password>
	<driverName>com.mysql.cj.jdbc.Driver</driverName>

	<module name="sys" package="cn.renlm">
		<table author="Renlm" name="sys_user" cover-entity="true"/>
	</module>

	<module name="log" package="cn.renlm">
		<table author="Renlm" name="log_oshi" />
	</module>
</generator>
```

### 案例

```
@Test
public void run() {
	MyGeneratorUtil.run("Generator.MySQL.xml");
}
```