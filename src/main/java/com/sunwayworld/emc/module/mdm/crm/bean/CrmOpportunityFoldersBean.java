package com.sunwayworld.emc.module.mdm.crm.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import com.sunwayworld.framework.support.domain.Insertable;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 商机folders表
 * @author zhengxy@sunwayworld.com 2021年08月26日
 */
@Table("T_CRM_OPPORTUNITY_FOLDERS")
public class CrmOpportunityFoldersBean extends AbstractInsertable<String> implements Insertable<String> {

    @Transient
    private static final long serialVersionUID = -82566196618711043L;

    @Id
    private String id;// 主键
    private LocalDate apprDate;// 签发日期
    private String apprDisp;// 签发状态
    private String approvedBy;// 签发人
    private String apprsts;// 签发状态
    private String comments;// 备注
    private LocalDate drawDate;// 编号生成日期
    private String fldSts;// 样品合格状态
    private String opportunityNo;// 检品编号
    private LocalDate logDate;// 受理完成时间
    private String prodGroup;// 检品类型
    private Long spCode;// 模板编号
    private String submitter;// 检品接收人
    private String opportunityId;// 商机表id
    private Long printNum;// 打印份数
    private String testResult;// 检验结论
    private String reportReleaser;// 签发人
    private String reportReleaserUsrNam;// 签发人用户名
    private String profile;// 模板方案编号
    private String oosPublishComments;// 不合格上报情况
    private String createReportBy;// 业务科合成人
    private String createReportByUsrNam;// 业务科合成人用户名
    private LocalDate createReportDate;// 业务科合成日期
    private String approvedByUsrNam;// 实际签发人用户名
    private String price;// 费用
    private String addPrice;// 附加费用
    private String pdfReport;// pdf原始记录路径
    private String cnas;// cnas
    private String cma;// cma
    private String sfda;// sfda
    private LocalDate mainDate;// 主检科室完成时间
    private LocalDate endDate;// 检验完成日期
    private String printByUsrNam;// 打印人
    private String createReportUsrNam;// 业务科主任
    private String choosePreApproval;// 初审人、主检人
    private String chooseApproval;// 业务科主任
    private String sourceFolderNo;// 检品信息来源
    private String rptOnly;// 报告路径
    private String reportType;// 报告类型
    private String oosItem;// 问题项目
    private String oosItem2;// 问题项目
    private String testResult2;// 检验结论
    private String oosPublishComments2;// 不合格上报情况
    private String comments2;// 备注
    private String orgId;// 科室id
    private String orgCode;// 科室代码
    private String orgName;// 科室名称
    private String corpId;// 单位id
    private String createdById;// 制单人编码
    private String createdByName;// 制单人名称
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;// 制单时间
    private String createdByOrgId;// 制单人单位编码
    private String createdByOrgName;// 制单人单位名称
    private String corpName;// 单位名称
    private String reportPrintFlag;// 报告书打印标志(1 :已打印；0：未打印)
    private String wfProcInstId;// 流程实例内码
    private String wfNodeInstId;// 当前节点实例内码
    private String wfNodeCode;// 当前审批节点编码
    private String wfNodeName;// 当前审批节点名称
    private String sendToCompany;// 报告领取单位
    private String sendComments;// 发放情况
    private String location;// 存放地址
    private String reportGetType;// 取报告书方式
    private String ems;// 快递单号
    private String packageId;// 档案盒号
    private String result;// 报告状态【合格（默认），不合格】
    private LocalDate assignDate;// 任务分配时间
    private Long wfBackwardFlag;// 节点退回标记
    private String preApprovalName;// 主检人姓名
    private String sampleStatus;// 检品状态（0：正常检验；1：终止检验；注：可继续扩展其他含义，默认为0）
    private String pdfReportSeal;// 签章后报告书
    private String recordOpinion;// 保存查看原始记录时记录的意见信息
    private String opinionFileId;// 标准复核意见
    private String pitestRules;// 补充依据
    private String piTestType;// 检验类型
    private String piResult;// 批签发摘要审核结论
    private String piNumber;// 批签发证号中文
    private String piNumberEn;// 批签发证号英文
    private String piCertificationinStruction;// 批签发证说明中文
    private String piCertificationinStructionEn;// 批签发证说明英文
    private String piConclusion;// 结论说明中文
    private String piConclusionEn;// 结论说明英文
    private Long piExported;// 是否已导出
    private String opinionFilePath;// 标准复核意见文件路径
    private String mergeStatus;// pdf文件合成状态(0 :未合成 1 :合成中 2 :合成完成)
    private String reportPdfPath;// 查看报告书pdf的路径
    private LocalDate reportsEndDate;// 报告发放日期
    private String opinionPdfFilepath;// 标准复核意见pdf
    private String receivStatus;// 接收状态 0 :未接收 1 :已接收
    private String receiverCode;// 接收人编码
    private String receiverName;// 接收人名称
    private LocalDate receivTime;// 接收时间
    private String receivDept;// 接收科室编码
    private String receivDeptDesc;// 接收科室名称
    private Long piPublished;// 是否已公示
    private String swzphgzcapdfpath;// 生物制品合格证ca签章pdf路径
    private String renewalReportNo;// 换发报告编号后缀
    private String isFee;// 是否收费
    private String piPushed;// 是否推送（0：未推送，1：已推送）
    private String reportFetcher;// 报告领取人
    private String statementStamp;// 是否加盖声明页的签章
    private String isStamp;// 是否加盖检验检测专用签章
    private String isPlaceonFile;// 是否归档：0、未归档；1、已归档；默认：0
    private String isSendrep;// 是否发放：0、未发放；1、已发放；默认：0
    private String isca;// 是否使用电子签章
    private String jcbgzCode;// 检测报告章规则
    private String isFirstTest;// 是否首次检验 0：否 1：是

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

