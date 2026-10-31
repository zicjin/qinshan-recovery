package com.sunwayworld.emc.module.erp.bean;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author zhengxy@sunwayworld.com 2023年10月16日
 */
public class OrderChangeSampleInfo implements Serializable {

    private String gbId;
    private Integer sampleCnt;
    private Map<String, String> sampleValue;
    private String subject;
    private String enSubject;
    private String samplingTypeNo;
    private String samplingTypeName;
    private String pointName;
    private Integer samplingPointCount;
    private Integer samplingPeriod;
    private Integer samplingFrequency;
    private Integer samplingNum;
    private List<MidSkuDetailVo> skus;
    private List<String> subInspectItems;

    public String getGbId() {
        return gbId;
    }

    public void setGbId(String gbId) {
        this.gbId = gbId;
    }

    public Map<String, String> getSampleValue() {
        return sampleValue;
    }

    public void setSampleValue(Map<String, String> sampleValue) {
        this.sampleValue = sampleValue;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getEnSubject() {
        return enSubject;
    }

    public void setEnSubject(String enSubject) {
        this.enSubject = enSubject;
    }

    public String getSamplingTypeNo() {
        return samplingTypeNo;
    }

    public void setSamplingTypeNo(String samplingTypeNo) {
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

    public Integer getSampleCnt() {
        return sampleCnt;
    }

    public void setSampleCnt(Integer sampleCnt) {
        this.sampleCnt = sampleCnt;
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

    public Integer getSamplingNum() {
        return samplingNum;
    }

    public void setSamplingNum(Integer samplingNum) {
        this.samplingNum = samplingNum;
    }

    public List<MidSkuDetailVo> getSkus() {
        return skus;
    }

    public void setSkus(List<MidSkuDetailVo> skus) {
        this.skus = skus;
    }

    public List<String> getSubInspectItems() {
        return subInspectItems;
    }

    public void setSubInspectItems(List<String> subInspectItems) {
        this.subInspectItems = subInspectItems;
    }
}
