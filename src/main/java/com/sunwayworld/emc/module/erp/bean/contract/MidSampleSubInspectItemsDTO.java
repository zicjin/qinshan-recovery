package com.sunwayworld.emc.module.erp.bean.contract;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Map;

import com.google.common.collect.Maps;

public class MidSampleSubInspectItemsDTO implements Serializable {
    /** 非标检测项gbId */
    private Long gbId;

    /** 项目名 */
    @NotBlank
    private String inspectItem;

    /** 备注 */
    private String remark;

    /** 方法标准 */
    private String skuStandardMethodContent;

    /** 判定标准 */
    private String skuStandardDetermineContent;

    /** 非标检测项自定义模板数据: key:自定义字段的id, value:自定义字段的值 */
    private Map<Long, String> inspectItemValue = Maps.newHashMap();

    /** 实验室标识 */
    private Long laboratoryGbId;

    /** 实验室名称 */
    private String laboratoryName;

    /** 额外字段,二开唯一标识 */
    private String relateOut;

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

    public String getRelateOut() {
        return relateOut;
    }

    public void setRelateOut(String relateOut) {
        this.relateOut = relateOut;
    }

    public Long getLaboratoryGbId() {
        return laboratoryGbId;
    }

    public MidSampleSubInspectItemsDTO setLaboratoryGbId(Long laboratoryGbId) {
        this.laboratoryGbId = laboratoryGbId;
        return this;
    }

    public String getLaboratoryName() {
        return laboratoryName;
    }

    public MidSampleSubInspectItemsDTO setLaboratoryName(String laboratoryName) {
        this.laboratoryName = laboratoryName;
        return this;
    }
}
