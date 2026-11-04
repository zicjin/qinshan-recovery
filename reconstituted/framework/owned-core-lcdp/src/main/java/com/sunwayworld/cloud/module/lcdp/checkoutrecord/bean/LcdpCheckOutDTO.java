/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.checkoutrecord.bean;

import com.sunwayworld.cloud.module.lcdp.checkoutrecord.bean.LcdpCheckoutDetailDTO;
import com.sunwayworld.framework.support.domain.AbstractBaseData;
import java.util.List;

public class LcdpCheckOutDTO
extends AbstractBaseData {
    private static final long serialVersionUID = -4921361708968023308L;
    private List<LcdpCheckoutDetailDTO> checkoutDetailList;
    private String downLoadUrl;

    public String getDownLoadUrl() {
        return this.downLoadUrl;
    }

    public void setDownLoadUrl(String downLoadUrl) {
        this.downLoadUrl = downLoadUrl;
    }

    public List<LcdpCheckoutDetailDTO> getCheckoutDetailList() {
        return this.checkoutDetailList;
    }

    public void setCheckoutDetailList(List<LcdpCheckoutDetailDTO> checkoutDetailList) {
        this.checkoutDetailList = checkoutDetailList;
    }
}
