## 简介
针对SpringBoot、Mybatis-Plus及多数据源的一套代码生成
```
	spring-boot 
		3.2.4
	mybatis-plus-boot-starter 
		3.5.6
	dynamic-datasource-spring-boot-starter 
		4.3.0
	mybatis-plus-generator
		3.5.6
	freemarker
		2.3.32
```

## 安装
### Maven
在项目的pom.xml的dependencies中加入以下内容:

```xml
<dependency>
    <groupId>cn.renlm.plugins</groupId>
    <artifactId>MyGenerator</artifactId>
    <version>2.8.4</version>
	<scope>test</scope>
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

	<module name="sys" package="cn.renlm.example">
		<table author="RenLiMing(任黎明)" name="sys_const" entity="true" excel="true" />
		<table author="RenLiMing(任黎明)" name="sys_file" idType="ASSIGN_ID">
			<column name="file_content">
				<typeHandler>org.apache.ibatis.type.BlobTypeHandler</typeHandler>
				<javaSqlType type="byte[]" />
			</column>
		</table>
	</module>

	<url>jdbc:postgresql://postgres:5432/db</url>
	<schema>public</schema>
	<username>username</username>
	<password>password</password>

	<config springdoc="false" swagger="false">
		<typeConvert>
			<javaSqlType name="BIGINT" type="BigInteger" pkg="java.math.BigInteger" />
			<javaSqlType name="TINYINT" type="Boolean" />
			<javaSqlType name="DOUBLE" type="Double" />
		</typeConvert>
		<package>
			<controller>controller</controller>
			<serviceImpl>service.impl</serviceImpl>
			<service>service</service>
			<entity>entity</entity>
			<mapper>mapper</mapper>
			<xml>mapper</xml>
		</package>
		<strategy>
			<entity>
				<formatFileName>{entityName}</formatFileName>
				<disableSerialVersionUID>false</disableSerialVersionUID>
				<tableFieldAnnotationEnable>true</tableFieldAnnotationEnable>
				<versionColumnName>version</versionColumnName>
				<logicDeleteColumnName>deleted</logicDeleteColumnName>
				<superClass>cn.renlm.plugins.Common.BaseModel</superClass>
				<superEntityColumns>
					<column>created_at</column>
					<column>updated_at</column>
					<column>deleted</column>
				</superEntityColumns>
			</entity>
			<controller>
				<controller>false</controller>
				<enableRestStyle>false</enableRestStyle>
				<formatFileName>{entityName}Controller</formatFileName>
			</controller>
			<service>
				<formatServiceFileName>I{entityName}Service</formatServiceFileName>
				<formatServiceImplFileName>{entityName}ServiceImpl</formatServiceImplFileName>
			</service>
			<mapper>
				<formatMapperFileName>{entityName}Mapper</formatMapperFileName>
				<formatXmlFileName>{entityName}Mapper</formatXmlFileName>
			</mapper>
		</strategy>
	</config>

</generator>
```

### 案例

```
@Test
public void run() {
	MyGeneratorUtil.run("MyGenerator.xml");
}
```