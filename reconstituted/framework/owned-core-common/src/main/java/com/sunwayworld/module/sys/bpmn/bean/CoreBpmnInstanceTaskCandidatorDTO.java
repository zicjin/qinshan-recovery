/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.bpmn.bean;

import com.sunwayworld.framework.support.domain.AbstractBaseData;
import com.sunwayworld.framework.support.domain.BaseData;

public class CoreBpmnInstanceTaskCandidatorDTO
extends AbstractBaseData
implements BaseData {
    private static final long serialVersionUID = 8224175189030735826L;
    private Long instTaskId;
    private String candidatorId;

    public Long getInstTaskId() {
        return this.instTaskId;
    }

    public void setInstTaskId(Long instTaskId) {
        this.instTaskId = instTaskId;
    }

    public String getCandidatorId() {
        return this.candidatorId;
    }

    public void setCandidatorId(String candidatorId) {
        this.candidatorId = candidatorId;
    }
}
