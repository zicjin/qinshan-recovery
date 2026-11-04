/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.support.auditable.bean;

import java.io.Serializable;

public class CoreBpmnTaskDTO
implements Serializable {
    private static final long serialVersionUID = -1194042252577136926L;
    private String name;
    private String statusCode;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatusCode() {
        return this.statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }
}
