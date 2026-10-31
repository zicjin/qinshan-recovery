// 2026-09-24 从生产字节码反编译恢复（CFR 0.152）：生产新增类，历史源码缺失
package com.sunwayworld.emc.module.mdm.oa.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import com.sunwayworld.framework.support.domain.Insertable;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

@Table(value="T_EMC_OA_BANK")
public class EmcOaBankBean
extends AbstractInsertable<Long>
implements Insertable<Long> {
    @Transient
    private static final long serialVersionUID = -82566196618711043L;
    @Id
    private Long id;
    private String interbanknumber;
    private String accountbankno;
    private String bankdeposit;
    private String accountbank;
    private String bankname;
    private String titletype;
    private String clientCode;
    private String createdById;
    private String createdByName;
    @JSONField(format="yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;
    private String createdByOrgId;
    private String createdByOrgName;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getInterbanknumber() {
        return this.interbanknumber;
    }

    public void setInterbanknumber(String interbanknumber) {
        this.interbanknumber = interbanknumber;
    }

    public String getAccountbankno() {
        return this.accountbankno;
    }

    public void setAccountbankno(String accountbankno) {
        this.accountbankno = accountbankno;
    }

    public String getBankdeposit() {
        return this.bankdeposit;
    }

    public void setBankdeposit(String bankdeposit) {
        this.bankdeposit = bankdeposit;
    }

    public String getAccountbank() {
        return this.accountbank;
    }

    public void setAccountbank(String accountbank) {
        this.accountbank = accountbank;
    }

    public String getBankname() {
        return this.bankname;
    }

    public void setBankname(String bankname) {
        this.bankname = bankname;
    }

    public String getTitletype() {
        return this.titletype;
    }

    public void setTitletype(String titletype) {
        this.titletype = titletype;
    }

    public String getClientCode() {
        return this.clientCode;
    }

    public void setClientCode(String clientCode) {
        this.clientCode = clientCode;
    }

    @Override
    public String getCreatedById() {
        return this.createdById;
    }

    @Override
    public void setCreatedById(String createdById) {
        this.createdById = createdById;
    }

    @Override
    public String getCreatedByName() {
        return this.createdByName;
    }

    @Override
    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    @Override
    public LocalDateTime getCreatedTime() {
        return this.createdTime;
    }

    @Override
    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    @Override
    public String getCreatedByOrgId() {
        return this.createdByOrgId;
    }

    @Override
    public void setCreatedByOrgId(String createdByOrgId) {
        this.createdByOrgId = createdByOrgId;
    }

    @Override
    public String getCreatedByOrgName() {
        return this.createdByOrgName;
    }

    @Override
    public void setCreatedByOrgName(String createdByOrgName) {
        this.createdByOrgName = createdByOrgName;
    }
}
