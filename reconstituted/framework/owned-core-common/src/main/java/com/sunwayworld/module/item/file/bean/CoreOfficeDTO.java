/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.item.file.bean;

import java.io.Serializable;
import java.nio.file.Path;

public class CoreOfficeDTO
implements Serializable {
    private static final long serialVersionUID = -8728548197005031649L;
    private Long fileId;
    private Path filePath;

    public CoreOfficeDTO(Long fileId, Path filePath) {
        this.fileId = fileId;
        this.filePath = filePath;
    }

    public Long getFileId() {
        return this.fileId;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }

    public Path getFilePath() {
        return this.filePath;
    }

    public void setFilePath(Path filePath) {
        this.filePath = filePath;
    }
}
