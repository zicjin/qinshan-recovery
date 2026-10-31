package com.sunwayworld.emc.module.mdm.basemdm.monitortmplgroup.bean;

import java.time.LocalDateTime;

import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.NotNull;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import com.sunwayworld.framework.support.domain.Activatable;

/**
 * 监测模板组表
 *
 * @author magh@sunwayworld.com 2019年9月6日
 */

@Table("T_EMC_MONITOR_TMPL_GROUP")
public class EmcMonitorTmplGroupBean extends AbstractInsertable<Long> implements Activatable<Long> {

    @Transient
    private static final long serialVersionUID = 6701207184500331880L;

    @Id
    private Long id;// 主键

    private String monitorTmplGroup;// 监测模板组

    private String tmplGroupDesc;// 监测模板组描述

    @NotNull(defaultValue = "1")
    private Long orderNo;// 排序码

    private Long erpCategoryCode; // ERP产品分类编码

    @NotNull(defaultValue = Constant.ACTIVATED_STATUS_YES)
    private String activatedFlag;// 启用标志

    private String activatedById;// 启用人编码

    private String activatedByName;// 启用人名称

    private LocalDateTime activatedTime;// 启用时间

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getMonitorTmplGroup() {
        return monitorTmplGroup;
    }

    public void setMonitorTmplGroup(String monitorTmplGroup) {
        this.monitorTmplGroup = monitorTmplGroup;
    }

    public String getTmplGroupDesc() {
        return tmplGroupDesc;
    }

    public void setTmplGroupDesc(String tmplGroupDesc) {
        this.tmplGroupDesc = tmplGroupDesc;
    }

    public Long getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(Long orderNo) {
        this.orderNo = orderNo;
    }

    public Long getErpCategoryCode() {
        return erpCategoryCode;
    }

    public void setErpCategoryCode(Long erpCategoryCode) {
        this.erpCategoryCode = erpCategoryCode;
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
}
