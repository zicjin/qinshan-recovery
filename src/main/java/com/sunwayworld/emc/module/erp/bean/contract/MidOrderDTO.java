package com.sunwayworld.emc.module.erp.bean.contract;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.google.common.collect.Maps;

/** 订单Dto */
public class MidOrderDTO implements Serializable {

    @JsonSerialize(using = ToStringSerializer.class)
    /** 订单gbId */
    private Long gbId;

    /** 周期 */
    private Integer period;

    /** 周期类型", dataType = "com.jiancetech.business.model.enumModel.OrderPeriodType */
    private Integer periodType;

    /** 周期类型", dataType = "com.jiancetech.business.model.enumModel.DeliverableType */
    private Integer deliverableType;

    /** 资质", dataType = "com.jiancetech.business.model.enumModel.Qualification */
    private Integer qualificationType;

    /** 额外报告字段类型 */
    private List<MidOrderAdditionalReportDTO> additionalReports;

    /** 发送地址类型", dataType = "com.jiancetech.business.model.enumModel.DelegateAddrType */
    private Integer reportSendAddrType;

    /** 报告寄送地址结构化数据 */
    private MidAddressDTO reportSendAddrModel;

    @JsonSerialize(using = ToStringSerializer.class)
    /** 报告寄送地址Id */
    private Long reportSendAddrId;

    /** 报告抬头类型", dataType = "com.jiancetech.business.model.enumModel.TitleType */
    private Integer reportTitleType;

    /** 报告抬头 */
    private String reportTitle;

    /** 报告抬头公司地址类型", dataType = "com.jiancetech.business.model.enumModel.DelegateAddrType */
    private Integer reportCompanyAddrType;

    /** 报告抬头公司地址结构化数据 */
    private MidAddressDTO reportCompanyAddrModel;

    @JsonSerialize(using = ToStringSerializer.class)
    /** 报告抬头公司地址Id */
    private Long reportCompanyAddrId;

    @JsonSerialize(using = ToStringSerializer.class)
    /** 业务类型id */
    private Long merchantContractBusinessSettingsId;

    /** 流水号 */
    private String serialNumber;

    /** 报告抬头公司地址类型-英文", dataType = "com.jiancetech.business.model.enumModel.DelegateAddrType */
    private Integer enReportCompanyAddrType;

    /** 报告抬头公司地址-英文 */
    private String enReportCompanyAddr;

    /** 报告抬头类型-英文", dataType = "com.jiancetech.business.model.enumModel.TitleType */
    private Integer enReportTitleType;

    /** 报告抬头-英文 */
    private String enReportTitle;

    /** 订单备注 */
    private String remarks;

    /** 收件人 */
    private String recipient;

    /** 收件人联系电话 */
    private String recipientPhone;


    /** 额外字段,返回生成的订单和二开订单之间映射关系 */
    private String relateOut;

    /** 前端传递，与报价中订单的uuid一致 */
    private String uuid;

    //========== 样品信息 =========

    /** 订单自定义模板数据, 根据合同选择的模板自定义数据, key:字段id, value:字段值 */
    private Map<String, String> orderValue = Maps.newHashMap();


    /** 样品 */
    private List<MidSampleDTOExt> samples;


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

    public Integer getReportSendAddrType() {
        return reportSendAddrType;
    }

    public void setReportSendAddrType(Integer reportSendAddrType) {
        this.reportSendAddrType = reportSendAddrType;
    }

    public MidAddressDTO getReportSendAddrModel() {
        return reportSendAddrModel;
    }

    public void setReportSendAddrModel(MidAddressDTO reportSendAddrModel) {
        this.reportSendAddrModel = reportSendAddrModel;
    }

    public Long getReportSendAddrId() {
        return reportSendAddrId;
    }

    public void setReportSendAddrId(Long reportSendAddrId) {
        this.reportSendAddrId = reportSendAddrId;
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

    public Integer getReportCompanyAddrType() {
        return reportCompanyAddrType;
    }

    public void setReportCompanyAddrType(Integer reportCompanyAddrType) {
        this.reportCompanyAddrType = reportCompanyAddrType;
    }

    public MidAddressDTO getReportCompanyAddrModel() {
        return reportCompanyAddrModel;
    }

    public void setReportCompanyAddrModel(MidAddressDTO reportCompanyAddrModel) {
        this.reportCompanyAddrModel = reportCompanyAddrModel;
    }

    public Long getReportCompanyAddrId() {
        return reportCompanyAddrId;
    }

    public void setReportCompanyAddrId(Long reportCompanyAddrId) {
        this.reportCompanyAddrId = reportCompanyAddrId;
    }

    public Long getMerchantContractBusinessSettingsId() {
        return merchantContractBusinessSettingsId;
    }

    public void setMerchantContractBusinessSettingsId(Long merchantContractBusinessSettingsId) {
        this.merchantContractBusinessSettingsId = merchantContractBusinessSettingsId;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
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

    public Integer getEnReportTitleType() {
        return enReportTitleType;
    }

    public void setEnReportTitleType(Integer enReportTitleType) {
        this.enReportTitleType = enReportTitleType;
    }

    public String getEnReportTitle() {
        return enReportTitle;
    }

    public void setEnReportTitle(String enReportTitle) {
        this.enReportTitle = enReportTitle;
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

    public String getRelateOut() {
        return relateOut;
    }

    public void setRelateOut(String relateOut) {
        this.relateOut = relateOut;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Integer getDeliverableType() {
        return deliverableType;
    }

    public void setDeliverableType(Integer deliverableType) {
        this.deliverableType = deliverableType;
    }

    public List<MidSampleDTOExt> getSamples() {
        return samples;
    }

    public void setSamples(List<MidSampleDTOExt> samples) {
        this.samples = samples;
    }
}
