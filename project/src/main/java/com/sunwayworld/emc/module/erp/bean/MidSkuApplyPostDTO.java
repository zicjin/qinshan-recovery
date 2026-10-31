package com.sunwayworld.emc.module.erp.bean;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.hibernate.validator.constraints.Length;

/**
 * sku报价申请时的skuDTO
 */
public class MidSkuApplyPostDTO implements Serializable {
    /** gbId */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long gbId;

    /** 产品线gbId */
    private Long productLineGbId;

    /** 行业群GbId */
    private Long industryGbId;

    /** 业务大类 */
    private Long bCategory;

    /** 产品大类 */
    private Long pCategory;

    /** 产品大类Code */
    private List<Long> pCategoryCodes;

    private String merchantId;

    /** 名称 */
    private String name;

    /** 别名 */
    private String aliasName;

    /** 标准id */
    private Long standardId;

    /** 标准号 */
    @NotBlank(message = "standardNumber err")
    private String standardNumber;

    /** 标准名称 */
    @NotBlank(message = "standardName err")
    private String standardName;

    /** 标准类型 */
    private Integer standardType;

    /** sku标准 */
    private SkuStandardDetermineDto skuStandardDetermine;

    /** sku类型，0-单项，1-套餐 */
    private Integer skuType;

    /** 套餐用途 */
    private Long skuPurposeId;

    /** 备注 */
    private String notes;

    /** 创建时间 */
    private Long tWhen;

    /** 如果是套餐，包含的单项sku gbId */
    private List<Long> singleGbIds;

    /** 计价方式 */
    private Integer quoteUnit;

    /** 如果是套餐，包含的单项sku */
    private List<MidSkuApplyPostDTO> singleSkuList;

    /** 章节 */
    private String chapter;

    /** 检测对象 */
    private String detectionObject;

    /** 样品保存期 */
    private String  sampleRetentionPeriod;

    /** 项目说明 */
    @Length(max = 256)
    private String itemDesc;

    /** 风险点 */
    @Length(max = 256)
    private String risk;

    /** 是否需要氰化物验证 */
    @NotNull(message = "是否需要氰化物验证不能为空")
    private Boolean bCyanideAssess;

    private String limsId;

    /**
     * sku报价申请时判定标准DTO
     */
    public class SkuStandardDetermineDto implements Serializable {
        /**
         * 标准id
         */
        private Long standardId;

        /**
         * 判定类型
         */
        private List<String> standardDetermineName;

        public Long getStandardId() {
            return standardId;
        }

        public void setStandardId(Long standardId) {
            this.standardId = standardId;
        }

        public List<String> getStandardDetermineName() {
            return standardDetermineName;
        }

        public void setStandardDetermineName(List<String> standardDetermineName) {
            this.standardDetermineName = standardDetermineName;
        }
    }

    public Long getGbId() {
        return gbId;
    }

    public void setGbId(Long gbId)
    {
        this.gbId = gbId;
    }

    public Long getProductLineGbId()
    {
        return productLineGbId;
    }

    public void setProductLineGbId(Long productLineGbId)
    {
        this.productLineGbId = productLineGbId;
    }

    public Long getIndustryGbId()
    {
        return industryGbId;
    }

    public void setIndustryGbId(Long industryGbId)
    {
        this.industryGbId = industryGbId;
    }

    public Long getbCategory()
    {
        return bCategory;
    }

    public void setbCategory(Long bCategory)
    {
        this.bCategory = bCategory;
    }

    public Long getpCategory()
    {
        return pCategory;
    }

    public void setpCategory(Long pCategory)
    {
        this.pCategory = pCategory;
    }

    public List<Long> getpCategoryCodes() {
        return pCategoryCodes;
    }

    public void setpCategoryCodes(List<Long> pCategoryCodes) {
        this.pCategoryCodes = pCategoryCodes;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getAliasName()
    {
        return aliasName;
    }

    public void setAliasName(String aliasName)
    {
        this.aliasName = aliasName;
    }

    public Long getStandardId()
    {
        return standardId;
    }

    public void setStandardId(Long standardId)
    {
        this.standardId = standardId;
    }

    public String getStandardNumber()
    {
        return standardNumber;
    }

    public void setStandardNumber(String standardNumber)
    {
        this.standardNumber = standardNumber;
    }

    public String getStandardName()
    {
        return standardName;
    }

    public void setStandardName(String standardName)
    {
        this.standardName = standardName;
    }

    public Integer getStandardType()
    {
        return standardType;
    }

    public void setStandardType(Integer standardType)
    {
        this.standardType = standardType;
    }

    public SkuStandardDetermineDto getSkuStandardDetermine()
    {
        return skuStandardDetermine;
    }

    public void setSkuStandardDetermine(SkuStandardDetermineDto skuStandardDetermine)
    {
        this.skuStandardDetermine = skuStandardDetermine;
    }

    public Integer getSkuType()
    {
        return skuType;
    }

    public void setSkuType(Integer skuType)
    {
        this.skuType = skuType;
    }

    public Long getSkuPurposeId()
    {
        return skuPurposeId;
    }

    public void setSkuPurposeId(Long skuPurposeId)
    {
        this.skuPurposeId = skuPurposeId;
    }

    public String getNotes()
    {
        return notes;
    }

    public void setNotes(String notes)
    {
        this.notes = notes;
    }

    public Long gettWhen()
    {
        return tWhen;
    }

    public void settWhen(Long tWhen)
    {
        this.tWhen = tWhen;
    }

    public List<Long> getSingleGbIds()
    {
        return singleGbIds;
    }

    public void setSingleGbIds(List<Long> singleGbIds)
    {
        this.singleGbIds = singleGbIds;
    }

    public Integer getQuoteUnit()
    {
        return quoteUnit;
    }

    public void setQuoteUnit(Integer quoteUnit)
    {
        this.quoteUnit = quoteUnit;
    }

    public List<MidSkuApplyPostDTO> getSingleSkuList()
    {
        return singleSkuList;
    }

    public void setSingleSkuList(List<MidSkuApplyPostDTO> singleSkuList)
    {
        this.singleSkuList = singleSkuList;
    }

    public String getChapter()
    {
        return chapter;
    }

    public void setChapter(String chapter)
    {
        this.chapter = chapter;
    }

    public String getDetectionObject()
    {
        return detectionObject;
    }

    public void setDetectionObject(String detectionObject)
    {
        this.detectionObject = detectionObject;
    }

    public String getSampleRetentionPeriod()
    {
        return sampleRetentionPeriod;
    }

    public void setSampleRetentionPeriod(String sampleRetentionPeriod)
    {
        this.sampleRetentionPeriod = sampleRetentionPeriod;
    }

    public String getItemDesc()
    {
        return itemDesc;
    }

    public void setItemDesc(String itemDesc)
    {
        this.itemDesc = itemDesc;
    }

    public String getRisk()
    {
        return risk;
    }

    public void setRisk(String risk)
    {
        this.risk = risk;
    }

    public Boolean getbCyanideAssess()
    {
        return bCyanideAssess;
    }

    public void setbCyanideAssess(Boolean bCyanideAssess)
    {
        this.bCyanideAssess = bCyanideAssess;
    }

    public String getLimsId() {
        return limsId;
    }

    public void setLimsId(String limsId) {
        this.limsId = limsId;
    }
}
