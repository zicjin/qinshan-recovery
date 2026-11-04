/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.spcs.bean;

import java.io.Serializable;

public class LimisBean
implements Serializable {
    private static final long serialVersionUID = 4746628658513090099L;
    private Double upperLimit;
    private Double lowerLimit;

    public Double getUpperLimit() {
        return this.upperLimit;
    }

    public void setUpperLimit(Double upperLimit) {
        this.upperLimit = upperLimit;
    }

    public Double getLowerLimit() {
        return this.lowerLimit;
    }

    public void setLowerLimit(Double lowerLimit) {
        this.lowerLimit = lowerLimit;
    }
}
