package ${data.packageName}.service;

import ${data.packageName}.bean.${data.javaObjectName}Bean;
import com.sunwayworld.framework.support.base.service.GenericService;
<#if data.subTableTemplateAttrList?? && (data.subTableTemplateAttrList?size > 0) >
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.data.page.Page;
<#list data.subTableTemplateAttrList as subTableTemplateAttr>
import ${data.packageName}.bean.${subTableTemplateAttr.javaObjectName}Bean;
</#list>
</#if>
<#if data.funcList ?? && data.funcList?seq_contains("audit") > 
import com.sunwayworld.framework.support.auditable.service.GenericAuditableService;
</#if>
<#if data.funcList ?? && data.funcList?seq_contains("choose") > 
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;
</#if>
<#function getFieldType fieldType>
<#if fieldType=="varchar"><#return "String"><#elseif fieldType=="date"><#return "LocalDateTime"><#else><#return "Long"></#if>
</#function>

public interface ${data.javaObjectName}Service extends GenericService<${data.javaObjectName}Bean, ${getFieldType(data.idFieldType)}><#if data.funcList ?? && data.funcList?seq_contains("audit") >, GenericAuditableService<${data.javaObjectName}Bean, ${getFieldType(data.idFieldType)}></#if><#if data.funcList ?? && data.funcList?seq_contains("choose") >, GenericChoosableService<${data.javaObjectName}Bean, ${getFieldType(data.idFieldType)}></#if> {

<#if data.subTableTemplateAttrList?? && (data.subTableTemplateAttrList?size > 0) >
<#list data.subTableTemplateAttrList as subTableTemplateAttr>
    Page<${subTableTemplateAttr.javaObjectName}Bean> select${subTableTemplateAttr.tableObject}PaginationBy${data.tableObject}Id(${getFieldType(data.idFieldType)} id, RestJsonWrapperBean wrapper);

    Long insert${subTableTemplateAttr.tableObject}(${getFieldType(data.idFieldType)} id, RestJsonWrapperBean wrapper);

    void delete${subTableTemplateAttr.tableObject}(${getFieldType(data.idFieldType)} id, RestJsonWrapperBean wrapper);

</#list>
</#if>
}
