package com.sunwayworld.emc.module.mdm.bizmdm.test.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.sunwayworld.emc.framework.constant.EmcConstant;
import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.data.annotation.*;
import com.sunwayworld.framework.support.domain.AbstractPersistable;
import com.sunwayworld.framework.support.domain.Activatable;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * 测试方法表
 *
 * @author yuanhh@sunwayworld.com
 * @date 2019-09-19
 */

@Table("T_EMC_TEST_METHOD")
public class EmcTestMethodBean extends AbstractPersistable<Long> implements Activatable<Long> {

    @Transient
    private static final long serialVersionUID = -917955269120901787L;

    @Id
    private Long id;// 主键

    private Long testId;// 测试编码

    private String testEnglishName;// 测试英文名称

    private Long testCategoryId;// 测试类型ID

    private String testMethodType;// 类型----江苏益铭和上海水文添加该字段，用来记录测试方法的类型，比如理化、有机、金属、二噁英等

    private String testClass;// 测试类别

    private Long methodId;// 方法编码

    private String analysisOrgId;// 分析科室编码

    private String methodParam;// 方法参数

    private String conrolledNo;// 分析记录受控编号

    private String displayMonitorBasis;// 显示监测依据

    private String authentication;// 是否认证

    private String approval;// 是否认可

    private String analysisRecordName;// 分析记录名称

    private Double analysisFee;// 分析费

    private Double sampleFee;// 采样费

    private Double hours;// 时效(小时)

    private String inputUserId;// 录入人编码

    private String inputUserName;// 录入人名称

    @NotNull(defaultValue = Constant.NO)
    private String defaultFlag;// 默认方法

    private String remark; // 备注

    private String recoveryRate; // 加标回收率
    @Clob
    private String beforeHandle; // 前处理方法名

    private String beforeHandleId;//前处理Id

    private String retainTime; // 留样时间

    private String versions; // 版本

    private String acqNoSuffix; // 采集编号后缀

    private String reportLimitType; // 报告检出类型

    @NotNull(defaultValue = Constant.ACTIVATED_STATUS_YES)
    private String activatedFlag;// 启用标志

    @NotNull(defaultValue = EmcConstant.LOCKED)
    private String isLocked;// 锁定标志

    private String activatedById;// 启用人编码

    private String activatedByName;// 启用人名称

    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime activatedTime;// 启用时间

    private String createdById;// 制单人编码

    private String createdByName;// 制单人名称

    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;// 制单时间

    private String createdByOrgId;// 制单人单位编码

    private String createdByOrgName;// 制单人单位名称

    @NotNull(defaultValue = Constant.NO)
    private String sampleParam;// 是否气采样参数

    @NotNull(defaultValue = Constant.NO)
    private String tube;// 试管
    private String methodScope;// 方法范围
    private String multipleItemGroup;// 多分析项组
    private String generateLabel;// 生成标签

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getSampleParam() {
        return sampleParam;
    }

    public void setSampleParam(String sampleParam) {
        this.sampleParam = sampleParam;
    }

    public String getTube() {
        return tube;
    }

    public void setTube(String tube) {
        this.tube = tube;
    }

    public Long getTestId() {
        return testId;
    }

    public void setTestId(Long testId) {
        this.testId = testId;
    }

    public String getTestEnglishName() {
        return testEnglishName;
    }

    public void setTestEnglishName(String testEnglishName) {
        this.testEnglishName = testEnglishName;
    }

    public Long getTestCategoryId() {
        return testCategoryId;
    }

    public void setTestCategoryId(Long testCategoryId) {
        this.testCategoryId = testCategoryId;
    }

    public String getTestClass() {
        return testClass;
    }

    public void setTestClass(String testClass) {
        this.testClass = testClass;
    }

    public Long getMethodId() {
        return methodId;
    }

    public void setMethodId(Long methodId) {
        this.methodId = methodId;
    }

    public String getAnalysisOrgId() {
        return analysisOrgId;
    }

    public void setAnalysisOrgId(String analysisOrgId) {
        this.analysisOrgId = analysisOrgId;
    }

    public String getMethodParam() {
        return methodParam;
    }

    public void setMethodParam(String methodParam) {
        this.methodParam = methodParam;
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

    public String getAuthentication() {
        return authentication;
    }

    public void setAuthentication(String authentication) {
        this.authentication = authentication;
    }

    public String getApproval() {
        return approval;
    }

    public void setApproval(String approval) {
        this.approval = approval;
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

    public Double getHours() {
        return hours;
    }

    public void setHours(Double hours) {
        this.hours = hours;
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

    public String getDefaultFlag() {
        return defaultFlag;
    }

    public void setDefaultFlag(String defaultFlag) {
        this.defaultFlag = defaultFlag;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getRecoveryRate() {
        return recoveryRate;
    }

    public void setRecoveryRate(String recoveryRate) {
        this.recoveryRate = recoveryRate;
    }

    @Override
    public String getActivatedFlag() {
        return activatedFlag;
    }

    @Override
    public void setActivatedFlag(String activatedFlag) {
        this.activatedFlag = activatedFlag;
    }

    @Override
    public String getActivatedById() {
        return activatedById;
    }

    @Override
    public void setActivatedById(String activatedById) {
        this.activatedById = activatedById;
    }

    @Override
    public String getActivatedByName() {
        return activatedByName;
    }

    @Override
    public void setActivatedByName(String activatedByName) {
        this.activatedByName = activatedByName;
    }

    @Override
    public LocalDateTime getActivatedTime() {
        return activatedTime;
    }

    @Override
    public void setActivatedTime(LocalDateTime activatedTime) {
        this.activatedTime = activatedTime;
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

    public String getBeforeHandle() {
        return beforeHandle;
    }

    public void setBeforeHandle(String beforeHandle) {
        this.beforeHandle = beforeHandle;
    }

    public String getBeforeHandleId() {
        return beforeHandleId;
    }

    public void setBeforeHandleId(String beforeHandleId) {
        this.beforeHandleId = beforeHandleId;
    }

    public String getRetainTime() {
        return retainTime;
    }

    public void setRetainTime(String retainTime) {
        this.retainTime = retainTime;
    }

    public String getIsLocked() {
        return isLocked;
    }

    public void setIsLocked(String isLocked) {
        this.isLocked = isLocked;
    }

    public String getVersions() {
        return versions;
    }

    public void setVersions(String versions) {
        this.versions = versions;
    }

	public String getTestMethodType() {
		return testMethodType;
	}

	public void setTestMethodType(String testMethodType) {
		this.testMethodType = testMethodType;
	}

    public String getAcqNoSuffix() {
        return acqNoSuffix;
    }

    public void setAcqNoSuffix(String acqNoSuffix) {
        this.acqNoSuffix = acqNoSuffix;
    }

    public String getReportLimitType() {
        return reportLimitType;
    }

    public void setReportLimitType(String reportLimitType) {
        this.reportLimitType = reportLimitType;
    }

    public String getMethodScope() {
        return methodScope;
    }

    public void setMethodScope(String methodScope) {
        this.methodScope = methodScope;
    }

    public String getMultipleItemGroup() {
        return multipleItemGroup;
    }

    public void setMultipleItemGroup(String multipleItemGroup) {
        this.multipleItemGroup = multipleItemGroup;
    }

    public String getGenerateLabel() {
        return generateLabel;
    }

    public void setGenerateLabel(String generateLabel) {
        this.generateLabel = generateLabel;
    }
}
