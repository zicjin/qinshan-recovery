package com.sunwayworld.emc.module.bp.order.orderreceipt.resource;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.sunwayworld.emc.module.mdm.basemdm.qccategory.bean.EmcQcCategoryBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

/**
 * @author zhengxy@sunwayworld.com 2019年10月14日
 */
@RequestMapping("/secure/emc/module/bp/order/order-receipts")
public interface EmcOrderReceiptResource {

    @RequestMapping(value = "/qc/{category}/queries/choosable", method = RequestMethod.POST)
    Page<EmcQcCategoryBean> selectQcCategoryList(@PathVariable String category, RestJsonWrapperBean wrapper);

    @RequestMapping(value = "/qc-orders/action/copy", method = RequestMethod.POST)
    void copyQcOrder(RestJsonWrapperBean wrapper);

    @RequestMapping(value = "/qc-orders", method = RequestMethod.DELETE)
    void deleteQcOrder(RestJsonWrapperBean wrapper);

    @RequestMapping(value = "/samples/action/match-barcode", method = RequestMethod.POST)
    Integer matchBarcode(RestJsonWrapperBean wrapper);

    @RequestMapping(value = "/orders/action/retain", method = RequestMethod.POST)
    void retainOrder(RestJsonWrapperBean wrapper);

    @RequestMapping(value = "/orders", method = RequestMethod.DELETE)
    void deleteOrder(RestJsonWrapperBean wrapper);

}
