package com.sunwayworld.emc.module.bp.samplebottle.bean;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.Insertable;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;
import com.alibaba.fastjson.annotation.JSONField;

/**
 * 采样瓶表
 * 
 * @author licm@sunwayworld.com@sunwayworld.com
 * @date 2023-03-10
 */
@Table("T_EMC_SAMPLE_BOTTLE")
public class EmcSampleBottleBean extends AbstractInsertable<Long> implements Insertable<Long> {

    @Transient
    private static final long serialVersionUID = -82566196618711043L;

    @Id
    private Long id;// 主键
    private String sampleBottleNo;// 采样瓶编号
    private String weighingUserName;// 称重人
    private String emptyBottleWeight;// 空瓶重量
    private LocalDate weightTime;// 称重时间
    private String projNo;// 项目编号
    private String orgId;
    private String createdById;// 制单人编码
    private String createdByName;// 制单人名称
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;// 制单时间
    private String createdByOrgId;// 制单人单位编码
    private String createdByOrgName;// 制单人单位名称

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSampleBottleNo() {
        return sampleBottleNo;
    }

    public void setSampleBottleNo(String sampleBottleNo) {
        this.sampleBottleNo = sampleBottleNo;
    }

    public String getWeighingUserName() {
        return weighingUserName;
    }

    public void setWeighingUserName(String weighingUserName) {
        this.weighingUserName = weighingUserName;
    }

    public String getEmptyBottleWeight() {
        return emptyBottleWeight;
    }

    public void setEmptyBottleWeight(String emptyBottleWeight) {
        this.emptyBottleWeight = emptyBottleWeight;
    }

    public LocalDate getWeightTime() {
        return weightTime;
    }

    public void setWeightTime(LocalDate weightTime) {
        this.weightTime = weightTime;
    }

    public String getProjNo() {
        return projNo;
    }

    public void setProjNo(String projNo) {
        this.projNo = projNo;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
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
