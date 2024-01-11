package com.sunwayworld.emc.module.erp.bean.contract;

import java.io.Serializable;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/** 中台-计划合同-拆分合同信息-VO */
public class MidPlanContractToContractVO implements Serializable {

    @JsonSerialize(using = ToStringSerializer.class)
    /** 计划标识 */
    private Long planContractGbId;

    @JsonSerialize(using = ToStringSerializer.class)
    /** 拆分标识 */
    private Long contractGbId;

    @JsonSerialize(using = ToStringSerializer.class)
    /** 拆分金额 */
    private Long price;

    @JsonSerialize(using = ToStringSerializer.class)
    /** 商户客户标识 */
    private Long customerGbId;

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

    @JsonSerialize(using = ToStringSerializer.class)
    /** 行业群标识 */
    private Long industryGbId;

    /** 行业群名称 */
    private String industryName;

    @JsonSerialize(using = ToStringSerializer.class)
    /** 合同标识 */
    private Long userContractGbId;

    @JsonSerialize(using = ToStringSerializer.class)
    /** 订单标识 */
    private Long orderGbId;

    /** 订单编号 */
    private String orderNumber;

    /** 订单状态 */
    private Integer orderStatus;

    /** 是否以变更 */
    private Boolean hasChange;

    @JsonSerialize(using = ToStringSerializer.class)
    /** 变更标识 */
    private Long applyGbId;

    /** 拆分计划状态 */
    private Integer contractStatus;

    public Long getPlanContractGbId() {
        return planContractGbId;
    }

    public void setPlanContractGbId(Long planContractGbId) {
        this.planContractGbId = planContractGbId;
    }

    public Long getContractGbId() {
        return contractGbId;
    }

    public void setContractGbId(Long contractGbId) {
        this.contractGbId = contractGbId;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public Long getCustomerGbId() {
        return customerGbId;
    }

    public void setCustomerGbId(Long customerGbId) {
        this.customerGbId = customerGbId;
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

    public Long getIndustryGbId() {
        return industryGbId;
    }

    public void setIndustryGbId(Long industryGbId) {
        this.industryGbId = industryGbId;
    }

    public String getIndustryName() {
        return industryName;
    }

    public void setIndustryName(String industryName) {
        this.industryName = industryName;
    }

    public Long getUserContractGbId() {
        return userContractGbId;
    }

    public void setUserContractGbId(Long userContractGbId) {
        this.userContractGbId = userContractGbId;
    }

    public Long getOrderGbId() {
        return orderGbId;
    }

    public void setOrderGbId(Long orderGbId) {
        this.orderGbId = orderGbId;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public Integer getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(Integer orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Boolean getHasChange() {
        return hasChange;
    }

    public void setHasChange(Boolean hasChange) {
        this.hasChange = hasChange;
    }

    public Long getApplyGbId() {
        return applyGbId;
    }

    public void setApplyGbId(Long applyGbId) {
        this.applyGbId = applyGbId;
    }

    public Integer getContractStatus() {
        return contractStatus;
    }

    public void setContractStatus(Integer contractStatus) {
        this.contractStatus = contractStatus;
    }
}
