/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.bpmn.bean;

import com.sunwayworld.framework.support.domain.AbstractBaseData;

public class CoreBpmnRuntimeDTO
extends AbstractBaseData {
    private static final long serialVersionUID = -1789059094314576458L;
    private String processStatus;
    private Long instanceId;
    private Long procId;
    private Long diagramId;

    public Long getInstanceId() {
        return this.instanceId;
    }

    public void setInstanceId(Long instanceId) {
        this.instanceId = instanceId;
    }

    public Long getProcId() {
        return this.procId;
    }

    public void setProcId(Long procId) {
        this.procId = procId;
    }

    public Long getDiagramId() {
        return this.diagramId;
    }

    public void setDiagramId(Long diagramId) {
        this.diagramId = diagramId;
    }

    public String getProcessStatus() {
        return this.processStatus;
    }

    public void setProcessStatus(String processStatus) {
        this.processStatus = processStatus;
    }
}
