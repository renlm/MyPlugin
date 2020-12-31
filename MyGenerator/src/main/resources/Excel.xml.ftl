<?xml version="1.0" encoding="UTF-8"?>
<excel name="${comment}" 
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:noNamespaceSchemaLocation=".excel.xsd">
    <sheet name="${comment}">
    <#list fields as field><#if !field.isPkey><#if field_index?if_exists gt 1>
</#if>        <column field="${field.poFieldName}">
			<title split="-">${field.comment}</title>
		</column>
    </#if></#list>
    </sheet>
</excel>