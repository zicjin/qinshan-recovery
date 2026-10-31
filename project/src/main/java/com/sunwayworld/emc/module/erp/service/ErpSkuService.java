package com.sunwayworld.emc.module.erp.service;

import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

/**
 * @author zhengxy@sunwayworld.com 2023年07月25日
 */
public interface ErpSkuService {

    /**
     * 推送单项SKU数据
     * @param wrapper
     */
    void pushSku(RestJsonWrapperBean wrapper);

    /**
     * 推送套餐SKU数据
     * @param wrapper
     */
    void pushPackageSku(RestJsonWrapperBean wrapper);

    /**
     * 创建单项SKU采样成本
     * @param wrapper
     */
    void createSkuSamplingCost(RestJsonWrapperBean wrapper);

    /**
     * 删除单项SKU采样成本
     * @param wrapper
     */
    void deleteSkuSamplingCost(RestJsonWrapperBean wrapper);

    /**
     * 更新单项SKU采样成本
     * @param wrapper
     */
    void updateSkuSamplingCost(RestJsonWrapperBean wrapper);

    /**
     * 推送SKU到ERP
     * @param skuJson
     * @return
     */
    String pushSkuToErp(String skuJson);
}
