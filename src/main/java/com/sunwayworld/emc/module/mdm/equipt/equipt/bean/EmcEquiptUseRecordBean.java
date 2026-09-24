/*
 * Decompiled with CFR.
 */
package com.sunwayworld.emc.module.mdm.equipt.equipt.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractPersistable;
import com.sunwayworld.framework.support.domain.Insertable;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

@Table(value="T_EMC_EQUIPT_USE_RECORD")
public class EmcEquiptUseRecordBean
extends AbstractPersistable<Long>
implements Insertable<Long> {
    @Transient
    private static final long serialVersionUID = -2915409537745707942L;
    @Id
    private Long id;
    private Long projId;
    private String projNo;
    private Long sampleId;
    private String sampleNo;
    private String testId;
    private String testName;
    private Long equiptId;
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime usedTime;
    private String beforeUseStatus;
    private String afterUseStatus;
    private String userId;
    private String userName;
    private String runId;
    private String equiptCondition;
    private String orderQty;
    private String startDate;
    private String startTime;
    private String endDate;
    private String endTime;
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

    public Long getProjId() {
        return this.projId;
    }

    public void setProjId(Long projId) {
        this.projId = projId;
    }

    public String getProjNo() {
        return this.projNo;
    }

    public void setProjNo(String projNo) {
        this.projNo = projNo;
    }

    public Long getSampleId() {
        return this.sampleId;
    }

    public void setSampleId(Long sampleId) {
        this.sampleId = sampleId;
    }

    public String getSampleNo() {
        return this.sampleNo;
    }

    public void setSampleNo(String sampleNo) {
        this.sampleNo = sampleNo;
    }

    public String getTestId() {
        return this.testId;
    }

    public void setTestId(String testId) {
        this.testId = testId;
    }

    public String getTestName() {
        return this.testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public Long getEquiptId() {
        return this.equiptId;
    }

    public void setEquiptId(Long equiptId) {
        this.equiptId = equiptId;
    }

    public LocalDateTime getUsedTime() {
        return this.usedTime;
    }

    public void setUsedTime(LocalDateTime usedTime) {
        this.usedTime = usedTime;
    }

    public String getBeforeUseStatus() {
        return this.beforeUseStatus;
    }

    public void setBeforeUseStatus(String beforeUseStatus) {
        this.beforeUseStatus = beforeUseStatus;
    }

    public String getAfterUseStatus() {
        return this.afterUseStatus;
    }

    public void setAfterUseStatus(String afterUseStatus) {
        this.afterUseStatus = afterUseStatus;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getRunId() {
        return this.runId;
    }

    public void setRunId(String runId) {
        this.runId = runId;
    }

    public String getEquiptCondition() {
        return this.equiptCondition;
    }

    public void setEquiptCondition(String equiptCondition) {
        this.equiptCondition = equiptCondition;
    }

    public String getOrderQty() {
        return this.orderQty;
    }

    public void setOrderQty(String orderQty) {
        this.orderQty = orderQty;
    }

    public String getStartDate() {
        return this.startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getStartTime() {
        return this.startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndDate() {
        return this.endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getEndTime() {
        return this.endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
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

