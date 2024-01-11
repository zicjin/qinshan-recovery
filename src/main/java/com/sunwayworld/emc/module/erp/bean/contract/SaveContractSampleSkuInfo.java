package com.sunwayworld.emc.module.erp.bean.contract;

import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

public class SaveContractSampleSkuInfo {

    /** sku gbId */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long skuGbId;

    /** sku名称 */
    private String skuName;

    /** sku 类型 */
    private Integer skuType;

    /** 采样类型 */
    private String skuSamplingType;

    /** 采样类型名称 */
    private String skuSamplingTypeName;

    /** 是否需要检测 */
    private Boolean needDetected;

    /** 检测方法标准 */
    private String skuStandardMethodContent;

    /** 若为套餐，则下属单项的sku信息 */
    private List<SaveContractSampleSkuInfo> singles;

    public Long getSkuGbId() {
        return skuGbId;
    }

    public void setSkuGbId(Long skuGbId) {
        this.skuGbId = skuGbId;
    }

    public Integer getSkuType() {
        return skuType;
    }

    public void setSkuType(Integer skuType) {
        this.skuType = skuType;
    }

    public String getSkuSamplingType() {
        return skuSamplingType;
    }

    public void setSkuSamplingType(String skuSamplingType) {
        this.skuSamplingType = skuSamplingType;
    }

    public Boolean getNeedDetected() {
        return needDetected;
    }

    public void setNeedDetected(Boolean needDetected) {
        this.needDetected = needDetected;
    }

    public List<SaveContractSampleSkuInfo> getSingles() {
        return singles;
    }

    public void setSingles(List<SaveContractSampleSkuInfo> singles) {
        this.singles = singles;
    }

    public String getSkuStandardMethodContent() {
        return skuStandardMethodContent;
    }

    public void setSkuStandardMethodContent(String skuStandardMethodContent) {
        this.skuStandardMethodContent = skuStandardMethodContent;
    }

    public String getSkuName() {
        return skuName;
    }

    public void setSkuName(String skuName) {
        this.skuName = skuName;
    }

    public String getSkuSamplingTypeName() {
        return skuSamplingTypeName;
    }

    public void setSkuSamplingTypeName(String skuSamplingTypeName) {
        this.skuSamplingTypeName = skuSamplingTypeName;
    }

}
