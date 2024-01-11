package com.sunwayworld.emc.module.erp.bean;

import java.io.Serializable;
import java.util.List;

public class MidMerchantCustomTemplateVO implements Serializable {

    /** 商户Id */
    private Long merchantId;

    /** 商户名字 */
    private String merchantName;

    /** 商户下的模板 */
    private List<MidMerchantCustomTemplateDto> merchantCustomTemplateDtoList;

    public Long getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Long merchantId) {
        this.merchantId = merchantId;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public List<MidMerchantCustomTemplateDto> getMerchantCustomTemplateDtoList() {
        return merchantCustomTemplateDtoList;
    }

    public void setMerchantCustomTemplateDtoList(List<MidMerchantCustomTemplateDto> merchantCustomTemplateDtoList) {
        this.merchantCustomTemplateDtoList = merchantCustomTemplateDtoList;
    }
}
