package com.sunwayworld.emc.module.bp.sample.sample.controller.impl;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.bp.sample.sample.controller.PfSampleController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author licm@sunwayworld.com 2021年10月13日
 */
@Controller
@GikamBean
public class PfSampleControllerImpl extends EmcSampleControllerImpl implements PfSampleController {

    @Override
    @Log(value = "voc瓶号", type = LogType.CONTROLLER)
    @RequestMapping("/voc-bottle")
    public String vocBottleChoosePage() {
        return "emc/module/bp/sample/samples/emc-sample-voc-bottle-choose-list";
    }

    @Override
    @Log(value = "现场采样-数据录入土壤样品描述详情页", type = LogType.CONTROLLER)
    @RequestMapping("/soli-order-desc-detail")
    public String soliOrderDescDetailPage() {
        return "emc/module/bp/sample/samples/emc-sample-soli-order-desc-detail";
    }

    @Override
    @Log(value = "出入库", type = LogType.CONTROLLER)
    @RequestMapping("/order-stock")
    public String orderStockPage() {
        return "emc/module/bp/order/order-stock/emc-order-stock-edit-list";
    }

    @Override
    @Log(value = "出入库明细", type = LogType.CONTROLLER)
    @RequestMapping("/order-stock-details")
    public String orderStockDetailsPage() {
        return "emc/module/bp/order/order-stock/emc-order-stock-details-list";
    }

    @Override
    @Log(value = "滤筒", type = LogType.CONTROLLER)
    @RequestMapping("/oil-film")
    public String oilFilmChoosePage() {
        return "emc/module/bp/sample/samples/emc-sample-oil-film-choose-list";
    }

    @Override
    @Log(value = "采样单扩展页", type = LogType.CONTROLLER)
    @RequestMapping("/sample-expand-detail")
    public String sampleExpandDetailPage() {
        return "emc/module/bp/sample/samples/emc-sample-expand-detail-list";
    }

    @Override
    @Log(value = "样品容器选择页面", type = LogType.CONTROLLER)
    @RequestMapping("/order-container-choose-list")
    public String orderContainerChoosePage() {
        return "emc/module/bp/sample/samples/emc-order-container-choose-list";
    }

    @Override
    @Log(value = "气参数录入页面", type = LogType.CONTROLLER)
    @RequestMapping("/gas-parameter-input")
    public String gasParameterInputPage() {
        return "emc/module/bp/sample/samples/emc-sample-gas-parameter-list";
    }

    @Override
    @Log(value = "采样记录查询", type = LogType.CONTROLLER)
    @RequestMapping("/sample-query-search-list")
    public String sampleQuerySearchPage() {
        return "emc/module/bp/sample/samples/emc-sample-query-search-list";
    }
}
