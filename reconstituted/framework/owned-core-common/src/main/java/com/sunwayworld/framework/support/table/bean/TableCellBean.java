/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.support.table.bean;

import com.sunwayworld.framework.support.domain.AbstractBaseData;
import java.io.Serializable;

public class TableCellBean
extends AbstractBaseData
implements Serializable {
    private static final long serialVersionUID = 4405761231494770618L;
    private String key;
    private String value;

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
