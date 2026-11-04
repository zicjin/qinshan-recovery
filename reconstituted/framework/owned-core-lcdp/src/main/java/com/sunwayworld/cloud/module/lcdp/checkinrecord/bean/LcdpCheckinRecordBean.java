/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.checkinrecord.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import com.sunwayworld.framework.support.domain.Insertable;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

@Table(value="T_LCDP_CHECKIN_RECORD")
public class LcdpCheckinRecordBean
extends AbstractInsertable<Long>
implements Insertable<Long> {
    @Transient
    private static final long serialVersionUID = -82566196618711043L;
    @Id
    private Long id;
    private String checkinNo;
    private String checkinNote;
    private String checkinStatus;
    private String checkinWay;
    private Long checkoutRecordId;
    private String checkoutRecordNo;
    private String checkoutSysName;
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime checkinTime;
    private String createdById;
    private String createdByName;
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
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

    public String getCheckinNo() {
        return this.checkinNo;
    }

    public void setCheckinNo(String checkinNo) {
        this.checkinNo = checkinNo;
    }

    public String getCheckinNote() {
        return this.checkinNote;
    }

    public void setCheckinNote(String checkinNote) {
        this.checkinNote = checkinNote;
    }

    public String getCheckinStatus() {
        return this.checkinStatus;
    }

    public void setCheckinStatus(String checkinStatus) {
        this.checkinStatus = checkinStatus;
    }

    public String getCheckinWay() {
        return this.checkinWay;
    }

    public void setCheckinWay(String checkinWay) {
        this.checkinWay = checkinWay;
    }

    public Long getCheckoutRecordId() {
        return this.checkoutRecordId;
    }

    public void setCheckoutRecordId(Long checkoutRecordId) {
        this.checkoutRecordId = checkoutRecordId;
    }

    public String getCheckoutRecordNo() {
        return this.checkoutRecordNo;
    }

    public void setCheckoutRecordNo(String checkoutRecordNo) {
        this.checkoutRecordNo = checkoutRecordNo;
    }

    public String getCheckoutSysName() {
        return this.checkoutSysName;
    }

    public void setCheckoutSysName(String checkoutSysName) {
        this.checkoutSysName = checkoutSysName;
    }

    public LocalDateTime getCheckinTime() {
        return this.checkinTime;
    }

    public void setCheckinTime(LocalDateTime checkinTime) {
        this.checkinTime = checkinTime;
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
