package com.sunwayworld.emc.module.mdm.crm.bean;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.format.annotation.DateTimeFormat;

import com.sunwayworld.framework.data.annotation.Clob;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;

/**
 * 商机明细表
 * @author zhengxy@sunwayworld.com 2021年08月26日
 */
@Table("T_CRM_OPPORTUNITY_ORDTASK")
public class CrmOpportunityOrdtaskBean extends AbstractInsertable<String> {

    @Transient
    private static final long serialVersionUID = -4351450239071223365L;

    @Id
    private String id;// 主键
    private LocalDate apprDate;// 校对日期
    private String apprvedBy;// 实际校对人
    private String drawNo;// 抽样方案
    private String opportunityNo;// 样品编号
    private Long methodReference;// 方法说明
    private String needRun;// 需要流水号
    private String notebookReference;// 方法备用说明
    private String ordNo;// 样号
    private String pageNo;// eln显示页
    private String pareantOrdNo;// 父样号
    private Double priceAfterDisc;// 折后费用
    private String profile;// 方案
    private String spCode;// 模板号
    private String testCode;// 检测项目代码
    private String testGroupName;// 检测项目类型
    private String testNo;// 检测项目
    private String usrNam;// 测试人
    private Double spTestSorter;// 序号
    private String elnReference;// eln链接
    private String method;// eln模板名称(原：方法）
    private String retestFlag;// 重测标识
    private String elnVersionId;// eln模板号
    private String spTestno;// 报告书名称
    private Double addPrice;// 方法费用
    private String fullName;// 测试人名称
    private LocalDate assignDate;// 指派时间
    private String mainUsrNam;// 主检人
    private String checkUsrNam;// 校对人
    private String checkFullName;// 校对人名称
    private String apprvedByUsrNam;// 审核人
    private LocalDate submitDate;// 提交时间
    private String submitUsrNam;// 提交人
    private String submitFullName;// 提交人
    private String elnPdfRawfile;// 原始记录路径
    private String elnType;// 报告类型
    private String displayFlag;// 是否在报告书中显示
    private String priceSorter;// 价格排序
    private Long pageCount;// eln页数
    private Double price;// 费用
    private String testCriterion;// 检验方法(原：检测依据）
    private LocalDate elnOpenDate;// eln打开时间
    private String elnPdfAttachment;// eln附件
    private String elnFlag;// 不使用eln
    private String copyFlag;// 是否是复制eln
    private LocalDate firstAssignDate;// 首次指派时间
    private Long commonOrigrec;// 公共origrec 合并检测项目使用
    private String productCategories;// 检验项目类型
    private String degrees;// 等级
    private String reportType;// 报告类型
    private String isElnSave;// 是否保存eln
    private String cma;// cma认证
    private String cnas;// cnas认证
    private String partner;// 合作人id
    private Double nodisPrice;// 折扣前价格
    private Double discount;// 折扣系数
    private String newTestcriterion;// 下达检测依据
    private String retainUsrName;// 检验人2
    private String sdComments;// 备注
    private String orgId;// 科室id
    private String orgCode;// 科室代码
    private String orgName;// 科室名称
    private String ordersId;// 样号信息id
    private String corpId;// 一级单位id
    private String corpCode;// 一级单位代码
    private String corpName;// 一级单位名称
    private String sourceTestsId;// 合并项目id
    private String foldersId;// 检品主键
    private String wfProcInstId;// 流程实例内码
    private String wfNodeCode;// 当前审批节点编码
    private String wfNodeName;// 当前审批节点名称
    private String qcType;// qc类型
    private String wfNodeInstId;// 当前节点实例内码
    private String partnerName;// 合作人姓名
    private String mainUserName;// 主检人姓名
    private Double sorter;// 排序码
    private String methodVersion;// 版本
    private String elnRawfile;// eln源文件路径
    private String elnRawfileFlag;// eln源文件记录标识，用于审计追踪
    private String modifyRecordFlag;// 是否记录eln审计追踪
    private String runId;// 批内码
    private Long wfBackwardFlag;// 节点退回标记
    private Double nodisAddPrice;// 打折前附加费
    private String pdfReport;// pdf原始记录路径
    private String appComments;// 审核意见
    private String createdById;// 制单人编码
    private String createdByName;// 制单人名称
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;// 制单时间
    private String createdByOrgId;// 制单人单位编码
    private String createdByOrgName;// 制单人单位名称
    private String ordtestRules;// 项目通则
    private String remark;// 备注
    private String parentId;// 父项目id
    private Long lvl;// 项目级别
    private Long testOperationNum;// 实验操作数
    private Long mobilePhaseNum;// 流动相数
    private String elnRawfileSha;// eln原文件sha值
    private String units;// 单位
    private String mergeStatus;// pdf文件合成状态(0:未合成 1:合成中 2:合成完成)
    @Clob
    private String serialDataContext;// 串口采集文本信息
    private Long subOrderNo;// 子序号
    private String multiTest;// 多次测试标志1是0否
    private String templateId;// 检品模板id
    private String wordReportPath;// word报告pdfreportfileid
    private String pdfForwordReportPath;// word报告转换的pdf文件
    private String funcCategory;// 功能类别
    private String testGrouporgId;// 检验小组id
    private String testGrouporgName;// 检验小组名称
    private LocalDate testGrouporgDate;// 指派检验小组时间
    private LocalDate dueDate;// 检验时限
    private String addNotes;// 附加说明
    private String testsFullName;// 检验项目全名称
    private String testsId;// 检验项目id
    private String retestReason;// 重测原因
    private String testCriterionCode;// 标准号
    private String testCriterionClause;// 条款
    private String progName;// 检品模板名称
    private String isApply;// 申报情况(默认值为0，0：未申报，1：已申报)
    private String catl;// catl
    private String testingType;// 检测类别
    private String testingPosition;// 检测点位
    private Long positionCount;// 点位数量
    private Long samplePoints;// 样品/点
    private Long testingFrequency;// 频次/天
    private Long testingDays;// 监测天数
    private Long sampleCounts;// 样品总数
    private String spCodeId;// 检品模板ID
    private String spTestId;
    private String TestStyleId;// 检测项目ID
    private String orign;// 来源(选择检品模板时，标识为1，选择通过标准库时，标识为0)
    private String executiveStandard; // 执行标准
    private Double unitPrice; //单价
    private Long numberOfInspections; //检测次数
    private String testRule; // 检验依据
    private String testMethods; //执行标准
    private Long count; //数量
    private Double totalAmount; // 合计
    private Double guidePrice; //原单价
    private String analyte; //分析项
    private String unit; // 单位
    private String fee; // 费用
    private String criterion; //技术要求
    private String lowLimit; //低限
    private String highLimit; //高限
    private String qrules; //规则
    private String schemeName; //套餐名称
    private String monitorId; //环境视图id

