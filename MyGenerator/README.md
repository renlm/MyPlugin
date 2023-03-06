## 简介
针对SpringBoot、Mybatis-Plus及多数据源的一套代码生成
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
    <version>2.7.8</version>
</dependency>
```

## 使用
### 配置Xml
详细规则可查看[MyGenerator.xsd](https://renlm.gitee.io/schemas/MyGenerator.xsd)，引入约束后可自动提示，简单易用。

```
<?xml version="1.0" encoding="UTF-8"?>
<generator dsName="pg"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:noNamespaceSchemaLocation="https://renlm.gitee.io/schemas/MyGenerator.xsd">

	<url>jdbc:postgresql://localhost:5432/db</url>
	<username>username</username>
	<password>password</password>

	<module name="sys" package="cn.renlm.crawler">
		<table schema="public" author="Renlm" name="sys_const" entity="true" excel="true" />
		<table schema="public" author="Renlm" name="sys_file" idType="ASSIGN_ID" />
	</module>
</generator>
```

### 案例

```
@Test
public void run() {
	MyGeneratorUtil.run("MyGenerator.xml");
}
```