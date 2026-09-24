package com.sunwayworld.emc.module.bp.report.report.bean;

/**
 * Word模板中的基本数据（每一个数据都对应特定的占位符）
 *
 * @author zhengxy@sunwayworld.com 2020年06月10日
 */
public class WordTemplateBasicDTO {

    /**
     * 报告编号
     */
    private String reportNo;
    /**
     * 年
     */
    private String year;
    /**
     * 月
     */
    private String month;
    /**
     * 日
     */
    private String day;
    /**
     * 委托单位
     */
    private String entrustOrgName;
    /**
     * 委托单位地址
     */
    private String entrustOrgAddress;
    /**
     * 委托单位联系人
     */
    private String entrustOrgContact;
    /**
     * 委托单位联系电话
     */
    private String entrustOrgPhone;
    /**
     * 委托单位描述
     */
    private String entrustOrgDesc;
    /**
     * 受检单位
     */
    private String testOrgName;
    /**
     * 受检单位地址
     */
    private String testOrgAddress;
    /**
     * 受检单位联系电话
     */
    private String testOrgPhone;
    /**
     * 受检单位描述
     */
    private String testOrgDesc;
    /**
     * 项目名称
     */
    private String projectName;
    /**
     * 项目地址
     */
    private String projectAddress;
    /**
     * 项目信息
     */
    private String projectInfo;
    /**
     * 检测类别
     */
    private String detectCategory;
    /**
     * 样品类型简称，比如固废二噁英类简称固废
     */
    private String simpleOrderCategory;
    /**
     * 样品类型
     */
    private String orderCategory;
    /**
     * 采样日期
     */
    private String sampleDate;
    /**
     * 采样地点
     */
    private String samplePosition;
    /**
     * 采样人员
     */
    private String sampleUser;
    /**
     * 分析日期
     */
    private String analysisDate;
    /**
     * 分析人员
     */
    private String analysisUser;
    /**
     * 样品来源
     */
    private String orderSource;
    /**
     * 样品状态
     */
    private String orderStatus;
    /**
     * 项目备注
     */
    private String projectRemark;
    /**
     * 编制人
     */
    private String editor;
    /**
     * 审核人
     */
    private String auditor;
    /**
     * 批准人
     */
    private String approver;
    /**
     * 检测结果
     */
    private String testResult;
    /**
     * 检测依据
     */
    private String testBasis;
    /**
     * 公司名称
     */
    private String companyName;
    /**
     * 公司英文名称
     */
    private String companyEnName;
    /**
     * 公司地址
     */
    private String companyAddress;
    /**
     * 邮编
     */
    private String companyPostcode;
    /**
     * 公司电话
     */
    private String companyPhone;
    /**
     * 公司投诉电话
     */
    private String companyComplaintPhone;
    /**
     * 公司邮箱
     */
    private String companyEmail;
    /**
     * 登记日期
     */
    private String registeDate;
    /**
     * 检测项目
     */
    private String testNames;
    /**
     * 检测依据
     */
    private String testMethodBasis;
    /**
     * 委托方式
     */
    private String entrustType;
    /**
     * 采样人、送样人
     */
    private String userTitle;
    /**
     * 采样日期、接样日期
     */
    private String dateTitle;
    /**
     * 文档结束备注
     */
    private String reportEndRemark;
    /**
     * 执行标准，评价标准
     */
    private String executeStd;
    /**
     * 符号附注
     */
    private String symbolRemark;
    /**
     * 环境条件控制
     */
    private String environmentalConditions;
    /**
     * 检测目的
     */
    private String detectPurpose;
    /**
     * 现场采样
     */
    private String samplingDesc;
    /**
     * 采样环境控制
     */
    private String samplingEnv;
    /**
     * 保存条件
     */
    private String storeCondition;
    /**
     * 综合报告受控编号
     */
    private String controlNumber;
    /**
     * 质控报告受控编号
     */
    private String qcControlNumber;
    /**
     * 报告名称
     */
    private String reportName;
    /**
     * 报告日期
     */
    private String reportDate;

    public String getReportNo() {
        return reportNo;
    }

