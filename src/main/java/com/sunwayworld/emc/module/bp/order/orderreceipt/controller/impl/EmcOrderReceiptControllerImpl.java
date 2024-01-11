package com.sunwayworld.emc.module.bp.order.orderreceipt.controller.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.bp.order.orderreceipt.controller.EmcOrderReceiptController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author zhengxy@sunwayworld.com 2019年10月14日
 */
@Controller
@GikamBean
public class EmcOrderReceiptControllerImpl implements EmcOrderReceiptController {

    @Override
    @Log(value = "样品接收编制列表页", type = LogType.CONTROLLER)
    public String editListPage() {
        return "emc/module/bp/order/order-receipts/emc-order-receipt-edit-list";
    }


    @Override
    @Log(value = "样品接收-QC类型选择列表页", type = LogType.CONTROLLER)
    public String qcCategoryChooseListPage() {
        return "emc/module/bp/order/order-receipts/emc-order-receipt-qc-choose-list";
    }

    @Override
    @Log(value = "样品接收-采样单附件信息列表页", type = LogType.CONTROLLER)
    public String sampleAnnexListPage() {
        return "emc/module/bp/order/order-receipts/emc-order-receipt-sample-annex";
    }

    @Override
    @Log(value = "样品容器页", type = LogType.CONTROLLER)
    public String orderReceiptContainerPage() {
        return "emc/module/bp/order/order-receipts/emc-order-receipt-container";
    }

    @Log(value = "样品容器批录入页", type = LogType.CONTROLLER)
    @Override
    public String batchOrderContainerPage() {
        return "emc/module/bp/sample/samples/emc-order-container-batch";
    }
}
