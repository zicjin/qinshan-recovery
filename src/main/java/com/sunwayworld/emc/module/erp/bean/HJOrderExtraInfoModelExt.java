package com.sunwayworld.emc.module.erp.bean;

import java.io.Serializable;
import java.util.List;

/**
 * @author LHQ
 */
public class HJOrderExtraInfoModelExt implements Serializable {

    private static final long serialVersionUID = -6039521761506713288L;

    /** 合同价格详情 */
    private EnvContractPriceInfoEntity contractPriceInfo;

    /** 合同折扣审核信息，只保存最新的记录 */
    private EnvContractDiscountInfoEntity contractDiscountInfo;

    /** 环境订单额外增加信息 */
    private EnvOrderExtraInfoEntity orderExtraInfo;

    /** "环境订单样品额外增加信息" */
    private List<EnvOrderSampleExtraInfoEntity> envOrderSampleExtraInfoEntity;

    /** 环境订单SKU检测项信息 */
    private List<EnvSampleSkuSamplingTypeEntity> sampleSkuSamplingTypes;

    /** 其他费用列表 */
    private List<EnvOrderOtherFeeInfoEntity> otherFee;

    public EnvContractPriceInfoEntity getContractPriceInfo() {
        return contractPriceInfo;
    }

    public void setContractPriceInfo(EnvContractPriceInfoEntity contractPriceInfo) {
        this.contractPriceInfo = contractPriceInfo;
    }

    public EnvContractDiscountInfoEntity getContractDiscountInfo() {
        return contractDiscountInfo;
    }

    public void setContractDiscountInfo(EnvContractDiscountInfoEntity contractDiscountInfo) {
        this.contractDiscountInfo = contractDiscountInfo;
    }

    public EnvOrderExtraInfoEntity getOrderExtraInfo() {
        return orderExtraInfo;
    }

    public void setOrderExtraInfo(EnvOrderExtraInfoEntity orderExtraInfo) {
        this.orderExtraInfo = orderExtraInfo;
    }

    public List<EnvOrderSampleExtraInfoEntity> getEnvOrderSampleExtraInfoEntity() {
        return envOrderSampleExtraInfoEntity;
    }

    public void setEnvOrderSampleExtraInfoEntity(List<EnvOrderSampleExtraInfoEntity> envOrderSampleExtraInfoEntity) {
        this.envOrderSampleExtraInfoEntity = envOrderSampleExtraInfoEntity;
    }

    public List<EnvSampleSkuSamplingTypeEntity> getSampleSkuSamplingTypes() {
        return sampleSkuSamplingTypes;
    }

    public void setSampleSkuSamplingTypes(List<EnvSampleSkuSamplingTypeEntity> sampleSkuSamplingTypes) {
        this.sampleSkuSamplingTypes = sampleSkuSamplingTypes;
    }

    public List<EnvOrderOtherFeeInfoEntity> getOtherFee() {
        return otherFee;
    }

    public void setOtherFee(List<EnvOrderOtherFeeInfoEntity> otherFee) {
        this.otherFee = otherFee;
    }
}
