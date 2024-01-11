package com.sunwayworld.emc.module.erp.bean.contract;

import java.io.Serializable;
import java.util.Map;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.google.common.collect.Maps;

/** 非标检测项详情 */
public class MidSampleSubInspectItemsVO implements Serializable {

    @JsonSerialize(using = ToStringSerializer.class)
    /** 非标检测项gbId */
    private Long gbId;

    /** 项目名 */
    private String inspectItem;

    /** 备注 */
    private String remark;

    /** 标准编号 */
    private String standardNumber;

    /** 标准名 */
    private String standardName;

    /** 方法标准 */
    private String skuStandardMethodContent;

    /** 判定标准 */
    private String skuStandardDetermineContent;

    /** 周期 */
    private Integer period;

    /** 非标检测项自定义模板数据: key:自定义字段的id, value:自定义字段的值 */
    private Map<Long, String> inspectItemValue = Maps.newHashMap();

    public String getInspectItem() {
        return inspectItem;
    }

    public void setInspectItem(String inspectItem) {
        this.inspectItem = inspectItem;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getStandardNumber() {
        return standardNumber;
    }

    public void setStandardNumber(String standardNumber) {
        this.standardNumber = standardNumber;
    }

    public String getStandardName() {
        return standardName;
    }

    public void setStandardName(String standardName) {
        this.standardName = standardName;
    }

    public Long getGbId() {
        return gbId;
    }

    public void setGbId(Long gbId) {
        this.gbId = gbId;
    }

    public Map<Long, String> getInspectItemValue() {
        return inspectItemValue;
    }

    public void setInspectItemValue(Map<Long, String> inspectItemValue) {
        this.inspectItemValue = inspectItemValue;
    }

    public Integer getPeriod() {
        return period;
    }

    public void setPeriod(Integer period) {
        this.period = period;
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
}
