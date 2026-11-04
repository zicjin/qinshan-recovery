/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.item.file.event;

import org.springframework.context.ApplicationEvent;

public class CoreFileUploadEvent
extends ApplicationEvent {
    private static final long serialVersionUID = 2546987639455034609L;
    private Long fileId;

    public CoreFileUploadEvent(Long fileId, Object fileObject) {
        super(fileObject);
        this.fileId = fileId;
    }

    public Long getFileId() {
        return this.fileId;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }
}
