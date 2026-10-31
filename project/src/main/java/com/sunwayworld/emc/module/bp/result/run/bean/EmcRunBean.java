package com.sunwayworld.emc.module.bp.result.run.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.sunwayworld.framework.data.annotation.Clob;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.NotNull;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractAuditable;
import com.sunwayworld.framework.support.domain.Insertable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

@Table(value="T_EMC_RUN")
public class EmcRunBean
extends AbstractAuditable<String>
implements Insertable<String> {
    @Transient
    private static final long serialVersionUID = 4908341682974050355L;
    @Id
    private String id;
    private Long projId;
    private String humidity;
    private String temperature;
    @JSONField(format="yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate analysisBeginDate;
    private String analysisBeginTime;
    @JSONField(format="yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate analysisEndDate;
    private String analysisEndTime;
    private String receiveDate;
    private String receiveTime;
    @JSONField(format="yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate handleDate;
    private String handleMethod;
    @Clob
    private String beforeHandle;
    private String beforeHandleId;
    private String methodParam;
    private String analysisUserId;
    private String analysisUserName;
    private String partakeUserId;
    private String partakeUserName;
    private String assistEquiptId;
    private String assistEquiptName;
    private Long equiptId;
    private String curveId;
    private String curveNo;
    private String orderCategory;
    private String checkUserId;
    private String checkUserName;
    private String testMethodId;
    private String testId;
    private String testName;
    private Long methodId;
    private Long reportConfigLineId;
    private Long sol1Id;
    private Long sol2Id;
    private Long reagentId;
    @NotNull(defaultValue="draft")
    private String status;
    private String checkStatus;
    private String assistEquiptModel;
    private String assistEquiptNo;
    private String relatedRunId;
    private String remark;
    private String createdById;
    private String createdByName;
    @JSONField(format="yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;
    private String createdByOrgId;
    private String createdByOrgName;
    private String orgId;
    private String orgName;
    private String auditedUserId;
    private String auditedUserName;
    @JSONField(format="yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime auditedDate;
    private String sol1Amount;
    private String sol2Amount;
    private String projNo;
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime weightTime;
    private Long weighingReportLineId;
    private String undoReason;
    private String undoFlag;
    private String stNo;
    private String stConcent;
    @JSONField(format="yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate stValiDate;
    private String stValiDateStr;

    public String getUndoFlag() {
        return this.undoFlag;
    }

    public void setUndoFlag(String undoFlag) {
        this.undoFlag = undoFlag;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getProjId() {
        return this.projId;
    }

    public void setProjId(Long projId) {
        this.projId = projId;
    }

    public String getHumidity() {
        return this.humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getTemperature() {
        return this.temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public LocalDate getAnalysisBeginDate() {
        return this.analysisBeginDate;
    }

    public void setAnalysisBeginDate(LocalDate analysisBeginDate) {
        this.analysisBeginDate = analysisBeginDate;
    }

    public String getAnalysisBeginTime() {
        return this.analysisBeginTime;
    }

    public void setAnalysisBeginTime(String analysisBeginTime) {
        this.analysisBeginTime = analysisBeginTime;
    }

    public LocalDate getAnalysisEndDate() {
        return this.analysisEndDate;
    }

    public void setAnalysisEndDate(LocalDate analysisEndDate) {
        this.analysisEndDate = analysisEndDate;
    }

    public String getAnalysisEndTime() {
        return this.analysisEndTime;
    }

    public void setAnalysisEndTime(String analysisEndTime) {
        this.analysisEndTime = analysisEndTime;
    }

    public String getReceiveDate() {
        return this.receiveDate;
    }

    public void setReceiveDate(String receiveDate) {
        this.receiveDate = receiveDate;
    }

    public String getReceiveTime() {
        return this.receiveTime;
    }

    public void setReceiveTime(String receiveTime) {
        this.receiveTime = receiveTime;
    }

    public LocalDate getHandleDate() {
        return this.handleDate;
    }

    public void setHandleDate(LocalDate handleDate) {
        this.handleDate = handleDate;
    }

    public String getHandleMethod() {
        return this.handleMethod;
    }

    public void setHandleMethod(String handleMethod) {
        this.handleMethod = handleMethod;
    }

    public String getBeforeHandle() {
        return this.beforeHandle;
    }

    public void setBeforeHandle(String beforeHandle) {
        this.beforeHandle = beforeHandle;
    }

    public String getMethodParam() {
        return this.methodParam;
    }

    public void setMethodParam(String methodParam) {
        this.methodParam = methodParam;
    }

    public String getAnalysisUserId() {
        return this.analysisUserId;
    }

    public void setAnalysisUserId(String analysisUserId) {
        this.analysisUserId = analysisUserId;
    }

    public String getAnalysisUserName() {
        return this.analysisUserName;
    }

    public void setAnalysisUserName(String analysisUserName) {
        this.analysisUserName = analysisUserName;
    }

    public String getPartakeUserId() {
        return this.partakeUserId;
    }

    public void setPartakeUserId(String partakeUserId) {
        this.partakeUserId = partakeUserId;
    }

    public String getPartakeUserName() {
        return this.partakeUserName;
    }

    public void setPartakeUserName(String partakeUserName) {
        this.partakeUserName = partakeUserName;
    }

    public String getAssistEquiptId() {
        return this.assistEquiptId;
    }

    public void setAssistEquiptId(String assistEquiptId) {
        this.assistEquiptId = assistEquiptId;
    }

    public String getAssistEquiptName() {
        return this.assistEquiptName;
    }

    public void setAssistEquiptName(String assistEquiptName) {
        this.assistEquiptName = assistEquiptName;
    }

    public Long getEquiptId() {
        return this.equiptId;
    }

    public void setEquiptId(Long equiptId) {
        this.equiptId = equiptId;
    }

    public String getCurveId() {
        return this.curveId;
    }

    public void setCurveId(String curveId) {
        this.curveId = curveId;
    }

    public String getCurveNo() {
        return this.curveNo;
    }

    public void setCurveNo(String curveNo) {
        this.curveNo = curveNo;
    }

    public String getOrderCategory() {
        return this.orderCategory;
    }

    public void setOrderCategory(String orderCategory) {
        this.orderCategory = orderCategory;
    }

    public String getCheckUserId() {
        return this.checkUserId;
    }

    public void setCheckUserId(String checkUserId) {
        this.checkUserId = checkUserId;
    }

    public String getCheckUserName() {
        return this.checkUserName;
    }

    public void setCheckUserName(String checkUserName) {
        this.checkUserName = checkUserName;
    }

    public String getTestMethodId() {
        return this.testMethodId;
    }

    public void setTestMethodId(String testMethodId) {
        this.testMethodId = testMethodId;
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

    public Long getMethodId() {
        return this.methodId;
    }

    public void setMethodId(Long methodId) {
        this.methodId = methodId;
    }

    public Long getReportConfigLineId() {
        return this.reportConfigLineId;
    }

    public void setReportConfigLineId(Long reportConfigLineId) {
        this.reportConfigLineId = reportConfigLineId;
    }

    public Long getSol1Id() {
        return this.sol1Id;
    }

    public void setSol1Id(Long sol1Id) {
        this.sol1Id = sol1Id;
    }

    public Long getSol2Id() {
        return this.sol2Id;
    }

    public void setSol2Id(Long sol2Id) {
        this.sol2Id = sol2Id;
    }

    public Long getReagentId() {
        return this.reagentId;
    }

    public void setReagentId(Long reagentId) {
        this.reagentId = reagentId;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCheckStatus() {
        return this.checkStatus;
    }

    public void setCheckStatus(String checkStatus) {
        this.checkStatus = checkStatus;
    }

    public String getAssistEquiptModel() {
        return this.assistEquiptModel;
    }

    public void setAssistEquiptModel(String assistEquiptModel) {
        this.assistEquiptModel = assistEquiptModel;
    }

    public String getAssistEquiptNo() {
        return this.assistEquiptNo;
    }

    public void setAssistEquiptNo(String assistEquiptNo) {
        this.assistEquiptNo = assistEquiptNo;
    }

    public String getRelatedRunId() {
        return this.relatedRunId;
    }

    public void setRelatedRunId(String relatedRunId) {
        this.relatedRunId = relatedRunId;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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

    public String getAuditedUserId() {
        return this.auditedUserId;
    }

    public void setAuditedUserId(String auditedUserId) {
        this.auditedUserId = auditedUserId;
    }

    public String getAuditedUserName() {
        return this.auditedUserName;
    }

    public void setAuditedUserName(String auditedUserName) {
        this.auditedUserName = auditedUserName;
    }

    public LocalDateTime getAuditedDate() {
        return this.auditedDate;
    }

    public void setAuditedDate(LocalDateTime auditedDate) {
        this.auditedDate = auditedDate;
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

    public String getBeforeHandleId() {
        return this.beforeHandleId;
    }

    public void setBeforeHandleId(String beforeHandleId) {
        this.beforeHandleId = beforeHandleId;
    }

    public String getSol1Amount() {
        return this.sol1Amount;
    }

    public void setSol1Amount(String sol1Amount) {
        this.sol1Amount = sol1Amount;
    }

    public String getSol2Amount() {
        return this.sol2Amount;
    }

    public void setSol2Amount(String sol2Amount) {
        this.sol2Amount = sol2Amount;
    }

    public String getProjNo() {
        return this.projNo;
    }

    public void setProjNo(String projNo) {
        this.projNo = projNo;
    }

    public LocalDateTime getWeightTime() {
        return this.weightTime;
    }

    public void setWeightTime(LocalDateTime weightTime) {
        this.weightTime = weightTime;
    }

    public Long getWeighingReportLineId() {
        return this.weighingReportLineId;
    }

    public void setWeighingReportLineId(Long weighingReportLineId) {
        this.weighingReportLineId = weighingReportLineId;
    }

    public String getUndoReason() {
        return this.undoReason;
    }

    public void setUndoReason(String undoReason) {
        this.undoReason = undoReason;
    }

    public String getStNo() {
        return this.stNo;
    }

    public void setStNo(String stNo) {
        this.stNo = stNo;
    }

    public String getStConcent() {
        return this.stConcent;
    }

    public void setStConcent(String stConcent) {
        this.stConcent = stConcent;
    }

    public LocalDate getStValiDate() {
        return this.stValiDate;
    }

    public void setStValiDate(LocalDate stValiDate) {
        this.stValiDate = stValiDate;
    }

    public String getStValiDateStr() {
        return this.stValiDateStr;
    }

    public void setStValiDateStr(String stValiDateStr) {
        this.stValiDateStr = stValiDateStr;
    }
}
