package com.sunwayworld.emc.module.erp.bean.contract;

import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/** 中台-计划合同-VO */
public class MidPlanContractVO extends BaseErp5Param {

    @JsonSerialize(using = ToStringSerializer.class)
    /** 计划合同标识 */
    private Long planContractGbId;

    /** 计划合同编号 */
    private String planContractNumber;

    @JsonSerialize(using = ToStringSerializer.class)
    /** 计划合同签署子公司 */
    private Long companyGbId;

    /** 计划合同签署子公司名称 */
    private String companyName;

    @JsonSerialize(using = ToStringSerializer.class)
    /** 集团客户标识 */
    private Long groupCustomerGbId;

    /** 客户名称 */
    private String customerName;

    /** 委托方信息 */
    private List<MidPlanContractToDelegateCompanyVO> delegateCompanies;

    /** 服务方信息 */
    private List<MidPlanContractToServerVO> servers;

    /** 附件信息 */
    private MidPlanContractToAttachVO attach;

    /** 拆分计划 */
    private MidPlanContractToSplitVO split;

    /** 预先拆分商机 */
    private List<MidPlanContractToPreOpportunityVO> preOpportunities;

    public Long getPlanContractGbId() {
        return planContractGbId;
    }

    public void setPlanContractGbId(Long planContractGbId) {
        this.planContractGbId = planContractGbId;
    }

    public String getPlanContractNumber() {
        return planContractNumber;
    }

    public void setPlanContractNumber(String planContractNumber) {
        this.planContractNumber = planContractNumber;
    }

    public Long getCompanyGbId() {
        return companyGbId;
    }

    public void setCompanyGbId(Long companyGbId) {
        this.companyGbId = companyGbId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Long getGroupCustomerGbId() {
        return groupCustomerGbId;
    }

    public void setGroupCustomerGbId(Long groupCustomerGbId) {
        this.groupCustomerGbId = groupCustomerGbId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public List<MidPlanContractToDelegateCompanyVO> getDelegateCompanies() {
        return delegateCompanies;
    }

    public void setDelegateCompanies(List<MidPlanContractToDelegateCompanyVO> delegateCompanies) {
        this.delegateCompanies = delegateCompanies;
    }

    public List<MidPlanContractToServerVO> getServers() {
        return servers;
    }

    public void setServers(List<MidPlanContractToServerVO> servers) {
        this.servers = servers;
    }

    public MidPlanContractToAttachVO getAttach() {
        return attach;
    }

    public void setAttach(MidPlanContractToAttachVO attach) {
        this.attach = attach;
    }

    public MidPlanContractToSplitVO getSplit() {
        return split;
    }

    public void setSplit(MidPlanContractToSplitVO split) {
        this.split = split;
    }

    public List<MidPlanContractToPreOpportunityVO> getPreOpportunities() {
        return preOpportunities;
    }

    public void setPreOpportunities(List<MidPlanContractToPreOpportunityVO> preOpportunities) {
        this.preOpportunities = preOpportunities;
    }
}
