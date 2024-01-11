package com.sunwayworld.emc.module.mdm.bizmdm.indexcategory.bean;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.format.annotation.DateTimeFormat;

import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.NotNull;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import com.sunwayworld.framework.support.domain.Activatable;

/**
 * 指标分类
 *
 * @author zhangsy@sunwayworld.com 2019/9/16
 *
 */
@Table("T_EMC_INDEX_CATEGORY")
public class EmcIndexCategoryBean extends AbstractInsertable<Long>implements Activatable<Long> {

    @Transient
    private static final long serialVersionUID = -6635454576778768764L;

    @Id
    private Long id; // id<主键>

    private String stdName; // 标准名称

    private String monitorTmplGroupId; // 监测模板组编码

    private String propertyTmplId; // 标准设置

    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate; // 开始日期

    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate expireDate; // 过期日期

    private String remark; // 备注

    @NotNull(defaultValue = Constant.ACTIVATED_STATUS_YES)
    private String activatedFlag;// 启用标志

    private String activatedById;// 启用人编码

    private String activatedByName;// 启用人名称

    private LocalDateTime activatedTime;// 启用时间

    @NotNull(defaultValue = "0")
    private String eStandard; // 是否易标准

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStdName() {
        return stdName;
    }

    public void setStdName(String stdName) {
        this.stdName = stdName;
    }

    public String getMonitorTmplGroupId() {
        return monitorTmplGroupId;
    }

    public void setMonitorTmplGroupId(String monitorTmplGroupId) {
        this.monitorTmplGroupId = monitorTmplGroupId;
    }

    public String getPropertyTmplId() {
        return propertyTmplId;
    }

    public void setPropertyTmplId(String propertyTmplId) {
        this.propertyTmplId = propertyTmplId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(LocalDate expireDate) {
        this.expireDate = expireDate;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getActivatedFlag() {
        return activatedFlag;
    }

    public void setActivatedFlag(String activatedFlag) {
        this.activatedFlag = activatedFlag;
    }

    public String getActivatedById() {
        return activatedById;
    }

    public void setActivatedById(String activatedById) {
        this.activatedById = activatedById;
    }

    public String getActivatedByName() {
        return activatedByName;
    }

    public void setActivatedByName(String activatedByName) {
        this.activatedByName = activatedByName;
    }

    public LocalDateTime getActivatedTime() {
        return activatedTime;
    }

    public void setActivatedTime(LocalDateTime activatedTime) {
        this.activatedTime = activatedTime;
    }

    public String getEStandard() {
        return eStandard;
    }

    public void setEStandard(String eStandard) {
        this.eStandard = eStandard;
    }


}
