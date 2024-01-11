package com.sunwayworld.emc.module.mdm.equipt.equiptacqscript.bean;

import java.time.LocalDateTime;

import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.format.annotation.DateTimeFormat;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractPersistable;
import com.sunwayworld.framework.support.domain.Insertable;

/**
 * 设备台账-仪器采集脚本配置-预处理(T_EMC_EAS_PREANALY)
 *
 */
@Table("T_EMC_EAS_PREANALY")
public class EmcEasPreAnalyBean extends AbstractPersistable<Long> implements Insertable<Long> {

    @Transient
    private static final long serialVersionUID = 660008560650684943L;

    @Id
    private Long id; // 主键

    private Long scriptId; // 仪器采集脚本ID

    private String fieldName; // 字段描述

    private String fieldSymbol; // 关键字(字段描述对应的字符)

    private String fieldReplaceSymbol; // 替换字符

    private String preAnalyCategory; // 操作 下拉列表equiptAcqPreAnalyCategory

    private Integer sortNum; // 排序码

    private String createdById; // 制单人编码

    private String createdByName; // 制单人名称

    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime; // 制单时间

    private String createdByOrgId; // 制单人单位编码

    private String createdByOrgName; // 制单人单位名称

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getScriptId() {
        return scriptId;
    }

    public void setScriptId(Long scriptId) {
        this.scriptId = scriptId;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldSymbol() {
        return fieldSymbol;
    }

    public void setFieldSymbol(String fieldSymbol) {
        this.fieldSymbol = fieldSymbol;
    }

    public String getFieldReplaceSymbol() {
        return fieldReplaceSymbol;
    }

    public void setFieldReplaceSymbol(String fieldReplaceSymbol) {
        this.fieldReplaceSymbol = fieldReplaceSymbol;
    }

    public String getPreAnalyCategory() {
        return preAnalyCategory;
    }

    public void setPreAnalyCategory(String preAnalyCategory) {
        this.preAnalyCategory = preAnalyCategory;
    }

    public Integer getSortNum() {
        return sortNum;
    }

    public void setSortNum(Integer sortNum) {
        this.sortNum = sortNum;
    }

    public String getCreatedById() {
        return createdById;
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

}
