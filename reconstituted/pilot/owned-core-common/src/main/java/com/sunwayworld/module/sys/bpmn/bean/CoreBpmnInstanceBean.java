/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.bpmn.bean;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import com.sunwayworld.framework.support.domain.Insertable;

@Table(value="T_CORE_BPMN_INST")
public class CoreBpmnInstanceBean
extends AbstractInsertable<Long>
implements Insertable<Long> {
    private static final long serialVersionUID = 8731362359855549210L;
    @Id
    private Long id;
    private String targetId;
    private Long procId;
    private String initiator;
    private Long diagramId;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getTargetId() {
        return this.targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public Long getProcId() {
        return this.procId;
    }

    public void setProcId(Long procId) {
        this.procId = procId;
    }

    public String getInitiator() {
        return this.initiator;
    }

    public void setInitiator(String initiator) {
        this.initiator = initiator;
    }

    public Long getDiagramId() {
        return this.diagramId;
    }

    public void setDiagramId(Long diagramId) {
        this.diagramId = diagramId;
    }
}
