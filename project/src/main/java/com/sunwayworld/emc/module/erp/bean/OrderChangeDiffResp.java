package com.sunwayworld.emc.module.erp.bean;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/** 订单变更记录数据详情 */
public class OrderChangeDiffResp {

    /** 申请id */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long applyGbId;

    /** 申请类型 */
    private Integer auditType;

    /** 是否合并报告 */
    private Boolean bMergeReport;

    /** 是否拆分报告 */
    private Boolean bSplitReport;

    /** 变更金额 */
    private Long changeFee;

    /** 变更销售报价金额 */
    private Long changeSaleFee;

    /** 联系人信息 */
    private CustomerContactDto contact;

    /** 联系人编号 */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long contactGbId;

    /** 商户模板gbId */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long customTemplateGbId;

    /** 自定义模板信息 */
    private ContractCustomTemplateInfo customTemplateView;

    /** 预计出报告时间 */
    private Long eUploadReportTWhen;

    /** 英文名称 */
    private String enName;

    /** 英文-报告抬头公司地址 */
    private String enReportCompanyAddr;

    /** 英文-报告抬头公司地址类型 */
    private Integer enReportCompanyAddrType;

    /** 英文-报告抬头 */
    private String enReportTitle;

    /** 英文-报告抬头类型 */
    private Integer enReportTitleType;

    /** 唯一标识 */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long gbId;

    /** 合同合并的订单 */
    private List<String> mergeOrders;

    /** 中文名称 */
    private String name;

    /** 备注 */
    private String note;

    /** 报告发送额外字段 */
    private List<AdditionalReportInfo> orderAdditionalReports;

    /** 订单gbId */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long orderGbId;

    /** 订单自定义模板数据 */
    private Map<String, String> orderValue;

    /** 展示到款信息 */
    private PaymentInfoDto paymentInfo;

    /** 服务周期 */
    private Integer period;

    /** 周期类型 */
    private Integer periodType;

    /** 报告资质 */
    private Integer qualificationType;

    /** 收件人 */
    private String recipient;

    /** 收件人电话 */
    private String recipientPhone;

    /** 订单备注 */
    private String remarks;

    /** 报告公司地址 */
    private AddressViewModel reportCompanyAddr;

    /** 中文-报告抬头公司地址类型 */
    private Integer reportCompanyAddrType;

    /** 报告寄送地址 */
    private AddressViewModel reportSendAddr;

    /** 报告寄送地址类型 */
    private Integer reportSendAddrType;

    /** 报告抬头 */
    private String reportTitle;

    /** 报告抬头类型 */
    private Integer reportTitleType;

    /** 样品信息 */
    private List<OrderChangeSampleInfo> samples;

    /** 拆分的报告信息 */
    private List<OrderSampleChangeReportInfo> splitReports;

    /** 创建时间 */
    private Long tWhen;

    /** 采样方式 这个不能更改 */
    private Integer samplingType;

    /** 报告类型 */
    private Integer reportType;

    /** 报告费 */
    private Long reportFee;

    /** 保密度 */
    private Integer securityLevel;

    /** 父级行业群编号 */
    private String parentIndustryCode;

    /** 父级行业群名称 */
    private String parentIndustryName;

    /** 父级行业群唯一标识 */
    private String parentIndustryGbId;

    /** 中间行业群编号 */
    private String currentIndustryCode;

    /** 中间行业群名称 */
    private String currentIndustryName;

    /** 中间行业群唯一标识 */
    private String currentIndustryGbId;

    /** 子级行业群编号 */
    private String childIndustryCode;

    /** 子级行业群名称 */
    private String childIndustryName;

    /** 子级行业群唯一标识 */
    private String childIndustryGbId;

    /** 其他费用列表 */
    private List<SaveContractOrderOtherFeeReq> otherFeeList;

    /** 订单总工分 */
    private String workPoint;

    public Integer getPeriodType() {
        return periodType;
    }

    public void setPeriodType(Integer periodType) {
        this.periodType = periodType;
    }

