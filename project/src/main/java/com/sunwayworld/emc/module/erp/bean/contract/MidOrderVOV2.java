package com.sunwayworld.emc.module.erp.bean.contract;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.google.common.collect.Maps;


public class MidOrderVOV2 implements Serializable {

    @JsonSerialize(using = ToStringSerializer.class)
    /** 订单gbId */
    private Long gbId;

    /** 周期 */
    private Integer period;

    /** 周期类型", dataType = "com.jiancetech.business.model.enumModel.OrderPeriodType */
    private Integer periodType;

    /** 资质", dataType = "com.jiancetech.business.model.enumModel.Qualification */
    private Integer qualificationType;

    /** 额外报告字段类型 */
    private List<MidOrderAdditionalReportDTO> additionalReports;

    /** 报告抬头公司地址类型", dataType = "com.jiancetech.business.model.enumModel.DelegateAddrType */
    private Integer reportCompanyAddrType;

    @JsonSerialize(using = ToStringSerializer.class)
    /** 报告寄送地址id */
    private Long reportSendAddrId;

    /** 报告寄送地址 */
    private String reportSendAddr;

    /** 报告寄送地址Model */
    private MidAddressDTO reportSendAddrModel;

    /** 报告抬头类型", dataType = "com.jiancetech.business.model.enumModel.reportTitleType */
    private Integer reportTitleType;

    /** 报告抬头 */
    private String reportTitle;

    /** 英文-报告抬头类型", dataType = "com.jiancetech.business.model.enumModel.TitleType */
    private Integer enReportTitleType;

    /** 英文-报告抬头 */
    private String enReportTitle;

    /** 报告抬头公司地址类型", dataType = "com.jiancetech.business.model.enumModel.DelegateAddrType */
    private Integer reportSendAddrType;

    @JsonSerialize(using = ToStringSerializer.class)
    /** 报告抬头公司地址Id */
    private Long reportCompanyAddrId;

    /** 报告抬头公司地址 */
    private String reportCompanyAddr;

    /** 英文-报告抬头公司地址类型", dataType = "com.jiancetech.business.model.enumModel.DelegateAddrType */
    private Integer enReportCompanyAddrType;

    /** 英文-报告抬头公司地址 */
    private String enReportCompanyAddr;

    /** 订单已付款 */
    private boolean paid;

    /** 订单已寄样 */
    private boolean sampleSent;

    /** 订单已出报告 */
    private boolean reportAvailable;

    @JsonSerialize(using = ToStringSerializer.class)
    /** 对应的orgOrderGbId */
    private Long orgOrderGbId;

    @JsonSerialize(using = ToStringSerializer.class)
    /** 合同订单业务类型id */
    private Long merchantContractBusinessSettingsId;

    /** 订单编码 */
    private String orderNumber;

    /** 订单备注 */
    private String remarks;

    /** 收件人 */
    private String recipient;

    /** 收件人联系电话 */
    private String recipientPhone;

    /** 订单自定义模板数据, 根据合同选择的模板自定义数据, key:字段id, value:字段值 */
    private Map<String, String> orderValue = Maps.newHashMap();

    /** 订单创建时间 */
    private Long tWhen;

    /** 交付物类型 */
    private Integer deliverableType;

    //====== 二开 ======

    /** 样品 */
    private List<MidSampleVOV2Ext> samples;

    public Integer getDeliverableType() {
        return deliverableType;
    }

    public void setDeliverableType(Integer deliverableType) {
        this.deliverableType = deliverableType;
    }

    public Long getGbId() {
        return gbId;
    }

    public void setGbId(Long gbId) {
        this.gbId = gbId;
    }

    public Integer getPeriod() {
        return period;
    }

    public void setPeriod(Integer period) {
        this.period = period;
    }

    public Integer getPeriodType() {
        return periodType;
    }

    public void setPeriodType(Integer periodType) {
        this.periodType = periodType;
    }

    public Integer getQualificationType() {
        return qualificationType;
    }

    public void setQualificationType(Integer qualificationType) {
        this.qualificationType = qualificationType;
    }

    public List<MidOrderAdditionalReportDTO> getAdditionalReports() {
        return additionalReports;
    }

    public void setAdditionalReports(List<MidOrderAdditionalReportDTO> additionalReports) {
        this.additionalReports = additionalReports;
    }

