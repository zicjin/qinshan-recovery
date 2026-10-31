package com.sunwayworld.emc.module.erp.resource;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

/**
 * @author zhengxy@sunwayworld.com 2023年07月26日
 */
@RequestMapping("/secure/emc/module/erp/order")
public interface ErpOrderResource {

    void updateMerchandiser(RestJsonWrapperBean wrapper);
}
