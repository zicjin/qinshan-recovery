/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.admin.script.bean;

import java.io.Serializable;

public class CoreAdminScriptResultDTO
implements Serializable {
    private static final long serialVersionUID = -7691395012922232734L;
    private int code;
    private String content;

    public int getCode() {
        return this.code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
