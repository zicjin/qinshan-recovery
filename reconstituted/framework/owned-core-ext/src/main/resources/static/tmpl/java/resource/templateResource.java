package ${data.packageName}.resource;

import org.springframework.web.bind.annotation.RequestMapping;

import ${data.packageName}.bean.${data.javaObjectName}Bean;
<#if data.subTableTemplateAttrList?? && (data.subTableTemplateAttrList?size > 0) >
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
<#list data.subTableTemplateAttrList as subTableTemplateAttr>
import ${data.packageName}.bean.${subTableTemplateAttr.javaObjectName}Bean;
</#list>
</#if>
import ${data.packageName}.service.${data.javaObjectName}Service;
import com.sunwayworld.framework.support.base.resource.GenericResource;
<#if data.funcList ?? && data.funcList?seq_contains("audit") > 
import com.sunwayworld.framework.support.auditable.resource.GenericAuditableResource;
</#if>
<#if data.funcList ?? && data.funcList?seq_contains("choose") > 
import com.sunwayworld.framework.support.choosable.resource.GenericChoosableResource;
</#if>
<#function getFieldType fieldType>
    <#if fieldType=="varchar"><#return "String"><#elseif fieldType=="date"><#return "LocalDateTime"><#else><#return "Long"></#if>
</#function>

@RequestMapping("${data.resourcePath}")
public interface ${data.javaObjectName}Resource extends GenericResource<${data.javaObjectName}Service, ${data.javaObjectName}Bean, ${getFieldType(data.idFieldType)}><#if data.funcList ?? && data.funcList?seq_contains("audit") >, GenericAuditableResource<${data.javaObjectName}Service, ${data.javaObjectName}Bean, ${getFieldType(data.idFieldType)}></#if><#if data.funcList ?? && data.funcList?seq_contains("choose") >, GenericChoosableResource<${data.javaObjectName}Service, ${data.javaObjectName}Bean, ${getFieldType(data.idFieldType)}></#if> {
<#if data.subTableTemplateAttrList?? && (data.subTableTemplateAttrList?size > 0) >
<#list data.subTableTemplateAttrList as subTableTemplateAttr>

    Page<${subTableTemplateAttr.javaObjectName}Bean> select${subTableTemplateAttr.tableObject}PaginationBy${data.tableObject}Id(${getFieldType(data.idFieldType)} id, RestJsonWrapperBean wrapper);

    Long insert${subTableTemplateAttr.tableObject}(${getFieldType(data.idFieldType)} id, RestJsonWrapperBean wrapper);

    void delete${subTableTemplateAttr.tableObject}(${getFieldType(data.idFieldType)} id, RestJsonWrapperBean wrapper);
</#list>
</#if>
}
