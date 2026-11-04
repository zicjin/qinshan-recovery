/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.bpmn.event;

import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceStatusDTO;
import java.util.List;
import org.springframework.context.ApplicationEvent;

public class CoreBpmnUpdateStatusEvent
extends ApplicationEvent {
    private static final long serialVersionUID = -595507526485393875L;
    private List<CoreBpmnInstanceStatusDTO<?>> statusList;

    public CoreBpmnUpdateStatusEvent(Object tableName, List statusList) {
        super(tableName);
        this.statusList = statusList;
    }

    public List<CoreBpmnInstanceStatusDTO<?>> getStatusList() {
        return this.statusList;
    }

    public void setStatusList(List<CoreBpmnInstanceStatusDTO<?>> statusList) {
        this.statusList = statusList;
    }
}
