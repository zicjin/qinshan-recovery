package com.sunwayworld.emc.module.bp.result.pretreatment.bean;

import java.time.LocalDateTime;

import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.format.annotation.DateTimeFormat;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.support.domain.AbstractPersistable;
import com.sunwayworld.framework.support.domain.Insertable;

/**
 * @author zhengxy@sunwayworld.com 2023年08月14日
 */
@Table("T_EMC_PURIFY_METHOD")
public class EmcPurifyMethodBean extends AbstractPersistable<Long> implements Insertable<Long> {

    @Id
    private Long id;// 主键

    private String orgId; // 单位ID
    private Long pretreatmentId; //前处理ID
    private String purifyMethod; //净化方法
    private String model; //型号
    private String eluent; //洗脱液
    private String eluentAmount; //洗脱液剂量

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

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public Long getPretreatmentId() {
        return pretreatmentId;
    }

    public void setPretreatmentId(Long pretreatmentId) {
        this.pretreatmentId = pretreatmentId;
    }

    public String getPurifyMethod() {
        return purifyMethod;
    }

    public void setPurifyMethod(String purifyMethod) {
        this.purifyMethod = purifyMethod;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getEluent() {
        return eluent;
    }

    public void setEluent(String eluent) {
        this.eluent = eluent;
    }

    public String getEluentAmount() {
        return eluentAmount;
    }

    public void setEluentAmount(String eluentAmount) {
        this.eluentAmount = eluentAmount;
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
