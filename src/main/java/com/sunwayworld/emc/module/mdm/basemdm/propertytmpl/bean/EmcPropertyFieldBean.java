package com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.bean;

import java.time.LocalDateTime;

import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.format.annotation.DateTimeFormat;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import com.sunwayworld.framework.support.domain.Insertable;

/**
 * 属性字段
 *
 * @author zhangsy@sunwayworld.com 2019/9/10
 *
 */
@Table("T_EMC_PROPERTY_FIELD")
public class EmcPropertyFieldBean extends AbstractInsertable<Long>implements Insertable<Long> {

    @Transient
    private static final long serialVersionUID = -6045683847329867543L;

    @Id
    private Long id; // 主键

    private Long tmplId; // 属性模板编码

    private String internalField; // 内部字段

    private String fieldName; // 字段名称

    private String fieldType; // 字段类型

    private String defaultValue; // 默认值

    private String required; // 必填

    private String fieldLength; // 字段长度

    private String sysCode; // 系统编码

    private String createdById; // 制单人编码

    private String createdByName; // 制单人名称

    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime; // 制单时间

    private String createdByOrgId; // 制单人单位编码

    private String createdByOrgName; // 制单人单位名称

    private String orderNo; // 排序号

    public Long getTmplId() {
        return tmplId;
    }

    public void setTmplId(Long tmplId) {
        this.tmplId = tmplId;
    }

    public String getInternalField() {
        return internalField;
    }

    public void setInternalField(String internalField) {
        this.internalField = internalField;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getRequired() {
        return required;
    }

    public void setRequired(String required) {
        this.required = required;
    }

    public String getCreatedById() {
        return createdById;
    }

    public String getFieldLength() {
        return fieldLength;
    }

    public void setFieldLength(String fieldLength) {
        this.fieldLength = fieldLength;
    }

    public String getSysCode() {
        return sysCode;
    }

    public void setSysCode(String sysCode) {
        this.sysCode = sysCode;
    }

    public void setCreatedById(String createdById) {
        this.createdById = createdById;
    }

    public String getCreatedByName() {
        return createdByName;
    }

    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public String getCreatedByOrgId() {
        return createdByOrgId;
    }

    public void setCreatedByOrgId(String createdByOrgId) {
        this.createdByOrgId = createdByOrgId;
    }

    public String getCreatedByOrgName() {
        return createdByOrgName;
    }

    public void setCreatedByOrgName(String createdByOrgName) {
        this.createdByOrgName = createdByOrgName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

}
