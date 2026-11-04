/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.security.bean;

import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.utils.CollectionUtils;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.lang.Nullable;
import org.springframework.security.core.userdetails.User;

public class LoginUser
extends User {
    @Transient
    private static final long serialVersionUID = -4330491852385170523L;
    private String orgId;
    private String orgName;
    private Long roleId;
    private String roleName;
    private Long tenantId;
    private String tenantName;
    private String casToken;
    @Nullable
    private LoginUser trustee;
    private boolean additionalCheck = true;
    private boolean badPassword = false;
    @Nullable
    private String loginType;
    private Map<String, Object> ext = new HashMap<String, Object>();
    @Nullable
    private String loginToken;

    public LoginUser(String username, String password) {
        super(username, Optional.ofNullable(password).orElse(""), CollectionUtils.emptyList());
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

    public Long getRoleId() {
        return this.roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return this.roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public boolean getAdditionalCheck() {
        return this.additionalCheck;
    }

    public void setAdditionalCheck(boolean additionalCheck) {
        this.additionalCheck = additionalCheck;
    }

    public String getCasToken() {
        return this.casToken;
    }

    public void setCasToken(String casToken) {
        this.casToken = casToken;
    }

    public LoginUser getTrustee() {
        return this.trustee;
    }

    public void setTrustee(LoginUser trustee) {
        this.trustee = trustee;
    }

    public boolean isBadPassword() {
        return this.badPassword;
    }

    public void setBadPassword(boolean badPassword) {
        this.badPassword = badPassword;
    }

    public String getLoginType() {
        return this.loginType;
    }

    public void setLoginType(String loginType) {
        this.loginType = loginType;
    }

    public Long getTenantId() {
        return this.tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public String getTenantName() {
        return this.tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public Map<String, Object> getExt() {
        return this.ext;
    }

    public void setExt(Map<String, Object> ext) {
        this.ext = ext;
    }

    public void addExtValue(String key, Object value) {
        this.getExt().put(key, value);
    }

    public Object getExtValue(String key) {
        return this.getExt().get(key);
    }

    public String getLoginToken() {
        return this.loginToken;
    }

    public void setLoginToken(String loginToken) {
        this.loginToken = loginToken;
    }
}
