package com.sunwayworld.emc.module.item.reportqueries.bean;

import java.time.LocalDateTime;

import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.format.annotation.DateTimeFormat;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractAuditable;
import com.sunwayworld.framework.support.domain.Auditable;

/**
 * 数据报告查用
 * @author zhangft@sunwayworld.com
 * @date 2019-11-07
 */

@Table("T_EMC_REPORT_QUERY")
public class EmcReportQueryBean extends AbstractAuditable<String> implements Auditable<String> {

    @Transient
    private static final long serialVersionUID = 4051335406733290782L;

    @Id
    private String id;// 主键

    private String queryContent;// 查用内容

    private String queryForm;// 查用形式

    private String purpose;// 用途简述

    private String contact;// 联系人

    private String phone;// 联系电话

    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime submitDate;// 要求报送时间

    private String register;// 登记人

    private String remark;// 备注

    private String createdById;// 制单人编码

    private String createdByName;// 制单人名称

    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;// 制单时间

    private String createdByOrgId;// 制单人单位编码

    private String createdByOrgName;// 制单人单位名称

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQueryContent() {
        return queryContent;
    }

    public void setQueryContent(String queryContent) {
        this.queryContent = queryContent;
    }

    public String getQueryForm() {
        return queryForm;
    }

    public void setQueryForm(String queryForm) {
        this.queryForm = queryForm;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LocalDateTime getSubmitDate() {
        return submitDate;
    }

    public void setSubmitDate(LocalDateTime submitDate) {
        this.submitDate = submitDate;
    }

    public String getRegister() {
        return register;
    }

    public void setRegister(String register) {
        this.register = register;
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
