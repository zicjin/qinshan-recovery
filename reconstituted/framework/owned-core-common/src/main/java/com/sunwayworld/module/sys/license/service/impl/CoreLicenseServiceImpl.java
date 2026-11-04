/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.license.service.impl;

import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.module.sys.license.service.CoreLicenseService;
import org.springframework.stereotype.Repository;

@Repository
public class CoreLicenseServiceImpl
implements CoreLicenseService {
    @Override
    public int getLicenseCountdownDays() {
        return ApplicationContextHelper.getLicenseCountdownDays();
    }

    @Override
    public int getNumberOfOnlineUsers() {
        return ApplicationContextHelper.getNumberOfOnlineUsers();
    }
}