    public Integer getReportCompanyAddrType() {
        return reportCompanyAddrType;
    }

    public void setReportCompanyAddrType(Integer reportCompanyAddrType) {
        this.reportCompanyAddrType = reportCompanyAddrType;
    }

    public Long getReportSendAddrId() {
        return reportSendAddrId;
    }

    public void setReportSendAddrId(Long reportSendAddrId) {
        this.reportSendAddrId = reportSendAddrId;
    }

    public String getReportSendAddr() {
        return reportSendAddr;
    }

    public void setReportSendAddr(String reportSendAddr) {
        this.reportSendAddr = reportSendAddr;
    }

    public Integer getReportTitleType() {
        return reportTitleType;
    }

    public void setReportTitleType(Integer reportTitleType) {
        this.reportTitleType = reportTitleType;
    }

    public String getReportTitle() {
        return reportTitle;
    }

    public void setReportTitle(String reportTitle) {
        this.reportTitle = reportTitle;
    }

    public String getEnReportTitle() {
        return enReportTitle;
    }

    public void setEnReportTitle(String enReportTitle) {
        this.enReportTitle = enReportTitle;
    }

    public Integer getReportSendAddrType() {
        return reportSendAddrType;
    }

    public void setReportSendAddrType(Integer reportSendAddrType) {
        this.reportSendAddrType = reportSendAddrType;
    }

    public Long getReportCompanyAddrId() {
        return reportCompanyAddrId;
    }

    public void setReportCompanyAddrId(Long reportCompanyAddrId) {
        this.reportCompanyAddrId = reportCompanyAddrId;
    }

    public String getReportCompanyAddr() {
        return reportCompanyAddr;
    }

    public void setReportCompanyAddr(String reportCompanyAddr) {
        this.reportCompanyAddr = reportCompanyAddr;
    }

    public Integer getEnReportCompanyAddrType() {
        return enReportCompanyAddrType;
    }

    public void setEnReportCompanyAddrType(Integer enReportCompanyAddrType) {
        this.enReportCompanyAddrType = enReportCompanyAddrType;
    }

    public String getEnReportCompanyAddr() {
        return enReportCompanyAddr;
    }

    public void setEnReportCompanyAddr(String enReportCompanyAddr) {
        this.enReportCompanyAddr = enReportCompanyAddr;
    }

    public List<MidSampleVOV2Ext> getSamples() {
        return samples;
    }

    public void setSamples(List<MidSampleVOV2Ext> samples) {
        this.samples = samples;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    public boolean isSampleSent() {
        return sampleSent;
    }

    public void setSampleSent(boolean sampleSent) {
        this.sampleSent = sampleSent;
    }

    public boolean isReportAvailable() {
        return reportAvailable;
    }

    public void setReportAvailable(boolean reportAvailable) {
        this.reportAvailable = reportAvailable;
    }

    public Long getOrgOrderGbId() {
        return orgOrderGbId;
    }

    public void setOrgOrderGbId(Long orgOrderGbId) {
        this.orgOrderGbId = orgOrderGbId;
    }

    public Long getMerchantContractBusinessSettingsId() {
        return merchantContractBusinessSettingsId;
    }

    public void setMerchantContractBusinessSettingsId(Long merchantContractBusinessSettingsId) {
        this.merchantContractBusinessSettingsId = merchantContractBusinessSettingsId;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getRecipientPhone() {
        return recipientPhone;
    }

    public void setRecipientPhone(String recipientPhone) {
        this.recipientPhone = recipientPhone;
    }

    public Map<String, String> getOrderValue() {
        return orderValue;
    }

    public void setOrderValue(Map<String, String> orderValue) {
        this.orderValue = orderValue;
    }

    public Integer getEnReportTitleType() {
        return enReportTitleType;
    }

    public void setEnReportTitleType(Integer enReportTitleType) {
        this.enReportTitleType = enReportTitleType;
    }

    public Long gettWhen() {
        return tWhen;
    }

    public void settWhen(Long tWhen) {
        this.tWhen = tWhen;
    }

    public MidAddressDTO getReportSendAddrModel() {
        return reportSendAddrModel;
    }

    public void setReportSendAddrModel(MidAddressDTO reportSendAddrModel) {
        this.reportSendAddrModel = reportSendAddrModel;
    }
}
