package com.sunwayworld.emc.module.erp.resource;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

/**
 * @author zhengxy@sunwayworld.com 2023年07月25日
 */
@RequestMapping("/secure/emc/module/erp/sku")
public interface ErpSkuResource {

    void pushSku(RestJsonWrapperBean wrapper);

    void pushPackageSku(RestJsonWrapperBean wrapper);

    void createSkuSamplingCost(RestJsonWrapperBean wrapper);

    void deleteSkuSamplingCost(RestJsonWrapperBean wrapper);

    void updateSkuSamplingCost(RestJsonWrapperBean wrapper);
}
