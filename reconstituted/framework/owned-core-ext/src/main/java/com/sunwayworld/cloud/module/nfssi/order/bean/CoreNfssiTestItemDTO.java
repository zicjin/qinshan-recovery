/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.nfssi.order.bean;

import com.sunwayworld.cloud.module.nfssi.order.bean.CoreNfssiTestReasonDTO;
import com.sunwayworld.cloud.module.nfssi.order.bean.CoreNfssiVerifyReasonDTO;
import com.sunwayworld.framework.data.annotation.Transient;
import java.io.Serializable;
import java.util.List;

public class CoreNfssiTestItemDTO
implements Serializable {
    @Transient
    private static final long serialVersionUID = -3567544549225983532L;
    private Long id;
    private String sp_s_16;
    private String item;
    private String itemType;
    private String gnlb;
    private List<CoreNfssiVerifyReasonDTO> verifyReason;
    private List<CoreNfssiTestReasonDTO> testReason;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSp_s_16() {
        return this.sp_s_16;
    }

    public void setSp_s_16(String sp_s_16) {
        this.sp_s_16 = sp_s_16;
    }

    public String getItem() {
        return this.item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getItemType() {
        return this.itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public String getGnlb() {
        return this.gnlb;
    }

    public void setGnlb(String gnlb) {
        this.gnlb = gnlb;
    }

    public List<CoreNfssiVerifyReasonDTO> getVerifyReason() {
        return this.verifyReason;
    }

    public void setVerifyReason(List<CoreNfssiVerifyReasonDTO> verifyReason) {
        this.verifyReason = verifyReason;
    }

    public List<CoreNfssiTestReasonDTO> getTestReason() {
        return this.testReason;
    }

    public void setTestReason(List<CoreNfssiTestReasonDTO> testReason) {
        this.testReason = testReason;
    }
}
