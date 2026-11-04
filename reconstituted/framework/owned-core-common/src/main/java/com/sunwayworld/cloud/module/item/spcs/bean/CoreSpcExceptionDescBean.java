/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.spcs.bean;

import java.io.Serializable;

public class CoreSpcExceptionDescBean
implements Serializable {
    private static final long serialVersionUID = 2788749343957120224L;
    private String ruleId;
    private String color;
    private String text;

    public String getRuleId() {
        return this.ruleId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    public String getColor() {
        return this.color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
