package ${data.packageName}.mapper;

import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.mapper.GenericMapper;

<#function getFieldType fieldType>
<#if fieldType=="varchar"><#return "String"><#elseif fieldType=="date"><#return "LocalDateTime"><#else><#return "Long"></#if>
</#function>
@GikamBean
public interface ${data.javaObjectName}Mapper extends GenericMapper<${getFieldType(data.idFieldType)}> {

}