    public Long getApplyGbId() {
        return applyGbId;
    }

    public void setApplyGbId(Long applyGbId) {
        this.applyGbId = applyGbId;
    }

    public Integer getAuditType() {
        return auditType;
    }

    public void setAuditType(Integer auditType) {
        this.auditType = auditType;
    }

    public Boolean getbMergeReport() {
        return bMergeReport;
    }

    public void setbMergeReport(Boolean bMergeReport) {
        this.bMergeReport = bMergeReport;
    }

    public Boolean getbSplitReport() {
        return bSplitReport;
    }

    public void setbSplitReport(Boolean bSplitReport) {
        this.bSplitReport = bSplitReport;
    }

    public Long getChangeFee() {
        return changeFee;
    }

    public void setChangeFee(Long changeFee) {
        this.changeFee = changeFee;
    }

    public Long getChangeSaleFee() {
        return changeSaleFee;
    }

    public void setChangeSaleFee(Long changeSaleFee) {
        this.changeSaleFee = changeSaleFee;
    }

    public CustomerContactDto getContact() {
        return contact;
    }

    public void setContact(CustomerContactDto contact) {
        this.contact = contact;
    }

    public Long getContactGbId() {
        return contactGbId;
    }

    public void setContactGbId(Long contactGbId) {
        this.contactGbId = contactGbId;
    }

    public Long getCustomTemplateGbId() {
        return customTemplateGbId;
    }

    public void setCustomTemplateGbId(Long customTemplateGbId) {
        this.customTemplateGbId = customTemplateGbId;
    }

    public ContractCustomTemplateInfo getCustomTemplateView() {
        return customTemplateView;
    }

    public void setCustomTemplateView(ContractCustomTemplateInfo customTemplateView) {
        this.customTemplateView = customTemplateView;
    }

    public Long geteUploadReportTWhen() {
        return eUploadReportTWhen;
    }

    public void seteUploadReportTWhen(Long eUploadReportTWhen) {
        this.eUploadReportTWhen = eUploadReportTWhen;
    }

    public String getEnName() {
        return enName;
    }

    public void setEnName(String enName) {
        this.enName = enName;
    }

    public String getEnReportCompanyAddr() {
        return enReportCompanyAddr;
    }

    public void setEnReportCompanyAddr(String enReportCompanyAddr) {
        this.enReportCompanyAddr = enReportCompanyAddr;
    }

    public Integer getEnReportCompanyAddrType() {
        return enReportCompanyAddrType;
    }

    public void setEnReportCompanyAddrType(Integer enReportCompanyAddrType) {
        this.enReportCompanyAddrType = enReportCompanyAddrType;
    }

    public String getEnReportTitle() {
        return enReportTitle;
    }

    public void setEnReportTitle(String enReportTitle) {
        this.enReportTitle = enReportTitle;
    }

    public Integer getEnReportTitleType() {
        return enReportTitleType;
    }

    public void setEnReportTitleType(Integer enReportTitleType) {
        this.enReportTitleType = enReportTitleType;
    }

    public Long getGbId() {
        return gbId;
    }

    public void setGbId(Long gbId) {
        this.gbId = gbId;
    }

    public List<String> getMergeOrders() {
        return mergeOrders;
    }

