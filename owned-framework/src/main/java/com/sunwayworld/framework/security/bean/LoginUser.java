package com.sunwayworld.framework.security.bean;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.userdetails.User;

/** LIMS principal data carried by the business security layer. */
public class LoginUser extends User {
    private static final long serialVersionUID = 1L;
    private String orgId;
    private String orgName;
    private Long roleId;
    private String roleName;
    private Long tenantId;
    private String tenantName;
    private String casToken;
    private LoginUser trustee;
    private boolean additionalCheck;
    private boolean badPassword;
    private String loginType;
    private Map<String, Object> ext;
    private String loginToken;

    public LoginUser(String username, String password) {
        super(username, password == null ? "" : password, java.util.Collections.<org.springframework.security.core.GrantedAuthority>emptyList());
        additionalCheck = true;
        badPassword = false;
        ext = new HashMap<String, Object>();
    }
    public String getOrgId() { return orgId; } public void setOrgId(String value) { orgId = value; }
    public String getOrgName() { return orgName; } public void setOrgName(String value) { orgName = value; }
    public Long getRoleId() { return roleId; } public void setRoleId(Long value) { roleId = value; }
    public String getRoleName() { return roleName; } public void setRoleName(String value) { roleName = value; }
    public boolean getAdditionalCheck() { return additionalCheck; } public void setAdditionalCheck(boolean value) { additionalCheck = value; }
    public String getCasToken() { return casToken; } public void setCasToken(String value) { casToken = value; }
    public LoginUser getTrustee() { return trustee; } public void setTrustee(LoginUser value) { trustee = value; }
    public boolean isBadPassword() { return badPassword; } public void setBadPassword(boolean value) { badPassword = value; }
    public String getLoginType() { return loginType; } public void setLoginType(String value) { loginType = value; }
    public Long getTenantId() { return tenantId; } public void setTenantId(Long value) { tenantId = value; }
    public String getTenantName() { return tenantName; } public void setTenantName(String value) { tenantName = value; }
    public Map<String, Object> getExt() { return ext; } public void setExt(Map<String, Object> value) { ext = value; }
    public void addExtValue(String key, Object value) { getExt().put(key, value); }
    public Object getExtValue(String key) { return getExt().get(key); }
    public String getLoginToken() { return loginToken; } public void setLoginToken(String value) { loginToken = value; }
}
