package com.sunwayworld.emc.module.erp.bean.contract;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import java.util.List;

/** 中台-计划合同变更-DTO */
public class MidPlanContractChangeApplyDTO extends BaseErp5Param {

    @JsonSerialize(using = ToStringSerializer.class)
    /** 计划合同标识 */
    private Long planContractGbId;

    @JsonSerialize(using = ToStringSerializer.class)
    /** 计划合同签署子公司 */
    private Long companyGbId;

    /** 计划合同编号 */
    private String planContractNumber;

    /** 计划合同状态 */
    private Integer planContractStatus;

    /** 委托方信息 */
    private List<MidPlanContractToDelegateCompanyDTO> delegateCompanies;

    /** 服务方信息 */
    private List<MidPlanContractToServerDTO> servers;

    /** 附件信息 */
    private MidPlanContractToAttachDTO attach;

    /** 拆分计划信息 */
    private List<MidPlanContractToUserContractDTO> split;

    public Long getPlanContractGbId() {
        return planContractGbId;
    }

    public void setPlanContractGbId(Long planContractGbId) {
        this.planContractGbId = planContractGbId;
    }

    public Long getCompanyGbId() {
        return companyGbId;
    }

    public void setCompanyGbId(Long companyGbId) {
        this.companyGbId = companyGbId;
    }

    public String getPlanContractNumber() {
        return planContractNumber;
    }

    public void setPlanContractNumber(String planContractNumber) {
        this.planContractNumber = planContractNumber;
    }

    public Integer getPlanContractStatus() {
        return planContractStatus;
    }

    public void setPlanContractStatus(Integer planContractStatus) {
        this.planContractStatus = planContractStatus;
    }

    public List<MidPlanContractToDelegateCompanyDTO> getDelegateCompanies() {
        return delegateCompanies;
    }

    public void setDelegateCompanies(List<MidPlanContractToDelegateCompanyDTO> delegateCompanies) {
        this.delegateCompanies = delegateCompanies;
    }

    public List<MidPlanContractToServerDTO> getServers() {
        return servers;
    }

    public void setServers(List<MidPlanContractToServerDTO> servers) {
        this.servers = servers;
    }

    public MidPlanContractToAttachDTO getAttach() {
        return attach;
    }

    public void setAttach(MidPlanContractToAttachDTO attach) {
        this.attach = attach;
    }

    public List<MidPlanContractToUserContractDTO> getSplit() {
        return split;
    }

    public void setSplit(List<MidPlanContractToUserContractDTO> split) {
        this.split = split;
    }
}
