package com.sunwayworld.emc.module.mdm.invoices.bean;

import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import com.alibaba.fastjson.annotation.JSONField;
import java.time.LocalDateTime;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.Auditable;
import com.sunwayworld.framework.support.domain.AbstractAuditable;
import com.sunwayworld.framework.data.annotation.NotNull;

/**
 * 开票记录
 * 
 * @author liuyz@sunwayworld.com
 * @date 2021-03-10
 */
@Table("T_EMC_INVOICE")
public class EmcInvoiceBean extends AbstractAuditable<Long> implements Auditable<Long> {

    @Transient
    private static final long serialVersionUID = -82566196618711043L;

    @Id
    private Long id;// 主键
    private String sysNo;// 系统流水号
    private String invoiceName;// 发票名称
    private String invoiceNo;// 发票编号
    private String invoiceCategory;// 发票类型
    private LocalDate makeDate;// 开票时间
    private String drawerId;// 开票人id
    private String drawer;// 开票人
    private String invoiceAmount;// 开票金额
    private String contractId;// 关联合同id
    private String contractName;// 关联合同
    private String projectId;// 关联项目id
    private String projectName;// 关联项目
    private String doneFlag;// 是否完成回款
    private String auditUser;// 审核人
    private String auditUserId;// 审核人ID
    private String remark;// 备注
    @NotNull(defaultValue = "draft")
    private String processStatus;// 流程状态
    private String createdById;// 制单人编码
    private String createdByName;// 制单人名称
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;// 制单时间
    private String createdByOrgId;// 制单人单位编码
    private String createdByOrgName;// 制单人单位名称

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSysNo() {
        return sysNo;
    }

    public void setSysNo(String sysNo) {
        this.sysNo = sysNo;
    }

    public String getInvoiceName() {
        return invoiceName;
    }

    public void setInvoiceName(String invoiceName) {
        this.invoiceName = invoiceName;
    }

    public String getInvoiceNo() {
        return invoiceNo;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
    }

    public String getInvoiceCategory() {
        return invoiceCategory;
    }

    public void setInvoiceCategory(String invoiceCategory) {
        this.invoiceCategory = invoiceCategory;
    }

    public String getDoneFlag() {
        return doneFlag;
    }

    public void setDoneFlag(String doneFlag) {
        this.doneFlag = doneFlag;
    }

    public LocalDate getMakeDate() {
        return makeDate;
    }

    public void setMakeDate(LocalDate makeDate) {
        this.makeDate = makeDate;
    }

    public String getDrawerId() {
        return drawerId;
    }

    public void setDrawerId(String drawerId) {
        this.drawerId = drawerId;
    }

    public String getDrawer() {
        return drawer;
    }

    public void setDrawer(String drawer) {
        this.drawer = drawer;
    }

    public String getInvoiceAmount() {
        return invoiceAmount;
    }

    public void setInvoiceAmount(String invoiceAmount) {
        this.invoiceAmount = invoiceAmount;
    }

    public String getContractId() {
        return contractId;
    }

    public void setContractId(String contractId) {
        this.contractId = contractId;
    }

    public String getContractName() {
        return contractName;
    }

    public void setContractName(String contractName) {
        this.contractName = contractName;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getRemark() {
        return remark;
    }

    public String getAuditUser() {
        return auditUser;
    }

    public void setAuditUser(String auditUser) {
        this.auditUser = auditUser;
    }

    public String getAuditUserId() {
        return auditUserId;
    }

    public void setAuditUserId(String auditUserId) {
        this.auditUserId = auditUserId;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getProcessStatus() {
        return processStatus;
    }

    public void setProcessStatus(String processStatus) {
        this.processStatus = processStatus;
    }

    public String getCreatedById() {
        return createdById;
    }

    public void setCreatedById(String createdById) {
        this.createdById = createdById;
    }

    public String getCreatedByName() {
        return createdByName;
    }

    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public String getCreatedByOrgId() {
        return createdByOrgId;
    }

    public void setCreatedByOrgId(String createdByOrgId) {
        this.createdByOrgId = createdByOrgId;
    }

    public String getCreatedByOrgName() {
        return createdByOrgName;
    }

    public void setCreatedByOrgName(String createdByOrgName) {
        this.createdByOrgName = createdByOrgName;
    }

}
