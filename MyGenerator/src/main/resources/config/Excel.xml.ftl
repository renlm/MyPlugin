<?xml version="1.0" encoding="UTF-8"?>
<excel name="${package.Entity}.${entity}"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:noNamespaceSchemaLocation="https://www.renlm.cn/schema/MyExcel.xsd">
	<sheet name="${table.comment!}">
<#list table.fields as field>
<#if field.keyFlag><#--生成主键排在第一位-->
		<column field="${field.propertyName}">
			<title>${field.comment}</title>
		</column>
</#if>
</#list>
<#list table.commonFields as field><#--生成公共字段 -->
        <column field="${field.propertyName}">
			<title>${field.comment}</title>
		</column>
</#list>
<#list table.fields as field>
<#if !field.keyFlag><#--生成普通字段 -->
        <column field="${field.propertyName}">
			<title>${field.comment}</title>
		</column>
</#if>
</#list>
	</sheet>
</excel>