package com.sunwayworld.emc.module.mdm.basemdm.datacorrelation.bean;

import java.time.LocalDateTime;

import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.format.annotation.DateTimeFormat;

import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.NotNull;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractPersistable;
import com.sunwayworld.framework.support.domain.Insertable;



/**
 * 数据相关性bean
 * @author zhangft@sunwayworld.com@sunwayworld.com
 * @date 2020-01-03
 */
@Table("T_EMC_DATA_CORRELATION")
public class EmcDataCorrelationBean extends AbstractPersistable<Long> implements Insertable<Long>{

    @Transient
    private static final long serialVersionUID = 2114839833243398976L;

    @Id
    private Long id;//主键
    private String relateCode;//关联代码
    private String relateName;//关联名称
    private String relateScript;//关联公式

    @NotNull(defaultValue = Constant.ACTIVATED_STATUS_YES)
    private String activatedFlag;// 启用标志

    private String activatedById;// 启用人编码

    private String activatedByName;// 启用人名称

    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime activatedTime;// 启用时间

    private String createdById;//制单人编码
    private String createdByName;//制单人名称
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;//制单时间
    private String createdByOrgId;//制单人单位编码
    private String createdByOrgName;//制单人单位名称

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getRelateCode() {
        return relateCode;
    }

    public void setRelateCode(String relateCode) {
        this.relateCode = relateCode;
    }
    public String getRelateName() {
        return relateName;
    }

    public void setRelateName(String relateName) {
        this.relateName = relateName;
    }
    public String getRelateScript() {
        return relateScript;
    }

    public void setRelateScript(String relateScript) {
        this.relateScript = relateScript;
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
