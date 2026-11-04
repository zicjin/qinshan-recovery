/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.status.resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping(value={"/core/module/sys/statuses"})
public interface CoreStatusResource {
    @RequestMapping(value={"/speed"}, method={RequestMethod.GET})
    public String selectSpeed();

    @RequestMapping(value={"/action/keep-online"}, method={RequestMethod.POST})
    public void keepOnline();
}
