/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.support.base.service;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.extractor.DownloadParamExtractor;
import java.sql.ResultSet;

public interface GenericExcelBuilderService {
    public <T> DownloadExcelDTO buildExcel(DownloadParamExtractor var1, Class<? extends Object> var2, ResultSet var3, String var4, String ... var5);

    public <T> DownloadExcelDTO buildExcel(DownloadParamExtractor var1, Page<T> var2, String var3);

    public static class DownloadExcelDTO {
        private String fileName;
        private String url;

        public String getFileName() {
            return this.fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getUrl() {
            return this.url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
