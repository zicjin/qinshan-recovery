/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.security.sso;

import com.sunwayworld.framework.security.bean.LoginUser;
import com.sunwayworld.framework.security.sso.SsoAuthenticator;
import com.sunwayworld.framework.security.sso.impl.TestLoginSsoAuthenticator;
import com.sunwayworld.framework.utils.ClassUtils;
import java.util.HashMap;
import java.util.Map;

public enum SsoAuthenticatorRegistry {
    INSTANCE;

    private Map<String, SsoAuthenticator> REPOSITORY = new HashMap<String, SsoAuthenticator>();

    public void register(String key, Class<? extends SsoAuthenticator> clazz) {
        this.REPOSITORY.put(key, ClassUtils.newInstance(clazz));
    }

    public SsoAuthenticator getAuthenticator(String key) {
        return this.REPOSITORY.get(key);
    }

    public void checkLoginUser(LoginUser loginUser) {
        this.REPOSITORY.forEach((k, v) -> v.checkLoginUser(loginUser));
    }

    static {
        INSTANCE.register("testLogin", TestLoginSsoAuthenticator.class);
    }
}
