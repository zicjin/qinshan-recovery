/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.equipt.pdfacqconfig.bean;

import com.sunwayworld.framework.data.annotation.Transient;
import java.io.Serializable;

public class CorePdfAcqAreaResultParamDTO
implements Serializable {
    @Transient
    private static final long serialVersionUID = 4961151561430258398L;
    private String itemName;
    private Long resultColNo;
    private Long sampleNoColNo;
    private Long itemColNo;

    public String getItemName() {
        return this.itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Long getResultColNo() {
        return this.resultColNo;
    }

    public void setResultColNo(Long resultColNo) {
        this.resultColNo = resultColNo;
    }

    public Long getSampleNoColNo() {
        return this.sampleNoColNo;
    }

    public void setSampleNoColNo(Long sampleNoColNo) {
        this.sampleNoColNo = sampleNoColNo;
    }

    public Long getItemColNo() {
        return this.itemColNo;
    }

    public void setItemColNo(Long itemColNo) {
        this.itemColNo = itemColNo;
    }
}
