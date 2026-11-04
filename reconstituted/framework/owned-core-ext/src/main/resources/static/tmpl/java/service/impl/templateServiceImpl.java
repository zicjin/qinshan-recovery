package ${data.packageName}.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.spring.annotation.GikamBean;

import ${data.packageName}.dao.${data.javaObjectName}Dao;
import ${data.packageName}.bean.${data.javaObjectName}Bean;
import ${data.packageName}.service.${data.javaObjectName}Service;
import com.sunwayworld.framework.context.ApplicationContextHelper;
<#if data.subTableTemplateAttrList?? && (data.subTableTemplateAttrList?size > 0) >
import java.util.List;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
<#list data.subTableTemplateAttrList as subTableTemplateAttr>
import ${data.packageName}.bean.${subTableTemplateAttr.javaObjectName}Bean;
import ${data.packageName}.service.${subTableTemplateAttr.javaObjectName}Service;
</#list>
</#if>
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

<#function getFieldType fieldType>
<#if fieldType=="varchar"><#return "String"><#elseif fieldType=="date"><#return "LocalDateTime"><#else><#return "Long"></#if>
</#function>
<#function getIdSequence fieldType>
<#if fieldType=="varchar"><#return "ApplicationContextHelper.getNextSequence(getDao().getTable())"><#else><#return "ApplicationContextHelper.getNextIdentity()"></#if>
</#function>
@Repository
@GikamBean
public class ${data.javaObjectName}ServiceImpl implements ${data.javaObjectName}Service {

    @Autowired
    private ${data.javaObjectName}Dao ${data.javaObjectName?uncap_first}Dao;

<#if data.subTableTemplateAttrList?? && (data.subTableTemplateAttrList?size > 0) >
<#list data.subTableTemplateAttrList as subTableTemplateAttr>
    @Autowired
    private ${subTableTemplateAttr.javaObjectName}Service ${subTableTemplateAttr.javaObjectName?uncap_first}Service;

</#list>
</#if>
    @Override
    @SuppressWarnings("unchecked")
    public ${data.javaObjectName}Dao getDao() {
        return ${data.javaObjectName?uncap_first}Dao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public ${getFieldType(data.idFieldType)} insert(RestJsonWrapperBean jsonWrapper) {
        ${data.javaObjectName}Bean ${data.javaObjectName?uncap_first} = jsonWrapper.parseUnique(${data.javaObjectName}Bean.class);
        ${data.javaObjectName?uncap_first}.setId(${getIdSequence(data.idFieldType)});
        getDao().insert(${data.javaObjectName?uncap_first});
        return ${data.javaObjectName?uncap_first}.getId();
    }

<#if data.subTableTemplateAttrList?? && (data.subTableTemplateAttrList?size > 0) >
<#list data.subTableTemplateAttrList as subTableTemplateAttr>
    @Override
    public Page<${subTableTemplateAttr.javaObjectName}Bean> select${subTableTemplateAttr.tableObject}PaginationBy${data.tableObject}Id(${getFieldType(data.idFieldType)} id, RestJsonWrapperBean wrapper) {
        return ${subTableTemplateAttr.javaObjectName?uncap_first}Service.selectPaginationByFilter(SearchFilter.instance().match("${subTableTemplateAttr.foreignField?upper_case}", id).filter(MatchPattern.EQ), wrapper);
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert${subTableTemplateAttr.tableObject}(${getFieldType(data.idFieldType)} id, RestJsonWrapperBean wrapper) {

        List<${subTableTemplateAttr.javaObjectName}Bean> ${subTableTemplateAttr.javaObjectName?uncap_first}List = wrapper.parse(${subTableTemplateAttr.javaObjectName}Bean.class);

        ${subTableTemplateAttr.javaObjectName?uncap_first}List.forEach(e -> {
            e.setId(ApplicationContextHelper.getNextIdentity());
            e.set${subTableTemplateAttr.foreignField?cap_first}(id);
        });

        ${subTableTemplateAttr.javaObjectName?uncap_first}Service.getDao().insert(${subTableTemplateAttr.javaObjectName?uncap_first}List);

        return ${subTableTemplateAttr.javaObjectName?uncap_first}List.get(0).getId();
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.DELETE)
    public void delete${subTableTemplateAttr.tableObject}(${getFieldType(data.idFieldType)} id, RestJsonWrapperBean wrapper) {
        ${subTableTemplateAttr.javaObjectName?uncap_first}Service.delete(wrapper);
    }

</#list>
</#if>
}
