package com.sunwayworld.emc.module.erp.bean;

import java.io.Serializable;
import java.util.List;

/**
 * @author zhengxy@sunwayworld.com 2023年10月16日
 */
public class MerchantCustomTemplateDto implements Serializable {

    private boolean defaultTemplate;
    private String gbId;
    private String name;
    private boolean bPdfPageLandscape;
    private List<String> customAttributes;
    private List<Long> productLineGbIds;
    private List<Long> subCompanyGbIds;

    public boolean isDefaultTemplate() {
        return defaultTemplate;
    }

    public void setDefaultTemplate(boolean defaultTemplate) {
        this.defaultTemplate = defaultTemplate;
    }

    public String getGbId() {
        return gbId;
    }

    public void setGbId(String gbId) {
        this.gbId = gbId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isBPdfPageLandscape() {
        return bPdfPageLandscape;
    }

    public void setBPdfPageLandscape(boolean bPdfPageLandscape) {
        this.bPdfPageLandscape = bPdfPageLandscape;
    }

    public List<?> getCustomAttributes() {
        return customAttributes;
    }

    public void setCustomAttributes(List<String> customAttributes) {
        this.customAttributes = customAttributes;
    }

    public List<Long> getProductLineGbIds() {
        return productLineGbIds;
    }

    public void setProductLineGbIds(List<Long> productLineGbIds) {
        this.productLineGbIds = productLineGbIds;
    }

    public List<Long> getSubCompanyGbIds() {
        return subCompanyGbIds;
    }

    public void setSubCompanyGbIds(List<Long> subCompanyGbIds) {
        this.subCompanyGbIds = subCompanyGbIds;
    }
}
