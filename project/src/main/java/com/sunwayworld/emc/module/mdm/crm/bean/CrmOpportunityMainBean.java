package com.sunwayworld.emc.module.mdm.crm.bean;

import java.time.LocalDate;

import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.NotNull;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;

/**
 * 商机基本信息表
 * @author zhengxy@sunwayworld.com 2021年08月26日
 */
@Table("T_CRM_OPPORTUNITY_MAIN")
public class CrmOpportunityMainBean extends AbstractInsertable<String> {

    @Transient
    private static final long serialVersionUID = -3551238616664072223L;

    @Id
    private String id;// 主键
    private String opportunityNo;// 商机单号d
    private String customerId;// 客户ID
    private String customerName;// 客户名称
    private String customerContact;// 客户联系人
    private String customerAddress;// 客户地址
    private String customerPhone;// 客户联系人电话
    private String customerMail;// 客户联系人邮箱
    private String customerCode;// 客户税号
    private String domainId;// 检验领域

    private LocalDate drawdate;// 委托日期
    private String authorizationUnit;// 委托单位
    private String reqsubtyp;// 委托类别
    private String reqCycle;// 委托周期
    private Integer implementationTimes;// 实施次数
    private String maintainSecrecy;// 保密要求
    private String drawArea;// 采样地点
    private String authorizeWay;// 委托方式
    private String getReport;// 报告提交方式
    private String reportCyle;// 报告周期
    private Integer fastestDays;// 最快
    private Integer slowestDays;// 最迟
    private String productionUnit;// 生产单位
    private String productionUnitAdd;// 生产单位地址
    private String reportUnit;// 报告单位
    private String reportUnitAdd;// 报告单位地址
    private String detectionType;// 检测类型
    private String reportPurposes;// 报告用途
    private LocalDate completionDate;// 完成日期
    private LocalDate acceptDate;// 受理日期
    private Double drawAmount;// 委托金额
    private String postalCode;// 邮政编码
    private String accountBank; // 开户行
    private String accountNo;// 开户账号
    private String accountName;// 开户名称
    private String relateBill;// 关联委托单

    private String urgentFlag;// 是否加急
    private String discountRatio;// 折扣系数
    private String sampleSource;// 样品来源
    private String projectType;// 项目类别
    private String collectFlag;// 是否采样
    private String inspectedUnit;// 受检单位
    private String inspectedAddr;// 受检地址
    private String inspectedContcts;// 现场联系人
    private String inspectedPhone;// 现场联系电话
    private String projectName;// 项目名称
    private String corpId;// 所属公司
    private String deptId;// 所属部门
    private String creator;// 录入人
    private String bidder;// 报价人
    private String bidderName; // 报价人名称
    private String corpAddr;// 公司地址
    private String corpCode;// 公司税号
    private String bidderPhone;// 报价人电话
    private String bidderMail;// 报价人邮箱
    private String remark;// 说明
    private String wfProcInstId;// 流程实例Id
    private String wfNodeInstId;// 流程节点实例Id

    private String wfNodeCode;// 流程节点编码
    @NotNull(defaultValue = Constant.NO)
    private String referFlag;// 参照标识
    private Double testingFee; // 检测费用合计
    private Double guideFee; //检测指导价合计
    private Double incidentalFee; // 杂费
    private Double reportFeeRate; // 报告编制(%)
    private Double reportFee; // 报告编制费
    private Double acceptReportFee; // 验收报告编制费
    private Double urgentFee; //加急费
    private Double otherExpenses; // 其他费用
    private Double amountFee; //费用合计
    private Double discountAmount; //优惠金额
    private Double discountRate; //折扣率
    private Double taxRate; //税率
    private Double taxAmount; //税额
    private Double dealAmount; // 成交金额

