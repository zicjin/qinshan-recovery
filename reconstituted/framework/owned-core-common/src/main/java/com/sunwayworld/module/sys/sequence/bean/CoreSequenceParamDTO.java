/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.sequence.bean;

import com.sunwayworld.framework.data.annotation.Transient;
import java.io.Serializable;

public class CoreSequenceParamDTO
implements Serializable {
    @Transient
    private static final long serialVersionUID = -5333987310538158152L;
    private String key;
    private String yyyy;
    private String yy;
    private String mm;
    private String dd;
    private Integer n;

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getYyyy() {
        return this.yyyy;
    }

    public void setYyyy(String yyyy) {
        this.yyyy = yyyy;
    }

    public String getYy() {
        return this.yy;
    }

    public void setYy(String yy) {
        this.yy = yy;
    }

    public String getMm() {
        return this.mm;
    }

    public void setMm(String mm) {
        this.mm = mm;
    }

    public String getDd() {
        return this.dd;
    }

    public void setDd(String dd) {
        this.dd = dd;
    }

    public Integer getN() {
        return this.n;
    }

    public void setN(Integer n) {
        this.n = n;
    }
}
