/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.security.sso;

import com.sunwayworld.framework.security.bean.LoginUser;

public interface RemoteAuthenticator {
    public LoginUser authenticate(String var1, String var2);
}
