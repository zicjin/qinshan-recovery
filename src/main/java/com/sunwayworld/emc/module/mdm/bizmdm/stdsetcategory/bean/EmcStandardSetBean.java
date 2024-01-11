package com.sunwayworld.emc.module.mdm.bizmdm.stdsetcategory.bean;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.format.annotation.DateTimeFormat;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractPersistable;
import com.sunwayworld.framework.support.domain.Insertable;

/**
 * 标准设置表
 *
 * @author qianc@sunwayworld.com 2019年11月1日
 */
@Table("T_EMC_STANDARD_SET")
public class EmcStandardSetBean extends AbstractPersistable<Long>implements Insertable<Long> {

    @Transient
    private static final long serialVersionUID = 7228475031444638108L;

    @Id
    private Long id;// 主键

    private Long standSetCategoryId;// 标准设置分类id

    private String setName;// 设置名称

    private String setDesc;// 设置描述

    private String script1;// 计算

    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private String validateScript;// 验证

    private String script2;// 计算2

    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;// 开始日期

    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate validDate;// 有效期限

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

    public Long getStandSetCategoryId() {
        return standSetCategoryId;
    }

    public void setStandSetCategoryId(Long standSetCategoryId) {
        this.standSetCategoryId = standSetCategoryId;
    }

    public String getSetName() {
        return setName;
    }

    public void setSetName(String setName) {
        this.setName = setName;
    }

    public String getSetDesc() {
        return setDesc;
    }

    public void setSetDesc(String setDesc) {
        this.setDesc = setDesc;
    }

    public String getScript1() {
        return script1;
    }

    public void setScript1(String script1) {
        this.script1 = script1;
    }

    public String getValidateScript() {
        return validateScript;
    }

    public void setValidateScript(String validateScript) {
        this.validateScript = validateScript;
    }

    public String getScript2() {
        return script2;
    }

    public void setScript2(String script2) {
        this.script2 = script2;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getValidDate() {
        return validDate;
    }

    public void setValidDate(LocalDate validDate) {
        this.validDate = validDate;
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
