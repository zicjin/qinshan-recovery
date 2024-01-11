package com.sunwayworld.emc.module.item.quotation.detectionfactor.bean;

import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;
import com.alibaba.fastjson.annotation.JSONField;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.Insertable;
import com.sunwayworld.framework.support.domain.AbstractInsertable;

/**
 * 检测因子价格管理表
 * 
 * @author xiongjie@sunwayworld.com@sunwayworld.com
 * @date 2021-03-25
 */
@Table("T_EMC_QUOTATION_DETECTIONFACTOR")
public class EmcDetectionfactorBean extends AbstractInsertable<Long> implements Insertable<Long> {

    @Transient
    private static final long serialVersionUID = -82566196618711043L;

    @Id
    private Long id;// 主键
    private String monitorTmplGroupId;// 样品类型ID
    private Long testId;// 测试ID
    private Long methodId;// 方法ID
    private Long itemGroupId;// 分析项组ID
    private String trialScope;// 试用范围
    private String tmplGroupDesc;// 样品类型
    private String testName;// 测试名称
    private String monitorTmplGroupName;// 样品类型
    private String methodName;// 方法名称
    private String itemGroupName;// 分析项组名称
    private String samplePrice;// 采样单价
    private String analysisPrice;// 分析单价
    private String remark;// 备注
    private String createdById;// 制单人编码
    private String createdByName;// 制单人名称
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;// 制单时间
    private String createdByOrgId;// 制单人单位编码
    private String createdByOrgName;// 制单人单位名称

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getTestId() {
        return testId;
    }

    public void setTestId(Long testId) {
        this.testId = testId;
    }

    public Long getMethodId() {
        return methodId;
    }

    public void setMethodId(Long methodId) {
        this.methodId = methodId;
    }

    public Long getItemGroupId() {
        return itemGroupId;
    }

    public void setItemGroupId(Long itemGroupId) {
        this.itemGroupId = itemGroupId;
    }

    public String getTrialScope() {
        return trialScope;
    }

    public void setTrialScope(String trialScope) {
        this.trialScope = trialScope;
    }

    public String getTmplGroupDesc() {
        return tmplGroupDesc;
    }

    public void setTmplGroupDesc(String tmplGroupDesc) {
        this.tmplGroupDesc = tmplGroupDesc;
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

    public String getItemGroupName() {
        return itemGroupName;
    }

    public void setItemGroupName(String itemGroupName) {
        this.itemGroupName = itemGroupName;
    }

    public String getSamplePrice() {
        return samplePrice;
    }

    public void setSamplePrice(String samplePrice) {
        this.samplePrice = samplePrice;
    }

    public String getAnalysisPrice() {
        return analysisPrice;
    }

    public void setAnalysisPrice(String analysisPrice) {
        this.analysisPrice = analysisPrice;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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

    public String getMonitorTmplGroupId() {
        return monitorTmplGroupId;
    }

    public void setMonitorTmplGroupId(String monitorTmplGroupId) {
        this.monitorTmplGroupId = monitorTmplGroupId;
    }

    public String getMonitorTmplGroupName() {
        return monitorTmplGroupName;
    }

    public void setMonitorTmplGroupName(String monitorTmplGroupName) {
        this.monitorTmplGroupName = monitorTmplGroupName;
    }
}
