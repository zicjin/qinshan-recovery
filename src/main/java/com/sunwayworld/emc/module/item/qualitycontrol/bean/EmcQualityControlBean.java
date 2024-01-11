package com.sunwayworld.emc.module.item.qualitycontrol.bean;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.format.annotation.DateTimeFormat;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractAuditable;
import com.sunwayworld.framework.support.domain.Insertable;

/**
 * 质量控制计划
 *
 * @author magt@sunwayworld.com 2019/12/03
 *
 */
@Table("T_EMC_QUALITY_CONTROL")
public class EmcQualityControlBean extends AbstractAuditable<Long> implements Insertable<Long> {

    @Transient
    private static final long serialVersionUID = -1115003991658951047L;
    @Id
    private Long id; // 主键
    private String qualityControlCode; // 质控编码
    private String qualityControlScope; // 质控范围
    private String qualityControlCategory; // 质控类型
    private String qualityControlObject; // 质控对象
    private String qualityControlTarget; // 质控目标
    private String qualityControlOrgName; // 质控部门
    private String request; // 要求
    private String time; // 时间
    private String remark; // 备注
    private String standardMtl; // 标准物质
    private String testBasis; // 测试依据
    private String partakeUser; // 参加人员
    private String technicalRequest; // 技术要求
    private String envCondition; // 环境条件
    private String resourceConfig; // 资源配置
    private String testOrder; // 测试样品
    private String feasibility; // 计划可行性
    private String reviewUser; // 评审人员
    private String evaluateUser; // 评价人员
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate evaluateDate; // 评价日期
    private String originalRecord; // 原始记录
    private String controlResult; // 控制结论
    private String influence; // 影响因素
    private String potentialFactor; // 潜在因素
    private String precaution; // 预防措施
    private String createdById;// 制单人编码
    private String createdByName;// 制单人名称
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;// 制单时间
    private String createdByOrgId;// 制单人单位编码
    private String createdByOrgName;// 制单人单位名称

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQualityControlCode() {
        return qualityControlCode;
    }

    public void setQualityControlCode(String qualityControlCode) {
        this.qualityControlCode = qualityControlCode;
    }

    public String getQualityControlScope() {
        return qualityControlScope;
    }

    public void setQualityControlScope(String qualityControlScope) {
        this.qualityControlScope = qualityControlScope;
    }

    public String getQualityControlCategory() {
        return qualityControlCategory;
    }

    public void setQualityControlCategory(String qualityControlCategory) {
        this.qualityControlCategory = qualityControlCategory;
    }

    public String getQualityControlObject() {
        return qualityControlObject;
    }

    public void setQualityControlObject(String qualityControlObject) {
        this.qualityControlObject = qualityControlObject;
    }

    public String getQualityControlTarget() {
        return qualityControlTarget;
    }

    public void setQualityControlTarget(String qualityControlTarget) {
        this.qualityControlTarget = qualityControlTarget;
    }

    public String getQualityControlOrgName() {
        return qualityControlOrgName;
    }

    public void setQualityControlOrgName(String qualityControlOrgName) {
        this.qualityControlOrgName = qualityControlOrgName;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getStandardMtl() {
        return standardMtl;
    }

    public void setStandardMtl(String standardMtl) {
        this.standardMtl = standardMtl;
    }

    public String getTestBasis() {
        return testBasis;
    }

    public void setTestBasis(String testBasis) {
        this.testBasis = testBasis;
    }

    public String getPartakeUser() {
        return partakeUser;
    }

    public void setPartakeUser(String partakeUser) {
        this.partakeUser = partakeUser;
    }

    public String getTechnicalRequest() {
        return technicalRequest;
    }

    public void setTechnicalRequest(String technicalRequest) {
        this.technicalRequest = technicalRequest;
    }

    public String getEnvCondition() {
        return envCondition;
    }

    public void setEnvCondition(String envCondition) {
        this.envCondition = envCondition;
    }

    public String getResourceConfig() {
        return resourceConfig;
    }

    public void setResourceConfig(String resourceConfig) {
        this.resourceConfig = resourceConfig;
    }

    public String getTestOrder() {
        return testOrder;
    }

    public void setTestOrder(String testOrder) {
        this.testOrder = testOrder;
    }

    public String getFeasibility() {
        return feasibility;
    }

    public void setFeasibility(String feasibility) {
        this.feasibility = feasibility;
    }

    public String getReviewUser() {
        return reviewUser;
    }

    public void setReviewUser(String reviewUser) {
        this.reviewUser = reviewUser;
    }

    public String getEvaluateUser() {
        return evaluateUser;
    }

    public void setEvaluateUser(String evaluateUser) {
        this.evaluateUser = evaluateUser;
    }

    public LocalDate getEvaluateDate() {
        return evaluateDate;
    }

    public void setEvaluateDate(LocalDate evaluateDate) {
        this.evaluateDate = evaluateDate;
    }

    public String getOriginalRecord() {
        return originalRecord;
    }

    public void setOriginalRecord(String originalRecord) {
        this.originalRecord = originalRecord;
    }

    public String getControlResult() {
        return controlResult;
    }

    public void setControlResult(String controlResult) {
        this.controlResult = controlResult;
    }

    public String getInfluence() {
        return influence;
    }

    public void setInfluence(String influence) {
        this.influence = influence;
    }

    public String getPotentialFactor() {
        return potentialFactor;
    }

    public void setPotentialFactor(String potentialFactor) {
        this.potentialFactor = potentialFactor;
    }

    public String getPrecaution() {
        return precaution;
    }

    public void setPrecaution(String precaution) {
        this.precaution = precaution;
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

}
