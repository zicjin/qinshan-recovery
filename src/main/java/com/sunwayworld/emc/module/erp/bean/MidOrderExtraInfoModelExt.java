package com.sunwayworld.emc.module.erp.bean;

import java.io.Serializable;
import java.util.List;

/**
 * @author zhengxy@sunwayworld.com 2023年09月18日
 */
public class MidOrderExtraInfoModelExt implements Serializable {

    private static final long serialVersionUID = -4238617601425543625L;

    private EmcOrderExtraInfo orderExtraInfo;
    private List<String> sampleSkuSamplingTypes;

    public EmcOrderExtraInfo getOrderExtraInfo() {
        return orderExtraInfo;
    }

    public void setOrderExtraInfo(EmcOrderExtraInfo orderExtraInfo) {
        this.orderExtraInfo = orderExtraInfo;
    }

    public List<String> getSampleSkuSamplingTypes() {
        return sampleSkuSamplingTypes;
    }

    public void setSampleSkuSamplingTypes(List<String> sampleSkuSamplingTypes) {
        this.sampleSkuSamplingTypes = sampleSkuSamplingTypes;
    }
}
