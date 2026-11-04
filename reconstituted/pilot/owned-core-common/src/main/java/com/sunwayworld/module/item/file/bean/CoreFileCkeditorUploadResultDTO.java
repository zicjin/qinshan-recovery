/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.item.file.bean;

import java.io.Serializable;

public class CoreFileCkeditorUploadResultDTO
implements Serializable {
    private static final long serialVersionUID = 7430326700552264346L;
    private Integer uploaded = 1;
    private String fileName;
    private String url;

    public Integer getUploaded() {
        return this.uploaded;
    }

    public void setUploaded(Integer uploaded) {
        this.uploaded = uploaded;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
