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
 * 设备台账-仪器采集脚本配置-结果参数(T_EMC_EAS_RESULT)
 *
 */
@Table("T_EMC_EAS_RESULT")
public class EmcEasResultBean extends AbstractPersistable<Long> implements Insertable<Long> {

    @Transient
    private static final long serialVersionUID = -1078099987844182662L;

    @Id
    private Long id; // 主键

    private Long scriptId; // 仪器采集脚本ID

    private String fieldName; // 编码名称

    private Long testItemColumnNo; // 分析项列号

    private Long resultColumnNo; // 结果列号

    private Long shortNoColumnNo; // 短号列号

    private String dynamicCreating; // 动态生成结果列号

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

    public Long getTestItemColumnNo() {
        return testItemColumnNo;
    }

    public void setTestItemColumnNo(Long testItemColumnNo) {
        this.testItemColumnNo = testItemColumnNo;
    }

    public Long getResultColumnNo() {
        return resultColumnNo;
    }

    public void setResultColumnNo(Long resultColumnNo) {
        this.resultColumnNo = resultColumnNo;
    }

    public Long getShortNoColumnNo() {
        return shortNoColumnNo;
    }

    public void setShortNoColumnNo(Long shortNoColumnNo) {
        this.shortNoColumnNo = shortNoColumnNo;
    }

    public String getDynamicCreating() {
        return dynamicCreating;
    }

    public void setDynamicCreating(String dynamicCreating) {
        this.dynamicCreating = dynamicCreating;
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
