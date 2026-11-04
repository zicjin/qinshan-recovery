/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.mdm.user.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.NotNull;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import com.sunwayworld.framework.support.domain.Insertable;
import com.sunwayworld.framework.support.domain.Suspendable;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

@Table(value="T_CORE_USER")
public class CoreUserBean
extends AbstractInsertable<String>
implements Insertable<String>,
Suspendable<String> {
    @Transient
    private static final long serialVersionUID = -7191529835633871340L;
    @Id
    private String id;
    private String userNo;
    private String userName;
    @JSONField(serialize=false)
    private String password;
    private String mobile;
    private String email;
    private String idCard;
    private String status;
    private String orgId;
    private String orgName;
    @NotNull(defaultValue="normal")
    private String userType;
    @NotNull(defaultValue="0")
    private String readonly;
    @JSONField(format="yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expiryDate;
    @NotNull(defaultValue="0")
    private Long failedLoginAttempts;
    @NotNull
    @JSONField(format="yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime passwordUpdatedTime;
    @NotNull(defaultValue="0")
    private Long expiredPasswordUses;
    @NotNull(defaultValue="0")
    private String multiLogin;
    @NotNull(defaultValue="0")
    private String lastSuspendedFlag;
    private String lastSuspendedById;
    private String lastSuspendedByName;
    @JSONField(format="yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastSuspendedTime;

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public String getUserNo() {
        return this.userNo;
    }

    public void setUserNo(String userNo) {
        this.userNo = userNo;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMobile() {
        return this.mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIdCard() {
        return this.idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getFailedLoginAttempts() {
        return this.failedLoginAttempts;
    }

    public void setFailedLoginAttempts(Long failedLoginAttempts) {
        this.failedLoginAttempts = failedLoginAttempts;
    }

    public String getOrgId() {
        return this.orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getOrgName() {
        return this.orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    @Override
    public String getLastSuspendedFlag() {
        return this.lastSuspendedFlag;
    }

    @Override
    public void setLastSuspendedFlag(String lastSuspendedFlag) {
        this.lastSuspendedFlag = lastSuspendedFlag;
    }

    public Long getExpiredPasswordUses() {
        return this.expiredPasswordUses;
    }

    public void setExpiredPasswordUses(Long expiredPasswordUses) {
        this.expiredPasswordUses = expiredPasswordUses;
    }

    public LocalDateTime getPasswordUpdatedTime() {
        return this.passwordUpdatedTime;
    }

    public void setPasswordUpdatedTime(LocalDateTime passwordUpdatedTime) {
        this.passwordUpdatedTime = passwordUpdatedTime;
    }

    public String getUserType() {
        return this.userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getReadonly() {
        return this.readonly;
    }

    public void setReadonly(String readonly) {
        this.readonly = readonly;
    }

    public String getMultiLogin() {
        return this.multiLogin;
    }

    public void setMultiLogin(String multiLogin) {
        this.multiLogin = multiLogin;
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

    public LocalDateTime getExpiryDate() {
        return this.expiryDate;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }
}
