package com.sunwayworld.emc.module.erp.bean;

import java.io.Serializable;

/**
 * @author zhengxy@sunwayworld.com 2023年10月09日
 */
public class MidOrderReportSendDTO implements Serializable {

    /** 是否实际发送邮件 */
    private Boolean bActualSendEmail;

    /** 是否抄送销售 */
    private Boolean bCopyToEngineer;

    /** 是否抄送跟单员 */
    private Boolean bCopyToMerchandiser;

    /** 是否进行业务类型的校验 */
    private Boolean bJudgeSameMerchantContractBusinessSetting;

    /** 是否记录订单流转日志 */
    private Boolean bRecordFlowLog;

    /** 是否无需发送 */
    private Boolean bSendReport;

    /** 是否发送短信 */
    private Boolean bSendSms;

    /** 发送邮件 */
    private String email;

    /** 物流编号 */
    private String expressNo;

    /** 物流类型 */
    private Integer expressType;

    /** 手机号后四位 */
    private String lastFourOfPhone;

    private String merchantId;

    /** 操作人唯一标识 */
    private Long operatorGbId;

    /** 订单id */
    private Long orderGbId;

    /** 备注 */
    private String remark;

    public Boolean getbActualSendEmail() {
        return bActualSendEmail;
    }

    public void setbActualSendEmail(Boolean bActualSendEmail) {
        this.bActualSendEmail = bActualSendEmail;
    }

    public Boolean getbCopyToEngineer() {
        return bCopyToEngineer;
    }

    public void setbCopyToEngineer(Boolean bCopyToEngineer) {
        this.bCopyToEngineer = bCopyToEngineer;
    }

    public Boolean getbCopyToMerchandiser() {
        return bCopyToMerchandiser;
    }

    public void setbCopyToMerchandiser(Boolean bCopyToMerchandiser) {
        this.bCopyToMerchandiser = bCopyToMerchandiser;
    }

    public Boolean getbJudgeSameMerchantContractBusinessSetting() {
        return bJudgeSameMerchantContractBusinessSetting;
    }

    public void setbJudgeSameMerchantContractBusinessSetting(Boolean bJudgeSameMerchantContractBusinessSetting) {
        this.bJudgeSameMerchantContractBusinessSetting = bJudgeSameMerchantContractBusinessSetting;
    }

    public Boolean getbRecordFlowLog() {
        return bRecordFlowLog;
    }

    public void setbRecordFlowLog(Boolean bRecordFlowLog) {
        this.bRecordFlowLog = bRecordFlowLog;
    }

    public Boolean getbSendReport() {
        return bSendReport;
    }

    public void setbSendReport(Boolean bSendReport) {
        this.bSendReport = bSendReport;
    }

    public Boolean getbSendSms() {
        return bSendSms;
    }

    public void setbSendSms(Boolean bSendSms) {
        this.bSendSms = bSendSms;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getExpressNo() {
        return expressNo;
    }

    public void setExpressNo(String expressNo) {
        this.expressNo = expressNo;
    }

    public Integer getExpressType() {
        return expressType;
    }

    public void setExpressType(Integer expressType) {
        this.expressType = expressType;
    }

    public String getLastFourOfPhone() {
        return lastFourOfPhone;
    }

    public void setLastFourOfPhone(String lastFourOfPhone) {
        this.lastFourOfPhone = lastFourOfPhone;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public Long getOperatorGbId() {
        return operatorGbId;
    }

    public void setOperatorGbId(Long operatorGbId) {
        this.operatorGbId = operatorGbId;
    }

    public Long getOrderGbId() {
        return orderGbId;
    }

    public void setOrderGbId(Long orderGbId) {
        this.orderGbId = orderGbId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
