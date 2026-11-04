/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.bpmn.diagram;

public enum ProcessElementType {
    START_EVENT("startEvent"),
    USER_TASK("userTask"),
    END_EVENT("endEvent"),
    SEQUENCE_FLOW("sequenceFlow"),
    PARALLEL_GATEWAY("parallelGateway"),
    EXCLUSIVE_GATEWAY("exclusiveGateway");

    private String name;

    private ProcessElementType(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public static ProcessElementType nameOf(String name) {
        for (ProcessElementType type : ProcessElementType.values()) {
            if (!type.getName().equals(name)) continue;
            return type;
        }
        return null;
    }
}
