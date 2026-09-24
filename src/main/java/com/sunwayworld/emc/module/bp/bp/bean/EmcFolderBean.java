package com.sunwayworld.emc.module.bp.bp.bean;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.format.annotation.DateTimeFormat;

import com.sunwayworld.emc.framework.constant.EmcBizConstant;
import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.NotNull;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractAuditable;

/**
 * @author zhangsy@sunwayworld.com
 * @date 2019-10-12
 */

@Table("T_EMC_FOLDER")
public class EmcFolderBean extends AbstractAuditable<Long> {

    @Transient
    private static final long serialVersionUID = -5528091725703528072L;

    @Id
    private Long id;// 主键

    private String folderNo;// 点位编号

    private Long projId;// 项目编码

    private Long paramProjId;// 项目编码-气采样参数项目

    private String source;// 来源

    private String sampleOrgId;// 采样科室编码

    private String sampleOrgName;// 采样科室名称

    private String monitorTmplId;// 监测模板编码

    private String orderCategory;// 样品类型

    private Long monitorPointId;// 监测点ID

    private String monitorPointNo;// 监测点编码

    private String monitorPointName;// 监测点名称

    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate planSampleBeginDate;// 计划采样开始日期(江苏新锐)

    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate planSampleDate;// 计划采样日期

    private String sceneUserId;// 现场负责人编码

    private String sceneUserName;// 现场负责人名称

    private String planSampleUserId; // 计划采样人

    private String planSampleUserName; // 计划采样人名称

    private String sampleUserId;// 采样人编码

    private String sampleUserName;// 采样人名称

    private String monitorPointCategoryId;// 监测点类型编码

    private String monitorPointCategory;// 监测点类型

    private String remark;// 备注

    @NotNull(defaultValue = Constant.NO)
    private String termination;// 是否终止

    @NotNull(defaultValue = EmcBizConstant.FOLDER_STATUS_DRAFT)
    private String status;// 状态

    private String longitude; // 经度

    private String latitude; // 纬度

    private String runId; // 批号

    private String mobileId; // 移动端点位ID

    private String river; // 河流

    private String tributary; // 支流

    private String section; // 断面

    private String sectionConditionDesc; // 断面情况描述

    private String sectionWaterWidth; // 断面水面宽度

    private String sectionWaterDepth; // 断面水深

    private String sectionWaterQuality; // 断面水质

    private String createdById;// 制单人编码

    private String createdByName;// 制单人名称

    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;// 制单时间

    private String createdByOrgId;// 制单人单位编码

    private String createdByOrgName;// 制单人单位名称
    /**
     * 广州分析测试中心
     */
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate closingSampleDate;// 采样截止日期

    /**
     * 上海水文
     **/
    private String flowDirection;//水流方向
    private String realityLongitude;//实际采样经度
    private String realityLatitude;//实际采样纬度
    private String realityDistance;//实际采样距离
    private String samplingRange;////采样范围

    private String samplingDepth;//采样深度(江苏新锐)

    @NotNull(defaultValue = Constant.NO)
    private String emissionStatus;// 排放状态

    private String waterTemperature;// 水温

    private String turbidity;// 浊度

    public String getWaterTemperature() {
        return waterTemperature;
    }

    public void setWaterTemperature(String waterTemperature) {
        this.waterTemperature = waterTemperature;
    }

    public String getTurbidity() {
        return turbidity;
    }

