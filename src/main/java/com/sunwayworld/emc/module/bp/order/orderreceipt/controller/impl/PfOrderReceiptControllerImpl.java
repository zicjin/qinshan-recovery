package com.sunwayworld.emc.module.bp.order.orderreceipt.controller.impl;



import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.bp.order.orderreceipt.controller.PfOrderReceiptController;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author licm@sunwayworld.com 2022年07月23日
 */
@Controller
@GikamBean
public class PfOrderReceiptControllerImpl extends EmcOrderReceiptControllerImpl implements PfOrderReceiptController {

    @Override
    public String addTestEditPage() {
        return "emc/module/bp/order/order-add-test/emc-order-add-test-edit-list";        
    }

    public String addTestSearchPage() {
        return "emc/module/bp/order/order-add-test/emc-order-add-test-search-list";
    }

}
