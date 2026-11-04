/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.bpmn.bean;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

public class CoreBpmnInstanceRuntimeContainerDTO
implements Serializable {
    private static final long serialVersionUID = -6429237230543818744L;
    private Long procId;
    private Long procInstId;
    private String initiator;
    private Map<String, Object> vars;
    @Nullable
    private List<String> candidatorList;

    public Long getProcId() {
        return this.procId;
    }

    public void setProcId(Long procId) {
        this.procId = procId;
    }

    public Long getProcInstId() {
        return this.procInstId;
    }

    public void setProcInstId(Long procInstId) {
        this.procInstId = procInstId;
    }

    public String getInitiator() {
        return this.initiator;
    }

    public void setInitiator(String initiator) {
        this.initiator = initiator;
    }

    public List<String> getCandidatorList() {
        return this.candidatorList;
    }

    public void setCandidatorList(List<String> candidatorList) {
        this.candidatorList = candidatorList;
    }

    public Map<String, Object> getVars() {
        return this.vars;
    }

    public void setVars(Map<String, Object> vars) {
        this.vars = vars;
    }
}
