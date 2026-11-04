/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.design.studio.bean;

import com.alibaba.fastjson.JSONObject;
import java.io.Serializable;

public class CoreStudioBlockFormDTO
implements Serializable {
    private static final long serialVersionUID = -6014258283891839139L;
    private JSONObject[] columns;
    private boolean visible;
    private Integer formColumnQty;

    public JSONObject[] getColumns() {
        return this.columns;
    }

    public void setColumns(JSONObject[] columns) {
        this.columns = columns;
    }

    public boolean isVisible() {
        return this.visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public Integer getFormColumnQty() {
        return this.formColumnQty;
    }

    public void setFormColumnQty(Integer formColumnQty) {
        this.formColumnQty = formColumnQty;
    }
}
