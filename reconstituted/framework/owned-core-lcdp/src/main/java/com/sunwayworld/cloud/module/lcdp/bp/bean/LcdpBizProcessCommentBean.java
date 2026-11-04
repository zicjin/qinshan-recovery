/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.bp.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import com.sunwayworld.framework.support.domain.Insertable;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

@Table(value="T_LCDP_BIZ_PROCESS_COMMENT")
public class LcdpBizProcessCommentBean
extends AbstractInsertable<Long>
implements Insertable<Long> {
    @Transient
    private static final long serialVersionUID = -4269783652274928978L;
    @Id
    private Long id;
    private Long bpId;
    private Long bpActionId;
    private String bpActionCode;
    private String bpActionName;
    private String tableName;
    private String bizId;
    private String auditUserId;
    private String auditUserName;
    private String comments;
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime auditTime;
    private String bpNodeCode;
    private String bpNodeName;
    private String nextBpNodeCode;
    private String nextBpNodeName;
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

    public Long getBpId() {
        return this.bpId;
    }

    public void setBpId(Long bpId) {
        this.bpId = bpId;
    }

    public Long getBpActionId() {
        return this.bpActionId;
    }

    public void setBpActionId(Long bpActionId) {
        this.bpActionId = bpActionId;
    }

    public String getBpActionCode() {
        return this.bpActionCode;
    }

    public void setBpActionCode(String bpActionCode) {
        this.bpActionCode = bpActionCode;
    }

    public String getBpActionName() {
        return this.bpActionName;
    }

    public void setBpActionName(String bpActionName) {
        this.bpActionName = bpActionName;
    }

    public String getTableName() {
        return this.tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getBizId() {
        return this.bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public String getAuditUserId() {
        return this.auditUserId;
    }

    public void setAuditUserId(String auditUserId) {
        this.auditUserId = auditUserId;
    }

    public String getAuditUserName() {
        return this.auditUserName;
    }

    public void setAuditUserName(String auditUserName) {
        this.auditUserName = auditUserName;
    }

    public String getComments() {
        return this.comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public LocalDateTime getAuditTime() {
        return this.auditTime;
    }

    public void setAuditTime(LocalDateTime auditTime) {
        this.auditTime = auditTime;
    }

    public String getBpNodeCode() {
        return this.bpNodeCode;
    }

    public void setBpNodeCode(String bpNodeCode) {
        this.bpNodeCode = bpNodeCode;
    }

    public String getBpNodeName() {
        return this.bpNodeName;
    }

    public void setBpNodeName(String bpNodeName) {
        this.bpNodeName = bpNodeName;
    }

    public String getNextBpNodeCode() {
        return this.nextBpNodeCode;
    }

    public void setNextBpNodeCode(String nextBpNodeCode) {
        this.nextBpNodeCode = nextBpNodeCode;
    }

    public String getNextBpNodeName() {
        return this.nextBpNodeName;
    }

    public void setNextBpNodeName(String nextBpNodeName) {
        this.nextBpNodeName = nextBpNodeName;
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
