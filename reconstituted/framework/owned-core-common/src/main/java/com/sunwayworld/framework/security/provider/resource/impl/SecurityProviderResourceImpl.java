/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.security.provider.resource.impl;

import com.sunwayworld.framework.security.provider.resource.SecurityProviderResource;
import com.sunwayworld.framework.security.provider.service.SecurityProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SecurityProviderResourceImpl
implements SecurityProviderResource {
    @Autowired
    private SecurityProviderService providerService;

    @Override
    public String getPublicKey() {
        return this.providerService.getPublicKey();
    }
}
