/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.spcs.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;

public class CoreSpcLineBean
implements Serializable {
    private static final long serialVersionUID = 7357304877623845113L;
    @JsonProperty(value="yAxis")
    private Double yAxis;
    private String tooltip;
    private String type;
    private int startIndex;
    private int endIndex;

    public Double getyAxis() {
        return this.yAxis;
    }

    public void setyAxis(Double yAxis) {
        this.yAxis = yAxis;
    }

    public String getTooltip() {
        return this.tooltip;
    }

    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getStartIndex() {
        return this.startIndex;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public int getEndIndex() {
        return this.endIndex;
    }

    public void setEndIndex(int endIndex) {
        this.endIndex = endIndex;
    }
}
