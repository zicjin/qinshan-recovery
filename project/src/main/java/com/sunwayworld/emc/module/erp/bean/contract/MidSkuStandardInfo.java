package com.sunwayworld.emc.module.erp.bean.contract;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

public class MidSkuStandardInfo {

    /** 标准id */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long standardId;

    /** 标准名 */
    private String standardName;

    /** 标准号 */
    private String standardNum;

    /** 标准类型 */
    private Integer standardType;

    /** 判定类型列表 */
    //private List<MidSkuStandardDetermineInfo> skuStandardDetermines;

    public Long getStandardId() {
        return standardId;
    }

    public void setStandardId(Long standardId) {
        this.standardId = standardId;
    }

    public String getStandardName() {
        return standardName;
    }

    public void setStandardName(String standardName) {
        this.standardName = standardName;
    }

    public String getStandardNum() {
        return standardNum;
    }

    public void setStandardNum(String standardNum) {
        this.standardNum = standardNum;
    }

    public Integer getStandardType() {
        return standardType;
    }

    public void setStandardType(Integer standardType) {
        this.standardType = standardType;
    }

    @Override
    public String toString() {
        return "MidSkuStandardInfo{" +
                "standardId=" + standardId +
                ", standardName='" + standardName + '\'' +
                ", standardNum='" + standardNum + '\'' +
                ", standardType=" + standardType +
                '}';
    }
}
