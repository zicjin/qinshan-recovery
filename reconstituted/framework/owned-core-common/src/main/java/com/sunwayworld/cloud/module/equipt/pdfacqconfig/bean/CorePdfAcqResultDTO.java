/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.equipt.pdfacqconfig.bean;

import com.sunwayworld.framework.data.annotation.Transient;
import java.io.Serializable;

public class CorePdfAcqResultDTO
implements Serializable {
    @Transient
    private static final long serialVersionUID = -5374300758272337246L;
    private String sampleId;
    private String itemName;
    private String result;
    private Long orderNo;

    public String getSampleId() {
        return this.sampleId;
    }

    public void setSampleId(String sampleId) {
        this.sampleId = sampleId;
    }

    public String getItemName() {
        return this.itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getResult() {
        return this.result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Long getOrderNo() {
        return this.orderNo;
    }

    public void setOrderNo(Long orderNo) {
        this.orderNo = orderNo;
    }
}
