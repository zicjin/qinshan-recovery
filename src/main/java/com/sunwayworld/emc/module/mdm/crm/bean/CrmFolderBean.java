package com.sunwayworld.emc.module.mdm.crm.bean;

import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;

import java.time.LocalDate;

/**
 * @author zhengxy@sunwayworld.com 2021年11月01日
 */
public class CrmFolderBean  extends AbstractInsertable<String> {

    @Transient
    private static final long serialVersionUID = -3460439329798805134L;

    private String id;
    /** 项目ID */
    private String projId;
    /** 合同ID */
    private String contractId;
    /** 领域，CRM设置的固定值 */
    private String domainId;
    /** 项目编号 */
    private String projNo;
    /** 项目金额 */
    private Double projAmount;
    /** 已核销金额 */
    private Double writeOffAmount;
    /** 完成日期 */
    private LocalDate finishDate;
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProjId() {
        return projId;
    }

    public void setProjId(String projId) {
        this.projId = projId;
    }

    public String getContractId() {
        return contractId;
    }

    public void setContractId(String contractId) {
        this.contractId = contractId;
    }

    public String getDomainId() {
        return domainId;
    }

    public void setDomainId(String domainId) {
        this.domainId = domainId;
    }

    public String getProjNo() {
        return projNo;
    }

    public void setProjNo(String projNo) {
        this.projNo = projNo;
    }

    public Double getProjAmount() {
        return projAmount;
    }

    public void setProjAmount(Double projAmount) {
        this.projAmount = projAmount;
    }

    public Double getWriteOffAmount() {
        return writeOffAmount;
    }

    public void setWriteOffAmount(Double writeOffAmount) {
        this.writeOffAmount = writeOffAmount;
    }

    public LocalDate getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(LocalDate finishDate) {
        this.finishDate = finishDate;
    }

}
