/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.bpmn.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.sunwayworld.framework.support.domain.AbstractBaseData;
import com.sunwayworld.framework.support.domain.BaseData;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

public class CoreBpmnCommentDTO
extends AbstractBaseData
implements BaseData {
    private static final long serialVersionUID = -5803265059259898726L;
    private Long id;
    private Long procId;
    private String taskName;
    private String auditedById;
    private String auditedByName;
    private String route;
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime auditedTime;
    private String comment;
    private String status;
    private Long attachmentId;
    private String attachmentName;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProcId() {
        return this.procId;
    }

    public void setProcId(Long procId) {
        this.procId = procId;
    }

    public String getTaskName() {
        return this.taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getAuditedByName() {
        return this.auditedByName;
    }

    public void setAuditedByName(String auditedByName) {
        this.auditedByName = auditedByName;
    }

    public String getRoute() {
        return this.route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public LocalDateTime getAuditedTime() {
        return this.auditedTime;
    }

    public void setAuditedTime(LocalDateTime auditedTime) {
        this.auditedTime = auditedTime;
    }

    public String getComment() {
        return this.comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Long getAttachmentId() {
        return this.attachmentId;
    }

    public void setAttachmentId(Long attachmentId) {
        this.attachmentId = attachmentId;
    }

    public String getAttachmentName() {
        return this.attachmentName;
    }

    public void setAttachmentName(String attachmentName) {
        this.attachmentName = attachmentName;
    }

    public String getAuditedById() {
        return this.auditedById;
    }

    public void setAuditedById(String auditedById) {
        this.auditedById = auditedById;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
