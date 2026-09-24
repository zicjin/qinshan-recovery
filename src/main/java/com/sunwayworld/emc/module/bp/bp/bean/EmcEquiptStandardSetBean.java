// 2026-09-24 从生产字节码反编译恢复（CFR 0.152）：生产新增类，历史源码缺失
package com.sunwayworld.emc.module.bp.bp.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import com.sunwayworld.framework.support.domain.Insertable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

@Table(value="T_EMC_EQUIPT_STANDARD_SET")
public class EmcEquiptStandardSetBean
extends AbstractInsertable<Long>
implements Insertable<Long> {
    @Transient
    private static final long serialVersionUID = -82566196618711043L;
    @Id
    private Long id;
    private Long sampleId;
    private String gasName;
    private String gasNo;
    private LocalDate validDate;
    private String gasType;
    private String density;
    private String beforeTestA;
    private String beforeTestB;
    private String beforeError;
    private String beforeSysError;
    private String afterTestA;
    private String afterTestB;
    private String afterError;
    private String afterSysError;
    private String conclusion;
    private String bizType;
    private String createdById;
    private String createdByName;
    @JSONField(format="yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;
    private String createdByOrgId;
    private String createdByOrgName;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getSampleId() {
        return this.sampleId;
    }

    public void setSampleId(Long sampleId) {
        this.sampleId = sampleId;
    }

    public String getGasName() {
        return this.gasName;
    }

    public void setGasName(String gasName) {
        this.gasName = gasName;
    }

    public String getGasNo() {
        return this.gasNo;
    }

    public void setGasNo(String gasNo) {
        this.gasNo = gasNo;
    }

    public LocalDate getValidDate() {
        return this.validDate;
    }

    public void setValidDate(LocalDate validDate) {
        this.validDate = validDate;
    }

    public String getGasType() {
        return this.gasType;
    }

    public void setGasType(String gasType) {
        this.gasType = gasType;
    }

    public String getDensity() {
        return this.density;
    }

    public void setDensity(String density) {
        this.density = density;
    }

    public String getBeforeTestA() {
        return this.beforeTestA;
    }

    public void setBeforeTestA(String beforeTestA) {
        this.beforeTestA = beforeTestA;
    }

    public String getBeforeTestB() {
        return this.beforeTestB;
    }

    public void setBeforeTestB(String beforeTestB) {
        this.beforeTestB = beforeTestB;
    }

    public String getBeforeError() {
        return this.beforeError;
    }

    public void setBeforeError(String beforeError) {
        this.beforeError = beforeError;
    }

    public String getAfterTestA() {
        return this.afterTestA;
    }

    public void setAfterTestA(String afterTestA) {
        this.afterTestA = afterTestA;
    }

    public String getAfterTestB() {
        return this.afterTestB;
    }

    public void setAfterTestB(String afterTestB) {
        this.afterTestB = afterTestB;
    }

    public String getAfterError() {
        return this.afterError;
    }

    public void setAfterError(String afterError) {
        this.afterError = afterError;
    }

    public String getConclusion() {
        return this.conclusion;
    }

    public void setConclusion(String conclusion) {
        this.conclusion = conclusion;
    }

    public String getBizType() {
        return this.bizType;
    }

    public void setBizType(String bizType) {
        this.bizType = bizType;
    }

    @Override
    public String getCreatedById() {
        return this.createdById;
    }

    @Override
    public void setCreatedById(String createdById) {
        this.createdById = createdById;
    }

    @Override
    public String getCreatedByName() {
        return this.createdByName;
    }

    @Override
    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    @Override
    public LocalDateTime getCreatedTime() {
        return this.createdTime;
    }

    @Override
    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    @Override
    public String getCreatedByOrgId() {
        return this.createdByOrgId;
    }

    @Override
    public void setCreatedByOrgId(String createdByOrgId) {
        this.createdByOrgId = createdByOrgId;
    }

    @Override
    public String getCreatedByOrgName() {
        return this.createdByOrgName;
    }

    @Override
    public void setCreatedByOrgName(String createdByOrgName) {
        this.createdByOrgName = createdByOrgName;
    }

    public String getBeforeSysError() {
        return this.beforeSysError;
    }

    public void setBeforeSysError(String beforeSysError) {
        this.beforeSysError = beforeSysError;
    }

    public String getAfterSysError() {
        return this.afterSysError;
    }

    public void setAfterSysError(String afterSysError) {
        this.afterSysError = afterSysError;
    }
}
