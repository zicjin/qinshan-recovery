package com.sunwayworld.emc.module.erp.bean.contract;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/** 样品信息 */
public class MidSampleVOV2 implements Serializable {

    @JsonSerialize(using = ToStringSerializer.class)
    /** 样品gbId */
    private Long gbId;

    /** 样品名称 */
    private String subject;

    /** 样品名称(英文) */
    private String enSubject;

    /** 非标检查项 */
    private String inspectItems;

    /** 备注 */
    private String notes;

    /** 规格型号 */
    private String model;

    /** 批号 */
    private String batchNumber;

    /** 生产厂家 */
    private String producer;

    /** 样品数量 */
    private Integer sampleCnt;

    /** skus */
    private List<MidSkuDetailVoV2> skus;

    /** skuGbId对应sku的量（key是skuGbId , value对应数值   :多少(次)或(小时) */
    private Map<Long, Integer> skuUnits;

    /** skuGbId对应检测项备注（key是skuGbId , value对应值   :备注 */
    private Map<Long, String> skuRemarks;

    /** 非标检测项 */
    private List<MidSampleSubInspectItemsVO> subInspectItems;

    /** 周期 */
    private Integer period;

    /** 样品说明 */
    private String desc;

    /** 危险性", dataType = "com.jiancetech.business.model.enumModel.SampleRisk */
    private List<Integer> risks = Lists.newArrayList();

    /** 危险性备注 */
    private String riskNote;

    /** 样品图片 */
    private String picUrl;

    /** 样品自定义模板数据 */
    private Map<String, String> sampleValue = Maps.newHashMap();

    public Long getGbId() {
        return gbId;
    }

    public void setGbId(Long gbId) {
        this.gbId = gbId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getInspectItems() {
        return inspectItems;
    }

    public void setInspectItems(String inspectItems) {
        this.inspectItems = inspectItems;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getBatchNumber() {
        return batchNumber;
    }

    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
    }

    public String getProducer() {
        return producer;
    }

    public void setProducer(String producer) {
        this.producer = producer;
    }

    public Integer getSampleCnt() {
        return sampleCnt;
    }

    public void setSampleCnt(Integer sampleCnt) {
        this.sampleCnt = sampleCnt;
    }

    public List<MidSkuDetailVoV2> getSkus() {
        return skus;
    }

    public void setSkus(List<MidSkuDetailVoV2> skus) {
        this.skus = skus;
    }

    public Map<Long, Integer> getSkuUnits() {
        return skuUnits;
    }

    public void setSkuUnits(Map<Long, Integer> skuUnits) {
        this.skuUnits = skuUnits;
    }

    public Map<Long, String> getSkuRemarks() {
        return skuRemarks;
    }

    public void setSkuRemarks(Map<Long, String> skuRemarks) {
        this.skuRemarks = skuRemarks;
    }

    public List<MidSampleSubInspectItemsVO> getSubInspectItems() {
        return subInspectItems;
    }

    public void setSubInspectItems(List<MidSampleSubInspectItemsVO> subInspectItems) {
        this.subInspectItems = subInspectItems;
    }

    public Integer getPeriod() {
        return period;
    }

    public void setPeriod(Integer period) {
        this.period = period;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public List<Integer> getRisks() {
        return risks;
    }

    public void setRisks(List<Integer> risks) {
        this.risks = risks;
    }

    public String getRiskNote() {
        return riskNote;
    }

    public void setRiskNote(String riskNote) {
        this.riskNote = riskNote;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public Map<String, String> getSampleValue() {
        return sampleValue;
    }

    public void setSampleValue(Map<String, String> sampleValue) {
        this.sampleValue = sampleValue;
    }

    public String getEnSubject() {
        return enSubject;
    }

    public void setEnSubject(String enSubject) {
        this.enSubject = enSubject;
    }
}
