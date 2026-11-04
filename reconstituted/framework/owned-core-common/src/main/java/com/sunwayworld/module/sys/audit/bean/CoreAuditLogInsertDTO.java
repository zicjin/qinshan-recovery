/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.audit.bean;

import com.sunwayworld.framework.data.CaseInsensitiveLinkedMap;
import java.io.Serializable;
import java.time.LocalDateTime;

public class CoreAuditLogInsertDTO
implements Serializable {
    private static final long serialVersionUID = -3434856542726678308L;
    private String table;
    private String activity;
    private String comment;
    private String signature;
    private String userId;
    private String witnessId;
    private String witnessName;
    private CaseInsensitiveLinkedMap<Object> oldValueMap;
    private CaseInsensitiveLinkedMap<Object> newValueMap;
    private LocalDateTime createdTime;

    public String getTable() {
        return this.table;
    }

    public void setTable(String table) {
        this.table = table;
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

    public CaseInsensitiveLinkedMap<Object> getOldValueMap() {
        return this.oldValueMap;
    }

    public void setOldValueMap(CaseInsensitiveLinkedMap<Object> oldValueMap) {
        this.oldValueMap = oldValueMap;
    }

    public CaseInsensitiveLinkedMap<Object> getNewValueMap() {
        return this.newValueMap;
    }

    public void setNewValueMap(CaseInsensitiveLinkedMap<Object> newValueMap) {
        this.newValueMap = newValueMap;
    }

    public LocalDateTime getCreatedTime() {
        return this.createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }
}
