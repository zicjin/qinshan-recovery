package com.sunwayworld.emc.module.bp.report.report.bean;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.support.domain.AbstractInsertable;

/**
 * @author zhengxy@sunwayworld.com 2023年10月26日
 */
@Table("T_EMC_REPORT_SEND_INFO")
public class EmcReportSendInfoBean extends AbstractInsertable<Long> {

    private static final long serialVersionUID = -4138808799946168737L;

    @Id
    private Long id;  // 主键

    private Long projId; // 项目ID

    private String needCnReport; // 是否需要中文电子版报告
    private Integer cnReportCnt; // 中文纸质版报告份数
    private String needEnReport; // 是否需要英文电子版报告
    private Integer enReportCnt; // 英文纸质版报告份数
    private String needCnEnReport; // 是否需要中英文电子版报告
    private Integer cnEnReportCnt; // 中英文纸质版报告份数

    private String sendToQuoter; // 是否抄送给跟单员
    private String sendToSaler; // 是否抄送给销售
    private String sendEmail; // 发送邮箱
    private String expressType; // 物流公司
    private String expressNo; // 物流单号
    private String remark; // 备注

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getProjId() {
        return projId;
    }

    public void setProjId(Long projId) {
        this.projId = projId;
    }

    public String getNeedCnReport() {
        return needCnReport;
    }

    public void setNeedCnReport(String needCnReport) {
        this.needCnReport = needCnReport;
    }

    public Integer getCnReportCnt() {
        return cnReportCnt;
    }

    public void setCnReportCnt(Integer cnReportCnt) {
        this.cnReportCnt = cnReportCnt;
    }

    public String getNeedEnReport() {
        return needEnReport;
    }

    public void setNeedEnReport(String needEnReport) {
        this.needEnReport = needEnReport;
    }

    public Integer getEnReportCnt() {
        return enReportCnt;
    }

    public void setEnReportCnt(Integer enReportCnt) {
        this.enReportCnt = enReportCnt;
    }

    public String getNeedCnEnReport() {
        return needCnEnReport;
    }

    public void setNeedCnEnReport(String needCnEnReport) {
        this.needCnEnReport = needCnEnReport;
    }

    public Integer getCnEnReportCnt() {
        return cnEnReportCnt;
    }

    public void setCnEnReportCnt(Integer cnEnReportCnt) {
        this.cnEnReportCnt = cnEnReportCnt;
    }

    public String getSendToQuoter() {
        return sendToQuoter;
    }

    public void setSendToQuoter(String sendToQuoter) {
        this.sendToQuoter = sendToQuoter;
    }

    public String getSendToSaler() {
        return sendToSaler;
    }

    public void setSendToSaler(String sendToSaler) {
        this.sendToSaler = sendToSaler;
    }

    public String getSendEmail() {
        return sendEmail;
    }

    public void setSendEmail(String sendEmail) {
        this.sendEmail = sendEmail;
    }

    public String getExpressType() {
        return expressType;
    }

    public void setExpressType(String expressType) {
        this.expressType = expressType;
    }

    public String getExpressNo() {
        return expressNo;
    }

    public void setExpressNo(String expressNo) {
        this.expressNo = expressNo;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
