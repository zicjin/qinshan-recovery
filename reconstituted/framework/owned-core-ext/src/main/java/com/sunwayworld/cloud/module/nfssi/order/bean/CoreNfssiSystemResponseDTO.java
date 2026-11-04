/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.nfssi.order.bean;

import com.sunwayworld.framework.data.annotation.Transient;
import java.io.Serializable;

public class CoreNfssiSystemResponseDTO
implements Serializable {
    @Transient
    private static final long serialVersionUID = -694716265147761578L;
    private Long code;
    private String msg;
    private String data;

    public Long getCode() {
        return this.code;
    }

    public void setCode(Long code) {
        this.code = code;
    }

    public String getMsg() {
        return this.msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getData() {
        return this.data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
