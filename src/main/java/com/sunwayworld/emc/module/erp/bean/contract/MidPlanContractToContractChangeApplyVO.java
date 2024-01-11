package com.sunwayworld.emc.module.erp.bean.contract;

/** 中台-计划合同子合同变更-VO */
public class MidPlanContractToContractChangeApplyVO extends BaseErp5Param {

    private MidPlanContractToUserContractVO oldContract;

    private MidPlanContractToUserContractVO newContract;


    public MidPlanContractToUserContractVO getOldContract() {
        return oldContract;
    }

    public void setOldContract(MidPlanContractToUserContractVO oldContract) {
        this.oldContract = oldContract;
    }

    public MidPlanContractToUserContractVO getNewContract() {
        return newContract;
    }

    public void setNewContract(MidPlanContractToUserContractVO newContract) {
        this.newContract = newContract;
    }
}
