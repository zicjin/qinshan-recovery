/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.audit.event;

import com.sunwayworld.framework.data.CaseInsensitiveLinkedMap;
import com.sunwayworld.framework.utils.ConvertUtils;
import java.io.Serializable;
import java.time.LocalDateTime;

public class AuditLogDTO
implements Serializable {
    private static final long serialVersionUID = -1774025047600647449L;
    private final String table;
    private final String id;
    private final String activity;
    private final String comment;
    private final String signature;
    private final String userId;
    private final String witnessId;
    private final String witnessName;
    private final CaseInsensitiveLinkedMap<Object> oldValueMap;
    private CaseInsensitiveLinkedMap<Object> newValueMap;
    private final LocalDateTime createdTime;

    public AuditLogDTO(String table, String activity, String comment, String signature, String userId, String witnessId, String witnessName, CaseInsensitiveLinkedMap<Object> oldValueMap, CaseInsensitiveLinkedMap<Object> newValueMap) {
        this.table = table;
        this.activity = activity;
        this.comment = comment;
        this.signature = signature;
        this.userId = userId;
        this.witnessId = witnessId;
        this.witnessName = witnessName;
        this.oldValueMap = oldValueMap;
        this.newValueMap = newValueMap;
        this.id = ConvertUtils.convert(oldValueMap != null && !oldValueMap.isEmpty() ? oldValueMap.get("id") : newValueMap.get("id"), String.class);
        this.createdTime = LocalDateTime.now();
    }

    public String getTable() {
        return this.table;
    }

    public String getId() {
        return this.id;
    }

    public String getActivity() {
        return this.activity;
    }

    public String getComment() {
        return this.comment;
    }

    public String getSignature() {
        return this.signature;
    }

    public String getUserId() {
        return this.userId;
    }

    public String getWitnessId() {
        return this.witnessId;
    }

    public String getWitnessName() {
        return this.witnessName;
    }

    public CaseInsensitiveLinkedMap<Object> getOldValueMap() {
        return this.oldValueMap;
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
}
