package com.sunwayworld.emc.module.erp.resource;

import org.springframework.web.bind.annotation.RequestMapping;

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

/**
 * @author zhengxy@sunwayworld.com 2023年08月11日
 */
@RequestMapping("/open/hj/lims")
public interface ErpOpenResource {

    RestResult<Boolean> pushOrder(MidOrderDetailVO orderDetail);

    RestResult<Boolean> pushOrderChange(OrderChangeDetailResp orderChangeDetail);

    RestResult<Boolean> pushOrderChangeConfirm(ConfirmOrderChangeReq confirmOrderChangeReq);

    RestResult<Boolean> pushOrderPriceChangeConfirm(OrderReviewReq orderReviewReq);

    RestResult<Boolean> pushPlanContract(MidPlanContractVO planContract);

    RestResult<Boolean> pushPlanContractOrderConfirm(PlanContractOrderConfirm orderConfirm);

    RestResult<Boolean> pushPlanContractOrderChange(MidPlanContractChangeApplyDTO contractChange);

    RestResult<Boolean> pushAuditUserContract(PushAuditUserContractReq auditUserContractReq);

    RestResult<Boolean> pushPlanContractCancel(PlanContractCancelReq planContractCancelReq);
}
