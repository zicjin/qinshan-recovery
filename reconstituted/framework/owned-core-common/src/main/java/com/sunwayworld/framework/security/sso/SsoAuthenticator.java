/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.security.sso;

import com.sunwayworld.framework.security.bean.LoginUser;

public interface SsoAuthenticator {
    public LoginUser authenticate();

    default public boolean login() {
        return true;
    }

    default public void logout() {
    }

    default public void checkLoginUser(LoginUser loginUser) {
    }
}
