/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.item.file.bean;

import com.sunwayworld.framework.io.file.FilePathDTO;
import java.io.Serializable;
import java.util.List;

public class CoreFileMergePdfDTO
implements Serializable {
    private static final long serialVersionUID = -7875290401868027254L;
    private List<FilePathDTO> sourceFilePathList;
    private FilePathDTO targetFilePath;

    public List<FilePathDTO> getSourceFilePathList() {
        return this.sourceFilePathList;
    }

    public void setSourceFilePathList(List<FilePathDTO> sourceFilePathList) {
        this.sourceFilePathList = sourceFilePathList;
    }

    public FilePathDTO getTargetFilePath() {
        return this.targetFilePath;
    }

    public void setTargetFilePath(FilePathDTO targetFilePath) {
        this.targetFilePath = targetFilePath;
    }
}
