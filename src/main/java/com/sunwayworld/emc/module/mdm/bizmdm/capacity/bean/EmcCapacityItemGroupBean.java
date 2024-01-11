package com.sunwayworld.emc.module.mdm.bizmdm.capacity.bean;

import java.time.LocalDateTime;

import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.format.annotation.DateTimeFormat;

import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.NotNull;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractPersistable;
import com.sunwayworld.framework.support.domain.Activatable;

/**
 * 测试方法分析项组表
 *
 * @author luoc@sunwayworld.com
 * @date 2021-04-15
 */

@Table("T_EMC_CAPACITY_ITEM_GROUP")
public class EmcCapacityItemGroupBean extends AbstractPersistable<Long>implements Activatable<Long> {

    @Transient
    private static final long serialVersionUID = -3583182025912806094L;

    @Id
    private Long id;// 主键

    private Long testCapacityId;//测试能力库Id

    private Long testMethodId;// 测试方法ID

    private Long itemGroupId; // 分析项组ID

    private String itemGroup;// 分析项组

    private Long testId;// 测试编码

    private String relatedFlag; // 关联标识

    private String relatedItemGroupId; // 关联分析项组ID

    private String relatedItemGroup; // 关联分析项组

    @NotNull(defaultValue = Constant.ACTIVATED_STATUS_YES)
    private String activatedFlag;// 启用标志

    private String activatedById;// 启用人编码

    private String activatedByName;// 启用人名称

    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime activatedTime;// 启用时间

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

    public Long getTestCapacityId() {
        return testCapacityId;
    }

    public void setTestCapacityId(Long testCapacityId) {
        this.testCapacityId = testCapacityId;
    }

    public Long getTestMethodId() {
        return testMethodId;
    }

    public void setTestMethodId(Long testMethodId) {
        this.testMethodId = testMethodId;
    }

    public Long getItemGroupId() {
        return itemGroupId;
    }

    public void setItemGroupId(Long itemGroupId) {
        this.itemGroupId = itemGroupId;
    }

    public String getItemGroup() {
        return itemGroup;
    }

    public void setItemGroup(String itemGroup) {
        this.itemGroup = itemGroup;
    }

    public Long getTestId() {
        return testId;
    }

    public void setTestId(Long testId) {
        this.testId = testId;
    }

    public String getRelatedFlag() {
        return relatedFlag;
    }

    public void setRelatedFlag(String relatedFlag) {
        this.relatedFlag = relatedFlag;
    }

    public String getRelatedItemGroupId() {
        return relatedItemGroupId;
    }

    public void setRelatedItemGroupId(String relatedItemGroupId) {
        this.relatedItemGroupId = relatedItemGroupId;
    }

    public String getRelatedItemGroup() {
        return relatedItemGroup;
    }

    public void setRelatedItemGroup(String relatedItemGroup) {
        this.relatedItemGroup = relatedItemGroup;
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
