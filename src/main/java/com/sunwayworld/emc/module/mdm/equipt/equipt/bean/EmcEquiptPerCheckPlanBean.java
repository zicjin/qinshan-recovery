package com.sunwayworld.emc.module.mdm.equipt.equipt.bean;

import java.time.LocalDateTime;

import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.format.annotation.DateTimeFormat;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractPersistable;
import com.sunwayworld.framework.support.domain.Insertable;

/**
 * @author liuyz@sunwayworld.com 2020年09月25日
 */
@Table("T_EMC_EQUIPT_PER_CHECK_PLAN")
public class EmcEquiptPerCheckPlanBean extends AbstractPersistable<Long> implements Insertable<Long> {

    @Transient
    private static final long serialVersionUID = 9151035879797549239L;

    @Id
    private Long id;// 主键

    private Long equiptId;// 仪器id

    private Long maintId;// 维护事件id

    private String maintEvent;// 维护事件

    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime maintDate;// 维护日期

    private String cycle;// 维护周期

    private Long warningDays;// 提前提醒（天）

    private String checkReason;// 核查依据

    private String checkEquipt;// 核查所使用的仪器设备

    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime maintEndDate;// 维护到期日期

    private String remark;// 备注

    private String checkWay; // 核查方式
    private String checkUserId; // 核查人ID
    private String checkUserName; // 核查人

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

    public String getMaintEvent() {
        return maintEvent;
    }

    public void setMaintEvent(String maintEvent) {
        this.maintEvent = maintEvent;
    }

    public LocalDateTime getMaintDate() {
        return maintDate;
    }

    public void setMaintDate(LocalDateTime maintDate) {
        this.maintDate = maintDate;
    }

    public String getCycle() {
        return cycle;
    }

    public void setCycle(String cycle) {
        this.cycle = cycle;
    }

    public Long getWarningDays() {
        return warningDays;
    }

    public void setWarningDays(Long warningDays) {
        this.warningDays = warningDays;
    }

    public String getCheckReason() {
        return checkReason;
    }

    public void setCheckReason(String checkReason) {
        this.checkReason = checkReason;
    }

    public String getCheckEquipt() {
        return checkEquipt;
    }

    public void setCheckEquipt(String checkEquipt) {
        this.checkEquipt = checkEquipt;
    }

    public LocalDateTime getMaintEndDate() {
        return maintEndDate;
    }

    public void setMaintEndDate(LocalDateTime maintEndDate) {
        this.maintEndDate = maintEndDate;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCheckWay() {
        return checkWay;
    }

    public void setCheckWay(String checkWay) {
        this.checkWay = checkWay;
    }

    public String getCheckUserId() {
        return checkUserId;
    }

    public void setCheckUserId(String checkUserId) {
        this.checkUserId = checkUserId;
    }

    public String getCheckUserName() {
        return checkUserName;
    }

    public void setCheckUserName(String checkUserName) {
        this.checkUserName = checkUserName;
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
