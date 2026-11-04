/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.equipt.usagerecord.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import com.sunwayworld.framework.support.domain.Insertable;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

@Table(value="T_CORE_EQUIPT_USAGE_RECORD")
public class CoreEquiptUsageRecordBean
extends AbstractInsertable<Long>
implements Insertable<Long> {
    @Transient
    private static final long serialVersionUID = -6380925415678637970L;
    @Id
    private Long id;
    private Long equiptId;
    private String equiptNo;
    private String equiptName;
    private String equiptCategory;
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime upTime;
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime downTime;
    private Long usageTime;
    private String userName;
    private String testSample;
    private String testItem;
    private Long sampleQty;
    private Long orderTaskQty;
    private Double amount;
    private String createdById;
    private String createdByName;
    @JSONField(format="yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;
    private String createdByOrgId;
    private String createdByOrgName;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
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

    public String getEquiptCategory() {
        return this.equiptCategory;
    }

    public void setEquiptCategory(String equiptCategory) {
        this.equiptCategory = equiptCategory;
    }

    public LocalDateTime getUpTime() {
        return this.upTime;
    }

    public void setUpTime(LocalDateTime upTime) {
        this.upTime = upTime;
    }

    public LocalDateTime getDownTime() {
        return this.downTime;
    }

    public void setDownTime(LocalDateTime downTime) {
        this.downTime = downTime;
    }

    public Long getUsageTime() {
        return this.usageTime;
    }

    public void setUsageTime(Long usageTime) {
        this.usageTime = usageTime;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTestSample() {
        return this.testSample;
    }

    public void setTestSample(String testSample) {
        this.testSample = testSample;
    }

    public String getTestItem() {
        return this.testItem;
    }

    public void setTestItem(String testItem) {
        this.testItem = testItem;
    }

    public Long getSampleQty() {
        return this.sampleQty;
    }

    public void setSampleQty(Long sampleQty) {
        this.sampleQty = sampleQty;
    }

    public Long getOrderTaskQty() {
        return this.orderTaskQty;
    }

    public void setOrderTaskQty(Long orderTaskQty) {
        this.orderTaskQty = orderTaskQty;
    }

    public Double getAmount() {
        return this.amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
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
}
