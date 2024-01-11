package com.sunwayworld.emc.module.erp.service;

import java.util.List;

import com.alibaba.fastjson.JSONObject;

import com.sunwayworld.emc.module.bp.project.project.bean.EmcProjectBean;
import com.sunwayworld.emc.module.erp.bean.RestResult;
import com.sunwayworld.emc.module.erp.bean.contract.MidSamplingTypeDTO;
import com.sunwayworld.emc.module.erp.bean.contract.PlanContractOrderConfirm;
import com.sunwayworld.module.mdm.org.bean.CoreOrgBean;

/**
 * 计划合同订单
 * @author zhengxy@sunwayworld.com 2023年11月09日
 */
public interface ErpContractOrderService {

    /**
     * 推送计划合同订单，LIMS -> ERP
     * @param project 关联计划合同的项目
     */
    void pushPlanContractOrder(EmcProjectBean project);

    /**
     * 推送计划合同订单，用于远程调用记录
     * @param jsonStr 订单JSON数据
     * @return 结果JSON
     */
    String pushPlanContractOrderToErp(String jsonStr);

    /**
     * 推送计划合同下单确认，ERP -> LIMS
     * @param orderConfirm 具体的订单
     * @return 操作结果
     */
    RestResult<Boolean> pushPlanContractOrderConfirm(PlanContractOrderConfirm orderConfirm);

    /**
     * 获取ERP样品类型列表
     * @param merchantId 商户ID
     * @return 样品类型列表
     */
    List<MidSamplingTypeDTO> fetchErpSamplingType(String merchantId);

    /**
     * 获取ERP工业群数据树
     * @return JSONObject
     */
    JSONObject fetchErpIndustryTree();

    /**
     * 获取商户机构
     * @return CoreOrgBean
     */
    CoreOrgBean getMerchantOrg();

    /**
     * 获取ERP人员ID
     * @return ERP GBID
     */
    Long getErpUserId();

    /**
     * 获取默认报价员ID
     * @return ERP GBID
     */
    Long getDefaultSaleGbId();
}
