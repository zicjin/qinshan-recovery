/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.admin.helpcenter.bean;

import java.io.Serializable;

public class CoreHelpCenterDTO
implements Serializable {
    private static final long serialVersionUID = -8551459496595240071L;
    private Long manualVideoFileId;
    private String manualVideoFileName;
    private String manualVideoFileDownloadUrl;
    private String manualEditingMethod;
    private Long manualDocFileId;
    private String manualDocFileName;
    private String manualDocFileDownloadUrl;
    private String manualContent;

    public Long getManualVideoFileId() {
        return this.manualVideoFileId;
    }

    public void setManualVideoFileId(Long manualVideoFileId) {
        this.manualVideoFileId = manualVideoFileId;
    }

    public String getManualVideoFileName() {
        return this.manualVideoFileName;
    }

    public void setManualVideoFileName(String manualVideoFileName) {
        this.manualVideoFileName = manualVideoFileName;
    }

    public String getManualVideoFileDownloadUrl() {
        return this.manualVideoFileDownloadUrl;
    }

    public void setManualVideoFileDownloadUrl(String manualVideoFileDownloadUrl) {
        this.manualVideoFileDownloadUrl = manualVideoFileDownloadUrl;
    }

    public String getManualEditingMethod() {
        return this.manualEditingMethod;
    }

    public void setManualEditingMethod(String manualEditingMethod) {
        this.manualEditingMethod = manualEditingMethod;
    }

    public Long getManualDocFileId() {
        return this.manualDocFileId;
    }

    public void setManualDocFileId(Long manualDocFileId) {
        this.manualDocFileId = manualDocFileId;
    }

    public String getManualDocFileName() {
        return this.manualDocFileName;
    }

    public void setManualDocFileName(String manualDocFileName) {
        this.manualDocFileName = manualDocFileName;
    }

    public String getManualDocFileDownloadUrl() {
        return this.manualDocFileDownloadUrl;
    }

    public void setManualDocFileDownloadUrl(String manualDocFileDownloadUrl) {
        this.manualDocFileDownloadUrl = manualDocFileDownloadUrl;
    }

    public String getManualContent() {
        return this.manualContent;
    }

    public void setManualContent(String manualContent) {
        this.manualContent = manualContent;
    }
}
