package com.sunwayworld.emc.module.erp.bean;

import java.io.Serializable;
import java.util.List;

public class MidOrgVO implements Serializable {

    /** 机构编号 */
    private Long gbId;
    /** 名称 */
    private String name;
    /** 类型，com.jiancetech.business.model.enumModel.OrgType */
    private Integer type;

    /** 资质 */
    private List<String> qualification;

    /** 联系人名称 */
    private String contractName;

    /** 联系人电话 */
    private String contractPhone;

    /** 机构地址 */
    private MidAddressDTO address;

    /** 机构状态 */
    private Integer orgStatus;

    /** 审核机构状态 */
    private Integer auditState;

    public Long getGbId() {
        return gbId;
    }

    public void setGbId(Long gbId) {
        this.gbId = gbId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public List<String> getQualification() {
        return qualification;
    }

    public void setQualification(List<String> qualification) {
        this.qualification = qualification;
    }

    public String getContractName() {
        return contractName;
    }

    public void setContractName(String contractName) {
        this.contractName = contractName;
    }

    public String getContractPhone() {
        return contractPhone;
    }

    public void setContractPhone(String contractPhone) {
        this.contractPhone = contractPhone;
    }

    public MidAddressDTO getAddress() {
        return address;
    }

    public void setAddress(MidAddressDTO address) {
        this.address = address;
    }

    public Integer getOrgStatus() {
        return orgStatus;
    }

    public void setOrgStatus(Integer orgStatus) {
        this.orgStatus = orgStatus;
    }

    public Integer getAuditState() {
        return auditState;
    }

    public void setAuditState(Integer auditState) {
        this.auditState = auditState;
    }
}
