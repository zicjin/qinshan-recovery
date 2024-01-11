package com.sunwayworld.emc.module.erp.bean.contract;

import java.util.List;

/**
 * @author zhengxy@sunwayworld.com 2023年12月22日
 */
public class PlanContractCancelReq extends BaseErp5Param {

    /** 计划合同GBID */
    private Long planContractGbId;

    private List<AttachVO> attach;

    public Long getPlanContractGbId() {
        return planContractGbId;
    }

    public void setPlanContractGbId(Long planContractGbId) {
        this.planContractGbId = planContractGbId;
    }

    public List<AttachVO> getAttach() {
        return attach;
    }

    public void setAttach(List<AttachVO> attach) {
        this.attach = attach;
    }
}
