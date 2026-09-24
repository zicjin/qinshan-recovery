package com.sunwayworld.emc.module.bp.project.project.bean;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import com.alibaba.fastjson.annotation.JSONField;
import com.sunwayworld.emc.framework.constant.EmcBizConstant;
import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.data.annotation.Clob;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.NotNull;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractAuditable;
import com.sunwayworld.framework.support.domain.Auditable;
import java.time.LocalDate;

/**
 * 项目
 *
 * @author magh@sunwayworld.com
 * @date 2019-09-27
 */

@Table("T_EMC_PROJECT")
    public class EmcProjectBean extends AbstractAuditable<Long> implements Auditable<Long> {

    @Transient
    private static final long serialVersionUID = -8279392325526171185L;

    @Id
    private Long id;// 主键

    private String projNo;// 项目编号

    private String projName;// 项目名称

    private String testOrgId;// 受检单位编码

    private String testOrgName;// 受检单位名称

    private String projAddress;// 项目地址

    private String entrustOrgId;// 委托单位编码

    private String entrustOrgName;// 委托单位名称

    private String entrustOrgAddress;// 委托单位地址

    private Long bizCategoryId;// 业务类型编码

    private String bizCategoryCode;// 业务类型编号

    private String taskNo;// 任务编号

    private String description;//项目描述

    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime registDate;// 登记日期

    private String reportOrgId;// 报告科室编码

    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime acceptDate;// 受理日期

    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime compleDate;// 项目完成期限

    private String monitorCategory;

    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime entrustDate;// 委托日期

    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime sendDate;// 送样日期

    private String sendType;// 送样方式

    private String entrustMethod;// 委托方式

    private String subPackageDesc;// 分包说明

    private String ndCal;// nd计算方式

    @NotNull(defaultValue = EmcBizConstant.PROJECT_STATUS_CREATE)
    private String status;// 状态 正常/取消

    private String cancleReason;// 取消原因

    private String sendUsername;// 送样人

    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime taskDate;// 任务日期

    private String testContent;// 检测内容及要求

    private String qcReq;// 质控要求

    private String remark;// 备注

    private String entrustOrgContact; // 委托单位联系人

    private String entrustOrgPhone; // 委托单位联系电话

    private String testOrgAddress; // 受检单位地址

    private String testOrgContact; // 受检单位联系人

    private String testOrgPhone; // 受检单位联系电话

    private String monitorOrgName; // 检测单位名称

    private String source; // 来源

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime nextTaskDate;// 下次任务日期

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime; // 开始时间

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime; // 结束时间

    private String cron;// CRON表达式

    private String mobileId; // 移动端项目ID

    @NotNull(defaultValue = EmcBizConstant.PROJECT_CN_NODE_XMDJ)
    private String projNode; // 项目节点

    private String sample; // 是否采样

    private String urgent; // 是否加急

    private String beforeStatus; // 项目终止前状态

    private String reportEditUserId; // 报告编制人编码

    private String reportEditUserName; // 报告编制人

    @NotNull(defaultValue = Constant.NO)
    private String qcCheck; // 是否质控考核

    private String cancelReason; // 终止原因

    private String schemeReportLineId; // 监测方案报表模板ID

    private String testOrgEmail; // 受检单位邮箱

    private String testOrgFixedPhone; // 受检单位固定电话

    private String projectQcUserId; // 项目质控人ID

    private String projectQcUserName; // 项目质控人

    private String projectSampleOrgId; // 采样科室id

    private String sampleOrgName; // 采样科室名称

    private String projectSampleById;//采样员编码

    private String projectSampleByName;// 采样员

    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime projectSampleBeginDate;//采样开始日期
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime projectSampleEndDate;//采样结束日期

    private String createdById;// 制单人编码

    private String createdByName;// 制单人名称

    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;// 制单时间

    private String createdByOrgId;// 制单人单位编码

    private String createdByOrgName;// 制单人单位名称

    private String orgId;// 所属单位编码

    private String orgName;// 所属单位名称

    /**
     * 广州分析测试中心
     */
    private String keepDescription;// 样品性质与保存描述
    private String remainSampleDeal;// 余样处理
    private String testsMethod;// 测试方法
    private String servicePay;// 服务费用
    private String invoiceTitle;// 发票抬头及类别
    private String taxNo;// 税号
    private String payType;// 付费方式
    private String reportFormat;// 报告格式
    private String reportNum;// 报告数量
    private String reportGrantTime;// 监测报告发放时间
    private String reportSendType;// 监测报告发送
    /**
     * 现场踏勘功能
     */
    @NotNull(defaultValue = Constant.NO)
    private String prospect; // 是否踏勘
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime prospectDate; // 勘察日期
    @Clob
    private String opinion; // 现场意见
    @Clob
    private String safetyMeasures; // 安全措施
    @Clob
    private String monitorCondition; // 监测条件
    @Clob
    private String prospecRemark;// 踏勘备注

    private String testCycle;//测试周期(江苏新锐)

    private String reportDeliveryWay;//报告交付方式(江苏新锐)

    private String packMethod;//包装方式(江苏新锐)

    private String requiredSamptime;//要求采样时间(广分所)

    private String orderNum;//样品数量(江苏新锐)

    private Long erpGbId; // ERP订单ID
    private Long erpReviewId; // ERP评审ID

    private String reportAptitude; // 报告资质;
    private String reportUse; // 报告用途;
    private String pretreatment; // 前处理方式备注;
    private String needQcReport; // 是否需要提供质控报告;
    private String qcReportType; // 质控报告类型
    private String needSplitReport; // 是否拆分报告;
    private String subcontractSupplier; // 分包供应商名称;
    private String reportEnName; // 报告抬头公司名称(英文);
    private String reportCnName; // 报告抬头公司名称(中文);
    private String reportEnAddress; // 报告抬头公司地址(英文);
    private String reportCnAddress; // 报告抬头公司地址(中文);
    private String testingLab; // 做单实验室;
    private String testMethodAssigner; // 检测方法指定方;
    private String mergeReport; // 分包项目是否合并至微谱报告;
    private String reportPostAddress; // 报告寄送地址;
    private String addressee; // 收件人;
    private String recipientPhone; // 收件人联系电话

    private String detectPurpose; // 检测目的
    private String erpOrderNumber; // ERP订单编号
    private String reportTitle; // 报告抬头
    private Long splitContractId; // 拆分子合同ID

    private String securityLevel; // 保密度
    private String reportFee; // 报告费
    private String needReport; // 是否需要报告
    private String periodType; // 服务周期类型
    private String period; // 服务周期（工作日，服务周期类型为加急或特急时需要填写）
    private String otherReportUse; // 其他报告用途
    private String erpContactGbId; // ERP委托联系人ID
    private String entrustOrgEmail; // 委托邮箱

    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime projCreatedTime; // 用于修改

    private String labInnerNo; // 实验室内部编号

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getProjNo() {
        return projNo;
    }

    public void setProjNo(String projNo) {
        this.projNo = projNo;
    }

    public String getProjName() {
        return projName;
    }

    public void setProjName(String projName) {
        this.projName = projName;
    }

    public String getTestOrgId() {
        return testOrgId;
    }

    public void setTestOrgId(String testOrgId) {
        this.testOrgId = testOrgId;
    }

    public String getTestOrgName() {
        return testOrgName;
    }

    public void setTestOrgName(String testOrgName) {
        this.testOrgName = testOrgName;
    }

    public String getProjAddress() {
        return projAddress;
    }

    public void setProjAddress(String projAddress) {
        this.projAddress = projAddress;
    }

    public String getEntrustOrgId() {
        return entrustOrgId;
    }

    public void setEntrustOrgId(String entrustOrgId) {
        this.entrustOrgId = entrustOrgId;
    }

    public String getEntrustOrgName() {
        return entrustOrgName;
    }

    public void setEntrustOrgName(String entrustOrgName) {
        this.entrustOrgName = entrustOrgName;
    }

    public String getEntrustOrgAddress() {
        return entrustOrgAddress;
    }

    public void setEntrustOrgAddress(String entrustOrgAddress) {
        this.entrustOrgAddress = entrustOrgAddress;
    }

    public Long getBizCategoryId() {
        return bizCategoryId;
    }

    public void setBizCategoryId(Long bizCategoryId) {
        this.bizCategoryId = bizCategoryId;
    }

    public String getBizCategoryCode() {
        return bizCategoryCode;
    }

    public void setBizCategoryCode(String bizCategoryCode) {
        this.bizCategoryCode = bizCategoryCode;
    }

    public String getTaskNo() {
        return taskNo;
    }

    public void setTaskNo(String taskNo) {
        this.taskNo = taskNo;
    }

    public LocalDateTime getRegistDate() {
        return registDate;
    }

    public void setRegistDate(LocalDateTime registDate) {
        this.registDate = registDate;
    }

    public String getReportOrgId() {
        return reportOrgId;
    }

    public void setReportOrgId(String reportOrgId) {
        this.reportOrgId = reportOrgId;
    }

    public LocalDateTime getAcceptDate() {
        return acceptDate;
    }

    public void setAcceptDate(LocalDateTime acceptDate) {
        this.acceptDate = acceptDate;
    }

    public LocalDateTime getCompleDate() {
        return compleDate;
    }

    public void setCompleDate(LocalDateTime compleDate) {
        this.compleDate = compleDate;
    }

    public String getMonitorCategory() {
        return monitorCategory;
    }

    public void setMonitorCategory(String monitorCategory) {
        this.monitorCategory = monitorCategory;
    }

    public LocalDateTime getEntrustDate() {
        return entrustDate;
    }

    public void setEntrustDate(LocalDateTime entrustDate) {
        this.entrustDate = entrustDate;
    }

    public LocalDateTime getSendDate() {
        return sendDate;
    }

    public void setSendDate(LocalDateTime sendDate) {
        this.sendDate = sendDate;
    }

    public String getSendType() {
        return sendType;
    }

    public void setSendType(String sendType) {
        this.sendType = sendType;
    }

    public String getEntrustMethod() {
        return entrustMethod;
    }

    public void setEntrustMethod(String entrustMethod) {
        this.entrustMethod = entrustMethod;
    }

    public String getSubPackageDesc() {
        return subPackageDesc;
    }

    public void setSubPackageDesc(String subPackageDesc) {
        this.subPackageDesc = subPackageDesc;
    }

    public String getNdCal() {
        return ndCal;
    }

    public void setNdCal(String ndCal) {
        this.ndCal = ndCal;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCancleReason() {
        return cancleReason;
    }

    public void setCancleReason(String cancleReason) {
        this.cancleReason = cancleReason;
    }

    public String getSendUsername() {
        return sendUsername;
    }

    public void setSendUsername(String sendUsername) {
        this.sendUsername = sendUsername;
    }

    public LocalDateTime getTaskDate() {
        return taskDate;
    }

    public void setTaskDate(LocalDateTime taskDate) {
        this.taskDate = taskDate;
    }

    public String getTestContent() {
        return testContent;
    }

    public void setTestContent(String testContent) {
        this.testContent = testContent;
    }

    public String getQcReq() {
        return qcReq;
    }

    public void setQcReq(String qcReq) {
        this.qcReq = qcReq;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getEntrustOrgContact() {
        return entrustOrgContact;
    }

    public void setEntrustOrgContact(String entrustOrgContact) {
        this.entrustOrgContact = entrustOrgContact;
    }

    public String getEntrustOrgPhone() {
        return entrustOrgPhone;
    }

    public void setEntrustOrgPhone(String entrustOrgPhone) {
        this.entrustOrgPhone = entrustOrgPhone;
    }

    public String getTestOrgAddress() {
        return testOrgAddress;
    }

    public void setTestOrgAddress(String testOrgAddress) {
        this.testOrgAddress = testOrgAddress;
    }

    public String getTestOrgContact() {
        return testOrgContact;
    }

    public void setTestOrgContact(String testOrgContact) {
        this.testOrgContact = testOrgContact;
    }

    public String getTestOrgPhone() {
        return testOrgPhone;
    }

    public void setTestOrgPhone(String testOrgPhone) {
        this.testOrgPhone = testOrgPhone;
    }

    public String getMonitorOrgName() {
        return monitorOrgName;
    }

    public void setMonitorOrgName(String monitorOrgName) {
        this.monitorOrgName = monitorOrgName;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public LocalDateTime getNextTaskDate() {
        return nextTaskDate;
    }

    public void setNextTaskDate(LocalDateTime nextTaskDate) {
        this.nextTaskDate = nextTaskDate;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public String getMobileId() {
        return mobileId;
    }

    public void setMobileId(String mobileId) {
        this.mobileId = mobileId;
    }

    public String getProjNode() {
        return projNode;
    }

    public void setProjNode(String projNode) {
        this.projNode = projNode;
    }

    public String getSample() {
        return sample;
    }

    public void setSample(String sample) {
        this.sample = sample;
    }

    public String getUrgent() {
        return urgent;
    }

    public void setUrgent(String urgent) {
        this.urgent = urgent;
    }

    public String getBeforeStatus() {
        return beforeStatus;
    }

    public void setBeforeStatus(String beforeStatus) {
        this.beforeStatus = beforeStatus;
    }

    public String getReportEditUserId() {
        return reportEditUserId;
    }

    public void setReportEditUserId(String reportEditUserId) {
        this.reportEditUserId = reportEditUserId;
    }

    public String getReportEditUserName() {
        return reportEditUserName;
    }

    public void setReportEditUserName(String reportEditUserName) {
        this.reportEditUserName = reportEditUserName;
    }

    public String getQcCheck() {
        return qcCheck;
    }

    public void setQcCheck(String qcCheck) {
        this.qcCheck = qcCheck;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }

    public String getSchemeReportLineId() {
        return schemeReportLineId;
    }

    public void setSchemeReportLineId(String schemeReportLineId) {
        this.schemeReportLineId = schemeReportLineId;
    }

    public String getTestOrgEmail() {
        return testOrgEmail;
    }

    public void setTestOrgEmail(String testOrgEmail) {
        this.testOrgEmail = testOrgEmail;
    }

    public String getTestOrgFixedPhone() {
        return testOrgFixedPhone;
    }

    public void setTestOrgFixedPhone(String testOrgFixedPhone) {
        this.testOrgFixedPhone = testOrgFixedPhone;
    }

    public String getProjectQcUserId() {
        return projectQcUserId;
    }

    public void setProjectQcUserId(String projectQcUserId) {
        this.projectQcUserId = projectQcUserId;
    }

    public String getProjectQcUserName() {
        return projectQcUserName;
    }

    public void setProjectQcUserName(String projectQcUserName) {
        this.projectQcUserName = projectQcUserName;
    }

    public String getProjectSampleOrgId() {
        return projectSampleOrgId;
    }

    public void setProjectSampleOrgId(String projectSampleOrgId) {
        this.projectSampleOrgId = projectSampleOrgId;
    }

    public String getSampleOrgName() {
        return sampleOrgName;
    }

    public void setSampleOrgName(String sampleOrgName) {
        this.sampleOrgName = sampleOrgName;
    }

    public String getProjectSampleById() {
        return projectSampleById;
    }

    public void setProjectSampleById(String projectSampleById) {
        this.projectSampleById = projectSampleById;
    }

    public String getProjectSampleByName() {
        return projectSampleByName;
    }

    public void setProjectSampleByName(String projectSampleByName) {
        this.projectSampleByName = projectSampleByName;
    }

    public LocalDateTime getProjectSampleBeginDate() {
        return projectSampleBeginDate;
    }

    public void setProjectSampleBeginDate(LocalDateTime projectSampleBeginDate) {
        this.projectSampleBeginDate = projectSampleBeginDate;
    }

    public LocalDateTime getProjectSampleEndDate() {
        return projectSampleEndDate;
    }

    public void setProjectSampleEndDate(LocalDateTime projectSampleEndDate) {
        this.projectSampleEndDate = projectSampleEndDate;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

    public String getKeepDescription() {
        return keepDescription;
    }

    public String getRemainSampleDeal() {
        return remainSampleDeal;
    }

    public String getTestsMethod() {
        return testsMethod;
    }

    public String getServicePay() {
        return servicePay;
    }

    public String getInvoiceTitle() {
        return invoiceTitle;
    }

    public String getTaxNo() {
        return taxNo;
    }

    public String getPayType() {
        return payType;
    }

    public String getReportFormat() {
        return reportFormat;
    }

    public String getReportNum() {
        return reportNum;
    }

    public String getReportGrantTime() {
        return reportGrantTime;
    }

    public String getReportSendType() {
        return reportSendType;
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
        return prospectDate;
    }

    public void setProspectDate(LocalDateTime prospectDate) {
        this.prospectDate = prospectDate;
    }

    public String getOpinion() {
        return opinion;
    }

    public void setOpinion(String opinion) {
        this.opinion = opinion;
    }

    public String getSafetyMeasures() {
        return safetyMeasures;
    }

    public void setSafetyMeasures(String safetyMeasures) {
        this.safetyMeasures = safetyMeasures;
    }

    public String getMonitorCondition() {
        return monitorCondition;
    }

    public void setMonitorCondition(String monitorCondition) {
        this.monitorCondition = monitorCondition;
    }

    public String getProspecRemark() {
        return prospecRemark;
    }

    public void setProspecRemark(String prospecRemark) {
        this.prospecRemark = prospecRemark;
    }

    public String getProspect() {
        return prospect;
    }

    public void setProspect(String prospect) {
        this.prospect = prospect;
    }

    public String getTestCycle() {
        return testCycle;
    }

    public void setTestCycle(String testCycle) {
        this.testCycle = testCycle;
    }

    public String getReportDeliveryWay() {
        return reportDeliveryWay;
    }

    public void setReportDeliveryWay(String reportDeliveryWay) {
        this.reportDeliveryWay = reportDeliveryWay;
    }

    public String getPackMethod() {
        return packMethod;
    }

    public void setPackMethod(String packMethod) {
        this.packMethod = packMethod;
    }

    public String getRequiredSamptime() {
        return requiredSamptime;
    }

    public void setRequiredSamptime(String requiredSamptime) {
        this.requiredSamptime = requiredSamptime;
    }

    public String getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }

    public Long getErpGbId() {
        return erpGbId;
    }

    public void setErpGbId(Long erpGbId) {
        this.erpGbId = erpGbId;
    }

    public Long getErpReviewId() {
        return erpReviewId;
    }

    public void setErpReviewId(Long erpReviewId) {
        this.erpReviewId = erpReviewId;
    }

    public String getReportAptitude() {
        return reportAptitude;
    }

    public void setReportAptitude(String reportAptitude) {
        this.reportAptitude = reportAptitude;
    }

    public String getReportUse() {
        return reportUse;
    }

    public void setReportUse(String reportUse) {
        this.reportUse = reportUse;
    }

    public String getPretreatment() {
        return pretreatment;
    }

    public void setPretreatment(String pretreatment) {
        this.pretreatment = pretreatment;
    }

    public String getNeedQcReport() {
        return needQcReport;
    }

    public void setNeedQcReport(String needQcReport) {
        this.needQcReport = needQcReport;
    }

    public String getQcReportType() {
        return qcReportType;
    }

    public void setQcReportType(String qcReportType) {
        this.qcReportType = qcReportType;
    }

    public String getNeedSplitReport() {
        return needSplitReport;
    }

    public void setNeedSplitReport(String needSplitReport) {
        this.needSplitReport = needSplitReport;
    }

    public String getSubcontractSupplier() {
        return subcontractSupplier;
    }

    public void setSubcontractSupplier(String subcontractSupplier) {
        this.subcontractSupplier = subcontractSupplier;
    }

    public String getReportEnName() {
        return reportEnName;
    }

    public void setReportEnName(String reportEnName) {
        this.reportEnName = reportEnName;
    }

    public String getReportCnName() {
        return reportCnName;
    }

    public void setReportCnName(String reportCnName) {
        this.reportCnName = reportCnName;
    }

    public String getReportEnAddress() {
        return reportEnAddress;
    }

    public void setReportEnAddress(String reportEnAddress) {
        this.reportEnAddress = reportEnAddress;
    }

    public String getReportCnAddress() {
        return reportCnAddress;
    }

    public void setReportCnAddress(String reportCnAddress) {
        this.reportCnAddress = reportCnAddress;
    }

    public String getTestingLab() {
        return testingLab;
    }

    public void setTestingLab(String testingLab) {
        this.testingLab = testingLab;
    }

    public String getTestMethodAssigner() {
        return testMethodAssigner;
    }

    public void setTestMethodAssigner(String testMethodAssigner) {
        this.testMethodAssigner = testMethodAssigner;
    }

    public String getMergeReport() {
        return mergeReport;
    }

    public void setMergeReport(String mergeReport) {
        this.mergeReport = mergeReport;
    }

    public String getReportPostAddress() {
        return reportPostAddress;
    }

    public void setReportPostAddress(String reportPostAddress) {
        this.reportPostAddress = reportPostAddress;
    }

    public String getAddressee() {
        return addressee;
    }

    public void setAddressee(String addressee) {
        this.addressee = addressee;
    }

    public String getDetectPurpose() {
        return detectPurpose;
    }

    public void setDetectPurpose(String detectPurpose) {
        this.detectPurpose = detectPurpose;
    }

    public String getRecipientPhone() {
        return recipientPhone;
    }

    public void setRecipientPhone(String recipientPhone) {
        this.recipientPhone = recipientPhone;
    }

    public String getErpOrderNumber() {
        return erpOrderNumber;
    }

    public void setErpOrderNumber(String erpOrderNumber) {
        this.erpOrderNumber = erpOrderNumber;
    }

    public String getReportTitle() {
        return reportTitle;
    }

    public void setReportTitle(String reportTitle) {
        this.reportTitle = reportTitle;
    }

    public Long getSplitContractId() {
        return splitContractId;
    }

    public void setSplitContractId(Long splitContractId) {
        this.splitContractId = splitContractId;
    }

    public String getSecurityLevel() {
        return securityLevel;
    }

    public void setSecurityLevel(String securityLevel) {
        this.securityLevel = securityLevel;
    }

    public String getReportFee() {
        return reportFee;
    }

    public void setReportFee(String reportFee) {
        this.reportFee = reportFee;
    }

    public String getNeedReport() {
        return needReport;
    }

    public void setNeedReport(String needReport) {
        this.needReport = needReport;
    }

    public String getPeriodType() {
        return periodType;
    }

    public void setPeriodType(String periodType) {
        this.periodType = periodType;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getOtherReportUse() {
        return otherReportUse;
    }

    public void setOtherReportUse(String otherReportUse) {
        this.otherReportUse = otherReportUse;
    }

    public String getErpContactGbId() {
        return erpContactGbId;
    }

    public void setErpContactGbId(String erpContactGbId) {
        this.erpContactGbId = erpContactGbId;
    }

    public String getEntrustOrgEmail() {
        return entrustOrgEmail;
    }

    public void setEntrustOrgEmail(String entrustOrgEmail) {
        this.entrustOrgEmail = entrustOrgEmail;
    }

    public LocalDateTime getProjCreatedTime() {
        return projCreatedTime;
    }

    public void setProjCreatedTime(LocalDateTime projCreatedTime) {
        this.projCreatedTime = projCreatedTime;
    }

    public String getLabInnerNo() {
        return labInnerNo;
    }

    public void setLabInnerNo(String labInnerNo) {
        this.labInnerNo = labInnerNo;
    }

    private String projAmount;
    private Long sampleOrderCount;
    private Long subcontractorId;
    private String projType;
    private LocalDate requiredCompleteDate;
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime reportIssueTime;
    private String testProjectName;
    private String businessUserId;
    private String businessUserName;
    private String minSampleDuration;
    private Long year;
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate toReportEditDate;
    private String reportAssignStatus;
    private String projectType;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
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
    public Long getSampleOrderCount() {
        return this.sampleOrderCount;
    }
    public void setSampleOrderCount(Long sampleOrderCount) {
        this.sampleOrderCount = sampleOrderCount;
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