    public void setReportNo(String reportNo) {
        this.reportNo = reportNo;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
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

    public String getEntrustOrgDesc() {
        return entrustOrgDesc;
    }

    public void setEntrustOrgDesc(String entrustOrgDesc) {
        this.entrustOrgDesc = entrustOrgDesc;
    }

    public String getTestOrgName() {
        return testOrgName;
    }

    public void setTestOrgName(String testOrgName) {
        this.testOrgName = testOrgName;
    }

    public String getTestOrgAddress() {
        return testOrgAddress;
    }

    public void setTestOrgAddress(String testOrgAddress) {
        this.testOrgAddress = testOrgAddress;
    }

    public String getTestOrgPhone() {
        return testOrgPhone;
    }

    public void setTestOrgPhone(String testOrgPhone) {
        this.testOrgPhone = testOrgPhone;
    }

    public String getTestOrgDesc() {
        return testOrgDesc;
    }

    public void setTestOrgDesc(String testOrgDesc) {
        this.testOrgDesc = testOrgDesc;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectAddress() {
        return projectAddress;
    }

    public void setProjectAddress(String projectAddress) {
        this.projectAddress = projectAddress;
    }

    public String getProjectInfo() {
        return projectInfo;
    }

    public void setProjectInfo(String projectInfo) {
        this.projectInfo = projectInfo;
    }

    public String getDetectCategory() {
        return detectCategory;
    }

    public void setDetectCategory(String detectCategory) {
        this.detectCategory = detectCategory;
    }

    public String getSimpleOrderCategory() {
        return simpleOrderCategory;
    }

    public void setSimpleOrderCategory(String simpleOrderCategory) {
        this.simpleOrderCategory = simpleOrderCategory;
    }

    public String getOrderCategory() {
        return orderCategory;
    }

    public void setOrderCategory(String orderCategory) {
        this.orderCategory = orderCategory;
    }

    public String getSampleDate() {
        return sampleDate;
    }

    public void setSampleDate(String sampleDate) {
        this.sampleDate = sampleDate;
    }

    public String getSamplePosition() {
        return samplePosition;
    }

    public void setSamplePosition(String samplePosition) {
        this.samplePosition = samplePosition;
    }

    public String getSampleUser() {
        return sampleUser;
    }

    public void setSampleUser(String sampleUser) {
        this.sampleUser = sampleUser;
    }

    public String getAnalysisDate() {
        return analysisDate;
    }

    public void setAnalysisDate(String analysisDate) {
        this.analysisDate = analysisDate;
    }

    public String getAnalysisUser() {
        return analysisUser;
    }

    public void setAnalysisUser(String analysisUser) {
        this.analysisUser = analysisUser;
    }

    public String getOrderSource() {
        return orderSource;
    }

    public void setOrderSource(String orderSource) {
        this.orderSource = orderSource;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getProjectRemark() {
        return projectRemark;
    }

    public void setProjectRemark(String projectRemark) {
        this.projectRemark = projectRemark;
    }

    public String getEditor() {
        return editor;
    }

    public void setEditor(String editor) {
        this.editor = editor;
    }

    public String getAuditor() {
        return auditor;
    }

    public void setAuditor(String auditor) {
        this.auditor = auditor;
    }

    public String getApprover() {
        return approver;
    }

    public void setApprover(String approver) {
        this.approver = approver;
    }

    public String getTestResult() {
        return testResult;
    }

    public void setTestResult(String testResult) {
        this.testResult = testResult;
    }

    public String getTestBasis() {
        return testBasis;
    }

    public void setTestBasis(String testBasis) {
        this.testBasis = testBasis;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyEnName() {
        return companyEnName;
    }

    public void setCompanyEnName(String companyEnName) {
        this.companyEnName = companyEnName;
    }

    public String getCompanyAddress() {
        return companyAddress;
    }

    public void setCompanyAddress(String companyAddress) {
        this.companyAddress = companyAddress;
    }

    public String getCompanyPostcode() {
        return companyPostcode;
    }

    public void setCompanyPostcode(String companyPostcode) {
        this.companyPostcode = companyPostcode;
    }

    public String getCompanyPhone() {
        return companyPhone;
    }

    public void setCompanyPhone(String companyPhone) {
        this.companyPhone = companyPhone;
    }

    public String getCompanyComplaintPhone() {
        return companyComplaintPhone;
    }

    public void setCompanyComplaintPhone(String companyComplaintPhone) {
        this.companyComplaintPhone = companyComplaintPhone;
    }

    public String getCompanyEmail() {
        return companyEmail;
    }

    public void setCompanyEmail(String companyEmail) {
        this.companyEmail = companyEmail;
    }

    public String getRegisteDate() {
        return registeDate;
    }

    public void setRegisteDate(String registeDate) {
        this.registeDate = registeDate;
    }

    public String getTestNames() {
        return testNames;
    }

    public void setTestNames(String testNames) {
        this.testNames = testNames;
    }

    public String getTestMethodBasis() {
        return testMethodBasis;
    }

    public void setTestMethodBasis(String testMethodBasis) {
        this.testMethodBasis = testMethodBasis;
    }

    public String getEntrustType() {
        return entrustType;
    }

    public void setEntrustType(String entrustType) {
        this.entrustType = entrustType;
    }

    public String getUserTitle() {
        return userTitle;
    }

    public void setUserTitle(String userTitle) {
        this.userTitle = userTitle;
    }

    public String getDateTitle() {
        return dateTitle;
    }

    public void setDateTitle(String dateTitle) {
        this.dateTitle = dateTitle;
    }

    public String getReportEndRemark() {
        return reportEndRemark;
    }

    public void setReportEndRemark(String reportEndRemark) {
        this.reportEndRemark = reportEndRemark;
    }

    public String getExecuteStd() {
        return executeStd;
    }

    public void setExecuteStd(String executeStd) {
        this.executeStd = executeStd;
    }

    public String getSymbolRemark() {
        return symbolRemark;
    }

    public void setSymbolRemark(String symbolRemark) {
        this.symbolRemark = symbolRemark;
    }

    public String getEnvironmentalConditions() {
        return environmentalConditions;
    }

    public void setEnvironmentalConditions(String environmentalConditions) {
        this.environmentalConditions = environmentalConditions;
    }

    public String getDetectPurpose() {
        return detectPurpose;
    }

    public void setDetectPurpose(String detectPurpose) {
        this.detectPurpose = detectPurpose;
    }

    public String getSamplingDesc() {
        return samplingDesc;
    }

    public void setSamplingDesc(String samplingDesc) {
        this.samplingDesc = samplingDesc;
    }

    public String getSamplingEnv() {
        return samplingEnv;
    }

    public void setSamplingEnv(String samplingEnv) {
        this.samplingEnv = samplingEnv;
    }

    public String getStoreCondition() {
        return storeCondition;
    }

    public void setStoreCondition(String storeCondition) {
        this.storeCondition = storeCondition;
    }

    public String getControlNumber() {
        return controlNumber;
    }

    public void setControlNumber(String controlNumber) {
        this.controlNumber = controlNumber;
    }

    public String getQcControlNumber() {
        return qcControlNumber;
    }

    public void setQcControlNumber(String qcControlNumber) {
        this.qcControlNumber = qcControlNumber;
    }

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    public String getReportDate() {
        return reportDate;
    }

    public void setReportDate(String reportDate) {
        this.reportDate = reportDate;
    }

    @Override
    public String toString() {
        return "WordTemplateBasicDTO{" +
                "reportNo='" + reportNo + '\'' +
                ", year='" + year + '\'' +
                ", month='" + month + '\'' +
                ", day='" + day + '\'' +
                ", entrustOrgName='" + entrustOrgName + '\'' +
                ", entrustOrgAddress='" + entrustOrgAddress + '\'' +
                ", entrustOrgContact='" + entrustOrgContact + '\'' +
                ", entrustOrgPhone='" + entrustOrgPhone + '\'' +
                ", testOrgName='" + testOrgName + '\'' +
                ", testOrgAddress='" + testOrgAddress + '\'' +
                ", testOrgPhone='" + testOrgPhone + '\'' +
                ", testOrgDesc='" + testOrgDesc + '\'' +
                ", projectName='" + projectName + '\'' +
                ", detectCategory='" + detectCategory + '\'' +
                ", orderCategory='" + orderCategory + '\'' +
                ", sampleDate='" + sampleDate + '\'' +
                ", samplePosition='" + samplePosition + '\'' +
                ", sampleUser='" + sampleUser + '\'' +
                ", analysisDate='" + analysisDate + '\'' +
                ", analysisUser='" + analysisUser + '\'' +
                ", orderSource='" + orderSource + '\'' +
                ", orderStatus='" + orderStatus + '\'' +
                ", projectRemark='" + projectRemark + '\'' +
                ", editor='" + editor + '\'' +
                ", auditor='" + auditor + '\'' +
                ", approver='" + approver + '\'' +
                ", testResult='" + testResult + '\'' +
                ", testBasis='" + testBasis + '\'' +
                ", companyName='" + companyName + '\'' +
                ", companyEnName='" + companyEnName + '\'' +
                ", companyAddress='" + companyAddress + '\'' +
                ", companyPostcode='" + companyPostcode + '\'' +
                ", companyPhone='" + companyPhone + '\'' +
                ", companyComplaintPhone='" + companyComplaintPhone + '\'' +
                ", companyEmail='" + companyEmail + '\'' +
                ", registeDate='" + registeDate + '\'' +
                ", testNames='" + testNames + '\'' +
                ", testMethodBasis='" + testMethodBasis + '\'' +
                '}';
    }
}
