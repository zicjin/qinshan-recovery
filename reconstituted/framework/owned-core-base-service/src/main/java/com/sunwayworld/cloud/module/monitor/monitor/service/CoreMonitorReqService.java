/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.monitor.monitor.service;

import com.sunwayworld.cloud.module.monitor.monitor.bean.CoreMonitorReqBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import java.util.List;

public interface CoreMonitorReqService
extends GenericService<CoreMonitorReqBean, Long> {
    public void insertMonitorReq(List<CoreMonitorReqBean> var1);
}
