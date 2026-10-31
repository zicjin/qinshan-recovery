package com.sunwayworld.emc.module.bp.bp.bean;

import java.time.LocalDate;
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
 * @author zhengxy@sunwayworld.com 2023年08月18日
 */
@Table("T_EMC_ORDER_CONTAINER")
public class EmcOrderContainerBean extends AbstractPersistable<Long> implements Insertable<Long> {

    @Transient
    private static final long serialVersionUID = 2538167343012276963L;

    @Id
    private Long id;// 主键

    private Long projId;// 项目编码

    private Long folderId;// 点位编码

    private Long orderId;// 样品ID

    private String orderNo;// 样品编号

    private String orderContainerNo;// 样品容器编号

    private String labelNo;// 标签编号

    private String clientOrderName;// 客户样品名称

    private String sampleAddress;// 采样地点

    private String parentOrderNo;// 父样编号

    private String orderDesc;// 样品描述

    private Long containerCategoryId;// 容器类型主键
    private String containerCategoryCode; // 容器类型编码
    private String containerCategoryName;// 容器类型名称
    private String containerGroup;// 容器组
    private String fixative;// 固定剂

    private String orderVolume;// 样品量
    private String sensoryDesc;// 感官描述
    private String mysteryValue;//
    private String testName;//监测项目
    private String orgId;// 所属单位

    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate sendDate;// 送样日期

    private String sendTime;// 送样时间

    private Long cycle;// 周期

    private Long frequency;// 频次

    private String monitorTmplId;// 监测模板ID

    private String orderCategory;// 样品类型

    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate sampleBeginDate;// 采样开始日期

    private String sampleBeginTime;// 采样开始时间

    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate sampleCompleteDate;// 采样完成日期

    private String sampleCompleteTime;// 采样完成时间

    private Long sampleNum;// 采样数

    private Long qcCategoryId;// QC类型主键

    @NotNull(defaultValue = "N/A")
    private String qcCategoryCode; // QC类型编码

    @NotNull(defaultValue = "N/A")
    private String qcName; // QC类型中文

    private Long orderNum;// 排序码

    private String remark;// 备注

    @NotNull(defaultValue = Constant.NO)
    private String termination;// 是否终止

    private String acceptedUserId; // 接收人编码

    private String acceptedUserName; // 接收人

    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime acceptedDate; // 接收日期

    private String acceptedTime; // 接收时间

    private String horizontalCode; // 水平代码

    private String verticalCode; // 垂直代码

    private String mobileId; // 移动端样品ID

    private String comparer;// 比对人

    private String source; // 来源

    private Long deliveryId; // 送样单ID

    @NotNull(defaultValue = "0")
    private String isScan;//是否扫码

    @NotNull(defaultValue = "0")
    private String isReceiveScan;//是否领样扫码

    private Long parentId; // 父样ID

    private String parentNo; // 父样编码

    private Long baseRecordVId; // 源记录模板版本ID

    private String isRetain; // 是否留样

    private String sampleReceiverId;// 领样人编码

    private String sampleReceiverName;// 领样人名称

    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime sampleReceiverDate;// 领样日期

    private String sampleReceiverTime;// 领样时间
    private String storeCondition;// 样品保存条件

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

    public String getIsReceiveScan() {
        return isReceiveScan;
    }

    public void setIsReceiveScan(String isReceiveScan) {
        this.isReceiveScan = isReceiveScan;
    }

    public String getAcceptedTime() {
        return acceptedTime;
    }

    public void setAcceptedTime(String acceptedTime) {
        this.acceptedTime = acceptedTime;
    }

    public String getOrderContainerNo() {
        return orderContainerNo;
    }

