package ${data.packageName}.bean;

<#list data.fields as field>
<#if field.fieldType == "clob">
import com.sunwayworld.framework.data.annotation.Clob;
</#if>
</#list>
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
<#assign beanExtendClass = "AbstractPersistable" >
<#assign beanImplementInterface = "Persistable" >
<#if data.audit == "1"> 
import com.sunwayworld.framework.support.domain.Auditable;
import com.sunwayworld.framework.support.domain.AbstractAuditable;
import com.sunwayworld.framework.data.annotation.NotNull;
<#assign beanImplementInterface = "Auditable" >
<#assign beanExtendClass = "AbstractAuditable" >
<#elseif data.create == "1">
import com.sunwayworld.framework.support.domain.Insertable;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
<#assign beanImplementInterface = "Insertable" >
<#assign beanExtendClass = "AbstractInsertable" >
<#else>
import com.sunwayworld.framework.support.domain.Persistable;
import com.sunwayworld.framework.support.domain.AbstractPersistable;
</#if>
<#if data.date == "1"> 
import java.time.LocalDate;
</#if>
<#if data.dateTime == "1"> 
import java.time.LocalDateTime;
</#if>
<#if data.dateFlag == "1"> 
import org.springframework.format.annotation.DateTimeFormat;
import com.alibaba.fastjson.annotation.JSONField;
</#if>

<#function getFieldType fieldType>
    <#if fieldType=="varchar"><#return "String"><#elseif fieldType=="datetime"><#return "LocalDateTime"><#elseif fieldType=="date"><#return "LocalDate"><#elseif fieldType=="clob"><#return "String"><#elseif fieldType=="Double"><#return "Double"><#else><#return "Long"></#if>
</#function>
/**
 * ${data.tableDesc}
 * 
 * @author ${data.author}@sunwayworld.com
 * @date ${data.nowDate}
 */
@Table("${data.tableName?upper_case}")
public class ${data.javaObjectName}Bean extends ${beanExtendClass}<${getFieldType(data.idFieldType)}> implements ${beanImplementInterface}<${getFieldType(data.idFieldType)}> {

    @Transient
    private static final long serialVersionUID = -82566196618711043L;

    <#list data.fields as field>
    <#if field.field?upper_case=="ID">
    @Id
    <#elseif field.field=="createdById">
    <#elseif field.field=="createdByName">
    <#elseif field.field=="createdTime">
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    <#elseif field.field=="createdByOrgId">
    <#elseif field.field=="createdByOrgName">
    <#elseif field.field=="lastUpdatedById">
    <#elseif field.field=="lastUpdatedByName">
    <#elseif field.field=="lastUpdatedTime">
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    <#elseif field.field=="lastSuspendedFlag">
    @NotNull(defaultValue = "0")
    <#elseif field.field=="lastSuspendedById">
    <#elseif field.field=="lastSuspendedByName">
    <#elseif field.field=="lastSuspendedTime">
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    <#elseif field.field=="processInstanceId">
    <#elseif field.field=="lastAuditedFlag">
    @NotNull(defaultValue = "0")
    <#elseif field.field=="lastAuditedById">
    <#elseif field.field=="lastAuditedByName">
    <#elseif field.field=="processStatus">
    @NotNull(defaultValue = "draft")
    <#elseif field.field=="lastAuditedTime">
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    <#elseif field.field=="lastRejectedFlag">
    @NotNull(defaultValue = "0")
    <#elseif field.field=="lastRejectedById">
    <#elseif field.field=="lastRejectedByName">
    <#elseif field.field=="lastRejectedTime">
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    <#elseif field.field=="submittedById">
    <#elseif field.field=="submittedByName">
    <#elseif field.field=="submittedTime">
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    <#elseif field.field=="approvedById">
    <#elseif field.field=="approvedByName">
    <#elseif field.field=="approvedTime">
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    <#elseif field.fieldType?upper_case=="CLOB">
    @Clob
    </#if>
    private ${getFieldType(field.fieldType)} ${field.field};// ${field.fieldName}
    </#list>

    <#list data.fields as field>
    public ${getFieldType(field.fieldType)} get${field.field?cap_first}() {
        return ${field.field};
    }

    public void set${field.field?cap_first}(${getFieldType(field.fieldType)} ${field.field}) {
        this.${field.field} = ${field.field};
    }

</#list>
}
