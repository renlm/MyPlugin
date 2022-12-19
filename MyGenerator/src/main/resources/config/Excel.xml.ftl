<?xml version="1.0" encoding="UTF-8"?>
<excel name="${package.Entity}.${entity}"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:noNamespaceSchemaLocation="https://renlm.cn/schemas/MyExcel.xsd">
<#if table.comment?default("")?trim?length gt 0>
	<sheet name="${table.comment}">
<#else>
	<sheet name="${table.name}">
</#if>
<#list table.fields as field>
<#if field.keyFlag><#--生成主键排在第一位-->
		<column field="${field.propertyName}">
			<title><#if field.comment?default("")?trim?length gt 0>${field.comment}<#else>${field.propertyName}</#if></title>
		</column>
</#if>
</#list>
<#list table.commonFields as field><#--生成公共字段 -->
        <column field="${field.propertyName}">
			<title><#if field.comment?default("")?trim?length gt 0>${field.comment}<#else>${field.propertyName}</#if></title>
		</column>
</#list>
<#list table.fields as field>
<#if !field.keyFlag><#--生成普通字段 -->
        <column field="${field.propertyName}">
			<title><#if field.comment?default("")?trim?length gt 0>${field.comment}<#else>${field.propertyName}</#if></title>
		</column>
</#if>
</#list>
	</sheet>
</excel>