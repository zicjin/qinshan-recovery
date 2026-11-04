/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.spcs.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;

public class CoreSpcExceptionPileBean
implements Serializable {
    private static final long serialVersionUID = 7390755794830647121L;
    @JsonProperty(value="xAxis")
    private Double xAxis;
    @JsonProperty(value="yAxis")
    private Double yAxis = 0.0;
    private String tooltip;
    private int[] symbolOffset;
    private String type;
    private String color;
    private Integer rulesIndex;
    private String ruleId;

    public Double getxAxis() {
        return this.xAxis;
    }

    public void setxAxis(Double xAxis) {
        this.xAxis = xAxis;
    }

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

    public int[] getSymbolOffset() {
        return this.symbolOffset;
    }

    public void setSymbolOffset(int[] symbolOffset) {
        this.symbolOffset = symbolOffset;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getColor() {
        return this.color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Integer getRulesIndex() {
        return this.rulesIndex;
    }

    public void setRulesIndex(Integer rulesIndex) {
        this.rulesIndex = rulesIndex;
    }

    public String getRuleId() {
        return this.ruleId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }
}
