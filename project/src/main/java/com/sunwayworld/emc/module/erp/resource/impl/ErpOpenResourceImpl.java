package com.sunwayworld.emc.module.erp.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.erp.bean.ConfirmOrderChangeReq;
import com.sunwayworld.emc.module.erp.bean.MidOrderDetailVO;
import com.sunwayworld.emc.module.erp.bean.OrderChangeDetailResp;
import com.sunwayworld.emc.module.erp.bean.OrderReviewReq;
import com.sunwayworld.emc.module.erp.bean.RestResult;
import com.sunwayworld.emc.module.erp.bean.contract.MidPlanContractChangeApplyDTO;
import com.sunwayworld.emc.module.erp.bean.contract.MidPlanContractVO;
import com.sunwayworld.emc.module.erp.bean.contract.PlanContractCancelReq;
import com.sunwayworld.emc.module.erp.bean.contract.PlanContractOrderConfirm;
import com.sunwayworld.emc.module.erp.bean.contract.PushAuditUserContractReq;
import com.sunwayworld.emc.module.erp.resource.ErpOpenResource;
import com.sunwayworld.emc.module.erp.service.ErpContractOrderService;
import com.sunwayworld.emc.module.erp.service.ErpContractService;
import com.sunwayworld.emc.module.erp.service.ErpOpenService;

/**
 * @author zhengxy@sunwayworld.com 2023年08月11日
 */
@RestController
public class ErpOpenResourceImpl implements ErpOpenResource {

    @Autowired
    private ErpOpenService openService;

    @Autowired
    private ErpContractService contractService;

    @Autowired
    private ErpContractOrderService contractOrderService;

    @Override
    @RequestMapping(value = "/pushOrder")
    public RestResult<Boolean> pushOrder(@RequestBody MidOrderDetailVO orderDetail) {
        return openService.pushOrder(orderDetail);
    }

    @Override
    @RequestMapping(value = "/pushOrderChange")
    public RestResult<Boolean> pushOrderChange(@RequestBody OrderChangeDetailResp orderChangeDetail) {
        return openService.pushOrderChange(orderChangeDetail);
    }

    @Override
    @RequestMapping(value = "/pushOrderChangeConfirm")
    public RestResult<Boolean> pushOrderChangeConfirm(@RequestBody ConfirmOrderChangeReq confirmOrderChangeReq) {
        return openService.pushOrderChangeConfirm(confirmOrderChangeReq);
    }

    @Override
    @RequestMapping(value = "/pushOrderPriceChangeConfirm")
    public RestResult<Boolean> pushOrderPriceChangeConfirm(@RequestBody OrderReviewReq orderReviewReq) {
        return openService.pushOrderPriceChangeConfirm(orderReviewReq);
    }

    @Override
    @RequestMapping(value = "/pushPlanContract")
    public RestResult<Boolean> pushPlanContract(@RequestBody MidPlanContractVO planContract) {
        return contractService.pushPlanContract(planContract);
    }

    @Override
    @RequestMapping(value = "/pushPlanContractOrderConfirm")
    public RestResult<Boolean> pushPlanContractOrderConfirm(@RequestBody PlanContractOrderConfirm orderConfirm) {
        return contractOrderService.pushPlanContractOrderConfirm(orderConfirm);
    }

    @Override
    @RequestMapping(value = "/rePushPlanContractChange")
    public RestResult<Boolean> pushPlanContractOrderChange(@RequestBody MidPlanContractChangeApplyDTO contractChange) {
        return contractService.pushPlanContractChange(contractChange);
    }

    @Override
    @RequestMapping(value = "/pushAuditUserContract")
    public RestResult<Boolean> pushAuditUserContract(@RequestBody PushAuditUserContractReq auditUserContractReq) {
        return contractService.pushAuditUserContract(auditUserContractReq);
    }

    @Override
    @RequestMapping(value = "/pushPlanContractCancel")
    public RestResult<Boolean> pushPlanContractCancel(@RequestBody PlanContractCancelReq planContractCancelReq) {
        return contractService.pushPlanContractCancel(planContractCancelReq);
    }
}
