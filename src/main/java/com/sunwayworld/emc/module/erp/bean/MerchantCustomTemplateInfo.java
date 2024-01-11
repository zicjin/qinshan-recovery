package com.sunwayworld.emc.module.erp.bean;

import java.io.Serializable;
import java.util.List;

/**
 * @author zhengxy@sunwayworld.com 2023年10月16日
 */
public class MerchantCustomTemplateInfo implements Serializable {

    private Integer merchantId;
    private String merchantName;
    private List<MerchantCustomTemplateDto> merchantCustomTemplateDtoList;

    public Integer getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Integer merchantId) {
        this.merchantId = merchantId;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public List<MerchantCustomTemplateDto> getMerchantCustomTemplateDtoList() {
        return merchantCustomTemplateDtoList;
    }

    public void setMerchantCustomTemplateDtoList(List<MerchantCustomTemplateDto> merchantCustomTemplateDtoList) {
        this.merchantCustomTemplateDtoList = merchantCustomTemplateDtoList;
    }
}
