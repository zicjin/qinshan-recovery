/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.helper.media;

import java.io.BufferedInputStream;

public class MediaFile {
    private String fileName;
    private String fullName;
    private String suffix;
    private String contentLength;
    private String contentType;
    private BufferedInputStream fileStream;
    private String error;

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFullName() {
        return this.fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getSuffix() {
        return this.suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getContentLength() {
        return this.contentLength;
    }

    public void setContentLength(String contentLength) {
        this.contentLength = contentLength;
    }

    public String getContentType() {
        return this.contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public BufferedInputStream getFileStream() {
        return this.fileStream;
    }

    public void setFileStream(BufferedInputStream fileStream) {
        this.fileStream = fileStream;
    }

    public String getError() {
        return this.error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
