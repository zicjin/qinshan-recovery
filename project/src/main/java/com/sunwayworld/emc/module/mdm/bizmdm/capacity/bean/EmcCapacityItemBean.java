package com.sunwayworld.emc.module.mdm.bizmdm.capacity.bean;

import java.time.LocalDateTime;

import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.format.annotation.DateTimeFormat;

import com.sunwayworld.emc.framework.constant.EmcBizConstant;
import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.NotNull;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractPersistable;
import com.sunwayworld.framework.support.domain.Activatable;

/**
 * 监测能力分析项表
 *
 * @author yuanhh@sunwayworld.com
 * @date 2019-09-19
 */

@Table("T_EMC_CAPACITY_ITEM")
public class EmcCapacityItemBean extends AbstractPersistable<Long>implements Activatable<Long> {

    @Transient
    private static final long serialVersionUID = -1628853842028921670L;

    @Id
    private Long id;// 主键

    private Long itemId; // 总库分析项ID

    private Long testCapacityId; // 监测能力库ID

    private Long capItemGroupId; // 监测能力分析项组ID

    private Long testMethodId;// 测试方法ID

    private String itemName;// 分析项名称

    private String itemAliasName;// 分析项别名

    private Long repeatItem;// 重复数

    private String lowLimit;// 检出低限

    private String unitName;// 单位名称

    private String revisionRule;// 修约规则

    private String script;// 公式

    @NotNull(defaultValue = "0")
    private String scriptFlag;// 是否存在公式

    @NotNull(defaultValue = EmcBizConstant.ANALYSISCATEGORY_ANALYSIS)
    private String analysisCategory;// 分析类型

    private String originalRecordDisplay;// 原始记录中显示

    private String reportDisplay;// 报告中显示

    private String param;// 是否参数

    private Long standSetId;// 标准设置编码

    private String defaultResult;// 默认结果

    private String originalRecordName;// 原始记录别名

    private String highLimit;// 检出高限

    private String resultCategory;// 结果类型

    private String limitFlag;// 检出限标志

    private String interceptRevision;// 截距修约

    private String slopeRevision;// 斜率修约

    private String pnFunc;// PN函数类型

    @NotNull(defaultValue = Constant.YES)
    private String limitRevision;// 是否根据检出限修约

    private String qcCategoryId;// QC中登录

    private String qcCategoryName;// QC类型名称(多个)

    @NotNull(defaultValue = Constant.NO)
    private String calQcResult;// 计算质控结果

    @NotNull(defaultValue = Constant.NO)
    private String scientificNotation;// 是否科学计数法

    private Long orderNo;// 排序码

    private String negativeToZero; // 负数报出零

    private String allowableError; // 允许偏差

    private String avgCalcType; // 均值计算方式

    @NotNull(defaultValue = Constant.ACTIVATED_STATUS_YES)
    private String activatedFlag;// 启用标志

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

    //上海水文添加字段
    @NotNull(defaultValue = Constant.NO)
    private String isblank;// 是否展示空白均值
    //上海水文添加字段

    @NotNull(defaultValue = Constant.NO)
    private String substitutes;//是否替代物(江苏新锐添加字段)

    private String orgId;

    @NotNull(defaultValue = Constant.NO)
    private String sampleParam;// 是否气采样参数

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

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public Long getTestCapacityId() {
        return testCapacityId;
    }

    public void setTestCapacityId(Long testCapacityId) {
        this.testCapacityId = testCapacityId;
    }

    public Long getCapItemGroupId() {
        return capItemGroupId;
    }

    public void setCapItemGroupId(Long capItemGroupId) {
        this.capItemGroupId = capItemGroupId;
    }

    public Long getTestMethodId() {
        return testMethodId;
    }

