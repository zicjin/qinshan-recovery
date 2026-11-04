/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.customds.bean;

import java.io.Serializable;

public class CoreCustomDsJoinFieldDTO
implements Serializable {
    private static final long serialVersionUID = 470103424187972100L;
    private String mainModelField;
    private String subModelField;
    private String operate;

    public String getMainModelField() {
        return this.mainModelField;
    }

    public void setMainModelField(String mainModelField) {
        this.mainModelField = mainModelField;
    }

    public String getSubModelField() {
        return this.subModelField;
    }

    public void setSubModelField(String subModelField) {
        this.subModelField = subModelField;
    }

    public String getOperate() {
        return this.operate;
    }

    public void setOperate(String operate) {
        this.operate = operate;
    }
}
