package com.sunwayworld.emc.module.erp.bean;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class MidSampleDetailVO implements Serializable {
    /** gbId */
    private Long gbId;

    /** 样品名称 */
    private String subject;

    /** 英文样品名称 */
    private String enSubject;

    /** 样品数量 */
    private Integer cnt;

    /** 样品危险性 */
    private List<String> sampleRisks;

    /** 是否判定 */
    private Boolean bNeedAssert;

    /** 判定标准 */
    private List<String> standards;

    /** 批号 */
    private String batchNumber;

    /** 生产厂家 */
    private String producer;

    /** 备注 */
    private String note;

    /** 非标检测项 */
    private String inspectItems;

    /** 规格型号 */
    private String model;

    /** skuGbId对应检测项备注（key是skuGbId , value对应值   :备注 */
    private Map<Long, String> skuRemarks;

    /** 非标检测项 */
    private List<MidSampleSubInspectItemsDTO> subInspectItems = Lists.newArrayList();

    /** skus */
    private List<MidSkuDetailVo> skus = Lists.newArrayList();

    /** 实体样品 */
    private List<MidExpressSampleVO> expressSamples;

    /** 技术指导价 */
    private Long techPrice;

    /** PO支付成本 */
    private Long poPrice;

    /** 倍率 */
    private Long magnification;

    /** 样品自定义模板数据 */
    private Map<Long, String> sampleValue = Maps.newHashMap();

    public List<String> getSampleRisks() {
        return sampleRisks;
    }

    public void setSampleRisks(List<String> sampleRisks) {
        this.sampleRisks = sampleRisks;
    }

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

    public Integer getCnt() {
        return cnt;
    }

    public void setCnt(Integer cnt) {
        this.cnt = cnt;
    }

    public Boolean getbNeedAssert() {
        return bNeedAssert;
    }

    public void setbNeedAssert(Boolean bNeedAssert) {
        this.bNeedAssert = bNeedAssert;
    }

    public List<String> getStandards() {
        return standards;
    }

    public void setStandards(List<String> standards) {
        this.standards = standards;
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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getInspectItems() {
        return inspectItems;
    }

    public void setInspectItems(String inspectItems) {
        this.inspectItems = inspectItems;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Map<Long, String> getSkuRemarks() {
        return skuRemarks;
    }

    public void setSkuRemarks(Map<Long, String> skuRemarks) {
        this.skuRemarks = skuRemarks;
    }

    public List<MidSampleSubInspectItemsDTO> getSubInspectItems() {
        return subInspectItems;
    }

    public void setSubInspectItems(List<MidSampleSubInspectItemsDTO> subInspectItems) {
        this.subInspectItems = subInspectItems;
    }

    public List<MidSkuDetailVo> getSkus() {
        return skus;
    }

    public void setSkus(List<MidSkuDetailVo> skus) {
        this.skus = skus;
    }

    public List<MidExpressSampleVO> getExpressSamples() {
        return expressSamples;
    }

    public void setExpressSamples(List<MidExpressSampleVO> expressSamples) {
        this.expressSamples = expressSamples;
    }

    public Long getTechPrice() {
        return techPrice;
    }

    public void setTechPrice(Long techPrice) {
        this.techPrice = techPrice;
    }

    public Long getPoPrice() {
        return poPrice;
    }

    public void setPoPrice(Long poPrice) {
        this.poPrice = poPrice;
    }

    public Long getMagnification() {
        return magnification;
    }

    public void setMagnification(Long magnification) {
        this.magnification = magnification;
    }

    public Map<Long, String> getSampleValue() {
        return sampleValue;
    }

    public void setSampleValue(Map<Long, String> sampleValue) {
        this.sampleValue = sampleValue;
    }

    public String getEnSubject() {
        return enSubject;
    }

    public void setEnSubject(String enSubject) {
        this.enSubject = enSubject;
    }
}
