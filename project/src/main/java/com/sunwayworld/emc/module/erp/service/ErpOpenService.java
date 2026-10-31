package com.sunwayworld.emc.module.erp.service;

import com.sunwayworld.emc.module.erp.bean.ConfirmOrderChangeReq;
import com.sunwayworld.emc.module.erp.bean.MidOrderDetailVO;
import com.sunwayworld.emc.module.erp.bean.OrderChangeDetailResp;
import com.sunwayworld.emc.module.erp.bean.OrderReviewReq;
import com.sunwayworld.emc.module.erp.bean.RestResult;

/**
 * @author zhengxy@sunwayworld.com 2023年07月26日
 */
public interface ErpOpenService {

    RestResult<Boolean> pushOrder(MidOrderDetailVO orderDetail);

    RestResult<Boolean> pushOrderChange(OrderChangeDetailResp orderChangeDetail);

    RestResult<Boolean> pushOrderChangeConfirm(ConfirmOrderChangeReq confirmOrderChangeReq);

    RestResult<Boolean> pushOrderPriceChangeConfirm(OrderReviewReq orderReviewReq);
}
