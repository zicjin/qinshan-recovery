package com.sunwayworld.emc.module.mdm.contract.bean;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.support.domain.AbstractInsertable;

/**
 * 拆分合同
 * @author zhengxy@sunwayworld.com 2023年11月07日
 */
@Table("T_EMC_SPLIT_CONTRACT")
public class EmcSplitContractBean extends AbstractInsertable<Long> {

    @Id
    private Long id;

    private Long contractId; //主合同ID
    private String erpGbId; //ERP中ID
    private Long price; //价格
    private String businessOpportunityGbId; //ERP商机ID
    private String businessOpportunityName; //ERP商机名称
    private String productionLineGbId; //ERP产品线ID
    private String productionLineName; //ERP产品线名称
    private String industryGbId; //ERP行业ID
    private String industryName; //ERP行业名称
    private String status; //状态
    private String erpContractGbId; //ERP中实际合同ID
    private String erpContractNumber; //ERP实际合同编号

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public String getErpGbId() {
        return erpGbId;
    }

    public void setErpGbId(String erpGbId) {
        this.erpGbId = erpGbId;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public String getBusinessOpportunityGbId() {
        return businessOpportunityGbId;
    }

    public void setBusinessOpportunityGbId(String businessOpportunityGbId) {
        this.businessOpportunityGbId = businessOpportunityGbId;
    }

    public String getBusinessOpportunityName() {
        return businessOpportunityName;
    }

    public void setBusinessOpportunityName(String businessOpportunityName) {
        this.businessOpportunityName = businessOpportunityName;
    }

    public String getProductionLineGbId() {
        return productionLineGbId;
    }

    public void setProductionLineGbId(String productionLineGbId) {
        this.productionLineGbId = productionLineGbId;
    }

    public String getProductionLineName() {
        return productionLineName;
    }

    public void setProductionLineName(String productionLineName) {
        this.productionLineName = productionLineName;
    }

    public String getIndustryGbId() {
        return industryGbId;
    }

    public void setIndustryGbId(String industryGbId) {
        this.industryGbId = industryGbId;
    }

    public String getIndustryName() {
        return industryName;
    }

    public void setIndustryName(String industryName) {
        this.industryName = industryName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getErpContractGbId() {
        return erpContractGbId;
    }

    public void setErpContractGbId(String erpContractGbId) {
        this.erpContractGbId = erpContractGbId;
    }

    public String getErpContractNumber() {
        return erpContractNumber;
    }

    public void setErpContractNumber(String erpContractNumber) {
        this.erpContractNumber = erpContractNumber;
    }
}
