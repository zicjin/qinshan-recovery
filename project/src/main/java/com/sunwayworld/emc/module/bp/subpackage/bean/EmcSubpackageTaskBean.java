package com.sunwayworld.emc.module.bp.subpackage.bean;

import java.time.LocalDateTime;

import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.format.annotation.DateTimeFormat;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;

/**
 * 分包单测试
 * @author zhengxy@sunwayworld.com 2022年09月05日
 */
@Table("T_EMC_SUBPACKAGE_TASK")
public class EmcSubpackageTaskBean extends AbstractInsertable<Long> {

    @Transient
    private static final long serialVersionUID = -3741947919533410949L;

    @Id
    private Long id;  // 主键
    private Long subpackageId;  // 分包单ID
    private String subpackageNo;  // 分包单号
    private Long projId;  // 项目ID
    private Long projSchemeId;  // 方案ID
    private String folderNo;  // 点位编号
    private Long cycle;  // 周期
    private Long frequency;  // 频次
    private Long sampleNum;  // 采样数
    private Long orderId;  // 样品ID
    private String orderNo;  // 样品编号
    private Long testId;  // 测试ID
    private String testName;  // 测试名称
    private Long methodId; // 方法ID
    private Long testMethodId;  // 测试方法ID
    private String itemGroup;  // 分析项组
    private Long itemGroupId;  // 分析项组ID

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

    public Long getSubpackageId() {
        return subpackageId;
    }

    public void setSubpackageId(Long subpackageId) {
        this.subpackageId = subpackageId;
    }

    public String getSubpackageNo() {
        return subpackageNo;
    }

    public void setSubpackageNo(String subpackageNo) {
        this.subpackageNo = subpackageNo;
    }

    public Long getProjId() {
        return projId;
    }

    public void setProjId(Long projId) {
        this.projId = projId;
    }

    public Long getProjSchemeId() {
        return projSchemeId;
    }

    public void setProjSchemeId(Long projSchemeId) {
        this.projSchemeId = projSchemeId;
    }

    public String getFolderNo() {
        return folderNo;
    }

    public void setFolderNo(String folderNo) {
        this.folderNo = folderNo;
    }

    public Long getCycle() {
        return cycle;
    }

    public void setCycle(Long cycle) {
        this.cycle = cycle;
    }

    public Long getFrequency() {
        return frequency;
    }

    public void setFrequency(Long frequency) {
        this.frequency = frequency;
    }

    public Long getSampleNum() {
        return sampleNum;
    }

    public void setSampleNum(Long sampleNum) {
        this.sampleNum = sampleNum;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Long getTestId() {
        return testId;
    }

    public void setTestId(Long testId) {
        this.testId = testId;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public Long getMethodId() {
        return methodId;
    }

    public void setMethodId(Long methodId) {
        this.methodId = methodId;
    }

    public Long getTestMethodId() {
        return testMethodId;
    }

    public void setTestMethodId(Long testMethodId) {
        this.testMethodId = testMethodId;
    }

    public String getItemGroup() {
        return itemGroup;
    }

    public void setItemGroup(String itemGroup) {
        this.itemGroup = itemGroup;
    }

    public Long getItemGroupId() {
        return itemGroupId;
    }

    public void setItemGroupId(Long itemGroupId) {
        this.itemGroupId = itemGroupId;
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