    public void setOrderContainerNo(String orderContainerNo) {
        this.orderContainerNo = orderContainerNo;
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

    public Long getProjId() {
        return projId;
    }

    public void setProjId(Long projId) {
        this.projId = projId;
    }

    public Long getFolderId() {
        return folderId;
    }

    public void setFolderId(Long folderId) {
        this.folderId = folderId;
    }

    public String getLabelNo() {
        return labelNo;
    }

    public void setLabelNo(String labelNo) {
        this.labelNo = labelNo;
    }

    public String getClientOrderName() {
        return clientOrderName;
    }

    public void setClientOrderName(String clientOrderName) {
        this.clientOrderName = clientOrderName;
    }

    public String getSampleAddress() {
        return sampleAddress;
    }

    public void setSampleAddress(String sampleAddress) {
        this.sampleAddress = sampleAddress;
    }

    public String getParentOrderNo() {
        return parentOrderNo;
    }

    public void setParentOrderNo(String parentOrderNo) {
        this.parentOrderNo = parentOrderNo;
    }

    public String getOrderDesc() {
        return orderDesc;
    }

    public void setOrderDesc(String orderDesc) {
        this.orderDesc = orderDesc;
    }

    public Long getContainerCategoryId() {
        return containerCategoryId;
    }

    public void setContainerCategoryId(Long containerCategoryId) {
        this.containerCategoryId = containerCategoryId;
    }

    public String getContainerCategoryCode() {
        return containerCategoryCode;
    }

    public void setContainerCategoryCode(String containerCategoryCode) {
        this.containerCategoryCode = containerCategoryCode;
    }

    public String getContainerCategoryName() {
        return containerCategoryName;
    }

    public void setContainerCategoryName(String containerCategoryName) {
        this.containerCategoryName = containerCategoryName;
    }

    public String getContainerGroup() {
        return containerGroup;
    }

    public void setContainerGroup(String containerGroup) {
        this.containerGroup = containerGroup;
    }

    public String getFixative() {
        return fixative;
    }

    public void setFixative(String fixative) {
        this.fixative = fixative;
    }

    public String getOrderVolume() {
        return orderVolume;
    }

    public void setOrderVolume(String orderVolume) {
        this.orderVolume = orderVolume;
    }

    public String getSensoryDesc() {
        return sensoryDesc;
    }

    public void setSensoryDesc(String sensoryDesc) {
        this.sensoryDesc = sensoryDesc;
    }

    public String getMysteryValue() {
        return mysteryValue;
    }

    public void setMysteryValue(String mysteryValue) {
        this.mysteryValue = mysteryValue;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public LocalDate getSendDate() {
        return sendDate;
    }

    public void setSendDate(LocalDate sendDate) {
        this.sendDate = sendDate;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
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

    public String getMonitorTmplId() {
        return monitorTmplId;
    }

    public void setMonitorTmplId(String monitorTmplId) {
        this.monitorTmplId = monitorTmplId;
    }

    public String getOrderCategory() {
        return orderCategory;
    }

    public void setOrderCategory(String orderCategory) {
        this.orderCategory = orderCategory;
    }

    public Long getDeliveryId() {
        return deliveryId;
    }

    public void setDeliveryId(Long deliveryId) {
        this.deliveryId = deliveryId;
    }

    public LocalDate getSampleBeginDate() {
        return sampleBeginDate;
    }

    public void setSampleBeginDate(LocalDate sampleBeginDate) {
        this.sampleBeginDate = sampleBeginDate;
    }

    public String getSampleBeginTime() {
        return sampleBeginTime;
    }

    public void setSampleBeginTime(String sampleBeginTime) {
        this.sampleBeginTime = sampleBeginTime;
    }

    public LocalDate getSampleCompleteDate() {
        return sampleCompleteDate;
    }

    public void setSampleCompleteDate(LocalDate sampleCompleteDate) {
        this.sampleCompleteDate = sampleCompleteDate;
    }

    public String getSampleCompleteTime() {
        return sampleCompleteTime;
    }

    public void setSampleCompleteTime(String sampleCompleteTime) {
        this.sampleCompleteTime = sampleCompleteTime;
    }

    public Long getSampleNum() {
        return sampleNum;
    }

    public void setSampleNum(Long sampleNum) {
        this.sampleNum = sampleNum;
    }

    public Long getQcCategoryId() {
        return qcCategoryId;
    }

    public void setQcCategoryId(Long qcCategoryId) {
        this.qcCategoryId = qcCategoryId;
    }

    public String getQcCategoryCode() {
        return qcCategoryCode;
    }

    public void setQcCategoryCode(String qcCategoryCode) {
        this.qcCategoryCode = qcCategoryCode;
    }

    public String getQcName() {
        return qcName;
    }

    public void setQcName(String qcName) {
        this.qcName = qcName;
    }

    public Long getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(Long orderNum) {
        this.orderNum = orderNum;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getTermination() {
        return termination;
    }

    public void setTermination(String termination) {
        this.termination = termination;
    }

    public String getAcceptedUserId() {
        return acceptedUserId;
    }

    public void setAcceptedUserId(String acceptedUserId) {
        this.acceptedUserId = acceptedUserId;
    }

    public String getAcceptedUserName() {
        return acceptedUserName;
    }

    public void setAcceptedUserName(String acceptedUserName) {
        this.acceptedUserName = acceptedUserName;
    }

    public LocalDateTime getAcceptedDate() {
        return acceptedDate;
    }

    public void setAcceptedDate(LocalDateTime acceptedDate) {
        this.acceptedDate = acceptedDate;
    }

    public String getHorizontalCode() {
        return horizontalCode;
    }

    public void setHorizontalCode(String horizontalCode) {
        this.horizontalCode = horizontalCode;
    }

    public String getVerticalCode() {
        return verticalCode;
    }

    public void setVerticalCode(String verticalCode) {
        this.verticalCode = verticalCode;
    }

    public String getMobileId() {
        return mobileId;
    }

    public void setMobileId(String mobileId) {
        this.mobileId = mobileId;
    }

    public String getComparer() {
        return comparer;
    }

    public void setComparer(String comparer) {
        this.comparer = comparer;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getIsScan() {
        return isScan;
    }

    public void setIsScan(String isScan) {
        this.isScan = isScan;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getParentNo() {
        return parentNo;
    }

    public void setParentNo(String parentNo) {
        this.parentNo = parentNo;
    }

    public Long getBaseRecordVId() {
        return baseRecordVId;
    }

    public void setBaseRecordVId(Long baseRecordVId) {
        this.baseRecordVId = baseRecordVId;
    }

    public String getIsRetain() {
        return isRetain;
    }

    public void setIsRetain(String isRetain) {
        this.isRetain = isRetain;
    }

    public String getStoreCondition() {
        return storeCondition;
    }

    public void setStoreCondition(String storeCondition) {
        this.storeCondition = storeCondition;
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

    public String getSampleReceiverId() {
        return sampleReceiverId;
    }

    public void setSampleReceiverId(String sampleReceiverId) {
        this.sampleReceiverId = sampleReceiverId;
    }

    public String getSampleReceiverName() {
        return sampleReceiverName;
    }

    public void setSampleReceiverName(String sampleReceiverName) {
        this.sampleReceiverName = sampleReceiverName;
    }

    public LocalDateTime getSampleReceiverDate() {
        return sampleReceiverDate;
    }

    public void setSampleReceiverDate(LocalDateTime sampleReceiverDate) {
        this.sampleReceiverDate = sampleReceiverDate;
    }

    public String getSampleReceiverTime() {
        return sampleReceiverTime;
    }

    public void setSampleReceiverTime(String sampleReceiverTime) {
        this.sampleReceiverTime = sampleReceiverTime;
    }
}
