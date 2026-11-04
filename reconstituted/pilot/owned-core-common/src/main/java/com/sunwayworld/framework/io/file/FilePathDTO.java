/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.io.file;

import com.sunwayworld.framework.io.file.FileScope;
import com.sunwayworld.framework.utils.DateTimeUtils;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class FilePathDTO
implements Serializable {
    private static final long serialVersionUID = -9053706250092340444L;
    private FileScope scope;
    private String timestamp;
    private String irregularName;
    private String fileName;

    private FilePathDTO() {
    }

    public static final FilePathDTO of(String scope, String irregularName, String fileName) {
        return FilePathDTO.of(scope, "", irregularName, fileName);
    }

    public static final FilePathDTO of(String scope, LocalDateTime timestamp, String irregularName, String fileName) {
        return FilePathDTO.of(scope, DateTimeUtils.getDateSerialNumber(Objects.requireNonNull(timestamp)), irregularName, fileName);
    }

    public static final FilePathDTO of(String scope, String timestamp, String irregularName, String fileName) {
        FilePathDTO fileRepresentation = new FilePathDTO();
        fileRepresentation.scope = Objects.requireNonNull(FileScope.valueOf(scope));
        fileRepresentation.timestamp = timestamp;
        fileRepresentation.irregularName = Objects.requireNonNull(irregularName);
        fileRepresentation.fileName = Objects.requireNonNull(fileName);
        return fileRepresentation;
    }

    public FileScope getScope() {
        return this.scope;
    }

    public String getTimestamp() {
        return this.timestamp;
    }

    public String getFileName() {
        return this.fileName;
    }

    public String getIrregularName() {
        return this.irregularName;
    }

    public void setScope(FileScope scope) {
        this.scope = scope;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setIrregularName(String irregularName) {
        this.irregularName = irregularName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
