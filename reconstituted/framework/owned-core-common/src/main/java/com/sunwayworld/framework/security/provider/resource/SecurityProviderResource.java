/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.security.provider.resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping(value={"/open/security"})
public interface SecurityProviderResource {
    @RequestMapping(value={"/public-key"}, method={RequestMethod.GET})
    public String getPublicKey();
}
