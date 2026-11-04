/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.les.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.sunwayworld.framework.support.tree.bean.AbstractTreeNode;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

public class CoreLesInstanceUnitDTO
extends AbstractTreeNode {
    private static final long serialVersionUID = 4330758545933077524L;
    private Long instanceId;
    private String unitCategory;
    private String unitTitle;
    private String unitBody;
    private String config;
    private String editFlag;
    private String exportReport;
    private String doneFlag;
    private Long orderNo;
    private String commentStatus;
    private String reviewedById;
    private String reviewedByName;
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime reviewedTime;
    private String unitParam;
    private String createdById;
    private String createdByName;
    @JSONField(format="yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;

    public Long getInstanceId() {
        return this.instanceId;
    }

    public void setInstanceId(Long instanceId) {
        this.instanceId = instanceId;
    }

    public String getUnitCategory() {
        return this.unitCategory;
    }

    public void setUnitCategory(String unitCategory) {
        this.unitCategory = unitCategory;
    }

    public String getUnitTitle() {
        return this.unitTitle;
    }

    public void setUnitTitle(String unitTitle) {
        this.unitTitle = unitTitle;
    }

    public String getUnitBody() {
        return this.unitBody;
    }

    public void setUnitBody(String unitBody) {
        this.unitBody = unitBody;
    }

    public Long getOrderNo() {
        return this.orderNo;
    }

    public void setOrderNo(Long orderNo) {
        this.orderNo = orderNo;
    }

    public String getEditFlag() {
        return this.editFlag;
    }

    public String getDoneFlag() {
        return this.doneFlag;
    }

    public void setDoneFlag(String doneFlag) {
        this.doneFlag = doneFlag;
    }

    public void setEditFlag(String editFlag) {
        this.editFlag = editFlag;
    }

    public String getExportReport() {
        return this.exportReport;
    }

    public void setExportReport(String exportReport) {
        this.exportReport = exportReport;
    }

    public String getConfig() {
        return this.config;
    }

    public void setConfig(String config) {
        this.config = config;
    }

    public String getCommentStatus() {
        return this.commentStatus;
    }

    public void setCommentStatus(String commentStatus) {
        this.commentStatus = commentStatus;
    }

    public String getReviewedById() {
        return this.reviewedById;
    }

    public void setReviewedById(String reviewedById) {
        this.reviewedById = reviewedById;
    }

    public String getReviewedByName() {
        return this.reviewedByName;
    }

    public void setReviewedByName(String reviewedByName) {
        this.reviewedByName = reviewedByName;
    }

    public LocalDateTime getReviewedTime() {
        return this.reviewedTime;
    }

    public void setReviewedTime(LocalDateTime reviewedTime) {
        this.reviewedTime = reviewedTime;
    }

    public String getUnitParam() {
        return this.unitParam;
    }

    public void setUnitParam(String unitParam) {
        this.unitParam = unitParam;
    }

    public String getCreatedById() {
        return this.createdById;
    }

    public void setCreatedById(String createdById) {
        this.createdById = createdById;
    }

    public String getCreatedByName() {
        return this.createdByName;
    }

    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    public LocalDateTime getCreatedTime() {
        return this.createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }
}
