package com.sunwayworld.emc.module.bp.order.orderhandover;



import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.bp.order.orderhandover.controller.EmcOrderHandoverController;

/**
 * @author licm@sunwayworld.com 2021年12月17日
 */
@RequestMapping("/secure/emc/module/bp/order/order-handovers/page")
public interface PfOrderHandoverController extends EmcOrderHandoverController {
    @RequestMapping("/search-all-list")
    String searchListAllPage();
}
