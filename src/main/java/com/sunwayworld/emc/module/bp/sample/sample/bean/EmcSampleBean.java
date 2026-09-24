package com.sunwayworld.emc.module.bp.sample.sample.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.NotNull;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractAuditable;
import com.sunwayworld.framework.support.domain.Auditable;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

@Table(value="T_EMC_SAMPLE")
public class EmcSampleBean
extends AbstractAuditable<Long>
implements Auditable<Long> {
    @Transient
    private static final long serialVersionUID = 2345464087581210775L;
    @Id
    private Long id;
    private String sampleNo;
    private Long projId;
    private String projNo;
    private String monitorTmplId;
    private String orderCategory;
    private String source;
    @NotNull(defaultValue="draft")
    private String status;
    @NotNull(defaultValue="draft")
    private String handStatus;
    private String remark;
    private Long propertyTmplId;
    private String reportConfigLineId;
    @JSONField(format="yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime sepSampleTime;
    private String acceptedUserId;
    private String accepteUserName;
    @JSONField(format="yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime acceptedDate;
    private String acceptedTime;
    private String mobileId;
    private String sampleCheck;
    private String usedContainer;
    private String storageValid;
    private String exchangeValid;
    private String samplingValid;
    private String markValid;
    private String containerValid;
    private String qcUserId;
    private String qcUserName;
    private String calibrationId;
    private String caliReportLineId;
    private String receiveReportLineId;
    private String printFlag;
    private String createdById;
    private String createdByName;
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;
    private String createdByOrgId;
    private String createdByOrgName;
    private String orgId;
    private String orgName;
    @NotNull(defaultValue="0")
    private String acceptStatus;
    @NotNull(defaultValue="0")
    private String assignStatus;
    private String undoReason;
    private String undoFlag;
    private String sampleSceneUserId;
    private String sampleSceneUserName;

    public String getUndoReason() {
        return this.undoReason;
    }

    public void setUndoReason(String undoReason) {
        this.undoReason = undoReason;
    }

    public String getUndoFlag() {
        return this.undoFlag;
    }

    public void setUndoFlag(String undoFlag) {
        this.undoFlag = undoFlag;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAcceptedTime() {
        return this.acceptedTime;
    }

    public void setAcceptedTime(String acceptedTime) {
        this.acceptedTime = acceptedTime;
    }

    public String getSampleNo() {
        return this.sampleNo;
    }

    public void setSampleNo(String sampleNo) {
        this.sampleNo = sampleNo;
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

    public String getMonitorTmplId() {
        return this.monitorTmplId;
    }

    public void setMonitorTmplId(String monitorTmplId) {
        this.monitorTmplId = monitorTmplId;
    }

    public String getOrderCategory() {
        return this.orderCategory;
    }

    public void setOrderCategory(String orderCategory) {
        this.orderCategory = orderCategory;
    }

    public String getSource() {
        return this.source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Long getPropertyTmplId() {
        return this.propertyTmplId;
    }

    public void setPropertyTmplId(Long propertyTmplId) {
        this.propertyTmplId = propertyTmplId;
    }

    public String getReportConfigLineId() {
        return this.reportConfigLineId;
    }

    public void setReportConfigLineId(String reportConfigLineId) {
        this.reportConfigLineId = reportConfigLineId;
    }

    public LocalDateTime getSepSampleTime() {
        return this.sepSampleTime;
    }

    public void setSepSampleTime(LocalDateTime sepSampleTime) {
        this.sepSampleTime = sepSampleTime;
    }

    public String getAcceptedUserId() {
        return this.acceptedUserId;
    }

    public void setAcceptedUserId(String acceptedUserId) {
        this.acceptedUserId = acceptedUserId;
    }

    public String getAccepteUserName() {
        return this.accepteUserName;
    }

    public void setAccepteUserName(String accepteUserName) {
        this.accepteUserName = accepteUserName;
    }

    public LocalDateTime getAcceptedDate() {
        return this.acceptedDate;
    }

    public void setAcceptedDate(LocalDateTime acceptedDate) {
        this.acceptedDate = acceptedDate;
    }

    public String getMobileId() {
        return this.mobileId;
    }

    public void setMobileId(String mobileId) {
        this.mobileId = mobileId;
    }

    public String getSampleCheck() {
        return this.sampleCheck;
    }

    public void setSampleCheck(String sampleCheck) {
        this.sampleCheck = sampleCheck;
    }

    public String getUsedContainer() {
        return this.usedContainer;
    }

    public void setUsedContainer(String usedContainer) {
        this.usedContainer = usedContainer;
    }

    public String getStorageValid() {
        return this.storageValid;
    }

    public void setStorageValid(String storageValid) {
        this.storageValid = storageValid;
    }

    public String getExchangeValid() {
        return this.exchangeValid;
    }

    public void setExchangeValid(String exchangeValid) {
        this.exchangeValid = exchangeValid;
    }

    public String getSamplingValid() {
        return this.samplingValid;
    }

    public void setSamplingValid(String samplingValid) {
        this.samplingValid = samplingValid;
    }

    public String getMarkValid() {
        return this.markValid;
    }

    public void setMarkValid(String markValid) {
        this.markValid = markValid;
    }

    public String getContainerValid() {
        return this.containerValid;
    }

    public void setContainerValid(String containerValid) {
        this.containerValid = containerValid;
    }

    public String getQcUserId() {
        return this.qcUserId;
    }

    public void setQcUserId(String qcUserId) {
        this.qcUserId = qcUserId;
    }

    public String getQcUserName() {
        return this.qcUserName;
    }

    public void setQcUserName(String qcUserName) {
        this.qcUserName = qcUserName;
    }

    public String getCalibrationId() {
        return this.calibrationId;
    }

    public void setCalibrationId(String calibrationId) {
        this.calibrationId = calibrationId;
    }

    public String getCaliReportLineId() {
        return this.caliReportLineId;
    }

    public void setCaliReportLineId(String caliReportLineId) {
        this.caliReportLineId = caliReportLineId;
    }

    public String getReceiveReportLineId() {
        return this.receiveReportLineId;
    }

    public void setReceiveReportLineId(String receiveReportLineId) {
        this.receiveReportLineId = receiveReportLineId;
    }

    public String getPrintFlag() {
        return this.printFlag;
    }

    public void setPrintFlag(String printFlag) {
        this.printFlag = printFlag;
    }

    public String getCreatedById() {
        return this.createdById;
    }

    public void setCreatedById(String createdById) {
        this.createdById = createdById;
    }

    public String getCreatedByName() {
        return this.createdByName;
    }

    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    public LocalDateTime getCreatedTime() {
        return this.createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public String getCreatedByOrgId() {
        return this.createdByOrgId;
    }

    public void setCreatedByOrgId(String createdByOrgId) {
        this.createdByOrgId = createdByOrgId;
    }

    public String getCreatedByOrgName() {
        return this.createdByOrgName;
    }

    public void setCreatedByOrgName(String createdByOrgName) {
        this.createdByOrgName = createdByOrgName;
    }

    public String getOrgId() {
        return this.orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getOrgName() {
        return this.orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getHandStatus() {
        return this.handStatus;
    }

    public void setHandStatus(String handStatus) {
        this.handStatus = handStatus;
    }

    public String getAcceptStatus() {
        return this.acceptStatus;
    }

    public void setAcceptStatus(String acceptStatus) {
        this.acceptStatus = acceptStatus;
    }

    public String getAssignStatus() {
        return this.assignStatus;
    }

    public void setAssignStatus(String assignStatus) {
        this.assignStatus = assignStatus;
    }

    public String getSampleSceneUserId() {
        return this.sampleSceneUserId;
    }

    public void setSampleSceneUserId(String sampleSceneUserId) {
        this.sampleSceneUserId = sampleSceneUserId;
    }

    public String getSampleSceneUserName() {
        return this.sampleSceneUserName;
    }

    public void setSampleSceneUserName(String sampleSceneUserName) {
        this.sampleSceneUserName = sampleSceneUserName;
    }
}
