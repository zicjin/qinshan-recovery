// 2026-09-24 从生产字节码反编译恢复（CFR 0.152）：生产新增类，历史源码缺失
package com.sunwayworld.emc.module.bp.bp.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import com.sunwayworld.framework.support.domain.Insertable;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

@Table(value="T_EMC_EQUIPT_FLOW_SET")
public class EmcEquiptFlowSetBean
extends AbstractInsertable<Long>
implements Insertable<Long> {
    @Transient
    private static final long serialVersionUID = -82566196618711043L;
    @Id
    private Long id;
    private Long sampleId;
    private Long equiptId;
    private String equiptNo;
    private String equiptName;
    private String specification;
    private String equiptInterface;
    private String airtight;
    private String flowUnit;
    private String beforeSampleFlow;
    private String beforeSetFlow;
    private String beforeError;
    private String beforeJudge;
    private String afterSampleFlow;
    private String afterSetFlow;
    private String afterError;
    private String afterJudge;
    private String errorValue;
    private String judge;
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

    public Long getEquiptId() {
        return this.equiptId;
    }

    public void setEquiptId(Long equiptId) {
        this.equiptId = equiptId;
    }

    public String getEquiptNo() {
        return this.equiptNo;
    }

    public void setEquiptNo(String equiptNo) {
        this.equiptNo = equiptNo;
    }

    public String getEquiptName() {
        return this.equiptName;
    }

    public void setEquiptName(String equiptName) {
        this.equiptName = equiptName;
    }

    public String getSpecification() {
        return this.specification;
    }

    public void setSpecification(String specification) {
        this.specification = specification;
    }

    public String getEquiptInterface() {
        return this.equiptInterface;
    }

    public void setEquiptInterface(String equiptInterface) {
        this.equiptInterface = equiptInterface;
    }

    public String getAirtight() {
        return this.airtight;
    }

    public void setAirtight(String airtight) {
        this.airtight = airtight;
    }

    public String getFlowUnit() {
        return this.flowUnit;
    }

    public void setFlowUnit(String flowUnit) {
        this.flowUnit = flowUnit;
    }

    public String getBeforeSampleFlow() {
        return this.beforeSampleFlow;
    }

    public void setBeforeSampleFlow(String beforeSampleFlow) {
        this.beforeSampleFlow = beforeSampleFlow;
    }

    public String getBeforeSetFlow() {
        return this.beforeSetFlow;
    }

    public void setBeforeSetFlow(String beforeSetFlow) {
        this.beforeSetFlow = beforeSetFlow;
    }

    public String getBeforeError() {
        return this.beforeError;
    }

    public void setBeforeError(String beforeError) {
        this.beforeError = beforeError;
    }

    public String getBeforeJudge() {
        return this.beforeJudge;
    }

    public void setBeforeJudge(String beforeJudge) {
        this.beforeJudge = beforeJudge;
    }

    public String getAfterSampleFlow() {
        return this.afterSampleFlow;
    }

    public void setAfterSampleFlow(String afterSampleFlow) {
        this.afterSampleFlow = afterSampleFlow;
    }

    public String getAfterSetFlow() {
        return this.afterSetFlow;
    }

    public void setAfterSetFlow(String afterSetFlow) {
        this.afterSetFlow = afterSetFlow;
    }

    public String getAfterError() {
        return this.afterError;
    }

    public void setAfterError(String afterError) {
        this.afterError = afterError;
    }

    public String getAfterJudge() {
        return this.afterJudge;
    }

    public void setAfterJudge(String afterJudge) {
        this.afterJudge = afterJudge;
    }

    public String getErrorValue() {
        return this.errorValue;
    }

    public void setErrorValue(String errorValue) {
        this.errorValue = errorValue;
    }

    public String getJudge() {
        return this.judge;
    }

    public void setJudge(String judge) {
        this.judge = judge;
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
}
