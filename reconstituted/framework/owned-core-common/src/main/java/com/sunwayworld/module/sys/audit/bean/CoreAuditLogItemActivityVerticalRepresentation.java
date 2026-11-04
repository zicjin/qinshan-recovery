/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.audit.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.sunwayworld.framework.support.domain.AbstractBaseData;
import com.sunwayworld.module.sys.audit.bean.CoreAuditLogColumnVerticalDTO;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;

public class CoreAuditLogItemActivityVerticalRepresentation
extends AbstractBaseData {
    private static final long serialVersionUID = -8986246442618714723L;
    private Long id;
    private String activity;
    private String comment;
    private String signature;
    private String userId;
    private String userName;
    private String targetTable;
    private String witnessId;
    private String witnessName;
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;
    private String lvl = "3";
    private List<CoreAuditLogColumnVerticalDTO> auditLogColumnVerticalDTOList;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getActivity() {
        return this.activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public String getComment() {
        return this.comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getSignature() {
        return this.signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTargetTable() {
        return this.targetTable;
    }

    public void setTargetTable(String targetTable) {
        this.targetTable = targetTable;
    }

    public LocalDateTime getCreatedTime() {
        return this.createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public String getLvl() {
        return this.lvl;
    }

    public String getWitnessId() {
        return this.witnessId;
    }

    public void setWitnessId(String witnessId) {
        this.witnessId = witnessId;
    }

    public String getWitnessName() {
        return this.witnessName;
    }

    public void setWitnessName(String witnessName) {
        this.witnessName = witnessName;
    }

    public void setLvl(String lvl) {
        this.lvl = lvl;
    }

    public List<CoreAuditLogColumnVerticalDTO> getAuditLogColumnVerticalDTOList() {
        return this.auditLogColumnVerticalDTOList;
    }

    public void setAuditLogColumnVerticalDTOList(List<CoreAuditLogColumnVerticalDTO> auditLogColumnVerticalDTOList) {
        this.auditLogColumnVerticalDTOList = auditLogColumnVerticalDTOList;
    }
}
