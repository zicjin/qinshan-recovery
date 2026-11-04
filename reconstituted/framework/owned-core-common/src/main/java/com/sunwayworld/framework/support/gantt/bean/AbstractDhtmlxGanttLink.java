/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.support.gantt.bean;

public abstract class AbstractDhtmlxGanttLink {
    private String id;
    private String source;
    private String target;
    private String type;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSource() {
        return this.source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTarget() {
        return this.target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
