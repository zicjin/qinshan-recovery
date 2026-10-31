package com.sunwayworld.emc.module.erp.bean.contract;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.google.common.collect.Maps;

/** sku详情 */
public class MidSkuDetailVoV2 implements Serializable {

    @JsonSerialize(using = ToStringSerializer.class)
    /** skuGbId */
    private Long gbId;

    /** 套餐名称 */
    private String name;

    /** sku类型", dataType = "com.jiancetech.business.model.enumModel.SkuType */
    private Integer skuType;

    /** sku标准 */
    private MidSkuStandardVOV2 skuStandard;

    /** 方法标准 */
    private String skuStandardMethodContent;

    /** 判定标准 */
    private String skuStandardDetermineContent;

    /** 计价方式", dataType = "com.jiancetech.business.model.enumModel.SkuQuoteUnit */
    private Integer quoteUnit;

    @JsonSerialize(using = ToStringSerializer.class)
    /** 产品大类 */
    private Long pCategory;

    /** 产品大类名 */
    private String pCategoryName;

    @JsonSerialize(using = ToStringSerializer.class)
    /** 业务大类 */
    private Long bCategory;

    /** 业务大类名 */
    private String bCategoryName;

    /** 单项:当skuType为套餐时, singles字段会展示该套餐下的所有的单项 */
    private List<String> singles;

    /** 套餐名称 */
    private String skuPurposeName;

    @JsonSerialize(using = ToStringSerializer.class)
    /** 套餐id */
    private Long skuPurposeId;

    /** 是否为自有报价 */
    private Boolean ownQuotation;

    /** sku备注 */
    private String notes;

    /** 周期 */
    private Integer period;

    /** 是否为联合报价 */
    private Boolean uniteQuotation;

    /** 检测项备注 */
    private String remark = "";

    /** sku自定义模板数据 */
    private Map<Long, String> skuValue = Maps.newHashMap();

    public Long getGbId() {
        return gbId;
    }

    public void setGbId(Long gbId) {
        this.gbId = gbId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSkuType() {
        return skuType;
    }

    public void setSkuType(Integer skuType) {
        this.skuType = skuType;
    }

    public Integer getQuoteUnit() {
        return quoteUnit;
    }

    public void setQuoteUnit(Integer quoteUnit) {
        this.quoteUnit = quoteUnit;
    }

    public Long getpCategory() {
        return pCategory;
    }

    public void setpCategory(Long pCategory) {
        this.pCategory = pCategory;
    }

    public String getpCategoryName() {
        return pCategoryName;
    }

    public void setpCategoryName(String pCategoryName) {
        this.pCategoryName = pCategoryName;
    }

    public Long getbCategory() {
        return bCategory;
    }

    public void setbCategory(Long bCategory) {
        this.bCategory = bCategory;
    }

    public String getbCategoryName() {
        return bCategoryName;
    }

    public void setbCategoryName(String bCategoryName) {
        this.bCategoryName = bCategoryName;
    }

    public List<String> getSingles() {
        return singles;
    }

    public void setSingles(List<String> singles) {
        this.singles = singles;
    }

    public String getSkuPurposeName() {
        return skuPurposeName;
    }

    public void setSkuPurposeName(String skuPurposeName) {
        this.skuPurposeName = skuPurposeName;
    }

    public Long getSkuPurposeId() {
        return skuPurposeId;
    }

    public void setSkuPurposeId(Long skuPurposeId) {
        this.skuPurposeId = skuPurposeId;
    }

    public Boolean getOwnQuotation() {
        return ownQuotation;
    }

    public void setOwnQuotation(Boolean ownQuotation) {
        this.ownQuotation = ownQuotation;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Integer getPeriod() {
        return period;
    }

    public void setPeriod(Integer period) {
        this.period = period;
    }

    public Boolean getUniteQuotation() {
        return uniteQuotation;
    }

    public void setUniteQuotation(Boolean uniteQuotation) {
        this.uniteQuotation = uniteQuotation;
    }

    public Map<Long, String> getSkuValue() {
        return skuValue;
    }

    public void setSkuValue(Map<Long, String> skuValue) {
        this.skuValue = skuValue;
    }

    public MidSkuStandardVOV2 getSkuStandard() {
        return skuStandard;
    }

    public void setSkuStandard(MidSkuStandardVOV2 skuStandard) {
        this.skuStandard = skuStandard;
    }

    public String getSkuStandardMethodContent() {
        return skuStandardMethodContent;
    }

    public void setSkuStandardMethodContent(String skuStandardMethodContent) {
        this.skuStandardMethodContent = skuStandardMethodContent;
    }

    public String getSkuStandardDetermineContent() {
        return skuStandardDetermineContent;
    }

    public void setSkuStandardDetermineContent(String skuStandardDetermineContent) {
        this.skuStandardDetermineContent = skuStandardDetermineContent;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public static class MidSkuStandardVOV2 implements Serializable {

        @JsonSerialize(using = ToStringSerializer.class)
        /** 标准id */
        private Long standardId;

        /** 标准号 */
        private String standardNum;

        /** 标准名 */
        private String standardName;

        /** 标准类别", dataType = "com.jiancetech.business.model.enumModel.SkuStandardType */
        private Integer standardType;

        /** 判定类型 */
        private List<MidSkuStandardDetermineVOV2> skuStandardDetermines;

        public Long getStandardId() {
            return standardId;
        }

        public void setStandardId(Long standardId) {
            this.standardId = standardId;
        }

        public String getStandardNum() {
            return standardNum;
        }

        public void setStandardNum(String standardNum) {
            this.standardNum = standardNum;
        }

        public String getStandardName() {
            return standardName;
        }

        public void setStandardName(String standardName) {
            this.standardName = standardName;
        }

        public Integer getStandardType() {
            return standardType;
        }

        public void setStandardType(Integer standardType) {
            this.standardType = standardType;
        }

        public List<MidSkuStandardDetermineVOV2> getSkuStandardDetermines() {
            return skuStandardDetermines;
        }

        public void setSkuStandardDetermines(List<MidSkuStandardDetermineVOV2> skuStandardDetermines) {
            this.skuStandardDetermines = skuStandardDetermines;
        }
    }

    public static class MidSkuStandardDetermineVOV2 implements Serializable {

        @JsonSerialize(using = ToStringSerializer.class)
        /** id */
        private Long id;

        /** 判定类型 */
        private String name;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }


    }
}
