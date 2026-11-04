/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.monitor.monitor.service;

import com.sunwayworld.cloud.module.monitor.monitor.bean.CoreMonitorServerBean;
import com.sunwayworld.framework.support.base.service.GenericService;

public interface CoreMonitorServerService
extends GenericService<CoreMonitorServerBean, String> {
    public void insertServerAndApp();
}
