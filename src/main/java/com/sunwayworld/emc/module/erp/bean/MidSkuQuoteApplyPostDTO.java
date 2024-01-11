package com.sunwayworld.emc.module.erp.bean;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;

import org.hibernate.validator.constraints.Length;

/**
 * 环境LIMS的sku报价申请DTO
 */
public class MidSkuQuoteApplyPostDTO implements Serializable
{
    private String merchantId;

    /** sku */
    private MidSkuApplyPostDTO sku;

    /** 资质：0-无资质，1-CMA，2-CNAS，3-CMA+CNAS，4-CAL */
    private Integer qualification;

    /** 样品需求描述 */
    @NotBlank(message = "样品需求描述 err")
    private String sample;

    /** 接单须知 */
    private String quoteNote;

    /** 分包项目名称,跟sku的项目名称一样就好了 */
    private String subQuoteName;

    /** 报价员 */
    private Long saleGbId;

    /** 报价员-集合 */
    private List<Long> saleGbIds;

    /** sku报价区分自有和分包字段 */
    private Integer quoteProviderType;

    /** 周期 */
    private Integer period;
    /** 技术指导价 */
    private Long price;
    /** 销售指导价 */
    private Long orgFixPrice;

    /** 加急周期 */
    private Integer urgentPeriod;

    /** 加急技术指导价 */
    private Long urgentPrice;
    /** 加急销售指导价 */
    private Long urgentOrgFixPrice;

    /** 特急周期 */
    private Integer extraUrgentPeriod;

    /** 特急技术指导价 */
    private Long extraUrgentPrice;

    /** 特急销售指导价 */
    private Long extraUrgentOrgFixPrice;

    /** 实验室编号 */
    private Long laboratoryGbId;

    /** 检测对象 */
    private String detectionObject;

    /** 报告模板 */
    @Length(max = 256, message = "报告模板文件名称过长")
    private String reportMode;

    /** 样品保存期 */
    private String  sampleRetentionPeriod;

    /** LIMS报价ID */
    private String limsQuoteId;

    public MidSkuApplyPostDTO getSku()
    {
        return sku;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public void setSku(MidSkuApplyPostDTO sku)
    {
        this.sku = sku;
    }

    public Integer getQualification()
    {
        return qualification;
    }

    public void setQualification(Integer qualification)
    {
        this.qualification = qualification;
    }

    public String getSample()
    {
        return sample;
    }

    public void setSample(String sample)
    {
        this.sample = sample;
    }

    public String getQuoteNote()
    {
        return quoteNote;
    }

    public void setQuoteNote(String quoteNote)
    {
        this.quoteNote = quoteNote;
    }

    public String getSubQuoteName()
    {
        return subQuoteName;
    }

    public void setSubQuoteName(String subQuoteName)
    {
        this.subQuoteName = subQuoteName;
    }

    public Long getSaleGbId()
    {
        return saleGbId;
    }

    public void setSaleGbId(Long saleGbId)
    {
        this.saleGbId = saleGbId;
    }

    public List<Long> getSaleGbIds()
    {
        return saleGbIds;
    }

    public void setSaleGbIds(List<Long> saleGbIds)
    {
        this.saleGbIds = saleGbIds;
    }

    public Integer getQuoteProviderType()
    {
        return quoteProviderType;
    }

    public void setQuoteProviderType(Integer quoteProviderType)
    {
        this.quoteProviderType = quoteProviderType;
    }

    public Integer getPeriod()
    {
        return period;
    }

    public void setPeriod(Integer period)
    {
        this.period = period;
    }

    public Long getPrice()
    {
        return price;
    }

    public void setPrice(Long price)
    {
        this.price = price;
    }

    public Long getOrgFixPrice()
    {
        return orgFixPrice;
    }

    public void setOrgFixPrice(Long orgFixPrice)
    {
        this.orgFixPrice = orgFixPrice;
    }

    public Integer getUrgentPeriod()
    {
        return urgentPeriod;
    }

    public void setUrgentPeriod(Integer urgentPeriod)
    {
        this.urgentPeriod = urgentPeriod;
    }

    public Long getUrgentPrice()
    {
        return urgentPrice;
    }

    public void setUrgentPrice(Long urgentPrice)
    {
        this.urgentPrice = urgentPrice;
    }

    public Long getUrgentOrgFixPrice()
    {
        return urgentOrgFixPrice;
    }

    public void setUrgentOrgFixPrice(Long urgentOrgFixPrice)
    {
        this.urgentOrgFixPrice = urgentOrgFixPrice;
    }

    public Integer getExtraUrgentPeriod()
    {
        return extraUrgentPeriod;
    }

    public void setExtraUrgentPeriod(Integer extraUrgentPeriod)
    {
        this.extraUrgentPeriod = extraUrgentPeriod;
    }

    public Long getExtraUrgentPrice()
    {
        return extraUrgentPrice;
    }

    public void setExtraUrgentPrice(Long extraUrgentPrice)
    {
        this.extraUrgentPrice = extraUrgentPrice;
    }

    public Long getExtraUrgentOrgFixPrice()
    {
        return extraUrgentOrgFixPrice;
    }

    public void setExtraUrgentOrgFixPrice(Long extraUrgentOrgFixPrice)
    {
        this.extraUrgentOrgFixPrice = extraUrgentOrgFixPrice;
    }

    public Long getLaboratoryGbId()
    {
        return laboratoryGbId;
    }

    public void setLaboratoryGbId(Long laboratoryGbId)
    {
        this.laboratoryGbId = laboratoryGbId;
    }

    public String getDetectionObject()
    {
        return detectionObject;
    }

    public void setDetectionObject(String detectionObject)
    {
        this.detectionObject = detectionObject;
    }

    public String getReportMode()
    {
        return reportMode;
    }

    public void setReportMode(String reportMode)
    {
        this.reportMode = reportMode;
    }

    public String getSampleRetentionPeriod()
    {
        return sampleRetentionPeriod;
    }

    public void setSampleRetentionPeriod(String sampleRetentionPeriod)
    {
        this.sampleRetentionPeriod = sampleRetentionPeriod;
    }

    public String getLimsQuoteId() {
        return limsQuoteId;
    }

    public void setLimsQuoteId(String limsQuoteId) {
        this.limsQuoteId = limsQuoteId;
    }
}
