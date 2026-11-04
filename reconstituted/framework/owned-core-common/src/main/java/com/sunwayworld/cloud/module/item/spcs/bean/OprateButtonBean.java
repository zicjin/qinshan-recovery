/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.spcs.bean;

import java.io.Serializable;

public class OprateButtonBean
implements Serializable {
    private static final long serialVersionUID = -8798955040665447085L;
    private Integer summary;
    private Integer details;

    public Integer getSummary() {
        return this.summary;
    }

    public void setSummary(Integer summary) {
        this.summary = summary;
    }

    public Integer getDetails() {
        return this.details;
    }

    public void setDetails(Integer details) {
        this.details = details;
    }
}
