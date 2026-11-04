/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.rounding.bean;

public class CoreRoundingRuleDTO {
    private String id;
    private String originalResult;
    private String param;
    private String revisionResult;

    public String getId() {
        return this.id;
    }

    public String getOriginalResult() {
        return this.originalResult;
    }

    public String getParam() {
        return this.param;
    }

    public String getRevisionResult() {
        return this.revisionResult;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setOriginalResult(String originalResult) {
        this.originalResult = originalResult;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public void setRevisionResult(String revisionResult) {
        this.revisionResult = revisionResult;
    }
}
