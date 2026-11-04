package ${data.packageName}.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import ${data.packageName}.bean.${data.javaObjectName}Bean;
import ${data.packageName}.dao.${data.javaObjectName}Dao;
import ${data.packageName}.mapper.${data.javaObjectName}Mapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;

<#function getFieldType fieldType>
    <#if fieldType=="varchar"><#return "String"><#elseif fieldType=="date"><#return "LocalDateTime"><#else><#return "Long"></#if>
</#function>
@Repository
@GikamBean
public class ${data.javaObjectName}DaoImpl extends MybatisDaoSupport<${data.javaObjectName}Bean, ${getFieldType(data.idFieldType)}> implements ${data.javaObjectName}Dao {

    @Autowired
    private ${data.javaObjectName}Mapper ${data.javaObjectName?uncap_first}Mapper;

    @Override
    public ${data.javaObjectName}Mapper getMapper() {
        return ${data.javaObjectName?uncap_first}Mapper;
    }

}
