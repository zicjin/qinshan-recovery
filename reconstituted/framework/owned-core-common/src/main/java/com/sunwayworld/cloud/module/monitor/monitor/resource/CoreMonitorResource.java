/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.monitor.monitor.resource;

import com.sunwayworld.cloud.module.monitor.monitor.bean.CoreMonitorServerBean;
import com.sunwayworld.framework.support.base.resource.GenericCloudResource;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/core/module/monitor/servers"})
public interface CoreMonitorResource
extends GenericCloudResource<CoreMonitorServerBean, String> {
}
