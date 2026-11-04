/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.at.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.module.sys.at.bean.CoreAuditTrailRecordLineBean;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

public class CoreAuditTrailRecordLineDTO
extends CoreAuditTrailRecordLineBean {
    @Transient
    private static final long serialVersionUID = 3636921447724061252L;
    private String auditTrailType;
    private String auditTrailName;
    private String comment;
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;
    private String createdByName;

    public String getAuditTrailType() {
        return this.auditTrailType;
    }

    public void setAuditTrailType(String auditTrailType) {
        this.auditTrailType = auditTrailType;
    }

    public String getAuditTrailName() {
        return this.auditTrailName;
    }

    public void setAuditTrailName(String auditTrailName) {
        this.auditTrailName = auditTrailName;
    }

    public String getComment() {
        return this.comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
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
    public String getCreatedByName() {
        return this.createdByName;
    }

    @Override
    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }
}
