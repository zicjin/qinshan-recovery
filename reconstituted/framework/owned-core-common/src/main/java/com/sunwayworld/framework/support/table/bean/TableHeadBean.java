/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.support.table.bean;

import com.sunwayworld.framework.support.domain.AbstractBaseData;
import java.io.Serializable;

public class TableHeadBean
extends AbstractBaseData
implements Serializable {
    private static final long serialVersionUID = 2443023062771889814L;
    private String field;
    private String title;

    public String getField() {
        return this.field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
