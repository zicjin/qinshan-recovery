package com.sunwayworld.emc.module.erp.bean;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.google.common.collect.Maps;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.sunwayworld.emc.module.erp.bean.contract.AttachVO;

public class MidOrderDetailVO implements Serializable {

    /** 合同概要 */
    private MidUserContractVO userContractViewModel;

    /** 订单 */
    private Long gbId;

    /** 合同编号 */
    private Long contractGbId;

    /** 实际周期 */
    private Integer actualPeriod;

    /** 委托名称 */
    private String delegateCompany;

    /** 委托单位地址 */
    private String delegateCompanyAddr;

    /** 联系人 */
    private String contact;

    /** 电话号码 */
    private String phoneNum;

    /** 邮箱 */
    private String email;

    /** 周期类型, com.jiancetech.business.model.enumModel.OrderPeriodType */
    private Integer periodType;

    /** 样品余样类型,  com.jiancetech.business.model.enumModel.SampleHandleType*/
    private Integer sampleHandleType;

    /** 样品余样信息 */
    private String sampleHandle;

    /** 资质, com.jiancetech.business.model.enumModel.Qualification*/
    private Integer qualification;

    /** 额外报告信息 */
    private List<MidOrderAdditionalReportDTO> orderReportsViews;

    /** 报告寄送地址 */
    private String reportSendAddr;

    /** 报告抬头 */
    private String reportTitle;

    /** 报告抬头地址 */
    private String reportCompanyAddr;

    /** 英文-报告抬头 */
    private String enReportTitle;

    /** 英文-报告抬头地址 */
    private String enReportCompanyAddr;

    /** 样品 */
    private List<MidSampleDetailVO> samples;

    /** 订单备注 */
    private String remarks;

    /** PO订单备注 */
    private String orgOrderRemarks;

    /** 收件人 */
    private String recipient;

    /** 收件人联系电话 */
    private String recipientPhone;

    /** 订单详情变更纪录 */
    private List<MidOrderChangeVo> orderChangeBriefModels;

    /** 订单编号 */
    private String orderNumber;


    /** 接包方订单编号 */
    private String labOrderNum;

    /** 分包单信息 */
    private MidSubContractVO subContractInfo;

    /** 自定义模板字段 */
    private MidCustomTemplateVO customTemplateView;

    /** 商户模板gbId */
    private Long customTemplateGbId;

    /** 合同自定义模板数据 */
    private Map<Long, String> userContractValue = Maps.newHashMap();

    /** 订单自定义模板数据 */
    private Map<Long, String> orderValue = Maps.newHashMap();

    /** 是否自己做单 */
    private Boolean bOwn;

    /** 是否自动报价 */
    private Boolean bAutoQuote;

    /** 跟单员标识 */
    private Long saleGbId;

    /** 跟单员名称 */
    private String saleName;

    /** 报告状态 */
    private Integer reportStatus;

    /** 创建销售 */
    private String createContractEngineerName;

    /** 销售 */
    private String engineerName;

    /** 客户标签 */
    private List<String> customerTags;

    /** 订单金额 */
    private Long price;

    /** 开始实验时间 */
    private Long startExperimentTime;

    /** 机构标识 */
    private Long orgGbId;

    /** 机构名称 */
    private String orgName;

    /** 预计报告时间 */
    private Long eUploadReportTime;

    /** 合同评审人id */
    private Long reviewGbId;

    /** 合同评审人 */
    private String reviewName;

    /** 业务类型id */
    private Long merchantContractBusinessSettingsId;

    /** 业务类型名称 */
    private String merchantContractBusinessSettingsName;

    /** 业务类型编码 */
    private String merchantContractBusinessSettingsCode;

    /** 合同评审附件 */
    private List<String> reviewAttachments;

    /** 评审id */
    private Long reviewId;

    /** 评审状态 */
    private UserContractReviewAuditStatus reviewAuditStatus;

    /** 评审时间 */
    private Long reviewAuditTime;

    //以下为二开字段
    /** orgOrder单编号 */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long orgOrderGbId;
    /** 二开字段信息 */
    private HJOrderExtraInfoModelExt orderExtraInfoModelExt;

    /** 合同附件信息 */
    private List<AttachVO> userContractAttaches;

    public Long getOrgOrderGbId() {
        return orgOrderGbId;
    }

    public void setOrgOrderGbId(Long orgOrderGbId) {
        this.orgOrderGbId = orgOrderGbId;
    }

    public HJOrderExtraInfoModelExt getOrderExtraInfoModelExt() {
        return orderExtraInfoModelExt;
    }

