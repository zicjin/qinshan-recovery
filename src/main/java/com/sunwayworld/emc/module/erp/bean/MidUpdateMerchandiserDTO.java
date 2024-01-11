package com.sunwayworld.emc.module.erp.bean;

import java.io.Serializable;

/**
 * @author zhengxy@sunwayworld.com 2023年10月10日
 */
public class MidUpdateMerchandiserDTO implements Serializable {

    private String merchantId;

    /** 操作人唯一标识 */
    private Long operatorGbId;

    private Long gbId;

    private Long merchandiserGbId;

    private String merchandiserJobNumber;

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public Long getOperatorGbId() {
        return operatorGbId;
    }

    public void setOperatorGbId(Long operatorGbId) {
        this.operatorGbId = operatorGbId;
    }

    public Long getGbId() {
        return gbId;
    }

    public void setGbId(Long gbId) {
        this.gbId = gbId;
    }

    public Long getMerchandiserGbId() {
        return merchandiserGbId;
    }

    public void setMerchandiserGbId(Long merchandiserGbId) {
        this.merchandiserGbId = merchandiserGbId;
    }

    public String getMerchandiserJobNumber() {
        return merchandiserJobNumber;
    }

    public void setMerchandiserJobNumber(String merchandiserJobNumber) {
        this.merchandiserJobNumber = merchandiserJobNumber;
    }
}
