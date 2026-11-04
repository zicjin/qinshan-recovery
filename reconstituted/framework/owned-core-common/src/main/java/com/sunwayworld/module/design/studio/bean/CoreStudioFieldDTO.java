/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.design.studio.bean;

import com.sunwayworld.module.design.studio.bean.CoreStudioFieldConfigDTO;
import java.io.Serializable;

public class CoreStudioFieldDTO
implements Serializable {
    private static final long serialVersionUID = -6242302386926521940L;
    private String table;
    private String field;
    private String title;
    private boolean form = false;
    private boolean appendField = false;
    private CoreStudioFieldConfigDTO fieldConfig;
    private String json;

    public String getTable() {
        return this.table;
    }

    public void setTable(String table) {
        this.table = table;
    }

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

    public boolean isForm() {
        return this.form;
    }

    public void setForm(boolean form) {
        this.form = form;
    }

    public boolean isAppendField() {
        return this.appendField;
    }

    public void setAppendField(boolean appendField) {
        this.appendField = appendField;
    }

    public CoreStudioFieldConfigDTO getFieldConfig() {
        return this.fieldConfig;
    }

    public void setFieldConfig(CoreStudioFieldConfigDTO fieldConfig) {
        this.fieldConfig = fieldConfig;
    }

    public String getJson() {
        return this.json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public boolean isDefault() {
        return this.getFieldConfig() == null || this.getFieldConfig().getField() == null;
    }
}
