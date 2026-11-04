/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.monitor.monitor.service;

import com.sunwayworld.cloud.module.monitor.monitor.bean.CoreMonitorAppRuntimeBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import java.util.List;

public interface CoreMonitorAppRuntimeService
extends GenericService<CoreMonitorAppRuntimeBean, Long> {
    public void insertAppRuntime(List<CoreMonitorAppRuntimeBean> var1);
}
