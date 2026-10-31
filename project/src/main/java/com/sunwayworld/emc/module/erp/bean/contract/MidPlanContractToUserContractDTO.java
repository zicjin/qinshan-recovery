package com.sunwayworld.emc.module.erp.bean.contract;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/** 计划合同拆分合同下单-DTO */
public class MidPlanContractToUserContractDTO extends BaseErp5Param {

    @JsonSerialize(using = ToStringSerializer.class)
    /** 计划合同标识 */
    private Long planContractGbId;

    @JsonSerialize(using = ToStringSerializer.class)
    /** 计划合同拆分子单标识 */
    private Long planContractBindItemGbId;

    @JsonSerialize(using = ToStringSerializer.class)
    /** 商户模板gbId */
    private Long customTemplateGbId;

    @JsonSerialize(using = ToStringSerializer.class)
    /** 合同标识 */
    private Long userContractGbId;

    /** 合同状态, 1->未下单，2->已下单，3->已完成，4->已取消 */
    private Integer contractStatus;

    @JsonSerialize(using = ToStringSerializer.class)
    /** 商机标识 */
    private Long businessOpportunityGbId;

    /** 商机名称 */
    private String businessOpportunityName;

    @JsonSerialize(using = ToStringSerializer.class)
    /** 行业群标识 */
    private Long industryGbId;

    /** 行业群名称 */
    private String industryName;

    @JsonSerialize(using = ToStringSerializer.class)
    /** 产品线标识 */
    private Long productionLineGbId;

    /** 产品线名称 */
    private String productionLineName;

    @JsonSerialize(using = ToStringSerializer.class)
    /** 拆分金额 */
    private Long price;

    /** 付款信息 */
    //private MidPlanContractToContractCashDTO pay;

    /** 报价信息 */
    private MidPlanContractToContractPkgDTO pkg;

    /** 委托方信息 */
    private MidPlanContractToDelegateCompanyDTO delegateCompany;

    // ============= 二开扩展类 ===============

    /** 订单 */
    private MidOrderDTOExt order;

    public Long getPlanContractGbId() {
        return planContractGbId;
    }

    public void setPlanContractGbId(Long planContractGbId) {
        this.planContractGbId = planContractGbId;
    }

    public Long getPlanContractBindItemGbId() {
        return planContractBindItemGbId;
    }

    public void setPlanContractBindItemGbId(Long planContractBindItemGbId) {
        this.planContractBindItemGbId = planContractBindItemGbId;
    }

    public Long getCustomTemplateGbId() {
        return customTemplateGbId;
    }

    public void setCustomTemplateGbId(Long customTemplateGbId) {
        this.customTemplateGbId = customTemplateGbId;
    }

    public Long getUserContractGbId() {
        return userContractGbId;
    }

    public void setUserContractGbId(Long userContractGbId) {
        this.userContractGbId = userContractGbId;
    }

    public Integer getContractStatus() {
        return contractStatus;
    }

    public void setContractStatus(Integer contractStatus) {
        this.contractStatus = contractStatus;
    }

    public Long getBusinessOpportunityGbId() {
        return businessOpportunityGbId;
    }

    public void setBusinessOpportunityGbId(Long businessOpportunityGbId) {
        this.businessOpportunityGbId = businessOpportunityGbId;
    }

    public Long getIndustryGbId() {
        return industryGbId;
    }

    public void setIndustryGbId(Long industryGbId) {
        this.industryGbId = industryGbId;
    }

    public Long getProductionLineGbId() {
        return productionLineGbId;
    }

    public void setProductionLineGbId(Long productionLineGbId) {
        this.productionLineGbId = productionLineGbId;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public MidOrderDTOExt getOrder() {
        return order;
    }

    public void setOrder(MidOrderDTOExt order) {
        this.order = order;
    }

    public MidPlanContractToContractPkgDTO getPkg() {
        return pkg;
    }

    public void setPkg(MidPlanContractToContractPkgDTO pkg) {
        this.pkg = pkg;
    }

    public String getBusinessOpportunityName() {
        return businessOpportunityName;
    }

    public void setBusinessOpportunityName(String businessOpportunityName) {
        this.businessOpportunityName = businessOpportunityName;
    }

    public String getIndustryName() {
        return industryName;
    }

    public void setIndustryName(String industryName) {
        this.industryName = industryName;
    }

    public String getProductionLineName() {
        return productionLineName;
    }

    public void setProductionLineName(String productionLineName) {
        this.productionLineName = productionLineName;
    }

    public MidPlanContractToDelegateCompanyDTO getDelegateCompany() {
        return delegateCompany;
    }

    public void setDelegateCompany(MidPlanContractToDelegateCompanyDTO delegateCompany) {
        this.delegateCompany = delegateCompany;
    }
}
