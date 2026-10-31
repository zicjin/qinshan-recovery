package com.sunwayworld.emc.module.mdm.equipt.equipt.bean;

import java.time.LocalDateTime;

import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.format.annotation.DateTimeFormat;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.NotNull;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractPersistable;
import com.sunwayworld.framework.support.domain.Insertable;

/**
 * 仪器维护记录表
 * @author cuikx@sunwayworld.com
 * @date 2019-11-21
 */

@Table("T_EMC_EQUIPT_RECORD")
public class EmcEquiptRecordBean extends AbstractPersistable<Long> implements Insertable<Long> {

    @Transient
    private static final long serialVersionUID = -4566599356858638758L;

    @Id
    private Long id;// 主键

    private Long equiptId;// 设备id

    private Long maintId;// 维护事件id

    private String maintEvent;// 维护事件

    private String verifyOrg;// 检定机构

    private String verifyOrgName;// 检定机构name

    private String certNo;// 证书编号

    private String certCategory;// 证书类型

    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime certEffectDate;// 证书生效日

    private String status;// 现在状态

    private String startStatus;// 开机状态

    private String airSaturation;// 空气饱和度

    private String addIce;// 加冰

    private String maintSituation;// 维护情况

    private String remark;// 备注

    @NotNull(defaultValue ="1")
    private String completeFlag;// 是否完成

    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime mainDate;

    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime maintEndDate;

    private String createdById;// 制单人编码

    private String createdByName;// 制单人名称

    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;// 制单时间

    private String createdByOrgId;// 制单人单位编码

    private String createdByOrgName;// 制单人单位名称

    //private String completeFlagName;// 是否完成Name

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getEquiptId() {
        return equiptId;
    }

    public void setEquiptId(Long equiptId) {
        this.equiptId = equiptId;
    }

    public Long getMaintId() {
        return maintId;
    }

    public void setMaintId(Long maintId) {
        this.maintId = maintId;
    }

    public String getCertNo() {
        return certNo;
    }

    public void setCertNo(String certNo) {
        this.certNo = certNo;
    }

    public String getCertCategory() {
        return certCategory;
    }

    public void setCertCategory(String certCategory) {
        this.certCategory = certCategory;
    }

    public LocalDateTime getCertEffectDate() {
        return certEffectDate;
    }

    public void setCertEffectDate(LocalDateTime certEffectDate) {
        this.certEffectDate = certEffectDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStartStatus() {
        return startStatus;
    }

    public void setStartStatus(String startStatus) {
        this.startStatus = startStatus;
    }

    public String getAirSaturation() {
        return airSaturation;
    }

    public void setAirSaturation(String airSaturation) {
        this.airSaturation = airSaturation;
    }

    public String getAddIce() {
        return addIce;
    }

    public void setAddIce(String addIce) {
        this.addIce = addIce;
    }

    public String getMaintSituation() {
        return maintSituation;
    }

    public void setMaintSituation(String maintSituation) {
        this.maintSituation = maintSituation;
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

    public String getMaintEvent() {
        return maintEvent;
    }

    public void setMaintEvent(String maintEvent) {
        this.maintEvent = maintEvent;
    }

    public String getVerifyOrg() {
        return verifyOrg;
    }

    public void setVerifyOrg(String verifyOrg) {
        this.verifyOrg = verifyOrg;
    }

    public String getVerifyOrgName() {
        return verifyOrgName;
    }

    public void setVerifyOrgName(String verifyOrgName) {
        this.verifyOrgName = verifyOrgName;
    }

    public String getCompleteFlag() {

        return completeFlag;
    }

    public void setCompleteFlag(String completeFlag) {

        this.completeFlag = completeFlag;
    }

    public LocalDateTime getMainDate() {
        return mainDate;
    }

    public void setMainDate(LocalDateTime mainDate) {
        this.mainDate = mainDate;
    }

    public LocalDateTime getMaintEndDate() {
        return maintEndDate;
    }

    public void setMaintEndDate(LocalDateTime maintEndDate) {
        this.maintEndDate = maintEndDate;
    }
}
