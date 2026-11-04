/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.status.service.impl;

import com.sunwayworld.module.sys.status.service.CoreStatusService;
import org.springframework.stereotype.Repository;

@Repository
public class CoreStatusServiceImpl
implements CoreStatusService {
    @Override
    public String selectSpeed() {
        return "1";
    }

    @Override
    public void keepOnline() {
    }
}
