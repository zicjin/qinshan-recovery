package com.sunwayworld.emc.module.erp.service;

import java.util.List;

import com.sunwayworld.emc.module.bp.edit.projectchange.bean.EmcProjectChangeBean;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcProjectBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

/**
 * @author zhengxy@sunwayworld.com 2023年07月26日
 */
public interface ErpOrderService {

    /**
     * 分配跟单员
     * @param wrapper
     */
    void updateMerchandiser(RestJsonWrapperBean wrapper);

    /**
     * 订单变更审核
     * @param projectList
     */
    void confirmOrderChange(List<EmcProjectChangeBean> projectList, boolean reject, String rejectReason);

    /**
     * 推送订单评审结果
     * @param jsonStr
     */
    String confirmOrderChangeToErp(String jsonStr);

    /**
     * 推送订单评审结果
     * @param projectList
     */
    void reviewOrderContract(List<EmcProjectBean> projectList, boolean reject, String rejectReason);

    /**
     * 推送订单评审结果
     * @param jsonStr
     */
    String reviewOrderContractToErp(String jsonStr);

    /**
     * 推送订单变更
     * @param wrapper
     */
    void pushOrderChange(RestJsonWrapperBean wrapper);
}
