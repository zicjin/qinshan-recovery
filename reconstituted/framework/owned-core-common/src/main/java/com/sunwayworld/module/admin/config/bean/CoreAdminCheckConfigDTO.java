/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.admin.config.bean;

import java.io.Serializable;

public class CoreAdminCheckConfigDTO
implements Serializable {
    private static final long serialVersionUID = 524175561570468248L;
    private String category;
    private String name;
    private String url;
    private String valueField;
    private String textField;
    private String groupField;
    private String cacheable;
    private String displayOther;
    private String otherRequired;

    public String getCategory() {
        return this.category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getValueField() {
        return this.valueField;
    }

    public void setValueField(String valueField) {
        this.valueField = valueField;
    }

    public String getTextField() {
        return this.textField;
    }

    public void setTextField(String textField) {
        this.textField = textField;
    }

    public String getGroupField() {
        return this.groupField;
    }

    public void setGroupField(String groupField) {
        this.groupField = groupField;
    }

    public String getCacheable() {
        return this.cacheable;
    }

    public void setCacheable(String cacheable) {
        this.cacheable = cacheable;
    }

    public String getDisplayOther() {
        return this.displayOther;
    }

    public void setDisplayOther(String displayOther) {
        this.displayOther = displayOther;
    }

    public String getOtherRequired() {
        return this.otherRequired;
    }

    public void setOtherRequired(String otherRequired) {
        this.otherRequired = otherRequired;
    }
}
