package com.sunwayworld.emc.module.erp.bean;

import java.io.Serializable;

/**
 * 转接跟单员dto
 */
public class MidTransferMerchandiserDto implements Serializable {

    /** 跟单员编号 */
    private String merchandiserJobNumber;

    public String getMerchandiserJobNumber() {
        return merchandiserJobNumber;
    }

    public void setMerchandiserJobNumber(String merchandiserJobNumber) {
        this.merchandiserJobNumber = merchandiserJobNumber;
    }
}