    private String chooseFlag; // 是否选择
    private String sampleManufacture; // 生产单位
    private String manuAdd; //生产单位地址
    private String reportTitleUnit; // 报告单位
    private String reportTitleUnitAddr; // 报告单位地址
    private String issamplereject; // 余样处置
    private String serviceType; // 服务类型
    private String reportFormat; // 报告版式
    private String reportAim; // 报告用途
    private String judgeFlag; //是否判定
    private String cma; //cma
    private String cnas; //cnas
    private String catl; //catl

    @NotNull(defaultValue = Constant.NO)
    private String contractFlag; //合同标记
    private Long remainingFrequency; //剩余频次
    private String scannedCopyOfContract; //合同扫描件
    private String originalContract; //合同原件
    private String voidFlag; //是否作废
    private String productCategories; // 食品产品类型全民

    private String authorizationUnitShow; //委托单位报告显示
    private String inspectedUnitShow; //受检单位报告显示
    private String entrustededUnitAddShow; //委托单位地址报告显示
    private String projectNameShow; //项目名称报告显示
    private String inspectedAddShow; //受检单位地址报告显示
    private String drawAreaShow; //项目地址报告显示
    private String entrustededUnitAdd ; //委托单位地址
    private String reportContact; //报告联系人
    private String reportPhone; //报告联系电话
    private String reportType; // 报告类型
    private String monitoringCategory;  // 环境监测类别

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public String getOpportunityNo() {
        return opportunityNo;
    }

