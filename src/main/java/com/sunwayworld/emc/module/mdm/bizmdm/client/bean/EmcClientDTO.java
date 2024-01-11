package com.sunwayworld.emc.module.mdm.bizmdm.client.bean;

import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.tree.bean.AbstractTreeNode;

/**
 * 
 * 监测点导入DTO
 *
 */
public class EmcClientDTO extends AbstractTreeNode {

    @Transient
    private static final long serialVersionUID = 7845243673949907687L;

    private String clientName; // 客户名称

    private String clientCategoryName; // 客户类别名称

    private String clientCategory;// 客户类别编码

    private String clientSCategoryName; // 客户小类名称

    private String clientSCategory;// 客户小类编码

    private String areaName; // 区域名称

    private Long areaId;// 区域编码

    private String phone; // 电话

    private String address; // 地址

    private String website; // 网址

    private String mail; // 邮政信箱

    private String scale; // 企业规模

    private String creditCode; // 统一社会信用代码

    private String clientLongitude; // 经度

    private String clientLatitude; // 纬度

    // 监测点属性
    private Long clientId;// 客户编码

    private Long orderNo; // 排序码

    private String monitorPointNo; // 监测点编码

    private String monitorPointName; // 监测点名称

    private Long defaultCycle; // 周期

    private Long defaultFrequency; // 频次

    private String monitorPointCategoryName; // 监测点类型名称

    private String monitorPointCategoryId;// 监测点类型编码

    private String monitorPointTypeName; // 监测点类别名称

    private String monitorPointType;// 监测点类别

    private String monitorPlace; // 检测地点

    private String exportName; // 是否出口名称

    private String export;// 是否出口

    private String monitorPointLevelName; // 监测点级别名称

    private String monitorPointLevel;// 监测点级别

    private String contrastFactor; // 编码对照因子

    private String monitorPointLongitude; // 监测点经度

    private String monitorPointLatitude; // 监测点纬度

    private String controllerLeverName; // 控制级别名称

    private String controllerLever;// 控制级别

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientCategoryName() {
        return clientCategoryName;
    }

    public void setClientCategoryName(String clientCategoryName) {
        this.clientCategoryName = clientCategoryName;
    }

    public String getClientCategory() {
        return clientCategory;
    }

    public void setClientCategory(String clientCategory) {
        this.clientCategory = clientCategory;
    }

    public String getClientSCategoryName() {
        return clientSCategoryName;
    }

    public void setClientSCategoryName(String clientSCategoryName) {
        this.clientSCategoryName = clientSCategoryName;
    }

    public String getClientSCategory() {
        return clientSCategory;
    }

    public void setClientSCategory(String clientSCategory) {
        this.clientSCategory = clientSCategory;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public Long getAreaId() {
        return areaId;
    }

    public void setAreaId(Long areaId) {
        this.areaId = areaId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getScale() {
        return scale;
    }

    public void setScale(String scale) {
        this.scale = scale;
    }

    public String getCreditCode() {
        return creditCode;
    }

    public void setCreditCode(String creditCode) {
        this.creditCode = creditCode;
    }

    public String getClientLongitude() {
        return clientLongitude;
    }

    public void setClientLongitude(String clientLongitude) {
        this.clientLongitude = clientLongitude;
    }

    public String getClientLatitude() {
        return clientLatitude;
    }

    public void setClientLatitude(String clientLatitude) {
        this.clientLatitude = clientLatitude;
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

    public String getMonitorPointCategoryName() {
        return monitorPointCategoryName;
    }

    public void setMonitorPointCategoryName(String monitorPointCategoryName) {
        this.monitorPointCategoryName = monitorPointCategoryName;
    }

    public String getMonitorPointCategoryId() {
        return monitorPointCategoryId;
    }

    public void setMonitorPointCategoryId(String monitorPointCategoryId) {
        this.monitorPointCategoryId = monitorPointCategoryId;
    }

    public String getMonitorPointTypeName() {
        return monitorPointTypeName;
    }

    public void setMonitorPointTypeName(String monitorPointTypeName) {
        this.monitorPointTypeName = monitorPointTypeName;
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

    public String getExportName() {
        return exportName;
    }

    public void setExportName(String exportName) {
        this.exportName = exportName;
    }

    public String getExport() {
        return export;
    }

    public void setExport(String export) {
        this.export = export;
    }

    public String getMonitorPointLevelName() {
        return monitorPointLevelName;
    }

    public void setMonitorPointLevelName(String monitorPointLevelName) {
        this.monitorPointLevelName = monitorPointLevelName;
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

    public String getMonitorPointLongitude() {
        return monitorPointLongitude;
    }

    public void setMonitorPointLongitude(String monitorPointLongitude) {
        this.monitorPointLongitude = monitorPointLongitude;
    }

    public String getMonitorPointLatitude() {
        return monitorPointLatitude;
    }

    public void setMonitorPointLatitude(String monitorPointLatitude) {
        this.monitorPointLatitude = monitorPointLatitude;
    }

    public String getControllerLeverName() {
        return controllerLeverName;
    }

    public void setControllerLeverName(String controllerLeverName) {
        this.controllerLeverName = controllerLeverName;
    }

    public String getControllerLever() {
        return controllerLever;
    }

    public void setControllerLever(String controllerLever) {
        this.controllerLever = controllerLever;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }
}
