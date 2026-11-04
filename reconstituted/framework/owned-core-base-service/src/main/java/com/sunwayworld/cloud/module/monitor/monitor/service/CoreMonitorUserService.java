/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.monitor.monitor.service;

import com.sunwayworld.cloud.module.monitor.monitor.bean.CoreMonitorUserBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import java.util.List;

public interface CoreMonitorUserService
extends GenericService<CoreMonitorUserBean, Long> {
    public void insertMonitorUser(List<CoreMonitorUserBean> var1);
}
