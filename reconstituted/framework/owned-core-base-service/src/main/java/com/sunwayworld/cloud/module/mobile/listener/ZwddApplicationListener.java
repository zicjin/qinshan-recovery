/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.listener;

import com.sunwayworld.cloud.module.mobile.zwdd.sso.ZwddSsoAuthenticator;
import com.sunwayworld.framework.security.sso.SsoAuthenticatorRegistry;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class ZwddApplicationListener
implements ApplicationListener<ApplicationReadyEvent> {
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        SsoAuthenticatorRegistry.INSTANCE.register("zwdd", ZwddSsoAuthenticator.class);
    }
}
