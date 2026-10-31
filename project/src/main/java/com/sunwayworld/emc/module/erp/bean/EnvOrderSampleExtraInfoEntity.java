package com.sunwayworld.emc.module.erp.bean;

import java.io.Serializable;

/**
 * @author zhengxy@sunwayworld.com 2023年10月27日
 */
public class EnvOrderSampleExtraInfoEntity implements Serializable {

    /**
     * @Fields id : id
     */
    private Integer id;
    /**
     * @Fields orderGbId : 订单一开唯一标识
     */
    private Long orderGbId;
    /**
     * @Fields sampleGbId : 样品一开唯一标识
     */
    private Long sampleGbId;
    /**
     * @Fields relateOut : 一开与二开样品关联字段
     */
    private Long relateOut;
    /**
     * @Fields samplingTypeNo : 关联的样品类型编号，送样类型订单可以为空
     */
    private Long samplingTypeNo;
    /**
     * @Fields samplingTypeName : 关联的样品类型名称，送样订单类型可以手动输入
     */
    private String samplingTypeName;
    /**
     * @Fields pointName : 点位名称
     */
    private String pointName;
    /**
     * @Fields samplingPointCount : 点位个数
     */
    private Integer samplingPointCount;
    /**
     * @Fields samplingPeriod : 周期
     */
    private Integer samplingPeriod;
    /**
     * @Fields samplingFrequency : 频次
     */
    private Integer samplingFrequency;
    /**
     * @Fields samplingNum : 点位采样样品个数
     */
    private Integer samplingNum;
    /**
     * @Fields sampleCount : 样品数量
     */
    private Integer sampleCount;
    /**
     * @Fields merchantId : 商户id
     */
    private Integer merchantId;
    /**
     * @Fields contractGbId : 合同唯一标识
     */
    private Long contractGbId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getOrderGbId() {
        return orderGbId;
    }

    public void setOrderGbId(Long orderGbId) {
        this.orderGbId = orderGbId;
    }

    public Long getSampleGbId() {
        return sampleGbId;
    }

    public void setSampleGbId(Long sampleGbId) {
        this.sampleGbId = sampleGbId;
    }

    public Long getRelateOut() {
        return relateOut;
    }

    public void setRelateOut(Long relateOut) {
        this.relateOut = relateOut;
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

    public Integer getSamplingNum() {
        return samplingNum;
    }

    public void setSamplingNum(Integer samplingNum) {
        this.samplingNum = samplingNum;
    }

    public Integer getSampleCount() {
        return sampleCount;
    }

    public void setSampleCount(Integer sampleCount) {
        this.sampleCount = sampleCount;
    }

    public Integer getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Integer merchantId) {
        this.merchantId = merchantId;
    }

    public Long getContractGbId() {
        return contractGbId;
    }

    public void setContractGbId(Long contractGbId) {
        this.contractGbId = contractGbId;
    }
}
