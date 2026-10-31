package com.sunwayworld.emc.module.erp.bean;

import java.util.List;

public class MidSkuQuoteApplyPostDTOExt extends MidSkuQuoteApplyPostDTO {

    /** 套餐sku成本管理额外扩展字段 */
    private EnvSkuCostExtraModel envSkuCostExtraModel;

    /** 单项sku采样成本表 */
    private List<EnvSkuSamplingCostModel> envSkuSamplingCostModels;

    public EnvSkuCostExtraModel getEnvSkuCostExtraModel() {
        return envSkuCostExtraModel;
    }

    public void setEnvSkuCostExtraModel(EnvSkuCostExtraModel envSkuCostExtraModel) {
        this.envSkuCostExtraModel = envSkuCostExtraModel;
    }

    public List<EnvSkuSamplingCostModel> getEnvSkuSamplingCostModels() {
        return envSkuSamplingCostModels;
    }

    public void setEnvSkuSamplingCostModels(List<EnvSkuSamplingCostModel> envSkuSamplingCostModels) {
        this.envSkuSamplingCostModels = envSkuSamplingCostModels;
    }
}
