/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.security.provider.service.impl;

import com.sunwayworld.framework.cache.redis.RedisHelper;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.security.bean.CoreLogoutEvent;
import com.sunwayworld.framework.security.provider.service.SecurityProviderService;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.tenant.TenantManager;
import com.sunwayworld.framework.utils.EncryptUtils;
import com.sunwayworld.framework.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class SecurityProviderServiceImpl
implements SecurityProviderService {
    @Autowired
    private TenantManager tenantManager;

    @Override
    public String getPublicKey() {
        return this.tenantManager.masterCall("SECURITY_PUBLIC", () -> {
            EncryptUtils.RsaKeyPair keyPair = EncryptUtils.genRSAKeyPair();
            RedisHelper.put("SECURITY-RSA-KEY", keyPair.getPublicKey(), keyPair.getPrivateKey(), 60);
            return keyPair.getPublicKey();
        });
    }

    @Override
    public String getPrivateKey(String publicKey) {
        return this.tenantManager.masterCall("SECURITY_PUBLIC", () -> (String)RedisHelper.get("SECURITY-RSA-KEY", publicKey));
    }

    @Override
    public void preLogout(String userId) {
        if (StringUtils.isBlank(userId)) {
            return;
        }
        CoreLogoutEvent logoutEvent = new CoreLogoutEvent(userId);
        ApplicationContextHelper.getApplicationContext().publishEvent(logoutEvent);
    }
}
