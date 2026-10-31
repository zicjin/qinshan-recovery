package com.sunwayworld.emc.module.erp.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.List;

public class MidMerchantCustomTemplateDto extends MidTemplateVO implements Serializable {

    @JsonIgnore
    private Long merchantId;

    /** 中心模板标识 */
    private Long adminTemplateGbId;

    /** 是否为默认模板 */
    private Boolean defaultTemplate = Boolean.FALSE;

    /** pdf模板文本额外属性 */
    private List<MidMerchantCustomAttributesDto> customAttributes;

    /** 产品线gbIds */
    private List<Long> productLineGbIds = Lists.newArrayList();

    /** 子公司gbIds */
    private List<Long> subCompanyGbIds = Lists.newArrayList();

    /** 是否按横版展示合同 */
    private Boolean bPdfPageLandscape;

    /** 语言模板类型 */
    private Integer languageTemplateType;

    public Long getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Long merchantId) {
        this.merchantId = merchantId;
    }

    public Long getAdminTemplateGbId() {
        return adminTemplateGbId;
    }

    public void setAdminTemplateGbId(Long adminTemplateGbId) {
        this.adminTemplateGbId = adminTemplateGbId;
    }

    public Boolean getDefaultTemplate() {
        return defaultTemplate;
    }

    public void setDefaultTemplate(Boolean defaultTemplate) {
        this.defaultTemplate = defaultTemplate;
    }

    public List<MidMerchantCustomAttributesDto> getCustomAttributes() {
        return customAttributes;
    }

    public void setCustomAttributes(List<MidMerchantCustomAttributesDto> customAttributes) {
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

    public Boolean getbPdfPageLandscape() {
        return bPdfPageLandscape;
    }

    public void setbPdfPageLandscape(Boolean bPdfPageLandscape) {
        this.bPdfPageLandscape = bPdfPageLandscape;
    }

    public Integer getLanguageTemplateType() {
        return languageTemplateType;
    }

    public void setLanguageTemplateType(Integer languageTemplateType) {
        this.languageTemplateType = languageTemplateType;
    }
}
