package com.sunwayworld.emc.module.erp.bean.contract;

import java.io.Serializable;
import java.util.List;

/** 中台-计划合同-拆分信息-VO */
public class MidPlanContractToSplitVO implements Serializable {
    /** 是否拆分 */
    private Boolean needSplit;

    /** 拆分合同 */
    private List<MidPlanContractToContractVO> contracts;


    public Boolean getNeedSplit() {
        return needSplit;
    }

    public void setNeedSplit(Boolean needSplit) {
        this.needSplit = needSplit;
    }

    public List<MidPlanContractToContractVO> getContracts() {
        return contracts;
    }

    public void setContracts(List<MidPlanContractToContractVO> contracts) {
        this.contracts = contracts;
    }
}
