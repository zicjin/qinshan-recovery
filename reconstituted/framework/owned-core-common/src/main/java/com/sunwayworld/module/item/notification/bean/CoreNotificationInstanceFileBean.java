/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.item.notification.bean;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.support.domain.AbstractInsertable;

@Table(value="T_CORE_NOTIFICATION_INST_FILE")
public class CoreNotificationInstanceFileBean
extends AbstractInsertable<Long> {
    private static final long serialVersionUID = -8990464074298643222L;
    @Id
    private Long id;
    private Long instId;
    private String fileName;
    private String fileDownloadUrl;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getInstId() {
        return this.instId;
    }

    public void setInstId(Long instId) {
        this.instId = instId;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileDownloadUrl() {
        return this.fileDownloadUrl;
    }

    public void setFileDownloadUrl(String fileDownloadUrl) {
        this.fileDownloadUrl = fileDownloadUrl;
    }
}
