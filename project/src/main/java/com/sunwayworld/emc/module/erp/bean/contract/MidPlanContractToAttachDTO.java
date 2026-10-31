package com.sunwayworld.emc.module.erp.bean.contract;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import java.io.Serializable;
import java.util.List;

/** 中台-计划合同-附件信息 */
public class MidPlanContractToAttachDTO implements Serializable {

    @JsonSerialize(using = ToStringSerializer.class)
    /** 计划合同标识-更新时有值 */
    private Long planContractGbId;

    /** 计划合同附件 */
    private List<AttachVO> attach;

    /** 计划合同附件类型 */
    private Integer attachType;

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

    public Integer getAttachType() {
        return attachType;
    }

    public void setAttachType(Integer attachType) {
        this.attachType = attachType;
    }
}
