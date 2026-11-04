/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.nfssi.order.bean;

import com.sunwayworld.framework.data.annotation.Transient;
import java.io.Serializable;

public class CoreNfssiVerifyReasonDTO
implements Serializable {
    @Transient
    private static final long serialVersionUID = -3567544549225983532L;
    private Long id;
    private Long testItemId;
    private String spdata_10;
    private String spdata_9;
    private String spdata_15;
    private String spdata_13;
    private String spdata_14;
    private String spdata_4;
    private String spdata_11;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTestItemId() {
        return this.testItemId;
    }

    public void setTestItemId(Long testItemId) {
        this.testItemId = testItemId;
    }

    public String getSpdata_10() {
        return this.spdata_10;
    }

    public void setSpdata_10(String spdata_10) {
        this.spdata_10 = spdata_10;
    }

    public String getSpdata_9() {
        return this.spdata_9;
    }

    public void setSpdata_9(String spdata_9) {
        this.spdata_9 = spdata_9;
    }

    public String getSpdata_15() {
        return this.spdata_15;
    }

    public void setSpdata_15(String spdata_15) {
        this.spdata_15 = spdata_15;
    }

    public String getSpdata_13() {
        return this.spdata_13;
    }

    public void setSpdata_13(String spdata_13) {
        this.spdata_13 = spdata_13;
    }

    public String getSpdata_14() {
        return this.spdata_14;
    }

    public void setSpdata_14(String spdata_14) {
        this.spdata_14 = spdata_14;
    }

    public String getSpdata_4() {
        return this.spdata_4;
    }

    public void setSpdata_4(String spdata_4) {
        this.spdata_4 = spdata_4;
    }

    public String getSpdata_11() {
        return this.spdata_11;
    }

    public void setSpdata_11(String spdata_11) {
        this.spdata_11 = spdata_11;
    }
}
