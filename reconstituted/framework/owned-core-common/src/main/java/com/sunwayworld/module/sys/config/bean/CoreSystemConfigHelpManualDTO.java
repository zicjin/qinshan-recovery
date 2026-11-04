/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.config.bean;

import java.io.Serializable;

public class CoreSystemConfigHelpManualDTO
implements Serializable {
    private static final long serialVersionUID = -8551459496595240071L;
    private Long fileId;
    private String fileName;
    private String downloadUrl;

    public Long getFileId() {
        return this.fileId;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getDownloadUrl() {
        return this.downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }
}
