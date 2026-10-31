package com.sunwayworld.emc.module.mdm.invoices.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.Persistable;
import com.sunwayworld.framework.support.domain.AbstractPersistable;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * 回款记录
 * 
 * @author liuyz@sunwayworld.com
 * @date 2021-03-10
 */
@Table("T_EMC_RETURNED")
public class EmcReturnedBean extends AbstractPersistable<Long> implements Persistable<Long> {

    @Transient
    private static final long serialVersionUID = -82566196618711043L;

    @Id
    private Long id;// 主键
    private Long invoiceId;// 开票记录id
    private String invoiceAmount;// 开票金额
    private String returnedAmount;// 本次汇款金额
    private String uncollectedAmount;// 未回款金额
    private String makeUserId;// 回款人id
    private String makeUser;// 回款人
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime makeTime;// 制单时间
    private String remark;// 备注
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

    public Long getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(Long invoiceId) {
        this.invoiceId = invoiceId;
    }

    public String getInvoiceAmount() {
        return invoiceAmount;
    }

    public void setInvoiceAmount(String invoiceAmount) {
        this.invoiceAmount = invoiceAmount;
    }

    public String getReturnedAmount() {
        return returnedAmount;
    }

    public void setReturnedAmount(String returnedAmount) {
        this.returnedAmount = returnedAmount;
    }

    public String getUncollectedAmount() {
        return uncollectedAmount;
    }

    public void setUncollectedAmount(String uncollectedAmount) {
        this.uncollectedAmount = uncollectedAmount;
    }

    public String getMakeUserId() {
        return makeUserId;
    }

    public void setMakeUserId(String makeUserId) {
        this.makeUserId = makeUserId;
    }

    public String getMakeUser() {
        return makeUser;
    }

    public void setMakeUser(String makeUser) {
        this.makeUser = makeUser;
    }

    public LocalDateTime getMakeTime() {
        return makeTime;
    }

    public void setMakeTime(LocalDateTime makeTime) {
        this.makeTime = makeTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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
