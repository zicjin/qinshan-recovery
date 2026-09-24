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
 * 样品
 *
 * @author magh@sunwayworld.com
 * @date 2019-10-09
 */

@Table("T_EMC_ORDER")
public class EmcOrderBean extends AbstractPersistable<Long> implements Insertable<Long> {

    @Transient
    private static final long serialVersionUID = 2236364087741210206L;

    @Id
    private Long id;// 主键

    private String orderNo;// 样品编号

    private String sampleParamOrderNo;// 样品编号-气采样参数

    private Long projId;// 项目编码

    private Long folderId;// 点位编码

    private String labelNo;// 标签编号

    private String clientOrderName;// 客户样品名称

    private String sampleAddress;// 采样地点

    private String parentOrderNo;// 父样编号

    private String orderDesc;// 样品描述

    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate sendDate;// 送样日期

    private String sendTime;// 送样时间

    private Long cycle;// 周期

    private Long frequency;// 频次

    private String monitorTmplId;// 监测模板ID

    private String orderCategory;// 样品类型

    private Long sampleId;// 采样单编码

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

    private String accepteUserName; // 接收人

    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime acceptedDate; // 接收日期

    private String horizontalCode; // 水平代码

    private String verticalCode; // 垂直代码

    private String verticalLine; // 垂线

    private String mobileId; // 移动端样品ID

    private String source; // 来源

    private String relatedOrderNo; // 关联的样品编号（针对复制的实验室空白或空白加标）

    private Long erpGbId; // ERP样品ID

    private String orderQty; // 样品个数

    private String senderId; // 送达人

    private String struct; // 结构

    private String otherOb; // 其他观察对象

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

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getSampleParamOrderNo() {
        return sampleParamOrderNo;
    }

    public void setSampleParamOrderNo(String sampleParamOrderNo) {
        this.sampleParamOrderNo = sampleParamOrderNo;
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

    public Long getSampleId() {
        return sampleId;
    }

    public void setSampleId(Long sampleId) {
        this.sampleId = sampleId;
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

    public String getOrderCategory() {
        return orderCategory;
    }

    public void setOrderCategory(String orderCategory) {
        this.orderCategory = orderCategory;
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

    public String getAccepteUserName() {
        return accepteUserName;
    }

    public void setAccepteUserName(String accepteUserName) {
        this.accepteUserName = accepteUserName;
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

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getRelatedOrderNo() {
        return relatedOrderNo;
    }

    public void setRelatedOrderNo(String relatedOrderNo) {
        this.relatedOrderNo = relatedOrderNo;
    }

    public Long getErpGbId() {
        return erpGbId;
    }

    public void setErpGbId(Long erpGbId) {
        this.erpGbId = erpGbId;
    }

    public String getOrderQty() {
        return orderQty;
    }

    public void setOrderQty(String orderQty) {
        this.orderQty = orderQty;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getStruct() {
        return struct;
    }

    public void setStruct(String struct) {
        this.struct = struct;
    }

    public String getOtherOb() {
        return otherOb;
    }

    public void setOtherOb(String otherOb) {
        this.otherOb = otherOb;
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

    public String getVerticalLine() {
        return verticalLine;
    }

    public void setVerticalLine(String verticalLine) {
        this.verticalLine = verticalLine;
    }
}
