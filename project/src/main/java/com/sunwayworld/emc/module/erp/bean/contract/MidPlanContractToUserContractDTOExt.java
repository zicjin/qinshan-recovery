package com.sunwayworld.emc.module.erp.bean.contract;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import com.sunwayworld.emc.module.erp.bean.contract.MidPlanContractToUserContractDTO;


public class MidPlanContractToUserContractDTOExt extends MidPlanContractToUserContractDTO {

    /** true:下单, false:保存草稿,lims只能保存订单，然后到erp认款再确认下单 */
    private Boolean bCheck;

    /** 本方签署公司名称 */
    private String ownCompanyName;

    /** 自定义字段采样类型id */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long samplingTypeId;

    public Boolean getbCheck() {
        return bCheck;
    }

    public void setbCheck(Boolean bCheck) {
        this.bCheck = bCheck;
    }

    public String getOwnCompanyName() {
        return ownCompanyName;
    }

    public void setOwnCompanyName(String ownCompanyName) {
        this.ownCompanyName = ownCompanyName;
    }

    public Long getSamplingTypeId() {
        return samplingTypeId;
    }

    public void setSamplingTypeId(Long samplingTypeId) {
        this.samplingTypeId = samplingTypeId;
    }
}
