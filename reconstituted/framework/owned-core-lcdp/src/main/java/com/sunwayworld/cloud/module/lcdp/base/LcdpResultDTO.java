/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.base;

import com.sunwayworld.framework.support.domain.AbstractBaseData;

public class LcdpResultDTO
extends AbstractBaseData {
    private static final long serialVersionUID = 7205056622411056371L;
    private String code;
    private String message;
    private Object id;

    public LcdpResultDTO(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public LcdpResultDTO(String code, String message, Object id) {
        this.code = code;
        this.message = message;
        this.id = id;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getId() {
        return this.id;
    }

    public void setId(Object id) {
        this.id = id;
    }
}
