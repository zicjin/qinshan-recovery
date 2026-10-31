package com.sunwayworld.emc.module.erp.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.erp.resource.ErpSkuResource;
import com.sunwayworld.emc.module.erp.service.ErpSkuService;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

/**
 * @author zhengxy@sunwayworld.com 2023年07月25日
 */
@RestController
public class ErpSkuResourceImpl implements ErpSkuResource {

    @Autowired
    private ErpSkuService erpSkuService;

    @Override
    @RequestMapping(value = "/action/push")
    public void pushSku(RestJsonWrapperBean wrapper) {
        erpSkuService.pushSku(wrapper);
    }

    @Override
    @RequestMapping(value = "/action/push-package")
    public void pushPackageSku(RestJsonWrapperBean wrapper) {
        erpSkuService.pushPackageSku(wrapper);
    }

    @Override
    public void createSkuSamplingCost(RestJsonWrapperBean wrapper) {

    }

    @Override
    public void deleteSkuSamplingCost(RestJsonWrapperBean wrapper) {

    }

    @Override
    public void updateSkuSamplingCost(RestJsonWrapperBean wrapper) {

    }
}
