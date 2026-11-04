/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.nfssi.order.bean;

import com.sunwayworld.framework.data.annotation.Transient;
import java.io.Serializable;

public class CoreNfssiTestReasonDTO
implements Serializable {
    @Transient
    private static final long serialVersionUID = -3567544549225983532L;
    private Long id;
    private Long testItemId;
    private String bz;
    private String spdata_3;
    private String spdata_18;
    private String sm;
    private String spdata_6;
    private String spdata_5;
    private String sp_s_70;
    private String sp_s_67;
    private String inReserve;

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

    public String getBz() {
        return this.bz;
    }

    public void setBz(String bz) {
        this.bz = bz;
    }

    public String getSpdata_3() {
        return this.spdata_3;
    }

    public void setSpdata_3(String spdata_3) {
        this.spdata_3 = spdata_3;
    }

    public String getSpdata_18() {
        return this.spdata_18;
    }

    public void setSpdata_18(String spdata_18) {
        this.spdata_18 = spdata_18;
    }

    public String getSm() {
        return this.sm;
    }

    public void setSm(String sm) {
        this.sm = sm;
    }

    public String getSpdata_6() {
        return this.spdata_6;
    }

    public void setSpdata_6(String spdata_6) {
        this.spdata_6 = spdata_6;
    }

    public String getSpdata_5() {
        return this.spdata_5;
    }

    public void setSpdata_5(String spdata_5) {
        this.spdata_5 = spdata_5;
    }

    public String getSp_s_70() {
        return this.sp_s_70;
    }

    public void setSp_s_70(String sp_s_70) {
        this.sp_s_70 = sp_s_70;
    }

    public String getSp_s_67() {
        return this.sp_s_67;
    }

    public void setSp_s_67(String sp_s_67) {
        this.sp_s_67 = sp_s_67;
    }

    public String getInReserve() {
        return this.inReserve;
    }

    public void setInReserve(String inReserve) {
        this.inReserve = inReserve;
    }
}
