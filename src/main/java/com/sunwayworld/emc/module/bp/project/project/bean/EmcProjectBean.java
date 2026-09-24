/*
 * Decompiled with CFR.
 */
package com.sunwayworld.emc.module.bp.project.project.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.sunwayworld.framework.data.annotation.Clob;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.NotNull;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractAuditable;
import com.sunwayworld.framework.support.domain.Auditable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

@Table(value="T_EMC_PROJECT")
public class EmcProjectBean
extends AbstractAuditable<Long>
implements Auditable<Long> {
    @Transient
    private static final long serialVersionUID = -8279392325526171185L;
    @Id
    private Long id;
    private String projNo;
    private String projName;
    private String testOrgId;
    private String testOrgName;
    private String projAddress;
    private String entrustOrgId;
    private String entrustOrgName;
    private String entrustOrgAddress;
    private Long bizCategoryId;
    private String bizCategoryCode;
    private String taskNo;
    private String description;
    @JSONField(format="yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime registDate;
    private String reportOrgId;
    @JSONField(format="yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime acceptDate;
    @JSONField(format="yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime compleDate;
    private String projAmount;
    private String monitorCategory;
    @JSONField(format="yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime entrustDate;
    @JSONField(format="yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime sendDate;
    private String sendType;
    private String entrustMethod;
    private String subPackageDesc;
    private String ndCal;
    @NotNull(defaultValue="create")
    private String status;
    private String cancleReason;
    private String sendUsername;
    @JSONField(format="yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime taskDate;
    private String testContent;
    private String qcReq;
    private String remark;
    private String entrustOrgContact;
    private String entrustOrgPhone;
    private String testOrgAddress;
    private String testOrgContact;
    private String testOrgPhone;
    private String monitorOrgName;
    private String source;
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime nextTaskDate;
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;
    private String cron;
    private String mobileId;
    @NotNull(defaultValue="\u767b\u8bb0")
    private String projNode;
    private String sample;
    private String urgent;
    private String beforeStatus;
    private String reportEditUserId;
    private String reportEditUserName;
    @NotNull(defaultValue="0")
    private String qcCheck;
    private String cancelReason;
    private String schemeReportLineId;
    private String testOrgEmail;
    private String testOrgFixedPhone;
    private String projectQcUserId;
    private String projectQcUserName;
    private String projectSampleOrgId;
    private String sampleOrgName;
    private String projectSampleById;
    private String projectSampleByName;
    @JSONField(format="yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime projectSampleBeginDate;
    @JSONField(format="yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime projectSampleEndDate;
    private String createdById;
    private String createdByName;
    @JSONField(format="yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;
    private String createdByOrgId;
    private String createdByOrgName;
    private String orgId;
    private String orgName;
    private String keepDescription;
    private String remainSampleDeal;
    private String testsMethod;
    private String servicePay;
    private String invoiceTitle;
    private String taxNo;
    private String payType;
    private String reportFormat;
    private String reportNum;
    private String reportGrantTime;
    private String reportSendType;
    @NotNull(defaultValue="0")
    private String prospect;
    @JSONField(format="yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime prospectDate;
    @Clob
    private String opinion;
    @Clob
    private String safetyMeasures;
    @Clob
    private String monitorCondition;
    @Clob
    private String prospecRemark;
    private String testCycle;
    private String reportDeliveryWay;
    private String packMethod;
    private String requiredSamptime;
    private String orderNum;
    private Long sampleOrderCount;
    private Long erpGbId;
    private Long erpReviewId;
    private String reportAptitude;
    private String reportUse;
    private String pretreatment;
    private String needQcReport;
    private String qcReportType;
    private String needSplitReport;
    private String subcontractSupplier;
    private String reportEnName;
    private String reportCnName;
    private String reportEnAddress;
    private String reportCnAddress;
    private String testingLab;
    @NotNull(defaultValue="1")
    private String testMethodAssigner;
    private String mergeReport;
    private String reportPostAddress;
    private String addressee;
    private String recipientPhone;
    private String detectPurpose;
    private String erpOrderNumber;
    private String reportTitle;
    private Long splitContractId;
    private String securityLevel;
    private String reportFee;
    private String needReport;
    private String periodType;
    @NotNull(defaultValue="7~15")
    private String period;
    private String otherReportUse;
    private String erpContactGbId;
    private String entrustOrgEmail;
    @JSONField(format="yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime projCreatedTime;
    private String labInnerNo;
    private Long subcontractorId;
    private String projType;
    private LocalDate requiredCompleteDate;
    @JSONField(format="yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime reportIssueTime;
    private String testProjectName;
    private String businessUserId;
    private String businessUserName;
    private String minSampleDuration;
    private Long year;
    @JSONField(format="yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate toReportEditDate;
    private String reportAssignStatus;
    private String projectType;
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime firstIssueDateTime;
    @NotNull(defaultValue="0")
    private String projectPrice;
    private String folderRemark;
    private String projSection;
    private String customerSource;
    private String remarkOne;
    private String remarkTwo;
    @NotNull(defaultValue="0")
    private String amountDeterm;

    public String getReportAssignStatus() {
        return this.reportAssignStatus;
    }

    public void setReportAssignStatus(String reportAssignStatus) {
        this.reportAssignStatus = reportAssignStatus;
    }

    public LocalDateTime getReportIssueTime() {
        return this.reportIssueTime;
    }

    public void setReportIssueTime(LocalDateTime reportIssueTime) {
        this.reportIssueTime = reportIssueTime;
    }

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getProjNo() {
        return this.projNo;
    }

    public void setProjNo(String projNo) {
        this.projNo = projNo;
    }

    public String getProjName() {
        return this.projName;
    }

    public void setProjName(String projName) {
        this.projName = projName;
    }

    public String getTestOrgId() {
        return this.testOrgId;
    }

    public void setTestOrgId(String testOrgId) {
        this.testOrgId = testOrgId;
    }

    public String getTestOrgName() {
        return this.testOrgName;
    }

    public void setTestOrgName(String testOrgName) {
        this.testOrgName = testOrgName;
    }

    public String getProjAddress() {
        return this.projAddress;
    }

    public void setProjAddress(String projAddress) {
        this.projAddress = projAddress;
    }

    public String getEntrustOrgId() {
        return this.entrustOrgId;
    }

    public void setEntrustOrgId(String entrustOrgId) {
        this.entrustOrgId = entrustOrgId;
    }

    public String getEntrustOrgName() {
        return this.entrustOrgName;
    }

    public void setEntrustOrgName(String entrustOrgName) {
        this.entrustOrgName = entrustOrgName;
    }

    public String getEntrustOrgAddress() {
        return this.entrustOrgAddress;
    }

    public void setEntrustOrgAddress(String entrustOrgAddress) {
        this.entrustOrgAddress = entrustOrgAddress;
    }

    public Long getBizCategoryId() {
        return this.bizCategoryId;
    }

    public void setBizCategoryId(Long bizCategoryId) {
        this.bizCategoryId = bizCategoryId;
    }

    public String getBizCategoryCode() {
        return this.bizCategoryCode;
    }

    public void setBizCategoryCode(String bizCategoryCode) {
        this.bizCategoryCode = bizCategoryCode;
    }

    public String getTaskNo() {
        return this.taskNo;
    }

    public void setTaskNo(String taskNo) {
        this.taskNo = taskNo;
    }

    public LocalDateTime getRegistDate() {
        return this.registDate;
    }

    public void setRegistDate(LocalDateTime registDate) {
        this.registDate = registDate;
    }

    public String getReportOrgId() {
        return this.reportOrgId;
    }

    public void setReportOrgId(String reportOrgId) {
        this.reportOrgId = reportOrgId;
    }

    public LocalDateTime getAcceptDate() {
        return this.acceptDate;
    }

    public void setAcceptDate(LocalDateTime acceptDate) {
        this.acceptDate = acceptDate;
    }

    public LocalDateTime getCompleDate() {
        return this.compleDate;
    }

    public void setCompleDate(LocalDateTime compleDate) {
        this.compleDate = compleDate;
    }

    public String getMonitorCategory() {
        return this.monitorCategory;
    }

    public void setMonitorCategory(String monitorCategory) {
        this.monitorCategory = monitorCategory;
    }

    public LocalDateTime getEntrustDate() {
        return this.entrustDate;
    }

    public void setEntrustDate(LocalDateTime entrustDate) {
        this.entrustDate = entrustDate;
    }

    public LocalDateTime getSendDate() {
        return this.sendDate;
    }

    public void setSendDate(LocalDateTime sendDate) {
        this.sendDate = sendDate;
    }

    public String getSendType() {
        return this.sendType;
    }

    public void setSendType(String sendType) {
        this.sendType = sendType;
    }

    public String getEntrustMethod() {
        return this.entrustMethod;
    }

    public void setEntrustMethod(String entrustMethod) {
        this.entrustMethod = entrustMethod;
    }

    public String getSubPackageDesc() {
        return this.subPackageDesc;
    }

    public void setSubPackageDesc(String subPackageDesc) {
        this.subPackageDesc = subPackageDesc;
    }

    public String getNdCal() {
        return this.ndCal;
    }

    public void setNdCal(String ndCal) {
        this.ndCal = ndCal;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCancleReason() {
        return this.cancleReason;
    }

    public void setCancleReason(String cancleReason) {
        this.cancleReason = cancleReason;
    }

    public String getSendUsername() {
        return this.sendUsername;
    }

    public void setSendUsername(String sendUsername) {
        this.sendUsername = sendUsername;
    }

    public LocalDateTime getTaskDate() {
        return this.taskDate;
    }

    public void setTaskDate(LocalDateTime taskDate) {
        this.taskDate = taskDate;
    }

    public String getTestContent() {
        return this.testContent;
    }

    public void setTestContent(String testContent) {
        this.testContent = testContent;
    }

    public String getQcReq() {
        return this.qcReq;
    }

    public void setQcReq(String qcReq) {
        this.qcReq = qcReq;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getEntrustOrgContact() {
        return this.entrustOrgContact;
    }

    public void setEntrustOrgContact(String entrustOrgContact) {
        this.entrustOrgContact = entrustOrgContact;
    }

    public String getEntrustOrgPhone() {
        return this.entrustOrgPhone;
    }

    public void setEntrustOrgPhone(String entrustOrgPhone) {
        this.entrustOrgPhone = entrustOrgPhone;
    }

    public String getTestOrgAddress() {
        return this.testOrgAddress;
    }

    public void setTestOrgAddress(String testOrgAddress) {
        this.testOrgAddress = testOrgAddress;
    }

    public String getTestOrgContact() {
        return this.testOrgContact;
    }

    public void setTestOrgContact(String testOrgContact) {
        this.testOrgContact = testOrgContact;
    }

    public String getTestOrgPhone() {
        return this.testOrgPhone;
    }

    public void setTestOrgPhone(String testOrgPhone) {
        this.testOrgPhone = testOrgPhone;
    }

    public String getMonitorOrgName() {
        return this.monitorOrgName;
    }

    public void setMonitorOrgName(String monitorOrgName) {
        this.monitorOrgName = monitorOrgName;
    }

    public String getSource() {
        return this.source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public LocalDateTime getNextTaskDate() {
        return this.nextTaskDate;
    }

    public void setNextTaskDate(LocalDateTime nextTaskDate) {
        this.nextTaskDate = nextTaskDate;
    }

    public LocalDateTime getStartTime() {
        return this.startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return this.endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getCron() {
        return this.cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public String getMobileId() {
        return this.mobileId;
    }

    public void setMobileId(String mobileId) {
        this.mobileId = mobileId;
    }

    public String getProjNode() {
        return this.projNode;
    }

    public void setProjNode(String projNode) {
        this.projNode = projNode;
    }

    public String getSample() {
        return this.sample;
    }

    public void setSample(String sample) {
        this.sample = sample;
    }

    public String getUrgent() {
        return this.urgent;
    }

    public void setUrgent(String urgent) {
        this.urgent = urgent;
    }

    public String getBeforeStatus() {
        return this.beforeStatus;
    }

    public void setBeforeStatus(String beforeStatus) {
        this.beforeStatus = beforeStatus;
    }

    public String getReportEditUserId() {
        return this.reportEditUserId;
    }

    public void setReportEditUserId(String reportEditUserId) {
        this.reportEditUserId = reportEditUserId;
    }

    public String getReportEditUserName() {
        return this.reportEditUserName;
    }

    public void setReportEditUserName(String reportEditUserName) {
        this.reportEditUserName = reportEditUserName;
    }

    public String getQcCheck() {
        return this.qcCheck;
    }

    public void setQcCheck(String qcCheck) {
        this.qcCheck = qcCheck;
    }

    public String getCancelReason() {
        return this.cancelReason;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }

    public String getSchemeReportLineId() {
        return this.schemeReportLineId;
    }

    public void setSchemeReportLineId(String schemeReportLineId) {
        this.schemeReportLineId = schemeReportLineId;
    }

    public String getTestOrgEmail() {
        return this.testOrgEmail;
    }

    public void setTestOrgEmail(String testOrgEmail) {
        this.testOrgEmail = testOrgEmail;
    }

    public String getTestOrgFixedPhone() {
        return this.testOrgFixedPhone;
    }

    public void setTestOrgFixedPhone(String testOrgFixedPhone) {
        this.testOrgFixedPhone = testOrgFixedPhone;
    }

    public String getProjectQcUserId() {
        return this.projectQcUserId;
    }

    public void setProjectQcUserId(String projectQcUserId) {
        this.projectQcUserId = projectQcUserId;
    }

    public String getProjectQcUserName() {
        return this.projectQcUserName;
    }

    public void setProjectQcUserName(String projectQcUserName) {
        this.projectQcUserName = projectQcUserName;
    }

    public String getProjectSampleOrgId() {
        return this.projectSampleOrgId;
    }

    public void setProjectSampleOrgId(String projectSampleOrgId) {
        this.projectSampleOrgId = projectSampleOrgId;
    }

    public String getSampleOrgName() {
        return this.sampleOrgName;
    }

    public void setSampleOrgName(String sampleOrgName) {
        this.sampleOrgName = sampleOrgName;
    }

    public String getProjectSampleById() {
        return this.projectSampleById;
    }

    public void setProjectSampleById(String projectSampleById) {
        this.projectSampleById = projectSampleById;
    }

    public String getProjectSampleByName() {
        return this.projectSampleByName;
    }

    public void setProjectSampleByName(String projectSampleByName) {
        this.projectSampleByName = projectSampleByName;
    }

    public LocalDateTime getProjectSampleBeginDate() {
        return this.projectSampleBeginDate;
    }

    public void setProjectSampleBeginDate(LocalDateTime projectSampleBeginDate) {
        this.projectSampleBeginDate = projectSampleBeginDate;
    }

    public LocalDateTime getProjectSampleEndDate() {
        return this.projectSampleEndDate;
    }

    public void setProjectSampleEndDate(LocalDateTime projectSampleEndDate) {
        this.projectSampleEndDate = projectSampleEndDate;
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

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getKeepDescription() {
        return this.keepDescription;
    }

    public String getRemainSampleDeal() {
        return this.remainSampleDeal;
    }

    public String getTestsMethod() {
        return this.testsMethod;
    }

    public String getServicePay() {
        return this.servicePay;
    }

    public String getInvoiceTitle() {
        return this.invoiceTitle;
    }

    public String getTaxNo() {
        return this.taxNo;
    }

    public String getPayType() {
        return this.payType;
    }

    public String getReportFormat() {
        return this.reportFormat;
    }

    public String getReportNum() {
        return this.reportNum;
    }

    public String getReportGrantTime() {
        return this.reportGrantTime;
    }

    public String getReportSendType() {
        return this.reportSendType;
    }

    public void setKeepDescription(String keepDescription) {
        this.keepDescription = keepDescription;
    }

    public void setRemainSampleDeal(String remainSampleDeal) {
        this.remainSampleDeal = remainSampleDeal;
    }

    public void setTestsMethod(String testsMethod) {
        this.testsMethod = testsMethod;
    }

    public void setServicePay(String servicePay) {
        this.servicePay = servicePay;
    }

    public void setInvoiceTitle(String invoiceTitle) {
        this.invoiceTitle = invoiceTitle;
    }

    public void setTaxNo(String taxNo) {
        this.taxNo = taxNo;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public void setReportFormat(String reportFormat) {
        this.reportFormat = reportFormat;
    }

    public void setReportNum(String reportNum) {
        this.reportNum = reportNum;
    }

    public void setReportGrantTime(String reportGrantTime) {
        this.reportGrantTime = reportGrantTime;
    }

    public void setReportSendType(String reportSendType) {
        this.reportSendType = reportSendType;
    }

    public LocalDateTime getProspectDate() {
        return this.prospectDate;
    }

    public void setProspectDate(LocalDateTime prospectDate) {
        this.prospectDate = prospectDate;
    }

    public String getOpinion() {
        return this.opinion;
    }

    public void setOpinion(String opinion) {
        this.opinion = opinion;
    }

    public String getSafetyMeasures() {
        return this.safetyMeasures;
    }

    public void setSafetyMeasures(String safetyMeasures) {
        this.safetyMeasures = safetyMeasures;
    }

    public String getMonitorCondition() {
        return this.monitorCondition;
    }

    public void setMonitorCondition(String monitorCondition) {
        this.monitorCondition = monitorCondition;
    }

    public String getProspecRemark() {
        return this.prospecRemark;
    }

    public void setProspecRemark(String prospecRemark) {
        this.prospecRemark = prospecRemark;
    }

    public String getProspect() {
        return this.prospect;
    }

    public void setProspect(String prospect) {
        this.prospect = prospect;
    }

    public String getTestCycle() {
        return this.testCycle;
    }

    public void setTestCycle(String testCycle) {
        this.testCycle = testCycle;
    }

    public String getReportDeliveryWay() {
        return this.reportDeliveryWay;
    }

    public void setReportDeliveryWay(String reportDeliveryWay) {
        this.reportDeliveryWay = reportDeliveryWay;
    }

    public String getPackMethod() {
        return this.packMethod;
    }

    public void setPackMethod(String packMethod) {
        this.packMethod = packMethod;
    }

    public String getRequiredSamptime() {
        return this.requiredSamptime;
    }

    public void setRequiredSamptime(String requiredSamptime) {
        this.requiredSamptime = requiredSamptime;
    }

    public String getOrderNum() {
        return this.orderNum;
    }

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }

    public Long getSampleOrderCount() {
        return this.sampleOrderCount;
    }

    public void setSampleOrderCount(Long sampleOrderCount) {
        this.sampleOrderCount = sampleOrderCount;
    }

    public Long getErpGbId() {
        return this.erpGbId;
    }

    public void setErpGbId(Long erpGbId) {
        this.erpGbId = erpGbId;
    }

    public Long getErpReviewId() {
        return this.erpReviewId;
    }

    public void setErpReviewId(Long erpReviewId) {
        this.erpReviewId = erpReviewId;
    }

    public String getReportAptitude() {
        return this.reportAptitude;
    }

    public void setReportAptitude(String reportAptitude) {
        this.reportAptitude = reportAptitude;
    }

    public String getReportUse() {
        return this.reportUse;
    }

    public void setReportUse(String reportUse) {
        this.reportUse = reportUse;
    }

    public String getPretreatment() {
        return this.pretreatment;
    }

    public void setPretreatment(String pretreatment) {
        this.pretreatment = pretreatment;
    }

    public String getNeedQcReport() {
        return this.needQcReport;
    }

    public void setNeedQcReport(String needQcReport) {
        this.needQcReport = needQcReport;
    }

    public String getQcReportType() {
        return this.qcReportType;
    }

    public void setQcReportType(String qcReportType) {
        this.qcReportType = qcReportType;
    }

    public String getNeedSplitReport() {
        return this.needSplitReport;
    }

    public void setNeedSplitReport(String needSplitReport) {
        this.needSplitReport = needSplitReport;
    }

    public String getSubcontractSupplier() {
        return this.subcontractSupplier;
    }

    public void setSubcontractSupplier(String subcontractSupplier) {
        this.subcontractSupplier = subcontractSupplier;
    }

    public String getReportEnName() {
        return this.reportEnName;
    }

    public void setReportEnName(String reportEnName) {
        this.reportEnName = reportEnName;
    }

    public String getReportCnName() {
        return this.reportCnName;
    }

    public void setReportCnName(String reportCnName) {
        this.reportCnName = reportCnName;
    }

    public String getReportEnAddress() {
        return this.reportEnAddress;
    }

    public void setReportEnAddress(String reportEnAddress) {
        this.reportEnAddress = reportEnAddress;
    }

    public String getReportCnAddress() {
        return this.reportCnAddress;
    }

    public void setReportCnAddress(String reportCnAddress) {
        this.reportCnAddress = reportCnAddress;
    }

    public String getTestingLab() {
        return this.testingLab;
    }

    public void setTestingLab(String testingLab) {
        this.testingLab = testingLab;
    }

    public String getTestMethodAssigner() {
        return this.testMethodAssigner;
    }

    public void setTestMethodAssigner(String testMethodAssigner) {
        this.testMethodAssigner = testMethodAssigner;
    }

    public String getMergeReport() {
        return this.mergeReport;
    }

    public void setMergeReport(String mergeReport) {
        this.mergeReport = mergeReport;
    }

    public String getReportPostAddress() {
        return this.reportPostAddress;
    }

    public void setReportPostAddress(String reportPostAddress) {
        this.reportPostAddress = reportPostAddress;
    }

    public String getAddressee() {
        return this.addressee;
    }

    public void setAddressee(String addressee) {
        this.addressee = addressee;
    }

    public String getDetectPurpose() {
        return this.detectPurpose;
    }

    public void setDetectPurpose(String detectPurpose) {
        this.detectPurpose = detectPurpose;
    }

    public String getRecipientPhone() {
        return this.recipientPhone;
    }

    public void setRecipientPhone(String recipientPhone) {
        this.recipientPhone = recipientPhone;
    }

    public String getErpOrderNumber() {
        return this.erpOrderNumber;
    }

    public void setErpOrderNumber(String erpOrderNumber) {
        this.erpOrderNumber = erpOrderNumber;
    }

    public String getReportTitle() {
        return this.reportTitle;
    }

    public void setReportTitle(String reportTitle) {
        this.reportTitle = reportTitle;
    }

    public Long getSplitContractId() {
        return this.splitContractId;
    }

    public void setSplitContractId(Long splitContractId) {
        this.splitContractId = splitContractId;
    }

    public String getSecurityLevel() {
        return this.securityLevel;
    }

    public void setSecurityLevel(String securityLevel) {
        this.securityLevel = securityLevel;
    }

    public String getReportFee() {
        return this.reportFee;
    }

    public void setReportFee(String reportFee) {
        this.reportFee = reportFee;
    }

    public String getNeedReport() {
        return this.needReport;
    }

    public void setNeedReport(String needReport) {
        this.needReport = needReport;
    }

    public String getPeriodType() {
        return this.periodType;
    }

    public void setPeriodType(String periodType) {
        this.periodType = periodType;
    }

    public String getPeriod() {
        return this.period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getOtherReportUse() {
        return this.otherReportUse;
    }

    public void setOtherReportUse(String otherReportUse) {
        this.otherReportUse = otherReportUse;
    }

    public String getErpContactGbId() {
        return this.erpContactGbId;
    }

    public void setErpContactGbId(String erpContactGbId) {
        this.erpContactGbId = erpContactGbId;
    }

    public String getEntrustOrgEmail() {
        return this.entrustOrgEmail;
    }

    public void setEntrustOrgEmail(String entrustOrgEmail) {
        this.entrustOrgEmail = entrustOrgEmail;
    }

    public LocalDateTime getProjCreatedTime() {
        return this.projCreatedTime;
    }

    public void setProjCreatedTime(LocalDateTime projCreatedTime) {
        this.projCreatedTime = projCreatedTime;
    }

    public String getLabInnerNo() {
        return this.labInnerNo;
    }

    public void setLabInnerNo(String labInnerNo) {
        this.labInnerNo = labInnerNo;
    }

    public Long getSubcontractorId() {
        return this.subcontractorId;
    }

    public void setSubcontractorId(Long subcontractorId) {
        this.subcontractorId = subcontractorId;
    }

    public String getProjType() {
        return this.projType;
    }

    public void setProjType(String projType) {
        this.projType = projType;
    }

    public String getProjAmount() {
        return this.projAmount;
    }

    public void setProjAmount(String projAmount) {
        this.projAmount = projAmount;
    }

    public LocalDate getRequiredCompleteDate() {
        return this.requiredCompleteDate;
    }

    public void setRequiredCompleteDate(LocalDate requiredCompleteDate) {
        this.requiredCompleteDate = requiredCompleteDate;
    }

    public String getTestProjectName() {
        return this.testProjectName;
    }

    public void setTestProjectName(String testProjectName) {
        this.testProjectName = testProjectName;
    }

    public String getBusinessUserId() {
        return this.businessUserId;
    }

    public void setBusinessUserId(String businessUserId) {
        this.businessUserId = businessUserId;
    }

    public String getBusinessUserName() {
        return this.businessUserName;
    }

    public void setBusinessUserName(String businessUserName) {
        this.businessUserName = businessUserName;
    }

    public String getMinSampleDuration() {
        return this.minSampleDuration;
    }

    public void setMinSampleDuration(String minSampleDuration) {
        this.minSampleDuration = minSampleDuration;
    }

    public Long getYear() {
        return this.year;
    }

    public void setYear(Long year) {
        this.year = year;
    }

    public LocalDate getToReportEditDate() {
        return this.toReportEditDate;
    }

    public void setToReportEditDate(LocalDate toReportEditDate) {
        this.toReportEditDate = toReportEditDate;
    }

    public String getProjectType() {
        return this.projectType;
    }

    public void setProjectType(String projectType) {
        this.projectType = projectType;
    }

    public LocalDateTime getFirstIssueDateTime() {
        return this.firstIssueDateTime;
    }

    public void setFirstIssueDateTime(LocalDateTime firstIssueDateTime) {
        this.firstIssueDateTime = firstIssueDateTime;
    }

    public String getProjectPrice() {
        return this.projectPrice;
    }

    public void setProjectPrice(String projectPrice) {
        this.projectPrice = projectPrice;
    }

    public String getFolderRemark() {
        return this.folderRemark;
    }

    public void setFolderRemark(String folderRemark) {
        this.folderRemark = folderRemark;
    }

    public String getProjSection() {
        return this.projSection;
    }

    public void setProjSection(String projSection) {
        this.projSection = projSection;
    }

    public String getCustomerSource() {
        return this.customerSource;
    }

    public void setCustomerSource(String customerSource) {
        this.customerSource = customerSource;
    }

    public String getRemarkOne() {
        return this.remarkOne;
    }

    public void setRemarkOne(String remarkOne) {
        this.remarkOne = remarkOne;
    }

    public String getRemarkTwo() {
        return this.remarkTwo;
    }

    public void setRemarkTwo(String remarkTwo) {
        this.remarkTwo = remarkTwo;
    }

    public String getAmountDeterm() {
        return this.amountDeterm;
    }

    public void setAmountDeterm(String amountDeterm) {
        this.amountDeterm = amountDeterm;
    }
}

