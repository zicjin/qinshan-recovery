package com.sunwayworld.emc.module.erp.bean.contract;

import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

public class SaveContractOriSkuInfo {

    /** sku名称 */
    private String name;

    /** gbId */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long gbId;

    /** 检测方法 */
    private String skuStandardMethodContent;

    /** 判定标准 */
    private String skuStandardDetermineContent;

    /** 下属的单个sku */
    private List<SaveContractOriSkuInfo> singles;

    /** 判定标准对象 */
    private MidSkuStandardInfo skuStandard;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getGbId() {
        return gbId;
    }

    public void setGbId(Long gbId) {
        this.gbId = gbId;
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

    public List<SaveContractOriSkuInfo> getSingles() {
        return singles;
    }

    public void setSingles(List<SaveContractOriSkuInfo> singles) {
        this.singles = singles;
    }

    public MidSkuStandardInfo getSkuStandard() {
        return skuStandard;
    }

    public void setSkuStandard(MidSkuStandardInfo skuStandard) {
        this.skuStandard = skuStandard;
    }

    @Override
    public String toString() {
        return "SaveContractOriSkuInfo{" +
                "name='" + name + '\'' +
                ", gbId=" + gbId +
                ", skuStandardMethodContent='" + skuStandardMethodContent + '\'' +
                ", skuStandardDetermineContent='" + skuStandardDetermineContent + '\'' +
                ", singles=" + singles +
                ", skuStandard=" + skuStandard +
                '}';
    }
}
