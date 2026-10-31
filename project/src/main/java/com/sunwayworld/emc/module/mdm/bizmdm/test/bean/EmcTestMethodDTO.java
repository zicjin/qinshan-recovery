package com.sunwayworld.emc.module.mdm.bizmdm.test.bean;

import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.tree.bean.AbstractTreeNode;

/**
 * 
 * 测试管理导入DTO
 *
 */
public class EmcTestMethodDTO extends AbstractTreeNode {

    @Transient
    private static final long serialVersionUID = 1606123402655571165L;

    private Long testCategoryId;// 测试类型ID

    private String testCategoryName;// 测试类型名称

    private Long testId;// 测试编码

    private String testName;// 测试名称

    private Long methodId;// 方法编码

    private String methodName;// 方法名称

    private String analysisOrgId;// 分析科室编码

    private String analysisOrgName;// 分析科室名称

    private String conrolledNo;// 分析记录受控编号

    private String displayMonitorBasis;// 显示监测依据编号

    private String displayMonitorBasisName;// 显示监测依据名称

    private String authentication;// 是否认证

    private String authenticationName;// 是否认证名称

    private String approval;// 是否认可

    private String approvalName;// 是否认可名称

    private String analysisRecordName;// 分析记录名称

    private Double analysisFee;// 分析费

    private Double sampleFee;// 采样费

    private Long hours;// 时效(小时)

    private String defaultFlag;// 默认方法

    private String defaultFlagName;// 默认方法名称

    private String testEnglishName;// 测试英文名称

    private String inputUserId;// 录入人编码

    private String inputUserName;// 录入人名称

    public Long getTestCategoryId() {
        return testCategoryId;
    }

    public void setTestCategoryId(Long testCategoryId) {
        this.testCategoryId = testCategoryId;
    }

    public String getTestCategoryName() {
        return testCategoryName;
    }

    public void setTestCategoryName(String testCategoryName) {
        this.testCategoryName = testCategoryName;
    }

    public Long getTestId() {
        return testId;
    }

    public void setTestId(Long testId) {
        this.testId = testId;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public Long getMethodId() {
        return methodId;
    }

    public void setMethodId(Long methodId) {
        this.methodId = methodId;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getAnalysisOrgId() {
        return analysisOrgId;
    }

    public void setAnalysisOrgId(String analysisOrgId) {
        this.analysisOrgId = analysisOrgId;
    }

    public String getAnalysisOrgName() {
        return analysisOrgName;
    }

    public void setAnalysisOrgName(String analysisOrgName) {
        this.analysisOrgName = analysisOrgName;
    }

    public String getConrolledNo() {
        return conrolledNo;
    }

    public void setConrolledNo(String conrolledNo) {
        this.conrolledNo = conrolledNo;
    }

    public String getDisplayMonitorBasis() {
        return displayMonitorBasis;
    }

    public void setDisplayMonitorBasis(String displayMonitorBasis) {
        this.displayMonitorBasis = displayMonitorBasis;
    }

    public String getDisplayMonitorBasisName() {
        return displayMonitorBasisName;
    }

    public void setDisplayMonitorBasisName(String displayMonitorBasisName) {
        this.displayMonitorBasisName = displayMonitorBasisName;
    }

    public String getAuthentication() {
        return authentication;
    }

    public void setAuthentication(String authentication) {
        this.authentication = authentication;
    }

    public String getAuthenticationName() {
        return authenticationName;
    }

    public void setAuthenticationName(String authenticationName) {
        this.authenticationName = authenticationName;
    }

    public String getApproval() {
        return approval;
    }

    public void setApproval(String approval) {
        this.approval = approval;
    }

    public String getApprovalName() {
        return approvalName;
    }

    public void setApprovalName(String approvalName) {
        this.approvalName = approvalName;
    }

    public String getAnalysisRecordName() {
        return analysisRecordName;
    }

    public void setAnalysisRecordName(String analysisRecordName) {
        this.analysisRecordName = analysisRecordName;
    }

    public Double getAnalysisFee() {
        return analysisFee;
    }

    public void setAnalysisFee(Double analysisFee) {
        this.analysisFee = analysisFee;
    }

    public Double getSampleFee() {
        return sampleFee;
    }

    public void setSampleFee(Double sampleFee) {
        this.sampleFee = sampleFee;
    }

    public Long getHours() {
        return hours;
    }

    public void setHours(Long hours) {
        this.hours = hours;
    }

    public String getDefaultFlag() {
        return defaultFlag;
    }

    public void setDefaultFlag(String defaultFlag) {
        this.defaultFlag = defaultFlag;
    }

    public String getDefaultFlagName() {
        return defaultFlagName;
    }

    public void setDefaultFlagName(String defaultFlagName) {
        this.defaultFlagName = defaultFlagName;
    }

    public String getTestEnglishName() {
        return testEnglishName;
    }

    public void setTestEnglishName(String testEnglishName) {
        this.testEnglishName = testEnglishName;
    }

    public String getInputUserId() {
        return inputUserId;
    }

    public void setInputUserId(String inputUserId) {
        this.inputUserId = inputUserId;
    }

    public String getInputUserName() {
        return inputUserName;
    }

    public void setInputUserName(String inputUserName) {
        this.inputUserName = inputUserName;
    }
}
