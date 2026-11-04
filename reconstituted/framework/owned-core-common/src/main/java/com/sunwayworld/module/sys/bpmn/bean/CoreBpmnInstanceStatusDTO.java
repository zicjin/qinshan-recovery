/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.bpmn.bean;

import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceRuntimeContainerDTO;
import com.sunwayworld.module.sys.bpmn.diagram.ProcessStatus;
import java.io.Serializable;

public class CoreBpmnInstanceStatusDTO<ID extends Serializable>
implements Serializable {
    private static final long serialVersionUID = -9142713558879631777L;
    private ID id;
    private String statusCode;
    private String nextStatusCode;
    private ProcessStatus processStatus;
    private CoreBpmnInstanceRuntimeContainerDTO runtimeContainer;

    public ID getId() {
        return this.id;
    }

    public void setId(ID id) {
        this.id = id;
    }

    public String getStatusCode() {
        return this.statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getNextStatusCode() {
        return this.nextStatusCode;
    }

    public void setNextStatusCode(String nextStatusCode) {
        this.nextStatusCode = nextStatusCode;
    }

    public ProcessStatus getProcessStatus() {
        return this.processStatus;
    }

    public void setProcessStatus(ProcessStatus processStatus) {
        this.processStatus = processStatus;
    }

    public CoreBpmnInstanceRuntimeContainerDTO getRuntimeContainer() {
        return this.runtimeContainer;
    }

    public void setRuntimeContainer(CoreBpmnInstanceRuntimeContainerDTO runtimeContainer) {
        this.runtimeContainer = runtimeContainer;
    }
}
