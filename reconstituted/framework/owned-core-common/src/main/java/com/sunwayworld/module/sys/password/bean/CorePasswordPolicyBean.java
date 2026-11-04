/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.password.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.NotNull;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.support.domain.AbstractPersistable;
import com.sunwayworld.framework.support.domain.Suspendable;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

@Table(value="T_CORE_PASSWORD_POLICY")
public class CorePasswordPolicyBean
extends AbstractPersistable<Long>
implements Suspendable<Long> {
    private static final long serialVersionUID = -1985232084471259933L;
    @Id
    private Long id;
    private String name;
    private String type;
    private Long minNumber;
    private Long minLowercase;
    private Long minUppercase;
    private Long minSpecialCharacters;
    private Long minLength;
    private Long maxLength;
    private Long composition;
    @NotNull(defaultValue="total")
    private String failedAttemptPolicy;
    private Long maxFailedAttempts;
    private Long expiryInterval;
    private Long expiryWarningInterval;
    @NotNull(defaultValue="optional")
    private String expiredPolicy;
    private Long expiredMaxUse;
    private Long reuseInterval;
    private Long historyLength;
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

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getMinNumber() {
        return this.minNumber;
    }

    public void setMinNumber(Long minNumber) {
        this.minNumber = minNumber;
    }

    public Long getMinLowercase() {
        return this.minLowercase;
    }

    public void setMinLowercase(Long minLowercase) {
        this.minLowercase = minLowercase;
    }

    public Long getMinUppercase() {
        return this.minUppercase;
    }

    public void setMinUppercase(Long minUppercase) {
        this.minUppercase = minUppercase;
    }

    public Long getMinSpecialCharacters() {
        return this.minSpecialCharacters;
    }

    public void setMinSpecialCharacters(Long minSpecialCharacters) {
        this.minSpecialCharacters = minSpecialCharacters;
    }

    public Long getMinLength() {
        return this.minLength;
    }

    public void setMinLength(Long minLength) {
        this.minLength = minLength;
    }

    public Long getMaxLength() {
        return this.maxLength;
    }

    public void setMaxLength(Long maxLength) {
        this.maxLength = maxLength;
    }

    public Long getComposition() {
        return this.composition;
    }

    public void setComposition(Long composition) {
        this.composition = composition;
    }

    public Long getMaxFailedAttempts() {
        return this.maxFailedAttempts;
    }

    public void setMaxFailedAttempts(Long maxFailedAttempts) {
        this.maxFailedAttempts = maxFailedAttempts;
    }

    public Long getExpiryInterval() {
        return this.expiryInterval;
    }

    public void setExpiryInterval(Long expiryInterval) {
        this.expiryInterval = expiryInterval;
    }

    public Long getExpiryWarningInterval() {
        return this.expiryWarningInterval;
    }

    public void setExpiryWarningInterval(Long expiryWarningInterval) {
        this.expiryWarningInterval = expiryWarningInterval;
    }

    public Long getExpiredMaxUse() {
        return this.expiredMaxUse;
    }

    public void setExpiredMaxUse(Long expiredMaxUse) {
        this.expiredMaxUse = expiredMaxUse;
    }

    public Long getReuseInterval() {
        return this.reuseInterval;
    }

    public void setReuseInterval(Long reuseInterval) {
        this.reuseInterval = reuseInterval;
    }

    public Long getHistoryLength() {
        return this.historyLength;
    }

    public void setHistoryLength(Long historyLength) {
        this.historyLength = historyLength;
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

    public String getFailedAttemptPolicy() {
        return this.failedAttemptPolicy;
    }

    public void setFailedAttemptPolicy(String failedAttemptPolicy) {
        this.failedAttemptPolicy = failedAttemptPolicy;
    }

    public String getExpiredPolicy() {
        return this.expiredPolicy;
    }

    public void setExpiredPolicy(String expiredPolicy) {
        this.expiredPolicy = expiredPolicy;
    }
}