    public void setTurbidity(String turbidity) {
        this.turbidity = turbidity;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getFolderNo() {
        return folderNo;
    }

    public void setFolderNo(String folderNo) {
        this.folderNo = folderNo;
    }

    public Long getProjId() {
        return projId;
    }

    public void setProjId(Long projId) {
        this.projId = projId;
    }

    public Long getParamProjId() {
        return paramProjId;
    }

    public void setParamProjId(Long paramProjId) {
        this.paramProjId = paramProjId;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSampleOrgId() {
        return sampleOrgId;
    }

    public void setSampleOrgId(String sampleOrgId) {
        this.sampleOrgId = sampleOrgId;
    }

    public String getSampleOrgName() {
        return sampleOrgName;
    }

    public void setSampleOrgName(String sampleOrgName) {
        this.sampleOrgName = sampleOrgName;
    }

    public String getMonitorTmplId() {
        return monitorTmplId;
    }

    public void setMonitorTmplId(String monitorTmplId) {
        this.monitorTmplId = monitorTmplId;
    }

    public String getOrderCategory() {
        return orderCategory;
    }

    public void setOrderCategory(String orderCategory) {
        this.orderCategory = orderCategory;
    }

    public Long getMonitorPointId() {
        return monitorPointId;
    }

    public void setMonitorPointId(Long monitorPointId) {
        this.monitorPointId = monitorPointId;
    }

    public String getMonitorPointNo() {
        return monitorPointNo;
    }

    public void setMonitorPointNo(String monitorPointNo) {
        this.monitorPointNo = monitorPointNo;
    }

    public String getMonitorPointName() {
        return monitorPointName;
    }

    public void setMonitorPointName(String monitorPointName) {
        this.monitorPointName = monitorPointName;
    }

    public LocalDate getPlanSampleDate() {
        return planSampleDate;
    }

    public void setPlanSampleDate(LocalDate planSampleDate) {
        this.planSampleDate = planSampleDate;
    }

    public String getSceneUserId() {
        return sceneUserId;
    }

    public void setSceneUserId(String sceneUserId) {
        this.sceneUserId = sceneUserId;
    }

    public String getSceneUserName() {
        return sceneUserName;
    }

    public void setSceneUserName(String sceneUserName) {
        this.sceneUserName = sceneUserName;
    }

    public String getSampleUserId() {
        return sampleUserId;
    }

    public void setSampleUserId(String sampleUserId) {
        this.sampleUserId = sampleUserId;
    }

    public String getSampleUserName() {
        return sampleUserName;
    }

    public void setSampleUserName(String sampleUserName) {
        this.sampleUserName = sampleUserName;
    }

    public String getMonitorPointCategoryId() {
        return monitorPointCategoryId;
    }

    public void setMonitorPointCategoryId(String monitorPointCategoryId) {
        this.monitorPointCategoryId = monitorPointCategoryId;
    }

    public String getMonitorPointCategory() {
        return monitorPointCategory;
    }

    public void setMonitorPointCategory(String monitorPointCategory) {
        this.monitorPointCategory = monitorPointCategory;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getTermination() {
        return termination;
    }

    public void setTermination(String termination) {
        this.termination = termination;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getRunId() {
        return runId;
    }

    public void setRunId(String runId) {
        this.runId = runId;
    }

    public String getMobileId() {
        return mobileId;
    }

    public void setMobileId(String mobileId) {
        this.mobileId = mobileId;
    }

    public String getRiver() {
        return river;
    }

    public void setRiver(String river) {
        this.river = river;
    }

    public String getTributary() {
        return tributary;
    }

    public void setTributary(String tributary) {
        this.tributary = tributary;
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

    public String getPlanSampleUserId() {
        return planSampleUserId;
    }

    public void setPlanSampleUserId(String planSampleUserId) {
        this.planSampleUserId = planSampleUserId;
    }

    public String getPlanSampleUserName() {
        return planSampleUserName;
    }

    public void setPlanSampleUserName(String planSampleUserName) {
        this.planSampleUserName = planSampleUserName;
    }

    public LocalDate getClosingSampleDate() {
        return closingSampleDate;
    }

    public void setClosingSampleDate(LocalDate closingSampleDate) {
        this.closingSampleDate = closingSampleDate;
    }

    public LocalDate getPlanSampleBeginDate() {
        return planSampleBeginDate;
    }

    public void setPlanSampleBeginDate(LocalDate planSampleBeginDate) {
        this.planSampleBeginDate = planSampleBeginDate;
    }

    public String getFlowDirection() {
        return flowDirection;
    }

    public void setFlowDirection(String flowDirection) {
        this.flowDirection = flowDirection;
    }

    public String getRealityLongitude() {
        return realityLongitude;
    }

    public void setRealityLongitude(String realityLongitude) {
        this.realityLongitude = realityLongitude;
    }

    public String getRealityLatitude() {
        return realityLatitude;
    }

    public void setRealityLatitude(String realityLatitude) {
        this.realityLatitude = realityLatitude;
    }

    public String getRealityDistance() {
        return realityDistance;
    }

    public void setRealityDistance(String realityDistance) {
        this.realityDistance = realityDistance;
    }

    public String getSamplingRange() {
        return samplingRange;
    }

    public void setSamplingRange(String samplingRange) {
        this.samplingRange = samplingRange;
    }

    public String getSamplingDepth() {
        return samplingDepth;
    }

    public void setSamplingDepth(String samplingDepth) {
        this.samplingDepth = samplingDepth;
    }

    public String getEmissionStatus() {
        return emissionStatus;
    }

    public void setEmissionStatus(String emissionStatus) {
        this.emissionStatus = emissionStatus;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getSectionConditionDesc() {
        return sectionConditionDesc;
    }

    public void setSectionConditionDesc(String sectionConditionDesc) {
        this.sectionConditionDesc = sectionConditionDesc;
    }

    public String getSectionWaterWidth() {
        return sectionWaterWidth;
    }

    public void setSectionWaterWidth(String sectionWaterWidth) {
        this.sectionWaterWidth = sectionWaterWidth;
    }

    public String getSectionWaterDepth() {
        return sectionWaterDepth;
    }

    public void setSectionWaterDepth(String sectionWaterDepth) {
        this.sectionWaterDepth = sectionWaterDepth;
    }

    public String getSectionWaterQuality() {
        return sectionWaterQuality;
    }

    public void setSectionWaterQuality(String sectionWaterQuality) {
        this.sectionWaterQuality = sectionWaterQuality;
    }
}
