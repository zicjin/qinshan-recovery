/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.monitor.monitor.service;

import com.sunwayworld.cloud.module.monitor.monitor.bean.CoreMonitorServerRuntimeBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import java.util.List;

public interface CoreMonitorServerRuntimeService
extends GenericService<CoreMonitorServerRuntimeBean, Long> {
    public void insertServerRuntime(List<CoreMonitorServerRuntimeBean> var1);
}
