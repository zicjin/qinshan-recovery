package com.sunwayworld.emc.module.erp.bean.contract;

import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

public class MidSampleDTOExt extends MidSampleDTO {

    /** id */
    private Integer id;

    /** 样品类型编号 当订单类型为送样时，该编号可能为空 */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long samplingTypeNo;

    /** 样品类型名称 */
    private String samplingTypeName;

    /** 点位名称 */
    private String pointName;

    /** 点位个数 */
    private Integer samplingPointCount;

    /** 采样周期 */
    private Integer samplingPeriod;

    /** 采样频次 */
    private Integer samplingFrequency;

    /** 采样样品个数 */
    private Integer samplingNum;

    /** sku基础信息 */
    private List<SaveContractOriSkuInfo> skus;

    /** 保存样品对应的sku列表，包含sku类型，是否需要检测等属性 */
    private List<SaveContractSampleSkuInfo> skuInfoList;

    public List<SaveContractSampleSkuInfo> getSkuInfoList() {
        return skuInfoList;
    }

    public void setSkuInfoList(List<SaveContractSampleSkuInfo> skuInfoList) {
        this.skuInfoList = skuInfoList;
    }

    public List<SaveContractOriSkuInfo> getSkus() {
        return skus;
    }

    public void setSkus(List<SaveContractOriSkuInfo> skus) {
        this.skus = skus;
    }

    public Integer getSamplingNum() {
        return samplingNum;
    }

    public void setSamplingNum(Integer samplingNum) {
        this.samplingNum = samplingNum;
    }

    public Long getSamplingTypeNo() {
        return samplingTypeNo;
    }

    public void setSamplingTypeNo(Long samplingTypeNo) {
        this.samplingTypeNo = samplingTypeNo;
    }

    public String getSamplingTypeName() {
        return samplingTypeName;
    }

    public void setSamplingTypeName(String samplingTypeName) {
        this.samplingTypeName = samplingTypeName;
    }

    public String getPointName() {
        return pointName;
    }

    public void setPointName(String pointName) {
        this.pointName = pointName;
    }

    public Integer getSamplingPointCount() {
        return samplingPointCount;
    }

    public void setSamplingPointCount(Integer samplingPointCount) {
        this.samplingPointCount = samplingPointCount;
    }

    public Integer getSamplingPeriod() {
        return samplingPeriod;
    }

    public void setSamplingPeriod(Integer samplingPeriod) {
        this.samplingPeriod = samplingPeriod;
    }

    public Integer getSamplingFrequency() {
        return samplingFrequency;
    }

    public void setSamplingFrequency(Integer samplingFrequency) {
        this.samplingFrequency = samplingFrequency;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


}
