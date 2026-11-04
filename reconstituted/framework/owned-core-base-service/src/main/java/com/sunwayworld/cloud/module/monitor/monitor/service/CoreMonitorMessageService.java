/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.monitor.monitor.service;

import com.sunwayworld.cloud.module.monitor.monitor.bean.CoreMonitorMessageBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import java.util.List;

public interface CoreMonitorMessageService
extends GenericService<CoreMonitorMessageBean, Long> {
    public List<CoreMonitorMessageBean> selectLastMessages();

    public void insertMonitorMessage(List<CoreMonitorMessageBean> var1);
}
