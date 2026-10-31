package com.sunwayworld.emc.module.mdm.crm.bean;

import java.time.LocalDate;

import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.NotNull;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;

/**
 * 商机合同表
 * @author zhengxy@sunwayworld.com 2021年08月26日
 */
@Table("T_CRM_OPPORTUNITY_CONTRACT")
public class CrmOpportunityContractBean extends AbstractInsertable<String> {

    @Transient
    private static final long serialVersionUID = -4265838792760868035L;

    @Id
    private String id;  // 主键
    private String contractNo;// 合同编号
    private String opportunityId;// 委托单
    private String entrustUnit;// 委托单位
    private Double contractAmount;// 合同金额
    private LocalDate contractCommencementDate;// 合同开始日期
    private LocalDate contractEndDate;// 合同结束日
    private String signingSite;// 签约地点
    private String performancePlace;// 履约地点
    private Double reportNumber;// 报告份数
    private Long contractNumber;// 合同份数
    private Double partyAContractNumber;// 甲方合同份数
    private Double partyBContractNumber;// 乙方合同份数
    private String entrustedUnit;// 受托单位
    private String entrustedUnitContact;// 受托单位联系人
    private String entrustedUnitContactInformation;// 受托单位联系方式
    private String entrustedUnitEmail;// 受托单位电子邮箱
    private String termsOfPayMent;// 付款方式
    private String reportingDays;// 提交报告天数
    private String appointmentForavisit;// 预约上门天数
    private String supplementClause;// 补充条款
    @NotNull(defaultValue = Constant.NO)
    private String validFlag;// 是否有效
    private String note;// 备注
    private String contractOrderer; //合同下单人
    private Double originalAmount; // 原始金额
    private String financialFlag; // 合同金额修改标识

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public String getContractNo() {
        return contractNo;
    }

    public void setContractNo(String contractNo) {
        this.contractNo = contractNo;
    }

    public String getOpportunityId() {
        return opportunityId;
    }

    public void setOpportunityId(String opportunityId) {
        this.opportunityId = opportunityId;
    }

    public String getEntrustUnit() {
        return entrustUnit;
    }

    public void setEntrustUnit(String entrustUnit) {
        this.entrustUnit = entrustUnit;
    }

    public Double getContractAmount() {
        return contractAmount;
    }

    public void setContractAmount(Double contractAmount) {
        this.contractAmount = contractAmount;
    }

    public LocalDate getContractCommencementDate() {
        return contractCommencementDate;
    }

    public void setContractCommencementDate(LocalDate contractCommencementDate) {
        this.contractCommencementDate = contractCommencementDate;
    }

    public LocalDate getContractEndDate() {
        return contractEndDate;
    }

    public void setContractEndDate(LocalDate contractEndDate) {
        this.contractEndDate = contractEndDate;
    }

    public String getSigningSite() {
        return signingSite;
    }

    public void setSigningSite(String signingSite) {
        this.signingSite = signingSite;
    }

    public String getPerformancePlace() {
        return performancePlace;
    }

    public void setPerformancePlace(String performancePlace) {
        this.performancePlace = performancePlace;
    }

    public Double getReportNumber() {
        return reportNumber;
    }

    public void setReportNumber(Double reportNumber) {
        this.reportNumber = reportNumber;
    }

    public Long getContractNumber() {
        return contractNumber;
    }

    public void setContractNumber(Long contractNumber) {
        this.contractNumber = contractNumber;
    }

    public Double getPartyAContractNumber() {
        return partyAContractNumber;
    }

    public void setPartyAContractNumber(Double partyAContractNumber) {
        this.partyAContractNumber = partyAContractNumber;
    }

    public Double getPartyBContractNumber() {
        return partyBContractNumber;
    }

    public void setPartyBContractNumber(Double partyBContractNumber) {
        this.partyBContractNumber = partyBContractNumber;
    }

    public String getEntrustedUnit() {
        return entrustedUnit;
    }

    public void setEntrustedUnit(String entrustedUnit) {
        this.entrustedUnit = entrustedUnit;
    }

    public String getEntrustedUnitContact() {
        return entrustedUnitContact;
    }

    public void setEntrustedUnitContact(String entrustedUnitContact) {
        this.entrustedUnitContact = entrustedUnitContact;
    }

    public String getEntrustedUnitContactInformation() {
        return entrustedUnitContactInformation;
    }

    public void setEntrustedUnitContactInformation(String entrustedUnitContactInformation) {
        this.entrustedUnitContactInformation = entrustedUnitContactInformation;
    }

    public String getEntrustedUnitEmail() {
        return entrustedUnitEmail;
    }

    public void setEntrustedUnitEmail(String entrustedUnitEmail) {
        this.entrustedUnitEmail = entrustedUnitEmail;
    }

    public String getTermsOfPayMent() {
        return termsOfPayMent;
    }

    public void setTermsOfPayMent(String termsOfPayMent) {
        this.termsOfPayMent = termsOfPayMent;
    }

    public String getReportingDays() {
        return reportingDays;
    }

    public void setReportingDays(String reportingDays) {
        this.reportingDays = reportingDays;
    }

    public String getAppointmentForavisit() {
        return appointmentForavisit;
    }

    public void setAppointmentForavisit(String appointmentForavisit) {
        this.appointmentForavisit = appointmentForavisit;
    }

    public String getSupplementClause() {
        return supplementClause;
    }

    public void setSupplementClause(String supplementClause) {
        this.supplementClause = supplementClause;
    }

    public String getValidFlag() {
        return validFlag;
    }

    public void setValidFlag(String validFlag) {
        this.validFlag = validFlag;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getContractOrderer() {
        return contractOrderer;
    }

    public void setContractOrderer(String contractOrderer) {
        this.contractOrderer = contractOrderer;
    }

    public Double getOriginalAmount() {
        return originalAmount;
    }

    public void setOriginalAmount(Double originalAmount) {
        this.originalAmount = originalAmount;
    }

    public String getFinancialFlag() {
        return financialFlag;
    }

    public void setFinancialFlag(String financialFlag) {
        this.financialFlag = financialFlag;
    }
}
