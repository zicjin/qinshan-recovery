package com.sunwayworld.emc.module.erp.bean.contract;

import java.io.Serializable;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/** 中台-计划合同-预先拆分商机-VO */
public class MidPlanContractToPreOpportunityVO implements Serializable {

    @JsonSerialize(using = ToStringSerializer.class)
    /** 计划标识 */
    private Long planContractGbId;

    @JsonSerialize(using = ToStringSerializer.class)
    /** 商机标识 */
    private Long businessOpportunityGbId;

    /** 商机名称 */
    private String businessOpportunityName;

    @JsonSerialize(using = ToStringSerializer.class)
    /** 产品线标识 */
    private Long productionLineGbId;

    /** 产品线名称 */
    private String productionLineName;

    /** 状态 */
    private Integer status;

    public Long getPlanContractGbId() {
        return planContractGbId;
    }

    public void setPlanContractGbId(Long planContractGbId) {
        this.planContractGbId = planContractGbId;
    }

    public Long getBusinessOpportunityGbId() {
        return businessOpportunityGbId;
    }

    public void setBusinessOpportunityGbId(Long businessOpportunityGbId) {
        this.businessOpportunityGbId = businessOpportunityGbId;
    }

    public String getBusinessOpportunityName() {
        return businessOpportunityName;
    }

    public void setBusinessOpportunityName(String businessOpportunityName) {
        this.businessOpportunityName = businessOpportunityName;
    }

    public Long getProductionLineGbId() {
        return productionLineGbId;
    }

    public void setProductionLineGbId(Long productionLineGbId) {
        this.productionLineGbId = productionLineGbId;
    }

    public String getProductionLineName() {
        return productionLineName;
    }

    public void setProductionLineName(String productionLineName) {
        this.productionLineName = productionLineName;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