    public void setOrderExtraInfoModelExt(HJOrderExtraInfoModelExt orderExtraInfoModelExt) {
        this.orderExtraInfoModelExt = orderExtraInfoModelExt;
    }

    public Long getReviewId() {
        return reviewId;
    }

    public void setReviewId(Long reviewId) {
        this.reviewId = reviewId;
    }

    public UserContractReviewAuditStatus getReviewAuditStatus() {
        return reviewAuditStatus;
    }

    public void setReviewAuditStatus(UserContractReviewAuditStatus reviewAuditStatus) {
        this.reviewAuditStatus = reviewAuditStatus;
    }

    public Long getReviewAuditTime() {
        return reviewAuditTime;
    }

    public void setReviewAuditTime(Long reviewAuditTime) {
        this.reviewAuditTime = reviewAuditTime;
    }

    public MidUserContractVO getUserContractViewModel() {
        return userContractViewModel;
    }

    public void setUserContractViewModel(MidUserContractVO userContractViewModel) {
        this.userContractViewModel = userContractViewModel;
    }

    public Long getGbId() {
        return gbId;
    }

    public void setGbId(Long gbId) {
        this.gbId = gbId;
    }

    public Long getContractGbId() {
        return contractGbId;
    }

    public void setContractGbId(Long contractGbId) {
        this.contractGbId = contractGbId;
    }

    public Integer getActualPeriod() {
        return actualPeriod;
    }

    public void setActualPeriod(Integer actualPeriod) {
        this.actualPeriod = actualPeriod;
    }

    public String getDelegateCompany() {
        return delegateCompany;
    }

    public void setDelegateCompany(String delegateCompany) {
        this.delegateCompany = delegateCompany;
    }

    public String getDelegateCompanyAddr() {
        return delegateCompanyAddr;
    }