    public void setOpportunityNo(String opportunityNo) {
        this.opportunityNo = opportunityNo;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerContact() {
        return customerContact;
    }

    public void setCustomerContact(String customerContact) {
        this.customerContact = customerContact;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String getCustomerMail() {
        return customerMail;
    }

    public void setCustomerMail(String customerMail) {
        this.customerMail = customerMail;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    public String getDomainId() {
        return domainId;
    }

    public void setDomainId(String domainId) {
        this.domainId = domainId;
    }

    public LocalDate getDrawdate() {
        return drawdate;
    }

    public void setDrawdate(LocalDate drawdate) {
        this.drawdate = drawdate;
    }

    public String getAuthorizationUnit() {
        return authorizationUnit;
    }

    public void setAuthorizationUnit(String authorizationUnit) {
        this.authorizationUnit = authorizationUnit;
    }

    public String getReqsubtyp() {
        return reqsubtyp;
    }

    public void setReqsubtyp(String reqsubtyp) {
        this.reqsubtyp = reqsubtyp;
    }

    public String getReqCycle() {
        return reqCycle;
    }

    public void setReqCycle(String reqCycle) {
        this.reqCycle = reqCycle;
    }

    public Integer getImplementationTimes() {
        return implementationTimes;
    }

    public void setImplementationTimes(Integer implementationTimes) {
        this.implementationTimes = implementationTimes;
    }

    public String getMaintainSecrecy() {
        return maintainSecrecy;
    }

    public void setMaintainSecrecy(String maintainSecrecy) {
        this.maintainSecrecy = maintainSecrecy;
    }

    public String getDrawArea() {
        return drawArea;
    }

    public void setDrawArea(String drawArea) {
        this.drawArea = drawArea;
    }

    public String getAuthorizeWay() {
        return authorizeWay;
    }

    public void setAuthorizeWay(String authorizeWay) {
        this.authorizeWay = authorizeWay;
    }

    public String getGetReport() {
        return getReport;
    }

    public void setGetReport(String getReport) {
        this.getReport = getReport;
    }

    public String getReportCyle() {
        return reportCyle;
    }

    public void setReportCyle(String reportCyle) {
        this.reportCyle = reportCyle;
    }

    public Integer getFastestDays() {
        return fastestDays;
    }

    public void setFastestDays(Integer fastestDays) {
        this.fastestDays = fastestDays;
    }

    public Integer getSlowestDays() {
        return slowestDays;
    }

    public void setSlowestDays(Integer slowestDays) {
        this.slowestDays = slowestDays;
    }

    public String getProductionUnit() {
        return productionUnit;
    }

    public void setProductionUnit(String productionUnit) {
        this.productionUnit = productionUnit;
    }

    public String getProductionUnitAdd() {
        return productionUnitAdd;
    }

    public void setProductionUnitAdd(String productionUnitAdd) {
        this.productionUnitAdd = productionUnitAdd;
    }

    public String getReportUnit() {
        return reportUnit;
    }

    public void setReportUnit(String reportUnit) {
        this.reportUnit = reportUnit;
    }

    public String getReportUnitAdd() {
        return reportUnitAdd;
    }

    public void setReportUnitAdd(String reportUnitAdd) {
        this.reportUnitAdd = reportUnitAdd;
    }

    public String getDetectionType() {
        return detectionType;
    }

    public void setDetectionType(String detectionType) {
        this.detectionType = detectionType;
    }

    public String getReportPurposes() {
        return reportPurposes;
    }

    public void setReportPurposes(String reportPurposes) {
        this.reportPurposes = reportPurposes;
    }

    public LocalDate getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(LocalDate completionDate) {
        this.completionDate = completionDate;
    }

    public LocalDate getAcceptDate() {
        return acceptDate;
    }

    public void setAcceptDate(LocalDate acceptDate) {
        this.acceptDate = acceptDate;
    }

    public Double getDrawAmount() {
        return drawAmount;
    }

    public void setDrawAmount(Double drawAmount) {
        this.drawAmount = drawAmount;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getAccountBank() {
        return accountBank;
    }

    public void setAccountBank(String accountBank) {
        this.accountBank = accountBank;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getRelateBill() {
        return relateBill;
    }

    public void setRelateBill(String relateBill) {
        this.relateBill = relateBill;
    }

    public String getUrgentFlag() {
        return urgentFlag;
    }

    public void setUrgentFlag(String urgentFlag) {
        this.urgentFlag = urgentFlag;
    }

    public String getDiscountRatio() {
        return discountRatio;
    }

    public void setDiscountRatio(String discountRatio) {
        this.discountRatio = discountRatio;
    }

    public String getSampleSource() {
        return sampleSource;
    }

    public void setSampleSource(String sampleSource) {
        this.sampleSource = sampleSource;
    }

    public String getProjectType() {
        return projectType;
    }

    public void setProjectType(String projectType) {
        this.projectType = projectType;
    }

    public String getCollectFlag() {
        return collectFlag;
    }

    public void setCollectFlag(String collectFlag) {
        this.collectFlag = collectFlag;
    }

    public String getInspectedUnit() {
        return inspectedUnit;
    }

    public void setInspectedUnit(String inspectedUnit) {
        this.inspectedUnit = inspectedUnit;
    }

    public String getInspectedAddr() {
        return inspectedAddr;
    }

    public void setInspectedAddr(String inspectedAddr) {
        this.inspectedAddr = inspectedAddr;
    }

    public String getInspectedContcts() {
        return inspectedContcts;
    }

    public void setInspectedContcts(String inspectedContcts) {
        this.inspectedContcts = inspectedContcts;
    }

    public String getInspectedPhone() {
        return inspectedPhone;
    }

    public void setInspectedPhone(String inspectedPhone) {
        this.inspectedPhone = inspectedPhone;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getCorpId() {
        return corpId;
    }

    public void setCorpId(String corpId) {
        this.corpId = corpId;
    }

    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getBidder() {
        return bidder;
    }

    public void setBidder(String bidder) {
        this.bidder = bidder;
    }

    public String getBidderName() {
        return bidderName;
    }

    public void setBidderName(String bidderName) {
        this.bidderName = bidderName;
    }

    public String getCorpAddr() {
        return corpAddr;
    }

    public void setCorpAddr(String corpAddr) {
        this.corpAddr = corpAddr;
    }

    public String getCorpCode() {
        return corpCode;
    }

    public void setCorpCode(String corpCode) {
        this.corpCode = corpCode;
    }

    public String getBidderPhone() {
        return bidderPhone;
    }

    public void setBidderPhone(String bidderPhone) {
        this.bidderPhone = bidderPhone;
    }

    public String getBidderMail() {
        return bidderMail;
    }

    public void setBidderMail(String bidderMail) {
        this.bidderMail = bidderMail;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getWfProcInstId() {
        return wfProcInstId;
    }

    public void setWfProcInstId(String wfProcInstId) {
        this.wfProcInstId = wfProcInstId;
    }

    public String getWfNodeInstId() {
        return wfNodeInstId;
    }

    public void setWfNodeInstId(String wfNodeInstId) {
        this.wfNodeInstId = wfNodeInstId;
    }

    public String getWfNodeCode() {
        return wfNodeCode;
    }

    public void setWfNodeCode(String wfNodeCode) {
        this.wfNodeCode = wfNodeCode;
    }

    public String getReferFlag() {
        return referFlag;
    }

    public void setReferFlag(String referFlag) {
        this.referFlag = referFlag;
    }

    public Double getTestingFee() {
        return testingFee;
    }

    public void setTestingFee(Double testingFee) {
        this.testingFee = testingFee;
    }

    public Double getGuideFee() {
        return guideFee;
    }

    public void setGuideFee(Double guideFee) {
        this.guideFee = guideFee;
    }

    public Double getIncidentalFee() {
        return incidentalFee;
    }

    public void setIncidentalFee(Double incidentalFee) {
        this.incidentalFee = incidentalFee;
    }

    public Double getReportFeeRate() {
        return reportFeeRate;
    }

    public void setReportFeeRate(Double reportFeeRate) {
        this.reportFeeRate = reportFeeRate;
    }

    public Double getReportFee() {
        return reportFee;
    }

    public void setReportFee(Double reportFee) {
        this.reportFee = reportFee;
    }

    public Double getAcceptReportFee() {
        return acceptReportFee;
    }

    public void setAcceptReportFee(Double acceptReportFee) {
        this.acceptReportFee = acceptReportFee;
    }

    public Double getUrgentFee() {
        return urgentFee;
    }

    public void setUrgentFee(Double urgentFee) {
        this.urgentFee = urgentFee;
    }

    public Double getOtherExpenses() {
        return otherExpenses;
    }

    public void setOtherExpenses(Double otherExpenses) {
        this.otherExpenses = otherExpenses;
    }

    public Double getAmountFee() {
        return amountFee;
    }

    public void setAmountFee(Double amountFee) {
        this.amountFee = amountFee;
    }

    public Double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(Double discountAmount) {
        this.discountAmount = discountAmount;
    }

    public Double getDiscountRate() {
        return discountRate;
    }

    public void setDiscountRate(Double discountRate) {
        this.discountRate = discountRate;
    }

    public Double getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(Double taxRate) {
        this.taxRate = taxRate;
    }

    public Double getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(Double taxAmount) {
        this.taxAmount = taxAmount;
    }

    public Double getDealAmount() {
        return dealAmount;
    }

    public void setDealAmount(Double dealAmount) {
        this.dealAmount = dealAmount;
    }

    public String getChooseFlag() {
        return chooseFlag;
    }

    public void setChooseFlag(String chooseFlag) {
        this.chooseFlag = chooseFlag;
    }

    public String getSampleManufacture() {
        return sampleManufacture;
    }

    public void setSampleManufacture(String sampleManufacture) {
        this.sampleManufacture = sampleManufacture;
    }

    public String getManuAdd() {
        return manuAdd;
    }

    public void setManuAdd(String manuAdd) {
        this.manuAdd = manuAdd;
    }

    public String getReportTitleUnit() {
        return reportTitleUnit;
    }

    public void setReportTitleUnit(String reportTitleUnit) {
        this.reportTitleUnit = reportTitleUnit;
    }

    public String getReportTitleUnitAddr() {
        return reportTitleUnitAddr;
    }

    public void setReportTitleUnitAddr(String reportTitleUnitAddr) {
        this.reportTitleUnitAddr = reportTitleUnitAddr;
    }

    public String getIssamplereject() {
        return issamplereject;
    }

    public void setIssamplereject(String issamplereject) {
        this.issamplereject = issamplereject;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getReportFormat() {
        return reportFormat;
    }

    public void setReportFormat(String reportFormat) {
        this.reportFormat = reportFormat;
    }

    public String getReportAim() {
        return reportAim;
    }

    public void setReportAim(String reportAim) {
        this.reportAim = reportAim;
    }

    public String getJudgeFlag() {
        return judgeFlag;
    }

    public void setJudgeFlag(String judgeFlag) {
        this.judgeFlag = judgeFlag;
    }

    public String getCma() {
        return cma;
    }

    public void setCma(String cma) {
        this.cma = cma;
    }

    public String getCnas() {
        return cnas;
    }

    public void setCnas(String cnas) {
        this.cnas = cnas;
    }

    public String getCatl() {
        return catl;
    }

    public void setCatl(String catl) {
        this.catl = catl;
    }

    public String getContractFlag() {
        return contractFlag;
    }

    public void setContractFlag(String contractFlag) {
        this.contractFlag = contractFlag;
    }

    public Long getRemainingFrequency() {
        return remainingFrequency;
    }

    public void setRemainingFrequency(Long remainingFrequency) {
        this.remainingFrequency = remainingFrequency;
    }

    public String getScannedCopyOfContract() {
        return scannedCopyOfContract;
    }

    public void setScannedCopyOfContract(String scannedCopyOfContract) {
        this.scannedCopyOfContract = scannedCopyOfContract;
    }

    public String getOriginalContract() {
        return originalContract;
    }

    public void setOriginalContract(String originalContract) {
        this.originalContract = originalContract;
    }

    public String getVoidFlag() {
        return voidFlag;
    }

    public void setVoidFlag(String voidFlag) {
        this.voidFlag = voidFlag;
    }

    public String getProductCategories() {
        return productCategories;
    }

    public void setProductCategories(String productCategories) {
        this.productCategories = productCategories;
    }

    public String getAuthorizationUnitShow() {
        return authorizationUnitShow;
    }

    public void setAuthorizationUnitShow(String authorizationUnitShow) {
        this.authorizationUnitShow = authorizationUnitShow;
    }

    public String getInspectedUnitShow() {
        return inspectedUnitShow;
    }

    public void setInspectedUnitShow(String inspectedUnitShow) {
        this.inspectedUnitShow = inspectedUnitShow;
    }

    public String getEntrustededUnitAddShow() {
        return entrustededUnitAddShow;
    }

    public void setEntrustededUnitAddShow(String entrustededUnitAddShow) {
        this.entrustededUnitAddShow = entrustededUnitAddShow;
    }

    public String getProjectNameShow() {
        return projectNameShow;
    }

    public void setProjectNameShow(String projectNameShow) {
        this.projectNameShow = projectNameShow;
    }

    public String getInspectedAddShow() {
        return inspectedAddShow;
    }

    public void setInspectedAddShow(String inspectedAddShow) {
        this.inspectedAddShow = inspectedAddShow;
    }

    public String getDrawAreaShow() {
        return drawAreaShow;
    }

    public void setDrawAreaShow(String drawAreaShow) {
        this.drawAreaShow = drawAreaShow;
    }

    public String getEntrustededUnitAdd() {
        return entrustededUnitAdd;
    }

    public void setEntrustededUnitAdd(String entrustededUnitAdd) {
        this.entrustededUnitAdd = entrustededUnitAdd;
    }

    public String getReportContact() {
        return reportContact;
    }

    public void setReportContact(String reportContact) {
        this.reportContact = reportContact;
    }

    public String getReportPhone() {
        return reportPhone;
    }

    public void setReportPhone(String reportPhone) {
        this.reportPhone = reportPhone;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public String getMonitoringCategory() {
        return monitoringCategory;
    }

    public void setMonitoringCategory(String monitoringCategory) {
        this.monitoringCategory = monitoringCategory;
    }
}
