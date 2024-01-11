package com.sunwayworld.emc.module.mdm.bizmdm.client.bean;

import java.time.LocalDateTime;

import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.format.annotation.DateTimeFormat;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractPersistable;
import com.sunwayworld.framework.support.domain.Insertable;

/**
 * 检测点
 *
 * @author magh@sunwayworld.com
 * @date 2019-10-19
 */

@Table("T_EMC_MONITOR_POINT")
public class EmcMonitorPointBean extends AbstractPersistable<Long> implements Insertable<Long> {

    @Transient
    private static final long serialVersionUID = -6184348445678892345L;

    @Id
    private Long id;// 主键

    private Long clientId;// 客户编码

    private Long orderNo;//排序码

    private String monitorPointNo;// 监测点编码

    private String monitorPointName;// 监测点名称

    private String monitorPointCategoryId;// 监测点类型编码

    private String monitorPointType;// 监测点类别

    private String monitorPlace;// 监测地点

    private String indexId;// 样品指标id

    private String export;// 是否出口

    private String monitorPointLevel;// 监测点级别

    private String contrastFactor;// 编码对照因子

    private String longitude;// 经度

    private String latitude;// 纬度

    private String controllerLever;// 控制级别

    private String remark;// 备注

    private String river; // 河流

    private String tributary; // 支流

    private String creditCode; // 比对单位信用代码

    private String industry; // 比对单位所属行业

    private String createdById;// 制单人编码

    private String createdByName;// 制单人名称

    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;// 制单时间

    private String createdByOrgId;// 制单人单位编码

    private String createdByOrgName;// 制单人单位名称

    private Long defaultCycle; // 默认周期

    private Long defaultFrequency; // 默认频次


    /**
     * 上海水文
     **/
    private String flowDirection;//水流方向

    private String samplingRange;//采样范围

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public Long getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(Long orderNo) {
        this.orderNo = orderNo;
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

    public String getMonitorPointCategoryId() {
        return monitorPointCategoryId;
    }

    public void setMonitorPointCategoryId(String monitorPointCategoryId) {
        this.monitorPointCategoryId = monitorPointCategoryId;
    }

    public String getMonitorPointType() {
        return monitorPointType;
    }

    public void setMonitorPointType(String monitorPointType) {
        this.monitorPointType = monitorPointType;
    }

    public String getMonitorPlace() {
        return monitorPlace;
    }

    public void setMonitorPlace(String monitorPlace) {
        this.monitorPlace = monitorPlace;
    }

    public String getIndexId() {
        return indexId;
    }

    public void setIndexId(String indexId) {
        this.indexId = indexId;
    }

    public String getExport() {
        return export;
    }

    public void setExport(String export) {
        this.export = export;
    }

    public String getMonitorPointLevel() {
        return monitorPointLevel;
    }

    public void setMonitorPointLevel(String monitorPointLevel) {
        this.monitorPointLevel = monitorPointLevel;
    }

    public String getContrastFactor() {
        return contrastFactor;
    }

    public void setContrastFactor(String contrastFactor) {
        this.contrastFactor = contrastFactor;
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

    public String getControllerLever() {
        return controllerLever;
    }

    public void setControllerLever(String controllerLever) {
        this.controllerLever = controllerLever;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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

    public String getCreditCode() {
        return creditCode;
    }

    public void setCreditCode(String creditCode) {
        this.creditCode = creditCode;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
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

    public Long getDefaultCycle() {
        return defaultCycle;
    }

    public void setDefaultCycle(Long defaultCycle) {
        this.defaultCycle = defaultCycle;
    }

    public Long getDefaultFrequency() {
        return defaultFrequency;
    }

    public void setDefaultFrequency(Long defaultFrequency) {
        this.defaultFrequency = defaultFrequency;
    }

    public String getFlowDirection() {
        return flowDirection;
    }

    public void setFlowDirection(String flowDirection) {
        this.flowDirection = flowDirection;
    }

    public String getSamplingRange() {
        return samplingRange;
    }

    public void setSamplingRange(String samplingRange) {
        this.samplingRange = samplingRange;
    }
}