    public void setTestMethodId(Long testMethodId) {
        this.testMethodId = testMethodId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemAliasName() {
        return itemAliasName;
    }

    public void setItemAliasName(String itemAliasName) {
        this.itemAliasName = itemAliasName;
    }

    public Long getRepeatItem() {
        return repeatItem;
    }

    public void setRepeatItem(Long repeatItem) {
        this.repeatItem = repeatItem;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public String getRevisionRule() {
        return revisionRule;
    }

    public void setRevisionRule(String revisionRule) {
        this.revisionRule = revisionRule;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public String getScriptFlag() {
        return scriptFlag;
    }

    public void setScriptFlag(String scriptFlag) {
        this.scriptFlag = scriptFlag;
    }

    public String getAnalysisCategory() {
        return analysisCategory;
    }

    public void setAnalysisCategory(String analysisCategory) {
        this.analysisCategory = analysisCategory;
    }

    public String getOriginalRecordDisplay() {
        return originalRecordDisplay;
    }

    public void setOriginalRecordDisplay(String originalRecordDisplay) {
        this.originalRecordDisplay = originalRecordDisplay;
    }

    public String getReportDisplay() {
        return reportDisplay;
    }

    public void setReportDisplay(String reportDisplay) {
        this.reportDisplay = reportDisplay;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public Long getStandSetId() {
        return standSetId;
    }

    public void setStandSetId(Long standSetId) {
        this.standSetId = standSetId;
    }

    public String getDefaultResult() {
        return defaultResult;
    }

    public void setDefaultResult(String defaultResult) {
        this.defaultResult = defaultResult;
    }

    public String getOriginalRecordName() {
        return originalRecordName;
    }

    public void setOriginalRecordName(String originalRecordName) {
        this.originalRecordName = originalRecordName;
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

    public String getResultCategory() {
        return resultCategory;
    }

    public void setResultCategory(String resultCategory) {
        this.resultCategory = resultCategory;
    }

    public String getLimitFlag() {
        return limitFlag;
    }

    public void setLimitFlag(String limitFlag) {
        this.limitFlag = limitFlag;
    }

    public String getInterceptRevision() {
        return interceptRevision;
    }

    public void setInterceptRevision(String interceptRevision) {
        this.interceptRevision = interceptRevision;
    }

    public String getSlopeRevision() {
        return slopeRevision;
    }

    public void setSlopeRevision(String slopeRevision) {
        this.slopeRevision = slopeRevision;
    }

    public String getPnFunc() {
        return pnFunc;
    }

    public void setPnFunc(String pnFunc) {
        this.pnFunc = pnFunc;
    }

    public String getLimitRevision() {
        return limitRevision;
    }

    public void setLimitRevision(String limitRevision) {
        this.limitRevision = limitRevision;
    }

    public String getQcCategoryId() {
        return qcCategoryId;
    }

    public void setQcCategoryId(String qcCategoryId) {
        this.qcCategoryId = qcCategoryId;
    }

    public String getQcCategoryName() {
        return qcCategoryName;
    }

    public void setQcCategoryName(String qcCategoryName) {
        this.qcCategoryName = qcCategoryName;
    }

    public String getCalQcResult() {
        return calQcResult;
    }

    public void setCalQcResult(String calQcResult) {
        this.calQcResult = calQcResult;
    }

    public String getScientificNotation() {
        return scientificNotation;
    }

    public void setScientificNotation(String scientificNotation) {
        this.scientificNotation = scientificNotation;
    }

    public Long getOrderNo() {
        return orderNo;
    }

    public String getNegativeToZero() {
        return negativeToZero;
    }

    public void setNegativeToZero(String negativeToZero) {
        this.negativeToZero = negativeToZero;
    }

    public String getAllowableError() {
        return allowableError;
    }

    public void setAllowableError(String allowableError) {
        this.allowableError = allowableError;
    }

    public String getAvgCalcType() {
        return avgCalcType;
    }

    public void setAvgCalcType(String avgCalcType) {
        this.avgCalcType = avgCalcType;
    }

    public void setOrderNo(Long orderNo) {
        this.orderNo = orderNo;
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

	public String getIsblank() {
		return isblank;
	}

	public void setIsblank(String isblank) {
		this.isblank = isblank;
	}

    public String getSubstitutes() {
        return substitutes;
    }

    public void setSubstitutes(String substitutes) {
        this.substitutes = substitutes;
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

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }
}
