package com.sunwayworld.emc.module.erp.bean.contract;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/** 样品Dto */
public class MidSampleDTO implements Serializable {

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

//    /** 判定标准 */
//    private String standard;
//
//    /** 判定标准的id数组 */
//    private List<Long> standardIds = Lists.newArrayList();
//
//    /** 是否判定 */
//    private Boolean bNeedAssert;

    /** 规格型号 */
    private String model;

    /** 批号 */
    private String batchNumber;

    /** 生产厂家 */
    private String producer;

    /** 样品数量 */
    private Integer sampleCnt;

    /** 选中的skuGbId */
    private List<Long> skuGbIds = Lists.newArrayList();

    /** skuGbId对应sku的量（key是skuGbId , value对应数值:多少(次)或(小时) */
    private Map<String, Integer> skuUnits = Maps.newHashMap();

    /** skuGbId对应sku的备注（key是skuGbId , value对应数值:备注 */
    private Map<String, String> skuRemarks = Maps.newHashMap();

    /** skuGbId对应sku的方法标准内容（key是skuGbId , value对应数值   :sku的方法标准内容 */
    private Map<String, String> skuMethodStandards = Maps.newHashMap();

    /** skuGbId对应sku的判定标准内容（key是skuGbId , value对应数值   :sku的判定标准内容 */
    private Map<String, String> skuDetermineStandards = Maps.newHashMap();

    /** skuGbId对应自定义字段值（key是skuGbId , value是structId对应值的map */
    private Map<String, Map<String, String>> skuValue = Maps.newHashMap();

    /** 非标检测项 */
    private List<MidSampleSubInspectItemsDTO> subInspectItems = Lists.newArrayList();

//    /** 仪器sku */
//    private List<MidSampleInstrumentSkuDto> instrumentSkus = Lists.newArrayList();

    /** 危险性", dataType = "com.jiancetech.business.model.enumModel.SampleRisk */
    private List<Integer> risks = Lists.newArrayList();

    /** 危险性备注 */
    private String riskNote;

    /** 样品自定义模板数据 */
    private Map<String, String> sampleValue = Maps.newHashMap();

    /** 额外字段,返回生成的样品和二开样品之间映射关系 */
    private String relateOut;

    /** 前端传递，与报价中订单的uuid一致 */
    private String uuid;

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

//    public List<MidSampleInstrumentSkuDto> getInstrumentSkus() {
//        return instrumentSkus;
//    }
//
//    public void setInstrumentSkus(List<MidSampleInstrumentSkuDto> instrumentSkus) {
//        this.instrumentSkus = instrumentSkus;
//    }

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

    public List<Long> getSkuGbIds() {
        return skuGbIds;
    }

    public void setSkuGbIds(List<Long> skuGbIds) {
        this.skuGbIds = skuGbIds;
    }

    public Map<String, Integer> getSkuUnits() {
        return skuUnits;
    }

    public void setSkuUnits(Map<String, Integer> skuUnits) {
        this.skuUnits = skuUnits;
    }

    public Map<String, String> getSkuRemarks() {
        return skuRemarks;
    }

    public void setSkuRemarks(Map<String, String> skuRemarks) {
        this.skuRemarks = skuRemarks;
    }

    public Map<String, Map<String, String>> getSkuValue() {
        return skuValue;
    }

    public void setSkuValue(Map<String, Map<String, String>> skuValue) {
        this.skuValue = skuValue;
    }

    public List<MidSampleSubInspectItemsDTO> getSubInspectItems() {
        return subInspectItems;
    }

    public void setSubInspectItems(List<MidSampleSubInspectItemsDTO> subInspectItems) {
        this.subInspectItems = subInspectItems;
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

    public Map<String, String> getSampleValue() {
        return sampleValue;
    }

    public void setSampleValue(Map<String, String> sampleValue) {
        this.sampleValue = sampleValue;
    }

    public String getRelateOut() {
        return relateOut;
    }

    public void setRelateOut(String relateOut) {
        this.relateOut = relateOut;
    }

    public Map<String, String> getSkuMethodStandards() {
        return skuMethodStandards;
    }

    public void setSkuMethodStandards(Map<String, String> skuMethodStandards) {
        this.skuMethodStandards = skuMethodStandards;
    }

    public Map<String, String> getSkuDetermineStandards() {
        return skuDetermineStandards;
    }

    public void setSkuDetermineStandards(Map<String, String> skuDetermineStandards) {
        this.skuDetermineStandards = skuDetermineStandards;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getEnSubject() {
        return enSubject;
    }

    public void setEnSubject(String enSubject) {
        this.enSubject = enSubject;
    }
}
