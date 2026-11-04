package ${data.packageName}.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import ${data.packageName}.resource.${data.javaObjectName}Resource;
import ${data.packageName}.service.${data.javaObjectName}Service;
import com.sunwayworld.framework.spring.annotation.GikamBean;
<#if data.subTableTemplateAttrList?? && (data.subTableTemplateAttrList?size > 0) >
import com.sunwayworld.framework.data.page.Page;
import org.springframework.web.bind.annotation.PathVariable;
<#list data.subTableTemplateAttrList as subTableTemplateAttr>
import ${data.packageName}.bean.${subTableTemplateAttr.javaObjectName}Bean;
</#list>
</#if>
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
<#function getFieldType fieldType>
<#if fieldType=="varchar"><#return "String"><#elseif fieldType=="date"><#return "LocalDateTime"><#else><#return "Long"></#if>
</#function>

@LogModule("${data.tableDesc}")
@RestController
@GikamBean
public class ${data.javaObjectName}ResourceImpl implements ${data.javaObjectName}Resource {

    @Autowired
    private ${data.javaObjectName}Service ${data.javaObjectName?uncap_first}Service;

    @Override
    public ${data.javaObjectName}Service getService() {
        return ${data.javaObjectName?uncap_first}Service;
    }

    @Log(value = "新增${data.tableDesc}", type = LogType.INSERT)
    @Override
    @RequestMapping(method = RequestMethod.POST)
    public ${getFieldType(data.idFieldType)} insert(RestJsonWrapperBean wrapper) {
        return getService().insert(wrapper);
    }

<#if data.subTableTemplateAttrList?? && (data.subTableTemplateAttrList?size > 0) >
<#list data.subTableTemplateAttrList as subTableTemplateAttr>
    @Override
    @Log(value = "根据${subTableTemplateAttr.tableDesc}ID查询${subTableTemplateAttr.subTableDesc}", type = LogType.SELECT)
    @RequestMapping(value = "/{id}/${subTableTemplateAttr.path}s/queries", method = RequestMethod.POST)
    public Page<${subTableTemplateAttr.javaObjectName}Bean> select${subTableTemplateAttr.tableObject}PaginationBy${data.tableObject}Id(@PathVariable ${getFieldType(data.idFieldType)} id, RestJsonWrapperBean wrapper) {
        return getService().select${subTableTemplateAttr.tableObject}PaginationBy${data.tableObject}Id(id, wrapper);
    }

    @Override
    @Log(value = "新增${subTableTemplateAttr.subTableDesc}", type = LogType.INSERT)
    @RequestMapping(value = "/{id}/${subTableTemplateAttr.path}s", method = RequestMethod.POST)
    public Long insert${subTableTemplateAttr.tableObject}(@PathVariable ${getFieldType(data.idFieldType)} id, RestJsonWrapperBean wrapper) {
        return getService().insert${subTableTemplateAttr.tableObject}(id, wrapper);
    }

    @Override
    @Log(value = "删除${subTableTemplateAttr.subTableDesc}", type = LogType.DELETE)
    @RequestMapping(value = "/{id}/${subTableTemplateAttr.path}s", method = RequestMethod.DELETE)
    public void delete${subTableTemplateAttr.tableObject}(@PathVariable ${getFieldType(data.idFieldType)} id, RestJsonWrapperBean wrapper) {
        getService().delete${subTableTemplateAttr.tableObject}(id, wrapper);
    }

</#list>
</#if>
}
