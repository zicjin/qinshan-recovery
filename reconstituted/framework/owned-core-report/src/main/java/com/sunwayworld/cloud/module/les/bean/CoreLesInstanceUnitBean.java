/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.les.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.sunwayworld.framework.data.annotation.Clob;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.NotNull;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import com.sunwayworld.framework.support.domain.Insertable;
import com.sunwayworld.framework.support.domain.Orderable;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

@Table(value="T_CORE_LES_INSTANCE_UNIT")
public class CoreLesInstanceUnitBean
extends AbstractInsertable<Long>
implements Insertable<Long>,
Orderable<Long> {
    @Transient
    private static final long serialVersionUID = -8896625792456192964L;
    @Id
    private Long id;
    private Long instanceId;
    private Long templateUnitId;
    private String unitCategory;
    private String unitTitle;
    private Long parentId;
    @Clob
    private String unitBody;
    @Clob
    private String config;
    @Clob
    private String unitParam;
    private String editFlag;
    @NotNull(defaultValue="1")
    private String exportReport;
    private Long orderNo;
    private String audit;
    @NotNull(defaultValue="0")
    private String doneFlag;
    private String commentStatus;
    private String reviewedById;
    private String reviewedByName;
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime reviewedTime;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getInstanceId() {
        return this.instanceId;
    }

    public void setInstanceId(Long instanceId) {
        this.instanceId = instanceId;
    }

    public Long getTemplateUnitId() {
        return this.templateUnitId;
    }

    public void setTemplateUnitId(Long templateUnitId) {
        this.templateUnitId = templateUnitId;
    }

    public String getUnitTitle() {
        return this.unitTitle;
    }

    public void setUnitTitle(String unitTitle) {
        this.unitTitle = unitTitle;
    }

    public Long getParentId() {
        return this.parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getUnitCategory() {
        return this.unitCategory;
    }

    public void setUnitCategory(String unitCategory) {
        this.unitCategory = unitCategory;
    }

    public String getUnitBody() {
        return this.unitBody;
    }

    public void setUnitBody(String unitBody) {
        this.unitBody = unitBody;
    }

    public String getConfig() {
        return this.config;
    }

    public void setConfig(String config) {
        this.config = config;
    }

    public String getUnitParam() {
        return this.unitParam;
    }

    public void setUnitParam(String unitParam) {
        this.unitParam = unitParam;
    }

    public String getEditFlag() {
        return this.editFlag;
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

    @Override
    public Long getOrderNo() {
        return this.orderNo;
    }

    @Override
    public void setOrderNo(Long orderNo) {
        this.orderNo = orderNo;
    }

    public String getAudit() {
        return this.audit;
    }

    public void setAudit(String audit) {
        this.audit = audit;
    }

    public String getDoneFlag() {
        return this.doneFlag;
    }

    public void setDoneFlag(String doneFlag) {
        this.doneFlag = doneFlag;
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
}
