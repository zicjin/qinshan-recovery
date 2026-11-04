/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.item.file.bean;

import java.io.Serializable;

public class CoreFileImportResultDTO
implements Serializable {
    private static final long serialVersionUID = -7819129985583954833L;
    private String id;
    private String url;
    private String message;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
