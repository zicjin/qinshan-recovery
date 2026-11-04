/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.sys.monitor.config.service;

import com.sunwayworld.cloud.module.sys.monitor.config.bean.CoreSystemMonitorConfigBean;
import com.sunwayworld.framework.support.base.service.GenericService;

public interface CoreSystemMonitorConfigService
extends GenericService<CoreSystemMonitorConfigBean, String> {
    public void scheduleAutoClean();
}
