/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.license.resource.impl;

import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.license.resource.CoreLicenseResource;
import com.sunwayworld.module.sys.license.service.CoreLicenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
@GikamBean
public class CoreLicenseResourceImpl
implements CoreLicenseResource {
    @Autowired
    private CoreLicenseService licenseService;

    @Override
    public int getLicenseCountdownDays() {
        return this.licenseService.getLicenseCountdownDays();
    }

    @Override
    public int getNumberOfOnlineUsers() {
        return this.licenseService.getNumberOfOnlineUsers();
    }
}
