/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.item.file.bean;

import java.io.Serializable;

public class CoreFileUploadDTO
implements Serializable {
    private static final long serialVersionUID = 2689177232699137676L;
    private Long id;
    private String scope;
    private String timestamp;
    private String irregularName;
    private String fileName;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getScope() {
        return this.scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getIrregularName() {
        return this.irregularName;
    }

    public void setIrregularName(String irregularName) {
        this.irregularName = irregularName;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
