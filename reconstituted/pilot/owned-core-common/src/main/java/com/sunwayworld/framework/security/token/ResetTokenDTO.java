/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.security.token;

import com.google.common.base.Objects;
import java.io.Serializable;

public class ResetTokenDTO
implements Serializable {
    private static final long serialVersionUID = -4006589514799067015L;
    private boolean mobile;
    private String token;
    private String tenant;

    public boolean isMobile() {
        return this.mobile;
    }

    public void setMobile(boolean mobile) {
        this.mobile = mobile;
    }

    public String getToken() {
        return this.token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTenant() {
        return this.tenant;
    }

    public void setTenant(String tenant) {
        this.tenant = tenant;
    }

    public boolean equals(Object target) {
        if (target == null || !(target instanceof ResetTokenDTO)) {
            return false;
        }
        return Objects.equal(this.isMobile(), ((ResetTokenDTO)target).isMobile()) && Objects.equal(this.getToken(), ((ResetTokenDTO)target).getToken()) && Objects.equal(this.getTenant(), ((ResetTokenDTO)target).getTenant());
    }

    public int hashCode() {
        return (this.token + "$" + this.mobile + "$" + this.tenant).hashCode();
    }
}
