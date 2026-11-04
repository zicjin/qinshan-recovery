/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.attorney.bean;

import java.io.Serializable;

public class CoreAttorneyDTO
implements Serializable {
    private static final long serialVersionUID = -1420472974067666179L;
    private String trustor;
    private String trustorName;
    private Integer todoQty;

    public String getTrustor() {
        return this.trustor;
    }

    public void setTrustor(String trustor) {
        this.trustor = trustor;
    }

    public String getTrustorName() {
        return this.trustorName;
    }

    public void setTrustorName(String trustorName) {
        this.trustorName = trustorName;
    }

    public Integer getTodoQty() {
        return this.todoQty;
    }

    public void setTodoQty(Integer todoQty) {
        this.todoQty = todoQty;
    }
}