    private String indexId;// 指标id
    private String indexCategoryId;// 指标名称id
    private String indexName;// 指标名称
    private String index1;// 分类1
    private String index2;// 分类2
    private String index3;// 分类3
    private String index4;// 分类4
    private String index5;// 分类5
    private String index6;// 分类6
    private String subcontract; //分包
    private String itemGroup; // 分析项组
    private String testName;
    private String methodName;
    private String subcontract2; // 采样前分包

    private String stdTeststyleId;
    private String firstPackage;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public LocalDate getApprDate() {
        return apprDate;
    }

    public void setApprDate(LocalDate apprDate) {
        this.apprDate = apprDate;
    }

    public String getApprvedBy() {
        return apprvedBy;
    }

    public void setApprvedBy(String apprvedBy) {
        this.apprvedBy = apprvedBy;
    }

    public String getDrawNo() {
        return drawNo;
    }

    public void setDrawNo(String drawNo) {
        this.drawNo = drawNo;
    }

    public String getOpportunityNo() {
        return opportunityNo;
    }

    public void setOpportunityNo(String opportunityNo) {
        this.opportunityNo = opportunityNo;
    }

    public Long getMethodReference() {
        return methodReference;
    }

    public void setMethodReference(Long methodReference) {
        this.methodReference = methodReference;
    }

    public String getNeedRun() {
        return needRun;
    }

    public void setNeedRun(String needRun) {
        this.needRun = needRun;
    }

    public String getNotebookReference() {
        return notebookReference;
    }

    public void setNotebookReference(String notebookReference) {
        this.notebookReference = notebookReference;
    }