    public String getApprDisp() {
        return apprDisp;
    }

    public void setApprDisp(String apprDisp) {
        this.apprDisp = apprDisp;
    }

    public String getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(String approvedBy) {
        this.approvedBy = approvedBy;
    }

    public String getApprsts() {
        return apprsts;
    }

    public void setApprsts(String apprsts) {
        this.apprsts = apprsts;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public LocalDate getDrawDate() {
        return drawDate;
    }

    public void setDrawDate(LocalDate drawDate) {
        this.drawDate = drawDate;
    }

    public String getFldSts() {
        return fldSts;
    }

    public void setFldSts(String fldSts) {
        this.fldSts = fldSts;
    }

    public String getOpportunityNo() {
        return opportunityNo;
    }

    public void setOpportunityNo(String opportunityNo) {
        this.opportunityNo = opportunityNo;
    }

    public LocalDate getLogDate() {
        return logDate;
    }

    public void setLogDate(LocalDate logDate) {
        this.logDate = logDate;
    }

    public String getProdGroup() {
        return prodGroup;
    }

    public void setProdGroup(String prodGroup) {
        this.prodGroup = prodGroup;
    }

    public Long getSpCode() {
        return spCode;
    }

    public void setSpCode(Long spCode) {
        this.spCode = spCode;
    }

    public String getSubmitter() {
        return submitter;
    }

    public void setSubmitter(String submitter) {
        this.submitter = submitter;
    }

    public String getOpportunityId() {
        return opportunityId;
    }

    public void setOpportunityId(String opportunityId) {
        this.opportunityId = opportunityId;
    }

    public Long getPrintNum() {
        return printNum;
    }

    public void setPrintNum(Long printNum) {
        this.printNum = printNum;
    }

    public String getTestResult() {
        return testResult;
    }

    public void setTestResult(String testResult) {
        this.testResult = testResult;
    }

    public String getReportReleaser() {
        return reportReleaser;
    }

    public void setReportReleaser(String reportReleaser) {
        this.reportReleaser = reportReleaser;
    }

    public String getReportReleaserUsrNam() {
        return reportReleaserUsrNam;
    }

    public void setReportReleaserUsrNam(String reportReleaserUsrNam) {
        this.reportReleaserUsrNam = reportReleaserUsrNam;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getOosPublishComments() {
        return oosPublishComments;
    }

    public void setOosPublishComments(String oosPublishComments) {
        this.oosPublishComments = oosPublishComments;
    }

    public String getCreateReportBy() {
        return createReportBy;
    }

    public void setCreateReportBy(String createReportBy) {
        this.createReportBy = createReportBy;
    }

    public String getCreateReportByUsrNam() {
        return createReportByUsrNam;
    }

    public void setCreateReportByUsrNam(String createReportByUsrNam) {
        this.createReportByUsrNam = createReportByUsrNam;
    }

    public LocalDate getCreateReportDate() {
        return createReportDate;
    }

    public void setCreateReportDate(LocalDate createReportDate) {
        this.createReportDate = createReportDate;
    }

    public String getApprovedByUsrNam() {
        return approvedByUsrNam;
    }

    public void setApprovedByUsrNam(String approvedByUsrNam) {
        this.approvedByUsrNam = approvedByUsrNam;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getAddPrice() {
        return addPrice;
    }

    public void setAddPrice(String addPrice) {
        this.addPrice = addPrice;
    }

    public String getPdfReport() {
        return pdfReport;
    }

    public void setPdfReport(String pdfReport) {
        this.pdfReport = pdfReport;
    }

    public String getCnas() {
        return cnas;
    }

    public void setCnas(String cnas) {
        this.cnas = cnas;
    }

    public String getCma() {
        return cma;
    }

    public void setCma(String cma) {
        this.cma = cma;
    }

    public String getSfda() {
        return sfda;
    }

    public void setSfda(String sfda) {
        this.sfda = sfda;
    }

    public LocalDate getMainDate() {
        return mainDate;
    }

    public void setMainDate(LocalDate mainDate) {
        this.mainDate = mainDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getPrintByUsrNam() {
        return printByUsrNam;
    }

    public void setPrintByUsrNam(String printByUsrNam) {
        this.printByUsrNam = printByUsrNam;
    }

    public String getCreateReportUsrNam() {
        return createReportUsrNam;
    }

    public void setCreateReportUsrNam(String createReportUsrNam) {
        this.createReportUsrNam = createReportUsrNam;
    }

    public String getChoosePreApproval() {
        return choosePreApproval;
    }

    public void setChoosePreApproval(String choosePreApproval) {
        this.choosePreApproval = choosePreApproval;
    }

    public String getChooseApproval() {
        return chooseApproval;
    }

    public void setChooseApproval(String chooseApproval) {
        this.chooseApproval = chooseApproval;
    }

    public String getSourceFolderNo() {
        return sourceFolderNo;
    }

    public void setSourceFolderNo(String sourceFolderNo) {
        this.sourceFolderNo = sourceFolderNo;
    }

    public String getRptOnly() {
        return rptOnly;
    }

    public void setRptOnly(String rptOnly) {
        this.rptOnly = rptOnly;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public String getOosItem() {
        return oosItem;
    }

    public void setOosItem(String oosItem) {
        this.oosItem = oosItem;
    }

    public String getOosItem2() {
        return oosItem2;
    }

    public void setOosItem2(String oosItem2) {
        this.oosItem2 = oosItem2;
    }

    public String getTestResult2() {
        return testResult2;
    }

    public void setTestResult2(String testResult2) {
        this.testResult2 = testResult2;
    }

    public String getOosPublishComments2() {
        return oosPublishComments2;
    }

    public void setOosPublishComments2(String oosPublishComments2) {
        this.oosPublishComments2 = oosPublishComments2;
    }

    public String getComments2() {
        return comments2;
    }

    public void setComments2(String comments2) {
        this.comments2 = comments2;
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

    public String getCorpId() {
        return corpId;
    }

    public void setCorpId(String corpId) {
        this.corpId = corpId;
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

    public String getCorpName() {
        return corpName;
    }

    public void setCorpName(String corpName) {
        this.corpName = corpName;
    }

    public String getReportPrintFlag() {
        return reportPrintFlag;
    }

    public void setReportPrintFlag(String reportPrintFlag) {
        this.reportPrintFlag = reportPrintFlag;
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

    public String getWfNodeName() {
        return wfNodeName;
    }

    public void setWfNodeName(String wfNodeName) {
        this.wfNodeName = wfNodeName;
    }

    public String getSendToCompany() {
        return sendToCompany;
    }

    public void setSendToCompany(String sendToCompany) {
        this.sendToCompany = sendToCompany;
    }

    public String getSendComments() {
        return sendComments;
    }

    public void setSendComments(String sendComments) {
        this.sendComments = sendComments;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getReportGetType() {
        return reportGetType;
    }

    public void setReportGetType(String reportGetType) {
        this.reportGetType = reportGetType;
    }

    public String getEms() {
        return ems;
    }

    public void setEms(String ems) {
        this.ems = ems;
    }

    public String getPackageId() {
        return packageId;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public LocalDate getAssignDate() {
        return assignDate;
    }

    public void setAssignDate(LocalDate assignDate) {
        this.assignDate = assignDate;
    }

    public Long getWfBackwardFlag() {
        return wfBackwardFlag;
    }

    public void setWfBackwardFlag(Long wfBackwardFlag) {
        this.wfBackwardFlag = wfBackwardFlag;
    }

    public String getPreApprovalName() {
        return preApprovalName;
    }

    public void setPreApprovalName(String preApprovalName) {
        this.preApprovalName = preApprovalName;
    }

    public String getSampleStatus() {
        return sampleStatus;
    }

    public void setSampleStatus(String sampleStatus) {
        this.sampleStatus = sampleStatus;
    }

    public String getPdfReportSeal() {
        return pdfReportSeal;
    }

    public void setPdfReportSeal(String pdfReportSeal) {
        this.pdfReportSeal = pdfReportSeal;
    }

    public String getRecordOpinion() {
        return recordOpinion;
    }

    public void setRecordOpinion(String recordOpinion) {
        this.recordOpinion = recordOpinion;
    }

    public String getOpinionFileId() {
        return opinionFileId;
    }

    public void setOpinionFileId(String opinionFileId) {
        this.opinionFileId = opinionFileId;
    }

    public String getPitestRules() {
        return pitestRules;
    }

    public void setPitestRules(String pitestRules) {
        this.pitestRules = pitestRules;
    }

    public String getPiTestType() {
        return piTestType;
    }

    public void setPiTestType(String piTestType) {
        this.piTestType = piTestType;
    }

    public String getPiResult() {
        return piResult;
    }

    public void setPiResult(String piResult) {
        this.piResult = piResult;
    }

    public String getPiNumber() {
        return piNumber;
    }

    public void setPiNumber(String piNumber) {
        this.piNumber = piNumber;
    }

    public String getPiNumberEn() {
        return piNumberEn;
    }

    public void setPiNumberEn(String piNumberEn) {
        this.piNumberEn = piNumberEn;
    }

    public String getPiCertificationinStruction() {
        return piCertificationinStruction;
    }

    public void setPiCertificationinStruction(String piCertificationinStruction) {
        this.piCertificationinStruction = piCertificationinStruction;
    }

    public String getPiCertificationinStructionEn() {
        return piCertificationinStructionEn;
    }

    public void setPiCertificationinStructionEn(String piCertificationinStructionEn) {
        this.piCertificationinStructionEn = piCertificationinStructionEn;
    }

    public String getPiConclusion() {
        return piConclusion;
    }

    public void setPiConclusion(String piConclusion) {
        this.piConclusion = piConclusion;
    }

    public String getPiConclusionEn() {
        return piConclusionEn;
    }

    public void setPiConclusionEn(String piConclusionEn) {
        this.piConclusionEn = piConclusionEn;
    }

    public Long getPiExported() {
        return piExported;
    }

    public void setPiExported(Long piExported) {
        this.piExported = piExported;
    }

    public String getOpinionFilePath() {
        return opinionFilePath;
    }

    public void setOpinionFilePath(String opinionFilePath) {
        this.opinionFilePath = opinionFilePath;
    }

    public String getMergeStatus() {
        return mergeStatus;
    }

    public void setMergeStatus(String mergeStatus) {
        this.mergeStatus = mergeStatus;
    }

    public String getReportPdfPath() {
        return reportPdfPath;
    }

    public void setReportPdfPath(String reportPdfPath) {
        this.reportPdfPath = reportPdfPath;
    }

    public LocalDate getReportsEndDate() {
        return reportsEndDate;
    }

    public void setReportsEndDate(LocalDate reportsEndDate) {
        this.reportsEndDate = reportsEndDate;
    }

    public String getOpinionPdfFilepath() {
        return opinionPdfFilepath;
    }

    public void setOpinionPdfFilepath(String opinionPdfFilepath) {
        this.opinionPdfFilepath = opinionPdfFilepath;
    }

    public String getReceivStatus() {
        return receivStatus;
    }

    public void setReceivStatus(String receivStatus) {
        this.receivStatus = receivStatus;
    }

    public String getReceiverCode() {
        return receiverCode;
    }

    public void setReceiverCode(String receiverCode) {
        this.receiverCode = receiverCode;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public LocalDate getReceivTime() {
        return receivTime;
    }

    public void setReceivTime(LocalDate receivTime) {
        this.receivTime = receivTime;
    }

    public String getReceivDept() {
        return receivDept;
    }

    public void setReceivDept(String receivDept) {
        this.receivDept = receivDept;
    }

    public String getReceivDeptDesc() {
        return receivDeptDesc;
    }

    public void setReceivDeptDesc(String receivDeptDesc) {
        this.receivDeptDesc = receivDeptDesc;
    }

    public Long getPiPublished() {
        return piPublished;
    }

    public void setPiPublished(Long piPublished) {
        this.piPublished = piPublished;
    }

    public String getSwzphgzcapdfpath() {
        return swzphgzcapdfpath;
    }

    public void setSwzphgzcapdfpath(String swzphgzcapdfpath) {
        this.swzphgzcapdfpath = swzphgzcapdfpath;
    }

    public String getRenewalReportNo() {
        return renewalReportNo;
    }

    public void setRenewalReportNo(String renewalReportNo) {
        this.renewalReportNo = renewalReportNo;
    }

    public String getIsFee() {
        return isFee;
    }

    public void setIsFee(String isFee) {
        this.isFee = isFee;
    }

    public String getPiPushed() {
        return piPushed;
    }

    public void setPiPushed(String piPushed) {
        this.piPushed = piPushed;
    }

    public String getReportFetcher() {
        return reportFetcher;
    }

    public void setReportFetcher(String reportFetcher) {
        this.reportFetcher = reportFetcher;
    }

    public String getStatementStamp() {
        return statementStamp;
    }

    public void setStatementStamp(String statementStamp) {
        this.statementStamp = statementStamp;
    }

    public String getIsStamp() {
        return isStamp;
    }

    public void setIsStamp(String isStamp) {
        this.isStamp = isStamp;
    }

    public String getIsPlaceonFile() {
        return isPlaceonFile;
    }

    public void setIsPlaceonFile(String isPlaceonFile) {
        this.isPlaceonFile = isPlaceonFile;
    }

    public String getIsSendrep() {
        return isSendrep;
    }

    public void setIsSendrep(String isSendrep) {
        this.isSendrep = isSendrep;
    }

    public String getIsca() {
        return isca;
    }

    public void setIsca(String isca) {
        this.isca = isca;
    }

    public String getJcbgzCode() {
        return jcbgzCode;
    }

    public void setJcbgzCode(String jcbgzCode) {
        this.jcbgzCode = jcbgzCode;
    }

    public String getIsFirstTest() {
        return isFirstTest;
    }

    public void setIsFirstTest(String isFirstTest) {
        this.isFirstTest = isFirstTest;
    }

}
