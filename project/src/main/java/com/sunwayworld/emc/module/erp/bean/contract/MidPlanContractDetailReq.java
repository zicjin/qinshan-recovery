package com.sunwayworld.emc.module.erp.bean.contract;

/**
 * @author zhengxy@sunwayworld.com 2023年11月22日
 */
public class MidPlanContractDetailReq extends BaseErp5Param {

    private Long planContractGbId;

    public Long getPlanContractGbId() {
        return planContractGbId;
    }

    public void setPlanContractGbId(Long planContractGbId) {
        this.planContractGbId = planContractGbId;
    }
}
