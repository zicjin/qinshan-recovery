/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.pagefield.bean;

import java.io.Serializable;

public class CorePageFieldCompFieldDTO
implements Serializable {
    private static final long serialVersionUID = -5793659843747436987L;
    private String field;
    private String title;
    private String type;
    private String category;
    private Long colspan;
    private Boolean readonly;
    private String value;
    private Long width;
    private Boolean editor;
    private String extConf;

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

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCategory() {
        return this.category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Long getColspan() {
        return this.colspan;
    }

    public void setColspan(Long colspan) {
        this.colspan = colspan;
    }

    public Boolean getReadonly() {
        return this.readonly;
    }

    public void setReadonly(Boolean readonly) {
        this.readonly = readonly;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Long getWidth() {
        return this.width;
    }

    public void setWidth(Long width) {
        this.width = width;
    }

    public Boolean getEditor() {
        return this.editor;
    }

    public void setEditor(Boolean editor) {
        this.editor = editor;
    }

    public String getExtConf() {
        return this.extConf;
    }

    public void setExtConf(String extConf) {
        this.extConf = extConf;
    }
}
