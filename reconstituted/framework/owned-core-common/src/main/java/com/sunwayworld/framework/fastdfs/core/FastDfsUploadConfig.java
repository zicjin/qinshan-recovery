/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.fastdfs.core;

import javax.annotation.Nullable;

public class FastDfsUploadConfig {
    private String group;
    @Nullable
    private String timestamp;
    private String path;
    private String fileName;

    private FastDfsUploadConfig(Builder builder) {
        this.group = builder.group;
        this.timestamp = builder.timestamp;
        this.path = builder.path;
        this.fileName = builder.fileName;
    }

    public static final FastDfsUploadConfig defaultConfig(String fileName, String path) {
        return FastDfsUploadConfig.builder().group("group").path(path).fileName(fileName).build();
    }

    public static final Builder builder() {
        return new Builder();
    }

    public String getGroup() {
        return this.group;
    }

    public String getTimestamp() {
        return this.timestamp;
    }

    public String getPath() {
        return this.path;
    }

    public String getFileName() {
        return this.fileName;
    }

    public static class Builder {
        private String group;
        @Nullable
        private String timestamp;
        private String path;
        private String fileName;

        public Builder group(String group) {
            this.group = group;
            return this;
        }

        public Builder path(String path) {
            this.path = path;
            return this;
        }

        public Builder timestamp(String timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder fileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        public FastDfsUploadConfig build() {
            return new FastDfsUploadConfig(this);
        }
    }
}
