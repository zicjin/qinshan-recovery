/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.license.resource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/core/module/sys/licenses"})
public interface CoreLicenseResource {
    @GetMapping
    public int getLicenseCountdownDays();

    @GetMapping(value={"/number-of-online-users"})
    public int getNumberOfOnlineUsers();
}
