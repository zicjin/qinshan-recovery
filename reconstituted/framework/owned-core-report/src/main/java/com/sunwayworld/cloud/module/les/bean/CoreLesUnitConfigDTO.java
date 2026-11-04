/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.les.bean;

import com.sunwayworld.framework.support.domain.AbstractBaseData;

public class CoreLesUnitConfigDTO
extends AbstractBaseData {
    private static final long serialVersionUID = 1769808339193851923L;
    private String title;
    private String value;
    private String field;
    private Long lesDsId;
    private Long lesDsFieldId;
    private Long colspan;
    private Long explicitImplicit;
    private Boolean readonly;
    private String lesDsName;

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getField() {
        return this.field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public Long getLesDsId() {
        return this.lesDsId;
    }

    public void setLesDsId(Long lesDsId) {
        this.lesDsId = lesDsId;
    }

    public Long getLesDsFieldId() {
        return this.lesDsFieldId;
    }

    public void setLesDsFieldId(Long lesDsFieldId) {
        this.lesDsFieldId = lesDsFieldId;
    }

    public Long getColspan() {
        return this.colspan;
    }

    public void setColspan(Long colspan) {
        this.colspan = colspan;
    }

    public Long getExplicitImplicit() {
        return this.explicitImplicit;
    }

    public void setExplicitImplicit(Long explicitImplicit) {
        this.explicitImplicit = explicitImplicit;
    }

    public Boolean getReadonly() {
        return this.readonly;
    }

    public void setReadonly(Boolean readonly) {
        this.readonly = readonly;
    }

    public String getLesDsName() {
        return this.lesDsName;
    }

    public void setLesDsName(String lesDsName) {
        this.lesDsName = lesDsName;
    }
}
