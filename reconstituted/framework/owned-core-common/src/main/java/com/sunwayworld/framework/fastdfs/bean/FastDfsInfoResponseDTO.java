/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.fastdfs.bean;

import com.sunwayworld.framework.fastdfs.bean.AbstractFastDfsResponseDTO;
import java.io.Serializable;

public class FastDfsInfoResponseDTO
extends AbstractFastDfsResponseDTO {
    private static final long serialVersionUID = 1885394940764719491L;
    private InfoResponseData data;
    private String publicUrl;

    public InfoResponseData getData() {
        return this.data;
    }

    public void setData(InfoResponseData data) {
        this.data = data;
    }

    public String getPublicUrl() {
        return this.publicUrl;
    }

    public void setPublicUrl(String publicUrl) {
        this.publicUrl = publicUrl;
    }

    public static class InfoResponseData
    implements Serializable {
        private static final long serialVersionUID = -7930225018908805622L;
        private String md5;
        private String name;
        private Long offset;
        private String path;
        private String[] peers;
        private String rename;
        private String scene;
        private Long size;
        private Long timestamp;

        public String getMd5() {
            return this.md5;
        }

        public void setMd5(String md5) {
            this.md5 = md5;
        }

        public String getName() {
            return this.name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Long getOffset() {
            return this.offset;
        }

        public void setOffset(Long offset) {
            this.offset = offset;
        }

        public String getPath() {
            return this.path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String[] getPeers() {
            return this.peers;
        }

        public void setPeers(String[] peers) {
            this.peers = peers;
        }

        public String getRename() {
            return this.rename;
        }

        public void setRename(String rename) {
            this.rename = rename;
        }

        public String getScene() {
            return this.scene;
        }

        public void setScene(String scene) {
            this.scene = scene;
        }

        public Long getSize() {
            return this.size;
        }

        public void setSize(Long size) {
            this.size = size;
        }

        public Long getTimestamp() {
            return this.timestamp;
        }

        public void setTimestamp(Long timestamp) {
            this.timestamp = timestamp;
        }
    }
}
