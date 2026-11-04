/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.bpmn.bean;

import com.sunwayworld.framework.support.domain.AbstractBaseData;

public class CoreBpmnDiagramSequenceFlowConditionDTO
extends AbstractBaseData {
    private static final long serialVersionUID = 8922820026286987347L;
    private String sourceTask;
    private String targetTask;
    private String conditionExpression;

    public String getSourceTask() {
        return this.sourceTask;
    }

    public void setSourceTask(String sourceTask) {
        this.sourceTask = sourceTask;
    }

    public String getTargetTask() {
        return this.targetTask;
    }

    public void setTargetTask(String targetTask) {
        this.targetTask = targetTask;
    }

    public String getConditionExpression() {
        return this.conditionExpression;
    }

    public void setConditionExpression(String conditionExpression) {
        this.conditionExpression = conditionExpression;
    }
}
