/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.fastdfs.bean;

import com.sunwayworld.framework.fastdfs.bean.AbstractFastDfsResponseDTO;
import java.io.Serializable;

public class FastDfsUploadResponseDTO
extends AbstractFastDfsResponseDTO {
    private static final long serialVersionUID = -3404559653334166240L;
    private UploadResponseData data;
    private String publicUrl;

    public UploadResponseData getData() {
        return this.data;
    }

    public void setData(UploadResponseData data) {
        this.data = data;
    }

    public String getPublicUrl() {
        return this.publicUrl;
    }

    public void setPublicUrl(String publicUrl) {
        this.publicUrl = publicUrl;
    }

    public static class UploadResponseData
    implements Serializable {
        private static final long serialVersionUID = 9151265278862404325L;
        private String domain;
        private String md5;
        private Long mtime;
        private String path;
        private Long retcode;
        private String retmsg;
        private String scene;
        private String scenes;
        private Long size;
        private String src;
        private String url;

        public String getDomain() {
            return this.domain;
        }

        public void setDomain(String domain) {
            this.domain = domain;
        }

        public String getMd5() {
            return this.md5;
        }

        public void setMd5(String md5) {
            this.md5 = md5;
        }

        public Long getMtime() {
            return this.mtime;
        }

        public void setMtime(Long mtime) {
            this.mtime = mtime;
        }

        public String getPath() {
            return this.path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public Long getRetcode() {
            return this.retcode;
        }

        public void setRetcode(Long retcode) {
            this.retcode = retcode;
        }

        public String getRetmsg() {
            return this.retmsg;
        }

        public void setRetmsg(String retmsg) {
            this.retmsg = retmsg;
        }

        public String getScene() {
            return this.scene;
        }

        public void setScene(String scene) {
            this.scene = scene;
        }

        public String getScenes() {
            return this.scenes;
        }

        public void setScenes(String scenes) {
            this.scenes = scenes;
        }

        public Long getSize() {
            return this.size;
        }

        public void setSize(Long size) {
            this.size = size;
        }

        public String getSrc() {
            return this.src;
        }

        public void setSrc(String src) {
            this.src = src;
        }

        public String getUrl() {
            return this.url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
