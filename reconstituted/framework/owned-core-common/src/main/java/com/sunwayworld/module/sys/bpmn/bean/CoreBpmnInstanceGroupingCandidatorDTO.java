/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.bpmn.bean;

import com.sunwayworld.framework.support.domain.AbstractBaseData;
import com.sunwayworld.framework.support.domain.BaseData;

public class CoreBpmnInstanceGroupingCandidatorDTO
extends AbstractBaseData
implements BaseData {
    private static final long serialVersionUID = 8526910356224985462L;
    private Long taskId;
    private String taskName;
    private String candidator;

    public Long getTaskId() {
        return this.taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getTaskName() {
        return this.taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getCandidator() {
        return this.candidator;
    }

    public void setCandidator(String candidator) {
        this.candidator = candidator;
    }
}