    public void setMergeOrders(List<String> mergeOrders) {
        this.mergeOrders = mergeOrders;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public List<AdditionalReportInfo> getOrderAdditionalReports() {
        return orderAdditionalReports;
    }

    public void setOrderAdditionalReports(List<AdditionalReportInfo> orderAdditionalReports) {
        this.orderAdditionalReports = orderAdditionalReports;
    }

    public Long getOrderGbId() {
        return orderGbId;
    }

    public void setOrderGbId(Long orderGbId) {
        this.orderGbId = orderGbId;
    }

    public Map<String, String> getOrderValue() {
        return orderValue;
    }

    public void setOrderValue(Map<String, String> orderValue) {
        this.orderValue = orderValue;
    }

    public PaymentInfoDto getPaymentInfo() {
        return paymentInfo;
    }

    public void setPaymentInfo(PaymentInfoDto paymentInfo) {
        this.paymentInfo = paymentInfo;
    }

    public Integer getPeriod() {
        return period;
    }

    public void setPeriod(Integer period) {
        this.period = period;
    }

    public Integer getQualificationType() {
        return qualificationType;
    }

    public void setQualificationType(Integer qualificationType) {
        this.qualificationType = qualificationType;
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

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public AddressViewModel getReportCompanyAddr() {
        return reportCompanyAddr;
    }

    public void setReportCompanyAddr(AddressViewModel reportCompanyAddr) {
        this.reportCompanyAddr = reportCompanyAddr;
    }

    public Integer getReportCompanyAddrType() {
        return reportCompanyAddrType;
    }

    public void setReportCompanyAddrType(Integer reportCompanyAddrType) {
        this.reportCompanyAddrType = reportCompanyAddrType;
    }

    public AddressViewModel getReportSendAddr() {
        return reportSendAddr;
    }

    public void setReportSendAddr(AddressViewModel reportSendAddr) {
        this.reportSendAddr = reportSendAddr;
    }

    public Integer getReportSendAddrType() {
        return reportSendAddrType;
    }

    public void setReportSendAddrType(Integer reportSendAddrType) {
        this.reportSendAddrType = reportSendAddrType;
    }

    public String getReportTitle() {
        return reportTitle;
    }

    public void setReportTitle(String reportTitle) {
        this.reportTitle = reportTitle;
    }

    public Integer getReportTitleType() {
        return reportTitleType;
    }

    public void setReportTitleType(Integer reportTitleType) {
        this.reportTitleType = reportTitleType;
    }

    public List<OrderChangeSampleInfo> getSamples() {
        return samples;
    }

    public void setSamples(List<OrderChangeSampleInfo> samples) {
        this.samples = samples;
    }

    public List<OrderSampleChangeReportInfo> getSplitReports() {
        return splitReports;
    }

    public void setSplitReports(List<OrderSampleChangeReportInfo> splitReports) {
        this.splitReports = splitReports;
    }

    public Long gettWhen() {
        return tWhen;
    }

    public void settWhen(Long tWhen) {
        this.tWhen = tWhen;
    }

    public Integer getSamplingType() {
        return samplingType;
    }

    public void setSamplingType(Integer samplingType) {
        this.samplingType = samplingType;
    }

    public Integer getReportType() {
        return reportType;
    }

    public void setReportType(Integer reportType) {
        this.reportType = reportType;
    }

    public Long getReportFee() {
        return reportFee;
    }

    public void setReportFee(Long reportFee) {
        this.reportFee = reportFee;
    }

    public Integer getSecurityLevel() {
        return securityLevel;
    }

    public void setSecurityLevel(Integer securityLevel) {
        this.securityLevel = securityLevel;
    }

    public String getParentIndustryCode() {
        return parentIndustryCode;
    }

    public void setParentIndustryCode(String parentIndustryCode) {
        this.parentIndustryCode = parentIndustryCode;
    }

    public String getParentIndustryName() {
        return parentIndustryName;
    }

    public void setParentIndustryName(String parentIndustryName) {
        this.parentIndustryName = parentIndustryName;
    }

    public String getParentIndustryGbId() {
        return parentIndustryGbId;
    }

    public void setParentIndustryGbId(String parentIndustryGbId) {
        this.parentIndustryGbId = parentIndustryGbId;
    }

    public String getCurrentIndustryCode() {
        return currentIndustryCode;
    }

    public void setCurrentIndustryCode(String currentIndustryCode) {
        this.currentIndustryCode = currentIndustryCode;
    }

    public String getCurrentIndustryName() {
        return currentIndustryName;
    }

    public void setCurrentIndustryName(String currentIndustryName) {
        this.currentIndustryName = currentIndustryName;
    }

    public String getCurrentIndustryGbId() {
        return currentIndustryGbId;
    }

    public void setCurrentIndustryGbId(String currentIndustryGbId) {
        this.currentIndustryGbId = currentIndustryGbId;
    }

    public String getChildIndustryCode() {
        return childIndustryCode;
    }

    public void setChildIndustryCode(String childIndustryCode) {
        this.childIndustryCode = childIndustryCode;
    }

    public String getChildIndustryName() {
        return childIndustryName;
    }

    public void setChildIndustryName(String childIndustryName) {
        this.childIndustryName = childIndustryName;
    }

    public String getChildIndustryGbId() {
        return childIndustryGbId;
    }

    public void setChildIndustryGbId(String childIndustryGbId) {
        this.childIndustryGbId = childIndustryGbId;
    }

    public List<SaveContractOrderOtherFeeReq> getOtherFeeList() {
        return otherFeeList;
    }

    public void setOtherFeeList(List<SaveContractOrderOtherFeeReq> otherFeeList) {
        this.otherFeeList = otherFeeList;
    }

    public String getWorkPoint() {
        return workPoint;
    }

    public void setWorkPoint(String workPoint) {
        this.workPoint = workPoint;
    }

    @Override
    public String toString() {
        return "OrderChangeDiffResp{" +
                "applyGbId=" + applyGbId +
                ", auditType=" + auditType +
                ", bMergeReport=" + bMergeReport +
                ", bSplitReport=" + bSplitReport +
                ", changeFee=" + changeFee +
                ", changeSaleFee=" + changeSaleFee +
                ", contact=" + contact +
                ", contactGbId=" + contactGbId +
                ", customTemplateGbId=" + customTemplateGbId +
                ", customTemplateView=" + customTemplateView +
                ", eUploadReportTWhen=" + eUploadReportTWhen +
                ", enName='" + enName + '\'' +
                ", enReportCompanyAddr='" + enReportCompanyAddr + '\'' +
                ", enReportCompanyAddrType=" + enReportCompanyAddrType +
                ", enReportTitle='" + enReportTitle + '\'' +
                ", enReportTitleType=" + enReportTitleType +
                ", gbId=" + gbId +
                ", mergeOrders=" + mergeOrders +
                ", name='" + name + '\'' +
                ", note='" + note + '\'' +
                ", orderAdditionalReports=" + orderAdditionalReports +
                ", orderGbId=" + orderGbId +
                ", orderValue=" + orderValue +
                ", paymentInfo=" + paymentInfo +
                ", period=" + period +
                ", qualificationType=" + qualificationType +
                ", recipient='" + recipient + '\'' +
                ", recipientPhone='" + recipientPhone + '\'' +
                ", remarks='" + remarks + '\'' +
                ", reportCompanyAddr=" + reportCompanyAddr +
                ", reportCompanyAddrType=" + reportCompanyAddrType +
                ", reportSendAddr=" + reportSendAddr +
                ", reportSendAddrType=" + reportSendAddrType +
                ", reportTitle='" + reportTitle + '\'' +
                ", reportTitleType=" + reportTitleType +
                ", samples=" + samples +
                ", splitReports=" + splitReports +
                ", tWhen=" + tWhen +
                ", samplingType=" + samplingType +
                ", reportType=" + reportType +
                ", reportFee=" + reportFee +
                ", securityLevel=" + securityLevel +
                ", parentIndustryCode='" + parentIndustryCode + '\'' +
                ", parentIndustryName='" + parentIndustryName + '\'' +
                ", parentIndustryGbId='" + parentIndustryGbId + '\'' +
                ", currentIndustryCode='" + currentIndustryCode + '\'' +
                ", currentIndustryName='" + currentIndustryName + '\'' +
                ", currentIndustryGbId='" + currentIndustryGbId + '\'' +
                ", childIndustryCode='" + childIndustryCode + '\'' +
                ", childIndustryName='" + childIndustryName + '\'' +
                ", childIndustryGbId='" + childIndustryGbId + '\'' +
                ", otherFeeList=" + otherFeeList +
                ", workPoint='" + workPoint + '\'' +
                '}';
    }
}
