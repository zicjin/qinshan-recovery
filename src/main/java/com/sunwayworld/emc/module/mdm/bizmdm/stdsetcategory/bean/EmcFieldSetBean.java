package com.sunwayworld.emc.module.mdm.bizmdm.stdsetcategory.bean;

import java.time.LocalDateTime;

import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.format.annotation.DateTimeFormat;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractPersistable;
import com.sunwayworld.framework.support.domain.Insertable;

/**
 * 字段设置表
 *
 * @author qianc@sunwayworld.com 2019年11月1日
 */
@Table("T_EMC_FIELD_SET")
public class EmcFieldSetBean extends AbstractPersistable<Long>implements Insertable<Long> {

    @Transient
    private static final long serialVersionUID = 8249107060449203642L;

    @Id
    private Long id;// 主键

    private Long standardSetId;// 标准设置id

    private String fieldName;// 字段名

    private String fieldTitle;// 字段标题

    private String display;// 是否显示

    private String width;// 宽度

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

    public Long getStandardSetId() {
        return standardSetId;
    }

    public void setStandardSetId(Long standardSetId) {
        this.standardSetId = standardSetId;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldTitle() {
        return fieldTitle;
    }

    public void setFieldTitle(String fieldTitle) {
        this.fieldTitle = fieldTitle;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
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

    @Override
    public boolean equals(Object object) {
        EmcFieldSetBean emcFieldSetBean = null;
        if (this == object) {
            return true;
        }
        if (object instanceof EmcFieldSetBean) {
            emcFieldSetBean = (EmcFieldSetBean) object;
            return this.getStandardSetId().equals(emcFieldSetBean.getStandardSetId()) && this.getFieldName().equals(emcFieldSetBean.getFieldName());
        }

        return false;
    }
}