    public void setDelegateCompanyAddr(String delegateCompanyAddr) {
        this.delegateCompanyAddr = delegateCompanyAddr;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getPeriodType() {
        return periodType;
    }

    public void setPeriodType(Integer periodType) {
        this.periodType = periodType;
    }

    public Integer getSampleHandleType() {
        return sampleHandleType;
    }

    public void setSampleHandleType(Integer sampleHandleType) {
        this.sampleHandleType = sampleHandleType;
    }

    public String getSampleHandle() {
        return sampleHandle;
    }

    public void setSampleHandle(String sampleHandle) {
        this.sampleHandle = sampleHandle;
    }

    public Integer getQualification() {
        return qualification;
    }

    public void setQualification(Integer qualification) {
        this.qualification = qualification;
    }

    public List<MidOrderAdditionalReportDTO> getOrderReportsViews() {
        return orderReportsViews;
    }

    public void setOrderReportsViews(List<MidOrderAdditionalReportDTO> orderReportsViews) {
        this.orderReportsViews = orderReportsViews;
    }

    public String getReportSendAddr() {
        return reportSendAddr;
    }

    public void setReportSendAddr(String reportSendAddr) {
        this.reportSendAddr = reportSendAddr;
    }

    public String getReportTitle() {
        return reportTitle;
    }

    public void setReportTitle(String reportTitle) {
        this.reportTitle = reportTitle;
    }

    public String getReportCompanyAddr() {
        return reportCompanyAddr;
    }

    public void setReportCompanyAddr(String reportCompanyAddr) {
        this.reportCompanyAddr = reportCompanyAddr;
    }

    public String getEnReportTitle() {
        return enReportTitle;
    }

    public void setEnReportTitle(String enReportTitle) {
        this.enReportTitle = enReportTitle;
    }

    public String getEnReportCompanyAddr() {
        return enReportCompanyAddr;
    }

    public void setEnReportCompanyAddr(String enReportCompanyAddr) {
        this.enReportCompanyAddr = enReportCompanyAddr;
    }

    public List<MidSampleDetailVO> getSamples() {
        return samples;
    }

    public void setSamples(List<MidSampleDetailVO> samples) {
        this.samples = samples;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getOrgOrderRemarks() {
        return orgOrderRemarks;
    }

    public void setOrgOrderRemarks(String orgOrderRemarks) {
        this.orgOrderRemarks = orgOrderRemarks;
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

    public List<MidOrderChangeVo> getOrderChangeBriefModels() {
        return orderChangeBriefModels;
    }

    public void setOrderChangeBriefModels(List<MidOrderChangeVo> orderChangeBriefModels) {
        this.orderChangeBriefModels = orderChangeBriefModels;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public MidSubContractVO getSubContractInfo() {
        return subContractInfo;
    }

    public void setSubContractInfo(MidSubContractVO subContractInfo) {
        this.subContractInfo = subContractInfo;
    }

    public MidCustomTemplateVO getCustomTemplateView() {
        return customTemplateView;
    }

    public void setCustomTemplateView(MidCustomTemplateVO customTemplateView) {
        this.customTemplateView = customTemplateView;
    }

    public Long getCustomTemplateGbId() {
        return customTemplateGbId;
    }

    public void setCustomTemplateGbId(Long customTemplateGbId) {
        this.customTemplateGbId = customTemplateGbId;
    }

    public Map<Long, String> getUserContractValue() {
        return userContractValue;
    }

    public void setUserContractValue(Map<Long, String> userContractValue) {
        this.userContractValue = userContractValue;
    }

    public Map<Long, String> getOrderValue() {
        return orderValue;
    }

    public void setOrderValue(Map<Long, String> orderValue) {
        this.orderValue = orderValue;
    }

    public Boolean getbOwn() {
        return bOwn;
    }

    public void setbOwn(Boolean bOwn) {
        this.bOwn = bOwn;
    }

    public Boolean getbAutoQuote() {
        return bAutoQuote;
    }

    public void setbAutoQuote(Boolean bAutoQuote) {
        this.bAutoQuote = bAutoQuote;
    }

    public Long getSaleGbId() {
        return saleGbId;
    }

    public void setSaleGbId(Long saleGbId) {
        this.saleGbId = saleGbId;
    }

    public String getSaleName() {
        return saleName;
    }

    public void setSaleName(String saleName) {
        this.saleName = saleName;
    }

    public Integer getReportStatus() {
        return reportStatus;
    }

    public void setReportStatus(Integer reportStatus) {
        this.reportStatus = reportStatus;
    }

    public String getCreateContractEngineerName() {
        return createContractEngineerName;
    }

    public void setCreateContractEngineerName(String createContractEngineerName) {
        this.createContractEngineerName = createContractEngineerName;
    }

    public List<String> getCustomerTags() {
        return customerTags;
    }

    public void setCustomerTags(List<String> customerTags) {
        this.customerTags = customerTags;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public Long getStartExperimentTime() {
        return startExperimentTime;
    }

    public void setStartExperimentTime(Long startExperimentTime) {
        this.startExperimentTime = startExperimentTime;
    }

    public Long getOrgGbId() {
        return orgGbId;
    }

    public void setOrgGbId(Long orgGbId) {
        this.orgGbId = orgGbId;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public Long geteUploadReportTime() {
        return eUploadReportTime;
    }

    public void seteUploadReportTime(Long eUploadReportTime) {
        this.eUploadReportTime = eUploadReportTime;
    }

    public Long getReviewGbId() {
        return reviewGbId;
    }

    public void setReviewGbId(Long reviewGbId) {
        this.reviewGbId = reviewGbId;
    }

    public String getReviewName() {
        return reviewName;
    }

    public void setReviewName(String reviewName) {
        this.reviewName = reviewName;
    }

    public Long getMerchantContractBusinessSettingsId() {
        return merchantContractBusinessSettingsId;
    }

    public void setMerchantContractBusinessSettingsId(Long merchantContractBusinessSettingsId) {
        this.merchantContractBusinessSettingsId = merchantContractBusinessSettingsId;
    }

    public String getMerchantContractBusinessSettingsName() {
        return merchantContractBusinessSettingsName;
    }

    public void setMerchantContractBusinessSettingsName(String merchantContractBusinessSettingsName) {
        this.merchantContractBusinessSettingsName = merchantContractBusinessSettingsName;
    }

    public String getMerchantContractBusinessSettingsCode() {
        return merchantContractBusinessSettingsCode;
    }

    public void setMerchantContractBusinessSettingsCode(String merchantContractBusinessSettingsCode) {
        this.merchantContractBusinessSettingsCode = merchantContractBusinessSettingsCode;
    }

    public String getEngineerName() {
        return engineerName;
    }

    public void setEngineerName(String engineerName) {
        this.engineerName = engineerName;
    }

    public List<String> getReviewAttachments() {
        return reviewAttachments;
    }

    public void setReviewAttachments(List<String> reviewAttachments) {
        this.reviewAttachments = reviewAttachments;
    }

    public String getLabOrderNum() {
        return labOrderNum;
    }

    public MidOrderDetailVO setLabOrderNum(String labOrderNum) {
        this.labOrderNum = labOrderNum;
        return this;
    }

    public List<AttachVO> getUserContractAttaches() {
        return userContractAttaches;
    }

    public void setUserContractAttaches(List<AttachVO> userContractAttaches) {
        this.userContractAttaches = userContractAttaches;
    }
}
