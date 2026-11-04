/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.support.base.bean;

import com.sunwayworld.framework.support.domain.AbstractBaseData;
import java.io.Serializable;

public class CoreControllerRequestEntryDTO
extends AbstractBaseData
implements Serializable {
    private static final long serialVersionUID = 3514977729179273484L;
    private String requestName;
    private String requestUrl;
    private String pageUrl;

    public String getRequestName() {
        return this.requestName;
    }

    public void setRequestName(String requestName) {
        this.requestName = requestName;
    }

    public String getRequestUrl() {
        return this.requestUrl;
    }

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    public String getPageUrl() {
        return this.pageUrl;
    }

    public void setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
    }
}
