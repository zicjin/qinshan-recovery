// 2026-09-24 从生产字节码反编译恢复（CFR 0.152）：生产新增类，历史源码缺失
package com.sunwayworld.emc.module.bp.bp.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import com.sunwayworld.framework.support.domain.Insertable;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

@Table(value="T_EMC_EQUIPT_DIOXIN_SET")
public class EmcEquiptDioxinSetBean
extends AbstractInsertable<Long>
implements Insertable<Long> {
    @Transient
    private static final long serialVersionUID = -82566196618711043L;
    @Id
    private Long id;
    private Long sampleId;
    private Long equiptId;
    private String equiptNo;
    private String equiptName;
    private String specification;
    private String beforeTen;
    private String beforeTwenty;
    private String beforeFifty;
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime beforeDateTime;
    private String afterTen;
    private String afterTwenty;
    private String afterFifty;
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime afterDateTime;
    private String conclusion;
    private String createdById;
    private String createdByName;
    @JSONField(format="yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;
    private String createdByOrgId;
    private String createdByOrgName;
    private String bizType;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getSampleId() {
        return this.sampleId;
    }

    public void setSampleId(Long sampleId) {
        this.sampleId = sampleId;
    }

    public Long getEquiptId() {
        return this.equiptId;
    }

    public void setEquiptId(Long equiptId) {
        this.equiptId = equiptId;
    }

    public String getEquiptNo() {
        return this.equiptNo;
    }

    public void setEquiptNo(String equiptNo) {
        this.equiptNo = equiptNo;
    }

    public String getEquiptName() {
        return this.equiptName;
    }

    public void setEquiptName(String equiptName) {
        this.equiptName = equiptName;
    }

    public String getSpecification() {
        return this.specification;
    }

    public void setSpecification(String specification) {
        this.specification = specification;
    }

    public String getBeforeTen() {
        return this.beforeTen;
    }

    public void setBeforeTen(String beforeTen) {
        this.beforeTen = beforeTen;
    }

    public String getBeforeTwenty() {
        return this.beforeTwenty;
    }

    public void setBeforeTwenty(String beforeTwenty) {
        this.beforeTwenty = beforeTwenty;
    }

    public String getBeforeFifty() {
        return this.beforeFifty;
    }

    public void setBeforeFifty(String beforeFifty) {
        this.beforeFifty = beforeFifty;
    }

    public String getAfterTen() {
        return this.afterTen;
    }

    public void setAfterTen(String afterTen) {
        this.afterTen = afterTen;
    }

    public String getAfterTwenty() {
        return this.afterTwenty;
    }

    public void setAfterTwenty(String afterTwenty) {
        this.afterTwenty = afterTwenty;
    }

    public String getAfterFifty() {
        return this.afterFifty;
    }

    public void setAfterFifty(String afterFifty) {
        this.afterFifty = afterFifty;
    }

    public String getConclusion() {
        return this.conclusion;
    }

    public void setConclusion(String conclusion) {
        this.conclusion = conclusion;
    }

    @Override
    public String getCreatedById() {
        return this.createdById;
    }

    @Override
    public void setCreatedById(String createdById) {
        this.createdById = createdById;
    }

    @Override
    public String getCreatedByName() {
        return this.createdByName;
    }

    @Override
    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    @Override
    public LocalDateTime getCreatedTime() {
        return this.createdTime;
    }

    @Override
    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    @Override
    public String getCreatedByOrgId() {
        return this.createdByOrgId;
    }

    @Override
    public void setCreatedByOrgId(String createdByOrgId) {
        this.createdByOrgId = createdByOrgId;
    }

    @Override
    public String getCreatedByOrgName() {
        return this.createdByOrgName;
    }

    @Override
    public void setCreatedByOrgName(String createdByOrgName) {
        this.createdByOrgName = createdByOrgName;
    }

    public String getBizType() {
        return this.bizType;
    }

    public void setBizType(String bizType) {
        this.bizType = bizType;
    }

    public LocalDateTime getBeforeDateTime() {
        return this.beforeDateTime;
    }

    public void setBeforeDateTime(LocalDateTime beforeDateTime) {
        this.beforeDateTime = beforeDateTime;
    }

    public LocalDateTime getAfterDateTime() {
        return this.afterDateTime;
    }

    public void setAfterDateTime(LocalDateTime afterDateTime) {
        this.afterDateTime = afterDateTime;
    }
}