    public String getOrdNo() {
        return ordNo;
    }

    public void setOrdNo(String ordNo) {
        this.ordNo = ordNo;
    }

    public String getPageNo() {
        return pageNo;
    }

    public void setPageNo(String pageNo) {
        this.pageNo = pageNo;
    }

    public String getPareantOrdNo() {
        return pareantOrdNo;
    }

    public void setPareantOrdNo(String pareantOrdNo) {
        this.pareantOrdNo = pareantOrdNo;
    }

    public Double getPriceAfterDisc() {
        return priceAfterDisc;
    }

    public void setPriceAfterDisc(Double priceAfterDisc) {
        this.priceAfterDisc = priceAfterDisc;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getSpCode() {
        return spCode;
    }

    public void setSpCode(String spCode) {
        this.spCode = spCode;
    }

    public String getTestCode() {
        return testCode;
    }

    public void setTestCode(String testCode) {
        this.testCode = testCode;
    }

    public String getTestGroupName() {
        return testGroupName;
    }

    public void setTestGroupName(String testGroupName) {
        this.testGroupName = testGroupName;
    }

    public String getTestNo() {
        return testNo;
    }

    public void setTestNo(String testNo) {
        this.testNo = testNo;
    }

    public String getUsrNam() {
        return usrNam;
    }

    public void setUsrNam(String usrNam) {
        this.usrNam = usrNam;
    }

    public Double getSpTestSorter() {
        return spTestSorter;
    }

    public void setSpTestSorter(Double spTestSorter) {
        this.spTestSorter = spTestSorter;
    }

    public String getElnReference() {
        return elnReference;
    }

    public void setElnReference(String elnReference) {
        this.elnReference = elnReference;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getRetestFlag() {
        return retestFlag;
    }

    public void setRetestFlag(String retestFlag) {
        this.retestFlag = retestFlag;
    }

    public String getElnVersionId() {
        return elnVersionId;
    }

    public void setElnVersionId(String elnVersionId) {
        this.elnVersionId = elnVersionId;
    }

    public String getSpTestno() {
        return spTestno;
    }

    public void setSpTestno(String spTestno) {
        this.spTestno = spTestno;
    }

    public Double getAddPrice() {
        return addPrice;
    }

    public void setAddPrice(Double addPrice) {
        this.addPrice = addPrice;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public LocalDate getAssignDate() {
        return assignDate;
    }

    public void setAssignDate(LocalDate assignDate) {
        this.assignDate = assignDate;
    }

    public String getMainUsrNam() {
        return mainUsrNam;
    }

    public void setMainUsrNam(String mainUsrNam) {
        this.mainUsrNam = mainUsrNam;
    }

    public String getCheckUsrNam() {
        return checkUsrNam;
    }

    public void setCheckUsrNam(String checkUsrNam) {
        this.checkUsrNam = checkUsrNam;
    }

    public String getCheckFullName() {
        return checkFullName;
    }

    public void setCheckFullName(String checkFullName) {
        this.checkFullName = checkFullName;
    }

    public String getApprvedByUsrNam() {
        return apprvedByUsrNam;
    }

    public void setApprvedByUsrNam(String apprvedByUsrNam) {
        this.apprvedByUsrNam = apprvedByUsrNam;
    }

    public LocalDate getSubmitDate() {
        return submitDate;
    }

    public void setSubmitDate(LocalDate submitDate) {
        this.submitDate = submitDate;
    }

    public String getSubmitUsrNam() {
        return submitUsrNam;
    }

    public void setSubmitUsrNam(String submitUsrNam) {
        this.submitUsrNam = submitUsrNam;
    }

    public String getSubmitFullName() {
        return submitFullName;
    }

    public void setSubmitFullName(String submitFullName) {
        this.submitFullName = submitFullName;
    }

    public String getElnPdfRawfile() {
        return elnPdfRawfile;
    }

    public void setElnPdfRawfile(String elnPdfRawfile) {
        this.elnPdfRawfile = elnPdfRawfile;
    }

    public String getElnType() {
        return elnType;
    }

    public void setElnType(String elnType) {
        this.elnType = elnType;
    }

    public String getDisplayFlag() {
        return displayFlag;
    }

    public void setDisplayFlag(String displayFlag) {
        this.displayFlag = displayFlag;
    }

    public String getPriceSorter() {
        return priceSorter;
    }

    public void setPriceSorter(String priceSorter) {
        this.priceSorter = priceSorter;
    }

    public Long getPageCount() {
        return pageCount;
    }

    public void setPageCount(Long pageCount) {
        this.pageCount = pageCount;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getTestCriterion() {
        return testCriterion;
    }

    public void setTestCriterion(String testCriterion) {
        this.testCriterion = testCriterion;
    }

    public LocalDate getElnOpenDate() {
        return elnOpenDate;
    }

    public void setElnOpenDate(LocalDate elnOpenDate) {
        this.elnOpenDate = elnOpenDate;
    }

    public String getElnPdfAttachment() {
        return elnPdfAttachment;
    }

    public void setElnPdfAttachment(String elnPdfAttachment) {
        this.elnPdfAttachment = elnPdfAttachment;
    }

    public String getElnFlag() {
        return elnFlag;
    }

    public void setElnFlag(String elnFlag) {
        this.elnFlag = elnFlag;
    }

    public String getCopyFlag() {
        return copyFlag;
    }

    public void setCopyFlag(String copyFlag) {
        this.copyFlag = copyFlag;
    }

    public LocalDate getFirstAssignDate() {
        return firstAssignDate;
    }

    public void setFirstAssignDate(LocalDate firstAssignDate) {
        this.firstAssignDate = firstAssignDate;
    }

    public Long getCommonOrigrec() {
        return commonOrigrec;
    }

    public void setCommonOrigrec(Long commonOrigrec) {
        this.commonOrigrec = commonOrigrec;
    }

    public String getProductCategories() {
        return productCategories;
    }

    public void setProductCategories(String productCategories) {
        this.productCategories = productCategories;
    }

    public String getDegrees() {
        return degrees;
    }

    public void setDegrees(String degrees) {
        this.degrees = degrees;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public String getIsElnSave() {
        return isElnSave;
    }

    public void setIsElnSave(String isElnSave) {
        this.isElnSave = isElnSave;
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

    public String getPartner() {
        return partner;
    }

    public void setPartner(String partner) {
        this.partner = partner;
    }

    public Double getNodisPrice() {
        return nodisPrice;
    }

    public void setNodisPrice(Double nodisPrice) {
        this.nodisPrice = nodisPrice;
    }

    public Double getDiscount() {
        return discount;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
    }

    public String getNewTestcriterion() {
        return newTestcriterion;
    }

    public void setNewTestcriterion(String newTestcriterion) {
        this.newTestcriterion = newTestcriterion;
    }

    public String getRetainUsrName() {
        return retainUsrName;
    }

    public void setRetainUsrName(String retainUsrName) {
        this.retainUsrName = retainUsrName;
    }

    public String getSdComments() {
        return sdComments;
    }

    public void setSdComments(String sdComments) {
        this.sdComments = sdComments;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getOrdersId() {
        return ordersId;
    }

    public void setOrdersId(String ordersId) {
        this.ordersId = ordersId;
    }

    public String getCorpId() {
        return corpId;
    }

    public void setCorpId(String corpId) {
        this.corpId = corpId;
    }

    public String getCorpCode() {
        return corpCode;
    }

    public void setCorpCode(String corpCode) {
        this.corpCode = corpCode;
    }

    public String getCorpName() {
        return corpName;
    }

    public void setCorpName(String corpName) {
        this.corpName = corpName;
    }

    public String getSourceTestsId() {
        return sourceTestsId;
    }

    public void setSourceTestsId(String sourceTestsId) {
        this.sourceTestsId = sourceTestsId;
    }

    public String getFoldersId() {
        return foldersId;
    }

    public void setFoldersId(String foldersId) {
        this.foldersId = foldersId;
    }

    public String getWfProcInstId() {
        return wfProcInstId;
    }

    public void setWfProcInstId(String wfProcInstId) {
        this.wfProcInstId = wfProcInstId;
    }

    public String getWfNodeCode() {
        return wfNodeCode;
    }

    public void setWfNodeCode(String wfNodeCode) {
        this.wfNodeCode = wfNodeCode;
    }

    public String getWfNodeName() {
        return wfNodeName;
    }

    public void setWfNodeName(String wfNodeName) {
        this.wfNodeName = wfNodeName;
    }

    public String getQcType() {
        return qcType;
    }

    public void setQcType(String qcType) {
        this.qcType = qcType;
    }

    public String getWfNodeInstId() {
        return wfNodeInstId;
    }

    public void setWfNodeInstId(String wfNodeInstId) {
        this.wfNodeInstId = wfNodeInstId;
    }

    public String getPartnerName() {
        return partnerName;
    }

    public void setPartnerName(String partnerName) {
        this.partnerName = partnerName;
    }

    public String getMainUserName() {
        return mainUserName;
    }

    public void setMainUserName(String mainUserName) {
        this.mainUserName = mainUserName;
    }

    public Double getSorter() {
        return sorter;
    }

    public void setSorter(Double sorter) {
        this.sorter = sorter;
    }

    public String getMethodVersion() {
        return methodVersion;
    }

    public void setMethodVersion(String methodVersion) {
        this.methodVersion = methodVersion;
    }

    public String getElnRawfile() {
        return elnRawfile;
    }

    public void setElnRawfile(String elnRawfile) {
        this.elnRawfile = elnRawfile;
    }

    public String getElnRawfileFlag() {
        return elnRawfileFlag;
    }

    public void setElnRawfileFlag(String elnRawfileFlag) {
        this.elnRawfileFlag = elnRawfileFlag;
    }

    public String getModifyRecordFlag() {
        return modifyRecordFlag;
    }

    public void setModifyRecordFlag(String modifyRecordFlag) {
        this.modifyRecordFlag = modifyRecordFlag;
    }

    public String getRunId() {
        return runId;
    }

    public void setRunId(String runId) {
        this.runId = runId;
    }

    public Long getWfBackwardFlag() {
        return wfBackwardFlag;
    }

    public void setWfBackwardFlag(Long wfBackwardFlag) {
        this.wfBackwardFlag = wfBackwardFlag;
    }

    public Double getNodisAddPrice() {
        return nodisAddPrice;
    }

    public void setNodisAddPrice(Double nodisAddPrice) {
        this.nodisAddPrice = nodisAddPrice;
    }

    public String getPdfReport() {
        return pdfReport;
    }

    public void setPdfReport(String pdfReport) {
        this.pdfReport = pdfReport;
    }

    public String getAppComments() {
        return appComments;
    }

    public void setAppComments(String appComments) {
        this.appComments = appComments;
    }

    @Override
    public String getCreatedById() {
        return createdById;
    }

    @Override
    public void setCreatedById(String createdById) {
        this.createdById = createdById;
    }

    @Override
    public String getCreatedByName() {
        return createdByName;
    }

    @Override
    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    @Override
    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    @Override
    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    @Override
    public String getCreatedByOrgId() {
        return createdByOrgId;
    }

    @Override
    public void setCreatedByOrgId(String createdByOrgId) {
        this.createdByOrgId = createdByOrgId;
    }

    @Override
    public String getCreatedByOrgName() {
        return createdByOrgName;
    }

    @Override
    public void setCreatedByOrgName(String createdByOrgName) {
        this.createdByOrgName = createdByOrgName;
    }

    public String getOrdtestRules() {
        return ordtestRules;
    }

    public void setOrdtestRules(String ordtestRules) {
        this.ordtestRules = ordtestRules;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public Long getLvl() {
        return lvl;
    }

    public void setLvl(Long lvl) {
        this.lvl = lvl;
    }

    public Long getTestOperationNum() {
        return testOperationNum;
    }

    public void setTestOperationNum(Long testOperationNum) {
        this.testOperationNum = testOperationNum;
    }

    public Long getMobilePhaseNum() {
        return mobilePhaseNum;
    }

    public void setMobilePhaseNum(Long mobilePhaseNum) {
        this.mobilePhaseNum = mobilePhaseNum;
    }

    public String getElnRawfileSha() {
        return elnRawfileSha;
    }

    public void setElnRawfileSha(String elnRawfileSha) {
        this.elnRawfileSha = elnRawfileSha;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    public String getMergeStatus() {
        return mergeStatus;
    }

    public void setMergeStatus(String mergeStatus) {
        this.mergeStatus = mergeStatus;
    }

    public String getSerialDataContext() {
        return serialDataContext;
    }

    public void setSerialDataContext(String serialDataContext) {
        this.serialDataContext = serialDataContext;
    }

    public Long getSubOrderNo() {
        return subOrderNo;
    }

    public void setSubOrderNo(Long subOrderNo) {
        this.subOrderNo = subOrderNo;
    }

    public String getMultiTest() {
        return multiTest;
    }

    public void setMultiTest(String multiTest) {
        this.multiTest = multiTest;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public String getWordReportPath() {
        return wordReportPath;
    }

    public void setWordReportPath(String wordReportPath) {
        this.wordReportPath = wordReportPath;
    }

    public String getPdfForwordReportPath() {
        return pdfForwordReportPath;
    }

    public void setPdfForwordReportPath(String pdfForwordReportPath) {
        this.pdfForwordReportPath = pdfForwordReportPath;
    }

    public String getFuncCategory() {
        return funcCategory;
    }

    public void setFuncCategory(String funcCategory) {
        this.funcCategory = funcCategory;
    }

    public String getTestGrouporgId() {
        return testGrouporgId;
    }

    public void setTestGrouporgId(String testGrouporgId) {
        this.testGrouporgId = testGrouporgId;
    }

    public String getTestGrouporgName() {
        return testGrouporgName;
    }

    public void setTestGrouporgName(String testGrouporgName) {
        this.testGrouporgName = testGrouporgName;
    }

    public LocalDate getTestGrouporgDate() {
        return testGrouporgDate;
    }

    public void setTestGrouporgDate(LocalDate testGrouporgDate) {
        this.testGrouporgDate = testGrouporgDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public String getAddNotes() {
        return addNotes;
    }

    public void setAddNotes(String addNotes) {
        this.addNotes = addNotes;
    }

    public String getTestsFullName() {
        return testsFullName;
    }

    public void setTestsFullName(String testsFullName) {
        this.testsFullName = testsFullName;
    }

    public String getTestsId() {
        return testsId;
    }

    public void setTestsId(String testsId) {
        this.testsId = testsId;
    }

    public String getRetestReason() {
        return retestReason;
    }

    public void setRetestReason(String retestReason) {
        this.retestReason = retestReason;
    }

    public String getTestCriterionCode() {
        return testCriterionCode;
    }

    public void setTestCriterionCode(String testCriterionCode) {
        this.testCriterionCode = testCriterionCode;
    }

    public String getTestCriterionClause() {
        return testCriterionClause;
    }

    public void setTestCriterionClause(String testCriterionClause) {
        this.testCriterionClause = testCriterionClause;
    }

    public String getProgName() {
        return progName;
    }

    public void setProgName(String progName) {
        this.progName = progName;
    }

    public String getIsApply() {
        return isApply;
    }

    public void setIsApply(String isApply) {
        this.isApply = isApply;
    }

    public String getCatl() {
        return catl;
    }

    public void setCatl(String catl) {
        this.catl = catl;
    }

    public String getTestingType() {
        return testingType;
    }

    public void setTestingType(String testingType) {
        this.testingType = testingType;
    }

    public String getTestingPosition() {
        return testingPosition;
    }

    public void setTestingPosition(String testingPosition) {
        this.testingPosition = testingPosition;
    }

    public Long getPositionCount() {
        return positionCount;
    }

    public void setPositionCount(Long positionCount) {
        this.positionCount = positionCount;
    }

    public Long getSamplePoints() {
        return samplePoints;
    }

    public void setSamplePoints(Long samplePoints) {
        this.samplePoints = samplePoints;
    }

    public Long getTestingFrequency() {
        return testingFrequency;
    }

    public void setTestingFrequency(Long testingFrequency) {
        this.testingFrequency = testingFrequency;
    }

    public Long getTestingDays() {
        return testingDays;
    }

    public void setTestingDays(Long testingDays) {
        this.testingDays = testingDays;
    }

    public Long getSampleCounts() {
        return sampleCounts;
    }

    public void setSampleCounts(Long sampleCounts) {
        this.sampleCounts = sampleCounts;
    }

    public String getSpCodeId() {
        return spCodeId;
    }

    public void setSpCodeId(String spCodeId) {
        this.spCodeId = spCodeId;
    }

    public String getSpTestId() {
        return spTestId;
    }

    public void setSpTestId(String spTestId) {
        this.spTestId = spTestId;
    }

    public String getTestStyleId() {
        return TestStyleId;
    }

    public void setTestStyleId(String testStyleId) {
        TestStyleId = testStyleId;
    }

    public String getOrign() {
        return orign;
    }

    public void setOrign(String orign) {
        this.orign = orign;
    }

    public String getExecutiveStandard() {
        return executiveStandard;
    }

    public void setExecutiveStandard(String executiveStandard) {
        this.executiveStandard = executiveStandard;
    }

    public Double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Long getNumberOfInspections() {
        return numberOfInspections;
    }

    public void setNumberOfInspections(Long numberOfInspections) {
        this.numberOfInspections = numberOfInspections;
    }

    public String getTestRule() {
        return testRule;
    }

    public void setTestRule(String testRule) {
        this.testRule = testRule;
    }

    public String getTestMethods() {
        return testMethods;
    }

    public void setTestMethods(String testMethods) {
        this.testMethods = testMethods;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Double getGuidePrice() {
        return guidePrice;
    }

    public void setGuidePrice(Double guidePrice) {
        this.guidePrice = guidePrice;
    }

    public String getAnalyte() {
        return analyte;
    }

    public void setAnalyte(String analyte) {
        this.analyte = analyte;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public String getCriterion() {
        return criterion;
    }

    public void setCriterion(String criterion) {
        this.criterion = criterion;
    }

    public String getLowLimit() {
        return lowLimit;
    }

    public void setLowLimit(String lowLimit) {
        this.lowLimit = lowLimit;
    }

    public String getHighLimit() {
        return highLimit;
    }

    public void setHighLimit(String highLimit) {
        this.highLimit = highLimit;
    }

    public String getQrules() {
        return qrules;
    }

    public void setQrules(String qrules) {
        this.qrules = qrules;
    }

    public String getSchemeName() {
        return schemeName;
    }

    public void setSchemeName(String schemeName) {
        this.schemeName = schemeName;
    }

    public String getMonitorId() {
        return monitorId;
    }

    public void setMonitorId(String monitorId) {
        this.monitorId = monitorId;
    }

    public String getIndexId() {
        return indexId;
    }

    public void setIndexId(String indexId) {
        this.indexId = indexId;
    }

    public String getIndexCategoryId() {
        return indexCategoryId;
    }

    public void setIndexCategoryId(String indexCategoryId) {
        this.indexCategoryId = indexCategoryId;
    }

    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    public String getIndex1() {
        return index1;
    }

    public void setIndex1(String index1) {
        this.index1 = index1;
    }

    public String getIndex2() {
        return index2;
    }

    public void setIndex2(String index2) {
        this.index2 = index2;
    }

    public String getIndex3() {
        return index3;
    }

    public void setIndex3(String index3) {
        this.index3 = index3;
    }

    public String getIndex4() {
        return index4;
    }

    public void setIndex4(String index4) {
        this.index4 = index4;
    }

    public String getIndex5() {
        return index5;
    }

    public void setIndex5(String index5) {
        this.index5 = index5;
    }

    public String getIndex6() {
        return index6;
    }

    public void setIndex6(String index6) {
        this.index6 = index6;
    }

    public String getSubcontract() {
        return subcontract;
    }

    public void setSubcontract(String subcontract) {
        this.subcontract = subcontract;
    }

    public String getItemGroup() {
        return itemGroup;
    }

    public void setItemGroup(String itemGroup) {
        this.itemGroup = itemGroup;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getSubcontract2() {
        return subcontract2;
    }

    public void setSubcontract2(String subcontract2) {
        this.subcontract2 = subcontract2;
    }

    public String getStdTeststyleId() {
        return stdTeststyleId;
    }

    public void setStdTeststyleId(String stdTeststyleId) {
        this.stdTeststyleId = stdTeststyleId;
    }

    public String getFirstPackage() {
        return firstPackage;
    }

    public void setFirstPackage(String firstPackage) {
        this.firstPackage = firstPackage;
    }
}
