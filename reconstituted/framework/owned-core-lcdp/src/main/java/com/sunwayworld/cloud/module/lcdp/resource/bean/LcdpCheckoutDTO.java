/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.resource.bean;

import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractBaseData;
import java.util.List;

public class LcdpCheckoutDTO
extends AbstractBaseData {
    @Transient
    private static final long serialVersionUID = 752618844753241623L;
    private String categoryCheckoutStatus;
    private String moduleCheckoutStatus;
    private List<Long> moduleIdList;
    private List<Long> categoryIdList;

    public static LcdpCheckoutDTO of(String categoryCheckoutStatus, String moduleCheckoutStatus) {
        LcdpCheckoutDTO checkoutDTO = new LcdpCheckoutDTO();
        checkoutDTO.setCategoryCheckoutStatus(categoryCheckoutStatus);
        checkoutDTO.setModuleCheckoutStatus(moduleCheckoutStatus);
        return checkoutDTO;
    }

    public String getCategoryCheckoutStatus() {
        return this.categoryCheckoutStatus;
    }

    public void setCategoryCheckoutStatus(String categoryCheckoutStatus) {
        this.categoryCheckoutStatus = categoryCheckoutStatus;
    }

    public String getModuleCheckoutStatus() {
        return this.moduleCheckoutStatus;
    }

    public void setModuleCheckoutStatus(String moduleCheckoutStatus) {
        this.moduleCheckoutStatus = moduleCheckoutStatus;
    }

    public List<Long> getModuleIdList() {
        return this.moduleIdList;
    }

    public void setModuleIdList(List<Long> moduleIdList) {
        this.moduleIdList = moduleIdList;
    }

    public List<Long> getCategoryIdList() {
        return this.categoryIdList;
    }

    public void setCategoryIdList(List<Long> categoryIdList) {
        this.categoryIdList = categoryIdList;
    }
}
