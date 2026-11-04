package ${data.packageName}.dao;

import ${data.packageName}.bean.${data.javaObjectName}Bean;
import com.sunwayworld.framework.support.base.dao.GenericDao;

<#function getFieldType fieldType>
    <#if fieldType=="varchar"><#return "String"><#elseif fieldType=="date"><#return "LocalDateTime"><#else><#return "Long"></#if>
</#function>
public interface ${data.javaObjectName}Dao extends GenericDao<${data.javaObjectName}Bean, ${getFieldType(data.idFieldType)}> {
}
