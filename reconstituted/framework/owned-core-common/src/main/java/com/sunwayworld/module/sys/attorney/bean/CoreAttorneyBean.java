/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.attorney.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.NotNull;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import com.sunwayworld.framework.support.domain.Activatable;
import com.sunwayworld.framework.support.domain.Suspendable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

@Table(value="T_CORE_ATTORNEY")
public class CoreAttorneyBean
extends AbstractInsertable<Long>
implements Activatable<Long>,
Suspendable<Long> {
    private static final long serialVersionUID = 1322986853713127142L;
    @Id
    private Long id;
    private String trustor;
    private String trustee;
    private LocalDate startDate;
    private LocalDate endDate;
    @NotNull(defaultValue="0")
    private String activatedFlag;
    private String activatedById;
    private String activatedByName;
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime activatedTime;
    @NotNull(defaultValue="0")
    private String lastSuspendedFlag;
    private String lastSuspendedById;
    private String lastSuspendedByName;
    @JSONField(format="yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastSuspendedTime;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getActivatedFlag() {
        return this.activatedFlag;
    }

    @Override
    public void setActivatedFlag(String activatedFlag) {
        this.activatedFlag = activatedFlag;
    }

    @Override
    public String getActivatedById() {
        return this.activatedById;
    }

    @Override
    public void setActivatedById(String activatedById) {
        this.activatedById = activatedById;
    }

    @Override
    public String getActivatedByName() {
        return this.activatedByName;
    }

    @Override
    public void setActivatedByName(String activatedByName) {
        this.activatedByName = activatedByName;
    }

    @Override
    public LocalDateTime getActivatedTime() {
        return this.activatedTime;
    }

    @Override
    public void setActivatedTime(LocalDateTime activatedTime) {
        this.activatedTime = activatedTime;
    }

    @Override
    public String getLastSuspendedFlag() {
        return this.lastSuspendedFlag;
    }

    @Override
    public void setLastSuspendedFlag(String lastSuspendedFlag) {
        this.lastSuspendedFlag = lastSuspendedFlag;
    }

    @Override
    public String getLastSuspendedById() {
        return this.lastSuspendedById;
    }

    @Override
    public void setLastSuspendedById(String lastSuspendedById) {
        this.lastSuspendedById = lastSuspendedById;
    }

    @Override
    public String getLastSuspendedByName() {
        return this.lastSuspendedByName;
    }

    @Override
    public void setLastSuspendedByName(String lastSuspendedByName) {
        this.lastSuspendedByName = lastSuspendedByName;
    }

    @Override
    public LocalDateTime getLastSuspendedTime() {
        return this.lastSuspendedTime;
    }

    @Override
    public void setLastSuspendedTime(LocalDateTime lastSuspendedTime) {
        this.lastSuspendedTime = lastSuspendedTime;
    }

    public static long getSerialversionuid() {
        return 1322986853713127142L;
    }

    public String getTrustor() {
        return this.trustor;
    }

    public void setTrustor(String trustor) {
        this.trustor = trustor;
    }

    public String getTrustee() {
        return this.trustee;
    }

    public void setTrustee(String trustee) {
        this.trustee = trustee;
    }

    public LocalDate getStartDate() {
        return this.startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return this.endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}
