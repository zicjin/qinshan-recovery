/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.office.bean;

import com.sunwayworld.framework.support.domain.AbstractBaseData;

public class CoreOfficeInstanceDTO
extends AbstractBaseData {
    private static final long serialVersionUID = 1846900645936148285L;
    private Long instanceId;
    private Long fileId;
    private String downloadUrl;

    public Long getInstanceId() {
        return this.instanceId;
    }

    public void setInstanceId(Long instanceId) {
        this.instanceId = instanceId;
    }

    public Long getFileId() {
        return this.fileId;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }

    public String getDownloadUrl() {
        return this.downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }
}
