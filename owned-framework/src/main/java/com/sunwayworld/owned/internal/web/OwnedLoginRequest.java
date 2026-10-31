package com.sunwayworld.owned.internal.web;

import java.util.Arrays;

/** Login input owned by the HTTP boundary.  Password material is single-use and wipeable. */
public final class OwnedLoginRequest {
    private final String userId;
    private final String orgId;
    private final String roleId;
    private final String tenantId;
    private final String loginType;
    private char[] password;

    public OwnedLoginRequest(String userId, char[] password) {
        this(userId, password, null, null, null, null);
    }

    public OwnedLoginRequest(String userId, char[] password, String orgId, String roleId, String tenantId, String loginType) {
        if (userId == null || userId.trim().length() == 0) throw new IllegalArgumentException("userId is required");
        if (password == null) throw new IllegalArgumentException("password is required");
        this.userId = userId;
        this.orgId = orgId;
        this.roleId = roleId;
        this.tenantId = tenantId;
        this.loginType = loginType;
        this.password = password.clone();
    }

    public String getUserId() { return userId; }
    public String getOrgId() { return orgId; }
    public String getRoleId() { return roleId; }
    public String getTenantId() { return tenantId; }
    public String getLoginType() { return loginType; }
    public boolean hasPassword() { return password != null; }

    /** Returns a defensive copy for one authentication attempt. */
    public char[] copyPassword() {
        if (password == null) throw new IllegalStateException("login request already used");
        return password.clone();
    }

    public void clear() {
        if (password != null) { Arrays.fill(password, '\0'); password = null; }
    }
}
