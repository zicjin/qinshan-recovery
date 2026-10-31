package com.sunwayworld.emc.module.bp.info.infotmpl.bean;

import java.time.LocalDateTime;

import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.format.annotation.DateTimeFormat;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractPersistable;
import com.sunwayworld.framework.support.domain.Insertable;


/**
 * 上报模板字段
 * @author zhangft@sunwayworld.com
 * @date 2019-11-19
 */

@Table("T_EMC_INFO_TMPL_FIELD")
public class EmcInfoTmplFieldBean extends AbstractPersistable<Long> implements Insertable<Long>{
    @Transient
    private static final long serialVersionUID = 2550391147960259944L;

    @Id
    private Long id;//字段编码

    private Long infoTmplId;//上报模板ID

    private String field;//字段名

    private String fieldTitle;//字段标题

    private Long width;//宽度

    private String display;//显示属性

    private String lowLimit;//检出低限

    private String unitName;//单位名称

    private String revisionRule;//修约规则

    private String transform;//是否转换列

    private Long primaryIndex;//主键列数

    private String primaryName;//主键名称

    private String bizField;//对应系统字段

    private String defaultValue;//默认值

    private String createdById;//制单人编码

    private String createdByName;//制单人名称

    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;//制单时间

    private String createdByOrgId;//制单人单位编码

    private String createdByOrgName;//制单人单位名称


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public Long getInfoTmplId() {
        return infoTmplId;
    }

    public void setInfoTmplId(Long infoTmplId) {
        this.infoTmplId = infoTmplId;
    }
    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }
    public String getFieldTitle() {
        return fieldTitle;
    }

    public void setFieldTitle(String fieldTitle) {
        this.fieldTitle = fieldTitle;
    }
    public Long getWidth() {
        return width;
    }

    public void setWidth(Long width) {
        this.width = width;
    }
    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }
    public String getLowLimit() {
        return lowLimit;
    }

    public void setLowLimit(String lowLimit) {
        this.lowLimit = lowLimit;
    }
    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }
    public String getRevisionRule() {
        return revisionRule;
    }

    public void setRevisionRule(String revisionRule) {
        this.revisionRule = revisionRule;
    }
    public String getTransform() {
        return transform;
    }

    public void setTransform(String transform) {
        this.transform = transform;
    }
    public Long getPrimaryIndex() {
        return primaryIndex;
    }

    public void setPrimaryIndex(Long primaryIndex) {
        this.primaryIndex = primaryIndex;
    }
    public String getPrimaryName() {
        return primaryName;
    }

    public void setPrimaryName(String primaryName) {
        this.primaryName = primaryName;
    }
    public String getBizField() {
        return bizField;
    }

    public void setBizField(String bizField) {
        this.bizField = bizField;
    }
    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
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
}
